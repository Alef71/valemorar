package br.com.valemorar.infra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;

/**
 * Armazena imagens enviadas pelos usuários e devolve a URL pública que é salva no banco.
 * Hoje grava em disco local (app.upload.dir) servido em /uploads/**; para migrar para GCS/S3,
 * basta reimplementar salvarImagem/remover aqui devolvendo a URL do bucket.
 */
@Component
public class ArmazenamentoArquivos {

    public static final String PREFIXO_URL = "/uploads/";

    private final Path raiz;

    public ArmazenamentoArquivos(@Value("${app.upload.dir:uploads}") String diretorio) {
        this.raiz = Path.of(diretorio).toAbsolutePath().normalize();
    }

    public Path getRaiz() {
        return raiz;
    }

    /** Salva a imagem em {raiz}/{pasta}/{uuid}.{ext} e retorna "/uploads/{pasta}/{uuid}.{ext}". */
    public String salvarImagem(MultipartFile arquivo, Pasta pasta) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("Nenhum arquivo enviado");
        }

        // O tipo vem do conteúdo (assinatura do arquivo), nunca do nome ou Content-Type enviados pelo cliente
        String extensao = detectarExtensao(arquivo);
        String nome = UUID.randomUUID() + "." + extensao;
        Path destino = raiz.resolve(pasta.diretorio).resolve(nome);

        try (InputStream entrada = arquivo.getInputStream()) {
            Files.createDirectories(destino.getParent());
            Files.copy(entrada, destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível salvar o arquivo", e);
        }
        return PREFIXO_URL + pasta.diretorio + "/" + nome;
    }

    /** Remove um arquivo salvo por esta classe; URLs externas ou fora de /uploads/ são ignoradas. */
    public void remover(String url) {
        if (url == null || !url.startsWith(PREFIXO_URL)) {
            return;
        }
        Path alvo = raiz.resolve(url.substring(PREFIXO_URL.length())).normalize();
        if (!alvo.startsWith(raiz)) {
            return; // proteção contra path traversal ("/uploads/../...")
        }
        try {
            Files.deleteIfExists(alvo);
        } catch (IOException ignored) {
            // Arquivo órfão não impede a operação principal
        }
    }

    private String detectarExtensao(MultipartFile arquivo) {
        byte[] cabecalho = new byte[12];
        int lidos;
        try (InputStream entrada = arquivo.getInputStream()) {
            lidos = entrada.readNBytes(cabecalho, 0, cabecalho.length);
        } catch (IOException e) {
            throw new IllegalArgumentException("Arquivo inválido");
        }
        if (lidos >= 3 && (cabecalho[0] & 0xFF) == 0xFF && (cabecalho[1] & 0xFF) == 0xD8 && (cabecalho[2] & 0xFF) == 0xFF) {
            return "jpg";
        }
        if (lidos >= 8 && Arrays.equals(Arrays.copyOf(cabecalho, 8),
                new byte[] { (byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A })) {
            return "png";
        }
        if (lidos >= 12 && new String(cabecalho, 0, 4).equals("RIFF") && new String(cabecalho, 8, 4).equals("WEBP")) {
            return "webp";
        }
        throw new IllegalArgumentException("Formato de imagem não suportado. Envie JPG, PNG ou WEBP");
    }

    public enum Pasta {
        USUARIOS("usuarios"),
        ANUNCIOS("anuncios");

        private final String diretorio;

        Pasta(String diretorio) {
            this.diretorio = diretorio;
        }
    }
}
