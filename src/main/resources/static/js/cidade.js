document.addEventListener('DOMContentLoaded', () => {
    const cidades = [
        {
            nome: 'Diamantina',
            regiao: 'Alto Vale',
            imagem: '../imagem/diamantina.png',
            atracoes: [
                'Centro Histórico, Patrimônio Cultural da Humanidade pela UNESCO',
                'Passadiço da Casa da Glória',
                'Casa de Juscelino Kubitschek',
                'Igreja Nossa Senhora do Carmo',
                'Parque Estadual do Biribiri'
            ],
            comercio: [
                'Entorno do Mercado Velho (Mercado Municipal Tropeiro)',
                'Eixos comerciais da Rua Direita e da Rua da Quitanda'
            ],
            instituicoes: [
                { nome: 'UFVJM — Universidade Federal dos Vales do Jequitinhonha e Mucuri', url: 'https://www.ufvjm.edu.br/' },
                { nome: 'COPASA — Companhia de Saneamento de Minas Gerais', url: 'https://www.copasa.com.br/' },
                { nome: 'CEMIG — Companhia Energética de Minas Gerais', url: 'https://www.cemig.com.br/' }
            ],
            portal: { nome: 'Visite Diamantina — Portal Oficial de Turismo', url: 'https://www.visitediamantina.com.br/' }
        },
        {
            nome: 'Araçuaí',
            regiao: 'Médio Vale',
            imagem: '../imagem/aracuai.png',
            atracoes: [
                'Centro Cultural Araçuaí',
                'Mercado Municipal de Araçuaí',
                'Estação Ferroviária',
                'Casarões coloniais às margens do Rio Araçuaí'
            ],
            comercio: [
                'Corredor comercial da Avenida Dom Severino',
                'Centro financeiro e apoio logístico à mineração de lítio'
            ],
            instituicoes: [
                { nome: 'Sigma Lithium Corp', url: 'https://sigmalithium.com/' },
                { nome: 'IFNMG — Campus Araçuaí', url: 'https://www.ifnmg.edu.br/aracuai' }
            ],
            portal: { nome: 'Prefeitura Municipal de Araçuaí', url: 'https://www.aracuai.mg.gov.br/' }
        },
        {
            nome: 'Almenara',
            regiao: 'Baixo Vale',
            imagem: '../imagem/jequitionha.png',
            atracoes: [
                'Orla do Rio Jequitinhonha',
                'Igreja Matriz de São João Batista',
                'Acervo arquitetônico da Praça Doutor José de Aquino'
            ],
            comercio: [
                'Vias comerciais centrais, como a Avenida Olindo de Castro',
                'Feira livre regional',
                'Polo atacadista e centro médico-hospitalar'
            ],
            instituicoes: [
                { nome: 'IFNMG — Campus Almenara', url: 'https://www.ifnmg.edu.br/almenara' },
                { nome: 'Sicoob Crediminas', url: 'https://www.sicoob.com.br/web/sicoobcrediminas' }
            ],
            portal: { nome: 'Prefeitura de Almenara', url: 'https://www.almenara.mg.gov.br/' }
        },
        {
            nome: 'Capelinha',
            regiao: 'Alto Vale',
            imagem: '../imagem/capelinha.png',
            atracoes: [
                'Igreja Matriz de Nossa Senhora da Graça',
                'Praça do Povo',
                'Fazendas tradicionais da arquitetura cafeeira da serra mineira'
            ],
            comercio: [
                'Centro comercial e financeiro concentrado na Avenida Tancredo Neves',
                'Agronegócio, com destaque para café e silvicultura',
                'Expansão imobiliária moderna'
            ],
            instituicoes: [
                { nome: 'Aperam BioEnergia', url: 'https://brasil.aperam.com/' }
            ],
            portal: { nome: 'Prefeitura de Capelinha — Cultura e Turismo', url: 'https://pmcapelinha.mg.gov.br/' }
        },
        {
            nome: 'Pedra Azul',
            regiao: 'Médio Vale',
            imagem: '../imagem/pedra%20azul.png',
            atracoes: [
                'Entorno da Praça da Liberdade',
                'Igreja Matriz de Nossa Senhora da Conceição',
                'Paisagens naturais dos maciços rochosos da Pedra da Agulha'
            ],
            comercio: [
                'Comércio central na Rua Getúlio Vargas',
                'Setor agropecuário e prestação de serviços regional'
            ],
            instituicoes: [
                { nome: 'Sicoob Credineves', url: 'https://www.sicoob.com.br/' }
            ],
            portal: { nome: 'Prefeitura de Pedra Azul', url: 'https://www.pedraazul.mg.gov.br/' }
        },
        {
            nome: 'Minas Novas',
            regiao: 'Tradição & Barro',
            imagem: '../imagem/minas%20novas.png',
            atracoes: [
                'Sobradão de Minas Novas, referência da arquitetura colonial em taipa de pilão',
                'Igreja de São Francisco',
                'Artesanato em barro das mestras oleiras do Vale'
            ],
            comercio: [
                'Centro histórico comercial da Praça Prefeito Sebastião Caldeira',
                'Feira Livre Municipal de artesanato e hortifrúti'
            ],
            instituicoes: [
                { nome: 'IEPHA — Instituto Estadual do Patrimônio Histórico e Artístico de Minas Gerais', url: 'https://www.iepha.mg.gov.br/' }
            ],
            portal: { nome: 'Prefeitura de Minas Novas', url: 'https://www.minasnovas.mg.gov.br/' }
        }
    ];

    function normalizar(texto) {
        return String(texto || '')
            .normalize('NFD')
            .replace(/[\u0300-\u036f]/g, '')
            .toLocaleLowerCase('pt-BR')
            .trim();
    }

    function escaparHTML(valor) {
        return String(valor ?? '').replace(/[&<>'"]/g, caractere => ({
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            "'": '&#39;',
            '"': '&quot;'
        })[caractere]);
    }

    const nomeSolicitado = new URLSearchParams(window.location.search).get('nome');
    const cidade = cidades.find(item => normalizar(item.nome) === normalizar(nomeSolicitado));

    if (!cidade) {
        document.title = 'Cidade não encontrada | Vale Morar';
        document.querySelector('main').innerHTML = `
            <section class="mx-auto max-w-3xl px-4 py-20 text-center">
                <h1 class="text-3xl font-bold text-brand-textPrimary">Cidade não encontrada</h1>
                <p class="mt-3 text-brand-textSecondary">Volte para a página inicial e escolha uma das cidades disponíveis.</p>
                <a href="index.html#cidades" class="mt-6 inline-flex rounded-lg bg-brand-green px-5 py-3 text-sm font-bold text-white">Ver cidades</a>
            </section>
        `;
        return;
    }

    document.title = `${cidade.nome} | Vale Morar`;
    document.getElementById('cidade-nome').textContent = cidade.nome;
    document.getElementById('cidade-regiao').textContent = cidade.regiao;
    document.getElementById('cidade-imagem').style.backgroundImage = `url("${cidade.imagem}")`;
    document.getElementById('cidade-atracoes').innerHTML = cidade.atracoes
        .map(item => `<li class="flex items-start gap-2 rounded-xl bg-brand-cream/70 p-3 text-sm leading-relaxed"><span class="material-symbols-outlined mt-0.5 text-base text-brand-gold" aria-hidden="true">location_on</span><span>${escaparHTML(item)}</span></li>`)
        .join('');
    document.getElementById('cidade-comercio').innerHTML = cidade.comercio
        .map(item => `<li class="flex items-start gap-2 text-sm leading-relaxed text-brand-textSecondary"><span class="material-symbols-outlined mt-0.5 text-base text-brand-gold" aria-hidden="true">arrow_right</span><span>${escaparHTML(item)}</span></li>`)
        .join('');
    document.getElementById('cidade-instituicoes').innerHTML = cidade.instituicoes
        .map(item => `<li><a href="${escaparHTML(item.url)}" target="_blank" rel="noopener noreferrer" class="flex h-full items-center justify-between gap-3 rounded-xl border border-[#dfd7c8] p-4 text-sm font-semibold text-brand-green transition hover:border-brand-green hover:bg-brand-cream/60"><span>${escaparHTML(item.nome)}</span><span class="material-symbols-outlined shrink-0 text-base" aria-hidden="true">open_in_new</span></a></li>`)
        .join('');
    const portal = document.getElementById('cidade-portal');
    portal.href = cidade.portal.url;
    portal.innerHTML = `${escaparHTML(cidade.portal.nome)}<span class="material-symbols-outlined text-base" aria-hidden="true">open_in_new</span>`;
});
