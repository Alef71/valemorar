document.addEventListener('DOMContentLoaded', () => {
    const API_BASE = 'http://localhost:8080/api';

    // Elementos DOM
    const priceRange = document.getElementById('price-range');
    const priceVal = document.getElementById('price-val');
    const vitrineContainer = document.getElementById('vitrine-imoveis');

    // 1. ATUALIZAÇÃO DINÂMICA DO VALOR DO SLIDER DE PREÇO
    if (priceRange && priceVal) {
        priceRange.addEventListener('input', (e) => {
            const valor = parseFloat(e.target.value);
            priceVal.textContent = valor.toLocaleString('pt-BR');
        });
    }

    // 2. BUSCAR E EXIBIR IMÓVEIS NA VITRINE
    async function carregarVitrine() {
        if (!vitrineContainer) return;

        try {
            const response = await fetch(`${API_BASE}/anuncios`);

            if (!response.ok) {
                throw new Error('Erro ao buscar anúncios');
            }

            const data = await response.json();
            const anuncios = data.content || data || [];

            // Filtra apenas anúncios com status ATIVO
            const anunciosAtivos = anuncios.filter(a => !a.status || a.status.toUpperCase() === 'ATIVO');

            renderizarVitrine(anunciosAtivos);
        } catch (error) {
            console.error('Erro ao carregar vitrine:', error);
            vitrineContainer.innerHTML = `
                <div class="col-span-full text-center py-8">
                    <p class="text-stone-500 text-sm">Não foi possível carregar os imóveis no momento.</p>
                </div>
            `;
        }
    }

    // 3. RENDERIZAR OS CARDS DOS IMÓVEIS
    function renderizarVitrine(lista) {
        if (lista.length === 0) {
            vitrineContainer.innerHTML = `
                <div class="col-span-full text-center py-8">
                    <p class="text-stone-500 text-sm">Nenhum imóvel disponível no momento.</p>
                </div>
            `;
            return;
        }

        vitrineContainer.innerHTML = lista.map(anuncio => {
            const valorFormatado = parseFloat(anuncio.valor || 0).toLocaleString('pt-BR', {
                style: 'currency',
                currency: 'BRL'
            });

            const tipoFormatado = anuncio.tipoImovel ? anuncio.tipoImovel.toUpperCase() : 'RESIDENCIAL';
            const modalidadeFormatada = anuncio.modalidade || 'ALUGUEL';

            // Mapeamento de Tags
            const tagsHTML = anuncio.tags && anuncio.tags.length > 0
                ? anuncio.tags.map(t => `<span class="bg-[#f0ede6] text-[#665c52] text-[11px] font-semibold px-2 py-0.5 rounded-md">${t}</span>`).join(' ')
                : '';

            return `
                <article class="bg-white border border-[#ece7dc] rounded-2xl overflow-hidden shadow-xs hover:shadow-md transition flex flex-col justify-between">
                    <div>
                        <!-- IMAGEM DO IMÓVEL -->
                        <div class="relative h-48 bg-stone-200 overflow-hidden">
                            <img src="${anuncio.imagemUrl || 'https://via.placeholder.com/400x250?text=Vale+Morar'}" 
                                 alt="${tipoFormatado} em ${anuncio.cidade}" 
                                 class="w-full h-full object-cover">
                            <div class="absolute top-3 left-3 flex gap-1.5 flex-wrap">
                                <span class="bg-brand-green text-white text-[10px] font-extrabold uppercase px-2.5 py-1 rounded-full shadow-xs">
                                    ${modalidadeFormatada}
                                </span>
                                <span class="bg-white/90 text-brand-textPrimary text-[10px] font-bold uppercase px-2.5 py-1 rounded-full shadow-xs">
                                    ${tipoFormatado}
                                </span>
                            </div>
                        </div>

                        <!-- CONTEÚDO DO CARD -->
                        <div class="p-5 space-y-3">
                            <div class="flex justify-between items-start gap-2">
                                <div>
                                    <h3 class="text-lg font-bold text-brand-textPrimary capitalize leading-snug">
                                        ${tipoFormatado} - ${anuncio.cidade}
                                    </h3>
                                    <p class="text-xs text-brand-textSecondary mt-0.5 flex items-center gap-1">
                                        <span class="material-symbols-outlined text-sm text-brand-green">location_on</span>
                                        ${anuncio.cidade}
                                    </p>
                                </div>
                            </div>

                            <!-- PREÇO E DETALHES -->
                            <div class="pt-2 border-t border-[#f0ede6] flex justify-between items-baseline">
                                <div>
                                    <span class="text-xl font-extrabold text-brand-green">${valorFormatado}</span>
                                    ${modalidadeFormatada === 'ALUGUEL' ? '<span class="text-xs text-brand-textSecondary">/mês</span>' : ''}
                                </div>
                                ${anuncio.quartos !== undefined && anuncio.quartos !== null ? `
                                    <span class="text-xs font-semibold text-brand-textSecondary flex items-center gap-1">
                                        <span class="material-symbols-outlined text-sm">bed</span>
                                        ${anuncio.quartos}${anuncio.quartos === 1 ? 'Quarto' : 'Quartos'}
                                    </span>
                                ` : ''}
                            </div>

                            <!-- TAGS -->
                            ${tagsHTML ? `<div class="flex flex-wrap gap-1.5 pt-1">${tagsHTML}</div>` : ''}
                        </div>
                    </div>

                    <!-- AÇÃO / BOTÃO -->
                    <div class="p-5 pt-0">
                        <a href="detalhes.html?id=${anuncio.id}" 
                           class="w-full bg-brand-surfaceLow hover:bg-[#e4dec2] text-brand-textPrimary py-2 px-4 rounded-lg text-xs font-bold transition block text-center">
                           Ver detalhes
                        </a>
                    </div>
                </article>
            `;
        }).join('');
    }

    // Inicializar busca da vitrine
    carregarVitrine();
});