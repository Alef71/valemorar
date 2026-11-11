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
    const cidadeBusca = document.getElementById('cidade-busca');
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

    function obterEnderecoAnuncio(anuncio) {
        const imovel = anuncio.imovel || anuncio;
        const enderecoAnuncio = anuncio.endereco || imovel.endereco || {};
        let enderecoSalvo = {};
        try {
            const cache = localStorage.getItem(`enderecoAnuncio_${anuncio.id}`);
            if (cache) {
                const enderecoParseado = JSON.parse(cache);
                if (!enderecoParseado || typeof enderecoParseado !== 'object') {
                    throw new Error('O endereço salvo possui um formato inválido.');
                }
                enderecoSalvo = enderecoParseado;
            }
        } catch (erro) {
            console.warn(`Não foi possível ler o endereço local do anúncio ${anuncio.id}:`, erro);
        }

        const obterCampo = campo => {
            if (Object.prototype.hasOwnProperty.call(enderecoAnuncio, campo)) {
                return enderecoAnuncio[campo];
            }
            if (Object.prototype.hasOwnProperty.call(anuncio, campo)) {
                return anuncio[campo];
            }
            return enderecoSalvo[campo];
        };

        return {
            logradouro: obterCampo('logradouro'),
            numero: obterCampo('numero'),
            complemento: obterCampo('complemento'),
            bairro: obterCampo('bairro'),
            cidade: obterCampo('cidade'),
            estado: obterCampo('estado'),
            cep: obterCampo('cep')
        };
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
        const cidadeSelecionada = normalizarTexto(cidadeBusca?.value);
        const filtrados = anuncios.filter(anuncio => {
            const imovel = anuncio.imovel || anuncio;
            const tipo = normalizarTexto(imovel.tipoImovel || anuncio.tipoImovel);
            const tipoFiltro = normalizarTexto(filtroTipoSelecionado);
            const valor = Number(anuncio.valor);
            const quartos = Number(imovel.quartos ?? anuncio.quartos ?? 0);
            const cidadeAnuncio = normalizarTexto(obterEnderecoAnuncio(anuncio).cidade);

            return (!tipoFiltro || tipo.includes(tipoFiltro))
                && (!Number.isFinite(precoMaximo) || valor <= precoMaximo)
                && (!cidadeSelecionada || cidadeAnuncio === cidadeSelecionada)
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
            const imovel = anuncio.imovel || anuncio;
            const endereco = obterEnderecoAnuncio(anuncio);
            const modalidadeOriginal = String(anuncio.modalidade || 'Disponível');
            const modalidade = escaparHTML(modalidadeOriginal);
            const tipoOriginal = String(imovel.tipoImovel || anuncio.tipoImovel || 'Imóvel')
                .replace(/_/g, ' ')
                .toLocaleLowerCase('pt-BR');
            const tipo = escaparHTML(tipoOriginal.charAt(0).toLocaleUpperCase('pt-BR') + tipoOriginal.slice(1));
            const titulo = imovel.titulo || anuncio.titulo || tipo;
            const descricao = imovel.descricao || anuncio.descricao || '';
            const preco = Number(anuncio.valor || 0).toLocaleString('pt-BR', {
                style: 'currency',
                currency: 'BRL'
            });
            const quartos = imovel.quartos ?? anuncio.quartos;
            const valorCondominioInformado = anuncio.valorCondominio;
            const valorCondominio = valorCondominioInformado == null ? null : Number(valorCondominioInformado);
            const valorIptu = Number(anuncio.valorIptu || 0);
            const logradouro = [endereco.logradouro, endereco.numero].filter(Boolean).join(', ');
            const complemento = endereco.complemento ? ` - ${endereco.complemento}` : '';
            const cidadeEstado = [endereco.cidade, endereco.estado].filter(Boolean).join(' - ');
            const localizacao = [
                logradouro ? `${logradouro}${complemento}` : '',
                endereco.bairro,
                cidadeEstado,
                endereco.cep ? `CEP ${endereco.cep}` : ''
            ].filter(Boolean).join(', ');
            const atributos = [
                quartos != null ? `<span class="flex items-center gap-1"><span class="material-symbols-outlined text-sm text-brand-green">bed</span>${Number(quartos)} quartos</span>` : ''
            ].filter(Boolean);
            const tags = Array.isArray(anuncio.tags)
                ? anuncio.tags.slice(0, 2).map(tag => `<span class="bg-brand-cream border border-[#dfd7c8] text-brand-brown text-[10px] font-bold px-2 py-0.5 rounded-md">${escaparHTML(tag)}</span>`).join('')
                : '';
            const estado = anuncio.status
                ? `<span class="absolute top-3 right-3 bg-white/95 text-brand-textPrimary text-[10px] font-bold px-2.5 py-1 rounded-md">${escaparHTML(anuncio.status)}</span>`
                : '';
            const custoAdicional = [
                valorIptu > 0 ? `IPTU: ${valorIptu.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}` : '',
                valorCondominio > 0
                    ? `Condomínio: ${valorCondominio.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}`
                    : valorCondominio === 0 ? 'Sem taxa de condomínio' : ''
            ].filter(Boolean).join(' • ');
            const imagem = imagemPadrao;
            const linkDetalhes = `detalhes-imovel.html?id=${encodeURIComponent(anuncio.id)}`;

            return `
                <article class="bg-brand-cream border border-[#ebe5db] rounded-2xl overflow-hidden shadow-sm hover:shadow-lg transition duration-300 flex flex-col group">
                    <a href="${linkDetalhes}" class="relative h-56 w-full bg-stone-100 overflow-hidden block" aria-label="Ver ${tipo}">
                        <img src="${imagem}" alt="Imagem ilustrativa de imóvel" loading="lazy" class="w-full h-full object-cover group-hover:scale-105 transition duration-500">
                        <span class="absolute top-3 left-3 bg-brand-green text-white text-[10px] font-bold px-2.5 py-1 rounded-md uppercase tracking-wider">${modalidade}</span>
                        ${estado}
                    </a>
                    <div class="p-4 flex flex-col flex-1">
                        <div class="flex items-start justify-between gap-3">
                            <p class="text-lg font-extrabold text-brand-green">${preco}${modalidadeOriginal.toLocaleUpperCase('pt-BR') === 'ALUGUEL' ? '<span class="text-xs font-medium text-brand-textSecondary"> /mês</span>' : ''}</p>
                            ${valorCondominio === 0 ? '<span class="shrink-0 bg-orange-100 text-orange-800 text-[10px] font-bold px-2 py-1 rounded-md">Sem condomínio</span>' : ''}
                        </div>
                        ${custoAdicional ? `<p class="text-[11px] text-brand-textSecondary mt-1">${escaparHTML(custoAdicional)}</p>` : ''}
                        <h3 class="font-serif text-xl leading-tight text-brand-textPrimary mt-2">${escaparHTML(titulo)}</h3>
                        ${descricao ? `<p class="text-xs text-brand-textSecondary mt-1">${escaparHTML(descricao)}</p>` : ''}
                        <p class="text-xs text-brand-textSecondary mt-2 flex items-center gap-1 min-h-5">
                            <span class="material-symbols-outlined text-sm text-brand-gold">location_on</span>
                            <span>${escaparHTML(localizacao || 'Localização não informada')}</span>
                        </p>
                        ${atributos.length ? `<div class="grid grid-cols-1 gap-x-2 gap-y-2 border-y border-[#eee8de] py-3 mt-3 text-[11px] text-brand-textSecondary">${atributos.join('')}</div>` : ''}
                        ${tags ? `<div class="flex flex-wrap gap-1 mt-3">${tags}</div>` : ''}
                        <div class="grid grid-cols-2 gap-2 mt-auto pt-4">
                            <a href="${linkDetalhes}" class="text-center py-2.5 bg-brand-cream hover:bg-[#e9e3d8] text-brand-textPrimary font-bold rounded-lg text-xs transition">Ver detalhes</a>
                            <a href="${linkDetalhes}#contato" class="text-center py-2.5 bg-brand-green hover:bg-brand-greenSoft text-white font-bold rounded-lg text-xs transition flex items-center justify-center gap-1"><span class="material-symbols-outlined text-sm">chat</span>Contatar</a>
                        </div>
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

    cidadeBusca?.addEventListener('change', aplicarFiltrosEExibir);

    formBusca?.addEventListener('submit', evento => {
        evento.preventDefault();
        aplicarFiltrosEExibir();
        document.getElementById('vitrine-imoveis')?.scrollIntoView({ behavior: 'smooth' });
    });

    carregarAnuncios();
});
