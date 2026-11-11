package br.com.valemorar.domain.anucio.service;

import br.com.valemorar.domain.anucio.Anuncio;
import br.com.valemorar.domain.anucio.dto.AnuncioResponseDTO;
import br.com.valemorar.domain.anucio.repository.AnuncioRepository;
import br.com.valemorar.domain.endereco.Endereco;
import br.com.valemorar.domain.endereco.repository.EnderecoRepository;
import br.com.valemorar.domain.imovel.Imovel;
import br.com.valemorar.domain.imovel.repository.ImovelRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AnuncioServiceTest {

    @Test
    void includesFullAddressInPaginatedPublicListings() {
        UUID enderecoId = UUID.randomUUID();
        Imovel imovel = new Imovel();
        imovel.setEnderecoId(enderecoId);
        imovel.setTitulo("Casa das Flores");
        imovel.setDescricao("Casa com quintal.");

        Anuncio anuncio = new Anuncio();
        anuncio.setImovel(imovel);

        Endereco endereco = new Endereco();
        endereco.setId(enderecoId);
        endereco.setLogradouro("Rua das Flores");
        endereco.setNumero("45");
        endereco.setComplemento("Casa 2");
        endereco.setBairro("Centro Histórico");
        endereco.setCidade("Diamantina");
        endereco.setEstado("MG");
        endereco.setCep("39100000");

        AnuncioRepository anuncioRepository = mock(AnuncioRepository.class);
        ImovelRepository imovelRepository = mock(ImovelRepository.class);
        EnderecoRepository enderecoRepository = mock(EnderecoRepository.class);
        AnuncioService service = new AnuncioService(anuncioRepository, imovelRepository, enderecoRepository);
        PageRequest pageable = PageRequest.of(0, 10);
        when(anuncioRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(anuncio), pageable, 1));
        when(enderecoRepository.findAllById(Set.of(enderecoId))).thenReturn(List.of(endereco));

        Page<AnuncioResponseDTO> result = service.listarTodos(pageable);

        AnuncioResponseDTO response = result.getContent().get(0);
        assertEquals("Rua das Flores", response.logradouro());
        assertEquals("45", response.numero());
        assertEquals("Casa 2", response.complemento());
        assertEquals("Centro Histórico", response.bairro());
        assertEquals("Diamantina", response.cidade());
        assertEquals("MG", response.estado());
        assertEquals("39100000", response.cep());
        assertEquals("Casa das Flores", response.titulo());
        assertEquals("Casa com quintal.", response.descricao());
        verify(enderecoRepository).findAllById(Set.of(enderecoId));
    }
}
