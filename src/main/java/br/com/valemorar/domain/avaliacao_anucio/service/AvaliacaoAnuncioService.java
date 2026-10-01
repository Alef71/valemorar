package br.com.valemorar.domain.avaliacao_anucio.service;

import br.com.valemorar.domain.avaliacao_anucio.AvaliacaoAnuncio;
import br.com.valemorar.domain.avaliacao_anucio.dto.AvaliacaoAnuncioCreateDTO;
import br.com.valemorar.domain.avaliacao_anucio.dto.AvaliacaoAnuncioResponseDTO;
import br.com.valemorar.domain.avaliacao_anucio.repository.AvaliacaoAnuncioRepository;
import br.com.valemorar.infra.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AvaliacaoAnuncioService {

    private final AvaliacaoAnuncioRepository repository;

    public AvaliacaoAnuncioService(AvaliacaoAnuncioRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public AvaliacaoAnuncioResponseDTO criar(AvaliacaoAnuncioCreateDTO dto) {
        AvaliacaoAnuncio avaliacao = new AvaliacaoAnuncio();
        avaliacao.setAnuncioId(dto.anuncioId());
        avaliacao.setUsuarioId(SecurityUtils.resolverDono(dto.usuarioId()));
        avaliacao.setNota(dto.nota());
        avaliacao.setComentario(dto.comentario());
        avaliacao.setCriadoEm(LocalDateTime.now());

        AvaliacaoAnuncio salvo = repository.save(avaliacao);
        return AvaliacaoAnuncioResponseDTO.fromEntity(salvo);
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoAnuncioResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(AvaliacaoAnuncioResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public AvaliacaoAnuncioResponseDTO buscarPorId(UUID id) {
        AvaliacaoAnuncio avaliacao = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada"));
        return AvaliacaoAnuncioResponseDTO.fromEntity(avaliacao);
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoAnuncioResponseDTO> buscarPorAnuncio(UUID anuncioId) {
        return repository.findByAnuncioId(anuncioId)
                .stream()
                .map(AvaliacaoAnuncioResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public AvaliacaoAnuncioResponseDTO atualizar(UUID id, AvaliacaoAnuncioCreateDTO dto) {
        AvaliacaoAnuncio avaliacao = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada"));
        SecurityUtils.exigirDonoOuAdmin(avaliacao.getUsuarioId());

        avaliacao.setNota(dto.nota());
        avaliacao.setComentario(dto.comentario());

        AvaliacaoAnuncio atualizado = repository.save(avaliacao);
        return AvaliacaoAnuncioResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public void deletar(UUID id) {
        AvaliacaoAnuncio avaliacao = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada"));
        SecurityUtils.exigirDonoOuAdmin(avaliacao.getUsuarioId());
        repository.delete(avaliacao);
    }
}