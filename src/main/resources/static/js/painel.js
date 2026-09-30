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

    // Elementos DOM
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
     * Reduz uma imagem de vários Megabytes para poucos Kilobytes em Base64
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

                    // Retorna a imagem comprimida em formato JPEG
                    const base64Comprimido = canvas.toDataURL('image/jpeg', qualidade);
                    resolve(base64Comprimido);
                };
                img.onerror = (err) => reject(err);
            };
            reader.onerror = (err) => reject(err);
        });
    }

    // Evento acionado ao escolher um arquivo de imagem do PC
    inputFotoFile.addEventListener('change', async (e) => {
        const file = e.target.files[0];
        if (file) {
            try {
                // Redimensiona para no máximo 300x300px com qualidade 80%
                fotoBase64EmMemoria = await comprimirImagem(file, 300, 300, 0.8);
                previewFoto.src = fotoBase64EmMemoria;
                previewFoto.style.display = 'block';
            } catch (err) {
                alert('Erro ao processar e comprimir a imagem selecionada.');
            }
        }
    });

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
        userNameElem.innerText = usuarioAtual.nome || 'Usuário';
        welcomeNameElem.innerText = usuarioAtual.nome ? usuarioAtual.nome.split(' ')[0] : 'Usuário';
        userEmailElem.innerText = usuarioAtual.email || '';

        if (usuarioAtual.fotoPerfil) {
            userAvatarElem.src = usuarioAtual.fotoPerfil;
        } else {
            userAvatarElem.src = `https://ui-avatars.com/api/?name=${encodeURIComponent(usuarioAtual.nome)}&background=1a252f&color=fff`;
        }
    }

    // 2. ATUALIZAR PERFIL (NOME E FOTO)
    btnAbrirPerfil.addEventListener('click', () => {
        document.getElementById('edit-nome').value = usuarioAtual?.nome || '';
        inputFotoFile.value = '';
        fotoBase64EmMemoria = null;

        if (usuarioAtual?.fotoPerfil) {
            previewFoto.src = usuarioAtual.fotoPerfil;
            previewFoto.style.display = 'block';
        } else {
            previewFoto.style.display = 'none';
        }

        modalPerfil.classList.add('active');
    });

    document.getElementById('btn-fechar-modal-perfil').onclick = () => modalPerfil.classList.remove('active');
    document.getElementById('btn-cancelar-perfil').onclick = () => modalPerfil.classList.remove('active');

    document.getElementById('form-editar-perfil').addEventListener('submit', async (e) => {
        e.preventDefault();
        const nome = document.getElementById('edit-nome').value.trim();
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
                modalPerfil.classList.remove('active');
                carregarPerfil();
            } else {
                const erroBody = await response.json().catch(() => ({}));
                const mensagem = erroBody.message || erroBody.erro || 'Erro ao atualizar perfil (verifique os dados).';
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
                } else {
                    enderecoContainer.innerHTML = '<p class="empty-message">Nenhum endereço cadastrado no momento.</p>';
                }
            }
        } catch (error) {
            console.error('Erro ao carregar endereço:', error);
        }
    }

    function renderizarEnderecoHTML(end) {
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

        document.getElementById('btn-editar-end-card').onclick = () => abrirModalEndereco(end);
        document.getElementById('btn-deletar-end-card').onclick = () => deletarEndereco(end.id);
    }

    // AUTOPREENCHIMENTO DE CEP (ViaCEP)
    document.getElementById('end-cep').addEventListener('blur', async (e) => {
        const cep = e.target.value.replace(/\D/g, '');
        if (cep.length === 8) {
            try {
                const res = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
                const data = await res.json();
                if (!data.erro) {
                    document.getElementById('end-logradouro').value = data.logradouro || '';
                    document.getElementById('end-bairro').value = data.bairro || '';
                    document.getElementById('end-cidade').value = data.localidade || '';
                    document.getElementById('end-estado').value = data.uf || '';
                }
            } catch (err) {
                console.warn('Não foi possível buscar CEP automaticamente.');
            }
        }
    });

    // MANIPULAÇÃO DO MODAL DE ENDEREÇO
    btnAbrirEndereco.addEventListener('click', () => abrirModalEndereco(null));
    document.getElementById('btn-fechar-modal-endereco').onclick = () => modalEndereco.classList.remove('active');
    document.getElementById('btn-cancelar-endereco').onclick = () => modalEndereco.classList.remove('active');

    function abrirModalEndereco(end = null) {
        document.getElementById('modal-endereco-titulo').innerText = end ? 'Editar Endereço' : 'Cadastrar Endereço';
        document.getElementById('end-cep').value = end?.cep || '';
        document.getElementById('end-logradouro').value = end?.logradouro || '';
        document.getElementById('end-numero').value = end?.numero || '';
        document.getElementById('end-complemento').value = end?.complemento || '';
        document.getElementById('end-bairro').value = end?.bairro || '';
        document.getElementById('end-cidade').value = end?.cidade || '';
        document.getElementById('end-estado').value = end?.estado || '';

        modalEndereco.classList.add('active');
    }

    // SALVAR ENDEREÇO
    document.getElementById('form-endereco').addEventListener('submit', async (e) => {
        e.preventDefault();

        const payload = {
            logradouro: document.getElementById('end-logradouro').value.trim(),
            numero: document.getElementById('end-numero').value.trim(),
            complemento: document.getElementById('end-complemento').value.trim() || null,
            bairro: document.getElementById('end-bairro').value.trim(),
            cidade: document.getElementById('end-cidade').value.trim(),
            estado: document.getElementById('end-estado').value.trim().toUpperCase(),
            cep: document.getElementById('end-cep').value.trim(),
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
                modalEndereco.classList.remove('active');
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
    document.getElementById('btn-logout').addEventListener('click', fazerLogout);

    function fazerLogout() {
        localStorage.removeItem('token');
        window.location.href = 'auth.html';
    }

    // INICIALIZAÇÃO
    carregarPerfil();
    carregarEndereco();
});