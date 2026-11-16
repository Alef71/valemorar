package br.com.valemorar.infra;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImageStorageServiceTest {

    @TempDir
    Path uploadDirectory;

    @Test
    void storesImageWithGeneratedName() throws Exception {
        ImageStorageService service = new ImageStorageService(uploadDirectory.toString());
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "profile.jpg",
                "image/jpeg",
                new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF });

        String path = service.store(file);

        assertTrue(path.matches("/uploads/[0-9a-f-]{36}\\.jpg"));
        assertTrue(Files.exists(uploadDirectory.resolve(path.substring("/uploads/".length()))));
    }

    @Test
    void rejectsContentThatDoesNotMatchAnAllowedImageFormat() {
        ImageStorageService service = new ImageStorageService(uploadDirectory.toString());
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "profile.png",
                "image/png",
                "not an image".getBytes());

        assertThrows(IllegalArgumentException.class, () -> service.store(file));
    }
}
