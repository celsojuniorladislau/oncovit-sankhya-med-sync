package com.oncovit.medsync.service;

import com.oncovit.medsync.domain.enums.CategoriaMedicamento;
import com.oncovit.medsync.domain.enums.StatusLote;
import com.oncovit.medsync.domain.enums.TipoArmazenamento;
import com.oncovit.medsync.domain.model.Lote;
import com.oncovit.medsync.domain.model.Medicamento;
import com.oncovit.medsync.dto.EtiquetaDTO;
import com.oncovit.medsync.exception.ResourceNotFoundException;
import com.oncovit.medsync.repository.LoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Serviço: EtiquetaZplService (ZPL II 203 DPI e Tarja Invertida)")
class EtiquetaZplServiceTest {

    @Mock
    private LoteRepository loteRepository;

    @InjectMocks
    private EtiquetaZplService etiquetaZplService;

    private Medicamento medFrio;
    private Medicamento medAmbiente;
    private Lote loteFrio;
    private Lote loteAmbiente;

    @BeforeEach
    void setUp() {
        medFrio = new Medicamento("MED-ONC-001", "Herceptin 440mg", "Trastuzumabe",
                CategoriaMedicamento.ANTICORPO_MONOCLONAL, TipoArmazenamento.CADEIA_DE_FRIO,
                new BigDecimal("2.0"), new BigDecimal("8.0"), "FRASCO_AMPOLA");
        medFrio.setId(1L);
        medFrio.setDosagem("440 mg");
        medFrio.setFormaFarmaceutica("Po liofilizado");
        medFrio.setRegistroAnvisa("1.0107.0123.001-2");

        loteFrio = new Lote("LOT-HER-2024-01", medFrio, LocalDate.of(2025, 11, 20), LocalDate.of(2026, 11, 23),
                StatusLote.LIBERADO, 100, new BigDecimal("7850.00"), "Roche Farma Brasil");
        loteFrio.setId(10L);
        loteFrio.setQuantidadeAtual(45);

        medAmbiente = new Medicamento("MED-HOS-006", "Meronem 1000mg", "Meropenem",
                CategoriaMedicamento.HOSPITALAR_INJETAVEL, TipoArmazenamento.AMBIENTE,
                new BigDecimal("15.0"), new BigDecimal("25.0"), "FRASCO_AMPOLA");
        medAmbiente.setId(6L);
        medAmbiente.setDosagem("1000 mg");
        medAmbiente.setFormaFarmaceutica("Po injetavel");
        medAmbiente.setRegistroAnvisa("1.0180.0345.001-1");

        loteAmbiente = new Lote("LOT-MER-2024-01", medAmbiente, LocalDate.of(2025, 12, 20), LocalDate.of(2026, 12, 10),
                StatusLote.LIBERADO, 600, new BigDecimal("92.50"), "AstraZeneca");
        loteAmbiente.setId(20L);
        loteAmbiente.setQuantidadeAtual(480);
    }

    @Test
    @DisplayName("Deve gerar ZPL com tarja invertida e bloco escuro para medicamentos de Cadeia de Frio")
    void deveGerarZplCadeiaFrioComTarjaInvertida() {
        when(loteRepository.findById(10L)).thenReturn(Optional.of(loteFrio));

        EtiquetaDTO dto = etiquetaZplService.gerarEtiquetaPorLoteId(10L);

        assertNotNull(dto);
        assertEquals(10L, dto.loteId());
        assertEquals("LOT-HER-2024-01", dto.numeroLote());
        assertEquals("CADEIA_DE_FRIO", dto.tipoArmazenamento());
        assertEquals("ZPL_II_203DPI", dto.formato());
        assertEquals(45, dto.quantidadeDisponivel());

        String zpl = dto.conteudoZpl();
        assertNotNull(zpl);
        assertTrue(zpl.startsWith("^XA"));
        assertTrue(zpl.endsWith("^XZ"));
        assertTrue(zpl.contains("^PW800"));
        assertTrue(zpl.contains("^LL480"));
        assertTrue(zpl.contains("^CI28"));

        // Destaque crítico de cadeia de frio: caixa preta sólida com ^GB740,50,50 e ^FR
        assertTrue(zpl.contains("^FO30,205^GB740,50,50^FS"));
        assertTrue(zpl.contains("^FR^FDCONSERVAR ENTRE 2C E 8C - TERMOLABIL / NAO CONGELAR^FS"));

        // Códigos de barras Code 128 e DataMatrix
        assertTrue(zpl.contains("^BCN,60,Y,N,N^FDLOT-HER-2024-01^FS"));
        assertTrue(zpl.contains("^BXN,5,200^FD010789123456789010LOT-HER-2024-0117261123^FS"));
    }

    @Test
    @DisplayName("Deve gerar ZPL com moldura regular para medicamentos de Temperatura Ambiente")
    void deveGerarZplAmbienteComMolduraRegular() {
        when(loteRepository.findById(20L)).thenReturn(Optional.of(loteAmbiente));

        EtiquetaDTO dto = etiquetaZplService.gerarEtiquetaPorLoteId(20L);

        assertNotNull(dto);
        assertEquals("AMBIENTE", dto.tipoArmazenamento());

        String zpl = dto.conteudoZpl();
        assertTrue(zpl.contains("^FO30,205^GB740,50,2^FS"));
        assertTrue(zpl.contains("CONSERVAR ENTRE 15C E 25C - TEMPERATURA AMBIENTE"));
        assertFalse(zpl.contains("^GB740,50,50"));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao solicitar etiqueta de lote inexistente")
    void deveLancarExcecaoLoteInexistente() {
        when(loteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                etiquetaZplService.gerarEtiquetaPorLoteId(999L));
    }
}
