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

    // Elementos de Perfil e Header
    const userNameElem = document.getElementById('user-name');
    const welcomeNameElem = document.getElementById('welcome-name');
    const userEmailElem = document.getElementById('user-email');
    const userRoleElem = document.getElementById('user-role');
    const userAvatarElem = document.getElementById('user-avatar');
    const headerCityElem = document.getElementById('header-user-city');

    // Elementos de Modais do Perfil e Endereço
    const modalPerfil = document.getElementById('modal-perfil');
    const btnAbrirPerfil = document.getElementById('btn-abrir-modal-perfil');
    const btnFecharPerfil = document.getElementById('btn-fechar-modal-perfil');
    const btnCancelarPerfil = document.getElementById('btn-cancelar-perfil');
    const formEditarPerfil = document.getElementById('form-editar-perfil');
    const editNome = document.getElementById('edit-nome');
    const editFotoFile = document.getElementById('edit-foto-file');
    const editFotoPreview = document.getElementById('edit-foto-preview');

    const modalEndereco = document.getElementById('modal-endereco');
    const btnAbrirEndereco = document.getElementById('btn-abrir-modal-endereco');
    const btnFecharEndereco = document.getElementById('btn-fechar-modal-endereco');
    const btnCancelarEndereco = document.getElementById('btn-cancelar-endereco');
    const formEndereco = document.getElementById('form-endereco');
    const enderecoContainer = document.getElementById('endereco-info-container');

    // Logout
    const btnLogout = document.getElementById('btn-logout');
    if (btnLogout) {
        btnLogout.addEventListener('click', async () => {
            const currentToken = localStorage.getItem('token');
            try {
                await fetch(`${API_BASE}/auth/logout`, { 
                    method: 'POST',
                    headers: { 'Authorization': `Bearer ${currentToken}` }
                });
            } catch (e) {
                console.warn('Erro ao deslogar:', e);
            }
            localStorage.removeItem('usuario');
            localStorage.removeItem('token');
            window.location.href = 'auth.html';
        });
    }

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
                headers: { 'Authorization': `Bearer ${currentToken}` }
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
            headerCityElem.innerText = `${usuarioLogado.cidade.toUpperCase()} ${usuarioLogado.estado ? usuarioLogado.estado.toUpperCase() : 'MG'}`;
        }
    }

    // 2. BUSCAR E RENDERIZAR ENDEREÇO DO USUÁRIO
    async function carregarEnderecoUsuario() {
        if (!usuarioLogado || !usuarioLogado.id) return;

        const cacheLocal = localStorage.getItem(`enderecoUsuario_${usuarioLogado.id}`);
        if (cacheLocal) {
            try {
                enderecoUsuarioMemoria = JSON.parse(cacheLocal);
                if (enderecoUsuarioMemoria && enderecoUsuarioMemoria.logradouro) {
                    renderizarEnderecoUsuario(enderecoUsuarioMemoria);
                    return;
                }
            } catch (e) {
                console.warn('Erro ao ler endereço do cache:', e);
            }
        }

        if (usuarioLogado.endereco && usuarioLogado.endereco.logradouro) {
            enderecoUsuarioMemoria = usuarioLogado.endereco;
            renderizarEnderecoUsuario(enderecoUsuarioMemoria);
            return;
        }

        const currentToken = localStorage.getItem('token');
        try {
            const res = await fetch(`${API_BASE}/enderecos/usuario/${usuarioLogado.id}`, {
                headers: { 'Authorization': `Bearer ${currentToken}` }
            });

            if (res.ok) {
                const data = await res.json();
                const end = Array.isArray(data) ? data[0] : data;
                if (end && end.logradouro) {
                    enderecoUsuarioMemoria = end;
                    localStorage.setItem(`enderecoUsuario_${usuarioLogado.id}`, JSON.stringify(enderecoUsuarioMemoria));
                    renderizarEnderecoUsuario(enderecoUsuarioMemoria);
                    return;
                }
            }
        } catch (err) {
            console.warn('Busca de endereço via API não retornou dados, verificando estado local.');
        }

        if (!enderecoUsuarioMemoria || !enderecoUsuarioMemoria.logradouro) {
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
                <p><strong>Bairro:</strong> ${escaparHTML(end.bairro)} | <strong>Cidade:</strong> ${escaparHTML(end.cidade)} ${escaparHTML(end.estado)}</p>
                <p><strong>CEP:</strong> ${escaparHTML(end.cep)}</p>
            </div>
        `;
    }

    // 3. EDITAR PERFIL (PUT /api/usuarios/me)
    if (btnAbrirPerfil) {
        btnAbrirPerfil.addEventListener('click', () => {
            if (editNome) editNome.value = usuarioLogado?.nome || '';
            if (editFotoPreview) {
                const foto = usuarioLogado?.fotoPerfil || usuarioLogado?.fotoUrl;
                if (foto) {
                    editFotoPreview.src = foto;
                    editFotoPreview.classList.remove('hidden');
                } else {
                    editFotoPreview.classList.add('hidden');
                }
            }
            if (modalPerfil) modalPerfil.classList.remove('hidden');
        });
    }

    function fecharModalPerfil() {
        if (modalPerfil) modalPerfil.classList.add('hidden');
    }
    if (btnFecharPerfil) btnFecharPerfil.onclick = fecharModalPerfil;
    if (btnCancelarPerfil) btnCancelarPerfil.onclick = fecharModalPerfil;

    if (editFotoFile) {
        editFotoFile.addEventListener('change', (e) => {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = (evt) => {
                    if (editFotoPreview) {
                        editFotoPreview.src = evt.target.result;
                        editFotoPreview.classList.remove('hidden');
                    }
                };
                reader.readAsDataURL(file);
            }
        });
    }

    if (formEditarPerfil) {
        formEditarPerfil.addEventListener('submit', async (e) => {
            e.preventDefault();
            const currentToken = localStorage.getItem('token');
            const nomeVal = editNome?.value.trim();

            if (!nomeVal || nomeVal.length < 3) {
                return alert('O nome deve ter pelo menos 3 caracteres.');
            }

            let fotoBase64 = usuarioLogado?.fotoPerfil || usuarioLogado?.fotoUrl || '';
            const file = editFotoFile?.files[0];
            
            if (file) {
                fotoBase64 = await new Promise((resolve) => {
                    const reader = new FileReader();
                    reader.onload = (evt) => resolve(evt.target.result);
                    reader.readAsDataURL(file);
                });
            }

            const payload = {
                nome: nomeVal,
                fotoPerfil: fotoBase64
            };

            try {
                const res = await fetch(`${API_BASE}/usuarios/me`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${currentToken}`
                    },
                    body: JSON.stringify(payload)
                });

                if (res.ok) {
                    const usuarioAtualizado = await res.json();
                    usuarioLogado = { ...usuarioLogado, ...usuarioAtualizado };
                    localStorage.setItem('usuario', JSON.stringify(usuarioLogado));
                    atualizarInterfacePerfil();
                    alert('Perfil atualizado com sucesso!');
                    fecharModalPerfil();
                } else {
                    const erro = await res.json().catch(() => ({}));
                    alert(erro.message || 'Erro ao atualizar perfil.');
                }
            } catch (err) {
                console.error(err);
                alert('Erro de conexão ao atualizar perfil.');
            }
        });
    }

    // 4. CADASTRAR / EDITAR ENDEREÇO
    if (btnAbrirEndereco) {
        btnAbrirEndereco.addEventListener('click', () => {
            if (enderecoUsuarioMemoria) {
                document.getElementById('end-cep').value = enderecoUsuarioMemoria.cep || '';
                document.getElementById('end-logradouro').value = enderecoUsuarioMemoria.logradouro || '';
                document.getElementById('end-numero').value = enderecoUsuarioMemoria.numero || '';
                document.getElementById('end-complemento').value = enderecoUsuarioMemoria.complemento || '';
                document.getElementById('end-bairro').value = enderecoUsuarioMemoria.bairro || '';
                document.getElementById('end-cidade').value = enderecoUsuarioMemoria.cidade || '';
                document.getElementById('end-estado').value = enderecoUsuarioMemoria.estado || '';
            }
            if (modalEndereco) modalEndereco.classList.remove('hidden');
        });
    }

    function fecharModalEndereco() {
        if (modalEndereco) modalEndereco.classList.add('hidden');
    }
    if (btnFecharEndereco) btnFecharEndereco.onclick = fecharModalEndereco;
    if (btnCancelarEndereco) btnCancelarEndereco.onclick = fecharModalEndereco;

    if (formEndereco) {
        formEndereco.addEventListener('submit', async (e) => {
            e.preventDefault();
            const currentToken = localStorage.getItem('token');

            let cepVal = document.getElementById('end-cep')?.value.trim() || '';

            const payload = {
                logradouro: document.getElementById('end-logradouro')?.value.trim(),
                numero: document.getElementById('end-numero')?.value.trim(),
                complemento: document.getElementById('end-complemento')?.value.trim() || null,
                bairro: document.getElementById('end-bairro')?.value.trim(),
                cidade: document.getElementById('end-cidade')?.value.trim(),
                estado: document.getElementById('end-estado')?.value.trim().toUpperCase(),
                cep: cepVal
            };

            const isUpdate = Boolean(enderecoUsuarioMemoria?.id);
            const url = isUpdate 
                ? `${API_BASE}/enderecos/${enderecoUsuarioMemoria.id}` 
                : `${API_BASE}/enderecos`;
            const method = isUpdate ? 'PUT' : 'POST';

            try {
                const res = await fetch(url, {
                    method: method,
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${currentToken}`
                    },
                    body: JSON.stringify(payload)
                });

                if (res.ok || res.status === 201) {
                    const enderecoSalvo = await res.json().catch(() => null);
                    
                    if (enderecoSalvo && enderecoSalvo.logradouro) {
                        enderecoUsuarioMemoria = enderecoSalvo;
                    } else {
                        enderecoUsuarioMemoria = { ...payload, id: enderecoUsuarioMemoria?.id || null };
                    }

                    if (usuarioLogado?.id) {
                        localStorage.setItem(`enderecoUsuario_${usuarioLogado.id}`, JSON.stringify(enderecoUsuarioMemoria));
                    }

                    renderizarEnderecoUsuario(enderecoUsuarioMemoria);
                    alert(`Endereço ${isUpdate ? 'atualizado' : 'cadastrado'} com sucesso!`);
                    fecharModalEndereco();
                } else {
                    const erro = await res.json().catch(() => ({}));
                    alert(erro.message || 'Erro ao salvar endereço. Verifique se todos os campos obrigatórios foram preenchidos.');
                }
            } catch (err) {
                console.error(err);
                alert('Erro de conexão ao salvar endereço.');
            }
        });
    }

    // Inicialização do Painel
    carregarUsuario();
});