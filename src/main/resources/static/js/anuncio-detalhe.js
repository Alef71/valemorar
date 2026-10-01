document.addEventListener('DOMContentLoaded', async () => {
    const API_BASE_URL = window.API_BASE_URL || (
        ['localhost', '127.0.0.1'].includes(window.location.hostname)
            ? 'http://localhost:8080'
            : window.location.origin
    );
    const API_BASE = `${API_BASE_URL}/api`;
    const IMAGEM_PADRAO = 'https://images.unsplash.com/photo-1600456899121-68eda5705257?q=80&w=800&auto=format&fit=crop';

    // /anuncios/{id}
    const anuncioId = decodeURIComponent(window.location.pathname.split('/').filter(Boolean).pop() || '');
    const token = localStorage.getItem('token');
    const usuario = (() => {
        try { return JSON.parse(localStorage.getItem('usuario') || 'null'); } catch (e) { return null; }
    })();

    const statusElem = document.getElementById('anuncio-status');
    const conteudoElem = document.getElementById('anuncio-conteudo');
    const contatoElem = document.getElementById('contato-conteudo');

    const $ = (id) => document.getElementById(id);
    const moeda = (v) => parseFloat(v || 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
    const rotulo = (texto) => {
        const s = String(texto || '').toLowerCase().replace(/_/g, ' ');
        return s.charAt(0).toUpperCase() + s.slice(1);
    };

    function escaparHTML(str) {
        if (str === null || str === undefined) return '';
        return String(str)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
    }

    // Só aceita http(s) ou arquivos enviados ao servidor (/uploads/...) em src de imagem vinda do banco
    const urlSegura = (url) => /^(https?:\/\/|\/uploads\/)/i.test(url || '') ? url : IMAGEM_PADRAO;

    function exibirErro(mensagem) {
        statusElem.textContent = mensagem;
        statusElem.classList.remove('hidden');
        conteudoElem.classList.add('hidden');
    }

    // 1. DADOS DO ANÚNCIO (públicos). O token vai junto para o dono conseguir ver o próprio anúncio pausado.
    let anuncio;
    try {
        const res = await fetch(`${API_BASE}/anuncios/${encodeURIComponent(anuncioId)}`, {
            headers: token ? { 'Authorization': `Bearer ${token}` } : {}
        });
        if (!res.ok) return exibirErro('Anúncio não encontrado ou indisponível.');
        anuncio = await res.json();
    } catch (err) {
        return exibirErro('Não foi possível carregar o anúncio. Tente novamente.');
    }

    renderizarAnuncio(anuncio);
    statusElem.classList.add('hidden');
    conteudoElem.classList.remove('hidden');

    function renderizarAnuncio(a) {
        const tipo = rotulo(a.tipoImovel || 'Imóvel');
        const local = [a.bairro, a.cidade].filter(Boolean).join(', ') + (a.estado ? ` - ${a.estado}` : '');
        document.title = `${a.titulo || tipo} - Vale Morar`;

        $('anuncio-titulo').textContent = a.titulo || `${tipo} em ${a.cidade || 'Vale do Jequitinhonha'}`;
        $('anuncio-localizacao').textContent = local || 'Vale do Jequitinhonha';
        $('anuncio-modalidade').textContent = a.status && a.status !== 'ATIVO'
            ? `${rotulo(a.modalidade)} · ${rotulo(a.status)}`
            : rotulo(a.modalidade || 'Disponível');
        $('anuncio-valor').textContent = moeda(a.valor);

        const custos = [];
        if (parseFloat(a.valorCondominio) > 0) custos.push(`Condomínio ${moeda(a.valorCondominio)}`);
        if (parseFloat(a.valorIptu) > 0) custos.push(`IPTU ${moeda(a.valorIptu)}`);
        $('anuncio-custos').textContent = custos.join(' · ');

        const caracteristicas = [
            ['Tipo', tipo],
            ['Quartos', a.quartos ?? 0],
            ['Modalidade', rotulo(a.modalidade)],
        ];
        if (a.totalAvaliacoes > 0) caracteristicas.push(['Avaliação', `${parseFloat(a.notaMedia).toFixed(1)} (${a.totalAvaliacoes})`]);
        $('anuncio-caracteristicas').innerHTML = caracteristicas.map(([nome, valor]) => `
            <div class="bg-[#fcfbf9] border border-[#ece6da] rounded-xl px-4 py-3">
                <dt class="text-[11px] font-bold uppercase tracking-wider text-brand-textSecondary">${escaparHTML(nome)}</dt>
                <dd class="text-sm font-bold text-brand-textPrimary mt-0.5">${escaparHTML(valor)}</dd>
            </div>
        `).join('');

        if (a.tags && a.tags.length > 0) {
            $('anuncio-tags-bloco').classList.remove('hidden');
            $('anuncio-tags').innerHTML = a.tags.map(t =>
                `<span class="bg-brand-cream border border-[#dfd7c8] text-brand-brown text-xs font-bold px-2.5 py-1 rounded">${escaparHTML(t)}</span>`
            ).join('');
        }

        const datas = [];
        if (a.publicadoEm) datas.push(`Publicado em ${new Date(a.publicadoEm).toLocaleDateString('pt-BR')}`);
        if (a.atualizadoEm) datas.push(`atualizado em ${new Date(a.atualizadoEm).toLocaleDateString('pt-BR')}`);
        $('anuncio-datas').textContent = datas.join(' · ');

        renderizarFotos(a.fotos || []);
    }

    function renderizarFotos(fotos) {
        const principal = $('foto-principal');
        const capa = fotos.find(f => f.capa) || fotos[0];
        principal.src = urlSegura(capa?.url);
        principal.onerror = () => { principal.onerror = null; principal.src = IMAGEM_PADRAO; };

        if (fotos.length < 2) return;
        const miniaturas = $('miniaturas');
        miniaturas.classList.remove('hidden');
        miniaturas.innerHTML = fotos.map((f, i) => `
            <button type="button" data-index="${i}" class="miniatura shrink-0 w-20 h-14 rounded-lg overflow-hidden border-2 ${f === capa ? 'border-brand-gold' : 'border-transparent'}">
                <img src="${escaparHTML(urlSegura(f.url))}" alt="Foto ${i + 1}" class="w-full h-full object-cover">
            </button>
        `).join('');
        miniaturas.addEventListener('click', (e) => {
            const btn = e.target.closest('.miniatura');
            if (!btn) return;
            principal.src = urlSegura(fotos[btn.dataset.index].url);
            miniaturas.querySelectorAll('.miniatura').forEach(m => m.classList.replace('border-brand-gold', 'border-transparent'));
            btn.classList.replace('border-transparent', 'border-brand-gold');
        });
    }

    // 2. CONTATO DO ANUNCIANTE: só é buscado com login; sem login, nada sobre o anunciante sai do servidor
    function exibirConvitesLogin() {
        const destino = encodeURIComponent(window.location.pathname);
        contatoElem.innerHTML = `
            <p class="text-brand-textSecondary">Entre na sua conta para ver o nome, telefone e WhatsApp do anunciante.</p>
            <a href="/entrar?redirect=${destino}" class="block w-full text-center py-2.5 bg-brand-gold hover:bg-brand-earthLight text-white font-bold rounded-xl text-sm transition">Entrar para ver contato</a>
            <a href="/entrar?modo=cadastro&redirect=${destino}" class="block w-full text-center py-2.5 bg-brand-cream border border-[#dfd7c8] hover:border-brand-gold text-brand-textPrimary font-bold rounded-xl text-sm transition">Criar conta grátis</a>
        `;
    }

    if (!token) return exibirConvitesLogin();

    if (usuario?.id && String(usuario.id) === String(anuncio.anuncianteId)) {
        contatoElem.innerHTML = `
            <p class="text-brand-textSecondary">Este anúncio é seu.</p>
            <a href="/painel" class="block w-full text-center py-2.5 bg-brand-green hover:bg-brand-greenSoft text-white font-bold rounded-xl text-sm transition">Gerenciar no painel</a>
        `;
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/anuncios/${encodeURIComponent(anuncioId)}/contato`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        // Token expirado/inválido: trata como deslogado
        if (res.status === 401 || res.status === 403) return exibirConvitesLogin();
        if (!res.ok) throw new Error('Erro ao buscar contato');
        renderizarContato(await res.json());
    } catch (err) {
        contatoElem.innerHTML = '<p class="text-red-600">Não foi possível carregar o contato do anunciante.</p>';
    }

    function renderizarContato(c) {
        const digitos = (v) => String(v || '').replace(/\D/g, '');
        // Números brasileiros sem DDI recebem 55 para o link do WhatsApp
        const whatsapp = digitos(c.whatsapp);
        const whatsappLink = whatsapp ? `https://wa.me/${whatsapp.length <= 11 ? '55' + whatsapp : whatsapp}` : null;
        const mensagem = encodeURIComponent(`Olá! Vi seu anúncio no Vale Morar: ${window.location.href}`);
        const inicial = escaparHTML((c.nome || '?').charAt(0).toUpperCase());

        contatoElem.innerHTML = `
            <div class="flex items-center gap-3 pb-3 border-b border-[#ece6da]">
                ${/^(https?:\/\/|\/uploads\/)/i.test(c.fotoPerfil || '')
                    ? `<img src="${escaparHTML(c.fotoPerfil)}" alt="" class="w-11 h-11 rounded-full object-cover border border-[#dfd7c8]">`
                    : `<div class="w-11 h-11 rounded-full bg-brand-green text-white flex items-center justify-center font-bold">${inicial}</div>`}
                <p class="font-bold text-brand-textPrimary">${escaparHTML(c.nome)}</p>
            </div>
            ${c.telefone ? `<p class="flex items-center gap-2"><span class="material-symbols-outlined text-base text-brand-gold">call</span><a class="hover:text-brand-green" href="tel:${escaparHTML(digitos(c.telefone))}">${escaparHTML(c.telefone)}</a></p>` : ''}
            <p class="flex items-center gap-2 break-all"><span class="material-symbols-outlined text-base text-brand-gold">mail</span><a class="hover:text-brand-green" href="mailto:${escaparHTML(c.email)}">${escaparHTML(c.email)}</a></p>
            ${whatsappLink ? `<a href="${whatsappLink}?text=${mensagem}" target="_blank" rel="noopener noreferrer" class="flex items-center justify-center gap-2 w-full py-2.5 bg-[#25D366] hover:brightness-95 text-white font-bold rounded-xl text-sm transition"><span class="material-symbols-outlined text-base">chat</span> Conversar no WhatsApp</a>` : ''}
        `;
    }
});
