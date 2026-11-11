document.addEventListener('DOMContentLoaded', async () => {
    const API_BASE_URL = window.API_BASE_URL || (
        ['localhost', '127.0.0.1'].includes(window.location.hostname)
            ? 'http://localhost:8080'
            : window.location.origin
    );
    const API_BASE = `${API_BASE_URL}/api`;
    const imagemPadrao = 'https://images.unsplash.com/photo-1560518883-ce09059eeffa?q=80&w=800&auto=format&fit=crop';
    const idAnuncio = new URLSearchParams(window.location.search).get('id');
    const loadingState = document.getElementById('loading-state');
    const conteudoImovel = document.getElementById('imovel-conteudo');
    const token = localStorage.getItem('token');

    function escaparHTML(valor) {
        return String(valor ?? '').replace(/[&<>'"]/g, caractere => ({
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            "'": '&#39;',
            '"': '&quot;'
        })[caractere]);
    }

    async function requisitarJson(caminho, autenticado = false) {
        const headers = { Accept: 'application/json' };
        if (autenticado && token) headers.Authorization = `Bearer ${token}`;

        const response = await fetch(`${API_BASE}${caminho}`, { headers });
        if (!response.ok) {
            const error = new Error(`Falha ao carregar ${caminho} (HTTP ${response.status}).`);
            error.status = response.status;
            throw error;
        }
        return response.json();
    }

    function mostrarFalha(mensagem) {
        if (loadingState) {
            loadingState.innerHTML = `<p class="text-red-600 font-bold py-12">${escaparHTML(mensagem)}</p>`;
        }
    }

    if (!idAnuncio) {
        mostrarFalha('ID do anúncio não informado na URL.');
        return;
    }

    let anuncio;
    try {
        anuncio = await requisitarJson(`/anuncios/${encodeURIComponent(idAnuncio)}`);
    } catch (error) {
        console.error('Erro ao carregar anúncio:', error);
        mostrarFalha('Não foi possível carregar as informações deste anúncio.');
        return;
    }

    let imovel = null;
    let endereco = null;
    let fotos = [];
    let locador = null;
    let erroDetalhes = null;

    if (token && anuncio.imovelId) {
        try {
            imovel = await requisitarJson(`/imoveis/${encodeURIComponent(anuncio.imovelId)}`, true);
            const consultas = await Promise.allSettled([
                imovel.enderecoId
                    ? requisitarJson(`/enderecos/${encodeURIComponent(imovel.enderecoId)}`, true)
                    : Promise.reject(new Error('O imóvel não possui endereço associado.')),
                requisitarJson(`/fotos-imovel/imovel/${encodeURIComponent(imovel.id)}`, true),
                requisitarJson(`/locadores/${encodeURIComponent(imovel.locadorId || anuncio.anuncianteId)}`, true)
            ]);

            if (consultas[0].status === 'fulfilled') {
                endereco = consultas[0].value;
            } else {
                console.error('Não foi possível carregar o endereço:', consultas[0].reason);
            }

            if (consultas[1].status === 'fulfilled' && Array.isArray(consultas[1].value)) {
                fotos = consultas[1].value;
            } else if (consultas[1].status === 'rejected') {
                console.error('Não foi possível carregar as fotos:', consultas[1].reason);
            }

            if (consultas[2].status === 'fulfilled') {
                locador = consultas[2].value;
            } else {
                erroDetalhes = consultas[2].reason;
                if (erroDetalhes.status === 400) {
                    console.info('O anunciante ainda não possui um perfil de locador cadastrado.');
                } else {
                    console.error('Não foi possível carregar os dados de contato:', consultas[2].reason);
                }
            }
        } catch (error) {
            erroDetalhes = error;
            console.error('Não foi possível carregar os dados completos do imóvel:', error);
        }
    }

    renderizarDetalhes(anuncio, imovel, endereco, fotos, locador, erroDetalhes);

    function renderizarDetalhes(dadosAnuncio, dadosImovel, dadosEndereco, fotosImovel, dadosLocador, falhaDetalhes) {
        const imovel = dadosAnuncio.imovel || dadosAnuncio;
        const enderecoAnuncio = dadosAnuncio.endereco || imovel.endereco || dadosAnuncio;
        const tipo = dadosImovel?.tipoImovel || imovel.tipoImovel || 'Imóvel';
        const modalidade = dadosAnuncio.modalidade || 'Disponível';
        const modalidadeNormalizada = modalidade.toLocaleUpperCase('pt-BR');
        const titulo = dadosImovel?.titulo || imovel.titulo || `${tipo} para ${modalidade.toLocaleLowerCase('pt-BR')}`;
        const descricao = dadosImovel?.descricao || imovel.descricao || 'O anunciante não informou uma descrição.';
        const preco = Number(dadosAnuncio.valor || 0);

        document.getElementById('imovel-titulo').innerText = titulo;
        document.getElementById('badge-modalidade').innerText = modalidade;
        document.getElementById('badge-tipo').innerText = tipo;
        document.getElementById('imovel-descricao').innerText = descricao;
        document.getElementById('imovel-preco').innerText = preco.toLocaleString('pt-BR', {
            style: 'currency',
            currency: 'BRL'
        });
        document.getElementById('imovel-tipo-preco').innerText =
            modalidadeNormalizada === 'VENDA' ? 'Valor de venda' : 'Valor anunciado';

        const enderecoElem = document.getElementById('imovel-endereco');
        const enderecoCompleto = dadosEndereco || enderecoAnuncio;
        const logradouro = [
            enderecoCompleto.logradouro,
            enderecoCompleto.numero
        ].filter(Boolean).join(', ');
        const partesEndereco = [
            logradouro
                ? `${logradouro}${enderecoCompleto.complemento ? ` - ${enderecoCompleto.complemento}` : ''}`
                : '',
            enderecoCompleto.bairro,
            [enderecoCompleto.cidade, enderecoCompleto.estado].filter(Boolean).join(' - '),
            enderecoCompleto.cep ? `CEP ${enderecoCompleto.cep}` : ''
        ].filter(Boolean);
        enderecoElem.innerText = partesEndereco.join(', ') || 'Localização não informada.';

        document.getElementById('spec-quartos').innerText = dadosImovel?.quartos ?? imovel.quartos ?? '-';
        document.getElementById('spec-banheiros').innerText = dadosImovel?.banheiros ?? '-';
        document.getElementById('spec-vagas').innerText = dadosImovel?.vagasGaragem ?? '-';
        document.getElementById('spec-area').innerText = dadosImovel?.areaTotal ? `${dadosImovel.areaTotal} m²` : '-';

        if (Number(dadosAnuncio.valorCondominio || 0) > 0) {
            document.getElementById('imovel-condominio').classList.remove('hidden');
            document.getElementById('val-condominio').innerText = Number(dadosAnuncio.valorCondominio).toLocaleString('pt-BR');
        }
        if (Number(dadosAnuncio.valorIptu || 0) > 0) {
            document.getElementById('imovel-iptu').classList.remove('hidden');
            document.getElementById('val-iptu').innerText = Number(dadosAnuncio.valorIptu).toLocaleString('pt-BR');
        }

        const tags = Array.isArray(dadosAnuncio.tags) ? dadosAnuncio.tags : [];
        document.getElementById('imovel-tags').innerHTML = tags.length
            ? tags.map(tag => `<span class="bg-brand-cream border border-[#dfd7c8] text-brand-brown font-bold text-[11px] px-2.5 py-1 rounded-md">${escaparHTML(tag)}</span>`).join('')
            : '<span class="text-xs text-stone-400">Nenhum diferencial cadastrado.</span>';

        const listaFotos = Array.isArray(fotosImovel)
            ? [...fotosImovel].sort((a, b) => Number(a.ordem || 0) - Number(b.ordem || 0))
            : [];
        const fotoPrincipal = document.getElementById('foto-principal');
        const fotoDestaque = listaFotos.find(foto => foto.capa && foto.url) || listaFotos[0];
        fotoPrincipal.src = fotoDestaque?.url || imagemPadrao;
        fotoPrincipal.alt = listaFotos.length ? 'Foto do imóvel' : 'Imagem ilustrativa de imóvel';

        const galeria = document.getElementById('galeria-thumbnails');
        const miniaturas = listaFotos.filter(foto => foto.url);
        const indiceDestaque = miniaturas.indexOf(fotoDestaque);
        galeria.classList.toggle('hidden', miniaturas.length < 2);
        galeria.innerHTML = miniaturas.map((foto, indice) => `
            <button type="button" class="thumb-foto h-16 w-20 shrink-0 overflow-hidden rounded-lg border-2 ${indice === indiceDestaque ? 'border-brand-green' : 'border-transparent'}" data-foto-indice="${indice}" aria-label="Exibir foto ${indice + 1}">
                <img src="${escaparHTML(foto.url)}" alt="Miniatura do imóvel" class="h-full w-full object-cover">
            </button>
        `).join('');
        galeria.addEventListener('click', evento => {
            const botao = evento.target.closest('[data-foto-indice]');
            if (!botao) return;
            const foto = miniaturas[Number(botao.dataset.fotoIndice)];
            if (!foto) return;
            fotoPrincipal.src = foto.url;
            galeria.querySelectorAll('.thumb-foto').forEach(item => {
                item.classList.toggle('border-brand-green', item === botao);
                item.classList.toggle('border-transparent', item !== botao);
            });
        });

        configurarContato(dadosLocador, titulo, falhaDetalhes);

        if (loadingState) loadingState.classList.add('hidden');
        if (conteudoImovel) conteudoImovel.classList.remove('hidden');
        if (window.location.hash === '#contato') {
            document.getElementById('contato')?.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
    }

    function configurarContato(dadosLocador, titulo, falhaDetalhes) {
        const nomeElem = document.getElementById('proprietario-nome');
        const inicialElem = document.getElementById('proprietario-inicial');
        const linkWhatsapp = document.getElementById('btn-whatsapp');
        const formulario = document.getElementById('form-contato-direto');
        const aviso = document.getElementById('contato-indisponivel');
        const mensagemAviso = document.getElementById('contato-mensagem');
        const linkLogin = document.getElementById('link-login-contato');

        nomeElem.innerText = 'Anunciante';
        inicialElem.innerText = 'A';

        const telefone = dadosLocador?.whatsapp || dadosLocador?.telefone;
        if (!token) {
            mensagemAviso.innerText = 'Entre na sua conta para consultar os dados de contato cadastrados pelo anunciante.';
            aviso.classList.remove('hidden');
            linkLogin.classList.remove('hidden');
            return;
        }

        if (!telefone) {
            mensagemAviso.innerText = falhaDetalhes?.status === 400
                ? 'O perfil de contato deste anunciante ainda não está cadastrado.'
                : falhaDetalhes
                    ? 'Não foi possível carregar os dados de contato. Verifique sua sessão e tente novamente.'
                    : 'O anunciante ainda não cadastrou telefone ou WhatsApp.';
            aviso.classList.remove('hidden');
            return;
        }

        const numero = String(telefone).replace(/\D/g, '');
        const numeroInternacional = numero.startsWith('55') ? numero : `55${numero}`;
        const urlWhatsapp = `https://wa.me/${numeroInternacional}?text=${encodeURIComponent(`Olá, tenho interesse no imóvel: ${titulo}`)}`;
        linkWhatsapp.href = urlWhatsapp;
        linkWhatsapp.classList.remove('hidden');
        linkWhatsapp.classList.add('flex');
        formulario.classList.remove('hidden');
        formulario.addEventListener('submit', evento => {
            evento.preventDefault();
            const nome = document.getElementById('msg-nome').value.trim();
            const texto = document.getElementById('msg-texto').value.trim();
            const mensagem = `Olá, meu nome é ${nome}. Tenho interesse no imóvel "${titulo}". ${texto}`;
            window.open(`https://wa.me/${numeroInternacional}?text=${encodeURIComponent(mensagem)}`, '_blank', 'noopener');
        });
    }
});
