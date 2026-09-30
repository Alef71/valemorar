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
    const filtroModalidade = document.getElementById('filtro-modalidade');
    const filtroTipo = document.getElementById('filtro-tipo');

    const IMAGEM_PADRAO = 'https://images.unsplash.com/photo-1560518883-ce09059eeffa?q=80&w=800&auto=format&fit=crop';

    function escaparHTML(str) {
        if (str === null || str === undefined) return '';
        return String(str)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }

    async function buscarImoveis(filtros = {}) {
        if (!vitrineContainer) return;
        vitrineContainer.innerHTML = '<p class="col-span-full text-center text-sm text-brand-textSecondary py-8">Buscando imóveis...</p>';

        try {
            const query = new URLSearchParams();
            if (filtros.cidade) query.append('cidade', filtros.cidade);
            if (filtros.modalidade) query.append('modalidade', filtros.modalidade);
            if (filtros.tipoImovel) query.append('tipoImovel', filtros.tipoImovel);

            const res = await fetch(`${API_BASE}/anuncios?${query.toString()}`);
            if (!res.ok) throw new Error('Erro ao buscar imóveis');

            const data = await res.json();
            const imoveis = data.content || data || [];

            renderizarVitrine(imoveis);
        } catch (err) {
            console.error(err);
            vitrineContainer.innerHTML = '<p class="col-span-full text-center text-sm text-red-600 py-8">Não foi possível carregar a vitrine de imóveis.</p>';
        }
    }

    function renderizarVitrine(imoveis) {
        if (imoveis.length === 0) {
            vitrineContainer.innerHTML = '<p class="col-span-full text-center text-sm text-brand-textSecondary py-8">Nenhum imóvel encontrado com os filtros selecionados.</p>';
            return;
        }

        vitrineContainer.innerHTML = imoveis.map(item => {
            const fotoCapa = (item.fotos && item.fotos.find(f => f.capa)?.url) || (item.fotos && item.fotos[0]?.url) || IMAGEM_PADRAO;
            const cidade = escaparHTML(item.cidade || 'Vale do Aço');
            const tipo = escaparHTML(item.tipoImovel || 'Imóvel');
            const modalidade = escaparHTML(item.modalidade || 'Disponível');
            const valor = parseFloat(item.valor || 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });

            const tags = item.tags && item.tags.length > 0 
                ? item.tags.map(t => `<span class="bg-brand-cream border border-[#dfd7c8] text-brand-brown text-[10px] font-bold px-2 py-0.5 rounded">${escaparHTML(t)}</span>`).join('') 
                : '';

            return `
                <div class="bg-white border border-[#dfd7c8] rounded-2xl overflow-hidden shadow-sm hover:shadow-md transition flex flex-col justify-between">
                    <div>
                        <div class="relative h-48 w-full bg-gray-100">
                            <img src="${escaparHTML(fotoCapa)}" alt="${tipo}" class="w-full h-full object-cover" onerror="this.onerror=null; this.src='${IMAGEM_PADRAO}';">
                            <span class="absolute top-3 left-3 bg-brand-green text-white text-[10px] font-bold px-2.5 py-1 rounded-md uppercase tracking-wider">${modalidade}</span>
                        </div>
                        <div class="p-5 space-y-2">
                            <h3 class="font-bold text-lg text-brand-textPrimary line-clamp-1">${tipo} em ${cidade}</h3>
                            <p class="text-2xl font-extrabold text-brand-green">${valor}</p>
                            <p class="text-xs text-brand-textSecondary">Quartos: <strong>${item.quartos || 0}</strong> ${item.valorCondominio ? `| Cond: R$ ${item.valorCondominio}` : ''}</p>
                            <div class="flex flex-wrap gap-1 pt-2">${tags}</div>
                        </div>
                    </div>
                    <div class="p-5 pt-0">
                        <a href="auth.html?modo=login" class="block w-full text-center py-2.5 bg-brand-cream border border-[#dfd7c8] hover:bg-brand-gold hover:text-white hover:border-brand-gold text-brand-textPrimary font-bold rounded-xl text-xs transition">Ver Detalhes & Contato</a>
                    </div>
                </div>
            `;
        }).join('');
    }

    if (formBusca) {
        formBusca.addEventListener('submit', (e) => {
            e.preventDefault();
            buscarImoveis({
                cidade: filtroCidade?.value,
                modalidade: filtroModalidade?.value,
                tipoImovel: filtroTipo?.value,
            });
        });
    }

    document.querySelectorAll('.btn-filtrar-cidade').forEach(btn => {
        btn.addEventListener('click', () => {
            const cidade = btn.getAttribute('data-cidade');
            if (filtroCidade) filtroCidade.value = cidade;
            buscarImoveis({ cidade });
            window.scrollTo({ top: 0, behavior: 'smooth' });
        });
    });

    buscarImoveis();
});