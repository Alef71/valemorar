document.addEventListener('DOMContentLoaded', async () => {
    const API_BASE = 'http://localhost:8080/api';
    const token = localStorage.getItem('token');
    
    if (!token) {
        window.location.href = 'index.html';
        return;
    }

    let usuarioLogado = null;
    let anunciosEmMemoria = [];
    let fotosLista = []; // Armazena as fotos do formulário atual

    // Elementos do DOM
    const btnNovoAnuncio = document.getElementById('btn-novo-anuncio');
    const modalAnuncio = document.getElementById('modal-anuncio');
    const btnFecharModal = document.getElementById('btn-fechar-modal-anuncio');
    const btnCancelarModal = document.getElementById('btn-cancelar-anuncio');
    const formAnuncio = document.getElementById('form-anuncio');
    const anunciosContainer = document.getElementById('anuncios-info-container');
    const totalAnunciosElem = document.getElementById('total-anuncios');

    // Elementos do Gerenciador de Fotos
    const inputFotoUrl = document.getElementById('foto-url-input');
    const checkFotoCapa = document.getElementById('foto-capa-checkbox');
    const btnAddFoto = document.getElementById('btn-add-foto');
    const containerFotos = document.getElementById('lista-fotos-container');

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
                anunciosContainer.innerHTML = '<p class="empty-message text-sm text-brand-textSecondary">Erro ao carregar seus anúncios.</p>';
            }
        } catch (error) {
            console.error('Erro ao buscar anúncios:', error);
            anunciosContainer.innerHTML = '<p class="empty-message text-sm text-brand-textSecondary">Erro de conexão ao carregar anúncios.</p>';
        }
    }

    // 3. RENDERIZAR LISTA DE ANÚNCIOS NO HTML
    function renderizarAnuncios(lista) {
        if (totalAnunciosElem) {
            totalAnunciosElem.innerText = lista.length;
        }

        if (lista.length === 0) {
            anunciosContainer.innerHTML = '<p class="empty-message text-sm text-brand-textSecondary">Nenhum anúncio cadastrado no momento.</p>';
            return;
        }

        anunciosContainer.innerHTML = lista.map(anuncio => {
            const tagsFormatted = anuncio.tags && anuncio.tags.length > 0 
                ? anuncio.tags.map(t => `<span class="bg-gray-200 text-gray-800 text-[10px] font-semibold px-2 py-0.5 rounded mr-1">${t}</span>`).join('') 
                : '<em class="text-xs text-gray-400">Nenhuma tag</em>';

            const statusClass = anuncio.status ? anuncio.status.toLowerCase() : 'ativo';

            return `
                <div class="anuncio-card border border-[#dfd7c8] p-4 rounded-xl mb-4 bg-white shadow-sm">
                    <div class="flex justify-between items-center mb-2">
                        <h4 class="font-bold text-brand-textPrimary">${anuncio.cidade} - ${anuncio.tipoImovel || 'RESIDENCIAL'} (${anuncio.modalidade})</h4>
                        <span class="status-badge px-2 py-1 rounded text-xs font-bold bg-gray-100 text-brand-textPrimary">
                            ${anuncio.status || 'ATIVO'}
                        </span>
                    </div>
                    <p class="text-xs text-brand-textSecondary mb-2">
                        <strong>Valor:</strong> R$ ${parseFloat(anuncio.valor).toFixed(2)} | 
                        <strong>Condomínio:</strong> R$ ${parseFloat(anuncio.valorCondominio || 0).toFixed(2)} | 
                        <strong>IPTU:</strong> R$ ${parseFloat(anuncio.valorIptu || 0).toFixed(2)}
                    </p>
                    <p class="text-xs text-brand-textSecondary mb-2">
                        <strong>Quartos:</strong> ${anuncio.quartos || 0} | <strong>Tags:</strong> ${tagsFormatted}
                    </p>
                    <p class="text-[11px] text-gray-500 mb-3">
                        <strong>Expira em:</strong> ${anuncio.expiraEm ? new Date(anuncio.expiraEm).toLocaleDateString('pt-BR') : 'Não definida'}
                    </p>
                    
                    <div class="anuncio-actions flex gap-2 flex-wrap">
                        <button class="btn-editar-anuncio px-3 py-1 bg-gray-100 hover:bg-gray-200 text-xs font-bold rounded border" data-id="${anuncio.id}">Editar</button>
                        <button class="btn-status-anuncio px-3 py-1 bg-gray-100 hover:bg-gray-200 text-xs font-bold rounded border" data-id="${anuncio.id}" data-status="${anuncio.status}">Alterar Status</button>
                        <button class="btn-renovar-anuncio px-3 py-1 bg-gray-100 hover:bg-gray-200 text-xs font-bold rounded border" data-id="${anuncio.id}">Renovar (90d)</button>
                        <button class="btn-deletar-anuncio px-3 py-1 bg-red-600 hover:bg-red-700 text-white text-xs font-bold rounded" data-id="${anuncio.id}">Excluir</button>
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

    // 5. GERENCIAMENTO DAS FOTOS
    if (btnAddFoto) {
        btnAddFoto.addEventListener('click', () => {
            const url = inputFotoUrl.value.trim();
            if (!url) return alert('Informe uma URL de imagem válida.');

            const isCapa = checkFotoCapa.checked;
            if (isCapa) {
                fotosLista.forEach(f => f.capa = false);
            }

            fotosLista.push({ url, capa: isCapa });
            inputFotoUrl.value = '';
            checkFotoCapa.checked = false;
            renderizarFotos();
        });
    }

    function renderizarFotos() {
        if (!containerFotos) return;

        if (fotosLista.length === 0) {
            containerFotos.innerHTML = '<p class="col-span-full text-center text-xs text-brand-textSecondary my-auto">Nenhuma foto adicionada ainda.</p>';
            return;
        }

        containerFotos.innerHTML = fotosLista.map((foto, index) => `
            <div class="relative group border rounded-lg overflow-hidden bg-gray-100 h-20">
                <img src="${foto.url}" class="w-full h-full object-cover" alt="Foto">
                ${foto.capa ? '<span class="absolute top-1 left-1 bg-brand-gold text-white text-[10px] font-bold px-1.5 py-0.5 rounded">Capa</span>' : ''}
                <button type="button" onclick="removerFoto(${index})" class="absolute top-1 right-1 bg-red-600 text-white rounded-full w-5 h-5 text-xs flex items-center justify-center font-bold shadow">&times;</button>
            </div>
        `).join('');
    }

    window.removerFoto = (index) => {
        fotosLista.splice(index, 1);
        renderizarFotos();
    };

    // 6. ABRIR E FECHAR MODAL (Com ajuste para Tailwind)
    if (btnNovoAnuncio) {
        btnNovoAnuncio.addEventListener('click', () => abrirModalAnuncio(null));
    }

    function fecharModal() {
        if (modalAnuncio) modalAnuncio.classList.add('hidden');
    }

    if (btnFecharModal) btnFecharModal.onclick = fecharModal;
    if (btnCancelarModal) btnCancelarModal.onclick = fecharModal;

    function abrirModalAnuncio(anuncio = null) {
        document.getElementById('modal-anuncio-titulo').innerText = anuncio ? 'Editar Anúncio' : 'Criar Anúncio';
        document.getElementById('anuncio-id').value = anuncio?.id || '';
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

        // Carrega fotos se existirem
        fotosLista = anuncio?.fotos || [];
        renderizarFotos();

        // Remove a classe "hidden" do Tailwind para mostrar o modal
        modalAnuncio.classList.remove('hidden');
    }

    // 7. ENVIAR FORMULÁRIO (CRIAR / ATUALIZAR)
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
            status: 'ATIVO',
            fotos: fotosLista // Mantido o envio das fotos!
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
                fecharModal();
                carregarMeusAnuncios();
            } else {
                const erroBody = await response.json().catch(() => ({}));
                alert(erroBody.message || erroBody.erro || 'Erro ao salvar anúncio. Verifique os dados fornecidos.');
            }
        } catch (err) {
            alert('Falha de conexão com o servidor ao tentar salvar anúncio.');
        }
    });

    // 8. AÇÕES DE API (STATUS, RENOVAR, DELETAR)
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