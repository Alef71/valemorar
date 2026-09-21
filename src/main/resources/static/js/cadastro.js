document.addEventListener('DOMContentLoaded', () => {
    const formCadastro = document.getElementById('form-cadastro');
    const feedbackDiv = document.getElementById('mensagem-feedback');
    const btnCadastrar = document.getElementById('btn-cadastrar');

    const API_URL = 'http://localhost:8080/api/usuarios';

    formCadastro.addEventListener('submit', async (event) => {
        event.preventDefault();

        esconderFeedback();
        btnCadastrar.disabled = true;
        btnCadastrar.innerText = 'Cadastrando...';

        const nome = document.getElementById('nome').value.trim();
        const email = document.getElementById('email').value.trim();
        const senha = document.getElementById('senha').value;
        const fotoPerfil = document.getElementById('fotoPerfil').value.trim() || null;

        const payload = {
            nome: nome,
            email: email,
            senha: senha,
            fotoPerfil: fotoPerfil
        };

        try {
            const response = await fetch(API_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            });

            if (response.status === 201) {
                exibirFeedback('Cadastro realizado com sucesso! Redirecionando para o login...', 'sucesso');
                formCadastro.reset();

                setTimeout(() => {
                    window.location.href = 'auth.html';
                }, 2000);

            } else {
                const erroData = await response.json().catch(() => ({}));
                const mensagemErro = erroData.message || erroData.erro || 'Erro ao realizar cadastro. Verifique os dados.';
                exibirFeedback(mensagemErro, 'erro');
            }

        } catch (error) {
            exibirFeedback('Falha de conexão com o servidor. Tente novamente mais tarde.', 'erro');
        } finally {
            btnCadastrar.disabled = false;
            btnCadastrar.innerText = 'Criar Conta';
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