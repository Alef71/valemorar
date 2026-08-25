package br.com.valemorar.domain.documento_legal.service;

import br.com.valemorar.domain.documento_legal.DocumentoLegal;
import br.com.valemorar.domain.documento_legal.dto.DocumentoLegalCreateDTO;
import br.com.valemorar.domain.documento_legal.dto.DocumentoLegalResponseDTO;
import br.com.valemorar.domain.documento_legal.repository.DocumentoLegalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentoLegalService {

    private final DocumentoLegalRepository repository;

    public DocumentoLegalService(DocumentoLegalRepository repository) {
        this.repository = repository;
    }

    public DocumentoLegalResponseDTO criar(DocumentoLegalCreateDTO dto) {
        DocumentoLegal entity = new DocumentoLegal();
        entity.setTipo(dto.tipo());
        entity.setVersao(dto.versao());
        entity.setConteudo(dto.conteudo());
        entity.setPublicadoEm(LocalDateTime.now());

        DocumentoLegal salvo = repository.save(entity);
        return DocumentoLegalResponseDTO.fromEntity(salvo);
    }

    public List<DocumentoLegalResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(DocumentoLegalResponseDTO::fromEntity)
                .toList();
    }

    public DocumentoLegalResponseDTO buscarPorId(UUID id) {
        DocumentoLegal entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento legal não encontrado"));
        return DocumentoLegalResponseDTO.fromEntity(entity);
    }

    public List<DocumentoLegalResponseDTO> buscarPorTipo(String tipo) {
        return repository.findByTipo(tipo)
                .stream()
                .map(DocumentoLegalResponseDTO::fromEntity)
                .toList();
    }

    public DocumentoLegalResponseDTO atualizar(UUID id, DocumentoLegalCreateDTO dto) {
        DocumentoLegal entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento legal não encontrado"));

        entity.setTipo(dto.tipo());
        entity.setVersao(dto.versao());
        entity.setConteudo(dto.conteudo());

        DocumentoLegal atualizado = repository.save(entity);
        return DocumentoLegalResponseDTO.fromEntity(atualizado);
    }

    public void deletar(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Documento legal não encontrado");
        }
        repository.deleteById(id);
    }
}
