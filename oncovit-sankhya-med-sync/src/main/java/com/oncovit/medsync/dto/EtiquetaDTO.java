package com.oncovit.medsync.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "Estrutura completa da etiqueta térmica com dados estruturados e código ZPL II")
public class EtiquetaDTO {

    @Schema(description = "ID do lote", example = "10")
    private Long loteId;

    @Schema(description = "Número do lote", example = "LOT-TRA-2026A")
    private String numeroLote;

    @Schema(description = "Nome comercial do medicamento", example = "Herceptin 440mg")
    private String nomeMedicamento;

    @Schema(description = "Princípio ativo / DCB", example = "Trastuzumabe")
    private String principioAtivo;

    @Schema(description = "Classe de armazenamento", example = "CADEIA_DE_FRIO")
    private String tipoArmazenamento;

    @Schema(description = "Texto instrucional de temperatura", example = "2°C a 8°C (NÃO CONGELAR)")
    private String faixaTemperatura;

    @Schema(description = "Data de fabricação", example = "2025-11-15")
    private LocalDate dataFabricacao;

    @Schema(description = "Data de validade", example = "2026-11-15")
    private LocalDate dataValidade;

    @Schema(description = "Código de barras Code 128 / GTIN", example = "7891234567890")
    private String codigoBarras;

    @Schema(description = "Payload para renderização de DataMatrix ou QR Code rastreável", example = "https://oncovit.com.br/rastreio/LOT-TRA-2026A")
    private String dadosQrCode;

    @Schema(description = "Padrão de formato térmico", example = "ZPL_II_203DPI")
    private String formato;

    @Schema(description = "Comando raw ZPL II para envio direto ao spooler de impressão Zebra", example = "^XA^PW800^LL480^CI28...^XZ")
    private String conteudoZpl;

    @Schema(description = "Quantidade física disponível no lote", example = "30")
    private Integer quantidadeDisponivel;

    @Schema(description = "Status do lote", example = "LIBERADO")
    private String status;

    @Schema(description = "Dias restantes até o vencimento", example = "27")
    private Long diasParaVencimento;

    public EtiquetaDTO() {}

    public EtiquetaDTO(Long loteId, String numeroLote, String nomeMedicamento, String principioAtivo,
                       String tipoArmazenamento, String faixaTemperatura, LocalDate dataFabricacao,
                       LocalDate dataValidade, String codigoBarras, String dadosQrCode,
                       String formato, String conteudoZpl, Integer quantidadeDisponivel,
                       String status, Long diasParaVencimento) {
        this.loteId = loteId;
        this.numeroLote = numeroLote;
        this.nomeMedicamento = nomeMedicamento;
        this.principioAtivo = principioAtivo;
        this.tipoArmazenamento = tipoArmazenamento;
        this.faixaTemperatura = faixaTemperatura;
        this.dataFabricacao = dataFabricacao;
        this.dataValidade = dataValidade;
        this.codigoBarras = codigoBarras;
        this.dadosQrCode = dadosQrCode;
        this.formato = formato;
        this.conteudoZpl = conteudoZpl;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.status = status;
        this.diasParaVencimento = diasParaVencimento;
    }

    public EtiquetaDTO(Long loteId, String numeroLote, String nomeMedicamento, String principioAtivo,
                       String tipoArmazenamento, String faixaTemperatura, LocalDate dataFabricacao,
                       LocalDate dataValidade, String codigoBarras, String dadosQrCode,
                       String formato, String conteudoZpl, Integer quantidadeDisponivel) {
        this(loteId, numeroLote, nomeMedicamento, principioAtivo, tipoArmazenamento, faixaTemperatura,
             dataFabricacao, dataValidade, codigoBarras, dadosQrCode, formato, conteudoZpl,
             quantidadeDisponivel, null, null);
    }

    public EtiquetaDTO(Long loteId, String numeroLote, String nomeMedicamento, String principioAtivo,
                       String tipoArmazenamento, String faixaTemperatura, LocalDate dataFabricacao,
                       LocalDate dataValidade, String codigoBarras, String dadosQrCode,
                       String formato, String conteudoZpl) {
        this(loteId, numeroLote, nomeMedicamento, principioAtivo, tipoArmazenamento, faixaTemperatura,
             dataFabricacao, dataValidade, codigoBarras, dadosQrCode, formato, conteudoZpl, null);
    }

    // Standard JavaBeans Getters and Setters
    public Long getLoteId() { return loteId; }
    public void setLoteId(Long loteId) { this.loteId = loteId; }

    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }

    public String getNomeMedicamento() { return nomeMedicamento; }
    public void setNomeMedicamento(String nomeMedicamento) { this.nomeMedicamento = nomeMedicamento; }

    public String getPrincipioAtivo() { return principioAtivo; }
    public void setPrincipioAtivo(String principioAtivo) { this.principioAtivo = principioAtivo; }

    public String getTipoArmazenamento() { return tipoArmazenamento; }
    public void setTipoArmazenamento(String tipoArmazenamento) { this.tipoArmazenamento = tipoArmazenamento; }

    public String getFaixaTemperatura() { return faixaTemperatura; }
    public void setFaixaTemperatura(String faixaTemperatura) { this.faixaTemperatura = faixaTemperatura; }

    public LocalDate getDataFabricacao() { return dataFabricacao; }
    public void setDataFabricacao(LocalDate dataFabricacao) { this.dataFabricacao = dataFabricacao; }

    public LocalDate getDataValidade() { return dataValidade; }
    public void setDataValidade(LocalDate dataValidade) { this.dataValidade = dataValidade; }

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public String getDadosQrCode() { return dadosQrCode; }
    public void setDadosQrCode(String dadosQrCode) { this.dadosQrCode = dadosQrCode; }

    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }

    public String getConteudoZpl() { return conteudoZpl; }
    public void setConteudoZpl(String conteudoZpl) { this.conteudoZpl = conteudoZpl; }

    public Integer getQuantidadeDisponivel() { return quantidadeDisponivel; }
    public void setQuantidadeDisponivel(Integer quantidadeDisponivel) { this.quantidadeDisponivel = quantidadeDisponivel; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getDiasParaVencimento() { return diasParaVencimento; }
    public void setDiasParaVencimento(Long diasParaVencimento) { this.diasParaVencimento = diasParaVencimento; }

    // Record-style compatibility accessors
    public Long loteId() { return loteId; }
    public String numeroLote() { return numeroLote; }
    public String nomeMedicamento() { return nomeMedicamento; }
    public String principioAtivo() { return principioAtivo; }
    public String tipoArmazenamento() { return tipoArmazenamento; }
    public String faixaTemperatura() { return faixaTemperatura; }
    public LocalDate dataFabricacao() { return dataFabricacao; }
    public LocalDate dataValidade() { return dataValidade; }
    public String codigoBarras() { return codigoBarras; }
    public String dadosQrCode() { return dadosQrCode; }
    public String formato() { return formato; }
    public String conteudoZpl() { return conteudoZpl; }
    public Integer quantidadeDisponivel() { return quantidadeDisponivel; }
    public String status() { return status; }
    public Long diasParaVencimento() { return diasParaVencimento; }
}
