-- Endereço passa a ter dono; "pessoal" marca o endereço do perfil (os demais são endereços de imóveis).
-- IF NOT EXISTS: bancos de desenvolvimento podem já ter as colunas criadas pelo antigo ddl-auto=update.
ALTER TABLE endereco ADD COLUMN IF NOT EXISTS usuario_id uuid;
ALTER TABLE endereco ADD COLUMN IF NOT EXISTS pessoal boolean;
UPDATE endereco SET pessoal = false WHERE pessoal IS NULL;
ALTER TABLE endereco ALTER COLUMN pessoal SET DEFAULT false;
ALTER TABLE endereco ALTER COLUMN pessoal SET NOT NULL;

-- Endereços de imóveis já existentes herdam o dono do imóvel
UPDATE endereco e SET usuario_id = i.locador_id FROM imovel i WHERE i.endereco_id = e.id AND e.usuario_id IS NULL;

CREATE INDEX IF NOT EXISTS idx_endereco_usuario ON endereco (usuario_id);
CREATE UNIQUE INDEX IF NOT EXISTS uq_endereco_pessoal_usuario ON endereco (usuario_id) WHERE pessoal;
