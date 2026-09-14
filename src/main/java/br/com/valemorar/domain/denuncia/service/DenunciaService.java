package br.com.valemorar.domain.denuncia.service;

import br.com.valemorar.domain.denuncia.Denuncia;
import br.com.valemorar.domain.denuncia.dto.DenunciaCreateDTO;
import br.com.valemorar.domain.denuncia.dto.DenunciaResponseDTO;
import br.com.valemorar.domain.denuncia.repository.DenunciaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class DenunciaService {

    private final DenunciaRepository repository;

    public DenunciaService(DenunciaRepository repository) {
        this.repository = repository;
    }

    @Transactional
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

    @Transactional(readOnly = true)
    public Page<DenunciaResponseDTO> listarTodos(Pageable pageable) {
        return repository.findAll(pageable)
                .map(DenunciaResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public DenunciaResponseDTO buscarPorId(UUID id) {
        Denuncia entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Denúncia não encontrada"));
        return DenunciaResponseDTO.fromEntity(entity);
    }

    @Transactional(readOnly = true)
    public Page<DenunciaResponseDTO> buscarPorStatus(String status, Pageable pageable) {
        return repository.findByStatus(status, pageable)
                .map(DenunciaResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<DenunciaResponseDTO> buscarPorAnuncio(UUID anuncioId, Pageable pageable) {
        return repository.findByAnuncioId(anuncioId, pageable)
                .map(DenunciaResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<DenunciaResponseDTO> buscarPorDenunciante(UUID denuncianteId, Pageable pageable) {
        return repository.findByDenuncianteId(denuncianteId, pageable)
                .map(DenunciaResponseDTO::fromEntity);
    }

    @Transactional
    public DenunciaResponseDTO resolverDenuncia(UUID id, UUID resolvidoPor, String novoStatus) {
        Denuncia entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Denúncia não encontrada"));

        entity.setStatus(novoStatus);
        entity.setResolvidoPor(resolvidoPor);
        entity.setResolvidoEm(LocalDateTime.now());

        Denuncia atualizado = repository.save(entity);
        return DenunciaResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public void deletar(UUID id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Denúncia não encontrada");
        }
        repository.deleteById(id);
    }
}