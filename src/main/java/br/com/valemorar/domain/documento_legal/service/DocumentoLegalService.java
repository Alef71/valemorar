package br.com.valemorar.domain.documento_legal.service;

import br.com.valemorar.domain.documento_legal.DocumentoLegal;
import br.com.valemorar.domain.documento_legal.dto.DocumentoLegalCreateDTO;
import br.com.valemorar.domain.documento_legal.dto.DocumentoLegalResponseDTO;
import br.com.valemorar.domain.documento_legal.repository.DocumentoLegalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentoLegalService {

    private final DocumentoLegalRepository repository;

    public DocumentoLegalService(DocumentoLegalRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public DocumentoLegalResponseDTO criar(DocumentoLegalCreateDTO dto) {
        if (repository.existsByTipoAndVersao(dto.tipo(), dto.versao())) {
            throw new IllegalArgumentException("Já existe um documento legal cadastrado para este tipo e versão");
        }

        DocumentoLegal entity = new DocumentoLegal();
        entity.setTipo(dto.tipo());
        entity.setVersao(dto.versao());
        entity.setConteudo(dto.conteudo());
        entity.setPublicadoEm(LocalDateTime.now());

        DocumentoLegal salvo = repository.save(entity);
        return DocumentoLegalResponseDTO.fromEntity(salvo);
    }

    @Transactional(readOnly = true)
    public List<DocumentoLegalResponseDTO> listarTodos() {
        return repository.findAllByOrderByPublicadoEmDesc()
                .stream()
                .map(DocumentoLegalResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public DocumentoLegalResponseDTO buscarPorId(UUID id) {
        DocumentoLegal entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Documento legal não encontrado"));
        return DocumentoLegalResponseDTO.fromEntity(entity);
    }

    @Transactional(readOnly = true)
    public List<DocumentoLegalResponseDTO> buscarPorTipo(String tipo) {
        return repository.findByTipoOrderByPublicadoEmDesc(tipo)
                .stream()
                .map(DocumentoLegalResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public DocumentoLegalResponseDTO buscarPorTipoEVersao(String tipo, String versao) {
        DocumentoLegal entity = repository.findByTipoAndVersao(tipo, versao)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Documento legal não encontrado para o tipo e versão especificados"));
        return DocumentoLegalResponseDTO.fromEntity(entity);
    }

    @Transactional
    public DocumentoLegalResponseDTO atualizar(UUID id, DocumentoLegalCreateDTO dto) {
        DocumentoLegal entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Documento legal não encontrado"));

        if (repository.existsByTipoAndVersaoAndIdNot(dto.tipo(), dto.versao(), id)) {
            throw new IllegalArgumentException("Já existe outro documento legal cadastrado para este tipo e versão");
        }

        entity.setTipo(dto.tipo());
        entity.setVersao(dto.versao());
        entity.setConteudo(dto.conteudo());

        DocumentoLegal atualizado = repository.save(entity);
        return DocumentoLegalResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public void deletar(UUID id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Documento legal não encontrado");
        }
        repository.deleteById(id);
    }
}