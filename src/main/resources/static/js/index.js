document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = window.API_BASE_URL || (
        ['localhost', '127.0.0.1'].includes(window.location.hostname)
            ? 'http://localhost:8080'
            : window.location.origin
    );
    const API_BASE = `${API_BASE_URL}/api`;

    const vitrineContainer = document.getElementById('vitrine-imoveis-container');
    const formBusca = document.getElementById('form-busca-vitrine');
    const filtroCidade = document.getElementById('filtro-cidade');
    const filtroTipo = document.getElementById('filtro-tipo');
    const totalElem = document.getElementById('vitrine-total');
    const subtituloElem = document.getElementById('vitrine-subtitulo');
    const ordemSelect = document.getElementById('vitrine-ordem');
    const btnCarregarMais = document.getElementById('vitrine-carregar-mais');

    const IMAGEM_PADRAO = 'https://images.unsplash.com/photo-1600456899121-68eda5705257?q=80&w=800&auto=format&fit=crop';
    const TAMANHO_PAGINA = 12;
    const DIAS_ANUNCIO_NOVO = 7;
    const MAX_PONTOS = 7;

    const TIPOS = { RESIDENCIAL: 'Residencial', COMERCIAL: 'Comercial', CHACARA: 'Sítio/Chácara' };

    const token = localStorage.getItem('token');
    const usuario = (() => {
        try { return JSON.parse(localStorage.getItem('usuario') || 'null'); } catch (e) { return null; }
    })();
    const logado = Boolean(token && usuario?.id);

    // Estado da listagem: filtros ativos, página carregada e anúncios em memória (para o carrossel)
    let filtrosAtuais = {};
    let paginaAtual = 0;
    const anunciosPorId = new Map();
    let favoritos = new Set();

    function escaparHTML(str) {
        if (str === null || str === undefined) return '';
        return String(str)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }

    const moeda = (v) => parseFloat(v || 0).toLocaleString('pt-BR', {
        style: 'currency', currency: 'BRL', maximumFractionDigits: 0
    });
    const plural = (n, singular, pluralTexto) => `${n} ${n === 1 ? singular : pluralTexto}`;
    const urlSegura = (url) => /^(https?:\/\/|\/uploads\/)/i.test(url || '') ? url : IMAGEM_PADRAO;

    function fotosDo(item) {
        const fotos = (item.fotos || []).map(f => urlSegura(f.url));
        if (fotos.length === 0) return [IMAGEM_PADRAO];
        // Capa primeiro
        const capa = (item.fotos || []).findIndex(f => f.capa);
        if (capa > 0) fotos.unshift(...fotos.splice(capa, 1));
        return fotos;
    }

    // 1. BUSCA (substitui a lista ou acrescenta a próxima página)
    async function buscarImoveis(filtros = filtrosAtuais, { acrescentar = false } = {}) {
        if (!vitrineContainer) return;
        filtrosAtuais = filtros;
        paginaAtual = acrescentar ? paginaAtual + 1 : 0;

        if (!acrescentar) {
            vitrineContainer.innerHTML = '<p class="col-span-full text-center text-sm text-brand-textSecondary py-8">Buscando imóveis...</p>';
            anunciosPorId.clear();
        }
        if (btnCarregarMais) btnCarregarMais.disabled = true;

        try {
            const query = new URLSearchParams();
            if (filtros.cidade) query.append('cidade', filtros.cidade);
            if (filtros.referencia) query.append('referencia', filtros.referencia);
            if (filtros.tipoImovel) query.append('tipoImovel', filtros.tipoImovel);
            if (filtros.precoMax) query.append('precoMax', filtros.precoMax);
            query.append('sort', ordemSelect?.value || 'publicadoEm,desc');
            query.append('page', paginaAtual);
            query.append('size', TAMANHO_PAGINA);

            const res = await fetch(`${API_BASE}/anuncios/busca?${query.toString()}`);
            if (!res.ok) throw new Error('Erro ao buscar imóveis');

            const data = await res.json();
            const imoveis = data.content || [];

            atualizarResumo(data.totalElements ?? imoveis.length, filtros);
            renderizarVitrine(imoveis, acrescentar);
            if (btnCarregarMais) btnCarregarMais.classList.toggle('hidden', data.last !== false);
        } catch (err) {
            console.error(err);
            if (!acrescentar) {
                vitrineContainer.innerHTML = '<p class="col-span-full text-center text-sm text-red-600 py-8">Não foi possível carregar a vitrine de imóveis.</p>';
            }
        } finally {
            if (btnCarregarMais) btnCarregarMais.disabled = false;
        }
    }

    function atualizarResumo(total, filtros) {
        if (totalElem) totalElem.textContent = plural(total, 'imóvel', 'imóveis').replace(/^(\d+)/, n => Number(n).toLocaleString('pt-BR'));
        if (!subtituloElem) return;
        const onde = filtros.cidade ? `em ${filtros.cidade}, MG` : 'no Vale do Jequitinhonha';
        const tipo = filtros.tipoImovel && TIPOS[filtros.tipoImovel] ? ` · ${TIPOS[filtros.tipoImovel]}` : '';
        subtituloElem.textContent = `para alugar ${onde}${tipo}`;
    }

    // 2. CARDS
    function renderizarVitrine(imoveis, acrescentar) {
        if (!acrescentar && imoveis.length === 0) {
            vitrineContainer.innerHTML = '<p class="col-span-full text-center text-sm text-brand-textSecondary py-8">Nenhum imóvel encontrado com os filtros selecionados.</p>';
            return;
        }
        imoveis.forEach(item => anunciosPorId.set(String(item.id), item));
        const html = imoveis.map(cardImovel).join('');
        if (acrescentar) {
            vitrineContainer.insertAdjacentHTML('beforeend', html);
        } else {
            vitrineContainer.innerHTML = html;
        }
    }

    function cardImovel(item) {
        const id = escaparHTML(item.id);
        const link = `/anuncios/${encodeURIComponent(item.id)}`;
        const fotos = fotosDo(item);
        const tipo = TIPOS[item.tipoImovel] || 'Imóvel';
        const quartos = item.quartos || 0;
        const local = item.bairro || item.cidade || 'Vale do Jequitinhonha';

        const novo = item.publicadoEm && (Date.now() - new Date(item.publicadoEm)) < DIAS_ANUNCIO_NOVO * 86400000;
        // economiaMercado: quanto está abaixo do preço de mercado estimado (mediana por quarto na cidade)
        const economia = parseFloat(item.economiaMercado || 0);
        const oferta = economia >= 50 ? `${moeda(economia)} abaixo do mercado` : null;
        const selos = [oferta, novo ? 'Anúncio novo' : null, tipo].filter(Boolean)
            .map(s => `<span class="bg-white/95 text-brand-textPrimary text-xs font-bold px-3 py-1.5 rounded-lg shadow-sm">${escaparHTML(s)}</span>`)
            .join('');

        const extras = parseFloat(item.valorCondominio || 0) + parseFloat(item.valorIptu || 0);
        const total = extras > 0 ? `<p class="text-sm text-brand-textSecondary">${moeda(parseFloat(item.valor || 0) + extras)} total</p>` : '';

        const descricao = `${tipo === 'Sítio/Chácara' ? 'Sítio/chácara' : tipo === 'Comercial' ? 'Imóvel comercial' : 'Imóvel residencial'}`
            + (quartos > 0 ? ` com ${plural(quartos, 'quarto', 'quartos')}` : '')
            + ` para alugar em ${local}.`;

        const caracteristicas = [quartos > 0 ? plural(quartos, 'quarto', 'quartos') : null, ...(item.tags || []).slice(0, 2)]
            .filter(Boolean).map(escaparHTML).join(' · ');

        const localizacao = [item.bairro, item.cidade].filter(Boolean).map(escaparHTML).join(' · ');
        const favorito = favoritos.has(String(item.id));

        const navegacao = fotos.length > 1 ? `
            <button type="button" data-acao="foto-anterior" aria-label="Foto anterior" class="absolute left-3 top-1/2 -translate-y-1/2 w-8 h-8 rounded-full bg-white/90 shadow flex items-center justify-center opacity-0 group-hover:opacity-100 focus:opacity-100 transition">
                <span class="material-symbols-outlined text-lg pointer-events-none">chevron_left</span>
            </button>
            <button type="button" data-acao="foto-proxima" aria-label="Próxima foto" class="absolute right-3 top-1/2 -translate-y-1/2 w-8 h-8 rounded-full bg-white/90 shadow flex items-center justify-center opacity-0 group-hover:opacity-100 focus:opacity-100 transition">
                <span class="material-symbols-outlined text-lg pointer-events-none">chevron_right</span>
            </button>
            <div class="absolute bottom-3 left-1/2 -translate-x-1/2 flex items-center gap-1.5" data-pontos>
                ${pontos(fotos.length, 0)}
            </div>` : '';

        return `
            <article class="group flex flex-col" data-anuncio-id="${id}" data-foto="0">
                <div class="relative aspect-[3/2] rounded-2xl overflow-hidden bg-gray-100">
                    <a href="${link}" class="block w-full h-full" tabindex="-1">
                        <img src="${escaparHTML(fotos[0])}" alt="${escaparHTML(tipo)} em ${escaparHTML(local)}" loading="lazy" class="w-full h-full object-cover transition duration-300 group-hover:scale-[1.02]" onerror="this.onerror=null; this.src='${IMAGEM_PADRAO}';">
                    </a>
                    <div class="absolute top-3 left-3 flex flex-wrap gap-2 pointer-events-none">${selos}</div>
                    ${navegacao}
                </div>

                <a href="${link}" class="block mt-3 space-y-1 focus:outline-none">
                    <p class="text-sm text-brand-textSecondary line-clamp-2">${escaparHTML(descricao)}</p>
                </a>
                <div class="flex items-start justify-between gap-3 mt-2">
                    <a href="${link}" class="block">
                        <p class="text-2xl font-extrabold text-brand-textPrimary leading-tight">${moeda(item.valor)} <span class="text-base font-bold">aluguel</span></p>
                        ${total}
                    </a>
                    <button type="button" data-acao="favoritar" aria-pressed="${favorito}" aria-label="${favorito ? 'Remover dos favoritos' : 'Salvar nos favoritos'}" class="shrink-0 w-10 h-10 rounded-full hover:bg-brand-cream flex items-center justify-center transition">
                        <span class="material-symbols-outlined text-[26px] pointer-events-none ${favorito ? 'text-red-600' : 'text-brand-textPrimary'}" style="font-variation-settings: 'FILL' ${favorito ? 1 : 0}">favorite</span>
                    </button>
                </div>
                <a href="${link}" class="block mt-2">
                    ${caracteristicas ? `<p class="text-sm font-bold text-brand-textPrimary">${caracteristicas}</p>` : ''}
                    <p class="text-sm text-brand-textSecondary">${localizacao}</p>
                </a>
            </article>
        `;
    }

    function pontos(total, ativo) {
        const visiveis = Math.min(total, MAX_PONTOS);
        // Mantém o ponto ativo dentro da janela visível quando há mais fotos que pontos
        const inicio = Math.min(Math.max(0, ativo - Math.floor(visiveis / 2)), total - visiveis);
        return Array.from({ length: visiveis }, (_, i) => {
            const indice = inicio + i;
            return `<span class="block rounded-full bg-white shadow ${indice === ativo ? 'w-2 h-2 opacity-100' : 'w-1.5 h-1.5 opacity-70'}"></span>`;
        }).join('');
    }

    // 3. INTERAÇÕES DOS CARDS (delegação: carrossel e favoritos)
    vitrineContainer?.addEventListener('click', async (e) => {
        const botao = e.target.closest('button[data-acao]');
        if (!botao) return;
        e.preventDefault();

        const card = botao.closest('article[data-anuncio-id]');
        const item = anunciosPorId.get(card.dataset.anuncioId);
        if (!item) return;

        if (botao.dataset.acao === 'favoritar') return alternarFavorito(botao, item);

        const fotos = fotosDo(item);
        const passo = botao.dataset.acao === 'foto-proxima' ? 1 : -1;
        const indice = (Number(card.dataset.foto) + passo + fotos.length) % fotos.length;
        card.dataset.foto = indice;
        card.querySelector('img').src = fotos[indice];
        card.querySelector('[data-pontos]').innerHTML = pontos(fotos.length, indice);
    });

    async function carregarFavoritos() {
        if (!logado) return;
        try {
            const res = await fetch(`${API_BASE}/favoritos/usuario/${encodeURIComponent(usuario.id)}?size=500`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (!res.ok) return;
            const data = await res.json();
            favoritos = new Set((data.content || []).map(f => String(f.anuncioId)));
        } catch (err) {
            // Sem favoritos, a vitrine continua funcionando
        }
    }

    function pintarFavorito(botao, ativo) {
        const icone = botao.querySelector('.material-symbols-outlined');
        botao.setAttribute('aria-pressed', ativo);
        botao.setAttribute('aria-label', ativo ? 'Remover dos favoritos' : 'Salvar nos favoritos');
        icone.style.fontVariationSettings = `'FILL' ${ativo ? 1 : 0}`;
        icone.classList.toggle('text-red-600', ativo);
        icone.classList.toggle('text-brand-textPrimary', !ativo);
    }

    async function alternarFavorito(botao, item) {
        if (!logado) {
            const voltar = encodeURIComponent(window.location.pathname + window.location.search);
            window.location.href = `/entrar?redirect=${voltar}`;
            return;
        }
        const id = String(item.id);
        const ativar = !favoritos.has(id);
        // Atualização otimista; desfaz se o servidor recusar
        pintarFavorito(botao, ativar);
        botao.disabled = true;
        try {
            const res = ativar
                ? await fetch(`${API_BASE}/favoritos`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
                    body: JSON.stringify({ anuncioId: id })
                })
                : await fetch(`${API_BASE}/favoritos/usuario/${encodeURIComponent(usuario.id)}/anuncio/${encodeURIComponent(id)}`, {
                    method: 'DELETE',
                    headers: { 'Authorization': `Bearer ${token}` }
                });
            if (!res.ok) throw new Error('Falha ao atualizar favorito');
            ativar ? favoritos.add(id) : favoritos.delete(id);
        } catch (err) {
            pintarFavorito(botao, !ativar);
        } finally {
            botao.disabled = false;
        }
    }

    // 4. FILTROS, ORDENAÇÃO E PAGINAÇÃO
    if (formBusca) {
        formBusca.addEventListener('submit', (e) => {
            e.preventDefault();
            const dados = new FormData(formBusca);
            buscarImoveis({
                tipoImovel: filtroTipo?.value,
                cidade: dados.get('cidade'),
                precoMax: dados.get('preco_max'),
                referencia: dados.get('bairro')?.trim(),
            });
            document.getElementById('vitrine')?.scrollIntoView({ behavior: 'smooth' });
        });
    }

    ordemSelect?.addEventListener('change', () => buscarImoveis(filtrosAtuais));
    btnCarregarMais?.addEventListener('click', () => buscarImoveis(filtrosAtuais, { acrescentar: true }));

    const precoRange = document.getElementById('price-range');
    const precoVal = document.getElementById('price-val');
    precoRange?.addEventListener('input', () => {
        precoVal.textContent = Number(precoRange.value).toLocaleString('pt-BR');
    });

    document.querySelectorAll('.btn-filtrar-cidade').forEach(btn => {
        btn.addEventListener('click', () => {
            const cidade = btn.getAttribute('data-cidade');
            if (filtroCidade) filtroCidade.value = cidade;
            buscarImoveis({ cidade });
            window.scrollTo({ top: 0, behavior: 'smooth' });
        });
    });

    window.preencherSelectCidades(filtroCidade);

    // Filtros vindos da URL (ex.: /imoveis?tipoImovel=RESIDENCIAL vindo do menu)
    const params = new URLSearchParams(window.location.search);
    if (params.get('cidade')) window.selecionarCidade(filtroCidade, params.get('cidade'));

    // Favoritos primeiro, para os corações já virem preenchidos
    carregarFavoritos().finally(() => buscarImoveis({
        tipoImovel: params.get('tipoImovel'),
        cidade: params.get('cidade'),
        precoMax: params.get('precoMax'),
        referencia: params.get('bairro'),
    }));
});
