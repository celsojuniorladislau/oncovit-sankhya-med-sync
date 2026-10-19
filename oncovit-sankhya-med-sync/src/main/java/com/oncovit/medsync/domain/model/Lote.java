package com.oncovit.medsync.domain.model;

import com.oncovit.medsync.domain.enums.StatusLote;
import com.oncovit.medsync.exception.ExpiredLotException;
import com.oncovit.medsync.exception.InsufficientStockException;
import com.oncovit.medsync.exception.InvalidLotStatusException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "lotes", indexes = {
    @Index(name = "idx_lotes_numero", columnList = "numero_lote", unique = true),
    @Index(name = "idx_lotes_medicamento", columnList = "medicamento_id"),
    @Index(name = "idx_lotes_validade", columnList = "data_validade"),
    @Index(name = "idx_lotes_status_validade", columnList = "status_lote, data_validade")
})
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;

    @NotBlank
    @Size(max = 50)
    @Column(name = "numero_lote", nullable = false, unique = true, length = 50)
    private String numeroLote;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    @NotNull
    @Column(name = "data_fabricacao", nullable = false)
    private LocalDate dataFabricacao;

    @NotNull
    @Column(name = "data_validade", nullable = false)
    private LocalDate dataValidade;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status_lote", nullable = false, length = 30)
    private StatusLote status;

    @NotNull
    @Min(0)
    @Column(name = "quantidade_inicial", nullable = false)
    private Integer quantidadeInicial;

    @NotNull
    @Min(0)
    @Column(name = "quantidade_atual", nullable = false)
    private Integer quantidadeAtual;

    @NotNull
    @Column(name = "preco_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precoUnitario;

    @Size(max = 150)
    @Column(name = "fornecedor", length = 150)
    private String fornecedor;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Movimentacao> movimentacoes = new ArrayList<>();

    public Lote() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    public Lote(String numeroLote, Medicamento medicamento, LocalDate dataFabricacao,
                LocalDate dataValidade, StatusLote status, Integer quantidadeInicial,
                BigDecimal precoUnitario, String fornecedor) {
        this();
        this.numeroLote = numeroLote;
        this.medicamento = medicamento;
        this.dataFabricacao = dataFabricacao;
        this.dataValidade = dataValidade;
        this.status = status;
        this.quantidadeInicial = quantidadeInicial;
        this.quantidadeAtual = quantidadeInicial;
        this.precoUnitario = precoUnitario;
        this.fornecedor = fornecedor;
    }

    @PrePersist
    protected void onCreate() {
        if (this.criadoEm == null) this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
        validarConsistenciaDatas();
    }

    @PreUpdate
    protected void onUpdate() {
        this.atualizadoEm = LocalDateTime.now();
        validarConsistenciaDatas();
    }

    private void validarConsistenciaDatas() {
        if (dataFabricacao != null && dataValidade != null && !dataValidade.isAfter(dataFabricacao)) {
            throw new IllegalStateException("A data de validade (" + dataValidade + 
                    ") deve ser estritamente posterior à data de fabricação (" + dataFabricacao + ").");
        }
    }

    // Regras de Negócio do Lote

    /**
     * Calcula se o lote está em validade crítica (<= 60 dias da data de corte) e liberado.
     */
    public boolean isCritico() {
        return isCritico(LocalDate.now());
    }

    public boolean isCritico(LocalDate dataReferencia) {
        if (this.status != StatusLote.LIBERADO || this.quantidadeAtual == null || this.quantidadeAtual <= 0) {
            return false;
        }
        if (isVencido(dataReferencia)) {
            return false;
        }
        long dias = ChronoUnit.DAYS.between(dataReferencia, this.dataValidade);
        return dias >= 0 && dias <= 60;
    }

    /**
     * Verifica se o lote já ultrapassou a data de validade.
     */
    public boolean isVencido() {
        return isVencido(LocalDate.now());
    }

    public boolean isVencido(LocalDate dataReferencia) {
        return this.dataValidade != null && this.dataValidade.isBefore(dataReferencia);
    }

    /**
     * Retorna a quantidade de dias restantes até o vencimento.
     */
    public long getDiasParaVencimento() {
        return getDiasParaVencimento(LocalDate.now());
    }

    public long getDiasParaVencimento(LocalDate dataReferencia) {
        if (this.dataValidade == null) return 0;
        return ChronoUnit.DAYS.between(dataReferencia, this.dataValidade);
    }

    /**
     * Calcula o valor financeiro do estoque deste lote.
     */
    public BigDecimal getValorFinanceiroTotal() {
        if (this.quantidadeAtual == null || this.precoUnitario == null) {
            return BigDecimal.ZERO;
        }
        return this.precoUnitario.multiply(BigDecimal.valueOf(this.quantidadeAtual));
    }

    /**
     * Realiza a baixa de estoque validando barreiras sanitárias e saldo.
     * @param quantidade Quantidade a ser deduzida (deve ser > 0).
     */
    public void debitarEstoque(int quantidade) {
        debitarEstoque(quantidade, LocalDate.now());
    }

    public void debitarEstoque(int quantidade, LocalDate dataReferencia) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade a ser baixada deve ser estritamente maior que zero.");
        }
        if (isVencido(dataReferencia)) {
            this.status = StatusLote.VENCIDO;
            throw new ExpiredLotException(
                    "Operação abortada por barreira sanitária: O lote " + this.numeroLote + 
                    " está vencido desde " + this.dataValidade + " conforme RDC 430/2020.",
                    this.numeroLote, this.dataValidade);
        }
        if (this.status != StatusLote.LIBERADO) {
            throw new InvalidLotStatusException(
                    "Operação rejeitada: O lote " + this.numeroLote + " está em status " + this.status + 
                    " e não pode ser dispensado.", this.numeroLote, this.status.name());
        }
        if (this.quantidadeAtual < quantidade) {
            throw new InsufficientStockException(
                    "Operação abortada: Saldo insuficiente no lote " + this.numeroLote + 
                    ". Solicitado: " + quantidade + ", Disponível: " + this.quantidadeAtual + ".",
                    this.numeroLote, quantidade, this.quantidadeAtual);
        }

        this.quantidadeAtual -= quantidade;
        this.atualizadoEm = LocalDateTime.now();
    }

    public void creditarEstoque(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade a ser creditada deve ser maior que zero.");
        }
        this.quantidadeAtual += quantidade;
        this.atualizadoEm = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }

    public Medicamento getMedicamento() { return medicamento; }
    public void setMedicamento(Medicamento medicamento) { this.medicamento = medicamento; }

    public LocalDate getDataFabricacao() { return dataFabricacao; }
    public void setDataFabricacao(LocalDate dataFabricacao) { this.dataFabricacao = dataFabricacao; }

    public LocalDate getDataValidade() { return dataValidade; }
    public void setDataValidade(LocalDate dataValidade) { this.dataValidade = dataValidade; }

    public StatusLote getStatus() { return status; }
    public void setStatus(StatusLote status) { this.status = status; }

    public StatusLote getStatusLote() { return status; }
    public void setStatusLote(StatusLote status) { this.status = status; }

    public Integer getQuantidadeInicial() { return quantidadeInicial; }
    public void setQuantidadeInicial(Integer quantidadeInicial) { this.quantidadeInicial = quantidadeInicial; }

    public Integer getQuantidadeAtual() { return quantidadeAtual; }
    public void setQuantidadeAtual(Integer quantidadeAtual) { this.quantidadeAtual = quantidadeAtual; }

    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }

    public String getFornecedor() { return fornecedor; }
    public void setFornecedor(String fornecedor) { this.fornecedor = fornecedor; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }

    public List<Movimentacao> getMovimentacoes() { return movimentacoes; }
    public void setMovimentacoes(List<Movimentacao> movimentacoes) { this.movimentacoes = movimentacoes; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lote lote)) return false;
        return Objects.equals(numeroLote, lote.numeroLote);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroLote);
    }
}
