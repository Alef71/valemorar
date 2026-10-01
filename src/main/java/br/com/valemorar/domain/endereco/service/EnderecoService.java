package br.com.valemorar.domain.endereco.service;

import br.com.valemorar.domain.endereco.Endereco;
import br.com.valemorar.domain.endereco.dto.EnderecoCreateDTO;
import br.com.valemorar.domain.endereco.dto.EnderecoResponseDTO;
import br.com.valemorar.domain.endereco.repository.EnderecoRepository;
import br.com.valemorar.infra.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class EnderecoService {

    private static final String MENSAGEM_NAO_ENCONTRADO = "Endereço não encontrado";

    private final EnderecoRepository repository;

    public EnderecoService(EnderecoRepository repository) {
        this.repository = repository;
    }

    /** Cria um endereço de imóvel pertencente ao usuário autenticado. */
    @Transactional
    public EnderecoResponseDTO criar(EnderecoCreateDTO dto) {
        Endereco entity = new Endereco();
        entity.setUsuarioId(SecurityUtils.getUsuarioAutenticadoId());
        entity.setPessoal(false);
        copiarDtoParaEntidade(dto, entity);

        Endereco salvo = repository.save(entity);
        return EnderecoResponseDTO.fromEntity(salvo);
    }

    @Transactional(readOnly = true)
    public Optional<EnderecoResponseDTO> buscarMeuEnderecoPessoal() {
        return repository.findByUsuarioIdAndPessoalTrue(SecurityUtils.getUsuarioAutenticadoId())
                .map(EnderecoResponseDTO::fromEntity);
    }

    /** Cria ou atualiza o endereço pessoal (do perfil) do usuário autenticado. */
    @Transactional
    public EnderecoResponseDTO salvarMeuEnderecoPessoal(EnderecoCreateDTO dto) {
        UUID usuarioId = SecurityUtils.getUsuarioAutenticadoId();
        Endereco entity = repository.findByUsuarioIdAndPessoalTrue(usuarioId).orElseGet(() -> {
            Endereco novo = new Endereco();
            novo.setUsuarioId(usuarioId);
            novo.setPessoal(true);
            return novo;
        });
        copiarDtoParaEntidade(dto, entity);

        return EnderecoResponseDTO.fromEntity(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<EnderecoResponseDTO> listarTodos(Pageable pageable) {
        SecurityUtils.exigirAdmin();
        return repository.findAll(pageable)
                .map(EnderecoResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public EnderecoResponseDTO buscarPorId(UUID id) {
        return EnderecoResponseDTO.fromEntity(buscarProprio(id));
    }

    @Transactional(readOnly = true)
    public Page<EnderecoResponseDTO> buscarPorCidade(String cidade, Pageable pageable) {
        SecurityUtils.exigirAdmin();
        return repository.findByCidadeIgnoreCase(cidade, pageable)
                .map(EnderecoResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<EnderecoResponseDTO> buscarPorCep(String cep, Pageable pageable) {
        SecurityUtils.exigirAdmin();
        return repository.findByCep(cep, pageable)
                .map(EnderecoResponseDTO::fromEntity);
    }

    @Transactional
    public EnderecoResponseDTO atualizar(UUID id, EnderecoCreateDTO dto) {
        Endereco entity = buscarProprio(id);
        copiarDtoParaEntidade(dto, entity);

        Endereco atualizado = repository.save(entity);
        return EnderecoResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public void deletar(UUID id) {
        repository.delete(buscarProprio(id));
    }

    /** Busca um endereço garantindo que pertence ao usuário autenticado (ou que ele é admin). */
    public Endereco buscarProprio(UUID id) {
        Endereco entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MENSAGEM_NAO_ENCONTRADO));
        SecurityUtils.exigirDonoOuAdmin(entity.getUsuarioId());
        return entity;
    }

    public void copiarDtoParaEntidade(EnderecoCreateDTO dto, Endereco entity) {
        entity.setLogradouro(dto.logradouro());
        entity.setNumero(dto.numero());
        entity.setComplemento(dto.complemento());
        entity.setBairro(dto.bairro());
        entity.setCidade(dto.cidade());
        entity.setEstado(dto.estado() != null ? dto.estado().toUpperCase() : null);
        entity.setCep(dto.cep() != null ? dto.cep().replace("-", "") : null);
        entity.setLatitude(dto.latitude());
        entity.setLongitude(dto.longitude());
    }
}
