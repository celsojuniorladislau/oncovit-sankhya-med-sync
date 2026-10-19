package com.oncovit.medsync.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "sankhya_sync_log", indexes = {
    @Index(name = "idx_sync_numero_nota", columnList = "numero_nota"),
    @Index(name = "idx_sync_numero_lote", columnList = "numero_lote"),
    @Index(name = "idx_sync_timestamp", columnList = "timestamp_sync"),
    @Index(name = "idx_sync_status", columnList = "status_transacao")
})
public class SankhyaSyncLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "timestamp_sync", nullable = false)
    private LocalDateTime timestamp;

    @NotBlank
    @Size(max = 100)
    @Column(name = "endpoint", nullable = false, length = 100)
    private String endpoint;

    @Column(name = "numero_nota")
    private Long numeroNota;

    @NotBlank
    @Size(max = 50)
    @Column(name = "numero_lote", nullable = false, length = 50)
    private String numeroLote;

    @NotNull
    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @NotBlank
    @Size(max = 50)
    @Column(name = "status_transacao", nullable = false, length = 50)
    private String statusTransacao;

    @NotNull
    @Column(name = "status_http", nullable = false)
    private Integer statusHttp;

    @Size(max = 500)
    @Column(name = "mensagem_retorno", length = 500)
    private String mensagemRetorno;

    @Lob
    @Column(name = "payload_requisicao", columnDefinition = "TEXT")
    private String payloadRequisicao;

    @Lob
    @Column(name = "payload_resposta", columnDefinition = "TEXT")
    private String payloadResposta;

    public SankhyaSyncLog() {
        this.timestamp = LocalDateTime.now();
    }

    public SankhyaSyncLog(String endpoint, Long numeroNota, String numeroLote,
                          Integer quantidade, String statusTransacao, Integer statusHttp,
                          String mensagemRetorno, String payloadRequisicao, String payloadResposta) {
        this();
        this.endpoint = endpoint;
        this.numeroNota = numeroNota;
        this.numeroLote = numeroLote;
        this.quantidade = quantidade;
        this.statusTransacao = statusTransacao;
        this.statusHttp = statusHttp;
        this.mensagemRetorno = mensagemRetorno;
        this.payloadRequisicao = payloadRequisicao;
        this.payloadResposta = payloadResposta;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public Long getNumeroNota() { return numeroNota; }
    public void setNumeroNota(Long numeroNota) { this.numeroNota = numeroNota; }

    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public String getStatusTransacao() { return statusTransacao; }
    public void setStatusTransacao(String statusTransacao) { this.statusTransacao = statusTransacao; }

    public Integer getStatusHttp() { return statusHttp; }
    public void setStatusHttp(Integer statusHttp) { this.statusHttp = statusHttp; }

    public String getMensagemRetorno() { return mensagemRetorno; }
    public void setMensagemRetorno(String mensagemRetorno) { this.mensagemRetorno = mensagemRetorno; }

    public String getPayloadRequisicao() { return payloadRequisicao; }
    public void setPayloadRequisicao(String payloadRequisicao) { this.payloadRequisicao = payloadRequisicao; }

    public String getPayloadResposta() { return payloadResposta; }
    public void setPayloadResposta(String payloadResposta) { this.payloadResposta = payloadResposta; }
}
