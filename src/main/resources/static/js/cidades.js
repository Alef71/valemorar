// Municípios da Mesorregião do Jequitinhonha (IBGE), agrupados por microrregião.
// Fonte única para o filtro da vitrine e para o cadastro de anúncio, garantindo a mesma grafia no banco.
window.CIDADES_VALE = {
    'Almenara': [
        'Almenara', 'Bandeira', 'Divisópolis', 'Felisburgo', 'Jacinto', 'Jequitinhonha', 'Joaíma', 'Jordânia',
        'Mata Verde', 'Monte Formoso', 'Palmópolis', 'Rio do Prado', 'Rubim', 'Salto da Divisa',
        'Santa Maria do Salto', 'Santo Antônio do Jacinto'
    ],
    'Araçuaí': [
        'Araçuaí', 'Caraí', 'Coronel Murta', 'Itinga', 'Novo Cruzeiro', 'Padre Paraíso', 'Ponto dos Volantes',
        'Virgem da Lapa'
    ],
    'Capelinha': [
        'Angelândia', 'Aricanduva', 'Berilo', 'Capelinha', 'Carbonita', 'Chapada do Norte', 'Francisco Badaró',
        'Itamarandiba', 'Jenipapo de Minas', 'José Gonçalves de Minas', 'Leme do Prado', 'Minas Novas', 'Turmalina',
        'Veredinha'
    ],
    'Diamantina': [
        'Couto de Magalhães de Minas', 'Datas', 'Diamantina', 'Felício dos Santos', 'Gouveia',
        'Presidente Kubitschek', 'São Gonçalo do Rio Preto', 'Senador Modestino Gonçalves'
    ],
    'Pedra Azul': ['Cachoeira de Pajeú', 'Comercinho', 'Itaobim', 'Medina', 'Pedra Azul']
};

// Preenche um <select> com as cidades agrupadas por microrregião, mantendo as opções já existentes (ex.: "Todas")
window.preencherSelectCidades = function (select) {
    if (!select) return;
    Object.entries(window.CIDADES_VALE).forEach(([microrregiao, cidades]) => {
        const grupo = document.createElement('optgroup');
        grupo.label = `Microrregião de ${microrregiao}`;
        cidades.forEach(cidade => grupo.appendChild(new Option(cidade, cidade)));
        select.appendChild(grupo);
    });
};

// Seleciona um valor; se for uma cidade antiga fora da lista, adiciona a opção para não perder o dado
window.selecionarCidade = function (select, cidade) {
    if (!select) return;
    if (cidade && ![...select.options].some(o => o.value === cidade)) {
        select.appendChild(new Option(cidade, cidade));
    }
    select.value = cidade || '';
};
