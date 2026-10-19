# Oncovit Sankhya Med-Sync — Banco de Dados Relacional & Scripts de Auditoria

Este diretório contém a modelagem física relacional, carga de dados farmacêuticos de homologação e consultas analíticas de auditoria do microsserviço **Oncovit Sankhya Med-Sync**.

---

## 1. Estrutura dos Arquivos

| Arquivo | Descrição |
|---|---|
| `01_schema_ddl.sql` | Criação das tabelas (`medicamentos`, `lotes`, `movimentacoes`, `sankhya_sync_log`), chaves primárias, chaves estrangeiras, restrições de integridade sanitária (`CHECK constraints`) e índices de alta performance. |
| `02_seed_dml.sql` | Carga de dados realistas de medicamentos de alta complexidade (anticorpos monoclonais oncológicos, cadeia de frio 2°C a 8°C e injetáveis hospitalares em temperatura ambiente), com lotes em diferentes faixas de validade (críticos < 60 dias, regulares e bloqueados). |
| `03_advanced_queries.sql` | Consultas analíticas para prevenção de perdas (FEFO < 60 dias com valor financeiro em risco) e agregação volumétrica/financeira por classe térmica de armazenamento. |

---

## 2. Compatibilidade e Dialeto

Os scripts foram desenhados com compatibilidade dual:
- **PostgreSQL 14+** (Ambiente corporativo de produção).
- **H2 Database 2.x** no modo de compatibilidade PostgreSQL (`MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1`), utilizado pelo Spring Boot nos perfis de desenvolvimento e testes automatizados.

---

## 3. Instruções de Execução

### 3.1 Execução no PostgreSQL

Para executar diretamente em uma instância PostgreSQL local ou remota via `psql`:

```bash
# 1. Conectar e criar o banco de dados (se necessário)
psql -U postgres -h localhost -c "CREATE DATABASE oncovit_db;"

# 2. Executar o DDL de esquema
psql -U postgres -h localhost -d oncovit_db -f 01_schema_ddl.sql

# 3. Executar a carga de dados (Seed DML)
psql -U postgres -h localhost -d oncovit_db -f 02_seed_dml.sql

# 4. Executar as consultas analíticas de auditoria
psql -U postgres -h localhost -d oncovit_db -f 03_advanced_queries.sql
```

### 3.2 Execução no Docker com PostgreSQL Efêmero

```bash
# Iniciar container temporário do PostgreSQL 16
docker run --name oncovit-postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=oncovit_db -p 5432:5432 -d postgres:16-alpine

# Executar scripts em sequência
docker exec -i oncovit-postgres psql -U postgres -d oncovit_db < 01_schema_ddl.sql
docker exec -i oncovit-postgres psql -U postgres -d oncovit_db < 02_seed_dml.sql
docker exec -i oncovit-postgres psql -U postgres -d oncovit_db < 03_advanced_queries.sql

# Encerrar container após verificação
docker rm -f oncovit-postgres
```

---

## 4. Destaques da Modelagem Relacional

1. **Restrições Sanitárias (ANVISA RDC 430/2020)**:
   - `chk_lote_datas`: Garante que a data de validade seja estritamente posterior à data de fabricação.
   - `chk_lote_qtd_atual`: Garante não negatividade do estoque (`quantidade_atual >= 0`) e que o saldo nunca exceda a quantidade inicial.
   - `chk_med_temperatura`: Garante consistência física onde `temperatura_minima <= temperatura_maxima`.
   - `chk_med_armazenamento`: Restringe classes térmicas a `CADEIA_DE_FRIO` (2°C a 8°C) e `AMBIENTE` (15°C a 25°C).

2. **Rastreabilidade Indelével**:
   - Chaves estrangeiras com `ON DELETE RESTRICT` em `lotes -> medicamentos` e `movimentacoes -> lotes`, impedindo exclusão acidental de histórico farmacêutico regulatório.
   - Tabela `sankhya_sync_log` com payload completo de requisição/resposta para auditoria fiscal e contábil.
