document.addEventListener('DOMContentLoaded', () => {
    const navLinks = document.getElementById('nav-links');
    const token = localStorage.getItem('token');

    if (token) {
        // USUÁRIO LOGADO: Exibe "Meu Painel" e botão "Sair"
        navLinks.innerHTML = `
            <a href="home.html">Home</a>
            <a href="painel.html" class="active">Meu Painel</a>
            <button id="btn-header-logout" class="btn-logout-header">Sair</button>
        `;

        // Evento para realizar Logout
        document.getElementById('btn-header-logout').addEventListener('click', () => {
            localStorage.removeItem('token');
            window.location.href = 'home.html';
        });
    } else {
        // USUÁRIO DESLOGADO: Exibe "Entrar / Cadastrar"
        navLinks.innerHTML = `
            <a href="home.html">Home</a>
            <a href="auth.html">Entrar / Cadastrar</a>
        `;
    }
});