package br.com.valemorar.domain.avaliacao_anucio.service;

import br.com.valemorar.domain.anucio.Anuncio;
import br.com.valemorar.domain.anucio.repository.AnuncioRepository;
import br.com.valemorar.domain.avaliacao_anucio.AvaliacaoAnuncio;
import br.com.valemorar.domain.avaliacao_anucio.dto.AvaliacaoAnuncioCreateDTO;
import br.com.valemorar.domain.avaliacao_anucio.dto.AvaliacaoAnuncioResponseDTO;
import br.com.valemorar.domain.avaliacao_anucio.repository.AvaliacaoAnuncioRepository;
import br.com.valemorar.domain.usuario.Usuario;
import br.com.valemorar.domain.usuario.repository.UsuarioRepository;
import br.com.valemorar.infra.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AvaliacaoAnuncioService {

    private static final int COMENTARIO_MAXIMO_CARACTERES = 2000;

    private final AvaliacaoAnuncioRepository repository;
    private final AnuncioRepository anuncioRepository;
    private final UsuarioRepository usuarioRepository;

    public AvaliacaoAnuncioService(
            AvaliacaoAnuncioRepository repository,
            AnuncioRepository anuncioRepository,
            UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.anuncioRepository = anuncioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public AvaliacaoAnuncioResponseDTO criar(AvaliacaoAnuncioCreateDTO dto) {
        if (dto.anuncioId() == null) {
            throw new IllegalArgumentException("O anúncio é obrigatório.");
        }
        validarComentario(dto.comentario());

        Usuario usuario = SecurityUtils.getUsuarioAutenticado();
        Anuncio anuncio = anuncioRepository.findById(dto.anuncioId())
                .orElseThrow(() -> new IllegalArgumentException("Anúncio não encontrado."));
        validarPodeComentar(anuncio, usuario.getId());

        AvaliacaoAnuncio avaliacao = new AvaliacaoAnuncio();
        avaliacao.setAnuncioId(anuncio.getId());
        avaliacao.setUsuarioId(usuario.getId());
        avaliacao.setNota(dto.nota());
        avaliacao.setComentario(dto.comentario().trim());
        avaliacao.setCriadoEm(LocalDateTime.now());

        AvaliacaoAnuncio salvo = repository.save(avaliacao);
        return AvaliacaoAnuncioResponseDTO.fromEntity(salvo, usuario.getNome());
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoAnuncioResponseDTO> listarTodos() {
        return mapearRespostas(repository.findAll());
    }

    @Transactional(readOnly = true)
    public AvaliacaoAnuncioResponseDTO buscarPorId(UUID id) {
        AvaliacaoAnuncio avaliacao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avaliação não encontrada"));
        return mapearRespostas(List.of(avaliacao)).get(0);
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoAnuncioResponseDTO> buscarPorAnuncio(UUID anuncioId) {
        return mapearRespostas(repository.findByAnuncioIdOrderByCriadoEmDesc(anuncioId));
    }

    @Transactional
    public AvaliacaoAnuncioResponseDTO atualizar(UUID id, AvaliacaoAnuncioCreateDTO dto) {
        Usuario usuario = SecurityUtils.getUsuarioAutenticado();
        AvaliacaoAnuncio avaliacao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avaliação não encontrada"));
        validarAutorDaAvaliacao(avaliacao, usuario.getId());

        avaliacao.setNota(dto.nota());
        avaliacao.setComentario(dto.comentario());

        AvaliacaoAnuncio atualizado = repository.save(avaliacao);
        return AvaliacaoAnuncioResponseDTO.fromEntity(atualizado, usuario.getNome());
    }

    @Transactional
    public void deletar(UUID id) {
        Usuario usuario = SecurityUtils.getUsuarioAutenticado();
        AvaliacaoAnuncio avaliacao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avaliação não encontrada"));
        validarAutorDaAvaliacao(avaliacao, usuario.getId());
        repository.delete(avaliacao);
    }

    private void validarComentario(String comentario) {
        if (comentario == null || comentario.isBlank()) {
            throw new IllegalArgumentException("O comentário não pode ficar vazio.");
        }
        if (comentario.length() > COMENTARIO_MAXIMO_CARACTERES) {
            throw new IllegalArgumentException("O comentário deve ter no máximo 2000 caracteres.");
        }
    }

    private void validarPodeComentar(Anuncio anuncio, UUID usuarioId) {
        if (anuncio.getAnuncianteId().equals(usuarioId)) {
            throw new IllegalArgumentException("Você não pode comentar no seu próprio anúncio.");
        }
    }

    private void validarAutorDaAvaliacao(AvaliacaoAnuncio avaliacao, UUID usuarioId) {
        if (!usuarioId.equals(avaliacao.getUsuarioId())) {
            throw new IllegalArgumentException("Você só pode alterar ou remover seus próprios comentários.");
        }
    }

    private List<AvaliacaoAnuncioResponseDTO> mapearRespostas(List<AvaliacaoAnuncio> avaliacoes) {
        Set<UUID> usuarioIds = avaliacoes.stream()
                .map(AvaliacaoAnuncio::getUsuarioId)
                .collect(Collectors.toSet());
        Map<UUID, Usuario> usuarios = usuarioRepository.findAllById(usuarioIds).stream()
                .collect(Collectors.toMap(Usuario::getId, Function.identity()));

        return avaliacoes.stream()
                .map(avaliacao -> {
                    Usuario usuario = usuarios.get(avaliacao.getUsuarioId());
                    String nome = usuario != null ? usuario.getNome() : "Usuário";
                    return AvaliacaoAnuncioResponseDTO.fromEntity(avaliacao, nome);
                })
                .toList();
    }
}