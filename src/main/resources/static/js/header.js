document.addEventListener('DOMContentLoaded', async () => {
    const container = document.getElementById('user-header-actions');
    const headerCityElem = document.getElementById('header-user-city');
    // Função para sanitizar HTML (Evita XSS)
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

    // 1. LEITURA SEGURA DO USUÁRIO NO LOCALSTORAGE
    let usuario = null;
    try {
        const userJson = localStorage.getItem('usuario');
        usuario = userJson && userJson !== 'undefined' ? JSON.parse(userJson) : null;
    } catch (e) {
        console.error('Erro ao ler dados do usuário no localStorage:', e);
    }

    // 2. RENDERIZA OS BOTÕES E ÁREA DO USUÁRIO NO HEADER
    if (container) {
        if (usuario && (usuario.nome || usuario.email)) {
            const nomeExibicao = usuario.nome || usuario.email.split('@')[0];
            const inicial = nomeExibicao.charAt(0).toUpperCase();

            const nomeSeguro = escaparHTML(nomeExibicao);
            const inicialSegura = escaparHTML(inicial);

            container.innerHTML = `
                <a href="painel.html" class="bg-brand-green hover:bg-opacity-90 text-white px-4 py-2 rounded-lg text-xs font-bold flex items-center gap-1.5 transition shadow-xs">
                    <span class="material-symbols-outlined text-base">home_work</span> Anunciar Imóvel
                </a>

                <a href="painel.html" class="flex items-center gap-2 pl-3 border-l border-[#dfd9cc] hover:opacity-80 transition">
                    <div class="w-8 h-8 rounded-full bg-brand-green text-white flex items-center justify-center font-bold text-xs shadow-xs">
                        ${inicialSegura}
                    </div>
                    <div class="text-left text-xs leading-tight hidden sm:block">
                        <span class="font-bold text-stone-800 block">${nomeSeguro}</span>
                        <span class="text-brand-green font-semibold text-[10px]">Meu Painel</span>
                    </div>
                </a>

                <button id="btn-header-logout" title="Sair da Conta" class="text-stone-400 hover:text-red-600 p-1 transition flex items-center ml-1 cursor-pointer">
                    <span class="material-symbols-outlined text-lg">logout</span>
                </button>
            `;

            document.getElementById('btn-header-logout')?.addEventListener('click', () => {
                localStorage.removeItem('usuario');
                localStorage.removeItem('token');
                window.location.href = 'index.html';
            });
        } else {
            // Visitante Deslogado
            container.innerHTML = `
                <a href="auth.html" class="bg-brand-green hover:bg-opacity-90 text-white px-4 py-2 rounded-lg text-xs font-bold flex items-center gap-1.5 transition shadow-xs">
                    <span class="material-symbols-outlined text-base">home_work</span> Anunciar Imóvel
                </a>
                <a href="auth.html" class="text-stone-600 hover:text-brand-green text-xs font-semibold px-2 py-1.5 transition">
                    Entrar
                </a>
                <a href="auth.html?modo=cadastro" class="bg-brand-gold hover:bg-opacity-90 text-white text-xs font-bold px-3.5 py-2 rounded-lg transition">
                    Criar Conta
                </a>
            `;
        }
    }

    // 3. RENDERIZA O BLOCO DA CIDADE NO HEADER
    function atualizarCidadeHeader(cidade, estado) {
        if (!headerCityElem) return;

        const cidadeFormatada = cidade ? cidade.toUpperCase() : '';
        const estadoFormatado = estado ? estado.toUpperCase() : '';

        headerCityElem.innerHTML = `
            <span class="text-[11px] font-bold text-brand-green tracking-wider uppercase leading-tight block">
                ${escaparHTML(cidadeFormatada)} • ${escaparHTML(estadoFormatado)}
            </span>
            <span class="text-[10px] text-stone-500 font-medium normal-case leading-tight block">
                Aluguel & Moradia
            </span>
        `;
    }

    // 4. CARREGA A CIDADE A PARTIR DO ENDEREÇO DO USUÁRIO
    async function carregarCidadeEndereco() {
        if (!usuario || !usuario.id) {
            atualizarCidadeHeader('JEQUITINHONHA', 'MG');
            return;
        }

        // A) Tenta ler do cache de endereço gerado pelo Painel
        const cacheEndereco = localStorage.getItem(`enderecoUsuario_${usuario.id}`);
        if (cacheEndereco) {
            try {
                const end = JSON.parse(cacheEndereco);
                if (end && end.cidade) {
                    atualizarCidadeHeader(end.cidade, end.estado);
                    return;
                }
            } catch (e) {
                console.warn('Erro ao ler cache de endereço no header:', e);
            }
        }

        // B) Tenta do perfil salvo no localStorage
        if (usuario.cidade) {
            atualizarCidadeHeader(usuario.cidade, usuario.estado);
            return;
        }

        atualizarCidadeHeader('VALE DO JEQUITINHONHA', 'MG');
    }

    carregarCidadeEndereco();
});