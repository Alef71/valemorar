package br.com.valemorar.domain.denuncia.service;

import br.com.valemorar.domain.denuncia.Denuncia;
import br.com.valemorar.domain.denuncia.dto.DenunciaCreateDTO;
import br.com.valemorar.domain.denuncia.dto.DenunciaResponseDTO;
import br.com.valemorar.domain.denuncia.enums.StatusDenunciaEnum;
import br.com.valemorar.domain.denuncia.repository.DenunciaRepository;
import br.com.valemorar.infra.SecurityUtils;
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
        UUID usuarioAutenticadoId = SecurityUtils.resolverDono(dto.denuncianteId());

        Denuncia entity = new Denuncia();
        entity.setDenuncianteId(usuarioAutenticadoId);
        entity.setAnuncioId(dto.anuncioId());
        entity.setMotivo(dto.motivo());
        entity.setDescricao(dto.descricao());
        entity.setStatus(StatusDenunciaEnum.PENDENTE.name());
        entity.setDenunciadoEm(LocalDateTime.now());

        Denuncia salvo = repository.save(entity);
        return DenunciaResponseDTO.fromEntity(salvo);
    }

    @Transactional(readOnly = true)
    public Page<DenunciaResponseDTO> listarTodos(Pageable pageable) {
        SecurityUtils.exigirAdmin();
        return repository.findAll(pageable)
                .map(DenunciaResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public DenunciaResponseDTO buscarPorId(UUID id) {
        Denuncia entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Denúncia não encontrada"));
        SecurityUtils.exigirDonoOuAdmin(entity.getDenuncianteId());
        return DenunciaResponseDTO.fromEntity(entity);
    }

    @Transactional(readOnly = true)
    public Page<DenunciaResponseDTO> buscarPorStatus(String status, Pageable pageable) {
        SecurityUtils.exigirAdmin();
        String statusNormalizado = normalizarStatus(status).name();
        return repository.findByStatus(statusNormalizado, pageable)
                .map(DenunciaResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<DenunciaResponseDTO> buscarPorAnuncio(UUID anuncioId, Pageable pageable) {
        SecurityUtils.exigirAdmin();
        return repository.findByAnuncioId(anuncioId, pageable)
                .map(DenunciaResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<DenunciaResponseDTO> buscarPorDenunciante(UUID denuncianteId, Pageable pageable) {
        SecurityUtils.exigirDonoOuAdmin(denuncianteId);
        return repository.findByDenuncianteId(denuncianteId, pageable)
                .map(DenunciaResponseDTO::fromEntity);
    }

    @Transactional
    public DenunciaResponseDTO resolverDenuncia(UUID id, String novoStatus) {
        SecurityUtils.exigirAdmin();
        Denuncia entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Denúncia não encontrada"));

        StatusDenunciaEnum statusResolvido = normalizarStatus(novoStatus);
        if (StatusDenunciaEnum.PENDENTE.equals(statusResolvido)) {
            throw new IllegalArgumentException("Status de resolução inválido para encerramento da denúncia");
        }

        entity.setStatus(statusResolvido.name());
        entity.setResolvidoPor(SecurityUtils.getUsuarioAutenticadoId());
        entity.setResolvidoEm(LocalDateTime.now());

        Denuncia atualizado = repository.save(entity);
        return DenunciaResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public void deletar(UUID id) {
        Denuncia entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Denúncia não encontrada"));
        SecurityUtils.exigirDonoOuAdmin(entity.getDenuncianteId());
        repository.delete(entity);
    }

    private StatusDenunciaEnum normalizarStatus(String status) {
        try {
            return StatusDenunciaEnum.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Status de denúncia inválido: " + status);
        }
    }
}