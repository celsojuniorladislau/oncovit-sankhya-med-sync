# Project: Oncovit Sankhya Med Sync

## Architecture
Microservice architecture simulating Sankhya ERP business rules for cold-chain and oncology pharmaceutical stock management and traceability, paired with thermal labeling, relational database queries, candidate presentation kit, and remote deployment on VPS 2.

### System Components:
1. **Core Spring Boot Microservice (`oncovit-sankhya-med-sync`)**:
   - Runtime: Java 17 LTS / Spring Boot 3.3.x.
   - Layers: Controller (REST API & Swagger/OpenAPI), Service (Business rules, FEFO, lot validation, ZPL generation, ERP sync), Repository (Spring Data JPA), Domain (Entities, Enums, DTOs).
   - Storage/DB: Dual-profile strategy: Embedded H2 (PostgreSQL compatibility mode) for automated builds/testing + PostgreSQL/SQL scripts for production auditing.
   - Open Endpoints:
     - `GET /api/medicamentos`: List medications with storage class and temperature thresholds.
     - `GET /api/lotes/criticos`: List lots expiring within 60 days (sorted by expiration date ascending, FEFO).
     - `GET /api/lotes/{id}/etiqueta`: Output ZPL II thermal label code + structured JSON payload for thermal printers.
     - `POST /api/integracao/sankhya/baixa-estoque`: Webhook/API endpoint simulating Sankhya ERP inventory deduction with validation, error codes (`OUT_OF_STOCK`, `EXPIRED_LOT_REJECTED`), and audit logging.
     - `GET /swagger-ui/index.html` & `GET /v3/api-docs`: Interactive OpenAPI documentation.
     - `GET /actuator/health`: Health status.

2. **Relational Database & Audit SQL (`database/`)**:
   - `01_schema_ddl.sql`: DDL for tables `medicamentos`, `lotes`, `movimentacoes`, `sankhya_sync_log` with check constraints and foreign keys.
   - `02_seed_dml.sql`: Realistic seed data with high-complexity oncology mAbs (Trastuzumabe, Pembrolizumabe, Rituximabe) and hospital injectables, cold chain (2°C-8°C) and ambient (15°C-25°C).
   - `03_advanced_queries.sql`:
     - Query 1: Imminent expiration (< 60 days) report with days-to-expiry and financial loss risk.
     - Query 2: Quantitative and financial aggregation grouped by storage type (Cadeia de Frio vs Ambiente).

3. **Candidate Dossier & Presential Strategy (`dossie-candidatura/`)**:
   - `01_curriculo_otimizado_gupy.md`: ATS-optimized resume emphasizing Java, POO, SQL, Git, REST, Sankhya ERP, and UVV.
   - `02_guia_abordagem_presencial.md`: Step-by-step physical approach guide (Rua José Vivacqua, 645, Bairro Jabour, Vitória-ES), receptionist script, 60-second elevator pitch to the IT Manager showcasing the deployed API.
   - `03_guia_teste_tecnico_resolvido.md`: Comprehensive technical test prep with 6 solved questions (3 Java/POO/Streams/Exception handling + 3 SQL DDL/Date queries/Aggregations).

4. **VPS 2 Deployment & Automation (`deploy/`)**:
   - Host: `vps2` (Ubuntu 26.04.1 LTS x86_64, root access, tmux 3.4).
   - Execution in persistent tmux session: `tmux new-session -d -s oncovit-api ...`.
   - Exposed port: HTTP 8080 (UFW allowed), live Swagger UI accessible via browser/curl.

---

## Code Layout
```
oncovit-sankhya-med-sync/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .mvn/wrapper/
├── src/
│   ├── main/
│   │   ├── java/com/oncovit/medsync/
│   │   │   ├── OncovitMedSyncApplication.java
│   │   │   ├── domain/
│   │   │   │   ├── enums/ (TipoArmazenamento, StatusLote, TipoMovimentacao, StatusIntegracao)
│   │   │   │   ├── model/ (Medicamento, Lote, Movimentacao, SankhyaSyncLog)
│   │   │   ├── dto/ (MedicamentoDTO, LoteDTO, EtiquetaDTO, BaixaEstoqueRequest, BaixaEstoqueResponse)
│   │   │   ├── repository/ (MedicamentoRepository, LoteRepository, MovimentacaoRepository, SankhyaSyncLogRepository)
│   │   │   ├── service/ (MedicamentoService, LoteService, EtiquetaZplService, SankhyaIntegracaoService)
│   │   │   ├── controller/ (MedicamentoController, LoteController, IntegracaoSankhyaController)
│   │   │   ├── config/ (OpenApiConfig, DataLoaderConfig)
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── schema.sql
│   │       └── data.sql
│   └── test/
│       └── java/com/oncovit/medsync/
│           ├── OncovitMedSyncApplicationTests.java
│           ├── domain/LoteBusinessRulesTest.java
│           ├── service/LoteServiceTest.java
│           ├── service/EtiquetaZplServiceTest.java
│           ├── service/SankhyaIntegracaoServiceTest.java
│           └── controller/ApiControllerIntegrationTest.java
├── database/
│   ├── 01_schema_ddl.sql
│   ├── 02_seed_dml.sql
│   ├── 03_advanced_queries.sql
│   └── README.md
├── dossie-candidatura/
│   ├── 01_curriculo_otimizado_gupy.md
│   ├── 02_guia_abordagem_presencial.md
│   ├── 03_guia_teste_tecnico_resolvido.md
│   └── README.md
└── deploy/
    ├── deploy_vps2.sh
    ├── verify_vps2.sh
    └── README.md
```

---

## Feature Inventory
| # | Feature | Description | Milestone | Source |
|---|---------|-------------|-----------|--------|
| 1 | Java Spring Boot App Skeleton | Maven build with Java 17, Spring Boot 3.3, Springdoc OpenAPI, H2, JPA | M1 | ORIGINAL_REQUEST §R1 |
| 2 | Oncology & Hospital POO Domain | Classes `Medicamento`, `Lote`, `Movimentacao` with storage classes and lifecycle validation | M1 | ORIGINAL_REQUEST §R1 |
| 3 | REST API `GET /api/medicamentos` | List all medications with storage constraints and temperature range | M1 | ORIGINAL_REQUEST §R1, Acceptance Criteria |
| 4 | REST API `GET /api/lotes/criticos` | List lots with expiration < 60 days, ordered by expiry date (FEFO) | M1 | ORIGINAL_REQUEST §R1, Acceptance Criteria |
| 5 | Swagger / OpenAPI Documentation | Interactive Swagger UI available at `/swagger-ui/index.html` | M1 | ORIGINAL_REQUEST §R1, Acceptance Criteria |
| 6 | Unit & Integration Test Suite | Comprehensive unit tests for business rules, lot validation, and endpoints | M1 | ORIGINAL_REQUEST Acceptance Criteria |
| 7 | Relational DDL Schema Script | Complete table creation script with PKs, FKs, and check constraints (`01_schema_ddl.sql`) | M1 | ORIGINAL_REQUEST §R2 |
| 8 | Realistic DML Pharmaceutical Data | DML seed script with realistic oncology mAbs, hospital injectables, cold chain & ambient | M1 | ORIGINAL_REQUEST §R2 |
| 9 | Advanced SQL Query: Expiration < 60d | Audit query filtering lots with expiry < 60 days, ordered by urgency, showing value at risk | M1 | ORIGINAL_REQUEST §R2, Acceptance Criteria |
| 10 | Advanced SQL Query: Storage Breakdown | Audit query aggregating volume and financial value by storage type (Cold Chain vs Ambient) | M1 | ORIGINAL_REQUEST §R2 |
| 11 | Thermal Label ZPL II Generation | Generate ZPL II format (203 DPI, 100x60mm) with inverted temp badge, Code128, and DataMatrix | M1 | ORIGINAL_REQUEST §R3 |
| 12 | Thermal Label JSON Export | Endpoint `GET /api/lotes/{id}/etiqueta` returning both ZPL and structured JSON | M1 | ORIGINAL_REQUEST §R3, Acceptance Criteria |
| 13 | Sankhya ERP Baixa de Estoque Webhook | Endpoint `POST /api/integracao/sankhya/baixa-estoque` simulating ERP stock deduction | M1 | ORIGINAL_REQUEST §R3 |
| 14 | Sankhya Stock Validation & Audit Log | Prevent deduction of expired/unreleased lots, prevent negative stock, log to `sankhya_sync_log` | M1 | ORIGINAL_REQUEST §R3 |
| 15 | ATS-Optimized Gupy Curriculum | Resume formatted with required keywords (Java, POO, SQL, Git, REST, Sankhya, UVV) | M2 | ORIGINAL_REQUEST §R4, Acceptance Criteria |
| 16 | Presential Approach Guide | Guide with Jabour/Vitória address, reception speech, and 60-second IT manager pitch | M2 | ORIGINAL_REQUEST §R4, Acceptance Criteria |
| 17 | Technical Test Preparation Guide | Solved questions of Java (POO/Streams/Exceptions) and SQL (DDL/Dates/Aggregations) | M2 | ORIGINAL_REQUEST §R4 |
| 18 | VPS 2 Remote Synchronization | Automated synchronization of codebase/JAR to `vps2` target directory | M3 | ORIGINAL_REQUEST §R5 |
| 19 | Persistent tmux Session on VPS 2 | Run application in persistent session `tmux new -s oncovit-api` | M3 | ORIGINAL_REQUEST §R5, Acceptance Criteria |
| 20 | Remote HTTP & Swagger Verification | Open port 8080 in UFW, verify HTTP 200 on Swagger UI and API endpoints | M3 | ORIGINAL_REQUEST §R5, Acceptance Criteria |
| 21 | E2E Regression & Adversarial Gate | Pass 100% of all E2E tests and forensic integrity verification | Final | ORIGINAL_REQUEST Acceptance Criteria |

---

## Milestones
| # | Name | Scope | Dependencies | Status |
|---|------|-------|-------------|--------|
| M1 | Core System (Spring Boot API, Relational DB, ZPL & Sankhya) | Features 1 to 14 (R1, R2, R3) | none | DONE |
| M2 | Candidate Dossier & Approach Guide | Features 15, 16, 17 (R4) | none | DONE |
| M3 | VPS 2 tmux Deployment & Verification | Features 18, 19, 20 (R5) | M1 | DONE |
| Final | 100% E2E Pass & Adversarial Hardening | Feature 21 (Acceptance criteria) | M1, M2, M3 | DONE |

---

## Interface Contracts

### 1. `MedicamentoController` ↔ Client / Frontend
- `GET /api/medicamentos`
  - Response: `200 OK`
  - Body: Array of `MedicamentoDTO`
  ```json
  [
    {
      "id": 1,
      "codigoSankhya": "MED-ONC-001",
      "nomeComercial": "Herceptin 440mg",
      "principioAtivo": "Trastuzumabe",
      "categoria": "ONCOLOGICO",
      "tipoArmazenamento": "CADEIA_DE_FRIO",
      "temperaturaMinima": 2.0,
      "temperaturaMaxima": 8.0,
      "unidadeMedida": "FRASCO_AMPOLA",
      "totalEstoqueDisponivel": 85
    }
  ]
  ```

### 2. `LoteController` ↔ Client / Logística
- `GET /api/lotes/criticos`
  - Response: `200 OK`
  - Body: Array of `LoteDTO` (lots where `dataValidade <= CURRENT_DATE + 60 days` and `status = 'LIBERADO'`, ordered by `dataValidade ASC`)
  ```json
  [
    {
      "id": 10,
      "numeroLote": "LOT-TRA-2026A",
      "medicamentoId": 1,
      "nomeMedicamento": "Herceptin 440mg (Trastuzumabe)",
      "dataFabricacao": "2025-11-15",
      "dataValidade": "2026-11-15",
      "diasParaVencimento": 27,
      "status": "LIBERADO",
      "quantidadeDisponivel": 30,
      "tipoArmazenamento": "CADEIA_DE_FRIO",
      "alertaCritico": true
    }
  ]
  ```
- `GET /api/lotes/{id}/etiqueta`
  - Response: `200 OK`
  - Body: `EtiquetaDTO`
  ```json
  {
    "loteId": 10,
    "numeroLote": "LOT-TRA-2026A",
    "nomeMedicamento": "Herceptin 440mg",
    "principioAtivo": "Trastuzumabe",
    "tipoArmazenamento": "CADEIA_DE_FRIO",
    "faixaTemperatura": "2°C a 8°C (NÃO CONGELAR)",
    "dataFabricacao": "2025-11-15",
    "dataValidade": "2026-11-15",
    "codigoBarras": "7891234567890",
    "dadosQrCode": "https://oncovit.com.br/rastreio/LOT-TRA-2026A",
    "formato": "ZPL_II_203DPI",
    "conteudoZpl": "^XA^PW800^LL480^CI28...^XZ"
  }
  ```

### 3. `IntegracaoSankhyaController` ↔ Sankhya ERP
- `POST /api/integracao/sankhya/baixa-estoque`
  - Headers:
    - `X-Sankhya-AppKey`: string (required)
    - `X-Sankhya-Token`: string (required)
    - `Content-Type`: `application/json`
  - Request Body:
  ```json
  {
    "codigoSankhyaProduto": "MED-ONC-001",
    "numeroLote": "LOT-TRA-2026A",
    "quantidade": 5,
    "numeroNota": 105420,
    "tipoOperacao": "SAIDA_VENDA_HOSPITALAR",
    "cnpjCliente": "01.234.567/0001-89",
    "motivo": "Atendimento pedido hospitalar Oncovit"
  }
  ```
  - Response Success: `200 OK`
  ```json
  {
    "status": "SUCCESS",
    "protocolo": "SNK-20261019-89421",
    "mensagem": "Baixa de estoque confirmada com sucesso no Oncovit Med Sync.",
    "saldoRestanteLote": 25,
    "dataHoraProcessamento": "2026-10-19T18:55:00Z"
  }
  ```
  - Response Errors:
    - `400 Bad Request`: Payload missing required fields.
    - `401 Unauthorized`: Invalid or missing Sankhya AppKey / Token.
    - `404 Not Found`: Product or Lot not found.
    - `409 Conflict`: Insufficient stock (`OUT_OF_STOCK`).
    - `422 Unprocessable Entity`: Lot expired or status not `LIBERADO` (`EXPIRED_LOT_REJECTED` or `LOT_NOT_RELEASED`).
