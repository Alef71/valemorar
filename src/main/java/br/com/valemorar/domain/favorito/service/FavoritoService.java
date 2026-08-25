package br.com.valemorar.domain.favorito.service;

import br.com.valemorar.domain.favorito.Favorito;
import br.com.valemorar.domain.favorito.dto.FavoritoCreateDTO;
import br.com.valemorar.domain.favorito.dto.FavoritoResponseDTO;
import br.com.valemorar.domain.favorito.repository.FavoritoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FavoritoService {

    private final FavoritoRepository repository;

    public FavoritoService(FavoritoRepository repository) {
        this.repository = repository;
    }

    public FavoritoResponseDTO criar(FavoritoCreateDTO dto) {
        if (repository.existsByUsuarioIdAndAnuncioId(dto.usuarioId(), dto.anuncioId())) {
            throw new RuntimeException("Anúncio já está nos favoritos deste usuário");
        }

        Favorito entity = new Favorito();
        entity.setUsuarioId(dto.usuarioId());
        entity.setAnuncioId(dto.anuncioId());
        entity.setAdicionadoEm(LocalDateTime.now());

        Favorito salvo = repository.save(entity);
        return FavoritoResponseDTO.fromEntity(salvo);
    }

    public List<FavoritoResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(FavoritoResponseDTO::fromEntity)
                .toList();
    }

    public FavoritoResponseDTO buscarPorId(UUID id) {
        Favorito entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Favorito não encontrado"));
        return FavoritoResponseDTO.fromEntity(entity);
    }

    public List<FavoritoResponseDTO> buscarPorUsuario(UUID usuarioId) {
        return repository.findByUsuarioId(usuarioId)
                .stream()
                .map(FavoritoResponseDTO::fromEntity)
                .toList();
    }

    public void deletar(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Favorito não encontrado");
        }
        repository.deleteById(id);
    }
}
