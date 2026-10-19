# E2E Test Infra: oncovit-sankhya-med-sync

## Test Philosophy
- Requirement-driven, opaque-box testing based on `ORIGINAL_REQUEST.md` and `PROJECT.md`.
- No reliance on internal Spring/JPA implementations — tests interact via HTTP REST, SQL execution, ZPL parsing, and remote SSH/tmux probes.
- Methodology: Category-Partition + Boundary Value Analysis (BVA) + Pairwise Combinatorial + Real-World Workload Testing.

## Test Architecture
- Test Runner: Python 3 test suite (`test_e2e_suite.py`) runnable against local Spring Boot or remote VPS 2.
- Invocations:
  - `python3 test_e2e_suite.py --target local --port 8080`
  - `python3 test_e2e_suite.py --target vps2 --host vps2 --port 8080`
- Exit Code: 0 on all tests passing, > 0 on any assertion failure.
- Artifacts: generates `TEST_READY.md` upon completion of test suite creation.

## Feature Inventory & Test Coverage
| # | Feature Area | Tier 1 (Coverage) | Tier 2 (Boundaries) | Tier 3 (Cross-Feature) | Tier 4 (Workload) |
|---|--------------|:-----------------:|:-------------------:|:----------------------:|:-----------------:|
| F1 | `GET /api/medicamentos` (Storage classes, temps) | 5 | 5 | ✓ | ✓ |
| F2 | `GET /api/lotes/criticos` (< 60 days, FEFO order) | 5 | 5 | ✓ | ✓ |
| F3 | `GET /api/lotes/{id}/etiqueta` (ZPL II + JSON) | 5 | 5 | ✓ | ✓ |
| F4 | `POST /api/integracao/sankhya/baixa-estoque` | 5 | 5 | ✓ | ✓ |
| F5 | Database Schema & Advanced Audit Queries | 5 | 5 | ✓ | ✓ |
| F6 | Candidate Dossier (CV, Pitch, Solved Tests) | 5 | 5 | ✓ | ✓ |
| F7 | VPS 2 tmux deployment & remote Swagger UI | 5 | 5 | ✓ | ✓ |

## Real-World Application Scenarios (Tier 4)
| # | Scenario | Features Exercised | Expected Outcome |
|---|----------|--------------------|------------------|
| 1 | Onco Hospital Urgent Order (Trastuzumabe 440mg) | F1, F2, F3, F4 | FEFO selection of critical lot, label generated with 2°C-8°C warning, stock deducted via Sankhya API |
| 2 | Rejection of Expired or Quarantined Lot | F2, F4 | Sankhya API rejects deduction with HTTP 422 `EXPIRED_LOT_REJECTED` |
| 3 | Stock Depletion to Zero Boundary | F4, F1 | Deduction of exact remaining quantity succeeds; subsequent deduction rejected with HTTP 409 `OUT_OF_STOCK` |
| 4 | Audit Inspection of Cold-Chain vs Ambient Value | F5 | SQL audit groups stock by storage class, proving 90%+ value concentration in cold chain |
| 5 | Presential Candidate Presentation Live Demo | F6, F7, F1, F3 | Candidate executes 60-second pitch, accesses VPS 2 Swagger UI via browser, demonstrates ZPL thermal label generation |
