document.addEventListener('DOMContentLoaded', () => {
    const formLogin = document.getElementById('form-login');
    const feedbackDiv = document.getElementById('mensagem-feedback');
    const btnEntrar = document.getElementById('btn-entrar');

    const API_URL = 'http://localhost:8080/api/auth/login';

    formLogin.addEventListener('submit', async (event) => {
        event.preventDefault();

        esconderFeedback();
        btnEntrar.disabled = true;
        btnEntrar.innerText = 'Entrando...';

        const email = document.getElementById('email').value.trim();
        const senha = document.getElementById('senha').value;

        const payload = {
            email: email,
            senha: senha
        };

        try {
            const response = await fetch(API_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            });

            if (response.ok) {
                const data = await response.json();
                
                // 1. Salva o Token JWT
                if (data.token) {
                    localStorage.setItem('token', data.token);
                }

                // 2. Salva o objeto do Usuário para o header.js e painel.html consumirem
                const usuario = {
                    id: data.id || data.usuario?.id || null,
                    nome: data.nome || data.usuario?.nome || email.split('@')[0],
                    email: data.email || email
                };
                localStorage.setItem('usuario', JSON.stringify(usuario));

                exibirFeedback('Login realizado com sucesso! Redirecionando...', 'sucesso');
                
                // 3. Redireciona para o painel
                setTimeout(() => {
                    window.location.href = 'painel.html';
                }, 1500);

            } else {
                const erroData = await response.json().catch(() => ({}));
                const mensagemErro = erroData.message || erroData.erro || 'E-mail ou senha incorretos.';
                exibirFeedback(mensagemErro, 'erro');
            }

        } catch (error) {
            exibirFeedback('Falha de conexão com o servidor. Tente novamente mais tarde.', 'erro');
        } finally {
            btnEntrar.disabled = false;
            btnEntrar.innerText = 'Entrar';
        }
    });

    function exibirFeedback(mensagem, tipo) {
        feedbackDiv.innerText = mensagem;
        feedbackDiv.className = `mensagem-feedback ${tipo}`;
        feedbackDiv.style.display = 'block';
    }

    function esconderFeedback() {
        feedbackDiv.style.display = 'none';
        feedbackDiv.innerText = '';
        feedbackDiv.className = 'mensagem-feedback';
    }
});