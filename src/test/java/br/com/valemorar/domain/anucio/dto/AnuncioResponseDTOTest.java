package br.com.valemorar.domain.anucio.dto;

import br.com.valemorar.domain.anucio.Anuncio;
import br.com.valemorar.domain.endereco.Endereco;
import br.com.valemorar.domain.imovel.Imovel;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AnuncioResponseDTOTest {

    @Test
    void includesPublicFullAddress() {
        UUID enderecoId = UUID.randomUUID();
        Imovel imovel = new Imovel();
        imovel.setEnderecoId(enderecoId);
        imovel.setTitulo("Casa espaçosa");
        imovel.setDescricao("Casa perto do centro.");

        Anuncio anuncio = new Anuncio();
        anuncio.setImovel(imovel);

        Endereco endereco = new Endereco();
        endereco.setId(enderecoId);
        endereco.setLogradouro("Rua Particular");
        endereco.setNumero("123");
        endereco.setBairro("Centro Histórico");
        endereco.setCidade("Diamantina");
        endereco.setEstado("MG");
        endereco.setCep("39100000");

        AnuncioResponseDTO response = AnuncioResponseDTO.fromEntity(anuncio, endereco);

        assertEquals("Rua Particular", response.logradouro());
        assertEquals("123", response.numero());
        assertNull(response.complemento());
        assertEquals("Centro Histórico", response.bairro());
        assertEquals("Diamantina", response.cidade());
        assertEquals("MG", response.estado());
        assertEquals("39100000", response.cep());
        assertEquals("Casa espaçosa", response.titulo());
        assertEquals("Casa perto do centro.", response.descricao());
    }

    @Test
    void returnsNullLocationWhenAddressDoesNotExist() {
        Anuncio anuncio = new Anuncio();

        AnuncioResponseDTO response = AnuncioResponseDTO.fromEntity(anuncio, null);

        assertNull(response.logradouro());
        assertNull(response.numero());
        assertNull(response.complemento());
        assertNull(response.bairro());
        assertNull(response.cidade());
        assertNull(response.estado());
        assertNull(response.cep());
        assertNull(response.titulo());
        assertNull(response.descricao());
    }
}
