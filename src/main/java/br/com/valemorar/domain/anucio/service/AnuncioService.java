package br.com.valemorar.domain.anucio.service;

import br.com.valemorar.domain.anucio.Anuncio;
import br.com.valemorar.domain.anucio.dto.AnuncioCreateDTO;
import br.com.valemorar.domain.anucio.dto.AnuncioResponseDTO;
import br.com.valemorar.domain.anucio.repository.AnuncioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AnuncioService {

    private final AnuncioRepository anuncioRepository;

    public AnuncioService(AnuncioRepository anuncioRepository) {
        this.anuncioRepository = anuncioRepository;
    }

    public AnuncioResponseDTO criar(AnuncioCreateDTO dto) {
        Anuncio anuncio = new Anuncio();
        anuncio.setImovelId(dto.imovelId());
        anuncio.setAnuncianteId(dto.anuncianteId());
        anuncio.setValor(dto.valor());
        anuncio.setModalidade(dto.modalidade());
        anuncio.setValorCondominio(dto.valorCondominio());
        anuncio.setValorIptu(dto.valorIptu());
        anuncio.setNotaMedia(BigDecimal.ZERO);
        anuncio.setTotalAvaliacoes(0);
        anuncio.setStatus(dto.status() != null ? dto.status() : "ATIVO");
        anuncio.setPublicadoEm(LocalDateTime.now());
        anuncio.setExpiraEm(dto.expiraEm());
        anuncio.setAtualizadoEm(LocalDateTime.now());

        Anuncio salvo = anuncioRepository.save(anuncio);
        return AnuncioResponseDTO.fromEntity(salvo);
    }

    public List<AnuncioResponseDTO> listarTodos() {
        return anuncioRepository.findAll()
                .stream()
                .map(AnuncioResponseDTO::fromEntity)
                .toList();
    }

    public AnuncioResponseDTO buscarPorId(UUID id) {
        Anuncio anuncio = anuncioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Anúncio não encontrado"));
        return AnuncioResponseDTO.fromEntity(anuncio);
    }

    public List<AnuncioResponseDTO> buscarPorAnunciante(UUID anuncianteId) {
        return anuncioRepository.findByAnuncianteId(anuncianteId)
                .stream()
                .map(AnuncioResponseDTO::fromEntity)
                .toList();
    }

    public AnuncioResponseDTO atualizar(UUID id, AnuncioCreateDTO dto) {
        Anuncio anuncio = anuncioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Anúncio não encontrado"));

        anuncio.setImovelId(dto.imovelId());
        anuncio.setAnuncianteId(dto.anuncianteId());
        anuncio.setValor(dto.valor());
        anuncio.setModalidade(dto.modalidade());
        anuncio.setValorCondominio(dto.valorCondominio());
        anuncio.setValorIptu(dto.valorIptu());
        if (dto.status() != null) {
            anuncio.setStatus(dto.status());
        }
        anuncio.setExpiraEm(dto.expiraEm());
        anuncio.setAtualizadoEm(LocalDateTime.now());

        Anuncio atualizado = anuncioRepository.save(anuncio);
        return AnuncioResponseDTO.fromEntity(atualizado);
    }

    public void deletar(UUID id) {
        if (!anuncioRepository.existsById(id)) {
            throw new RuntimeException("Anúncio não encontrado");
        }
        anuncioRepository.deleteById(id);
    }
}
