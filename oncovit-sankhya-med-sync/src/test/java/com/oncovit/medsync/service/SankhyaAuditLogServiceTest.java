package com.oncovit.medsync.service;

import com.oncovit.medsync.domain.model.SankhyaSyncLog;
import com.oncovit.medsync.repository.SankhyaSyncLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Unidade: SankhyaAuditLogService (Persistência Autônoma de Logs)")
class SankhyaAuditLogServiceTest {

    @Mock
    private SankhyaSyncLogRepository syncLogRepository;

    @InjectMocks
    private SankhyaAuditLogService auditLogService;

    @Test
    @DisplayName("Deve persistir log de auditoria com parâmetros válidos")
    void devePersistirLogComSucesso() {
        auditLogService.registrarLog(
                105420L, "LOT-HER-2024-01", 5,
                "SUCCESS", 200, "Baixa confirmada",
                "{\"nuNota\": 105420}", "{\"status\": \"SUCCESS\"}"
        );

        ArgumentCaptor<SankhyaSyncLog> captor = ArgumentCaptor.forClass(SankhyaSyncLog.class);
        verify(syncLogRepository, times(1)).save(captor.capture());

        SankhyaSyncLog saved = captor.getValue();
        assertEquals(105420L, saved.getNumeroNota());
        assertEquals("LOT-HER-2024-01", saved.getNumeroLote());
        assertEquals(5, saved.getQuantidade());
        assertEquals("SUCCESS", saved.getStatusTransacao());
        assertEquals(200, saved.getStatusHttp());
        assertEquals("Baixa confirmada", saved.getMensagemRetorno());
        assertEquals("/api/integracao/sankhya/baixa-estoque", saved.getEndpoint());
        assertNotNull(saved.getTimestamp());
    }

    @Test
    @DisplayName("Deve tratar lote nulo ou em branco atribuindo DESCONHECIDO para não violar constraint")
    void deveTratarLoteNulo() {
        auditLogService.registrarLog(
                null, null, 0,
                "UNAUTHORIZED", 401, "Autenticação recusada",
                null, null
        );

        ArgumentCaptor<SankhyaSyncLog> captor = ArgumentCaptor.forClass(SankhyaSyncLog.class);
        verify(syncLogRepository, times(1)).save(captor.capture());

        SankhyaSyncLog saved = captor.getValue();
        assertEquals("DESCONHECIDO", saved.getNumeroLote());
        assertNull(saved.getNumeroNota());
        assertEquals(401, saved.getStatusHttp());
    }
}
