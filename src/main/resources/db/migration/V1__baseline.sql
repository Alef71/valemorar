
CREATE TABLE public.aceite_documento (
    id uuid NOT NULL,
    aceito_em timestamp(6) without time zone,
    documento_id uuid NOT NULL,
    ip character varying(255),
    usuario_id uuid NOT NULL
);

CREATE TABLE public.anuncio (
    id uuid NOT NULL,
    anunciante_id uuid NOT NULL,
    atualizado_em timestamp(6) without time zone,
    expira_em timestamp(6) without time zone,
    modalidade character varying(255) NOT NULL,
    nota_media numeric(38,2),
    publicado_em timestamp(6) without time zone,
    status character varying(255) NOT NULL,
    total_avaliacoes integer,
    valor numeric(38,2) NOT NULL,
    imovel_id uuid NOT NULL
);

CREATE TABLE public.anuncio_tags (
    anuncio_id uuid NOT NULL,
    tag character varying(255)
);

CREATE TABLE public.avaliacao_anuncio (
    id uuid NOT NULL,
    anuncio_id uuid,
    comentario text,
    criado_em timestamp(6) without time zone,
    nota smallint,
    usuario_id uuid
);

CREATE TABLE public.caracteristica (
    id uuid NOT NULL,
    categoria_id uuid,
    nome character varying(255) NOT NULL,
    obrigatoria boolean,
    ordem integer,
    permite_multiplos boolean,
    tipo_valor character varying(255),
    unidade character varying(255)
);

CREATE TABLE public.categoria_caracteristica (
    id uuid NOT NULL,
    icone character varying(255),
    nome character varying(255) NOT NULL,
    ordem integer
);

CREATE TABLE public.denuncia (
    id uuid NOT NULL,
    anuncio_id uuid,
    denunciado_em timestamp(6) without time zone,
    denunciante_id uuid,
    descricao text,
    motivo character varying(255),
    resolvido_em timestamp(6) without time zone,
    resolvido_por uuid,
    status character varying(255)
);

CREATE TABLE public.documento_legal (
    id uuid NOT NULL,
    conteudo text,
    publicado_em timestamp(6) without time zone,
    tipo character varying(255),
    versao character varying(255)
);

CREATE TABLE public.endereco (
    id uuid NOT NULL,
    bairro character varying(255),
    cep character(8),
    cidade character varying(255) NOT NULL,
    complemento character varying(255),
    estado character varying(255) NOT NULL,
    latitude double precision,
    logradouro character varying(255) NOT NULL,
    longitude double precision,
    numero character varying(255)
);

CREATE TABLE public.favorito (
    id uuid NOT NULL,
    adicionado_em timestamp(6) without time zone,
    anuncio_id uuid,
    usuario_id uuid
);

CREATE TABLE public.foto_imovel (
    id uuid NOT NULL,
    capa boolean,
    criado_em timestamp(6) without time zone,
    imovel_id uuid,
    ordem integer,
    url character varying(255)
);

CREATE TABLE public.imovel (
    id uuid NOT NULL,
    aceita_pet boolean,
    andar integer,
    area_construida double precision,
    area_total double precision,
    atualizado_em timestamp(6) without time zone,
    banheiros integer,
    criado_em timestamp(6) without time zone,
    descricao text,
    endereco_id uuid NOT NULL,
    locador_id uuid NOT NULL,
    mobiliado boolean,
    quartos integer,
    suites integer,
    tipo_imovel character varying(255) NOT NULL,
    titulo character varying(255) NOT NULL,
    vagas_garagem integer,
    valor_condominio numeric(38,2),
    valor_iptu numeric(38,2)
);

CREATE TABLE public.imovel_caracteristica (
    id uuid NOT NULL,
    caracteristica_id uuid,
    imovel_id uuid,
    valor character varying(255)
);

CREATE TABLE public.locador (
    usuario_id uuid NOT NULL,
    criado_em timestamp(6) without time zone,
    documento character varying(255),
    telefone character varying(255),
    whatsapp character varying(255)
);

CREATE TABLE public.locatario (
    usuario_id uuid NOT NULL,
    criado_em timestamp(6) without time zone
);

CREATE TABLE public.notificacao (
    id uuid NOT NULL,
    anuncio_id uuid,
    criado_em timestamp(6) without time zone,
    lida boolean,
    mensagem text,
    tipo character varying(255),
    usuario_id uuid
);

CREATE TABLE public.role (
    id uuid NOT NULL,
    descricao text,
    nome character varying(255) NOT NULL
);

CREATE TABLE public.sessao (
    id uuid NOT NULL,
    criado_em timestamp(6) without time zone,
    expira_em timestamp(6) without time zone,
    ip character varying(255),
    refresh_token_hash character varying(255) NOT NULL,
    revogado_em timestamp(6) without time zone,
    user_agent character varying(255),
    usuario_id uuid NOT NULL
);

CREATE TABLE public.tokens_recuperacao (
    id uuid NOT NULL,
    data_expiracao timestamp(6) without time zone NOT NULL,
    token character varying(255) NOT NULL,
    usado boolean NOT NULL,
    usuario_id uuid NOT NULL
);

CREATE TABLE public.usuario (
    id uuid NOT NULL,
    atualizado_em timestamp(6) without time zone,
    criado_em timestamp(6) without time zone,
    email character varying(255) NOT NULL,
    email_verificado_em timestamp(6) without time zone,
    foto_perfil character varying(255),
    nome character varying(255) NOT NULL,
    perfil character varying(255) NOT NULL,
    senha_hash character varying(255) NOT NULL,
    status character varying(255) NOT NULL,
    CONSTRAINT usuario_perfil_check CHECK (((perfil)::text = ANY ((ARRAY['ROLE_USER'::character varying, 'ROLE_ADMIN'::character varying])::text[]))),
    CONSTRAINT usuario_status_check CHECK (((status)::text = ANY ((ARRAY['ATIVO'::character varying, 'INATIVO'::character varying, 'BLOQUEADO'::character varying])::text[])))
);

ALTER TABLE ONLY public.aceite_documento
    ADD CONSTRAINT aceite_documento_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.anuncio
    ADD CONSTRAINT anuncio_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.avaliacao_anuncio
    ADD CONSTRAINT avaliacao_anuncio_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.caracteristica
    ADD CONSTRAINT caracteristica_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.categoria_caracteristica
    ADD CONSTRAINT categoria_caracteristica_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.denuncia
    ADD CONSTRAINT denuncia_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.documento_legal
    ADD CONSTRAINT documento_legal_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.endereco
    ADD CONSTRAINT endereco_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.favorito
    ADD CONSTRAINT favorito_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.foto_imovel
    ADD CONSTRAINT foto_imovel_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.imovel_caracteristica
    ADD CONSTRAINT imovel_caracteristica_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.imovel
    ADD CONSTRAINT imovel_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.locador
    ADD CONSTRAINT locador_pkey PRIMARY KEY (usuario_id);

ALTER TABLE ONLY public.locatario
    ADD CONSTRAINT locatario_pkey PRIMARY KEY (usuario_id);

ALTER TABLE ONLY public.notificacao
    ADD CONSTRAINT notificacao_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.role
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.sessao
    ADD CONSTRAINT sessao_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.tokens_recuperacao
    ADD CONSTRAINT tokens_recuperacao_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT uk5171l57faosmj8myawaucatdw UNIQUE (email);

ALTER TABLE ONLY public.caracteristica
    ADD CONSTRAINT ukbiog1y3buqo14k94egm3c8mmt UNIQUE (nome);

ALTER TABLE ONLY public.categoria_caracteristica
    ADD CONSTRAINT ukf4or871jidp9ujtc8sntf9rk4 UNIQUE (nome);

ALTER TABLE ONLY public.role
    ADD CONSTRAINT ukpsbnsrja0jvuncak7b0sqo2fi UNIQUE (nome);

ALTER TABLE ONLY public.tokens_recuperacao
    ADD CONSTRAINT uks4i6nt7mgvu23pp28vktkbnpo UNIQUE (token);

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.anuncio
    ADD CONSTRAINT fk2gehyhigf82yfhompna0t82pe FOREIGN KEY (imovel_id) REFERENCES public.imovel(id);

ALTER TABLE ONLY public.tokens_recuperacao
    ADD CONSTRAINT fkdpsyi72pbihhlxhxelqinaljk FOREIGN KEY (usuario_id) REFERENCES public.usuario(id);

ALTER TABLE ONLY public.anuncio_tags
    ADD CONSTRAINT fklass9xlm13tu8lytx61els729 FOREIGN KEY (anuncio_id) REFERENCES public.anuncio(id);

