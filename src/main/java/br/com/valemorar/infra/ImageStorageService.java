package br.com.valemorar.infra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Service
public class ImageStorageService {

    private static final long MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024;
    private final Path uploadDirectory;

    public ImageStorageService(@Value("${app.uploads.directory:uploads}") String uploadDirectory) {
        this.uploadDirectory = Path.of(uploadDirectory).toAbsolutePath().normalize();
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Selecione uma imagem para enviar.");
        }
        if (file.getSize() > MAX_IMAGE_SIZE_BYTES) {
            throw new IllegalArgumentException("A imagem deve ter no máximo 5 MB.");
        }

        try {
            byte[] content = file.getBytes();
            String extension = detectarExtensao(content, file.getContentType());
            String fileName = UUID.randomUUID() + "." + extension;
            Path destination = uploadDirectory.resolve(fileName).normalize();
            if (!destination.getParent().equals(uploadDirectory)) {
                throw new IllegalArgumentException("Nome de arquivo inválido.");
            }

            Files.createDirectories(uploadDirectory);
            Files.write(destination, content, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
            return "/uploads/" + fileName;
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível salvar a imagem.", e);
        }
    }

    public void delete(String imagePath) {
        if (imagePath == null || !imagePath.startsWith("/uploads/")) {
            return;
        }

        Path file = uploadDirectory.resolve(imagePath.substring("/uploads/".length())).normalize();
        if (!file.getParent().equals(uploadDirectory)) {
            return;
        }

        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível remover a imagem enviada.", e);
        }
    }

    public Path getUploadDirectory() {
        return uploadDirectory;
    }

    private String detectarExtensao(byte[] content, String contentType) {
        if (content.length >= 3
                && (content[0] & 0xFF) == 0xFF
                && (content[1] & 0xFF) == 0xD8
                && (content[2] & 0xFF) == 0xFF
                && "image/jpeg".equalsIgnoreCase(contentType)) {
            return "jpg";
        }
        if (content.length >= 8
                && (content[0] & 0xFF) == 0x89
                && content[1] == 'P'
                && content[2] == 'N'
                && content[3] == 'G'
                && (content[4] & 0xFF) == 0x0D
                && (content[5] & 0xFF) == 0x0A
                && (content[6] & 0xFF) == 0x1A
                && (content[7] & 0xFF) == 0x0A
                && "image/png".equalsIgnoreCase(contentType)) {
            return "png";
        }
        if (content.length >= 12
                && content[0] == 'R'
                && content[1] == 'I'
                && content[2] == 'F'
                && content[3] == 'F'
                && content[8] == 'W'
                && content[9] == 'E'
                && content[10] == 'B'
                && content[11] == 'P'
                && "image/webp".equalsIgnoreCase(contentType)) {
            return "webp";
        }
        throw new IllegalArgumentException("Formato inválido. Envie uma imagem JPEG, PNG ou WebP.");
    }
}
