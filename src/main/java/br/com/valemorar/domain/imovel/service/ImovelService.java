package br.com.valemorar.domain.imovel.service;

import br.com.valemorar.domain.endereco.service.EnderecoService;
import br.com.valemorar.domain.imovel.Imovel;
import br.com.valemorar.domain.imovel.dto.ImovelCreateDTO;
import br.com.valemorar.domain.imovel.dto.ImovelResponseDTO;
import br.com.valemorar.domain.imovel.repository.ImovelRepository;
import br.com.valemorar.infra.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ImovelService {

    private static final String MENSAGEM_NAO_ENCONTRADO = "Imóvel não encontrado";

    private final ImovelRepository imovelRepository;
    private final EnderecoService enderecoService;

    public ImovelService(ImovelRepository imovelRepository, EnderecoService enderecoService) {
        this.imovelRepository = imovelRepository;
        this.enderecoService = enderecoService;
    }

    @Transactional
    public ImovelResponseDTO criar(ImovelCreateDTO dto) {
        Imovel imovel = new Imovel();
        imovel.setLocadorId(SecurityUtils.resolverDono(dto.locadorId()));
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
                .orElseThrow(() -> new IllegalArgumentException(MENSAGEM_NAO_ENCONTRADO));
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
        Imovel imovel = buscarProprio(id);

        preencherDadosImovel(imovel, dto);
        imovel.setAtualizadoEm(LocalDateTime.now());

        Imovel atualizado = imovelRepository.save(imovel);
        return ImovelResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public void deletar(UUID id) {
        imovelRepository.delete(buscarProprio(id));
    }

    /** Busca um imóvel garantindo que pertence ao usuário autenticado (ou que ele é admin). */
    public Imovel buscarProprio(UUID id) {
        Imovel imovel = imovelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MENSAGEM_NAO_ENCONTRADO));
        SecurityUtils.exigirDonoOuAdmin(imovel.getLocadorId());
        return imovel;
    }

    private void preencherDadosImovel(Imovel imovel, ImovelCreateDTO dto) {
        // O endereço precisa pertencer ao mesmo usuário
        enderecoService.buscarProprio(dto.enderecoId());
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
        imovel.setValorCondominio(dto.valorCondominio());
        imovel.setValorIptu(dto.valorIptu());
    }
}