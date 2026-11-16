document.addEventListener('DOMContentLoaded', () => {
    // BASE URL DINÂMICA
    const API_BASE_URL = window.API_BASE_URL || (
        ['localhost', '127.0.0.1'].includes(window.location.hostname)
            ? 'http://localhost:8080'
            : window.location.origin
    );

    // ENDPOINTS DE API
    const API_LOGIN_URL = `${API_BASE_URL}/api/auth/login`;
    const API_CADASTRO_URL = `${API_BASE_URL}/api/usuarios`;

    // ELEMENTOS
    const viewLogin = document.getElementById('view-login');
    const viewCadastro = document.getElementById('view-cadastro');
    const btnIrCadastro = document.getElementById('btn-ir-cadastro');
    const btnVoltarLogin = document.getElementById('btn-voltar-login');
    const formLogin = document.getElementById('form-login');
    const formCadastro = document.getElementById('form-cadastro');
    const feedbackDiv = document.getElementById('mensagem-feedback');
    const btnEntrar = document.getElementById('btn-entrar');
    const btnCadastrar = document.getElementById('btn-cadastrar');
    const cadFotoFile = document.getElementById('cad-foto-file');
    const cadFotoPreview = document.getElementById('cad-foto-preview');

    if (cadFotoFile) {
        cadFotoFile.addEventListener('change', () => {
            const file = cadFotoFile.files[0];
            if (!file) {
                cadFotoPreview?.classList.add('hidden');
                return;
            }
            if (!['image/jpeg', 'image/png', 'image/webp'].includes(file.type) ||
                file.size > 5 * 1024 * 1024) {
                alert('Envie uma imagem JPEG, PNG ou WebP de até 5 MB.');
                cadFotoFile.value = '';
                cadFotoPreview?.classList.add('hidden');
                return;
            }
            if (cadFotoPreview) {
                cadFotoPreview.src = URL.createObjectURL(file);
                cadFotoPreview.classList.remove('hidden');
            }
        });
    }

    // Suporte ao parâmetro ?modo=cadastro na URL (para vir do botão "Criar Conta" do cabeçalho)
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('modo') === 'cadastro') {
        toggleViews();
    }

    // TROCA DE TELAS (LOGIN / CADASTRO)
    if (btnIrCadastro) {
        btnIrCadastro.addEventListener('click', (e) => {
            e.preventDefault();
            esconderFeedback();
            toggleViews();
        });
    }

    if (btnVoltarLogin) {
        btnVoltarLogin.addEventListener('click', (e) => {
            e.preventDefault();
            esconderFeedback();
            toggleViews();
        });
    }

    function toggleViews() {
        if (viewLogin && viewCadastro) {
            viewLogin.classList.toggle('hidden');
            viewCadastro.classList.toggle('hidden');
        }
    }

    // 1. LÓGICA DE LOGIN
    if (formLogin) {
        formLogin.addEventListener('submit', async (e) => {
            e.preventDefault();
            esconderFeedback();

            if (btnEntrar) {
                btnEntrar.disabled = true;
                btnEntrar.innerText = 'Entrando...';
            }

            const email = document.getElementById('login-email')?.value.trim();
            const senha = document.getElementById('login-senha')?.value;

            const payload = { email, senha };

            try {
                const response = await fetch(API_LOGIN_URL, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    credentials: 'include', // Envia e recebe Cookies HttpOnly
                    body: JSON.stringify(payload)
                });

                if (response.ok) {
                    const data = await response.json().catch(() => ({}));

                    if (data.token) {
                        localStorage.setItem('token', data.token);
                    }

                    if (data.usuario || data.nome || data.id || data.usuarioId) {
                        const usuario = {
                            id: data.usuarioId || data.id || data.usuario?.id || null,
                            nome: data.nome || data.usuario?.nome || email.split('@')[0],
                            email: data.email || data.usuario?.email || email,
                            cidade: data.cidade || data.usuario?.cidade || null,
                            estado: data.estado || data.usuario?.estado || null
                        };
                        localStorage.setItem('usuario', JSON.stringify(usuario));
                    }

                    exibirFeedback('Login realizado com sucesso! Redirecionando...', 'sucesso');

                    // Redireciona diretamente para o painel de controle
                    setTimeout(() => {
                        window.location.href = 'painel.html';
                    }, 1200);

                } else {
                    const erroData = await response.json().catch(() => ({}));
                    const mensagemErro = erroData.message || erroData.erro || 'E-mail ou senha incorretos.';
                    exibirFeedback(mensagemErro, 'erro');
                }
            } catch (error) {
                console.error('Erro de conexão:', error);
                exibirFeedback('Falha de conexão com o servidor. Tente novamente mais tarde.', 'erro');
            } finally {
                if (btnEntrar) {
                    btnEntrar.disabled = false;
                    btnEntrar.innerText = 'Entrar';
                }
            }
        });
    }

    // 2. LÓGICA DE CADASTRO
    if (formCadastro) {
        formCadastro.addEventListener('submit', async (e) => {
            e.preventDefault();
            esconderFeedback();

            if (btnCadastrar) {
                btnCadastrar.disabled = true;
                btnCadastrar.innerText = 'Cadastrando...';
            }

            const nome = document.getElementById('cad-nome')?.value.trim();
            const email = document.getElementById('cad-email')?.value.trim();
            const senha = document.getElementById('cad-senha')?.value;
            const fotoFile = cadFotoFile?.files[0] || null;

            const payload = { nome, email, senha, fotoPerfil: null };

            try {
                const response = await fetch(API_CADASTRO_URL, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    credentials: 'include',
                    body: JSON.stringify(payload)
                });

                if (response.status === 201 || response.ok) {
                    // Login automático imediato para registrar a sessão/cookie
                    const resLogin = await fetch(API_LOGIN_URL, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        credentials: 'include',
                        body: JSON.stringify({ email, senha })
                    });

                    if (resLogin.ok) {
                        const data = await resLogin.json().catch(() => ({}));

                        if (data.token) {
                            localStorage.setItem('token', data.token);
                        }

                        const usuario = {
                            id: data.usuarioId || data.id || data.usuario?.id || null,
                            nome: data.nome || data.usuario?.nome || nome || email.split('@')[0],
                            email: data.email || data.usuario?.email || email,
                            cidade: data.cidade || data.usuario?.cidade || null,
                            estado: data.estado || data.usuario?.estado || null
                        };
                        localStorage.setItem('usuario', JSON.stringify(usuario));

                        let erroUploadFoto = null;
                        if (fotoFile) {
                            if (!data.token) {
                                erroUploadFoto = 'Não foi possível iniciar a sessão para salvar a foto.';
                            } else {
                                try {
                                    const formData = new FormData();
                                    formData.append('file', fotoFile);
                                    const fotoResponse = await fetch(`${API_BASE_URL}/api/usuarios/me/foto`, {
                                        method: 'PUT',
                                        headers: { 'Authorization': `Bearer ${data.token}` },
                                        body: formData
                                    });
                                    if (fotoResponse.ok) {
                                        const usuarioComFoto = await fotoResponse.json();
                                        Object.assign(usuario, usuarioComFoto);
                                        localStorage.setItem('usuario', JSON.stringify(usuario));
                                    } else {
                                        const erro = await fotoResponse.json().catch(() => ({}));
                                        erroUploadFoto = erro.erro || erro.message || 'Não foi possível salvar a foto de perfil.';
                                    }
                                } catch (error) {
                                    console.error('Erro ao enviar a foto de perfil:', error);
                                    erroUploadFoto = 'Não foi possível enviar a foto. Você poderá adicioná-la pelo painel.';
                                }
                            }
                        }

                        exibirFeedback(
                            erroUploadFoto
                                ? `Conta criada, mas a foto não foi salva: ${erroUploadFoto}`
                                : 'Conta criada com sucesso! Redirecionando para o painel...',
                            erroUploadFoto ? 'erro' : 'sucesso'
                        );

                        setTimeout(() => {
                            window.location.href = 'painel.html';
                        }, erroUploadFoto ? 3000 : 1200);
                    } else {
                        exibirFeedback(
                            fotoFile
                                ? 'Cadastro realizado! Entre e adicione sua foto pelo painel.'
                                : 'Cadastro realizado! Por favor, faça o login manual.',
                            'sucesso'
                        );
                        formCadastro.reset();
                        setTimeout(() => {
                            toggleViews();
                        }, 1500);
                    }

                } else {
                    const erroData = await response.json().catch(() => ({}));
                    const mensagemErro = erroData.message || erroData.erro || 'Erro ao realizar cadastro. Verifique os dados.';
                    exibirFeedback(mensagemErro, 'erro');
                }
            } catch (error) {
                console.error('Erro de conexão:', error);
                exibirFeedback('Falha de conexão com o servidor. Tente novamente mais tarde.', 'erro');
            } finally {
                if (btnCadastrar) {
                    btnCadastrar.disabled = false;
                    btnCadastrar.innerText = 'Criar Conta';
                }
            }
        });
    }

    // HELPER DE MENSAGENS (FEEDBACK)
    function exibirFeedback(mensagem, tipo) {
        if (!feedbackDiv) return;
        feedbackDiv.innerText = mensagem;
        feedbackDiv.className = 'mensagem-feedback mb-4 text-center p-3 rounded-lg text-sm font-semibold block';

        if (tipo === 'sucesso') {
            feedbackDiv.classList.add('bg-green-100', 'text-green-800', 'border', 'border-green-300');
        } else {
            feedbackDiv.classList.add('bg-red-100', 'text-red-800', 'border', 'border-red-300');
        }
    }

    function esconderFeedback() {
        if (!feedbackDiv) return;
        feedbackDiv.className = 'mensagem-feedback mb-4 text-center hidden';
        feedbackDiv.innerText = '';
    }
});