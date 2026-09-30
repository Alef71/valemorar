document.addEventListener('DOMContentLoaded', async () => {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'auth.html';
        return;
    }

    const API_BASE = 'http://localhost:8080/api';
    let usuarioAtual = null;
    let enderecoAtualId = null;
    let fotoBase64EmMemoria = null;

    // Elementos DOM - Perfil
    const userNameElem = document.getElementById('user-name');
    const welcomeNameElem = document.getElementById('welcome-name');
    const userEmailElem = document.getElementById('user-email');
    const userAvatarElem = document.getElementById('user-avatar');
    const enderecoContainer = document.getElementById('endereco-info-container');

    // Modais e Botões
    const modalPerfil = document.getElementById('modal-perfil');
    const modalEndereco = document.getElementById('modal-endereco');
    const btnAbrirPerfil = document.getElementById('btn-abrir-modal-perfil');
    const btnAbrirEndereco = document.getElementById('btn-abrir-modal-endereco');

    // Elementos da foto no modal
    const inputFotoFile = document.getElementById('edit-foto-file');
    const previewFoto = document.getElementById('edit-foto-preview');

    /**
     * Função para redimensionar e comprimir a imagem via HTML5 Canvas
     */
    function comprimirImagem(file, maxWidth = 300, maxHeight = 300, qualidade = 0.8) {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.readAsDataURL(file);
            reader.onload = (event) => {
                const img = new Image();
                img.src = event.target.result;
                img.onload = () => {
                    const canvas = document.createElement('canvas');
                    let width = img.width;
                    let height = img.height;

                    if (width > height) {
                        if (width > maxWidth) {
                            height = Math.round((height * maxWidth) / width);
                            width = maxWidth;
                        }
                    } else {
                        if (height > maxHeight) {
                            width = Math.round((width * maxHeight) / height);
                            height = maxHeight;
                        }
                    }

                    canvas.width = width;
                    canvas.height = height;

                    const ctx = canvas.getContext('2d');
                    ctx.drawImage(img, 0, 0, width, height);

                    const base64Comprimido = canvas.toDataURL('image/jpeg', qualidade);
                    resolve(base64Comprimido);
                };
                img.onerror = (err) => reject(err);
            };
            reader.onerror = (err) => reject(err);
        });
    }

    // Evento acionado ao escolher um arquivo de imagem do PC
    if (inputFotoFile) {
        inputFotoFile.addEventListener('change', async (e) => {
            const file = e.target.files[0];
            if (file) {
                try {
                    fotoBase64EmMemoria = await comprimirImagem(file, 300, 300, 0.8);
                    if (previewFoto) {
                        previewFoto.src = fotoBase64EmMemoria;
                        previewFoto.style.display = 'block';
                    }
                } catch (err) {
                    alert('Erro ao processar e comprimir a imagem selecionada.');
                }
            }
        });
    }

    // 1. CARREGAR DADOS DO USUÁRIO
    async function carregarPerfil() {
        try {
            const response = await fetch(`${API_BASE}/usuarios/me`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (response.ok) {
                usuarioAtual = await response.json();
                renderizarPerfil();
            } else if (response.status === 401) {
                fazerLogout();
            }
        } catch (error) {
            console.error('Erro ao carregar dados do usuário:', error);
        }
    }

    function renderizarPerfil() {
        if (!usuarioAtual) return;
        if (userNameElem) userNameElem.innerText = usuarioAtual.nome || 'Usuário';
        if (welcomeNameElem) welcomeNameElem.innerText = usuarioAtual.nome ? usuarioAtual.nome.split(' ')[0] : 'Usuário';
        if (userEmailElem) userEmailElem.innerText = usuarioAtual.email || '';

        if (userAvatarElem) {
            if (usuarioAtual.fotoPerfil) {
                userAvatarElem.src = usuarioAtual.fotoPerfil;
            } else {
                userAvatarElem.src = `https://ui-avatars.com/api/?name=${encodeURIComponent(usuarioAtual.nome || 'U')}&background=1a252f&color=fff`;
            }
        }
    }

    // 2. ATUALIZAR PERFIL (NOME E FOTO)
    if (btnAbrirPerfil) {
        btnAbrirPerfil.addEventListener('click', () => {
            const editNome = document.getElementById('edit-nome');
            if (editNome) editNome.value = usuarioAtual?.nome || '';
            if (inputFotoFile) inputFotoFile.value = '';
            fotoBase64EmMemoria = null;

            if (previewFoto) {
                if (usuarioAtual?.fotoPerfil) {
                    previewFoto.src = usuarioAtual.fotoPerfil;
                    previewFoto.style.display = 'block';
                } else {
                    previewFoto.style.display = 'none';
                }
            }

            modalPerfil?.classList.add('active');
        });
    }

    document.getElementById('btn-fechar-modal-perfil')?.addEventListener('click', () => modalPerfil?.classList.remove('active'));
    document.getElementById('btn-cancelar-perfil')?.addEventListener('click', () => modalPerfil?.classList.remove('active'));

    document.getElementById('form-editar-perfil')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const editNome = document.getElementById('edit-nome');
        const nome = editNome ? editNome.value.trim() : '';
        const fotoPerfilFinal = fotoBase64EmMemoria || usuarioAtual?.fotoPerfil || null;

        try {
            const response = await fetch(`${API_BASE}/usuarios/me`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({
                    nome: nome,
                    fotoPerfil: fotoPerfilFinal
                })
            });

            if (response.ok) {
                alert('Perfil e foto atualizados com sucesso!');
                modalPerfil?.classList.remove('active');
                carregarPerfil();
            } else {
                const erroBody = await response.json().catch(() => ({}));
                const mensagem = erroBody.message || erroBody.erro || 'Erro ao atualizar perfil.';
                alert(mensagem);
            }
        } catch (err) {
            alert('Erro de conexão com o servidor ao tentar atualizar perfil.');
        }
    });

    // 3. CARREGAR E GERENCIAR ENDEREÇO
    async function carregarEndereco() {
        try {
            const response = await fetch(`${API_BASE}/enderecos`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (response.ok) {
                const data = await response.json();
                const lista = data.content || data;

                if (Array.isArray(lista) && lista.length > 0) {
                    const end = lista[0];
                    enderecoAtualId = end.id;
                    renderizarEnderecoHTML(end);
                } else if (enderecoContainer) {
                    enderecoContainer.innerHTML = '<p class="empty-message">Nenhum endereço cadastrado no momento.</p>';
                }
            }
        } catch (error) {
            console.error('Erro ao carregar endereço:', error);
        }
    }

    function renderizarEnderecoHTML(end) {
        if (!enderecoContainer) return;
        enderecoContainer.innerHTML = `
            <div class="endereco-card">
                <p><strong>Rua:</strong> ${end.logradouro}, Nº ${end.numero} ${end.complemento ? ' - ' + end.complemento : ''}</p>
                <p><strong>Bairro:</strong> ${end.bairro} | <strong>CEP:</strong> ${end.cep}</p>
                <p><strong>Cidade:</strong> ${end.cidade} / ${end.estado}</p>
                <div class="endereco-actions">
                    <button class="btn-secondary" id="btn-editar-end-card">Editar Endereço</button>
                    <button class="btn-danger" id="btn-deletar-end-card">Excluir</button>
                </div>
            </div>
        `;

        document.getElementById('btn-editar-end-card')?.addEventListener('click', () => abrirModalEndereco(end));
        document.getElementById('btn-deletar-end-card')?.addEventListener('click', () => deletarEndereco(end.id));
    }

    // AUTOPREENCHIMENTO DE CEP (ViaCEP)
    document.getElementById('end-cep')?.addEventListener('blur', async (e) => {
        const cep = e.target.value.replace(/\D/g, '');
        if (cep.length === 8) {
            try {
                const res = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
                const data = await res.json();
                if (!data.erro) {
                    const logradouro = document.getElementById('end-logradouro');
                    const bairro = document.getElementById('end-bairro');
                    const cidade = document.getElementById('end-cidade');
                    const estado = document.getElementById('end-estado');

                    if (logradouro) logradouro.value = data.logradouro || '';
                    if (bairro) bairro.value = data.bairro || '';
                    if (cidade) cidade.value = data.localidade || '';
                    if (estado) estado.value = data.uf || '';
                }
            } catch (err) {
                console.warn('Não foi possível buscar CEP automaticamente.');
            }
        }
    });

    // MANIPULAÇÃO DO MODAL DE ENDEREÇO
    if (btnAbrirEndereco) {
        btnAbrirEndereco.addEventListener('click', () => abrirModalEndereco(null));
    }

    document.getElementById('btn-fechar-modal-endereco')?.addEventListener('click', () => modalEndereco?.classList.remove('active'));
    document.getElementById('btn-cancelar-endereco')?.addEventListener('click', () => modalEndereco?.classList.remove('active'));

    function abrirModalEndereco(end = null) {
        const tituloModal = document.getElementById('modal-endereco-titulo');
        if (tituloModal) tituloModal.innerText = end ? 'Editar Endereço' : 'Cadastrar Endereço';

        const setVal = (id, val) => {
            const el = document.getElementById(id);
            if (el) el.value = val || '';
        };

        setVal('end-cep', end?.cep);
        setVal('end-logradouro', end?.logradouro);
        setVal('end-numero', end?.numero);
        setVal('end-complemento', end?.complemento);
        setVal('end-bairro', end?.bairro);
        setVal('end-cidade', end?.cidade);
        setVal('end-estado', end?.estado);

        modalEndereco?.classList.add('active');
    }

    // SALVAR ENDEREÇO
    document.getElementById('form-endereco')?.addEventListener('submit', async (e) => {
        e.preventDefault();

        const getVal = (id) => document.getElementById(id)?.value.trim() || '';

        const payload = {
            logradouro: getVal('end-logradouro'),
            numero: getVal('end-numero'),
            complemento: getVal('end-complemento') || null,
            bairro: getVal('end-bairro'),
            cidade: getVal('end-cidade'),
            estado: getVal('end-estado').toUpperCase(),
            cep: getVal('end-cep'),
            latitude: null,
            longitude: null
        };

        const isUpdate = Boolean(enderecoAtualId);
        const url = isUpdate ? `${API_BASE}/enderecos/${enderecoAtualId}` : `${API_BASE}/enderecos`;
        const method = isUpdate ? 'PUT' : 'POST';

        try {
            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(payload)
            });

            if (response.ok || response.status === 201) {
                alert(`Endereço ${isUpdate ? 'atualizado' : 'cadastrado'} com sucesso!`);
                modalEndereco?.classList.remove('active');
                carregarEndereco();
            } else {
                alert('Erro ao salvar endereço.');
            }
        } catch (err) {
            alert('Falha de conexão ao salvar endereço.');
        }
    });

    // DELETAR ENDEREÇO
    async function deletarEndereco(id) {
        if (!confirm('Deseja realmente remover este endereço?')) return;

        try {
            const response = await fetch(`${API_BASE}/enderecos/${id}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (response.status === 204 || response.ok) {
                alert('Endereço excluído com sucesso.');
                enderecoAtualId = null;
                carregarEndereco();
            } else {
                alert('Erro ao excluir o endereço.');
            }
        } catch (err) {
            alert('Erro de conexão ao tentar excluir.');
        }
    }

    // LOGOUT
    document.getElementById('btn-logout')?.addEventListener('click', fazerLogout);

    function fazerLogout() {
        localStorage.removeItem('token');
        window.location.href = 'auth.html';
    }

    // INICIALIZAÇÃO
    carregarPerfil();
    carregarEndereco();
});