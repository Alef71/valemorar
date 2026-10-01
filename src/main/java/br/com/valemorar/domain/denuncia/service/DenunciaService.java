package br.com.valemorar.domain.denuncia.service;

import br.com.valemorar.domain.denuncia.Denuncia;
import br.com.valemorar.domain.denuncia.dto.DenunciaCreateDTO;
import br.com.valemorar.domain.denuncia.dto.DenunciaResponseDTO;
import br.com.valemorar.domain.denuncia.enums.StatusDenunciaEnum;
import br.com.valemorar.domain.denuncia.repository.DenunciaRepository;
import br.com.valemorar.domain.usuario.Usuario;
import br.com.valemorar.domain.usuario.enums.PerfilEnum;
import br.com.valemorar.infra.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
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
        UUID usuarioAutenticadoId = getUsuarioAutenticadoId();
        if (dto.denuncianteId() != null && !usuarioAutenticadoId.equals(dto.denuncianteId()) && !isAdmin()) {
            throw new AccessDeniedException("Você não tem permissão para registrar denúncia para outro usuário");
        }

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
        validarAdmin();
        return repository.findAll(pageable)
                .map(DenunciaResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public DenunciaResponseDTO buscarPorId(UUID id) {
        Denuncia entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Denúncia não encontrada"));
        validarAcessoDenuncia(entity);
        return DenunciaResponseDTO.fromEntity(entity);
    }

    @Transactional(readOnly = true)
    public Page<DenunciaResponseDTO> buscarPorStatus(String status, Pageable pageable) {
        validarAdmin();
        String statusNormalizado = normalizarStatus(status).name();
        return repository.findByStatus(statusNormalizado, pageable)
                .map(DenunciaResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<DenunciaResponseDTO> buscarPorAnuncio(UUID anuncioId, Pageable pageable) {
        validarAdmin();
        return repository.findByAnuncioId(anuncioId, pageable)
                .map(DenunciaResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<DenunciaResponseDTO> buscarPorDenunciante(UUID denuncianteId, Pageable pageable) {
        validarAcessoUsuario(denuncianteId);
        return repository.findByDenuncianteId(denuncianteId, pageable)
                .map(DenunciaResponseDTO::fromEntity);
    }

    @Transactional
    public DenunciaResponseDTO resolverDenuncia(UUID id, String novoStatus) {
        validarAdmin();
        Denuncia entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Denúncia não encontrada"));

        StatusDenunciaEnum statusResolvido = normalizarStatus(novoStatus);
        if (StatusDenunciaEnum.PENDENTE.equals(statusResolvido)) {
            throw new IllegalArgumentException("Status de resolução inválido para encerramento da denúncia");
        }

        entity.setStatus(statusResolvido.name());
        entity.setResolvidoPor(getUsuarioAutenticadoId());
        entity.setResolvidoEm(LocalDateTime.now());

        Denuncia atualizado = repository.save(entity);
        return DenunciaResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public void deletar(UUID id) {
        Denuncia entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Denúncia não encontrada"));
        validarAcessoDenuncia(entity);
        repository.delete(entity);
    }

    private UUID getUsuarioAutenticadoId() {
        return SecurityUtils.getUsuarioAutenticado().getId();
    }

    private boolean isAdmin() {
        Usuario usuario = SecurityUtils.getUsuarioAutenticado();
        return PerfilEnum.ROLE_ADMIN.equals(usuario.getPerfil());
    }

    private void validarAdmin() {
        if (!isAdmin()) {
            throw new AccessDeniedException("Apenas administradores podem acessar este recurso");
        }
    }

    private void validarAcessoUsuario(UUID usuarioId) {
        if (!isAdmin() && !getUsuarioAutenticadoId().equals(usuarioId)) {
            throw new AccessDeniedException("Você não tem permissão para acessar denúncias de outro usuário");
        }
    }

    private void validarAcessoDenuncia(Denuncia denuncia) {
        if (!isAdmin() && !getUsuarioAutenticadoId().equals(denuncia.getDenuncianteId())) {
            throw new AccessDeniedException("Você não tem permissão para acessar esta denúncia");
        }
    }

    private StatusDenunciaEnum normalizarStatus(String status) {
        try {
            return StatusDenunciaEnum.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Status de denúncia inválido: " + status);
        }
    }
}