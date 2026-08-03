# 🏢 ValeMorar: Plataforma Web para Intermediações de Aluguéis Residenciais no Vale do Jequitinhonha

> **Trabalho de Conclusão de Curso (TCC I & II)**  
> Curso de Bacharelado / Tecnologia em Análise e Desenvolvimento de Sistemas  
> **Instituto Federal do Norte de Minas Gerais (IFNMG) - Campus Araçuaí**  
> 🗓️ **Ano:** 2026

---

## 👨‍💻 Autores & Orientadores

* **Autores:** 
  * Roberth Neves Messias
  * Álef Francisco Ribeiro Amaral
* **Orientadora:** Profa. Mestra Ana Carolina Rodrigues
* **Coorientador:** Breno Gonçalves Cota *(Engenheiro de Software Sênior)*

---

## 📌 Sobre o Projeto

O mercado de locação residencial do **Vale do Jequitinhonha** ainda é caracterizado pela divulgação descentralizada de imóveis, realizada principalmente por meio de redes sociais, aplicativos de mensagens e anúncios informais. Essa falta de padronização dificulta o acesso às informações por proprietários e potenciais locatários (estudantes, trabalhadores e novos moradores da região).

O **ValeMorar** nasce como uma solução tecnológica desenvolvida sob medida para a realidade regional, com o objetivo de centralizar a oferta de imóveis, otimizar a busca por moradia, ampliar a visibilidade para locadores e fortalecer a economia dos municípios do Vale.

---

## 🎯 Objetivos

### Objetivo Geral
Desenvolver o **Produto Mínimo Viável (MVP)** de uma plataforma web destinada à divulgação e gerenciamento de imóveis disponíveis para locação no Vale do Jequitinhonha, visando facilitar o acesso às informações e organizar o mercado imobiliário regional.

### Objetivos Específicos
* 📚 Realizar levantamento bibliográfico sobre o mercado imobiliário regional e engenharia de software aplicada.
* 🔎 Analisar plataformas digitais existentes no setor para identificar lacunas e oportunidades.
* 📝 Levantar e documentar os Requisitos Funcionais (RF) e Não Funcionais (RNF) da plataforma.
* 📐 Modelar a arquitetura da solução por meio de Diagramas de Casos de Uso, Diagrama de Classes e MER.
* 💻 Desenvolver e validar o MVP contemplando o cadastro de usuários, publicação de anúncios, buscas e interações.

---

## 🛠️ Tecnologias Utilizadas

O ecossistema do **ValeMorar** foi projetado seguindo as melhores práticas da Engenharia de Software e arquitetura em camadas.

### **Backend**
* **Linguagem / Framework:** Java 17+ / Spring Boot 3
* **Persistência & ORM:** Spring Data JPA / Hibernate
* **Segurança:** Spring Security + JWT
* **Produtividade & Utilitários:** Lombok
* **Identificadores:** UUID v4 (para segurança das entidades)

### **Banco de Dados**
* **SGBD:** PostgreSQL

### **Frontend** *(Em desenvolvimento)*
* **Tecnologias:** HTML5, CSS3, JavaScript / Framework Reativo (React ou Vue)

---

## 🗂️ Estrutura do Domínio (Entidades da Aplicação)

A aplicação conta com uma modelagem relacional completa estruturada no pacote `br.com.valemorar.domain`:

| Módulo | Entidades Mapeadas |
| :--- | :--- |
| **Usuários e Acessos** | `Usuario`, `Role`, `Sessao` |
| **Perfis e Termos** | `Locador`, `Locatario`, `DocumentoLegal`, `AceiteDocumento` |
| **Endereçamento** | `Endereco` |
| **Imóveis** | `Imovel`, `FotoImovel`, `CategoriaCaracteristica`, `Caracteristica`, `ImovelCaracteristica` |
| **Anúncios e Negócio** | `Anuncio`, `AvaliacaoAnuncio`, `Favorito`, `Notificacao`, `Denuncia` |

---

## 🚀 Como Executar o Projeto Localmente

### Pré-requisitos
* **Java SDK 17** ou superior instalado.
* **Maven** configurado.
* Banco de Dados **PostgreSQL** rodando.

### Passo a passo

1. **Clonar o repositório:**
   ```bash
   git clone [https://github.com/SEU_USUARIO/valemorar.git](https://github.com/SEU_USUARIO/valemorar.git)
   cd valemorar
