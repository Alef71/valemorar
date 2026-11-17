package br.com.valemorar.domain.anucio.service;

import br.com.valemorar.domain.anucio.Anuncio;
import br.com.valemorar.domain.anucio.dto.AnuncioResponseDTO;
import br.com.valemorar.domain.anucio.repository.AnuncioRepository;
import br.com.valemorar.domain.endereco.Endereco;
import br.com.valemorar.domain.endereco.repository.EnderecoRepository;
import br.com.valemorar.domain.foto_imovel.FotoImovel;
import br.com.valemorar.domain.foto_imovel.repository.FotoImovelRepository;
import br.com.valemorar.domain.imovel.Imovel;
import br.com.valemorar.domain.imovel.repository.ImovelRepository;
import br.com.valemorar.domain.usuario.Usuario;
import br.com.valemorar.domain.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AnuncioServiceTest {

    @Test
    void includesFullAddressInPaginatedPublicListings() {
        UUID enderecoId = UUID.randomUUID();
        UUID imovelId = UUID.randomUUID();
        Imovel imovel = new Imovel();
        imovel.setId(imovelId);
        imovel.setEnderecoId(enderecoId);
        imovel.setTitulo("Casa das Flores");
        imovel.setDescricao("Casa com quintal.");
        imovel.setAceitaPet(true);

        Anuncio anuncio = new Anuncio();
        anuncio.setAnuncianteId(UUID.randomUUID());
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
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        FotoImovelRepository fotoImovelRepository = mock(FotoImovelRepository.class);
        AnuncioService service = new AnuncioService(
                anuncioRepository, imovelRepository, enderecoRepository, usuarioRepository, fotoImovelRepository);
        Usuario anunciante = new Usuario();
        anunciante.setId(anuncio.getAnuncianteId());
        anunciante.setNome("Maria Silva");
        anunciante.setEmail("maria@example.com");
        anunciante.setFotoPerfil("http://localhost:8080/uploads/perfil.jpg");
        PageRequest pageable = PageRequest.of(0, 10);
        when(anuncioRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(anuncio), pageable, 1));
        when(enderecoRepository.findAllById(Set.of(enderecoId))).thenReturn(List.of(endereco));
        when(usuarioRepository.findAllById(Set.of(anunciante.getId()))).thenReturn(List.of(anunciante));
        FotoImovel fotoCapa = new FotoImovel();
        fotoCapa.setImovelId(imovelId);
        fotoCapa.setUrl("http://localhost:8080/uploads/capa.jpg");
        fotoCapa.setCapa(true);
        when(fotoImovelRepository.findByImovelIdInOrderByOrdemAsc(Set.of(imovelId)))
                .thenReturn(List.of(fotoCapa));

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
        assertTrue(response.aceitaPet());
        assertEquals("Maria Silva", response.anuncianteNome());
        assertEquals("maria@example.com", response.anuncianteEmail());
        assertEquals("http://localhost:8080/uploads/capa.jpg", response.fotoCapa());
        assertEquals("http://localhost:8080/uploads/perfil.jpg", response.anuncianteFotoPerfil());
        verify(enderecoRepository).findAllById(Set.of(enderecoId));
    }

    @Test
    void includesAdvertiserNameAndEmailInPublicAnnouncementDetails() {
        UUID anuncioId = UUID.randomUUID();
        UUID anuncianteId = UUID.randomUUID();
        Anuncio anuncio = new Anuncio();
        anuncio.setId(anuncioId);
        anuncio.setAnuncianteId(anuncianteId);

        Usuario anunciante = new Usuario();
        anunciante.setId(anuncianteId);
        anunciante.setNome("Maria Silva");
        anunciante.setEmail("maria@example.com");
        anunciante.setFotoPerfil("http://localhost:8080/uploads/perfil.jpg");

        AnuncioRepository anuncioRepository = mock(AnuncioRepository.class);
        ImovelRepository imovelRepository = mock(ImovelRepository.class);
        EnderecoRepository enderecoRepository = mock(EnderecoRepository.class);
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        FotoImovelRepository fotoImovelRepository = mock(FotoImovelRepository.class);
        AnuncioService service = new AnuncioService(
                anuncioRepository, imovelRepository, enderecoRepository, usuarioRepository, fotoImovelRepository);
        when(anuncioRepository.findById(anuncioId)).thenReturn(Optional.of(anuncio));
        when(usuarioRepository.findById(anuncianteId)).thenReturn(Optional.of(anunciante));

        AnuncioResponseDTO resposta = service.buscarPorId(anuncioId);

        assertEquals("Maria Silva", resposta.anuncianteNome());
        assertEquals("maria@example.com", resposta.anuncianteEmail());
        assertEquals("http://localhost:8080/uploads/perfil.jpg", resposta.anuncianteFotoPerfil());
    }
}
