package br.com.valemorar.domain.imovel.service;

import br.com.valemorar.domain.imovel.Imovel;
import br.com.valemorar.domain.imovel.dto.ImovelCreateDTO;
import br.com.valemorar.domain.imovel.dto.ImovelResponseDTO;
import br.com.valemorar.domain.imovel.repository.ImovelRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ImovelService {

    private final ImovelRepository imovelRepository;

    public ImovelService(ImovelRepository imovelRepository) {
        this.imovelRepository = imovelRepository;
    }

    public ImovelResponseDTO criar(ImovelCreateDTO dto) {
        Imovel imovel = new Imovel();
        imovel.setLocadorId(dto.locadorId());
        imovel.setEnderecoId(dto.enderecoId());
        imovel.setTitulo(dto.titulo());
        imovel.setDescricao(dto.descricao());
        imovel.setTipoImovel(dto.tipoImovel());
        imovel.setAreaTotal(dto.areaTotal());
        imovel.setAreaConstruida(dto.areaConstruida());
        imovel.setQuartos(dto.quartos());
        imovel.setSuites(dto.suites());
        imovel.setBanheiros(dto.banheiros());
        imovel.setVagasGaragem(dto.vagasGaragem());
        imovel.setAndar(dto.andar());
        imovel.setMobiliado(dto.mobiliado());
        imovel.setAceitaPet(dto.aceitaPet());
        imovel.setCriadoEm(LocalDateTime.now());
        imovel.setAtualizadoEm(LocalDateTime.now());

        Imovel salvo = imovelRepository.save(imovel);
        return ImovelResponseDTO.fromEntity(salvo);
    }

    public List<ImovelResponseDTO> listarTodos() {
        return imovelRepository.findAll()
                .stream()
                .map(ImovelResponseDTO::fromEntity)
                .toList();
    }

    public ImovelResponseDTO buscarPorId(UUID id) {
        Imovel imovel = imovelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imóvel não encontrado"));
        return ImovelResponseDTO.fromEntity(imovel);
    }

    public ImovelResponseDTO atualizar(UUID id, ImovelCreateDTO dto) {
        Imovel imovel = imovelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imóvel não encontrado"));

        imovel.setLocadorId(dto.locadorId());
        imovel.setEnderecoId(dto.enderecoId());
        imovel.setTitulo(dto.titulo());
        imovel.setDescricao(dto.descricao());
        imovel.setTipoImovel(dto.tipoImovel());
        imovel.setAreaTotal(dto.areaTotal());
        imovel.setAreaConstruida(dto.areaConstruida());
        imovel.setQuartos(dto.quartos());
        imovel.setSuites(dto.suites());
        imovel.setBanheiros(dto.banheiros());
        imovel.setVagasGaragem(dto.vagasGaragem());
        imovel.setAndar(dto.andar());
        imovel.setMobiliado(dto.mobiliado());
        imovel.setAceitaPet(dto.aceitaPet());
        imovel.setAtualizadoEm(LocalDateTime.now());

        Imovel atualizado = imovelRepository.save(imovel);
        return ImovelResponseDTO.fromEntity(atualizado);
    }

    public void deletar(UUID id) {
        if (!imovelRepository.existsById(id)) {
            throw new RuntimeException("Imóvel não encontrado");
        }
        imovelRepository.deleteById(id);
    }
}