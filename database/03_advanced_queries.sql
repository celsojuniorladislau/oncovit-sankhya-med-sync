-- =============================================================================
-- PROJETO: ONCOVIT SANKHYA MED-SYNC
-- ARQUIVO: database/03_advanced_queries.sql
-- DESCRICAO: Consultas SQL avancadas para auditoria farmacêutica e controladoria
-- COMPATIBILIDADE: PostgreSQL 14+, H2 Database 2.x (MODE=PostgreSQL)
-- =============================================================================

-- =============================================================================
-- QUERY 1: RELATORIO DE PERDA IMINENTE POR VALIDADE CRITICA (< 60 DIAS) - FEFO
-- Objetivo: Identificar lotes ativos em status LIBERADO com data de validade
--           nos proximos 60 dias a partir da data atual, ordenados pelo criterio
--           FEFO (First Expired, First Out) com valor financeiro em risco.
-- =============================================================================
SELECT 
    l.numero_lote,
    m.codigo_sankhya,
    m.nome_comercial,
    m.principio_ativo,
    m.tipo_armazenamento,
    l.quantidade_atual AS saldo_disponivel,
    l.preco_unitario,
    ROUND(l.quantidade_atual * l.preco_unitario, 2) AS valor_financeiro_risco_brl,
    l.data_validade,
    CAST(l.data_validade - CURRENT_DATE AS INT) AS dias_para_vencer,
    CASE 
        WHEN l.data_validade < CURRENT_DATE THEN 'VENCIDO_BLOQUEADO'
        WHEN (l.data_validade - CURRENT_DATE) <= 30 THEN 'CRITICO_ALTISSIMO_RISCO'
        WHEN (l.data_validade - CURRENT_DATE) <= 60 THEN 'CRITICO_ATENCAO'
        ELSE 'REGULAR'
    END AS status_urgencia
FROM lotes l
JOIN medicamentos m ON l.medicamento_id = m.id
WHERE l.quantidade_atual > 0
  AND l.status_lote = 'LIBERADO'
  AND l.data_validade <= (CURRENT_DATE + 60)
ORDER BY l.data_validade ASC;

-- =============================================================================
-- QUERY 2: AGRUPAMENTO QUANTITATIVO E FINANCEIRO POR TIPO DE ARMAZENAMENTO
-- Objetivo: Consolidar o volume fisico e o valor financeiro imobilizado por
--           classe termica de conservacao (Cadeia de Frio 2°C a 8°C vs Ambiente),
--           destacando o percentual de valor em risco por shelf-life critico.
-- =============================================================================
SELECT 
    m.tipo_armazenamento,
    COUNT(DISTINCT m.id) AS total_medicamentos_distintos,
    COUNT(l.id) AS total_lotes_ativos,
    SUM(l.quantidade_atual) AS saldo_total_unidades,
    ROUND(SUM(l.quantidade_atual * l.preco_unitario), 2) AS valor_total_estoque_brl,
    SUM(CASE 
        WHEN l.data_validade <= (CURRENT_DATE + 60) AND l.status_lote = 'LIBERADO' 
        THEN l.quantidade_atual 
        ELSE 0 
    END) AS unidades_em_risco_validade_60d,
    ROUND(SUM(CASE 
        WHEN l.data_validade <= (CURRENT_DATE + 60) AND l.status_lote = 'LIBERADO' 
        THEN l.quantidade_atual * l.preco_unitario 
        ELSE 0 
    END), 2) AS valor_financeiro_em_risco_60d
FROM medicamentos m
JOIN lotes l ON l.medicamento_id = m.id
WHERE l.quantidade_atual > 0
GROUP BY m.tipo_armazenamento
ORDER BY m.tipo_armazenamento;
