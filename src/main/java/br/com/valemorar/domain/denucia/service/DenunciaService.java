package br.com.valemorar.domain.denucia.service;

import br.com.valemorar.domain.denucia.Denuncia;
import br.com.valemorar.domain.denucia.dto.DenunciaCreateDTO;
import br.com.valemorar.domain.denucia.dto.DenunciaResponseDTO;
import br.com.valemorar.domain.denucia.repository.DenunciaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DenunciaService {

    private final DenunciaRepository repository;

    public DenunciaService(DenunciaRepository repository) {
        this.repository = repository;
    }

    public DenunciaResponseDTO criar(DenunciaCreateDTO dto) {
        Denuncia entity = new Denuncia();
        entity.setDenuncianteId(dto.denuncianteId());
        entity.setAnuncioId(dto.anuncioId());
        entity.setMotivo(dto.motivo());
        entity.setDescricao(dto.descricao());
        entity.setStatus("PENDENTE");
        entity.setDenunciadoEm(LocalDateTime.now());

        Denuncia salvo = repository.save(entity);
        return DenunciaResponseDTO.fromEntity(salvo);
    }

    public List<DenunciaResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(DenunciaResponseDTO::fromEntity)
                .toList();
    }

    public DenunciaResponseDTO buscarPorId(UUID id) {
        Denuncia entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Denúncia não encontrada"));
        return DenunciaResponseDTO.fromEntity(entity);
    }

    public List<DenunciaResponseDTO> buscarPorStatus(String status) {
        return repository.findByStatus(status)
                .stream()
                .map(DenunciaResponseDTO::fromEntity)
                .toList();
    }

    public DenunciaResponseDTO resolverDenuncia(UUID id, UUID resolvidoPor, String novoStatus) {
        Denuncia entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Denúncia não encontrada"));

        entity.setStatus(novoStatus);
        entity.setResolvidoPor(resolvidoPor);
        entity.setResolvidoEm(LocalDateTime.now());

        Denuncia atualizado = repository.save(entity);
        return DenunciaResponseDTO.fromEntity(atualizado);
    }

    public void deletar(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Denúncia não encontrada");
        }
        repository.deleteById(id);
    }
}
