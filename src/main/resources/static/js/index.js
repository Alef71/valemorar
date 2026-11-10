document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = window.API_BASE_URL || (
        ['localhost', '127.0.0.1'].includes(window.location.hostname)
            ? 'http://localhost:8080'
            : window.location.origin
    );
    const API_BASE = `${API_BASE_URL}/api`;

    const vitrineContainer = document.getElementById('vitrine-imoveis');
    const formBusca = document.getElementById('form-busca-vitrine');
    const filtroCidade = document.getElementById('filtro-cidade');
    const filtroBairro = document.getElementById('filtro-bairro');
    const priceRange = document.getElementById('price-range');
    const priceVal = document.getElementById('price-val');
    const totalResultados = document.getElementById('total-resultados');

    const IMAGEM_PADRAO = 'https://images.unsplash.com/photo-1560518883-ce09059eeffa?q=80&w=800&auto=format&fit=crop';

    // Lista Oficial das 51 Cidades do Vale do Jequitinhonha
    const CIDADES_JEQUITINHONHA = [
        'Almenara', 'Angelândia', 'Araçuaí', 'Aricanduva', 'Berilo', 
        'Cachoeira de Pajeú', 'Capelinha', 'Caraí', 'Carbonita', 'Catuji', 
        'Chapada do Norte', 'Comercinho', 'Coronel Murta', 'Couto de Magalhães de Minas', 
        'Datas', 'Diamantina', 'Divisópolis', 'Felício dos Santos', 'Felisburgo', 
        'Francisco Badaró', 'Gouveia', 'Itamarandiba', 'Itaobim', 'Itinga', 
        'Jequitinhonha', 'Joaíma', 'Jordânia', 'José Gonçalves de Minas', 'Leme do Prado', 
        'Mata Verde', 'Medina', 'Minas Novas', 'Novo Cruzeiro', 'Padre Paraíso', 
        'Palmópolis', 'Pedra Azul', 'Ponto dos Volantes', 'Presidente Kubitschek', 
        'Rubim', 'Salto da Divisa', 'Santa Maria do Salto', 'Santo Antônio do Itambé', 
        'Santo Antônio do Jacinto', 'São Gonçalo do Rio Preto', 'Senador Modestino Gonçalves', 
        'Serro', 'Turmalina', 'Veredinha', 'Virgem da Lapa'
    ];

    // Estados dos Filtros Avançados
    let filtroTipoSelecionado = '';
    let filtroQuartosSelecionado = '';
    let todosAnunciosMemoria = [];

    // Função para sanitização de HTML contra XSS
    function escaparHTML(str) {
        if (str === null || str === undefined) return '';
        return String(str)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }

    // NORMALIZAÇÃO DE TEXTO INFALÍVEL
    function normalizarTexto(texto) {
        if (!texto) return '';
        if (typeof texto === 'object') {
            texto = texto.nome || texto.cidade || texto.descricao || '';
        }
        return String(texto)
            .toLowerCase()
            .normalize('NFD')
            .replace(/[\u0300-\u036f]/g, '')
            .trim();
    }

    // Helper ultra defensivo para extrair dados do anúncio
    function extrairDadosAnuncio(item) {
        if (!item) return {};

        const enderecoObj = item.endereco || item.imovel?.endereco || {};

        const cidadeBruta = item.cidade 
            || enderecoObj.cidade 
            || item.imovel?.cidade 
            || item.enderecoCidade 
            || '';

        const bairroBruto = item.bairro 
            || enderecoObj.bairro 
            || item.imovel?.bairro 
            || item.enderecoBairro 
            || '';

        const logradouroBruto = item.logradouro 
            || enderecoObj.logradouro 
            || item.imovel?.logradouro 
            || '';

        return {
            cidade: typeof cidadeBruta === 'object' ? (cidadeBruta.nome || cidadeBruta.cidade || '') : String(cidadeBruta),
            bairro: typeof bairroBruto === 'object' ? (bairroBruto.nome || bairroBruto.bairro || '') : String(bairroBruto),
            logradouro: typeof logradouroBruto === 'object' ? (logradouroBruto.nome || logradouroBruto.logradouro || '') : String(logradouroBruto),
            titulo: item.titulo || item.imovel?.titulo || '',
            descricao: item.descricao || item.imovel?.descricao || '',
            tipoImovel: item.tipoImovel || item.imovel?.tipoImovel || 'Imóvel',
            quartos: item.quartos ?? item.imovel?.quartos ?? 0,
            valor: parseFloat(item.valor || item.imovel?.valor || 0),
            valorCondominio: parseFloat(item.valorCondominio || item.imovel?.valorCondominio || 0),
            modalidade: item.modalidade || item.imovel?.modalidade || 'Disponível',
            tags: item.tags || item.imovel?.tags || []
        };
    }

    // 1. ATUALIZA TEXTO DO SLIDER DE PREÇO EM TEMPO REAL
    if (priceRange && priceVal) {
        priceRange.addEventListener('input', (e) => {
            const valor = parseFloat(e.target.value);
            priceVal.innerText = valor.toLocaleString('pt-BR');
            aplicarFiltrosEExibir();
        });
    }

    // 2. CONFIGURA AS ABAS DE CATEGORIA
    document.querySelectorAll('.btn-aba-tipo').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.btn-aba-tipo').forEach(b => {
                b.classList.remove('bg-brand-green', 'text-white');
                b.classList.add('bg-stone-100', 'text-stone-600');
            });
            btn.classList.remove('bg-stone-100', 'text-stone-600');
            btn.classList.add('bg-brand-green', 'text-white');

            filtroTipoSelecionado = btn.getAttribute('data-tipo') || '';
            aplicarFiltrosEExibir();
        });
    });

    // 3. CONFIGURA BOTÕES DE QUARTOS
    document.querySelectorAll('.btn-quartos').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.btn-quartos').forEach(b => {
                b.classList.remove('bg-brand-green', 'text-white');
                b.classList.add('text-stone-600');
            });
            btn.classList.remove('text-stone-600');
            btn.classList.add('bg-brand-green', 'text-white');

            filtroQuartosSelecionado = btn.getAttribute('data-quartos') || '';
            aplicarFiltrosEExibir();
        });
    });

    // 4. POPULA O SELECT DE CIDADES
    function carregarCidadesDisponiveis(anuncios) {
        if (!filtroCidade) return;

        const cidadesSet = new Set(CIDADES_JEQUITINHONHA);

        anuncios.forEach(rawItem => {
            const dados = extrairDadosAnuncio(rawItem);
            if (dados.cidade && dados.cidade.trim().length > 0) {
                cidadesSet.add(dados.cidade.trim());
            }
        });

        const urlParams = new URLSearchParams(window.location.search);
        const cidadeAtual = urlParams.get('cidade') || filtroCidade.value;

        const cidadesOrdenadas = Array.from(cidadesSet).sort((a, b) => a.localeCompare(b, 'pt-BR'));

        filtroCidade.innerHTML = '<option value="">Todas as Cidades</option>' +
            cidadesOrdenadas.map(cidade => `
                <option value="${escaparHTML(cidade)}">${escaparHTML(cidade)}</option>
            `).join('');

        if (cidadeAtual) {
            filtroCidade.value = cidadeAtual;
        }
    }

    // 5. CARREGA TODOS OS ANÚNCIOS DO BACKEND
    async function carregarAnunciosBackend() {
        if (!vitrineContainer) return;
        vitrineContainer.innerHTML = '<p class="col-span-full text-center text-sm text-brand-textSecondary py-8">Carregando imóveis...</p>';

        try {
            const res = await fetch(`${API_BASE}/anuncios`);
            if (!res.ok) throw new Error('Erro ao buscar imóveis');

            const data = await res.json();
            todosAnunciosMemoria = data.content || data || [];

            carregarCidadesDisponiveis(todosAnunciosMemoria);
            aplicarFiltrosEExibir();
        } catch (err) {
            console.error(err);
            vitrineContainer.innerHTML = '<p class="col-span-full text-center text-sm text-red-600 py-8">Não foi possível carregar os imóveis no momento.</p>';
        }
    }

    // 6. FILTRAGEM LOCAL COMPLETA
    function aplicarFiltrosEExibir() {
        if (!todosAnunciosMemoria) return;

        const cidadeFiltroNorm = normalizarTexto(filtroCidade?.value);
        const bairroFiltroNorm = normalizarTexto(filtroBairro?.value);
        const precoMaxFiltro = parseFloat(priceRange?.value || 10000);
        const minQuartos = parseInt(filtroQuartosSelecionado, 10) || 0;

        const imoveisFiltrados = todosAnunciosMemoria.filter(rawItem => {
            const item = extrairDadosAnuncio(rawItem);
            const cidadeItemNorm = normalizarTexto(item.cidade);

            if (cidadeFiltroNorm) {
                if (!cidadeItemNorm.includes(cidadeFiltroNorm) && !cidadeFiltroNorm.includes(cidadeItemNorm)) {
                    return false;
                }
            }

            if (bairroFiltroNorm) {
                const buscaTextoNorm = normalizarTexto(`${item.bairro} ${item.logradouro} ${item.titulo} ${item.descricao}`);
                if (!buscaTextoNorm.includes(bairroFiltroNorm)) {
                    return false;
                }
            }

            if (item.valor > precoMaxFiltro) return false;

            if (filtroTipoSelecionado) {
                const tipoAnuncioNorm = normalizarTexto(item.tipoImovel);
                const tipoFiltroNorm = normalizarTexto(filtroTipoSelecionado);
                if (!tipoAnuncioNorm.includes(tipoFiltroNorm)) return false;
            }

            if (minQuartos > 0 && item.quartos < minQuartos) {
                return false;
            }

            return true;
        });

        if (totalResultados) {
            totalResultados.innerText = `${imoveisFiltrados.length} imóvel(is) encontrado(s)`;
        }

        renderizarVitrine(imoveisFiltrados);
    }

    // 7. RENDERIZA OS CARDS DOS IMÓVEIS COM REDIRECIONAMENTO POR ID
    function renderizarVitrine(imoveis) {
        if (!vitrineContainer) return;

        if (imoveis.length === 0) {
            vitrineContainer.innerHTML = '<p class="col-span-full text-center text-sm text-stone-500 py-12">Nenhum imóvel encontrado com os filtros selecionados.</p>';
            return;
        }

        vitrineContainer.innerHTML = imoveis.map(rawItem => {
            const item = extrairDadosAnuncio(rawItem);
            const id = rawItem.id || rawItem.imovel?.id || '';
            const fotoCapa = (rawItem.fotos && rawItem.fotos.find(f => f.capa)?.url) || (rawItem.fotos && rawItem.fotos[0]?.url) || IMAGEM_PADRAO;
            
            const cidade = escaparHTML(item.cidade || 'Vale do Jequitinhonha');
            const tipo = escaparHTML(item.tipoImovel);
            const modalidade = escaparHTML(item.modalidade);
            const valor = item.valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });

            const tags = item.tags && item.tags.length > 0 
                ? item.tags.map(t => `<span class="bg-brand-cream border border-[#dfd7c8] text-brand-brown text-[10px] font-bold px-2 py-0.5 rounded-md">${escaparHTML(t)}</span>`).join('') 
                : '';

            return `
                <div class="bg-white border border-[#dfd7c8] rounded-2xl overflow-hidden shadow-sm hover:shadow-lg transition duration-300 flex flex-col justify-between group">
                    <div>
                        <div class="relative h-48 w-full bg-stone-100 overflow-hidden">
                            <img src="${escaparHTML(fotoCapa)}" alt="${tipo}" class="w-full h-full object-cover group-hover:scale-105 transition duration-500" onerror="this.onerror=null; this.src='${IMAGEM_PADRAO}';">
                            <span class="absolute top-3 left-3 bg-brand-green text-white text-[10px] font-bold px-2.5 py-1 rounded-md uppercase tracking-wider">${modalidade}</span>
                        </div>
                        <div class="p-5 space-y-2">
                            <h3 class="font-bold text-base text-brand-textPrimary line-clamp-1">${tipo} em ${cidade}</h3>
                            <p class="text-2xl font-extrabold text-brand-green">${valor}</p>
                            <p class="text-xs text-brand-textSecondary flex items-center gap-2">
                                <span>Quartos: <strong>${item.quartos}</strong></span>
                                ${item.valorCondominio > 0 ? `<span>• Cond: R$ ${item.valorCondominio.toFixed(0)}</span>` : ''}
                            </p>
                            <div class="flex flex-wrap gap-1 pt-2">${tags}</div>
                        </div>
                    </div>
                    <div class="p-5 pt-0">
                        <a href="detalhes-imovel.html?id=${id}" class="block w-full text-center py-2.5 bg-brand-cream border border-[#dfd7c8] hover:bg-brand-gold hover:text-white hover:border-brand-gold text-brand-textPrimary font-bold rounded-xl text-xs transition">
                            Ver Detalhes & Contato
                        </a>
                    </div>
                </div>
            `;
        }).join('');
    }

    // 8. EVENTOS DE CLIQUE NOS CARDS DE CIDADES
    function configurarCliqueCardsCidades() {
        document.querySelectorAll('.card-cidade').forEach(card => {
            card.addEventListener('click', () => {
                const cidadeNome = card.getAttribute('data-cidade');
                if (filtroCidade && cidadeNome) {
                    filtroCidade.value = cidadeNome;

                    const elementoBusca = document.getElementById('form-busca-vitrine') 
                        || document.querySelector('.mt-8.bg-white') 
                        || document.querySelector('main');

                    if (elementoBusca) {
                        elementoBusca.scrollIntoView({ behavior: 'smooth', block: 'center' });
                    }

                    aplicarFiltrosEExibir();
                }
            });
        });

        // Botão "Ver todas as cidades atendidas"
        document.getElementById('btn-ver-todas-cidades')?.addEventListener('click', () => {
            if (filtroCidade) {
                filtroCidade.value = '';
                const elementoBusca = document.getElementById('form-busca-vitrine') || document.querySelector('main');
                if (elementoBusca) {
                    elementoBusca.scrollIntoView({ behavior: 'smooth', block: 'center' });
                }
                filtroCidade.focus();
                aplicarFiltrosEExibir();
            }
        });
    }

    // 9. EVENTOS DE FORMULÁRIO DE PESQUISA
    if (formBusca) {
        formBusca.addEventListener('submit', (e) => {
            e.preventDefault();
            aplicarFiltrosEExibir();
        });
    }

    if (filtroCidade) {
        filtroCidade.addEventListener('change', aplicarFiltrosEExibir);
    }

    if (filtroBairro) {
        filtroBairro.addEventListener('input', aplicarFiltrosEExibir);
    }

    // Inicialização da Vitrine e Configurações
    carregarAnunciosBackend();
    configurarCliqueCardsCidades();
});