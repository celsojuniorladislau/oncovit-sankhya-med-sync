package com.oncovit.medsync.domain.model;

import com.oncovit.medsync.domain.enums.CategoriaMedicamento;
import com.oncovit.medsync.domain.enums.TipoArmazenamento;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "medicamentos", indexes = {
    @Index(name = "idx_med_codigo_sankhya", columnList = "codigo_sankhya", unique = true),
    @Index(name = "idx_med_categoria", columnList = "categoria"),
    @Index(name = "idx_med_armazenamento", columnList = "tipo_armazenamento")
})
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(name = "codigo_sankhya", nullable = false, unique = true, length = 50)
    private String codigoSankhya;

    @Size(max = 50)
    @Column(name = "codigo_sku", length = 50)
    private String codigoSku;

    @NotBlank
    @Size(max = 150)
    @Column(name = "nome_comercial", nullable = false, length = 150)
    private String nomeComercial;

    @NotBlank
    @Size(max = 150)
    @Column(name = "principio_ativo", nullable = false, length = 150)
    private String principioAtivo;

    @Size(max = 50)
    @Column(name = "dosagem", length = 50)
    private String dosagem;

    @Size(max = 100)
    @Column(name = "forma_farmaceutica", length = 100)
    private String formaFarmaceutica;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false, length = 50)
    private CategoriaMedicamento categoria;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_armazenamento", nullable = false, length = 30)
    private TipoArmazenamento tipoArmazenamento;

    @NotNull
    @Column(name = "temperatura_minima", nullable = false, precision = 4, scale = 1)
    private BigDecimal temperaturaMinima;

    @NotNull
    @Column(name = "temperatura_maxima", nullable = false, precision = 4, scale = 1)
    private BigDecimal temperaturaMaxima;

    @NotBlank
    @Size(max = 30)
    @Column(name = "unidade_medida", nullable = false, length = 30)
    private String unidadeMedida;

    @Size(max = 30)
    @Column(name = "registro_anvisa", length = 30)
    private String registroAnvisa;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @OneToMany(mappedBy = "medicamento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Lote> lotes = new ArrayList<>();

    public Medicamento() {
        this.criadoEm = LocalDateTime.now();
    }

    public Medicamento(String codigoSankhya, String nomeComercial, String principioAtivo,
                       CategoriaMedicamento categoria, TipoArmazenamento tipoArmazenamento,
                       BigDecimal temperaturaMinima, BigDecimal temperaturaMaxima, String unidadeMedida) {
        this();
        this.codigoSankhya = codigoSankhya;
        this.codigoSku = codigoSankhya;
        this.nomeComercial = nomeComercial;
        this.principioAtivo = principioAtivo;
        this.categoria = categoria;
        this.tipoArmazenamento = tipoArmazenamento;
        this.temperaturaMinima = temperaturaMinima;
        this.temperaturaMaxima = temperaturaMaxima;
        this.unidadeMedida = unidadeMedida;
    }

    @PrePersist
    protected void onCreate() {
        if (this.criadoEm == null) {
            this.criadoEm = LocalDateTime.now();
        }
        if (this.codigoSku == null) {
            this.codigoSku = this.codigoSankhya;
        }
        if (this.temperaturaMinima != null && this.temperaturaMaxima != null) {
            if (this.temperaturaMinima.compareTo(this.temperaturaMaxima) > 0) {
                throw new IllegalStateException("Temperatura mínima não pode ser maior que a temperatura máxima.");
            }
        }
    }

    // Métodos de Negócio
    public int getTotalEstoqueDisponivel() {
        return lotes.stream()
                .filter(l -> l.getStatus() != null && l.getStatus().permiteExpedicao() && !l.isVencido())
                .mapToInt(Lote::getQuantidadeAtual)
                .sum();
    }

    public boolean isTermolabil() {
        return this.tipoArmazenamento == TipoArmazenamento.CADEIA_DE_FRIO;
    }

    public void adicionarLote(Lote lote) {
        lotes.add(lote);
        lote.setMedicamento(this);
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigoSankhya() { return codigoSankhya; }
    public void setCodigoSankhya(String codigoSankhya) {
        this.codigoSankhya = codigoSankhya;
        if (this.codigoSku == null) {
            this.codigoSku = codigoSankhya;
        }
    }

    public String getCodigoSku() {
        return codigoSku != null ? codigoSku : codigoSankhya;
    }
    public void setCodigoSku(String codigoSku) { this.codigoSku = codigoSku; }

    public String getNomeComercial() { return nomeComercial; }
    public void setNomeComercial(String nomeComercial) { this.nomeComercial = nomeComercial; }

    public String getPrincipioAtivo() { return principioAtivo; }
    public void setPrincipioAtivo(String principioAtivo) { this.principioAtivo = principioAtivo; }

    public String getDosagem() { return dosagem; }
    public void setDosagem(String dosagem) { this.dosagem = dosagem; }

    public String getFormaFarmaceutica() { return formaFarmaceutica; }
    public void setFormaFarmaceutica(String formaFarmaceutica) { this.formaFarmaceutica = formaFarmaceutica; }

    public CategoriaMedicamento getCategoria() { return categoria; }
    public void setCategoria(CategoriaMedicamento categoria) { this.categoria = categoria; }

    public TipoArmazenamento getTipoArmazenamento() { return tipoArmazenamento; }
    public void setTipoArmazenamento(TipoArmazenamento tipoArmazenamento) { this.tipoArmazenamento = tipoArmazenamento; }

    public BigDecimal getTemperaturaMinima() { return temperaturaMinima; }
    public void setTemperaturaMinima(BigDecimal temperaturaMinima) { this.temperaturaMinima = temperaturaMinima; }

    public BigDecimal getTemperaturaMaxima() { return temperaturaMaxima; }
    public void setTemperaturaMaxima(BigDecimal temperaturaMaxima) { this.temperaturaMaxima = temperaturaMaxima; }

    public String getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(String unidadeMedida) { this.unidadeMedida = unidadeMedida; }

    public String getRegistroAnvisa() { return registroAnvisa; }
    public void setRegistroAnvisa(String registroAnvisa) { this.registroAnvisa = registroAnvisa; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public List<Lote> getLotes() { return lotes; }
    public void setLotes(List<Lote> lotes) { this.lotes = lotes; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medicamento that)) return false;
        return Objects.equals(codigoSankhya, that.codigoSankhya);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoSankhya);
    }
}
