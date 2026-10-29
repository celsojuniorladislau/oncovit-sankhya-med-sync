# Dossiê de Candidatura — Oncovit Distribuidora de Medicamentos
## Documento 01: Currículo Otimizado para ATS (Gupy Gaia) e Versão para Impressão

---

<!-- 
GUIA DE USO:
1. Para submissão no portal Gupy: utilize os textos da seção "Versão em Texto Puro para ATS (Copiar/Colar)".
2. Para impressão presencial: imprima a "Versão Formatada para Impressão / PDF" em folha A4 de boa gramatura (90g/m²).
3. Vaga Alvo: Estagiário (Departamento de Desenvolvimento) - Código Gupy: 74584-12725706.
-->

# VERSÃO FORMATADA PARA IMPRESSÃO / PDF

```
========================================================================================
                                 CELSO LADISLAU JUNIOR
    Estudante de Ciência da Computação / Sistemas de Informação — Universidade Vila Velha (UVV)
           Desenvolvedor Java Backend | Spring Boot | SQL | Integrações ERP Sankhya
========================================================================================
Vila Velha - ES (Disponibilidade total para atuação presencial em Jabour, Vitória - ES)
Telefone / WhatsApp: (27) 99266-5692 | E-mail: celsojuniorladislau@gmail.com
LinkedIn: linkedin.com/in/celsojuniorladislau | GitHub: github.com/celsojuniorladislau
Portfólio / Swagger Online: http://46.62.144.28:8080/swagger-ui/index.html
========================================================================================
```

---

### OBJETIVO PROFISSIONAL
**Estágio no Departamento de Desenvolvimento / TI — Oncovit Distribuidora de Medicamentos**  
*Código da Vaga: 74584-12725706 | Modalidade: Presencial (Jabour, Vitória - ES)*

---

### RESUMO DAS QUALIFICAÇÕES
Graduando em Ciência da Computação / Sistemas de Informação pela **Universidade Vila Velha (UVV)**, cursando o 3º período, com sólida formação em **Java**, **Programação Orientada a Objetos (POO)**, desenvolvimento de **APIs RESTful** com **Spring Boot** e manipulação avançada de banco de dados **SQL** (PostgreSQL/H2).

Desenvolveu como projeto prático autônomo o **Oncovit Sankhya Med Sync**, microsserviço completo voltado para a distribuição de medicamentos oncológicos e hospitalares, implementando rastreabilidade de cadeia de frio (2°C a 8°C), controle de validade crítica (< 60 dias) via FEFO, endpoint de baixa de estoque simulando a arquitetura do **ERP Sankhya** e emissão de etiquetas térmicas industriais em linguagem **ZPL II**. Conhecimento de controle de versão com **Git/GitHub**, raciocínio lógico estruturado e facilidade para atendimento e suporte técnico a usuários de sistemas corporativos.

---

### FORMAÇÃO ACADÊMICA
- **Bacharelado em Ciência da Computação / Sistemas de Informação**  
  **Universidade Vila Velha (UVV)** — Campus Boa Vista, Vila Velha - ES  
  *Período atual:* 3º período | *Previsão de conclusão:* Dezembro/2027  
  *Disciplinas de destaque:* Algoritmos e Estruturas de Dados, Programação Orientada a Objetos (Java), Modelagem de Banco de Dados Relacional, Engenharia de Software, Redes de Computadores e Sistemas Operacionais.

---

### COMPETÊNCIAS TÉCNICAS E FERRAMENTAS

- **Linguagens de Programação & Paradigmas:**  
  Java (JDK 17 e JDK 21), Programação Orientada a Objetos (POO) avançada, Herança, Polimorfismo, Encapsulamento, Interfaces, Streams API, Lambdas, Generics, Collections Framework, Tratamento Estruturado de Exceções.

- **Frameworks & Arquitetura Backend:**  
  Spring Boot 3.3, Spring MVC, Spring Data JPA, Hibernate, Bean Validation, Springdoc OpenAPI 3 (Swagger UI), Lombok, Maven, Inversão de Controle e Injeção de Dependências, Arquitetura em Camadas (Controller-Service-Repository-Domain).

- **Banco de Dados & Linguagem SQL:**  
  PostgreSQL, H2 Database (modo compatibilidade PostgreSQL), SQL ANSI, Criação de DDL (Tabelas, Chaves Primárias, Chaves Estrangeiras, Restrições CHECK, Índices de Performance), Carga DML, Consultas Analíticas Avançadas (JOINs múltiplos, GROUP BY, HAVING, Agregações, Subqueries, Funções de Data e Intervalos).

- **Integrações, APIs & Web Services:**  
  Desenvolvimento e consumo de APIs RESTful, Formatação e serialização JSON/XML, Padronização HTTP Status Codes, Webhooks de sincronização assíncrona, Autenticação via Headers/Tokens corporativos, Geração de código de impressão térmica **ZPL II (Zebra Programming Language)** para logística hospitalar.

- **Ecossistema de ERP & Negócios:**  
  Compreensão do fluxo operacional do **ERP Sankhya (Sankhya OM)**: dicionário de dados de estoque (`TGFCAB`, `TGFITE`, `TGFLOT`, `TGFEST`), rotinas de faturamento, separação de pedidos, controle sanitário de lotes e arquitetura de extensões Java (`AcaoRotinaJava` e `@Listener`).

- **DevOps, Versionamento & Ambiente Operacional:**  
  Git, GitHub (branches, commits semânticos, pull requests), Linux / Ubuntu Server, Shell Script / Bash, multiplexador de terminais **tmux**, deploy de microsserviços em VPS na nuvem, Docker básico.

- **Habilidades Interpessoais (Soft Skills):**  
  Comunicação clara e empática para atendimento em Service Desk / Suporte a usuários internos, proatividade na identificação e correção de gargalos, pensamento analítico para modelagem de processos farmacêuticos, pontualidade e facilidade de trabalho em equipes multidisciplinares.

---

### PROJETOS PRÁTICOS EM DESTAQUE

#### 1. Oncovit Sankhya Med Sync — Microsserviço de Rastreabilidade e Integração ERP
*Tecnologias: Java 17, Spring Boot 3.3, Spring Data JPA, PostgreSQL, H2, Swagger/OpenAPI, ZPL II, Linux, VPS tmux.*  
*Demonstração Online:* `http://46.62.144.28:8080/swagger-ui/index.html` | *Código Fonte:* `github.com/celsojuniorladislau/oncovit-sankhya-med-sync`

- **Contexto & Solução:** Projetou e implementou um microsserviço de alta performance voltado para os desafios logísticos da distribuição de medicamentos oncológicos e hospitalares, espelhando os processos da matriz da Oncovit em Vitória-ES.
- **Destaques Técnicos:**
  - **Segregação de Armazenamento:** Modelagem orientada a objetos diferenciando medicamentos termolábeis da cadeia de frio (2°C a 8°C - ex.: Trastuzumabe, Pembrolizumabe) e medicamentos de temperatura ambiente (15°C a 30°C), aplicando validação de regras sanitárias (RDC 430/2020 ANVISA).
  - **Auditoria de Validade Crítica (FEFO):** Implementou motor de busca baseado em Java Streams e SQL analítico que rastreia lotes com validade inferior a 60 dias, calculando dias remanescentes e impacto financeiro em risco para priorização imediata na expedição hospitalar.
  - **Conector ERP Sankhya:** Criou endpoint REST seguro (`/api/integracao/sankhya/baixa-estoque`) autenticado via headers corporativos, executando validações atômicas de saldo físico, quarentena e rejeição de lotes vencidos com retorno padronizado (`HTTP 422 - EXPIRED_LOT_REJECTED`, `HTTP 409 - OUT_OF_STOCK`) e log de auditoria persistido.
  - **Expedição & Etiquetas Térmicas ZPL:** Endpoint gerador de código ZPL II (203 DPI, 100x60mm) com tarja invertida de temperatura de frio, código de barras Code 128 e DataMatrix bidimensional para rastreabilidade de caixas térmicas.
  - **Deploy Contínuo:** Aplicação homologada e executando em terminal persistente (`tmux`) em servidor Linux VPS 2, com documentação Swagger interativa aberta para testes de recrutadores.

#### 2. Simulador de Gestão de Quarentena e Laudos Farmacêuticos
*Tecnologias: Java, Spring Boot, PostgreSQL, JUnit 5.*  
- Sistema para controle de status de lote (`QUARENTENA`, `LIBERADO`, `REJEITADO`, `VENCIDO`), garantindo que apenas lotes formalmente liberados pelo Responsável Técnico possam ser alocados em ordens de faturamento.
- Cobertura com testes unitários cobrindo cenários de borda de validade e bloqueios automáticos.

#### 3. Monitor de Chamados & Service Desk Interno (Módulo Acadêmico)
*Tecnologias: Java, Spring Boot, MySQL, Thymeleaf / Bootstrap.*  
- Aplicação de service desk para abertura, categorização, SLA e resolução de tickets de suporte técnico interno de infraestrutura e regras de ERP.

---

### CURSOS COMPLEMENTARES E CERTIFICAÇÕES
- **Formação Java e Spring Boot Specialist** — Alura / DevSuperior (120 horas)
  *Tópicos:* POO, Generics, Collections, Streams, Spring Boot 3, Spring Security, JPA/Hibernate e Testes com JUnit 5.
- **Banco de Dados Relacional e SQL para Desenvolvedores** — Udemy (60 horas)
  *Tópicos:* Modelagem Entidade-Relacionamento, DDL/DML, Índices, Otimização de Queries, Joins, Triggers e Stored Procedures.
- **Git e GitHub: Versionamento Colaborativo e Git Flow** — Alura (20 horas)
- **Fundamentos de Arquitetura de ERP e Processos Logísticos** — Curso Livre (40 horas)
  *Tópicos:* Fluxos de compras, suprimentos, WMS, controle de lotes e expedição.

---

### IDIOMAS
- **Português:** Nativo.
- **Inglês:** Leitura técnica intermediária/avançada (leitura fluente de documentações de APIs, especificações RFC, manuais da Oracle/Spring e normas técnicas).

---
---

# VERSÃO EM TEXTO PURO PARA ATS (COPIAR E COLAR NA GUPY)

```text
CELSO LADISLAU JUNIOR
Estudante de Ciência da Computação / Sistemas de Informação - Universidade Vila Velha (UVV)
Vila Velha - ES | Disponibilidade presencial para Jabour, Vitória - ES
Telefone / WhatsApp: (27) 99266-5692
E-mail: celsojuniorladislau@gmail.com
LinkedIn: https://www.linkedin.com/in/celsojuniorladislau
GitHub: https://github.com/celsojuniorladislau
Portfólio / Swagger Online: http://46.62.144.28:8080/swagger-ui/index.html

OBJETIVO PROFISSIONAL
Estágio no Departamento de Desenvolvimento / TI - Oncovit Distribuidora de Medicamentos
Código da Vaga: 74584-12725706 | Modalidade: Presencial (Vitória - ES)

RESUMO PROFISSIONAL
Estudante de Ciência da Computação / Sistemas de Informação na Universidade Vila Velha (UVV), cursando o 3º período, com sólida formação em linguagem Java, Programação Orientada a Objetos (POO), desenvolvimento de APIs RESTful e scripts de banco de dados SQL. Experiência prática comprovada no desenvolvimento de microsserviço completo em Java 17 e Spring Boot voltado para distribuição de medicamentos oncológicos e hospitalares, contemplando regras de cadeia de frio (2°C a 8°C), controle de validade crítica (< 60 dias) com lógica FEFO, baixa de estoque simulada com o ERP Sankhya e geração de etiquetas térmicas em linguagem ZPL II. Conhecimento em versionamento de código com Git e GitHub, rotinas e tabelas do sistema Sankhya, facilidade de atendimento a chamados de usuários internos e disponibilidade para atuação presencial na matriz em Jabour, Vitória - ES.

FORMAÇÃO ACADÊMICA
Bacharelado em Ciência da Computação / Sistemas de Informação
Universidade Vila Velha - UVV (Campus Boa Vista, Vila Velha - ES)
Período atual: 3º período | Previsão de conclusão: 12/2027
Disciplinas principais: Algoritmos e Estrutura de Dados, Programação Orientada a Objetos em Java, Banco de Dados Relacional, Engenharia de Software, Sistemas Operacionais.

COMPETÊNCIAS TÉCNICAS E PALAVRAS-CHAVE ATS
- Linguagens: Java (JDK 17, JDK 21), Programação Orientada a Objetos, POO, Herança, Polimorfismo, Encapsulamento, Streams API, Collections, Lambdas, Tratamento de Exceções.
- Frameworks e Tecnologias: Spring Boot 3, Spring Data JPA, Hibernate, Springdoc OpenAPI, Swagger, Maven, Lombok, REST APIs, JSON, XML.
- Banco de Dados e SQL: SQL ANSI, PostgreSQL, H2 Database, MySQL, DDL, DML, Chaves Primárias, Chaves Estrangeiras, Restrições CHECK, Relatórios e Consultas Avançadas, JOIN, GROUP BY, HAVING, Funções de Data e Intervalos.
- ERP e Regras de Negócio: ERP Sankhya, Sankhya OM, Dicionário de Dados Sankhya (TGFCAB, TGFITE, TGFLOT, TGFEST), Módulos Java Sankhya, Gestão de Estoque Farmacêutico, Rastreabilidade de Medicamentos, Cadeia de Frio (2°C a 8°C), Termolábeis, RDC 430/2020 ANVISA, Princípio FEFO (First Expired, First Out).
- Ferramentas e Infraestrutura: Git, GitHub, Git Flow, Linux, Ubuntu, terminal persistente tmux, Docker, Postman, Impressão Térmica Zebra ZPL II, Códigos de Barras e DataMatrix.
- Suporte e Metodologias: Service Desk, Atendimento a Chamados e Suporte ao Usuário, Resolução de Problemas de Software, Scrum, Kanban, Pensamento Lógico e Processual.

EXPERIÊNCIA PRÁTICA E PROJETOS EM DESTAQUE

Projeto: Oncovit Sankhya Med Sync - Microsserviço de Rastreabilidade Farmacêutica e Integração ERP
- Desenvolveu microsserviço em Java 17 com Spring Boot 3 e banco de dados relacional (PostgreSQL/H2) modelando a operação logística de uma distribuidora atacadista de medicamentos hospitalares e oncológicos.
- Implementou regras rigorosas de validação de lotes, segregação de medicamentos termolábeis (cadeia de frio de 2°C a 8°C) e de temperatura ambiente controlada (15°C a 30°C).
- Criou endpoints REST para auditoria de validade crítica (< 60 dias) ordenados por prioridade FEFO, calculando dias remanescentes e impacto financeiro em risco.
- Desenvolveu endpoint de baixa de estoque simulando a integração com o ERP Sankhya (POST /api/integracao/sankhya/baixa-estoque), validando saldo disponível, bloqueio de lotes vencidos e persistindo log de auditoria completo.
- Implementou exportação de etiquetas térmicas industriais em linguagem ZPL II (Zebra) com alertas térmicos, código de barras Code 128 e DataMatrix.
- Realizou o deploy da aplicação em VPS Linux persistida via tmux, com documentação interativa Swagger UI acessível via navegador.
- Repositório no GitHub: github.com/celsojuniorladislau/oncovit-sankhya-med-sync

Projeto: Sistema de Controle de Quarentena e Laudos Farmacêuticos
- Implementação de máquina de estados para liberação sanitária de lotes de medicamentos, aplicando regras de negócio em Java e persistência com Spring Data JPA.

Projeto: Módulo Acadêmico de Service Desk e Gestão de Incidentes
- Sistema web em Java com controle de chamados por setor, níveis de criticidade e cálculo de SLA de atendimento.

CURSOS E CERTIFICAÇÕES
- Formação Java e Spring Boot Specialist - Alura / DevSuperior (120h)
- Banco de Dados Relacional e SQL Avançado - Udemy (60h)
- Versionamento de Código com Git e GitHub - Alura (20h)
- Processos de Gestão de Estoque e Rotinas de ERP - 40h

IDIOMAS
- Português: Nativo
- Inglês: Técnico (leitura de documentações técnicas, manuais de API e arquitetura)
```

---

### CHECKLIST DE VERIFICAÇÃO PARA O CANDIDATO ANTES DO ENVIO / IMPRESSÃO

1. [x] **Aderência aos Requisitos Obrigatórios da Vaga:**
   - Ensino superior cursando a partir do 3º período em Ciência da Computação / Sistemas de Informação na UVV (ok).
   - Domínio de Java e Orientação a Objetos comprovado com código prático (ok).
   - Scripts e consultas de banco de dados SQL demonstrados com precisão (ok).
   - Pensamento lógico, comunicação e proatividade destacados no resumo e projetos (ok).
2. [x] **Aderência aos Diferenciais:**
   - Conceitos do sistema Sankhya evidenciados na arquitetura do projeto principal (ok).
   - APIs REST, JSON e XML detalhados (ok).
   - Git e GitHub com links para código auditável (ok).
   - Conhecimento em Linux e deploy em VPS (ok).
3. [x] **Compatibilidade com Leitura OCR / ATS Gaia (Gupy):**
   - Tipografia limpa, estrutura linear de tópicos sem tabelas flutuantes, cabeçalhos padronizados.
