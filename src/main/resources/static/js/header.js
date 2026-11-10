document.addEventListener('DOMContentLoaded', async () => {
    const container = document.getElementById('user-header-actions');
    const headerCityElem = document.getElementById('header-user-city');
    const API_BASE_URL = window.API_BASE_URL || (
        ['localhost', '127.0.0.1'].includes(window.location.hostname)
            ? 'http://localhost:8080'
            : window.location.origin
    );
    const token = localStorage.getItem('token');

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

        // C) Busca na API via endpoint do usuário autenticado
        if (token) {
            try {
                const response = await fetch(`${API_BASE_URL}/api/enderecos/usuario/${usuario.id}`, {
                    headers: { 'Authorization': `Bearer ${token}` }
                });
                if (response.ok) {
                    const data = await response.json();
                    const endereco = Array.isArray(data) ? data[0] : data;
                    if (endereco && endereco.cidade) {
                        atualizarCidadeHeader(endereco.cidade, endereco.estado);
                        localStorage.setItem(`enderecoUsuario_${usuario.id}`, JSON.stringify(endereco));
                        return;
                    }
                }
            } catch (err) {
                console.warn('Erro ao buscar endereço para o header:', err);
            }
        }

        atualizarCidadeHeader('JEQUITINHONHA', 'MG');
    }

    // 5. ROLAGEM SUAVE AO CLICAR EM LINKS DO MENU SUPERIOR DENTRO DA INDEX
    function configurarScrollMenuNav() {
        const isIndexPage = window.location.pathname.endsWith('index.html') 
            || window.location.pathname === '/' 
            || window.location.pathname === '';

        const navLinks = document.querySelectorAll('header nav a');

        navLinks.forEach(link => {
            link.addEventListener('click', (e) => {
                const href = link.getAttribute('href') || '';

                if (isIndexPage && !href.startsWith('http') && !href.includes('painel.html') && !href.includes('auth.html')) {
                    e.preventDefault();

                    // Elemento alvo da busca (Card de Filtros)
                    const alvoBusca = document.getElementById('form-busca-vitrine') 
                        || document.querySelector('.mt-8.bg-white') 
                        || document.querySelector('main');

                    if (alvoBusca) {
                        alvoBusca.scrollIntoView({ behavior: 'smooth', block: 'center' });
                    } else {
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                    }

                    // Aciona a aba ou filtro correspondente na index
                    if (href.includes('modalidade=comercial')) {
                        const btnComercial = document.querySelector('.btn-aba-tipo[data-tipo="COMERCIAL"]');
                        if (btnComercial) btnComercial.click();
                    } else if (href.includes('modalidade=sitios')) {
                        const btnSitio = document.querySelector('.btn-aba-tipo[data-tipo="SITIO"]');
                        if (btnSitio) btnSitio.click();
                    } else if (href.includes('modalidade=residencial')) {
                        const btnTodos = document.querySelector('.btn-aba-tipo[data-tipo=""]');
                        if (btnTodos) btnTodos.click();
                    } else if (href.includes('cidades') || href.includes('cidade')) {
                        const selectCidade = document.getElementById('filtro-cidade');
                        if (selectCidade) selectCidade.focus();
                    }
                }
            });
        });
    }

    carregarCidadeEndereco();
    configurarScrollMenuNav();
});