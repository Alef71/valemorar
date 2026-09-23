document.addEventListener('DOMContentLoaded', async () => {
    const token = localStorage.getItem('token');
    if (!token) return;

    const API_BASE = 'http://localhost:8080/api';
    let usuarioLogado = null;
    let anunciosEmMemoria = [];

    // Elementos DOM
    const btnNovoAnuncio = document.getElementById('btn-novo-anuncio');
    const modalAnuncio = document.getElementById('modal-anuncio');
    const btnFecharModal = document.getElementById('btn-fechar-modal-anuncio');
    const btnCancelarModal = document.getElementById('btn-cancelar-anuncio');
    const formAnuncio = document.getElementById('form-anuncio');
    const anunciosContainer = document.getElementById('anuncios-info-container');
    const totalAnunciosElem = document.getElementById('total-anuncios');

    // 1. CARREGAR IDENTIDADE DO USUÁRIO
    async function carregarUsuario() {
        try {
            const res = await fetch(`${API_BASE}/usuarios/me`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (res.ok) {
                usuarioLogado = await res.json();
                carregarMeusAnuncios();
            }
        } catch (err) {
            console.error('Erro ao obter usuário logado:', err);
        }
    }

    // 2. BUSCAR ANÚNCIOS DO ANUNCIANTE
    async function carregarMeusAnuncios() {
        if (!usuarioLogado || !usuarioLogado.id) return;

        try {
            const response = await fetch(`${API_BASE}/anuncios/anunciante/${usuarioLogado.id}`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (response.ok) {
                const data = await response.json();
                anunciosEmMemoria = data.content || data || [];
                renderizarAnuncios(anunciosEmMemoria);
            } else {
                anunciosContainer.innerHTML = '<p class="empty-message">Erro ao carregar seus anúncios.</p>';
            }
        } catch (error) {
            console.error('Erro ao buscar anúncios:', error);
            anunciosContainer.innerHTML = '<p class="empty-message">Erro de conexão ao carregar anúncios.</p>';
        }
    }

    // 3. RENDERIZAR LISTA DE ANÚNCIOS NO HTML
    function renderizarAnuncios(lista) {
        if (totalAnunciosElem) {
            totalAnunciosElem.innerText = lista.length;
        }

        if (lista.length === 0) {
            anunciosContainer.innerHTML = '<p class="empty-message">Nenhum anúncio cadastrado no momento.</p>';
            return;
        }

        anunciosContainer.innerHTML = lista.map(anuncio => {
            const tagsFormatted = anuncio.tags && anuncio.tags.length > 0 
                ? anuncio.tags.map(t => `<span class="badge-tag" style="background:#e0e0e0; padding:2px 6px; border-radius:4px; font-size:0.8em; margin-right:4px;">${t}</span>`).join('') 
                : '<em>Nenhuma tag</em>';

            const statusClass = anuncio.status ? anuncio.status.toLowerCase() : 'ativo';

            return `
                <div class="anuncio-card" style="border: 1px solid #ddd; padding: 15px; border-radius: 8px; margin-bottom: 15px; background: #fff;">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <h4 style="margin:0;">${anuncio.cidade} - ${anuncio.tipoImovel || 'RESIDENCIAL'} (${anuncio.modalidade})</h4>
                        <span class="status-badge status-${statusClass}" style="padding: 4px 8px; border-radius: 4px; font-weight: bold; background: #eee; font-size:0.85em;">
                            ${anuncio.status || 'ATIVO'}
                        </span>
                    </div>
                    <p style="margin: 8px 0;">
                        <strong>Valor:</strong> R$ ${parseFloat(anuncio.valor).toFixed(2)} | 
                        <strong>Condomínio:</strong> R$ ${parseFloat(anuncio.valorCondominio || 0).toFixed(2)} | 
                        <strong>IPTU:</strong> R$ ${parseFloat(anuncio.valorIptu || 0).toFixed(2)}
                    </p>
                    <p style="margin: 8px 0;"><strong>Quartos:</strong> ${anuncio.quartos || 0} | <strong>Tags:</strong> ${tagsFormatted}</p>
                    <p style="margin: 8px 0; font-size: 0.85em; color: #666;"><strong>Expira em:</strong> ${anuncio.expiraEm ? new Date(anuncio.expiraEm).toLocaleDateString('pt-BR') : 'Não definida'}</p>
                    
                    <div class="anuncio-actions" style="margin-top: 10px; display: flex; gap: 8px; flex-wrap: wrap;">
                        <button class="btn-secondary btn-editar-anuncio" data-id="${anuncio.id}">Editar</button>
                        <button class="btn-secondary btn-status-anuncio" data-id="${anuncio.id}" data-status="${anuncio.status}">Alterar Status</button>
                        <button class="btn-secondary btn-renovar-anuncio" data-id="${anuncio.id}">Renovar (90d)</button>
                        <button class="btn-danger btn-deletar-anuncio" data-id="${anuncio.id}">Excluir</button>
                    </div>
                </div>
            `;
        }).join('');

        vincularEventosCards();
    }

    // 4. ATRIBUIR EVENTOS AOS BOTÕES DOS CARDS
    function vincularEventosCards() {
        document.querySelectorAll('.btn-editar-anuncio').forEach(btn => {
            btn.onclick = () => {
                const id = btn.getAttribute('data-id');
                const item = anunciosEmMemoria.find(a => a.id === id);
                if (item) abrirModalAnuncio(item);
            };
        });

        document.querySelectorAll('.btn-status-anuncio').forEach(btn => {
            btn.onclick = () => {
                const id = btn.getAttribute('data-id');
                const statusAtual = btn.getAttribute('data-status');
                const novoStatus = prompt('Digite o novo status (ATIVO, ALUGADO, INDISPONIVEL):', statusAtual || 'ALUGADO');
                if (novoStatus) alterarStatus(id, novoStatus.toUpperCase());
            };
        });

        document.querySelectorAll('.btn-renovar-anuncio').forEach(btn => {
            btn.onclick = () => {
                const id = btn.getAttribute('data-id');
                if (confirm('Deseja renovar este anúncio por mais 90 dias?')) renovarAnuncio(id);
            };
        });

        document.querySelectorAll('.btn-deletar-anuncio').forEach(btn => {
            btn.onclick = () => {
                const id = btn.getAttribute('data-id');
                if (confirm('Tem certeza que deseja excluir este anúncio?')) deletarAnuncio(id);
            };
        });
    }

    // 5. GERENCIAMENTO DO MODAL
    if (btnNovoAnuncio) {
        btnNovoAnuncio.addEventListener('click', () => abrirModalAnuncio(null));
    }

    if (btnFecharModal) btnFecharModal.onclick = () => modalAnuncio.classList.remove('active');
    if (btnCancelarModal) btnCancelarModal.onclick = () => modalAnuncio.classList.remove('active');

    function abrirModalAnuncio(anuncio = null) {
        document.getElementById('modal-anuncio-titulo').innerText = anuncio ? 'Editar Anúncio' : 'Criar Anúncio';
        document.getElementById('anuncio-id').value = anuncio?.id || '';

        // Mantém ou gera UUID para imovelId
        document.getElementById('anuncio-imovel-id').value = anuncio?.imovelId || crypto.randomUUID();

        // Dados do Anúncio
        document.getElementById('anuncio-cidade').value = anuncio?.cidade || '';
        document.getElementById('anuncio-modalidade').value = anuncio?.modalidade || 'ALUGUEL';
        document.getElementById('anuncio-tipo-imovel').value = anuncio?.tipoImovel || 'RESIDENCIAL';
        document.getElementById('anuncio-quartos').value = anuncio?.quartos ?? 1;
        document.getElementById('anuncio-valor').value = anuncio?.valor || '';
        document.getElementById('anuncio-condominio').value = anuncio?.valorCondominio ?? 0;
        document.getElementById('anuncio-iptu').value = anuncio?.valorIptu ?? 0;
        document.getElementById('anuncio-tags').value = anuncio?.tags ? anuncio.tags.join(', ') : '';

        // Limpeza dos campos visuais de endereço do formulário
        document.getElementById('anuncio-cep').value = '';
        document.getElementById('anuncio-logradouro').value = '';
        document.getElementById('anuncio-numero').value = '';
        document.getElementById('anuncio-complemento').value = '';
        document.getElementById('anuncio-bairro').value = '';
        document.getElementById('anuncio-estado').value = '';

        modalAnuncio.classList.add('active');
    }

    // 6. ENVIAR FORMULÁRIO (CRIAR / ATUALIZAR)
    formAnuncio.addEventListener('submit', async (e) => {
        e.preventDefault();

        const id = document.getElementById('anuncio-id').value;
        const imovelId = document.getElementById('anuncio-imovel-id').value || crypto.randomUUID();
        const tagsInput = document.getElementById('anuncio-tags').value;
        const tagsArray = tagsInput ? tagsInput.split(',').map(t => t.trim()).filter(t => t.length > 0) : [];

        // Payload idêntico ao exigido no AnuncioCreateDTO do Java
        const payload = {
            imovelId: imovelId,
            anuncianteId: usuarioLogado.id,
            cidade: document.getElementById('anuncio-cidade').value.trim(),
            tipoImovel: document.getElementById('anuncio-tipo-imovel').value,
            quartos: parseInt(document.getElementById('anuncio-quartos').value, 10),
            tags: tagsArray,
            valor: parseFloat(document.getElementById('anuncio-valor').value),
            modalidade: document.getElementById('anuncio-modalidade').value,
            valorCondominio: parseFloat(document.getElementById('anuncio-condominio').value || 0),
            valorIptu: parseFloat(document.getElementById('anuncio-iptu').value || 0),
            status: 'ATIVO'
        };

        const isUpdate = Boolean(id);
        const url = isUpdate ? `${API_BASE}/anuncios/${id}` : `${API_BASE}/anuncios`;
        const method = isUpdate ? 'PUT' : 'POST';

        try {
            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(payload)
            });

            if (response.ok || response.status === 201) {
                alert(`Anúncio ${isUpdate ? 'atualizado' : 'criado'} com sucesso!`);
                modalAnuncio.classList.remove('active');
                carregarMeusAnuncios();
            } else {
                const erroBody = await response.json().catch(() => ({}));
                alert(erroBody.message || erroBody.erro || 'Erro ao salvar anúncio. Verifique os dados fornecidos.');
            }
        } catch (err) {
            alert('Falha de conexão com o servidor ao tentar salvar anúncio.');
        }
    });

    // 7. AÇÕES DE API (STATUS, RENOVAR, DELETAR)
    async function alterarStatus(id, novoStatus) {
        try {
            const response = await fetch(`${API_BASE}/anuncios/${id}/status?status=${encodeURIComponent(novoStatus)}`, {
                method: 'PATCH',
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (response.ok) {
                alert('Status alterado com sucesso!');
                carregarMeusAnuncios();
            } else {
                alert('Não foi possível alterar o status.');
            }
        } catch (err) {
            alert('Erro de conexão ao alterar status.');
        }
    }

    async function renovarAnuncio(id) {
        try {
            const response = await fetch(`${API_BASE}/anuncios/${id}/renovar`, {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (response.ok) {
                alert('Anúncio renovado por mais 90 dias!');
                carregarMeusAnuncios();
            } else {
                alert('Não foi possível renovar o anúncio.');
            }
        } catch (err) {
            alert('Erro de conexão ao renovar anúncio.');
        }
    }

    async function deletarAnuncio(id) {
        try {
            const response = await fetch(`${API_BASE}/anuncios/${id}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (response.status === 204 || response.ok) {
                alert('Anúncio deletado com sucesso.');
                carregarMeusAnuncios();
            } else {
                alert('Não foi possível deletar o anúncio.');
            }
        } catch (err) {
            alert('Erro de conexão ao deletar anúncio.');
        }
    }

    // Inicialização
    carregarUsuario();
});