package br.com.valemorar.domain.imovel.service;

import br.com.valemorar.domain.imovel.Imovel;
import br.com.valemorar.domain.imovel.dto.ImovelCreateDTO;
import br.com.valemorar.domain.imovel.dto.ImovelResponseDTO;
import br.com.valemorar.domain.imovel.repository.ImovelRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ImovelService {

    private final ImovelRepository imovelRepository;

    public ImovelService(ImovelRepository imovelRepository) {
        this.imovelRepository = imovelRepository;
    }

    @Transactional
    public ImovelResponseDTO criar(ImovelCreateDTO dto) {
        Imovel imovel = new Imovel();
        preencherDadosImovel(imovel, dto);
        imovel.setCriadoEm(LocalDateTime.now());
        imovel.setAtualizadoEm(LocalDateTime.now());

        Imovel salvo = imovelRepository.save(imovel);
        return ImovelResponseDTO.fromEntity(salvo);
    }

    @Transactional(readOnly = true)
    public Page<ImovelResponseDTO> listarTodos(Pageable pageable) {
        return imovelRepository.findAll(pageable)
                .map(ImovelResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public ImovelResponseDTO buscarPorId(UUID id) {
        Imovel imovel = imovelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Imóvel não encontrado"));
        return ImovelResponseDTO.fromEntity(imovel);
    }

    @Transactional(readOnly = true)
    public Page<ImovelResponseDTO> buscarPorLocador(UUID locadorId, Pageable pageable) {
        return imovelRepository.findByLocadorId(locadorId, pageable)
                .map(ImovelResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<ImovelResponseDTO> buscarPorTipo(String tipoImovel, Pageable pageable) {
        return imovelRepository.findByTipoImovelIgnoreCase(tipoImovel, pageable)
                .map(ImovelResponseDTO::fromEntity);
    }

    @Transactional
    public ImovelResponseDTO atualizar(UUID id, ImovelCreateDTO dto) {
        Imovel imovel = imovelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Imóvel não encontrado"));

        preencherDadosImovel(imovel, dto);
        imovel.setAtualizadoEm(LocalDateTime.now());

        Imovel atualizado = imovelRepository.save(imovel);
        return ImovelResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public void deletar(UUID id) {
        if (!imovelRepository.existsById(id)) {
            throw new IllegalArgumentException("Imóvel não encontrado");
        }
        imovelRepository.deleteById(id);
    }

    private void preencherDadosImovel(Imovel imovel, ImovelCreateDTO dto) {
        imovel.setLocadorId(dto.locadorId());
        imovel.setEnderecoId(dto.enderecoId());
        imovel.setTitulo(dto.titulo());
        imovel.setDescricao(dto.descricao());
        imovel.setTipoImovel(dto.tipoImovel());
        imovel.setAreaTotal(dto.areaTotal());
        imovel.setAreaConstruida(dto.areaConstruida());
        imovel.setQuartos(dto.quartos() != null ? dto.quartos() : 0);
        imovel.setSuites(dto.suites() != null ? dto.suites() : 0);
        imovel.setBanheiros(dto.banheiros() != null ? dto.banheiros() : 0);
        imovel.setVagasGaragem(dto.vagasGaragem() != null ? dto.vagasGaragem() : 0);
        imovel.setAndar(dto.andar());
        imovel.setMobiliado(Boolean.TRUE.equals(dto.mobiliado()));
        imovel.setAceitaPet(Boolean.TRUE.equals(dto.aceitaPet()));
    }
}