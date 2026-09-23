document.addEventListener('DOMContentLoaded', async () => {
    const container = document.getElementById('user-header-actions');
    const headerCityElem = document.getElementById('header-user-city');
    const token = localStorage.getItem('token');
    const userJson = localStorage.getItem('usuario');
    const usuario = userJson ? JSON.parse(userJson) : null;

    // Função para renderizar a cidade no cabeçalho
    function atualizarCidadeHeader(cidade, estado) {
        if (headerCityElem && cidade) {
            headerCityElem.innerText = `${cidade.toUpperCase()} • ${estado ? estado.toUpperCase() : 'MG'}`;
        }
    }

    // Tenta obter a cidade do objeto do usuário ou busca via API de endereço
    if (usuario?.cidade) {
        atualizarCidadeHeader(usuario.cidade, usuario.estado);
    } else if (token) {
        try {
            const response = await fetch('http://localhost:8080/api/enderecos', {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) {
                const data = await response.json();
                const lista = data.content || data;
                if (Array.isArray(lista) && lista.length > 0) {
                    atualizarCidadeHeader(lista[0].cidade, lista[0].estado);
                }
            }
        } catch (err) {
            console.warn('Erro ao carregar endereço no cabeçalho:', err);
        }
    }

    if (!container) return;

    if (token) {
        // USUÁRIO LOGADO: Exibe nome do usuário, link do Painel e botão Sair
        const nomeExibicao = usuario?.nome || 'Minha Conta';
        const inicial = nomeExibicao.charAt(0).toUpperCase();

        container.innerHTML = `
            <a href="anunciar.html" class="bg-brand-green text-white px-3.5 py-2 rounded-xl text-xs font-bold flex items-center gap-1 hover:bg-brand-greenDark transition">
                <span class="material-symbols-outlined text-base">add_home</span> Anunciar
            </a>

            <a href="painel.html" class="flex items-center gap-2 pl-3 border-l border-[#dfd9cc] hover:opacity-80 transition">
                <div class="w-8 h-8 rounded-full bg-brand-green text-white flex items-center justify-center font-bold text-xs shadow-xs">
                    ${inicial}
                </div>
                <div class="text-left text-xs leading-tight hidden sm:block">
                    <span class="font-bold text-brand-textPrimary block">${nomeExibicao}</span>
                    <span class="text-brand-green font-semibold text-[10px]">Meu Painel</span>
                </div>
            </a>

            <button id="btn-header-logout" title="Sair da Conta" class="text-stone-400 hover:text-red-600 p-1 transition flex items-center ml-1">
                <span class="material-symbols-outlined text-lg">logout</span>
            </button>
        `;

        // Evento de Logout
        document.getElementById('btn-header-logout')?.addEventListener('click', () => {
            localStorage.removeItem('token');
            localStorage.removeItem('usuario');
            window.location.href = 'home.html';
        });

    } else {
        // USUÁRIO DESLOGADO: Exibe Entrar e Criar Conta
        container.innerHTML = `
            <a href="auth.html" class="text-brand-textSecondary hover:text-brand-green text-xs font-semibold px-3 py-2 transition">
                Entrar
            </a>
            <a href="cadastro.html" class="bg-brand-orange text-white text-xs font-bold px-3.5 py-2 rounded-lg shadow-xs hover:bg-brand-orangeDark transition">
                Criar Conta
            </a>
        `;
    }
});