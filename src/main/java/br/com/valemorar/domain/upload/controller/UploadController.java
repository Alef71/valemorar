package br.com.valemorar.domain.upload.controller;

import br.com.valemorar.infra.ArmazenamentoArquivos;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/uploads")
@Tag(name = "Uploads", description = "Envio de imagens (armazenamento local em /uploads)")
public class UploadController {

    private final ArmazenamentoArquivos armazenamento;

    public UploadController(ArmazenamentoArquivos armazenamento) {
        this.armazenamento = armazenamento;
    }

    @Operation(summary = "Enviar foto de anúncio", description = "Recebe uma imagem (JPG, PNG ou WEBP, até 5MB) e retorna a URL para usar em fotos do anúncio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Imagem salva; corpo: { url }"),
            @ApiResponse(responseCode = "400", description = "Arquivo ausente ou formato não suportado")
    })
    @PostMapping(value = "/anuncios", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> enviarFotoAnuncio(@RequestParam("arquivo") MultipartFile arquivo) {
        // ponytail: fotos enviadas e nunca usadas em um anúncio ficam órfãs no disco; limpar com job periódico se o volume crescer
        String url = armazenamento.salvarImagem(arquivo, ArmazenamentoArquivos.Pasta.ANUNCIOS);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("url", url));
    }
}
