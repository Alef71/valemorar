document.addEventListener('DOMContentLoaded', async () => {
    // Base URL Dinâmica
    const API_BASE_URL = window.API_BASE_URL || (
        ['localhost', '127.0.0.1'].includes(window.location.hostname)
            ? 'http://localhost:8080'
            : window.location.origin
    );
    const API_BASE = `${API_BASE_URL}/api`;

    // Validação inicial do Token JWT
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'auth.html';
        return;
    }

    let usuarioLogado = null;
    let anunciosEmMemoria = [];
    let fotosLista = [];
    let enderecoUsuarioMemoria = null;

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

    const userNameElem = document.getElementById('user-name');
    const welcomeNameElem = document.getElementById('welcome-name');
    const userEmailElem = document.getElementById('user-email');
    const userRoleElem = document.getElementById('user-role');
    const userAvatarElem = document.getElementById('user-avatar');
    const headerCityElem = document.getElementById('header-user-city');

    const btnLogout = document.getElementById('btn-logout');
    if (btnLogout) {
        btnLogout.addEventListener('click', async () => {
            const currentToken = localStorage.getItem('token');
            try {
                await fetch(`${API_BASE}/auth/logout`, { 
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${currentToken}`
                    }
                });
            } catch (e) {
                console.warn('Erro ao deslogar:', e);
            }
            localStorage.removeItem('usuario');
            localStorage.removeItem('token');
            window.location.href = 'auth.html';
        });
    }

    const btnNovoAnuncio = document.getElementById('btn-novo-anuncio');
    const modalAnuncio = document.getElementById('modal-anuncio');
    const btnFecharModal = document.getElementById('btn-fechar-modal-anuncio');
    const btnCancelarModal = document.getElementById('btn-cancelar-anuncio');
    const formAnuncio = document.getElementById('form-anuncio');
    const anunciosContainer = document.getElementById('anuncios-info-container');
    const totalAnunciosElem = document.getElementById('total-anuncios');

    const inputFotoUrl = document.getElementById('foto-url-input');
    const checkFotoCapa = document.getElementById('foto-capa-checkbox');
    const btnAddFoto = document.getElementById('btn-add-foto');
    const containerFotos = document.getElementById('lista-fotos-container');

    const enderecoContainer = document.getElementById('endereco-info-container');

    // 1. CARREGAR IDENTIDADE DO USUÁRIO
    async function carregarUsuario() {
        const currentToken = localStorage.getItem('token');
        if (!currentToken) {
            window.location.href = 'auth.html';
            return;
        }

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
                headers: {
                    'Authorization': `Bearer ${currentToken}`
                }
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
            console.warn('Servidor inacessível, mantendo dados em cache.');
        }

        if (!usuarioLogado || (!usuarioLogado.id && !usuarioLogado.email)) {
            window.location.href = 'auth.html';
            return;
        }

        atualizarInterfacePerfil();
        carregarEnderecoUsuario();
        carregarMeusAnuncios();
    }

    function atualizarInterfacePerfil() {
        if (!usuarioLogado) return;
        if (userNameElem) userNameElem.innerText = usuarioLogado.nome || 'Usuário';
        if (welcomeNameElem) welcomeNameElem.innerText = usuarioLogado.nome ? usuarioLogado.nome.split(' ')[0] : 'Usuário';
        if (userEmailElem) userEmailElem.innerText = usuarioLogado.email || '';
        if (userRoleElem) userRoleElem.innerText = usuarioLogado.tipo || usuarioLogado.role || 'Usuário';
        if (userAvatarElem && (usuarioLogado.fotoPerfil || usuarioLogado.fotoUrl)) {
            userAvatarElem.src = usuarioLogado.fotoPerfil || usuarioLogado.fotoUrl;
        }
        if (headerCityElem && usuarioLogado.cidade) {
            headerCityElem.innerText = `${usuarioLogado.cidade.toUpperCase()} • ${usuarioLogado.estado ? usuarioLogado.estado.toUpperCase() : 'MG'}`;
        }
    }

    // 2. BUSCAR ENDEREÇO
    async function carregarEnderecoUsuario() {
        if (!usuarioLogado || !usuarioLogado.id) return;
        const currentToken = localStorage.getItem('token');

        try {
            const res = await fetch(`${API_BASE}/enderecos/usuario/${usuarioLogado.id}`, {
                headers: {
                    'Authorization': `Bearer ${currentToken}`
                }
            });

            if (res.ok) {
                const data = await res.json();
                enderecoUsuarioMemoria = Array.isArray(data) ? data[0] : data;
                renderizarEnderecoUsuario(enderecoUsuarioMemoria);
            } else {
                renderizarEnderecoUsuario(null);
            }
        } catch (err) {
            console.error('Erro ao carregar endereço:', err);
            renderizarEnderecoUsuario(null);
        }
    }

    function renderizarEnderecoUsuario(end) {
        if (!enderecoContainer) return;
        if (!end || !end.logradouro) {
            enderecoContainer.innerHTML = '<p class="empty-message text-sm text-brand-textSecondary">Nenhum endereço cadastrado no momento.</p>';
            return;
        }

        enderecoContainer.innerHTML = `
            <div class="bg-[#fcfbf9] p-4 rounded-xl border border-[#dfd7c8] text-sm text-brand-textPrimary space-y-1">
                <p><strong>Logradouro:</strong> ${escaparHTML(end.logradouro)}, Nº ${escaparHTML(end.numero)} ${end.complemento ? ' - ' + escaparHTML(end.complemento) : ''}</p>
                <p><strong>Bairro:</strong> ${escaparHTML(end.bairro)} | <strong>Cidade:</strong> ${escaparHTML(end.cidade)} - ${escaparHTML(end.estado)}</p>
                <p><strong>CEP:</strong> ${escaparHTML(end.cep)}</p>
            </div>
        `;
    }

    // 3. BUSCAR ANÚNCIOS
    async function carregarMeusAnuncios() {
        if (!usuarioLogado || !usuarioLogado.id) return;
        const currentToken = localStorage.getItem('token');

        try {
            const response = await fetch(`${API_BASE}/anuncios/anunciante/${usuarioLogado.id}`, {
                headers: {
                    'Authorization': `Bearer ${currentToken}`
                }
            });

            if (response.ok) {
                const data = await response.json();
                anunciosEmMemoria = data.content || data || [];
                renderizarAnuncios(anunciosEmMemoria);
            } else {
                if (anunciosContainer) {
                    anunciosContainer.innerHTML = '<p class="empty-message text-sm text-brand-textSecondary">Erro ao carregar seus anúncios.</p>';
                }
            }
        } catch (error) {
            console.error('Erro ao buscar anúncios:', error);
            if (anunciosContainer) {
                anunciosContainer.innerHTML = '<p class="empty-message text-sm text-brand-textSecondary">Erro de conexão ao carregar anúncios.</p>';
            }
        }
    }

    // 4. RENDERIZAR ANÚNCIOS
    function renderizarAnuncios(lista) {
        if (totalAnunciosElem) totalAnunciosElem.innerText = lista.length;
        if (!anunciosContainer) return;

        if (lista.length === 0) {
            anunciosContainer.innerHTML = '<p class="empty-message text-sm text-brand-textSecondary">Nenhum anúncio cadastrado no momento.</p>';
            return;
        }

        anunciosContainer.innerHTML = lista.map(anuncio => {
            const tagsFormatted = anuncio.tags && anuncio.tags.length > 0 
                ? anuncio.tags.map(t => `<span class="bg-gray-200 text-gray-800 text-[10px] font-semibold px-2 py-0.5 rounded mr-1">${escaparHTML(t)}</span>`).join('') 
                : '<em class="text-xs text-gray-400">Nenhuma tag</em>';

            const cidadeSegura = escaparHTML(anuncio.cidade || '');
            const tipoSeguro = escaparHTML(anuncio.tipoImovel || 'RESIDENCIAL');
            const modalidadeSegura = escaparHTML(anuncio.modalidade || 'ALUGUEL');
            const statusSeguro = escaparHTML(anuncio.status || 'ATIVO');

            return `
                <div class="anuncio-card border border-[#dfd7c8] p-4 rounded-xl mb-4 bg-white shadow-sm">
                    <div class="flex justify-between items-center mb-2">
                        <h4 class="font-bold text-brand-textPrimary">${cidadeSegura} - ${tipoSeguro} (${modalidadeSegura})</h4>
                        <span class="status-badge px-2 py-1 rounded text-xs font-bold bg-gray-100 text-brand-textPrimary">${statusSeguro}</span>
                    </div>
                    <p class="text-xs text-brand-textSecondary mb-2">
                        <strong>Valor:</strong> R$ ${parseFloat(anuncio.valor || 0).toFixed(2)} | 
                        <strong>Condomínio:</strong> R$ ${parseFloat(anuncio.valorCondominio || 0).toFixed(2)} | 
                        <strong>IPTU:</strong> R$ ${parseFloat(anuncio.valorIptu || 0).toFixed(2)}
                    </p>
                    <p class="text-xs text-brand-textSecondary mb-2">
                        <strong>Quartos:</strong> ${anuncio.quartos || 0} | <strong>Tags:</strong> ${tagsFormatted}
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

    // Ações do Anúncio (Status, Renovar, Excluir)
    async function alterarStatus(id, novoStatus) {
        const currentToken = localStorage.getItem('token');
        try {
            const response = await fetch(`${API_BASE}/anuncios/${id}/status?status=${novoStatus}`, {
                method: 'PATCH',
                headers: { 'Authorization': `Bearer ${currentToken}` }
            });
            if (response.ok) {
                alert('Status alterado com sucesso!');
                carregarMeusAnuncios();
            } else {
                alert('Erro ao alterar status.');
            }
        } catch (e) {
            console.error('Erro:', e);
        }
    }

    async function renovarAnuncio(id) {
        const currentToken = localStorage.getItem('token');
        try {
            const response = await fetch(`${API_BASE}/anuncios/${id}/renovar`, {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${currentToken}` }
            });
            if (response.ok) {
                alert('Anúncio renovado com sucesso!');
                carregarMeusAnuncios();
            } else {
                alert('Erro ao renovar anúncio.');
            }
        } catch (e) {
            console.error('Erro:', e);
        }
    }

    async function deletarAnuncio(id) {
        const currentToken = localStorage.getItem('token');
        try {
            const response = await fetch(`${API_BASE}/anuncios/${id}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${currentToken}` }
            });
            if (response.ok) {
                alert('Anúncio excluído com sucesso!');
                carregarMeusAnuncios();
            } else {
                alert('Erro ao excluir anúncio.');
            }
        } catch (e) {
            console.error('Erro:', e);
        }
    }

    // Delegação de Eventos para Ações de Anúncios
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

    // 5. GERENCIAMENTO DE FOTOS
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

    // 6. MODAL DE ANÚNCIO
    if (btnNovoAnuncio) btnNovoAnuncio.addEventListener('click', () => abrirModalAnuncio(null));

    function fecharModal() {
        if (modalAnuncio) modalAnuncio.classList.add('hidden');
    }

    if (btnFecharModal) btnFecharModal.onclick = fecharModal;
    if (btnCancelarModal) btnCancelarModal.onclick = fecharModal;

    function abrirModalAnuncio(anuncio = null) {
        const tituloElem = document.getElementById('modal-anuncio-titulo');
        if (tituloElem) tituloElem.innerText = anuncio ? 'Editar Anúncio' : 'Criar Anúncio';
        
        document.getElementById('anuncio-id').value = anuncio?.id || '';
        document.getElementById('anuncio-imovel-id').value = anuncio?.imovelId || (window.crypto?.randomUUID ? crypto.randomUUID() : String(Date.now()));

        document.getElementById('anuncio-cidade').value = anuncio?.cidade || '';
        document.getElementById('anuncio-modalidade').value = anuncio?.modalidade || 'ALUGUEL';
        document.getElementById('anuncio-tipo-imovel').value = anuncio?.tipoImovel || 'RESIDENCIAL';
        document.getElementById('anuncio-quartos').value = anuncio?.quartos ?? 1;
        document.getElementById('anuncio-valor').value = anuncio?.valor || '';
        document.getElementById('anuncio-condominio').value = anuncio?.valorCondominio ?? 0;
        document.getElementById('anuncio-iptu').value = anuncio?.valorIptu ?? 0;
        document.getElementById('anuncio-tags').value = anuncio?.tags ? anuncio.tags.join(', ') : '';

        if (inputFotoUrl) inputFotoUrl.value = '';
        if (checkFotoCapa) checkFotoCapa.checked = false;

        fotosLista = anuncio?.fotos ? [...anuncio.fotos] : [];
        renderizarFotos();

        if (modalAnuncio) modalAnuncio.classList.remove('hidden');
    }

    // 7. SALVAR ANÚNCIO
    if (formAnuncio) {
        formAnuncio.addEventListener('submit', async (e) => {
            e.preventDefault();

            const currentToken = localStorage.getItem('token');
            const id = document.getElementById('anuncio-id')?.value;
            const imovelId = document.getElementById('anuncio-imovel-id')?.value || (window.crypto?.randomUUID ? crypto.randomUUID() : String(Date.now()));
            const tagsInput = document.getElementById('anuncio-tags')?.value;
            const tagsArray = tagsInput ? tagsInput.split(',').map(t => t.trim()).filter(t => t.length > 0) : [];

            const valorRaw = parseFloat(document.getElementById('anuncio-valor')?.value);
            if (isNaN(valorRaw) || valorRaw <= 0) {
                return alert('Por favor, informe um valor válido para o anúncio.');
            }

            const payload = {
                imovelId: imovelId,
                anuncianteId: usuarioLogado.id,
                cidade: document.getElementById('anuncio-cidade')?.value.trim(),
                tipoImovel: document.getElementById('anuncio-tipo-imovel')?.value,
                quartos: parseInt(document.getElementById('anuncio-quartos')?.value, 10) || 0,
                tags: tagsArray,
                valor: valorRaw,
                modalidade: document.getElementById('anuncio-modalidade')?.value,
                valorCondominio: parseFloat(document.getElementById('anuncio-condominio')?.value) || 0,
                valorIptu: parseFloat(document.getElementById('anuncio-iptu')?.value) || 0,
                status: 'ATIVO',
                fotos: fotosLista
            };

            const isUpdate = Boolean(id);
            const url = isUpdate ? `${API_BASE}/anuncios/${id}` : `${API_BASE}/anuncios`;
            const method = isUpdate ? 'PUT' : 'POST';

            try {
                const response = await fetch(url, {
                    method: method,
                    headers: { 
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${currentToken}`
                    },
                    body: JSON.stringify(payload)
                });

                if (response.ok || response.status === 201) {
                    alert(`Anúncio ${isUpdate ? 'atualizado' : 'criado'} com sucesso!`);
                    fecharModal();
                    carregarMeusAnuncios();
                } else {
                    const erroBody = await response.json().catch(() => ({}));
                    alert(erroBody.message || erroBody.erro || 'Erro ao salvar anúncio. Verifique os dados fornecidos.');
                }
            } catch (err) {
                alert('Falha de conexão com o servidor ao tentar salvar anúncio.');
            }
        });
    }

    // Inicialização
    carregarUsuario();
});