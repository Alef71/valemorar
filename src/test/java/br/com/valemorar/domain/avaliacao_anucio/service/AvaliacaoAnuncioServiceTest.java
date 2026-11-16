package br.com.valemorar.domain.avaliacao_anucio.service;

import br.com.valemorar.domain.anucio.Anuncio;
import br.com.valemorar.domain.anucio.repository.AnuncioRepository;
import br.com.valemorar.domain.avaliacao_anucio.AvaliacaoAnuncio;
import br.com.valemorar.domain.avaliacao_anucio.dto.AvaliacaoAnuncioCreateDTO;
import br.com.valemorar.domain.avaliacao_anucio.dto.AvaliacaoAnuncioResponseDTO;
import br.com.valemorar.domain.avaliacao_anucio.repository.AvaliacaoAnuncioRepository;
import br.com.valemorar.domain.usuario.Usuario;
import br.com.valemorar.domain.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AvaliacaoAnuncioServiceTest {

    @AfterEach
    void limparContextoDeSeguranca() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void criaComentarioComUsuarioAutenticadoEPermiteAnuncioDeOutraPessoa() {
        UUID usuarioId = UUID.randomUUID();
        UUID usuarioInformadoNoPayload = UUID.randomUUID();
        UUID anuncioId = UUID.randomUUID();
        Usuario usuario = usuario(usuarioId, "Pessoa Comentarista");
        autenticar(usuario);

        Anuncio anuncio = new Anuncio();
        anuncio.setId(anuncioId);
        anuncio.setAnuncianteId(UUID.randomUUID());

        AvaliacaoAnuncioRepository repository = mock(AvaliacaoAnuncioRepository.class);
        AnuncioRepository anuncioRepository = mock(AnuncioRepository.class);
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        when(anuncioRepository.findById(anuncioId)).thenReturn(Optional.of(anuncio));
        when(repository.save(any(AvaliacaoAnuncio.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AvaliacaoAnuncioService service = new AvaliacaoAnuncioService(repository, anuncioRepository, usuarioRepository);
        AvaliacaoAnuncioResponseDTO response = service.criar(
                new AvaliacaoAnuncioCreateDTO(anuncioId, usuarioInformadoNoPayload, null, " Ótimo imóvel "));

        var avaliacaoCaptor = org.mockito.ArgumentCaptor.forClass(AvaliacaoAnuncio.class);
        verify(repository).save(avaliacaoCaptor.capture());
        assertEquals(usuarioId, avaliacaoCaptor.getValue().getUsuarioId());
        assertEquals(anuncioId, avaliacaoCaptor.getValue().getAnuncioId());
        assertEquals("Ótimo imóvel", avaliacaoCaptor.getValue().getComentario());
        assertEquals("Pessoa Comentarista", response.usuarioNome());
    }

    @Test
    void impedeAnuncianteDeComentarNoProprioAnuncio() {
        UUID usuarioId = UUID.randomUUID();
        UUID anuncioId = UUID.randomUUID();
        autenticar(usuario(usuarioId, "Anunciante"));

        Anuncio anuncio = new Anuncio();
        anuncio.setId(anuncioId);
        anuncio.setAnuncianteId(usuarioId);

        AvaliacaoAnuncioRepository repository = mock(AvaliacaoAnuncioRepository.class);
        AnuncioRepository anuncioRepository = mock(AnuncioRepository.class);
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        when(anuncioRepository.findById(anuncioId)).thenReturn(Optional.of(anuncio));

        AvaliacaoAnuncioService service = new AvaliacaoAnuncioService(repository, anuncioRepository, usuarioRepository);

        assertThrows(IllegalArgumentException.class, () -> service.criar(
                new AvaliacaoAnuncioCreateDTO(anuncioId, null, null, "Meu anúncio")));
        verify(repository, never()).save(any(AvaliacaoAnuncio.class));
    }

    @Test
    void recusaCriacaoSemUsuarioAutenticado() {
        UUID anuncioId = UUID.randomUUID();
        AvaliacaoAnuncioRepository repository = mock(AvaliacaoAnuncioRepository.class);
        AnuncioRepository anuncioRepository = mock(AnuncioRepository.class);
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        AvaliacaoAnuncioService service = new AvaliacaoAnuncioService(repository, anuncioRepository, usuarioRepository);

        assertThrows(IllegalStateException.class, () -> service.criar(
                new AvaliacaoAnuncioCreateDTO(anuncioId, UUID.randomUUID(), null, "Comentário")));
        verify(repository, never()).save(any(AvaliacaoAnuncio.class));
    }

    private Usuario usuario(UUID id, String nome) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNome(nome);
        return usuario;
    }

    private void autenticar(Usuario usuario) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario, null, List.of()));
    }
}
