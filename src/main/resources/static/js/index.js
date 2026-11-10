document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = window.API_BASE_URL || (
        ['localhost', '127.0.0.1'].includes(window.location.hostname)
            ? 'http://localhost:8080'
            : window.location.origin
    );
    const API_BASE = `${API_BASE_URL}/api`;
    const vitrineContainer = document.getElementById('lista-anuncios');
    const formBusca = document.getElementById('form-busca-vitrine');
    const priceRange = document.getElementById('price-range');
    const priceVal = document.getElementById('price-val');
    const totalResultados = document.getElementById('total-resultados');
    const imagemPadrao = 'https://images.unsplash.com/photo-1560518883-ce09059eeffa?q=80&w=800&auto=format&fit=crop';

    let filtroTipoSelecionado = '';
    let filtroQuartosSelecionado = 0;
    let anuncios = [];

    function escaparHTML(valor) {
        return String(valor ?? '').replace(/[&<>'"]/g, caractere => ({
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            "'": '&#39;',
            '"': '&quot;'
        })[caractere]);
    }

    function normalizarTexto(valor) {
        return String(valor ?? '')
            .toLocaleLowerCase('pt-BR')
            .normalize('NFD')
            .replace(/[\u0300-\u036f]/g, '');
    }

    async function carregarAnuncios() {
        if (!vitrineContainer) return;

        vitrineContainer.innerHTML = '<p class="col-span-full text-center text-sm text-brand-textSecondary py-8">Carregando imóveis...</p>';

        try {
            const todos = [];
            let pagina = 0;
            let totalPaginas = 1;

            while (pagina < totalPaginas) {
                const parametros = new URLSearchParams({ page: String(pagina), size: '100' });
                const resposta = await fetch(`${API_BASE}/anuncios?${parametros}`);
                if (!resposta.ok) {
                    throw new Error(`Não foi possível carregar os anúncios (HTTP ${resposta.status}).`);
                }

                const resultado = await resposta.json();
                if (Array.isArray(resultado)) {
                    todos.push(...resultado);
                    break;
                }

                if (!Array.isArray(resultado.content)) {
                    throw new Error('A API retornou um formato inesperado para a lista de anúncios.');
                }

                todos.push(...resultado.content);
                totalPaginas = Number.isInteger(resultado.totalPages) ? resultado.totalPages : pagina + 1;
                pagina += 1;
            }

            anuncios = todos;
            aplicarFiltrosEExibir();
        } catch (erro) {
            console.error('Erro ao carregar anúncios:', erro);
            vitrineContainer.innerHTML = '<p class="col-span-full text-center text-sm text-red-600 py-8">Não foi possível carregar os imóveis no momento.</p>';
            if (totalResultados) totalResultados.innerText = 'Falha ao carregar';
        }
    }

    function aplicarFiltrosEExibir() {
        if (!vitrineContainer) return;

        const precoMaximo = Number(priceRange?.value || Number.POSITIVE_INFINITY);
        const filtrados = anuncios.filter(anuncio => {
            const tipo = normalizarTexto(anuncio.tipoImovel);
            const tipoFiltro = normalizarTexto(filtroTipoSelecionado);
            const valor = Number(anuncio.valor);
            const quartos = Number(anuncio.quartos || 0);

            return (!tipoFiltro || tipo.includes(tipoFiltro))
                && (!Number.isFinite(precoMaximo) || valor <= precoMaximo)
                && quartos >= filtroQuartosSelecionado;
        });

        if (totalResultados) {
            totalResultados.innerText = `${filtrados.length} anúncio(s) encontrado(s)`;
        }

        renderizarAnuncios(filtrados);
    }

    function renderizarAnuncios(lista) {
        if (!vitrineContainer) return;

        if (lista.length === 0) {
            vitrineContainer.innerHTML = '<p class="col-span-full text-center text-sm text-stone-500 py-12">Nenhum anúncio encontrado com os filtros selecionados.</p>';
            return;
        }

        vitrineContainer.innerHTML = lista.map(anuncio => {
            const tipo = escaparHTML(anuncio.tipoImovel || 'Imóvel');
            const modalidade = escaparHTML(anuncio.modalidade || 'Disponível');
            const valor = Number(anuncio.valor || 0).toLocaleString('pt-BR', {
                style: 'currency',
                currency: 'BRL'
            });
            const tags = Array.isArray(anuncio.tags)
                ? anuncio.tags.map(tag => `<span class="bg-brand-cream border border-[#dfd7c8] text-brand-brown text-[10px] font-bold px-2 py-0.5 rounded-md">${escaparHTML(tag)}</span>`).join('')
                : '';

            return `
                <article class="bg-white border border-[#dfd7c8] rounded-2xl overflow-hidden shadow-sm hover:shadow-lg transition duration-300 flex flex-col justify-between group">
                    <div>
                        <div class="relative h-48 w-full bg-stone-100 overflow-hidden">
                            <img src="${imagemPadrao}" alt="Imagem ilustrativa de imóvel" class="w-full h-full object-cover group-hover:scale-105 transition duration-500">
                            <span class="absolute top-3 left-3 bg-brand-green text-white text-[10px] font-bold px-2.5 py-1 rounded-md uppercase tracking-wider">${modalidade}</span>
                        </div>
                        <div class="p-5 space-y-2">
                            <h3 class="font-bold text-base text-brand-textPrimary line-clamp-1">${tipo}</h3>
                            <p class="text-2xl font-extrabold text-brand-green">${valor}</p>
                            <p class="text-xs text-brand-textSecondary flex items-center gap-2">
                                <span>Quartos: <strong>${Number(anuncio.quartos || 0)}</strong></span>
                                ${Number(anuncio.valorCondominio || 0) > 0
                                    ? `<span>• Condomínio: ${Number(anuncio.valorCondominio).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}</span>`
                                    : ''}
                            </p>
                            <div class="flex flex-wrap gap-1 pt-2">${tags}</div>
                        </div>
                    </div>
                    <div class="p-5 pt-0">
                        <a href="detalhes-imovel.html?id=${encodeURIComponent(anuncio.id)}" class="block w-full text-center py-2.5 bg-brand-cream border border-[#dfd7c8] hover:bg-brand-gold hover:text-white hover:border-brand-gold text-brand-textPrimary font-bold rounded-xl text-xs transition">
                            Ver detalhes e contato
                        </a>
                    </div>
                </article>
            `;
        }).join('');
    }

    document.querySelectorAll('.btn-aba-tipo').forEach(botao => {
        botao.addEventListener('click', () => {
            document.querySelectorAll('.btn-aba-tipo').forEach(item => {
                item.classList.remove('bg-brand-green', 'text-white');
                item.classList.add('bg-stone-100', 'text-stone-600');
            });
            botao.classList.remove('bg-stone-100', 'text-stone-600');
            botao.classList.add('bg-brand-green', 'text-white');
            filtroTipoSelecionado = botao.dataset.tipo || '';
            aplicarFiltrosEExibir();
        });
    });

    document.querySelectorAll('.btn-quartos').forEach(botao => {
        botao.addEventListener('click', () => {
            document.querySelectorAll('.btn-quartos').forEach(item => {
                item.classList.remove('bg-brand-green', 'text-white');
                item.classList.add('text-stone-600');
            });
            botao.classList.remove('text-stone-600');
            botao.classList.add('bg-brand-green', 'text-white');
            filtroQuartosSelecionado = Number(botao.dataset.quartos || 0);
            aplicarFiltrosEExibir();
        });
    });

    if (priceRange && priceVal) {
        priceRange.addEventListener('input', () => {
            priceVal.innerText = Number(priceRange.value).toLocaleString('pt-BR');
            aplicarFiltrosEExibir();
        });
    }

    formBusca?.addEventListener('submit', evento => {
        evento.preventDefault();
        aplicarFiltrosEExibir();
        document.getElementById('vitrine-imoveis')?.scrollIntoView({ behavior: 'smooth' });
    });

    carregarAnuncios();
});
