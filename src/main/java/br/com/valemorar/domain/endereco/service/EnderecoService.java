package br.com.valemorar.domain.endereco.service;

import br.com.valemorar.domain.endereco.Endereco;
import br.com.valemorar.domain.endereco.dto.EnderecoCreateDTO;
import br.com.valemorar.domain.endereco.dto.EnderecoResponseDTO;
import br.com.valemorar.domain.endereco.repository.EnderecoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EnderecoService {

    private final EnderecoRepository repository;

    public EnderecoService(EnderecoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public EnderecoResponseDTO criar(EnderecoCreateDTO dto) {
        Endereco entity = new Endereco();
        copiarDtoParaEntidade(dto, entity);

        Endereco salvo = repository.save(entity);
        return EnderecoResponseDTO.fromEntity(salvo);
    }

    @Transactional(readOnly = true)
    public Page<EnderecoResponseDTO> listarTodos(Pageable pageable) {
        return repository.findAll(pageable)
                .map(EnderecoResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public EnderecoResponseDTO buscarPorId(UUID id) {
        Endereco entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Endereço não encontrado"));
        return EnderecoResponseDTO.fromEntity(entity);
    }

    @Transactional(readOnly = true)
    public Page<EnderecoResponseDTO> buscarPorCidade(String cidade, Pageable pageable) {
        return repository.findByCidadeIgnoreCase(cidade, pageable)
                .map(EnderecoResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<EnderecoResponseDTO> buscarPorCep(String cep, Pageable pageable) {
        return repository.findByCep(cep, pageable)
                .map(EnderecoResponseDTO::fromEntity);
    }

    @Transactional
    public EnderecoResponseDTO atualizar(UUID id, EnderecoCreateDTO dto) {
        Endereco entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Endereço não encontrado"));

        copiarDtoParaEntidade(dto, entity);

        Endereco atualizado = repository.save(entity);
        return EnderecoResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public void deletar(UUID id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Endereço não encontrado");
        }
        repository.deleteById(id);
    }

    private void copiarDtoParaEntidade(EnderecoCreateDTO dto, Endereco entity) {
        entity.setLogradouro(dto.logradouro());
        entity.setNumero(dto.numero());
        entity.setComplemento(dto.complemento());
        entity.setBairro(dto.bairro());
        entity.setCidade(dto.cidade());
        entity.setEstado(dto.estado());
        entity.setCep(dto.cep());
        entity.setLatitude(dto.latitude());
        entity.setLongitude(dto.longitude());
    }
}