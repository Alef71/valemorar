document.addEventListener('DOMContentLoaded', async () => {
    const container = document.getElementById('user-header-actions');
    const headerCityElem = document.getElementById('header-user-city');
    const API_BASE_URL = window.API_BASE_URL || (
        ['localhost', '127.0.0.1'].includes(window.location.hostname)
            ? 'http://localhost:8080'
            : window.location.origin
    );
    
    // Função para sanitizar HTML (Evita XSS)
    function escaparHTML(texto) {
        if (!texto) return '';
        return texto.toString().replace(/[&<>'"]/g, function(tag) {
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

    // 1. LEITURA SEGURA DO USUÁRIO NO LOCALSTORAGE
    let usuario = null;
    try {
        const userJson = localStorage.getItem('usuario');
        usuario = userJson && userJson !== 'undefined' ? JSON.parse(userJson) : null;
    } catch (e) {
        console.error('Erro ao ler dados do usuário no localStorage:', e);
    }

    // 2. RENDERIZA A BARRA COM BASE NO USUÁRIO LOGADO
    if (container) {
        if (usuario && (usuario.nome || usuario.email)) {
            const nomeExibicao = usuario.nome || usuario.email.split('@')[0];
            const inicial = nomeExibicao.charAt(0).toUpperCase();

            const nomeSeguro = escaparHTML(nomeExibicao);
            const inicialSegura = escaparHTML(inicial);

            container.innerHTML = `
                <a href="painel.html" class="bg-brand-green text-white px-3.5 py-2 rounded-xl text-xs font-bold flex items-center gap-1 hover:bg-brand-greenSoft transition">
                    <span class="material-symbols-outlined text-base">add_home</span> Anunciar
                </a>

                <a href="painel.html" class="flex items-center gap-2 pl-3 border-l border-[#dfd9cc] hover:opacity-80 transition">
                    <div class="w-8 h-8 rounded-full bg-brand-green text-white flex items-center justify-center font-bold text-xs shadow-xs">
                        ${inicialSegura}
                    </div>
                    <div class="text-left text-xs leading-tight hidden sm:block">
                        <span class="font-bold text-brand-textPrimary block">${nomeSeguro}</span>
                        <span class="text-brand-green font-semibold text-[10px]">Meu Painel</span>
                    </div>
                </a>

                <button id="btn-header-logout" title="Sair da Conta" class="text-stone-400 hover:text-red-600 p-1 transition flex items-center ml-1 cursor-pointer">
                    <span class="material-symbols-outlined text-lg">logout</span>
                </button>
            `;

            // Evento de Logout
            document.getElementById('btn-header-logout')?.addEventListener('click', () => {
                localStorage.removeItem('usuario');
                window.location.href = 'index.html';
            });
        } else {
            // USUÁRIO DESLOGADO
            container.innerHTML = `
                <a href="auth.html" class="text-brand-textSecondary hover:text-brand-green text-xs font-semibold px-3 py-2 transition">
                    Entrar
                </a>
                <a href="auth.html?modo=cadastro" class="bg-brand-gold text-white text-xs font-bold px-3.5 py-2 rounded-lg shadow-xs hover:bg-brand-earthLight transition">
                    Criar Conta
                </a>
            `;
        }
    }

    // 3. ATUALIZA A CIDADE NO CABEÇALHO (ENDEREÇO ESPECÍFICO DO USUÁRIO)
    function atualizarCidadeHeader(cidade, estado) {
        if (headerCityElem && cidade) {
            headerCityElem.innerText = `${cidade.toUpperCase()} • ${estado ? estado.toUpperCase() : 'MG'}`;
        }
    }

    if (usuario?.cidade) {
        atualizarCidadeHeader(usuario.cidade, usuario.estado);
    } else if (usuario && usuario.id) {
        try {
            // Rota ajustada para carregar apenas o endereço do próprio usuário logado
            const response = await fetch(`${API_BASE_URL}/api/enderecos/usuario/${usuario.id}`, {
                credentials: 'include'
            });
            if (response.ok) {
                const data = await response.json();
                const endereco = Array.isArray(data) ? data[0] : data;
                if (endereco && endereco.cidade) {
                    atualizarCidadeHeader(endereco.cidade, endereco.estado);
                    usuario.cidade = endereco.cidade;
                    usuario.estado = endereco.estado;
                    localStorage.setItem('usuario', JSON.stringify(usuario));
                }
            }
        } catch (err) {
            // Falha tratada para não quebrar a interface
        }
    }
});