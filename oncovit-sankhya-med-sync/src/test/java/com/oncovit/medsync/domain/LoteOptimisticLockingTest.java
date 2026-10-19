package com.oncovit.medsync.domain;

import com.oncovit.medsync.domain.model.Lote;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Controle de Concorrência: Lote (@Version)")
class LoteOptimisticLockingTest {

    @Test
    @DisplayName("Lote deve suportar atributo de versão para controle de concorrência otimista")
    void devePermitirDefinirERecuperarVersao() {
        Lote lote = new Lote();
        assertNull(lote.getVersion(), "Versão inicial deve ser nula antes da primeira persistência JPA");

        lote.setVersion(1L);
        assertEquals(1L, lote.getVersion(), "Getter e Setter de versão devem operar corretamente");
    }
}
