package br.com.valemorar.domain.foto_imovel.service;

import br.com.valemorar.domain.foto_imovel.repository.FotoImovelRepository;
import br.com.valemorar.domain.imovel.Imovel;
import br.com.valemorar.domain.imovel.repository.ImovelRepository;
import br.com.valemorar.domain.usuario.Usuario;
import br.com.valemorar.infra.ImageStorageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FotoImovelServiceTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void rejectsAnUploadWhenTheListingAlreadyHasSixPhotos() {
        UUID userId = UUID.randomUUID();
        UUID imovelId = UUID.randomUUID();
        Usuario usuario = new Usuario();
        usuario.setId(userId);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario, null, List.of()));

        Imovel imovel = new Imovel();
        imovel.setLocadorId(userId);
        FotoImovelRepository fotoRepository = mock(FotoImovelRepository.class);
        ImovelRepository imovelRepository = mock(ImovelRepository.class);
        ImageStorageService imageStorageService = mock(ImageStorageService.class);
        FotoImovelService service = new FotoImovelService(
                fotoRepository, imovelRepository, imageStorageService);
        when(imovelRepository.findById(imovelId)).thenReturn(Optional.of(imovel));
        when(fotoRepository.countByImovelId(imovelId)).thenReturn(6L);
        MockMultipartFile file = new MockMultipartFile(
                "file", "house.jpg", "image/jpeg", new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF });

        assertThrows(IllegalArgumentException.class,
                () -> service.criarUpload(imovelId, file, false, 6, "http://localhost:8080"));
        verify(imageStorageService, never()).store(file);
    }
}
