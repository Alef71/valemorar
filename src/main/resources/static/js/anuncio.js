document.addEventListener('DOMContentLoaded', async () => {
    // 1. Configuração da Base URL
    const API_BASE_URL = window.API_BASE_URL || (
        ['localhost', '127.0.0.1'].includes(window.location.hostname)
            ? 'http://localhost:8080'
            : window.location.origin
    );
    const API_BASE = `${API_BASE_URL}/api`;

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

    // Popula selects de cidades que existirem no DOM
    function popularSelectsCidade() {
        const idsSelect = ['anuncio-cidade', 'end-cidade', 'anuncio-cidade-select'];
        idsSelect.forEach(id => {
            const selectElem = document.getElementById(id);
            if (selectElem && selectElem.tagName === 'SELECT') {
                const valorAtual = selectElem.value;
                selectElem.innerHTML = '<option value="">Selecione uma Cidade do Vale</option>' +
                    CIDADES_JEQUITINHONHA.map(cidade => `<option value="${cidade}">${cidade}</option>`).join('');
                if (valorAtual) selectElem.value = valorAtual;
            }
        });
    }

    popularSelectsCidade();

    // 2. Validação de Autenticação
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'auth.html';
        return;
    }

    let usuarioLogado = null;
    let anunciosEmMemoria = [];
    let fotosLista = [];

    // Função para extrair a mensagem de erro exata retornada pelo Backend
    async function extrairMensagemErro(response) {
        try {
            const data = await response.json();
            if (typeof data === 'string') return data;

            // Erros de validação padrão do Spring Boot
            if (Array.isArray(data.errors)) {
                return data.errors.map(e => typeof e === 'object' ? `${e.field ? e.field + ': ' : ''}${e.defaultMessage || e.message}` : e).join('\n');
            }

            if (data.errors && typeof data.errors === 'object') {
                return Object.entries(data.errors).map(([k, v]) => `${k}: ${v}`).join('\n');
            }

            return data.message || data.erro || data.error || data.details || `Erro HTTP ${response.status}`;
        } catch (e) {
            return `Erro HTTP ${response.status} (${response.statusText})`;
        }
    }

    // Função de Escape HTML para Segurança (XSS)
    function escaparHTML(texto) {
        if (!texto) return '';
        return texto.toString().replace(/[&<>'"]/g, (tag) => {
            const caracteres = {
                '&': '&amp;',
                '<': '&lt;',
                '>': '&gt;',
                "'": '&#39;',
                '"': '&quot;'
            };
            return caracteres[tag] || tag;
        });
    }

    // Mapeamento de Elementos do DOM
    const btnNovoAnuncio = document.getElementById('btn-novo-anuncio');
    const modalAnuncio = document.getElementById('modal-anuncio');
    const btnFecharModal = document.getElementById('btn-fechar-modal-anuncio');
    const btnCancelarModal = document.getElementById('btn-cancelar-anuncio');
    const formAnuncioModal = document.getElementById('form-anuncio');
    const formAnunciarPagina = document.getElementById('form-anunciar');
    const anunciosContainer = document.getElementById('anuncios-info-container');
    const totalAnunciosElem = document.getElementById('total-anuncios');

    // Gerenciador de Fotos
    const inputFotoUrl = document.getElementById('foto-url-input');
    const checkFotoCapa = document.getElementById('foto-capa-checkbox');
    const btnAddFoto = document.getElementById('btn-add-foto');
    const containerFotos = document.getElementById('lista-fotos-container');

    // 3. CARREGAR IDENTIDADE DO USUÁRIO
    async function carregarUsuario() {
        try {
            const localUser = localStorage.getItem('usuario');
            if (localUser && localUser !== 'undefined') {
                usuarioLogado = JSON.parse(localUser);
            }
        } catch (e) {
            console.error('Erro ao ler localStorage:', e);
        }

        try {
            const res = await fetch(`${API_BASE}/usuarios/me`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (res.ok) {
                const userBackend = await res.json();
                usuarioLogado = { ...usuarioLogado, ...userBackend };
                localStorage.setItem('usuario', JSON.stringify(usuarioLogado));
            } else if (res.status === 401 || res.status === 403) {
                localStorage.removeItem('usuario');
                localStorage.removeItem('token');
                window.location.href = 'auth.html';
                return;
            }
        } catch (err) {
            console.warn('Servidor inacessível ao carregar usuário, mantendo cache.');
        }

        if (!usuarioLogado || !usuarioLogado.id) {
            window.location.href = 'auth.html';
            return;
        }

        if (anunciosContainer) {
            carregarMeusAnuncios();
        }
    }

    // 4. BUSCAR ANÚNCIOS DO ANUNCIANTE
    async function carregarMeusAnuncios() {
        if (!usuarioLogado || !usuarioLogado.id || !anunciosContainer) return;

        try {
            const response = await fetch(`${API_BASE}/anuncios/anunciante/${usuarioLogado.id}`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (response.ok) {
                const data = await response.json();
                anunciosEmMemoria = data.content || data || [];
                renderizarAnuncios(anunciosEmMemoria);
            } else {
                anunciosContainer.innerHTML = '<p class="empty-message text-sm text-brand-textSecondary">Erro ao carregar seus anúncios.</p>';
            }
        } catch (error) {
            console.error('Erro ao buscar anúncios:', error);
            anunciosContainer.innerHTML = '<p class="empty-message text-sm text-brand-textSecondary">Erro de conexão ao carregar anúncios.</p>';
        }
    }

    // 5. RENDERIZAR LISTA DE ANÚNCIOS
    function renderizarAnuncios(lista) {
        if (totalAnunciosElem) {
            totalAnunciosElem.innerText = lista.length;
        }

        if (!anunciosContainer) return;

        if (lista.length === 0) {
            anunciosContainer.innerHTML = '<p class="empty-message text-sm text-brand-textSecondary">Nenhum anúncio cadastrado no momento.</p>';
            return;
        }

        anunciosContainer.innerHTML = lista.map(anuncio => {
            const tagsFormatted = anuncio.tags && anuncio.tags.length > 0 
                ? anuncio.tags.map(t => `<span class="bg-gray-200 text-gray-800 text-[10px] font-semibold px-2 py-0.5 rounded mr-1">${escaparHTML(t)}</span>`).join('') 
                : '<em class="text-xs text-gray-400">Nenhuma tag</em>';

            const cidadeSegura = escaparHTML(anuncio.cidade || anuncio.imovel?.endereco?.cidade || 'Vale do Jequitinhonha');
            const tipoSeguro = escaparHTML(anuncio.tipoImovel || anuncio.imovel?.tipoImovel || 'RESIDENCIAL');
            const modalidadeSegura = escaparHTML(anuncio.modalidade || 'ALUGUEL');
            const statusSeguro = escaparHTML(anuncio.status || 'ATIVO');

            return `
                <div class="anuncio-card border border-[#dfd7c8] p-4 rounded-xl mb-4 bg-white shadow-sm">
                    <div class="flex justify-between items-center mb-2">
                        <h4 class="font-bold text-brand-textPrimary">${cidadeSegura} - ${tipoSeguro} (${modalidadeSegura})</h4>
                        <span class="status-badge px-2 py-1 rounded text-xs font-bold bg-gray-100 text-brand-textPrimary">
                            ${statusSeguro}
                        </span>
                    </div>
                    <p class="text-xs text-brand-textSecondary mb-2">
                        <strong>Valor:</strong> R$ ${parseFloat(anuncio.valor || 0).toFixed(2)} | 
                        <strong>Condomínio:</strong> R$ ${parseFloat(anuncio.valorCondominio || 0).toFixed(2)} | 
                        <strong>IPTU:</strong> R$ ${parseFloat(anuncio.valorIptu || 0).toFixed(2)}
                    </p>
                    <p class="text-xs text-brand-textSecondary mb-2">
                        <strong>Quartos:</strong> ${anuncio.quartos || anuncio.imovel?.quartos || 0} | <strong>Tags:</strong> ${tagsFormatted}
                    </p>
                    <p class="text-[11px] text-gray-500 mb-3">
                        <strong>Expira em:</strong> ${anuncio.expiraEm ? new Date(anuncio.expiraEm).toLocaleDateString('pt-BR') : 'Não definida'}
                    </p>
                    <div class="anuncio-actions flex gap-2 flex-wrap">
                        <button class="btn-editar-anuncio px-3 py-1 bg-gray-100 hover:bg-gray-200 text-xs font-bold rounded border" data-id="${anuncio.id}">Editar</button>
                        <button class="btn-status-anuncio px-3 py-1 bg-gray-100 hover:bg-gray-200 text-xs font-bold rounded border" data-id="${anuncio.id}" data-status="${statusSeguro}">Alterar Status</button>
                        <button class="btn-renovar-anuncio px-3 py-1 bg-gray-100 hover:bg-gray-200 text-xs font-bold rounded border" data-id="${anuncio.id}">Renovar (90d)</button>
                        <button class="btn-deletar-anuncio px-3 py-1 bg-red-600 hover:bg-red-700 text-white text-xs font-bold rounded" data-id="${anuncio.id}">Excluir</button>
                    </div>
                </div>
            `;
        }).join('');
    }

    // Eventos Delegados nos Cards de Anúncios
    if (anunciosContainer) {
        anunciosContainer.addEventListener('click', (e) => {
            const btn = e.target.closest('button');
            if (!btn) return;

            const id = btn.getAttribute('data-id');
            if (btn.classList.contains('btn-editar-anuncio')) {
                const item = anunciosEmMemoria.find(a => String(a.id) === String(id));
                if (item) abrirModalAnuncio(item);
            } else if (btn.classList.contains('btn-status-anuncio')) {
                const statusAtual = btn.getAttribute('data-status');
                const novoStatus = prompt('Digite o novo status (ATIVO, ALUGADO, INDISPONIVEL):', statusAtual || 'ALUGADO');
                if (novoStatus) alterarStatus(id, novoStatus.toUpperCase());
            } else if (btn.classList.contains('btn-renovar-anuncio')) {
                if (confirm('Deseja renovar este anúncio por mais 90 dias?')) renovarAnuncio(id);
            } else if (btn.classList.contains('btn-deletar-anuncio')) {
                if (confirm('Tem certeza que deseja excluir este anúncio?')) deletarAnuncio(id);
            }
        });
    }

    // 6. GERENCIAMENTO DE FOTOS
    if (btnAddFoto) {
        btnAddFoto.addEventListener('click', () => {
            const url = inputFotoUrl?.value.trim();
            if (!url) return alert('Informe uma URL de imagem válida.');

            const isCapa = checkFotoCapa?.checked;
            if (isCapa) fotosLista.forEach(f => f.capa = false);

            fotosLista.push({ url, capa: isCapa });
            if (inputFotoUrl) inputFotoUrl.value = '';
            if (checkFotoCapa) checkFotoCapa.checked = false;
            renderizarFotos();
        });
    }

    function renderizarFotos() {
        if (!containerFotos) return;
        if (fotosLista.length === 0) {
            containerFotos.innerHTML = '<p class="col-span-full text-center text-xs text-brand-textSecondary my-auto">Nenhuma foto adicionada ainda.</p>';
            return;
        }

        containerFotos.innerHTML = fotosLista.map((foto, index) => `
            <div class="relative group border rounded-lg overflow-hidden bg-gray-100 h-20">
                <img src="${escaparHTML(foto.url)}" class="w-full h-full object-cover" alt="Foto">
                ${foto.capa ? '<span class="absolute top-1 left-1 bg-brand-gold text-white text-[10px] font-bold px-1.5 py-0.5 rounded">Capa</span>' : ''}
                <button type="button" data-index="${index}" class="btn-remover-foto absolute top-1 right-1 bg-red-600 text-white rounded-full w-5 h-5 text-xs flex items-center justify-center font-bold shadow">&times;</button>
            </div>
        `).join('');
    }

    if (containerFotos) {
        containerFotos.addEventListener('click', (e) => {
            if (e.target.classList.contains('btn-remover-foto')) {
                const index = parseInt(e.target.getAttribute('data-index'), 10);
                fotosLista.splice(index, 1);
                renderizarFotos();
            }
        });
    }

    // 7. CONTROLE DO MODAL
    if (btnNovoAnuncio) {
        btnNovoAnuncio.addEventListener('click', () => abrirModalAnuncio(null));
    }

    function fecharModal() {
        if (modalAnuncio) modalAnuncio.classList.add('hidden');
    }

    if (btnFecharModal) btnFecharModal.onclick = fecharModal;
    if (btnCancelarModal) btnCancelarModal.onclick = fecharModal;

    function abrirModalAnuncio(anuncio = null) {
        popularSelectsCidade();

        const tituloElem = document.getElementById('modal-anuncio-titulo');
        if (tituloElem) tituloElem.innerText = anuncio ? 'Editar Anúncio' : 'Criar Anúncio';

        const elemId = document.getElementById('anuncio-id');
        const elemImovelId = document.getElementById('anuncio-imovel-id');
        if (elemId) elemId.value = anuncio?.id || '';
        if (elemImovelId) elemImovelId.value = anuncio?.imovelId || '';

        const setVal = (id, val) => {
            const el = document.getElementById(id);
            if (el) el.value = val;
        };

        setVal('anuncio-cidade', anuncio?.cidade || anuncio?.imovel?.endereco?.cidade || '');
        setVal('anuncio-modalidade', anuncio?.modalidade || 'ALUGUEL');
        setVal('anuncio-tipo-imovel', anuncio?.tipoImovel || 'RESIDENCIAL');
        setVal('anuncio-quartos', anuncio?.quartos ?? 1);
        setVal('anuncio-valor', anuncio?.valor || '');
        setVal('anuncio-condominio', anuncio?.valorCondominio ?? 0);
        setVal('anuncio-iptu', anuncio?.valorIptu ?? 0);
        setVal('anuncio-tags', anuncio?.tags ? anuncio.tags.join(', ') : '');

        if (inputFotoUrl) inputFotoUrl.value = '';
        if (checkFotoCapa) checkFotoCapa.checked = false;

        fotosLista = anuncio?.fotos ? [...anuncio.fotos] : [];
        renderizarFotos();

        if (modalAnuncio) modalAnuncio.classList.remove('hidden');
    }

    // 8. PROCESSAR CADASTRO COMPLETO (ENDEREÇO -> IMÓVEL -> ANÚNCIO)
    async function criarAnuncioCompleto(dadosForm) {
        // PASSO 1: CADASTRAR ENDEREÇO
        let cepVal = (dadosForm.cep || '').toString().trim();
        if (!cepVal) {
            cepVal = '35160000';
        }

        const enderecoPayload = {
            logradouro: dadosForm.logradouro || 'Rua Principal',
            numero: dadosForm.numero || '100',
            complemento: dadosForm.complemento || null,
            bairro: dadosForm.bairro || 'Centro',
            cidade: dadosForm.cidade || 'Diamantina',
            estado: (dadosForm.estado || 'MG').toUpperCase(),
            cep: cepVal
        };

        const resEndereco = await fetch(`${API_BASE}/enderecos`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(enderecoPayload)
        });

        if (!resEndereco.ok) {
            const msgErro = await extrairMensagemErro(resEndereco);
            console.error('Erro backend ao cadastrar endereço:', resEndereco.status, msgErro);
            throw new Error(`Erro ao cadastrar endereço:\n${msgErro}`);
        }

        const enderecoCriado = await resEndereco.json();

        // PASSO 2: CADASTRAR IMÓVEL
        const imovelPayload = {
            locadorId: usuarioLogado.id,
            enderecoId: enderecoCriado.id,
            titulo: dadosForm.titulo || `Imóvel em ${enderecoPayload.cidade}`,
            descricao: dadosForm.descricao || null,
            tipoImovel: dadosForm.tipoImovel || 'RESIDENCIAL',
            areaTotal: dadosForm.areaTotal || null,
            areaConstruida: dadosForm.areaConstruida || null,
            quartos: dadosForm.quartos || 0,
            suites: dadosForm.suites || 0,
            banheiros: dadosForm.banheiros || 0,
            vagasGaragem: dadosForm.vagasGaragem || 0,
            andar: dadosForm.andar || 0,
            mobiliado: dadosForm.mobiliado || false,
            aceitaPet: dadosForm.aceitaPet || false,
            valorCondominio: dadosForm.valorCondominio || 0,
            valorIptu: dadosForm.valorIptu || 0
        };

        const resImovel = await fetch(`${API_BASE}/imoveis`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(imovelPayload)
        });

        if (!resImovel.ok) {
            const msgErro = await extrairMensagemErro(resImovel);
            console.error('Erro backend ao cadastrar imóvel:', resImovel.status, msgErro);
            throw new Error(`Erro ao cadastrar imóvel:\n${msgErro}`);
        }

        const imovelCriado = await resImovel.json();

        // PASSO 3: PUBLICAR ANÚNCIO VINCULADO AO IMÓVEL
        const anuncioPayload = {
            imovelId: imovelCriado.id,
            anuncianteId: usuarioLogado.id,
            valor: dadosForm.valor,
            modalidade: dadosForm.modalidade || 'ALUGUEL',
            tags: dadosForm.tags || [],
            status: 'ATIVO',
            fotos: fotosLista
        };

        const resAnuncio = await fetch(`${API_BASE}/anuncios`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(anuncioPayload)
        });

        if (!resAnuncio.ok) {
            const msgErro = await extrairMensagemErro(resAnuncio);
            console.error('Erro backend ao cadastrar anúncio:', resAnuncio.status, msgErro);
            throw new Error(`Erro ao publicar anúncio:\n${msgErro}`);
        }

        return await resAnuncio.json();
    }

    // 9. SUBMIT DO FORMULÁRIO DO MODAL
    if (formAnuncioModal) {
        formAnuncioModal.addEventListener('submit', async (e) => {
            e.preventDefault();

            if (!usuarioLogado || !usuarioLogado.id) {
                return alert('Erro ao identificar usuário logado.');
            }

            const id = document.getElementById('anuncio-id')?.value;
            const isUpdate = Boolean(id);
            const btnSubmit = formAnuncioModal.querySelector('button[type="submit"]');
            if (btnSubmit) btnSubmit.disabled = true;

            try {
                const valorRaw = parseFloat(document.getElementById('anuncio-valor')?.value);
                if (isNaN(valorRaw) || valorRaw <= 0) {
                    throw new Error('Por favor, informe um valor válido para o anúncio.');
                }

                if (isUpdate) {
                    // Atualização via PUT
                    const imovelId = document.getElementById('anuncio-imovel-id')?.value;
                    const tagsInput = document.getElementById('anuncio-tags')?.value || '';
                    const tagsArray = tagsInput ? tagsInput.split(',').map(t => t.trim()).filter(t => t.length > 0) : [];

                    const payloadUpdate = {
                        imovelId: imovelId,
                        anuncianteId: usuarioLogado.id,
                        valor: valorRaw,
                        modalidade: document.getElementById('anuncio-modalidade')?.value,
                        tags: tagsArray,
                        status: 'ATIVO',
                        fotos: fotosLista
                    };

                    const response = await fetch(`${API_BASE}/anuncios/${id}`, {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json',
                            'Authorization': `Bearer ${token}`
                        },
                        body: JSON.stringify(payloadUpdate)
                    });

                    if (response.ok) {
                        alert('Anúncio atualizado com sucesso!');
                        fecharModal();
                        carregarMeusAnuncios();
                    } else {
                        const msgErro = await extrairMensagemErro(response);
                        throw new Error(`Erro ao atualizar anúncio:\n${msgErro}`);
                    }
                } else {
                    // Novo cadastro
                    const tagsInput = document.getElementById('anuncio-tags')?.value || '';
                    const tagsArray = tagsInput ? tagsInput.split(',').map(t => t.trim()).filter(t => t.length > 0) : [];

                    const getVal = (id) => document.getElementById(id)?.value?.trim() || '';

                    const dadosForm = {
                        cep: getVal('anuncio-cep') || getVal('end-cep') || '35160000',
                        logradouro: getVal('anuncio-logradouro') || getVal('end-logradouro') || 'Rua Principal',
                        numero: getVal('anuncio-numero') || getVal('end-numero') || '100',
                        complemento: getVal('anuncio-complemento') || getVal('end-complemento') || '',
                        bairro: getVal('anuncio-bairro') || getVal('end-bairro') || 'Centro',
                        cidade: getVal('anuncio-cidade') || getVal('end-cidade') || 'Diamantina',
                        estado: getVal('anuncio-estado') || getVal('end-estado') || 'MG',
                        
                        titulo: getVal('anuncio-titulo'),
                        descricao: getVal('anuncio-descricao'),
                        tipoImovel: document.getElementById('anuncio-tipo-imovel')?.value || 'RESIDENCIAL',
                        areaTotal: parseFloat(getVal('anuncio-area-total')) || null,
                        areaConstruida: parseFloat(getVal('anuncio-area-construida')) || null,
                        quartos: parseInt(getVal('anuncio-quartos'), 10) || 0,
                        suites: parseInt(getVal('anuncio-suites'), 10) || 0,
                        banheiros: parseInt(getVal('anuncio-banheiros'), 10) || 0,
                        vagasGaragem: parseInt(getVal('anuncio-vagas'), 10) || 0,
                        andar: parseInt(getVal('anuncio-andar'), 10) || 0,
                        mobiliado: document.getElementById('anuncio-mobiliado')?.checked || false,
                        aceitaPet: document.getElementById('anuncio-aceita-pet')?.checked || false,
                        valorCondominio: parseFloat(getVal('anuncio-condominio')) || 0,
                        valorIptu: parseFloat(getVal('anuncio-iptu')) || 0,
                        
                        valor: valorRaw,
                        modalidade: document.getElementById('anuncio-modalidade')?.value || 'ALUGUEL',
                        tags: tagsArray
                    };

                    await criarAnuncioCompleto(dadosForm);
                    alert('🎉 Imóvel e anúncio cadastrados com sucesso!');
                    fecharModal();
                    carregarMeusAnuncios();
                }
            } catch (err) {
                console.error('Erro ao salvar anúncio:', err);
                alert(err.message || 'Falha ao salvar o anúncio.');
            } finally {
                if (btnSubmit) btnSubmit.disabled = false;
            }
        });
    }

    // 10. SUBMIT DO FORMULÁRIO DA PÁGINA COMPLETA
    if (formAnunciarPagina) {
        formAnunciarPagina.addEventListener('submit', async (e) => {
            e.preventDefault();

            if (!usuarioLogado || !usuarioLogado.id) {
                return alert('Erro ao identificar usuário logado.');
            }

            const btnSubmit = formAnunciarPagina.querySelector('button[type="submit"]');
            if (btnSubmit) btnSubmit.disabled = true;

            try {
                const valorRaw = parseFloat(document.getElementById('anuncio-valor')?.value);
                if (isNaN(valorRaw) || valorRaw <= 0) {
                    throw new Error('Por favor, informe um valor válido para o anúncio.');
                }

                const tagsInput = document.getElementById('anuncio-tags')?.value || '';
                const tagsArray = tagsInput ? tagsInput.split(',').map(t => t.trim()).filter(t => t.length > 0) : [];

                const getVal = (id) => document.getElementById(id)?.value?.trim() || '';

                const dadosForm = {
                    cep: getVal('anuncio-cep') || '35160000',
                    logradouro: getVal('anuncio-logradouro') || 'Rua Principal',
                    numero: getVal('anuncio-numero') || '100',
                    complemento: getVal('anuncio-complemento'),
                    bairro: getVal('anuncio-bairro') || 'Centro',
                    cidade: getVal('anuncio-cidade') || 'Diamantina',
                    estado: getVal('anuncio-estado') || 'MG',
                    
                    titulo: getVal('anuncio-titulo'),
                    descricao: getVal('anuncio-descricao'),
                    tipoImovel: document.getElementById('anuncio-tipo-imovel')?.value || 'RESIDENCIAL',
                    areaTotal: parseFloat(getVal('anuncio-area-total')) || null,
                    areaConstruida: parseFloat(getVal('anuncio-area-construida')) || null,
                    quartos: parseInt(getVal('anuncio-quartos'), 10) || 0,
                    suites: parseInt(getVal('anuncio-suites'), 10) || 0,
                    banheiros: parseInt(getVal('anuncio-banheiros'), 10) || 0,
                    vagasGaragem: parseInt(getVal('anuncio-vagas'), 10) || 0,
                    andar: parseInt(getVal('anuncio-andar'), 10) || 0,
                    mobiliado: document.getElementById('anuncio-mobiliado')?.checked || false,
                    aceitaPet: document.getElementById('anuncio-aceita-pet')?.checked || false,
                    valorCondominio: parseFloat(getVal('anuncio-condominio')) || 0,
                    valorIptu: parseFloat(getVal('anuncio-iptu')) || 0,
                    
                    valor: valorRaw,
                    modalidade: document.getElementById('anuncio-modalidade')?.value || 'ALUGUEL',
                    tags: tagsArray
                };

                await criarAnuncioCompleto(dadosForm);
                alert('🎉 Imóvel e anúncio cadastrados com sucesso!');
                window.location.href = 'painel.html';

            } catch (err) {
                console.error('Erro na publicação do anúncio:', err);
                alert(err.message || 'Ocorreu um erro ao processar seu anúncio.');
            } finally {
                if (btnSubmit) btnSubmit.disabled = false;
            }
        });
    }

    // 11. AÇÕES AUXILIARES DE API (STATUS, RENOVAR, EXCLUIR)
    async function alterarStatus(id, novoStatus) {
        try {
            const response = await fetch(`${API_BASE}/anuncios/${id}/status?status=${encodeURIComponent(novoStatus)}`, {
                method: 'PATCH',
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (response.ok) {
                alert('Status alterado com sucesso!');
                carregarMeusAnuncios();
            } else {
                alert('Não foi possível alterar o status.');
            }
        } catch (err) {
            alert('Erro de conexão ao alterar status.');
        }
    }

    async function renovarAnuncio(id) {
        try {
            const response = await fetch(`${API_BASE}/anuncios/${id}/renovar`, {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (response.ok) {
                alert('Anúncio renovado por mais 90 dias!');
                carregarMeusAnuncios();
            } else {
                alert('Não foi possível renovar o anúncio.');
            }
        } catch (err) {
            alert('Erro de conexão ao renovar anúncio.');
        }
    }

    async function deletarAnuncio(id) {
        try {
            const response = await fetch(`${API_BASE}/anuncios/${id}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (response.status === 204 || response.ok) {
                alert('Anúncio deletado com sucesso.');
                carregarMeusAnuncios();
            } else {
                alert('Não foi possível deletar o anúncio.');
            }
        } catch (err) {
            alert('Erro de conexão ao deletar anúncio.');
        }
    }

    // Inicialização do Script
    carregarUsuario();
});