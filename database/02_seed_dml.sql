-- =============================================================================
-- PROJETO: ONCOVIT SANKHYA MED-SYNC
-- ARQUIVO: database/02_seed_dml.sql
-- DESCRICAO: Carga de dados realistas do portfólio oncológico e hospitalar
-- COMPATIBILIDADE: PostgreSQL 14+, H2 Database 2.x
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. INSERCAO DE MEDICAMENTOS (CATALOGO DE ALTA COMPLEXIDADE ONCOVIT)
-- -----------------------------------------------------------------------------
INSERT INTO medicamentos (
    codigo_sankhya, codigo_sku, nome_comercial, principio_ativo, dosagem,
    forma_farmaceutica, categoria, tipo_armazenamento, temperatura_minima,
    temperatura_maxima, registro_anvisa, unidade_medida
) VALUES 
('MED-ONC-001', 'MED-ONC-001', 'Herceptin 440mg', 'Trastuzumabe', '440 mg', 'Po liofilizado para solucao injetavel', 'ANTICORPO_MONOCLONAL', 'CADEIA_DE_FRIO', 2.0, 8.0, '1.0107.0123.001-2', 'FRASCO_AMPOLA'),
('MED-ONC-002', 'MED-ONC-002', 'MabThera 500mg', 'Rituximabe', '500 mg / 50 mL', 'Solucao para infusao injetavel', 'ANTICORPO_MONOCLONAL', 'CADEIA_DE_FRIO', 2.0, 8.0, '1.0107.0145.002-1', 'FRASCO_AMPOLA'),
('MED-ONC-003', 'MED-ONC-003', 'Keytruda 100mg', 'Pembrolizumabe', '100 mg / 4 mL', 'Solucao injetavel concentrada', 'ANTICORPO_MONOCLONAL', 'CADEIA_DE_FRIO', 2.0, 8.0, '1.0029.0205.001-4', 'FRASCO_AMPOLA'),
('MED-ONC-004', 'MED-ONC-004', 'Genuxal 1000mg', 'Ciclofosfamida', '1000 mg', 'Po para solucao injetavel', 'ONCO_HEMATOLOGICO', 'AMBIENTE', 15.0, 25.0, '1.0573.0034.003-8', 'FRASCO_AMPOLA'),
('MED-SUP-005', 'MED-SUP-005', 'Granulokine 300mcg', 'Filgrastim', '300 mcg / 30 MUI', 'Solucao injetavel seringa preenchida', 'SUPORTE_ONCOLOGICO', 'CADEIA_DE_FRIO', 2.0, 8.0, '1.0043.0789.001-9', 'SERINGA_PREENCHIDA'),
('MED-HOS-006', 'MED-HOS-006', 'Meronem 1000mg', 'Meropenem', '1000 mg', 'Po para solucao injetavel IV', 'HOSPITALAR_INJETAVEL', 'AMBIENTE', 15.0, 25.0, '1.0180.0345.001-1', 'FRASCO_AMPOLA'),
('MED-HOS-007', 'MED-HOS-007', 'Clexane 40mg', 'Enoxaparina Sodica', '40 mg / 0.4 mL', 'Solucao injetavel seringa preenchida', 'HOSPITALAR_INJETAVEL', 'AMBIENTE', 15.0, 25.0, '1.1300.0245.002-3', 'SERINGA_PREENCHIDA'),
('MED-SUP-008', 'MED-SUP-008', 'Zofran 8mg', 'Cloridrato de Ondansetrona', '8 mg / 4 mL', 'Solucao injetavel IV/IM', 'SUPORTE_ONCOLOGICO', 'AMBIENTE', 15.0, 25.0, '1.0107.0198.001-7', 'AMPOULA');

-- -----------------------------------------------------------------------------
-- 2. INSERCAO DE LOTES FARMACEUTICOS (COM LOTES CRITICOS < 60 DIAS E FEFO)
-- -----------------------------------------------------------------------------
INSERT INTO lotes (
    medicamento_id, numero_lote, data_fabricacao, data_validade,
    quantidade_inicial, quantidade_atual, status_lote, fornecedor, preco_unitario
) VALUES 
-- Herceptin 440mg (Trastuzumabe) - Cadeia de Frio
(1, 'LOT-HER-2024-01', '2025-11-20', '2026-11-23', 100, 45, 'LIBERADO', 'Roche Farma Brasil', 7850.00),
(1, 'LOT-HER-2024-02', '2026-06-15', '2027-04-18', 150, 120, 'LIBERADO', 'Roche Farma Brasil', 7850.00),

-- MabThera 500mg (Rituximabe) - Cadeia de Frio
(2, 'LOT-MAB-2024-01', '2025-11-01', '2026-11-03', 50, 20, 'LIBERADO', 'Roche Farma Brasil', 6950.00),
(2, 'LOT-MAB-2023-EX', '2024-09-01', '2026-09-29', 50, 10, 'BLOQUEADO', 'Roche Farma Brasil', 6950.00),

-- Keytruda 100mg (Pembrolizumabe) - Cadeia de Frio
(3, 'LOT-KEY-2024-01', '2026-07-20', '2027-07-21', 100, 85, 'LIBERADO', 'MSD Farmaceutica', 14200.00),

-- Genuxal 1000mg (Ciclofosfamida) - Ambiente
(4, 'LOT-GEN-2024-01', '2025-11-10', '2026-11-13', 200, 180, 'LIBERADO', 'Baxter Hospitalar', 145.00),

-- Granulokine 300mcg (Filgrastim) - Cadeia de Frio
(5, 'LOT-FIL-2024-01', '2025-12-15', '2026-12-06', 500, 320, 'LIBERADO', 'Amgen Biotecnologia', 185.00),

-- Meronem 1000mg (Meropenem) - Ambiente
(6, 'LOT-MER-2024-01', '2025-12-20', '2026-12-10', 600, 480, 'LIBERADO', 'AstraZeneca do Brasil', 92.50),
(6, 'LOT-MER-2024-02', '2026-08-15', '2027-08-15', 1500, 1400, 'LIBERADO', 'AstraZeneca do Brasil', 92.50),

-- Clexane 40mg (Enoxaparina) - Ambiente
(7, 'LOT-CLE-2024-01', '2026-05-10', '2027-05-17', 1000, 850, 'LIBERADO', 'Sanofi Medley', 38.50),

-- Zofran 8mg (Ondansetrona) - Ambiente
(8, 'LOT-OND-2024-01', '2025-12-01', '2026-11-28', 400, 250, 'LIBERADO', 'GSK Brasil', 28.00),

-- Herceptin 440mg (Trastuzumabe) - Quarentena
(1, 'LOT-QUA-2026-01', '2026-09-19', '2027-09-19', 50, 50, 'QUARENTENA', 'Roche Farma Brasil', 7850.00);

-- -----------------------------------------------------------------------------
-- 3. INSERCAO DE HISTORICO DE MOVIMENTACOES INICIAIS
-- -----------------------------------------------------------------------------
INSERT INTO movimentacoes (
    lote_id, tipo_movimentacao, quantidade, motivo,
    documento_referencia, usuario_responsavel
) VALUES 
(1, 'ENTRADA_NOTA', 100, 'Recebimento de lote via NF-e Fornecedor', 'NF-10492', 'farmaceutico.rt'),
(2, 'ENTRADA_NOTA', 150, 'Recebimento de lote via NF-e Fornecedor', 'NF-10884', 'farmaceutico.rt'),
(3, 'ENTRADA_NOTA', 50, 'Recebimento de lote via NF-e Fornecedor', 'NF-09341', 'farmaceutico.rt'),
(4, 'ENTRADA_NOTA', 50, 'Recebimento de lote via NF-e Fornecedor', 'NF-07823', 'farmaceutico.rt'),
(5, 'ENTRADA_NOTA', 100, 'Recebimento de lote via NF-e Fornecedor', 'NF-11002', 'farmaceutico.rt'),
(6, 'ENTRADA_NOTA', 200, 'Recebimento de lote via NF-e Fornecedor', 'NF-10115', 'farmaceutico.rt'),
(7, 'ENTRADA_NOTA', 500, 'Recebimento de lote via NF-e Fornecedor', 'NF-10550', 'farmaceutico.rt'),
(8, 'ENTRADA_NOTA', 600, 'Recebimento de lote via NF-e Fornecedor', 'NF-10771', 'farmaceutico.rt'),
(9, 'ENTRADA_NOTA', 1500, 'Recebimento de lote via NF-e Fornecedor', 'NF-11234', 'farmaceutico.rt'),
(10, 'ENTRADA_NOTA', 1000, 'Recebimento de lote via NF-e Fornecedor', 'NF-10920', 'farmaceutico.rt'),
(11, 'ENTRADA_NOTA', 400, 'Recebimento de lote via NF-e Fornecedor', 'NF-10332', 'farmaceutico.rt'),
(12, 'ENTRADA_NOTA', 50, 'Recebimento de lote em quarentena aguardando RT', 'NF-11450', 'farmaceutico.rt');
