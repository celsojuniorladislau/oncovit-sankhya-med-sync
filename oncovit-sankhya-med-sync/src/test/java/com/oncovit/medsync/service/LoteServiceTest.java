package com.oncovit.medsync.service;

import com.oncovit.medsync.domain.enums.CategoriaMedicamento;
import com.oncovit.medsync.domain.enums.StatusLote;
import com.oncovit.medsync.domain.enums.TipoArmazenamento;
import com.oncovit.medsync.domain.model.Lote;
import com.oncovit.medsync.domain.model.Medicamento;
import com.oncovit.medsync.dto.LoteDTO;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Serviço: LoteService (FEFO e Consultas Críticas)")
class LoteServiceTest {

    @Mock
    private LoteRepository loteRepository;

    @InjectMocks
    private LoteService loteService;

    private final LocalDate hoje = LocalDate.of(2026, 10, 19);
    private Medicamento medHerceptin;
    private Lote loteMaisUrgente;
    private Lote loteMenosUrgente;

    @BeforeEach
    void setUp() {
        medHerceptin = new Medicamento(
                "MED-ONC-001", "Herceptin", "Trastuzumabe",
                CategoriaMedicamento.ANTICORPO_MONOCLONAL, TipoArmazenamento.CADEIA_DE_FRIO,
                new BigDecimal("2.0"), new BigDecimal("8.0"), "FRASCO_AMPOLA"
        );
        medHerceptin.setId(1L);

        // Vence em 15 dias (mais urgente no FEFO)
        loteMaisUrgente = new Lote("LOT-URG-15D", medHerceptin, hoje.minusMonths(5), hoje.plusDays(15),
                StatusLote.LIBERADO, 20, new BigDecimal("7850.00"), "Roche Farma");
        loteMaisUrgente.setId(101L);

        // Vence em 45 dias
        loteMenosUrgente = new Lote("LOT-REG-45D", medHerceptin, hoje.minusMonths(4), hoje.plusDays(45),
                StatusLote.LIBERADO, 50, new BigDecimal("7850.00"), "Roche Farma");
        loteMenosUrgente.setId(102L);
    }

    @Test
    @DisplayName("Deve listar lotes críticos ordenados por FEFO (menor validade primeiro)")
    void deveListarLotesCriticosOrdenadosPorFefo() {
        LocalDate dataLimite = hoje.plusDays(60);
        when(loteRepository.findLotesCriticos(eq(hoje), eq(dataLimite)))
                .thenReturn(List.of(loteMaisUrgente, loteMenosUrgente));

        List<LoteDTO> criticos = loteService.listarLotesCriticos(hoje);

        assertNotNull(criticos);
        assertEquals(2, criticos.size());
        assertEquals("LOT-URG-15D", criticos.get(0).numeroLote());
        assertEquals(15, criticos.get(0).diasParaVencimento());
        assertTrue(criticos.get(0).alertaCritico());

        assertEquals("LOT-REG-45D", criticos.get(1).numeroLote());
        assertEquals(45, criticos.get(1).diasParaVencimento());
        assertTrue(criticos.get(1).alertaCritico());

        verify(loteRepository, times(1)).findLotesCriticos(hoje, dataLimite);
    }

    @Test
    @DisplayName("Deve buscar lote por número com sucesso quando existente")
    void deveBuscarPorNumeroLoteComSucesso() {
        when(loteRepository.findByNumeroLoteComMedicamento("LOT-URG-15D"))
                .thenReturn(Optional.of(loteMaisUrgente));

        Lote resultado = loteService.buscarPorNumeroLote("LOT-URG-15D");

        assertNotNull(resultado);
        assertEquals("LOT-URG-15D", resultado.getNumeroLote());
        assertEquals("Herceptin", resultado.getMedicamento().getNomeComercial());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o lote não for encontrado")
    void deveLancarExcecaoLoteNaoEncontrado() {
        when(loteRepository.findByNumeroLoteComMedicamento("LOT-INEXISTENTE"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                loteService.buscarPorNumeroLote("LOT-INEXISTENTE"));
    }
}
