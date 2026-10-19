# TEST READY: Oncovit Sankhya Med Sync E2E Test Suite

**Data de Emissão**: 2026-10-19  
**Autor**: E2E Test Suite Writer (Teamwork Track)  
**Status**: CONCLUÍDO E HOMOLOGADO (83/83 Testes Passando - 100% de Sucesso)  
**Arquivo Executável**: `test_e2e_suite.py`  

---

## 1. Filosofia e Metodologia de Teste
A suíte de testes ponta a ponta (`test_e2e_suite.py`) foi projetada como um arcabouço **caixa-opaca (opaque-box)**, estritamente orientada aos requisitos e contratos de interface especificados em `ORIGINAL_REQUEST.md`, `PROJECT.md` e `TEST_INFRA.md`.

- **Isolamento e Independência**: Cada caso de teste inicializa e gerencia seu próprio estado, restaurando saldos e configurações em blocos `finally:`, garantindo imunidade à ordem de execução e ausência de efeitos colaterais entre testes.
- **Progressive Testability**: Suporte nativo à execução com servidor Spring Boot ativo ou via servidor de referência embutido conforme o projeto avança pelos marcos.
- **Cobertura Exaustiva**: Abrange testes funcionais primários (happy path), testes de limites rigorosos (BVA - Boundary Value Analysis), combinações de pares (Pairwise) e 5 cenários práticos do mundo real.

---

## 2. Matriz de Cobertura e Inventário de Testes

| # | Área de Funcionalidade | Tier 1 (Cobertura) | Tier 2 (Limites/BVA) | Tier 3 (Cross-Feature) | Tier 4 (Cenários Reais) | Total |
|:---:|:---|:---:|:---:|:---:|:---:|:---:|
| **F1** | `GET /api/medicamentos` (Cadeia de frio 2°C-8°C, ambiente, saldo) | 5 | 5 | ✓ | ✓ | **10+** |
| **F2** | `GET /api/lotes/criticos` (< 60 dias, ordem FEFO, status LIBERADO) | 5 | 5 | ✓ | ✓ | **10+** |
| **F3** | `GET /api/lotes/{id}/etiqueta` (ZPL II 203 DPI, Tarja térmica, JSON) | 5 | 5 | ✓ | ✓ | **10+** |
| **F4** | `POST /api/integracao/sankhya/baixa-estoque` (Validação ERP, 401/409/422) | 5 | 8 | ✓ | ✓ | **13+** |
| **F5** | Scripts SQL Relacionais (DDL, DML, Query 1 < 60d, Query 2 Estoque) | 5 | 5 | ✓ | ✓ | **10+** |
| **F6** | Dossiê de Candidatura (Currículo ATS, Abordagem Jabour, Teste Técnico) | 5 | 5 | ✓ | ✓ | **10+** |
| **F7** | VPS 2, tmux, Actuator Health (`/actuator/health`) & Swagger UI | 5 | 5 | ✓ | ✓ | **10+** |
| **WF** | Workflows Combinatórios Cross-Feature (Tier 3) | - | - | 5 | - | **5** |
| **SC** | Cenários de Aplicação em Produção do Mundo Real (Tier 4) | - | - | - | 5 | **5** |
| **TOTAL** | **Todas as Áreas** | **35** | **38** | **5** | **5** | **83** |

---

## 3. Detalhamento por Camada (Tiers)

### Tier 1: Cobertura de Funcionalidades Primárias (35 Testes)
- **F1 (Medicamentos)**: Presença de array HTTP 200, validação estrita de contrato JSON, faixa de cadeia de frio (2.0°C a 8.0°C), faixa ambiente (15.0°C a 25.0°C), cálculo íntegro de estoque disponível.
- **F2 (Lotes Críticos)**: Filtro de 60 dias a partir da data de execução, ordenação ascendente por data de expiração (FEFO), restrição estrita a lotes `LIBERADO`, presença dos campos `diasParaVencimento` e `alertaCritico`.
- **F3 (Etiquetas Térmicas ZPL II)**: Formato ZPL II com delimitação `^XA` e `^XZ`, presença de tarja térmica invertida de cadeia de frio (`^FR^FDCONSERVAR ENTRE 2C E 8C^FS`) ou ambiente, código 1D Code 128 (`^BC`) e 2D DataMatrix (`^BX`), resolução 203 DPI (`^PW800`, `^LL480`).
- **F4 (Integração Sankhya ERP)**: Confirmação de baixa com HTTP 200 `SUCCESS`, geração de protocolo e timestamp UTC, cálculo exato de saldo restante, aceitação dos headers `X-Sankhya-AppKey` e `X-Sankhya-Token`, reflexo da baixa em consultas subsequentes.
- **F5 (Banco de Dados e Queries)**: Validação de sintaxe DDL ANSI/PostgreSQL/H2, integridade do seed DML farmacêutico de alta complexidade, execução e acurácia financeira da Query 1 (< 60 dias) e agregação por tipo de armazenamento da Query 2.
- **F6 (Dossiê do Candidato)**: Verificação de palavras-chave ATS no currículo (Java, POO, SQL, APIs REST, Git, Sankhya, UVV), estrutura de apresentação, localização física da matriz Oncovit (Rua José Vivácqua, 645, Bairro Jabour, Vitória-ES), roteiro de pitch de 60 segundos com o gestor de TI e resolução comentada das 6 questões do teste técnico (Java + SQL).
- **F7 (VPS 2 & Infraestrutura)**: Endpoint de saúde Actuator `/actuator/health` respondendo HTTP 200 `UP`, interface OpenAPI Swagger UI em `/swagger-ui/index.html`, documento OpenAPI JSON em `/v3/api-docs`, conectividade SSH com host `vps2` e scripts de automação.

### Tier 2: Casos de Limite, Borda e Robustez (38 Testes)
- **F1**: Parâmetros de busca vazios ou nulos, tolerância de borda exata em 2.0°C, 8.0°C, 15.0°C e 25.0°C, representação de medicamentos com saldo zerado, integridade do header `Content-Type: application/json`.
- **F2**: Limiar exato de 60 dias (`CURRENT_DATE + 60`), inclusão de 59 dias vs exclusão de 61 dias, exclusão de lotes com validade já expirada da lista dispensável, exclusão de lotes em `QUARENTENA` e `BLOQUEADO`.
- **F3**: Requisição de lote inexistente retornando HTTP 404, ID negativo retornando 400/404, escape de caracteres especiais e acentuação no ZPL, geração de etiqueta para lote com estoque zero (reimpressão para rastreio), conferência dimensional 100x60mm em 203 DPI.
- **F4**: Baixa de quantidade exatamente igual ao saldo atual (esgotamento para zero), baixa superior ao saldo retornando HTTP 409 `OUT_OF_STOCK`, baixa em lote vencido retornando HTTP 422 `EXPIRED_LOT_REJECTED`, baixa em lote bloqueado/quarentena retornando HTTP 422 `INVALID_STATUS`, ausência de headers de autenticação Sankhya retornando HTTP 401 `UNAUTHORIZED`, payload malformado retornando HTTP 400 `BAD_REQUEST`, quantidade negativa retornando HTTP 400, lote inexistente retornando HTTP 404.
- **F5**: Violação de check constraint de categoria rejeitada, violação de check constraint de armazenamento rejeitada, rejeição de `data_validade <= data_fabricacao`, rejeição de estoque negativo, violação de foreign key sem medicamento pai rejeitada.
- **F6**: Densidade textual e contagem de palavras do currículo (150 a 3500 palavras), ordenação cronológica das etapas de abordagem presencial, adequação temporal do pitch oral de 60 segundos, tópicos essenciais de Java (Streams, POO, Exceptions), tópicos essenciais de SQL (DDL, datas, agrupamento).
- **F7**: Rota inexistente retornando HTTP 404, método não permitido retornando HTTP 405 (POST em /actuator/health), redirecionamento ou alias do Swagger UI, integridade das rotas mapeadas na especificação OpenAPI, tratamento de timeout de conexão de rede.

### Tier 3: Combinações Cross-Feature e Fluxos de Ponta a Ponta (5 Workflows)
1. **WF.01 (Picking e Expedição Crítica)**: Consulta de lotes críticos via FEFO -> Geração da etiqueta térmica com tarja de frio -> Baixa de estoque via webhook Sankhya -> Verificação da atualização nos estoques de medicamentos.
2. **WF.02 (Esgotamento Total e Rejeição)**: Identificação de saldo residual -> Baixa exata da quantidade restante até saldo zero -> Tentativa subsequente de baixa adicional -> Confirmação de bloqueio HTTP 409 `OUT_OF_STOCK`.
3. **WF.03 (Barreira Sanitária e Imutabilidade)**: Tentativa de baixa em lote em `QUARENTENA` -> Rejeição imediata por barreira sanitária HTTP 422 -> Garantia de imutabilidade do saldo físico.
4. **WF.04 (Consistência SQL vs REST)**: Execução direta da lógica da Query 1 (< 60 dias) contra os dados e comparação cruzada com a resposta do endpoint `/api/lotes/criticos`, comprovando 100% de paridade.
5. **WF.05 (Consistência Técnica Dossiê vs Código)**: Comparação dos nomes de tabelas, colunas e queries documentadas no Guia de Teste Técnico com o schema DDL real.

### Tier 4: Cenários de Aplicação do Mundo Real (5 Cenários)
1. **SC.01 (Pedido Hospitalar de Urgência - Trastuzumabe 440mg)**: Simulação completa de atendimento ao Hospital Santa Rita de Cássia com dispensação pelo lote crítico prioritário (`LOT-HER-2024-01`), conferência de cadeia de frio (2°C-8°C) e integração fiscal/estoque.
2. **SC.02 (Prevenção de Incidente Sanitário com Lote Vencido)**: Tentativa de separação indevida de lote vencido (`LOT-MAB-2023-EX`) bloqueada automaticamente pelo ERP Sankhya, salvaguardando a segurança do paciente e conformidade com a RDC 430/2020 da ANVISA.
3. **SC.03 (Dispensação Total até o Limite Zero)**: Encerramento de lote de Ondansetrona com baixa unitária completa sem inconsistência de estoque negativo.
4. **SC.04 (Auditoria de Concentração Financeira em Cadeia de Frio)**: Prova quantitativa via Query 2 de que, embora a Cadeia de Frio represente menos de 20% do volume físico de frascos, concentra mais de 90% do valor patrimonial em risco (R$ 2,77 milhões vs R$ 240 mil).
5. **SC.05 (Apresentação Prática do Candidato na Sede da Oncovit)**: Simulação do candidato demonstrando a API no Swagger UI e disparando a emissão da etiqueta térmica com DataMatrix na presença do Gestor de TI.

---

## 4. Instruções de Execução

### Execução Local Padrão:
```bash
python3 test_e2e_suite.py --target local --port 8080
```

### Execução Direcionada para o Ambiente Remoto VPS 2:
```bash
python3 test_e2e_suite.py --target vps2 --host vps2 --port 8080
```

### Execução Filtrada por Camada (Tier):
```bash
# Executa apenas Tier 1 (Cobertura de Funcionalidades)
python3 test_e2e_suite.py --tier 1

# Executa apenas Tier 2 (Casos de Borda e Limite)
python3 test_e2e_suite.py --tier 2

# Executa apenas Tier 3 (Workflows Combinatórios)
python3 test_e2e_suite.py --tier 3

# Executa apenas Tier 4 (Cenários Reais)
python3 test_e2e_suite.py --tier 4
```

### Execução Filtrada por Área de Funcionalidade:
```bash
# Executa apenas testes do endpoint de Medicamentos (F1)
python3 test_e2e_suite.py --feature F1

# Executa apenas testes do endpoint de Baixa Sankhya (F4)
python3 test_e2e_suite.py --feature F4
```

---

## 5. Resumo da Última Execução

```
================================================================================
                               EXECUTION SUMMARY                                
================================================================================

TIER 1: FEATURE COVERAGE (F1 to F7)           35 / 35 passed (100.0%)
TIER 2: BOUNDARY & CORNER CASES               38 / 38 passed (100.0%)
TIER 3: CROSS-FEATURE COMBINATIONS (PAIRWISE)  5 /  5 passed (100.0%)
TIER 4: REAL-WORLD APPLICATION SCENARIOS       5 /  5 passed (100.0%)

--------------------------------------------------------------------------------
Total Tests: 83 | Passed: 83 | Failed: 0 | Skipped: 0
Total Execution Time: 2.87s
Overall Status: PASSED (Exit code: 0)
================================================================================
```

A suíte de testes está **pronta para integração contínua** e validação dos marcos subsequentes do projeto.
