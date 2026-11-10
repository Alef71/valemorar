document.addEventListener('DOMContentLoaded', async () => {
    // 1. Base URL Dinâmica (suporta backend em http://localhost:8080)
    const API_BASE_URL = window.API_BASE_URL || (
        ['localhost', '127.0.0.1'].includes(window.location.hostname)
            ? 'http://localhost:8080'
            : window.location.origin
    );
    const API_BASE = `${API_BASE_URL}/api`;
    const IMAGEM_PADRAO = 'https://images.unsplash.com/photo-1560518883-ce09059eeffa?q=80&w=800&auto=format&fit=crop';

    // Captura o ID da URL (?id=...)
    const urlParams = new URLSearchParams(window.location.search);
    const imovelId = urlParams.get('id');

    const loadingState = document.getElementById('loading-state');
    const imovelConteudo = document.getElementById('imovel-conteudo');

    if (!imovelId) {
        if (loadingState) {
            loadingState.innerHTML = '<p class="text-red-600 font-bold py-12">ID do imóvel não informado na URL.</p>';
        }
        return;
    }

    function escaparHTML(str) {
        if (str === null || str === undefined) return '';
        return String(str)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }

    function extrairTexto(campo) {
        if (!campo) return '';
        if (typeof campo === 'object') {
            return campo.nome || campo.cidade || campo.bairro || campo.logradouro || campo.descricao || '';
        }
        return String(campo).trim();
    }

    // Busca pública dos dados do imóvel no backend
    try {
        const token = localStorage.getItem('token');
        const headers = { 'Accept': 'application/json' };
        if (token) headers['Authorization'] = `Bearer ${token}`;

        const res = await fetch(`${API_BASE}/anuncios/${imovelId}`, { headers });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);

        const item = await res.json();
        console.log('Dados recebidos do imóvel:', item);
        renderizarDetalhes(item);
    } catch (err) {
        console.error('Erro ao buscar imóvel:', err);
        if (loadingState) {
            loadingState.innerHTML = `
                <div class="py-12 space-y-2">
                    <p class="text-red-600 font-bold text-base">Não foi possível carregar as informações deste imóvel.</p>
                    <p class="text-xs text-stone-500">Verifique se o backend Spring Boot está ativo em http://localhost:8080.</p>
                </div>
            `;
        }
    }

    function renderizarDetalhes(item) {
        if (!item) return;

        // Suporte flexível às entidades do Spring Boot (DTO/Anuncio/Imovel)
        const imovel = item.imovel || item;
        const endereco = item.endereco || imovel.endereco || item.imovelEndereco || {};
        const usuario = item.usuario || item.anunciante || item.proprietario || imovel.usuario || {};

        // Extração dos campos de endereço
        const cidade = extrairTexto(endereco.cidade || item.cidade || imovel.cidade);
        const bairro = extrairTexto(endereco.bairro || item.bairro || imovel.bairro);
        const logradouro = extrairTexto(endereco.logradouro || item.logradouro || imovel.logradouro);
        const numero = extrairTexto(endereco.numero || item.numero || imovel.numero);
        const estado = extrairTexto(endereco.estado || item.estado || imovel.estado || 'MG');

        // Formatação do Endereço
        const partesEndereco = [];
        if (logradouro) partesEndereco.push(numero ? `${logradouro}, Nº ${numero}` : logradouro);
        if (bairro) partesEndereco.push(`Bairro ${bairro}`);
        if (cidade) partesEndereco.push(`${cidade} - ${estado}`);

        const enderecoTexto = partesEndereco.length > 0 ? partesEndereco.join(' • ') : 'Localização não informada';

        // Detalhes principais
        const tipo = extrairTexto(imovel.tipoImovel || item.tipoImovel || 'Imóvel');
        const modalidade = extrairTexto(item.modalidade || imovel.modalidade || 'Aluguel');
        const titulo = extrairTexto(item.titulo || imovel.titulo) || `${tipo} em ${cidade || 'Vale do Jequitinhonha'}`;
        const descricao = extrairTexto(item.descricao || imovel.descricao) || 'Nenhuma descrição detalhada fornecida pelo anunciante.';

        const valor = parseFloat(item.valor || imovel.valor || 0);
        const valorCondominio = parseFloat(item.valorCondominio || imovel.valorCondominio || 0);

        const quartos = imovel.quartos ?? item.quartos ?? 0;
        const banheiros = imovel.banheiros ?? item.banheiros ?? 0;
        const vagas = imovel.vagas ?? item.vagas ?? 0;
        const area = imovel.area ?? item.area ?? '-';

        const tags = item.tags || imovel.tags || [];

        // Dados de contato do anunciante
        const nomeContato = extrairTexto(usuario.nome || item.nomeContato || item.nomeProprietario) || 'Proprietário';
        const emailContato = extrairTexto(usuario.email || item.emailContato || item.email) || 'contato@valemorar.com.br';
        const telefoneContato = extrairTexto(usuario.telefone || usuario.celular || item.telefoneContato || item.telefone);

        // Preenchimento no DOM
        const elemTitulo = document.getElementById('imovel-titulo');
        const elemEndereco = document.getElementById('imovel-endereco');
        const elemModalidade = document.getElementById('badge-modalidade');
        const elemTipo = document.getElementById('badge-tipo');

        if (elemTitulo) elemTitulo.innerText = titulo;
        if (elemEndereco) {
            elemEndereco.innerHTML = `
                <span class="material-symbols-outlined text-sm text-brand-green">location_on</span>
                <span>${escaparHTML(enderecoTexto)}</span>
            `;
        }
        if (elemModalidade) elemModalidade.innerText = modalidade;
        if (elemTipo) elemTipo.innerText = tipo;

        // Galeria de Fotos
        const fotos = item.fotos || imovel.fotos || [];
        const listaFotos = fotos.length > 0
            ? fotos.map(f => typeof f === 'string' ? f : (f.url || f.link || f.caminho))
            : [IMAGEM_PADRAO];

        const fotoPrincipal = document.getElementById('foto-principal');
        if (fotoPrincipal) fotoPrincipal.src = listaFotos[0];

        const galeriaContainer = document.getElementById('galeria-thumbnails');
        if (galeriaContainer) {
            if (listaFotos.length > 1) {
                galeriaContainer.style.display = 'flex';
                galeriaContainer.innerHTML = listaFotos.map((url, idx) => `
                    <img src="${escaparHTML(url)}" alt="Foto ${idx + 1}" class="thumb-foto h-16 w-20 object-cover rounded-lg cursor-pointer border-2 ${idx === 0 ? 'border-brand-green' : 'border-transparent'} hover:opacity-80 transition" onclick="trocarFoto('${escaparHTML(url)}', this)">
                `).join('');
            } else {
                galeriaContainer.style.display = 'none';
            }
        }

        // Especificações
        const elemQuartos = document.getElementById('spec-quartos');
        const elemBanheiros = document.getElementById('spec-banheiros');
        const elemVagas = document.getElementById('spec-vagas');
        const elemArea = document.getElementById('spec-area');

        if (elemQuartos) elemQuartos.innerText = quartos;
        if (elemBanheiros) elemBanheiros.innerText = banheiros;
        if (elemVagas) elemVagas.innerText = vagas;
        if (elemArea) elemArea.innerText = area !== '-' ? `${area} m²` : '-';

        // Descrição e Tags
        const elemDescricao = document.getElementById('imovel-descricao');
        if (elemDescricao) elemDescricao.innerText = descricao;

        const containerTags = document.getElementById('imovel-tags');
        if (containerTags) {
            if (tags.length > 0) {
                containerTags.innerHTML = tags.map(t => `
                    <span class="bg-brand-cream border border-[#dfd7c8] text-brand-brown font-bold text-[11px] px-2.5 py-1 rounded-md">
                        ${escaparHTML(t)}
                    </span>
                `).join('');
            } else {
                containerTags.innerHTML = '<span class="text-xs text-stone-400">Nenhum diferencial cadastrado.</span>';
            }
        }

        // Preço
        const elemPreco = document.getElementById('imovel-preco');
        if (elemPreco) elemPreco.innerText = valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });

        if (valorCondominio > 0) {
            const condElem = document.getElementById('imovel-condominio');
            const valCond = document.getElementById('val-condominio');
            if (condElem) condElem.classList.remove('hidden');
            if (valCond) valCond.innerText = valorCondominio.toLocaleString('pt-BR');
        }

        // Contatos
        const elemNomeProp = document.getElementById('proprietario-nome');
        const elemInicialProp = document.getElementById('proprietario-inicial');
        const elemEmailProp = document.getElementById('proprietario-email');

        if (elemNomeProp) elemNomeProp.innerText = nomeContato;
        if (elemInicialProp) elemInicialProp.innerText = nomeContato.charAt(0).toUpperCase();

        if (elemEmailProp) {
            elemEmailProp.innerText = emailContato;
            elemEmailProp.href = `mailto:${emailContato}?subject=Interesse no imóvel: ${encodeURIComponent(titulo)}`;
        }

        const btnWhatsapp = document.getElementById('btn-whatsapp');
        if (btnWhatsapp) {
            if (telefoneContato) {
                const numLimpo = telefoneContato.replace(/\D/g, '');
                btnWhatsapp.style.display = 'flex';
                btnWhatsapp.href = `https://wa.me/55${numLimpo}?text=Olá,%20tenho%20interesse%20no%20imóvel:%20${encodeURIComponent(titulo)}`;
            } else {
                btnWhatsapp.style.display = 'none';
            }
        }

        const formContato = document.getElementById('form-contato-direto');
        if (formContato) {
            formContato.addEventListener('submit', (e) => {
                e.preventDefault();
                const nomeInput = document.getElementById('msg-nome')?.value || '';
                const msgInput = document.getElementById('msg-texto')?.value || '';
                const mailtoUrl = `mailto:${emailContato}?subject=Interesse no imóvel - ${encodeURIComponent(titulo)}&body=Nome: ${encodeURIComponent(nomeInput)}%0A%0AMensagem:%0A${encodeURIComponent(msgInput)}`;
                window.location.href = mailtoUrl;
            });
        }

        // Exibe a tela após carregar tudo
        if (loadingState) loadingState.classList.add('hidden');
        if (imovelConteudo) imovelConteudo.classList.remove('hidden');
    }

    window.trocarFoto = (url, elemento) => {
        const fotoPrincipal = document.getElementById('foto-principal');
        if (fotoPrincipal) fotoPrincipal.src = url;

        document.querySelectorAll('.thumb-foto').forEach(el => {
            el.classList.remove('border-brand-green');
            el.classList.add('border-transparent');
        });
        if (elemento) {
            elemento.classList.remove('border-transparent');
            elemento.classList.add('border-brand-green');
        }
    };
});