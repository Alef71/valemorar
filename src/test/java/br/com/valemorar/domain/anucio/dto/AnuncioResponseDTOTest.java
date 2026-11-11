package br.com.valemorar.domain.anucio.dto;

import br.com.valemorar.domain.anucio.Anuncio;
import br.com.valemorar.domain.endereco.Endereco;
import br.com.valemorar.domain.foto_imovel.dto.FotoImovelResponseDTO;
import br.com.valemorar.domain.imovel.Imovel;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnuncioResponseDTOTest {

    @Test
    void includesDescriptionAndOnlyPublicAddressFields() {
        Imovel imovel = new Imovel();
        imovel.setTitulo("Casa espaçosa");
        imovel.setDescricao("Casa perto do centro.");

        Anuncio anuncio = new Anuncio();
        anuncio.setImovel(imovel);

        Endereco endereco = new Endereco();
        endereco.setLogradouro("Rua Particular");
        endereco.setNumero("123");
        endereco.setBairro("Centro Histórico");
        endereco.setCidade("Diamantina");
        endereco.setEstado("MG");

        AnuncioResponseDTO response = AnuncioResponseDTO.fromEntity(anuncio, endereco, List.<FotoImovelResponseDTO>of());

        assertEquals("Casa perto do centro.", response.descricao());
        assertEquals("Centro Histórico", response.bairro());
        assertEquals("Diamantina", response.cidade());
        assertEquals("MG", response.estado());
    }
}
