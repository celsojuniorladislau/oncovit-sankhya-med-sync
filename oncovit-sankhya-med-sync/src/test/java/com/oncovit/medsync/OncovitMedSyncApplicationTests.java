package com.oncovit.medsync;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OncovitMedSyncApplicationTests {

    @Test
    @DisplayName("Garante que o Spring ApplicationContext sobe com sucesso com o banco H2 e beans configurados")
    void contextLoads() {
        // Validação de startup do contexto Spring
    }
}
