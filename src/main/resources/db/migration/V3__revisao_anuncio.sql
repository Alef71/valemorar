-- Revisão automática de anúncios: motivos da reprovação e índice de valor (ranking "abaixo do mercado")
ALTER TABLE anuncio ADD COLUMN motivo_revisao text;
ALTER TABLE anuncio ADD COLUMN indice_valor numeric(10,4);
ALTER TABLE anuncio ADD COLUMN economia_mercado numeric(38,2);
CREATE INDEX idx_anuncio_status ON anuncio (status);

-- Hash das fotos para detectar imagens reaproveitadas de outros anunciantes
ALTER TABLE foto_imovel ADD COLUMN hash character varying(64);
CREATE INDEX idx_foto_imovel_hash ON foto_imovel (hash);
