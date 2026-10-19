package com.oncovit.medsync.domain.model;

import com.oncovit.medsync.domain.enums.TipoMovimentacao;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacoes", indexes = {
    @Index(name = "idx_mov_lote", columnList = "lote_id"),
    @Index(name = "idx_mov_data_hora", columnList = "data_hora"),
    @Index(name = "idx_mov_doc_ref", columnList = "documento_referencia")
})
public class Movimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimentacao", nullable = false, length = 50)
    private TipoMovimentacao tipoMovimentacao;

    @NotNull
    @Min(1)
    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @NotNull
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Size(max = 100)
    @Column(name = "documento_referencia", length = 100)
    private String documentoReferencia;

    @Size(max = 255)
    @Column(name = "motivo", length = 255)
    private String motivo;

    @Size(max = 100)
    @Column(name = "usuario_responsavel", length = 100)
    private String usuarioResponsavel;

    public Movimentacao() {
        this.dataHora = LocalDateTime.now();
    }

    public Movimentacao(Lote lote, TipoMovimentacao tipoMovimentacao, Integer quantidade,
                        String documentoReferencia, String motivo, String usuarioResponsavel) {
        this();
        this.lote = lote;
        this.tipoMovimentacao = tipoMovimentacao;
        this.quantidade = quantidade;
        this.documentoReferencia = documentoReferencia;
        this.motivo = motivo;
        this.usuarioResponsavel = usuarioResponsavel;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Lote getLote() { return lote; }
    public void setLote(Lote lote) { this.lote = lote; }

    public TipoMovimentacao getTipoMovimentacao() { return tipoMovimentacao; }
    public void setTipoMovimentacao(TipoMovimentacao tipoMovimentacao) { this.tipoMovimentacao = tipoMovimentacao; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public String getDocumentoReferencia() { return documentoReferencia; }
    public void setDocumentoReferencia(String documentoReferencia) { this.documentoReferencia = documentoReferencia; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getUsuarioResponsavel() { return usuarioResponsavel; }
    public void setUsuarioResponsavel(String usuarioResponsavel) { this.usuarioResponsavel = usuarioResponsavel; }
}
