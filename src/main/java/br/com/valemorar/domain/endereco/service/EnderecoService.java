package br.com.valemorar.domain.endereco.service;

import br.com.valemorar.domain.endereco.Endereco;
import br.com.valemorar.domain.endereco.dto.EnderecoCreateDTO;
import br.com.valemorar.domain.endereco.dto.EnderecoResponseDTO;
import br.com.valemorar.domain.endereco.repository.EnderecoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EnderecoService {

    private final EnderecoRepository repository;

    public EnderecoService(EnderecoRepository repository) {
        this.repository = repository;
    }

    public EnderecoResponseDTO criar(EnderecoCreateDTO dto) {
        Endereco entity = new Endereco();
        entity.setLogradouro(dto.logradouro());
        entity.setNumero(dto.numero());
        entity.setComplemento(dto.complemento());
        entity.setBairro(dto.bairro());
        entity.setCidade(dto.cidade());
        entity.setEstado(dto.estado());
        entity.setCep(dto.cep());
        entity.setLatitude(dto.latitude());
        entity.setLongitude(dto.longitude());

        Endereco salvo = repository.save(entity);
        return EnderecoResponseDTO.fromEntity(salvo);
    }

    public List<EnderecoResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(EnderecoResponseDTO::fromEntity)
                .toList();
    }

    public EnderecoResponseDTO buscarPorId(UUID id) {
        Endereco entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));
        return EnderecoResponseDTO.fromEntity(entity);
    }

    public List<EnderecoResponseDTO> buscarPorCidade(String cidade) {
        return repository.findByCidade(cidade)
                .stream()
                .map(EnderecoResponseDTO::fromEntity)
                .toList();
    }

    public EnderecoResponseDTO atualizar(UUID id, EnderecoCreateDTO dto) {
        Endereco entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));

        entity.setLogradouro(dto.logradouro());
        entity.setNumero(dto.numero());
        entity.setComplemento(dto.complemento());
        entity.setBairro(dto.bairro());
        entity.setCidade(dto.cidade());
        entity.setEstado(dto.estado());
        entity.setCep(dto.cep());
        entity.setLatitude(dto.latitude());
        entity.setLongitude(dto.longitude());

        Endereco atualizado = repository.save(entity);
        return EnderecoResponseDTO.fromEntity(atualizado);
    }

    public void deletar(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Endereço não encontrado");
        }
        repository.deleteById(id);
    }
}
