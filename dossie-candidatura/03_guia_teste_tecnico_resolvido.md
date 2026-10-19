# Dossiê de Candidatura — Oncovit Distribuidora de Medicamentos
## Documento 03: Guia Preparatório para o Teste Técnico Presencial (Java & SQL)

---

### INTRODUÇÃO E METODOLOGIA
A Etapa 1 do processo seletivo da **Oncovit Distribuidora de Medicamentos** (Edital Gupy nº `74584-12725706`) avalia a capacidade analítica, domínio conceitual e prática de desenvolvimento do candidato por meio de uma prova técnica presencial com foco em **Java (POO / Streams / Tratamento de Exceções)** e **SQL (DDL / Relatórios Analíticos / Agrupamentos)**.

Este guia contém **6 questões resolvidas em profundidade**, com código-fonte compilável em **Java 17**, scripts **SQL ANSI/PostgreSQL**, fundamentação arquitetural e orientações de **como defender verbalmente cada solução perante o Gestor de TI e o Tech Lead**.

---

# PARTE 1: MÓDULO JAVA & PROGRAMAÇÃO ORIENTADA A OBJETOS

---

## QUESTÃO 1 (POO & MODELAGEM DE DOMÍNIO SANITÁRIO)

### Enunciado da Prova
Em uma distribuidora atacadista de medicamentos de alta complexidade como a Oncovit, o catálogo é composto por itens hospitalares comuns (temperatura ambiente controlada entre 15.0°C e 30.0°C) e medicamentos oncológicos termolábeis (cadeia de frio rigorosa entre 2.0°C e 8.0°C, conforme RDC 430/2020 da ANVISA).
1. Modele essa hierarquia de classes em Java utilizando boas práticas de Orientação a Objetos: herança, polimorfismo, encapsulamento e imutabilidade seletiva.
2. Crie um método polimórfico `validarCondicaoArmazenamento(double temperaturaAferida)` que determine se a temperatura medida em um sensor ou câmara fria está em conformidade com as exigências sanitárias do medicamento.
3. Demonstre o princípio da Substituição de Liskov (LSP) na utilização dessa hierarquia.

---

### Solução Técnica Completa em Java 17

```java
package com.oncovit.dominio;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Enumeração dos tipos de armazenamento admitidos nas instalações da Oncovit.
 */
public enum TipoArmazenamento {
    CADEIA_DE_FRIO("Cadeia de Frio (2°C a 8°C - Termolábil)"),
    AMBIENTE("Temperatura Ambiente Controlada (15°C a 30°C)");

    private final String descricao;

    TipoArmazenamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}

/**
 * Superclasse abstrata que encapsula os atributos essenciais e invariantes de
 * qualquer medicamento comercializado pela Oncovit.
 */
public abstract class Medicamento {
    private final Long id;
    private final String codigoSankhya;
    private final String nomeComercial;
    private final String principioAtivo;
    private final String registroAnvisa;
    private final BigDecimal precoUnitario;
    private final TipoArmazenamento tipoArmazenamento;

    public Medicamento(Long id, 
                       String codigoSankhya, 
                       String nomeComercial, 
                       String principioAtivo, 
                       String registroAnvisa, 
                       BigDecimal precoUnitario,
                       TipoArmazenamento tipoArmazenamento) {
        this.id = Objects.requireNonNull(id, "O ID do medicamento não pode ser nulo.");
        this.codigoSankhya = validarCodigoSankhya(codigoSankhya);
        this.nomeComercial = Objects.requireNonNull(nomeComercial, "Nome comercial é obrigatório.");
        this.principioAtivo = Objects.requireNonNull(principioAtivo, "Princípio ativo é obrigatório.");
        this.registroAnvisa = validarRegistroAnvisa(registroAnvisa);
        this.precoUnitario = Objects.requireNonNull(precoUnitario, "Preço unitário é obrigatório.");
        this.tipoArmazenamento = Objects.requireNonNull(tipoArmazenamento, "Tipo de armazenamento é obrigatório.");
    }

    private String validarCodigoSankhya(String cod) {
        if (cod == null || cod.isBlank()) {
            throw new IllegalArgumentException("O código interno do ERP Sankhya é obrigatório.");
        }
        return cod.trim().toUpperCase();
    }

    private String validarRegistroAnvisa(String registro) {
        if (registro == null || !registro.matches("^\\d{9,13}$")) {
            throw new IllegalArgumentException("Registro ANVISA inválido: deve conter entre 9 e 13 dígitos numéricos.");
        }
        return registro;
    }

    /**
     * Método polimórfico abstrato que obriga cada especialização de medicamento a 
     * aplicar suas tolerâncias térmicas específicas.
     */
    public abstract boolean validarCondicaoArmazenamento(double temperaturaAferida);

    /**
     * Retorna a faixa térmica recomendada para exibição em laudos e etiquetas térmicas.
     */
    public abstract String getFaixaTemperaturaDescritiva();

    // Getters encapsulados garantindo imutabilidade dos metadados
    public Long getId() { return id; }
    public String getCodigoSankhya() { return codigoSankhya; }
    public String getNomeComercial() { return nomeComercial; }
    public String getPrincipioAtivo() { return principioAtivo; }
    public String getRegistroAnvisa() { return registroAnvisa; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public TipoArmazenamento getTipoArmazenamento() { return tipoArmazenamento; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medicamento that)) return false;
        return Objects.equals(id, that.id) || Objects.equals(codigoSankhya, that.codigoSankhya);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, codigoSankhya);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - %s", codigoSankhya, nomeComercial, principioAtivo, tipoArmazenamento);
    }
}

/**
 * Especialização para medicamentos de Cadeia de Frio (ex.: anticorpos monoclonais oncológicos
 * como Trastuzumabe, Pembrolizumabe e Rituximabe).
 */
public class MedicamentoTermolabil extends Medicamento {
    public static final double TEMP_MINIMA = 2.0;
    public static final double TEMP_MAXIMA = 8.0;

    public MedicamentoTermolabil(Long id, 
                                 String codigoSankhya, 
                                 String nomeComercial, 
                                 String principioAtivo, 
                                 String registroAnvisa, 
                                 BigDecimal precoUnitario) {
        super(id, codigoSankhya, nomeComercial, principioAtivo, registroAnvisa, precoUnitario, TipoArmazenamento.CADEIA_DE_FRIO);
    }

    @Override
    public boolean validarCondicaoArmazenamento(double temperaturaAferida) {
        // Validação estrita: não pode congelar (< 2°C) nem superaquecer (> 8°C)
        return temperaturaAferida >= TEMP_MINIMA && temperaturaAferida <= TEMP_MAXIMA;
    }

    @Override
    public String getFaixaTemperaturaDescritiva() {
        return "2.0°C a 8.0°C (CONSERVAR SOB REFRIGERAÇÃO - NÃO CONGELAR)";
    }
}

/**
 * Especialização para medicamentos de Temperatura Ambiente Controlada (ex.: analgésicos
 * hospitalares, antibióticos estáveis em frasco e soluções parenterais).
 */
public class MedicamentoAmbiente extends Medicamento {
    public static final double TEMP_MINIMA = 15.0;
    public static final double TEMP_MAXIMA = 30.0;

    public MedicamentoAmbiente(Long id, 
                               String codigoSankhya, 
                               String nomeComercial, 
                               String principioAtivo, 
                               String registroAnvisa, 
                               BigDecimal precoUnitario) {
        super(id, codigoSankhya, nomeComercial, principioAtivo, registroAnvisa, precoUnitario, TipoArmazenamento.AMBIENTE);
    }

    @Override
    public boolean validarCondicaoArmazenamento(double temperaturaAferida) {
        return temperaturaAferida >= TEMP_MINIMA && temperaturaAferida <= TEMP_MAXIMA;
    }

    @Override
    public String getFaixaTemperaturaDescritiva() {
        return "15.0°C a 30.0°C (CONSERVAR EM LOCAL SECO E AO ABRIGO DA LUZ)";
    }
}
```

---

### Demonstração de Polimorfismo e Liskov Substitution Principle (LSP)

```java
public class AuditoriaSanitariaService {

    /**
     * O método recebe a classe base Medicamento e funciona de forma transparente
     * para qualquer subclasse presente ou futura (Open/Closed Principle).
     */
    public boolean inspecionarRecebimentoDoca(Medicamento med, double temperaturaDoca) {
        boolean emConformidade = med.validarCondicaoArmazenamento(temperaturaDoca);
        
        if (!emConformidade) {
            System.err.printf("[ALERTA SANITÁRIO] Medicamento %s reprovado! Temperatura aferida: %.1f°C. Exigido: %s%n",
                    med.getNomeComercial(), temperaturaDoca, med.getFaixaTemperaturaDescritiva());
        } else {
            System.out.printf("[CONFORME] Medicamento %s liberado para estocagem. Temp: %.1f°C.%n",
                    med.getNomeComercial(), temperaturaDoca);
        }
        return emConformidade;
    }
}
```

### Como Defender Verbalmente na Entrevista:
> *"Optei por uma classe abstrata `Medicamento` porque todos os medicamentos compartilham invariantes de negócio inegociáveis (código do ERP Sankhya, registro ANVISA auditado e preço), mas diferem na regra biológica de tolerância térmica.  
> O uso do polimorfismo no método `validarCondicaoArmazenamento` atende perfeitamente ao Princípio de Responsabilidade Única (SRP) e ao Princípio Aberto/Fechado (OCP): se a Oncovit passar a operar medicamentos ultracongelados a -20°C ou -80°C (como certas terapias gênicas ou vacinas de RNA), basta estender a classe base sem alterar nenhuma linha de código dos serviços de inspeção ou faturamento existentes."*

---

## QUESTÃO 2 (JAVA STREAMS & EXPEDIÇÃO HOSPITALAR FEFO)

### Enunciado da Prova
Em logística farmacêutica, a expedição de medicamentos deve seguir rigorosamente a diretriz **FEFO (*First Expired, First Out* — Primeiro que Vence, Primeiro que Sai)** para minimizar perdas por descarte e garantir segurança ao paciente.
Escreva um método em Java utilizando a **API de Streams (Java 17)** que receba uma lista de lotes (`List<Lote>`), uma data de referência e um limite em dias (ex.: 60 dias). O método deve:
1. Descartar lotes bloqueados ou com estoque zerado;
2. Descartar lotes já vencidos na data de referência;
3. Filtrar apenas os lotes cuja data de validade ocorra dentro da janela crítica (<= 60 dias);
4. Ordenar o resultado de forma ascendente pela data de validade (lotes com vencimento mais próximo primeiro) e, como critério de desempate, pelo número do lote em ordem alfabética;
5. Retornar uma lista imutável com o resultado.

---

### Solução Técnica Completa em Java 17

```java
package com.oncovit.servico;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public enum StatusLote {
    LIBERADO,
    QUARENTENA,
    BLOQUEADO,
    VENCIDO
}

public record Lote(
        Long id,
        String numeroLote,
        String codigoSankhyaProduto,
        LocalDate dataFabricacao,
        LocalDate dataValidade,
        int quantidadeDisponivel,
        StatusLote status
) {
    public Lote {
        Objects.requireNonNull(numeroLote, "Número do lote é obrigatório.");
        Objects.requireNonNull(dataFabricacao, "Data de fabricação é obrigatória.");
        Objects.requireNonNull(dataValidade, "Data de validade é obrigatória.");
        if (dataValidade.isBefore(dataFabricacao) || dataValidade.isEqual(dataFabricacao)) {
            throw new IllegalArgumentException("A data de validade deve ser estritamente posterior à data de fabricação.");
        }
        if (quantidadeDisponivel < 0) {
            throw new IllegalArgumentException("Quantidade disponível não pode ser negativa.");
        }
    }

    public boolean isAptoParaExpedicao(LocalDate dataReferencia) {
        return this.status == StatusLote.LIBERADO 
                && this.quantidadeDisponivel > 0 
                && this.dataValidade.isAfter(dataReferencia);
    }

    public long diasAteVencimento(LocalDate dataReferencia) {
        return ChronoUnit.DAYS.between(dataReferencia, this.dataValidade);
    }
}

public class ExpedicaoFefoService {

    /**
     * Filtra e ordena os lotes críticos em conformidade com o princípio FEFO.
     *
     * @param lotes           Coleção de lotes em estoque
     * @param dataReferencia  Data atual de corte (ex.: LocalDate.now())
     * @param limiteDiasCorte Janela crítica de alerta (ex.: 60 dias)
     * @return Lista imutável contendo os lotes críticos ordenados por urgência
     */
    public List<Lote> filtrarLotesCriticosFEFO(List<Lote> lotes, LocalDate dataReferencia, long limiteDiasCorte) {
        if (lotes == null || lotes.isEmpty()) {
            return Collections.emptyList();
        }
        Objects.requireNonNull(dataReferencia, "Data de referência não pode ser nula.");
        if (limiteDiasCorte <= 0) {
            throw new IllegalArgumentException("O limite de dias deve ser um número positivo.");
        }

        // Definição explícita de Comparator encadeado: Primeiro Data de Validade ASC, depois Número do Lote
        Comparator<Lote> criterioFefo = Comparator
                .comparing(Lote::dataValidade)
                .thenComparing(Lote::numeroLote);

        return lotes.stream()
                // 1. Apenas lotes liberados, com saldo físico e que ainda não venceram
                .filter(lote -> lote.isAptoParaExpedicao(dataReferencia))
                // 2. Apenas lotes cuja validade expire dentro da janela de atenção (<= limiteDiasCorte)
                .filter(lote -> lote.diasAteVencimento(dataReferencia) <= limiteDiasCorte)
                // 3. Ordenação FEFO (First Expired, First Out)
                .sorted(criterioFefo)
                // 4. Retorno de lista imutável (Java 16+)
                .toList();
    }
}
```

---

### Casos de Teste e Borda (Boundary Conditions) Cobertos:
1. **Lote que vence no dia de hoje (`dataValidade.isEqual(hoje)`):** Descartado de imediato, pois itens com validade no dia não podem ser despachados para trânsito rodoviário.
2. **Lote no limite exato de 60 dias (`diasAteVencimento == 60`):** Incluído no topo da lista crítica.
3. **Lote de 61 dias:** Ignorado pelo filtro de urgência, permanecendo no estoque regular.
4. **Lotes com a mesma data de validade:** Desempatados deterministicamente pelo número alfanumérico do lote.

### Como Defender Verbalmente na Entrevista:
> *"Utilizei a API de Streams do Java 17 por sua expressividade declarativa e eficiência de pipeline. Encapsulei o predicado de elegibilidade `isAptoParaExpedicao` no próprio modelo do `Lote`, mantendo o código limpo e com alta coesão.  
> Para garantir imutabilidade e evitar efeitos colaterais em concorrência, fechei a Stream com `.toList()`, que retorna uma lista não modificável. A ordenação encadeada com `Comparator.comparing().thenComparing()` garante determinismo total na fila de separação do operador do armazém."*

---

## QUESTÃO 3 (TRATAMENTO DE EXCEÇÕES DE DOMÍNIO & REST CONTROLLER ADVICE)

### Enunciado da Prova
Em sistemas corporativos integrados ao ERP Sankhya, a captura genérica de erros com blocos `catch (Exception e) {}` mascarando falhas é proibida, pois corrompe a rastreabilidade e impede o diagnóstico rápido de incidentes.
1. Crie uma hierarquia de exceções de negócio personalizadas para o estoque farmacêutico: `LoteVencidoException` e `SaldoInsuficienteException`.
2. Implemente uma classe interceptadora `@RestControllerAdvice` no Spring Boot que capture essas exceções e retorne um payload estruturado com código de erro de negócio, timestamp e os códigos de status HTTP corretos (`422 Unprocessable Entity` e `409 Conflict`).

---

### Solução Técnica Completa em Java 17 (Spring Boot 3)

```java
package com.oncovit.excecao;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.servlet.http.HttpServletRequest;

import java.time.Instant;

/**
 * Superclasse abstrata para todas as violações de regras de negócio do Oncovit Med Sync.
 */
public abstract class NegocioException extends RuntimeException {
    private final String codigoErro;

    public NegocioException(String codigoErro, String mensagem) {
        super(mensagem);
        this.codigoErro = codigoErro;
    }

    public String getCodigoErro() {
        return codigoErro;
    }
}

/**
 * Lançada quando há tentativa de movimentar ou baixar um lote com validade expirada.
 */
public class LoteVencidoException extends NegocioException {
    public LoteVencidoException(String numeroLote, String dataValidade) {
        super("EXPIRED_LOT_REJECTED", 
              String.format("Operação rejeitada: O lote '%s' está vencido desde %s e seu faturamento é proibido por norma sanitária.", 
                            numeroLote, dataValidade));
    }
}

/**
 * Lançada quando a quantidade requisitada pelo Sankhya excede o saldo físico disponível.
 */
public class SaldoInsuficienteException extends NegocioException {
    public SaldoInsuficienteException(String numeroLote, int saldoAtual, int quantidadeRequisitada) {
        super("OUT_OF_STOCK", 
              String.format("Saldo insuficiente no lote '%s': Quantidade disponível em estoque é de %d unidades, mas foram solicitadas %d unidades.", 
                            numeroLote, saldoAtual, quantidadeRequisitada));
    }
}

/**
 * DTO padronizado para respostas de erro em conformidade com o RFC 7807 / RFC 9457.
 */
public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String errorCode,
        String message,
        String path
) {}

/**
 * Handler centralizador de exceções para todos os Controllers REST.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LoteVencidoException.class)
    public ResponseEntity<ApiErrorResponse> tratarLoteVencido(LoteVencidoException ex, HttpServletRequest request) {
        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.UNPROCESSABLE_ENTITY.value(), // 422
                "Violação Sanitária de Validade",
                ex.getCodigoErro(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<ApiErrorResponse> tratarSaldoInsuficiente(SaldoInsuficienteException ex, HttpServletRequest request) {
        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.CONFLICT.value(), // 409
                "Conflito de Estoque",
                ex.getCodigoErro(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> tratarArgumentoInvalido(IllegalArgumentException ex, HttpServletRequest request) {
        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(), // 400
                "Parâmetro Inválido",
                "INVALID_PAYLOAD",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> tratarErroInesperado(Exception ex, HttpServletRequest request) {
        // Log técnico para auditoria interna preservando stacktrace
        ex.printStackTrace(); 
        
        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(), // 500
                "Erro Interno de Processamento",
                "INTERNAL_SERVER_ERROR",
                "Ocorreu uma instabilidade inesperada. O incidente foi registrado para a equipe de suporte da Oncovit.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
```

### Como Defender Verbalmente na Entrevista:
> *"Em arquitetura REST corporativa, os controllers não devem conter lógica de captura de exceções repetitiva (`try/catch`). O `@RestControllerAdvice` desacopla completamente o tratamento de erros do fluxo feliz da aplicação.  
> Além disso, utilizei `RuntimeException` (exceções não checadas), o que garante que qualquer exceção disparada dentro de um método anotado com `@Transactional` do Spring force o rollback automático da transação do banco de dados, impedindo que baixas parciais ou inconsistências fiquem gravadas caso ocorra uma falha no meio do processo."*

---

# PARTE 2: MÓDULO BANCO DE DADOS RELACIONAL & SCRIPTS SQL

---

## QUESTÃO 4 (DDL, INTEGRIDADE REFERENCIAL & RESTRIÇÕES CHECK)

### Enunciado da Prova
Escreva o script DDL (Data Definition Language) compatível com PostgreSQL / SQL ANSI para criar a modelagem relacional de controle de lotes e rastreabilidade da distribuidora Oncovit:
1. Tabela `medicamento`: identificador, código do ERP Sankhya único, nome comercial, princípio ativo, tipo de armazenamento (`CADEIA_DE_FRIO`, `AMBIENTE`), temperaturas mínima e máxima de conservação, registro ANVISA e preço unitário.
2. Tabela `lote`: identificador, chave estrangeira para medicamento com proteção contra deleção acidental (`ON DELETE RESTRICT`), número do lote alfanumérico, datas de fabricação e validade, quantidade em estoque e status sanitário.
3. Aplique restrições de integridade (`CHECK`) garantindo que:
   - A quantidade disponível em estoque nunca seja negativa (`quantidade >= 0`);
   - A data de validade seja estritamente superior à data de fabricação (`data_validade > data_fabricacao`);
   - O status do lote pertença a um domínio estrito (`'LIBERADO'`, `'QUARENTENA'`, `'BLOQUEADO'`, `'VENCIDO'`);
   - A temperatura máxima de conservação seja maior ou igual à temperatura mínima.
4. Crie os índices apropriados para acelerar a busca de lotes críticos por validade.

---

### Solução Técnica Completa em SQL (PostgreSQL)

```sql
-- ============================================================================
-- SCRIPT DDL: CONTROLE DE RASTREABILIDADE FARMACÊUTICA ONCOVIT
-- ============================================================================

-- Limpeza preventiva de objetos caso já existam no schema
DROP TABLE IF EXISTS lote CASCADE;
DROP TABLE IF EXISTS medicamento CASCADE;

-- 1. TABELA DE MEDICAMENTOS (CATÁLOGO GERAL & DIRETRIZES TÉRMICAS)
CREATE TABLE medicamento (
    id BIGSERIAL PRIMARY KEY,
    codigo_sankhya VARCHAR(50) NOT NULL,
    nome_comercial VARCHAR(150) NOT NULL,
    principio_ativo VARCHAR(150) NOT NULL,
    categoria VARCHAR(50) NOT NULL, -- 'ONCOLOGICO', 'HOSPITALAR', 'IMUNOBIOLOGICO'
    tipo_armazenamento VARCHAR(30) NOT NULL, -- 'CADEIA_DE_FRIO', 'AMBIENTE'
    temperatura_minima NUMERIC(4, 1) NOT NULL,
    temperatura_maxima NUMERIC(4, 1) NOT NULL,
    registro_anvisa VARCHAR(20) NOT NULL,
    unidade_medida VARCHAR(20) NOT NULL DEFAULT 'FRASCO_AMPOLA',
    preco_unitario NUMERIC(12, 2) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Restrições de Unicidade e Integridade
    CONSTRAINT uq_medicamento_codigo_sankhya UNIQUE (codigo_sankhya),
    CONSTRAINT uq_medicamento_registro_anvisa UNIQUE (registro_anvisa),
    CONSTRAINT chk_medicamento_faixa_temp CHECK (temperatura_maxima >= temperatura_minima),
    CONSTRAINT chk_medicamento_tipo_armazenamento CHECK (tipo_armazenamento IN ('CADEIA_DE_FRIO', 'AMBIENTE')),
    CONSTRAINT chk_medicamento_preco_positivo CHECK (preco_unitario > 0.00)
);

-- 2. TABELA DE LOTES FÍSICOS E CONTROLE SANITÁRIO
CREATE TABLE lote (
    id BIGSERIAL PRIMARY KEY,
    medicamento_id BIGINT NOT NULL,
    numero_lote VARCHAR(50) NOT NULL,
    data_fabricacao DATE NOT NULL,
    data_validade DATE NOT NULL,
    quantidade_disponivel INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'LIBERADO',
    codigo_barras VARCHAR(14) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Integridade Referencial (Impede exclusão de medicamento com lotes associados)
    CONSTRAINT fk_lote_medicamento FOREIGN KEY (medicamento_id)
        REFERENCES medicamento (id) ON DELETE RESTRICT,

    -- Unicidade do lote por medicamento (evita duplicidade do mesmo lote no mesmo produto)
    CONSTRAINT uq_medicamento_numero_lote UNIQUE (medicamento_id, numero_lote),

    -- Restrições de Validação Regulatória e Matemática
    CONSTRAINT chk_lote_datas_validas CHECK (data_validade > data_fabricacao),
    CONSTRAINT chk_lote_saldo_nao_negativo CHECK (quantidade_disponivel >= 0),
    CONSTRAINT chk_lote_status_valido CHECK (status IN ('LIBERADO', 'QUARENTENA', 'BLOQUEADO', 'VENCIDO'))
);

-- 3. CRIAÇÃO DE ÍNDICES ESTRATÉGICOS PARA OTIMIZAÇÃO DE CONSULTAS
-- Índice composto para acelerar filtros de auditoria de validade crítica e priorização FEFO
CREATE INDEX idx_lote_validade_status_saldo 
    ON lote (data_validade ASC, status, quantidade_disponivel);

-- Índice para junções rápidas entre lote e medicamento
CREATE INDEX idx_lote_medicamento_id 
    ON lote (medicamento_id);

-- Comentários de documentação para o dicionário de dados da Oncovit
COMMENT ON TABLE medicamento IS 'Cadastro mestre de medicamentos hospitalares e oncológicos integrados ao ERP Sankhya.';
COMMENT ON TABLE lote IS 'Registro físico de lotes farmacêuticos, rastreabilidade de validade e saldo disponível.';
COMMENT ON CONSTRAINT chk_lote_datas_validas ON lote IS 'Garante que nenhum lote possua validade retroativa ou igual à fabricação.';
```

### Como Defender Verbalmente na Entrevista:
> *"No banco de dados, apliquei o princípio da 'Defesa em Profundidade': mesmo que a API ou a aplicação Java possuam validações de Bean Validation, as regras fundamentais de integridade foram consolidadas no próprio motor relacional através de constraints `CHECK`.  
> O uso de `ON DELETE RESTRICT` na chave estrangeira protege a rastreabilidade histórica da distribuidora: jamais permitimos a exclusão em cascata de um medicamento se houver movimentações ou lotes físicos registrados.  
> Além disso, criei um índice composto cobrindo `(data_validade, status, quantidade_disponivel)`, permitindo que as consultas de rotina de separação FEFO façam um Index Scan ultra veloz sem varrer todas as páginas da tabela."*

---

## QUESTÃO 5 (QUERY DE AUDITORIA: ALERTA DE VALIDADE CRÍTICA < 60 DIAS)

### Enunciado da Prova
A diretoria de operações da Oncovit necessita de um relatório diário de auditoria para identificar todos os lotes que estão na **faixa de validade crítica (vencimento inferior a 60 dias)**.
Escreva uma consulta SQL que:
1. Retorne o código Sankhya, nome comercial do medicamento, princípio ativo, tipo de armazenamento, número do lote, data de validade, quantidade em estoque e preço unitário;
2. Calcule dinamicamente a quantidade de dias restantes até o vencimento (`dias_para_vencer`);
3. Calcule o **valor financeiro total em risco de perda** (`valor_financeiro_em_risco = quantidade * preco_unitario`);
4. Filtre apenas lotes com status `'LIBERADO'`, com saldo positivo em estoque (`quantidade_disponivel > 0`) e cuja validade expire entre hoje e os próximos 60 dias;
5. Ordene o relatório por urgência: lotes com menor prazo de validade no topo; em caso de empate, ordene pelo maior valor financeiro em risco.

---

### Solução Técnica Completa em SQL (PostgreSQL / ANSI)

```sql
SELECT 
    m.codigo_sankhya AS "Código Sankhya",
    m.nome_comercial AS "Medicamento",
    m.principio_ativo AS "Princípio Ativo",
    m.tipo_armazenamento AS "Armazenamento",
    l.numero_lote AS "Nº do Lote",
    TO_CHAR(l.data_validade, 'DD/MM/YYYY') AS "Data Validade",
    l.quantidade_disponivel AS "Saldo Unidades",
    TO_CHAR(m.preco_unitario, 'R$ 999,990.00') AS "Preço Unitário",
    (l.data_validade - CURRENT_DATE) AS "Dias p/ Vencer",
    TO_CHAR((l.quantidade_disponivel * m.preco_unitario), 'R$ 999,999,990.00') AS "Valor em Risco (R$)"
FROM lote l
INNER JOIN medicamento m ON m.id = l.medicamento_id
WHERE l.status = 'LIBERADO'
  AND l.quantidade_disponivel > 0
  -- Janela crítica: entre o momento atual e 60 dias no futuro
  AND l.data_validade >= CURRENT_DATE
  AND l.data_validade <= (CURRENT_DATE + INTERVAL '60 days')
ORDER BY 
    l.data_validade ASC,
    (l.quantidade_disponivel * m.preco_unitario) DESC;
```

---

### Demonstração de Execução com Dados Amostrais

| Código Sankhya | Medicamento | Princípio Ativo | Armazenamento | Nº do Lote | Data Validade | Saldo Unidades | Preço Unitário | Dias p/ Vencer | Valor em Risco (R$) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **MED-ONC-001** | Herceptin 440mg | Trastuzumabe | CADEIA_DE_FRIO | LOT-TRA-2026A | 15/11/2026 | 30 | R$ 8.450,00 | **27 dias** | **R$ 253.500,00** |
| **MED-ONC-002** | Keytruda 100mg | Pembrolizumabe| CADEIA_DE_FRIO | LOT-PEM-2026B | 28/11/2026 | 15 | R$ 16.800,00 | **40 dias** | **R$ 252.000,00** |
| **MED-HOS-001** | Meropenem 1g | Meropenem Tri-hidratado| AMBIENTE | LOT-MER-2026C | 05/12/2026 | 450 | R$ 68,00 | **47 dias** | **R$ 30.600,00** |

### Como Defender Verbalmente na Entrevista:
> *"Esta query atende a uma dor clássica da logística hospitalar: evitar perdas milionárias em descarte de imunoterápicos.  
> Note que utilizei a subtração direta `(l.data_validade - CURRENT_DATE)`, que no PostgreSQL retorna precisamente o número inteiro de dias entre as datas.  
> Ao filtrar explicitamente `quantidade_disponivel > 0`, evitamos poluir a visão executiva com lotes zerados que já foram consumidos.  
> A ordenação secundária por `(quantidade * preco) DESC` prioriza ações comerciais: entre dois lotes que vencem no mesmo dia, o time de cotações deve tentar direcionar primeiro aquele que representa a maior exposição de capital para a Oncovit."*

---

## QUESTÃO 6 (AGRUPAMENTO ANALÍTICO: BALANÇO DE CAPITAL POR TIPO DE ARMAZENAMENTO)

### Enunciado da Prova
A gestão financeira da Oncovit necessita de um balanço consolidado de estoque agrupado por **Tipo de Armazenamento (`CADEIA_DE_FRIO` vs `AMBIENTE`)** para avaliar a concentração de valor patrimonial imobilizado e planejar a expansão das câmaras frias.
Escreva uma consulta SQL agregada que calcule:
1. O tipo de armazenamento;
2. A quantidade de itens (medicamentos) distintos cadastrados naquele tipo;
3. O número total de lotes físicos ativos com saldo;
4. O somatório de unidades físicas estocadas;
5. O valor patrimonial total em Reais (R$) estocado naquele tipo (`SUM(quantidade * preco_unitario)`);
6. O ticket médio monetário por unidade física armazenada;
7. A participação percentual do valor de cada categoria sobre o valor total do armazém utilizando **Window Functions (`OVER()`)**.
8. Filtre apenas tipos de armazenamento que contenham ao menos 1 lote com saldo positivo (`HAVING`).

---

### Solução Técnica Completa em SQL (PostgreSQL / ANSI)

```sql
SELECT 
    m.tipo_armazenamento AS "Tipo Armazenamento",
    COUNT(DISTINCT m.id) AS "Total Medicamentos Distintos",
    COUNT(l.id) AS "Total Lotes Ativos",
    SUM(l.quantidade_disponivel) AS "Total Unidades em Estoque",
    TO_CHAR(SUM(l.quantidade_disponivel * m.preco_unitario), 'R$ 999,999,990.00') AS "Valor Patrimonial Total",
    TO_CHAR(
        SUM(l.quantidade_disponivel * m.preco_unitario) / NULLIF(SUM(l.quantidade_disponivel), 0),
        'R$ 999,990.00'
    ) AS "Valor Médio Unitário",
    ROUND(
        (
            SUM(l.quantidade_disponivel * m.preco_unitario) 
            / SUM(SUM(l.quantidade_disponivel * m.preco_unitario)) OVER ()
        ) * 100.0, 
        2
    ) AS "% Participação no Capital"
FROM medicamento m
INNER JOIN lote l ON l.medicamento_id = m.id
WHERE l.status = 'LIBERADO'
  AND l.quantidade_disponivel > 0
GROUP BY m.tipo_armazenamento
HAVING SUM(l.quantidade_disponivel) > 0
ORDER BY SUM(l.quantidade_disponivel * m.preco_unitario) DESC;
```

---

### Demonstração de Resultado Esperado da Query

| Tipo Armazenamento | Total Medicamentos Distintos | Total Lotes Ativos | Total Unidades em Estoque | Valor Patrimonial Total | Valor Médio Unitário | % Participação no Capital |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **CADEIA_DE_FRIO** | 4 | 8 | 420 | **R$ 3.840.000,00** | R$ 9.142,85 | **92,15%** |
| **AMBIENTE** | 6 | 12 | 8.950 | **R$ 327.400,00** | R$ 36,58 | **7,85%** |

### Como Defender Verbalmente na Entrevista:
> *"Esta consulta demonstra a aplicação prática de funções de agregação combinadas com Window Functions.  
> Um ponto fundamental que demonstrei foi o uso de `COUNT(DISTINCT m.id)`: se usássemos apenas `COUNT(m.id)`, o valor seria inflado pela quantidade de lotes da tabela filha.  
> Utilizei o recurso `NULLIF(SUM(l.quantidade_disponivel), 0)` no cálculo do valor médio unitário para prevenir categoricamente o erro fatal de divisão por zero (`Division by Zero Error`) caso ocorra algum agrupamento atípico.  
> Por fim, a Window Function `OVER ()` calcula a soma geral de todos os grupos em uma única passada de leitura do banco, calculando a porcentagem exata de representatividade patrimonial sem a necessidade de criar subconsultas ou tabelas temporárias lentas."*

---

## 7. DICAS COMPORTAMENTAIS PARA A PROVA TÉCNICA PRESENCIAL

1. **Leia o enunciado até o final antes de começar a escrever:** Muitas vezes o avaliador insere uma pegadinha simples (ex.: *"apenas lotes liberados"*, *"não permitir saldo negativo"*).
2. **Escreva com letra legível e identação clara:** Se a prova for em papel, alinhe os blocos de chaves `{}` e use recuo de 2 ou 4 espaços. Em SQL, escreva palavras-chave em MAIÚSCULAS (`SELECT`, `FROM`, `WHERE`, `GROUP BY`) e nomes de colunas em minúsculas.
3. **Explique suas decisões nos comentários:** Se houver mais de uma abordagem possível, adicione uma linha comentada: `// Optei por ArrayList para acesso O(1) por índice` ou `-- Utilizado INNER JOIN para garantir integridade`.
4. **Pergunte em caso de ambiguidade:** Fazer uma pergunta pontual e técnica ao aplicador (ex.: *"Para o cálculo de validade crítica, devemos considerar o operador menor ou menor e igual a 60 dias?"*) demonstra atenção aos detalhes e segurança profissional.
