package com.oncovit.medsync.config;

import com.oncovit.medsync.domain.enums.CategoriaMedicamento;
import com.oncovit.medsync.domain.enums.StatusLote;
import com.oncovit.medsync.domain.enums.TipoArmazenamento;
import com.oncovit.medsync.domain.enums.TipoMovimentacao;
import com.oncovit.medsync.domain.model.Lote;
import com.oncovit.medsync.domain.model.Medicamento;
import com.oncovit.medsync.domain.model.Movimentacao;
import com.oncovit.medsync.repository.LoteRepository;
import com.oncovit.medsync.repository.MedicamentoRepository;
import com.oncovit.medsync.repository.MovimentacaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataLoaderConfig {

    private static final Logger log = LoggerFactory.getLogger(DataLoaderConfig.class);

    @Bean
    public CommandLineRunner initDatabase(
            MedicamentoRepository medicamentoRepository,
            LoteRepository loteRepository,
            MovimentacaoRepository movimentacaoRepository) {

        return args -> {
            if (medicamentoRepository.count() > 0) {
                log.info("Catálogo farmacêutico já inicializado no banco de dados.");
                return;
            }

            log.info("Inicializando catálogo farmacêutico e lotes com sementes de homologação...");
            LocalDate hoje = LocalDate.now();

            // 1. Herceptin 440mg
            Medicamento herceptin = new Medicamento(
                    "MED-ONC-001", "Herceptin 440mg", "Trastuzumabe",
                    CategoriaMedicamento.ANTICORPO_MONOCLONAL, TipoArmazenamento.CADEIA_DE_FRIO,
                    new BigDecimal("2.0"), new BigDecimal("8.0"), "FRASCO_AMPOLA"
            );
            herceptin.setDosagem("440 mg");
            herceptin.setFormaFarmaceutica("Po liofilizado para solucao injetavel");
            herceptin.setRegistroAnvisa("1.0107.0123.001-2");
            medicamentoRepository.save(herceptin);

            Lote lotHer01 = new Lote("LOT-HER-2024-01", herceptin, hoje.minusMonths(6), hoje.plusDays(35),
                    StatusLote.LIBERADO, 100, new BigDecimal("7850.00"), "Roche Farma Brasil");
            lotHer01.setQuantidadeAtual(45);
            loteRepository.save(lotHer01);

            Lote lotHer02 = new Lote("LOT-HER-2024-02", herceptin, hoje.minusMonths(3), hoje.plusMonths(6),
                    StatusLote.LIBERADO, 150, new BigDecimal("7850.00"), "Roche Farma Brasil");
            lotHer02.setQuantidadeAtual(120);
            loteRepository.save(lotHer02);

            Lote lotQua01 = new Lote("LOT-QUA-2026-01", herceptin, hoje.minusDays(30), hoje.plusDays(45),
                    StatusLote.QUARENTENA, 50, new BigDecimal("7850.00"), "Roche Farma Brasil");
            lotQua01.setQuantidadeAtual(50);
            loteRepository.save(lotQua01);

            // 2. MabThera 500mg
            Medicamento mabthera = new Medicamento(
                    "MED-ONC-002", "MabThera 500mg", "Rituximabe",
                    CategoriaMedicamento.ANTICORPO_MONOCLONAL, TipoArmazenamento.CADEIA_DE_FRIO,
                    new BigDecimal("2.0"), new BigDecimal("8.0"), "FRASCO_AMPOLA"
            );
            mabthera.setDosagem("500 mg / 50 mL");
            mabthera.setFormaFarmaceutica("Solucao para infusao injetavel");
            mabthera.setRegistroAnvisa("1.0107.0145.002-1");
            medicamentoRepository.save(mabthera);

            Lote lotMab01 = new Lote("LOT-MAB-2024-01", mabthera, hoje.minusMonths(6), hoje.plusDays(15),
                    StatusLote.LIBERADO, 50, new BigDecimal("6950.00"), "Roche Farma Brasil");
            lotMab01.setQuantidadeAtual(20);
            loteRepository.save(lotMab01);

            Lote lotMabEx = new Lote("LOT-MAB-2023-EX", mabthera, hoje.minusYears(2), hoje.minusDays(20),
                    StatusLote.BLOQUEADO, 50, new BigDecimal("6950.00"), "Roche Farma Brasil");
            lotMabEx.setQuantidadeAtual(10);
            loteRepository.save(lotMabEx);

            // 3. Keytruda 100mg
            Medicamento keytruda = new Medicamento(
                    "MED-ONC-003", "Keytruda 100mg", "Pembrolizumabe",
                    CategoriaMedicamento.ANTICORPO_MONOCLONAL, TipoArmazenamento.CADEIA_DE_FRIO,
                    new BigDecimal("2.0"), new BigDecimal("8.0"), "FRASCO_AMPOLA"
            );
            keytruda.setDosagem("100 mg / 4 mL");
            keytruda.setFormaFarmaceutica("Solucao injetavel concentrada");
            keytruda.setRegistroAnvisa("1.0029.0205.001-4");
            medicamentoRepository.save(keytruda);

            Lote lotKey01 = new Lote("LOT-KEY-2024-01", keytruda, hoje.minusMonths(2), hoje.plusMonths(9),
                    StatusLote.LIBERADO, 100, new BigDecimal("14200.00"), "MSD Farmaceutica");
            lotKey01.setQuantidadeAtual(85);
            loteRepository.save(lotKey01);

            // 4. Genuxal 1000mg
            Medicamento genuxal = new Medicamento(
                    "MED-ONC-004", "Genuxal 1000mg", "Ciclofosfamida",
                    CategoriaMedicamento.ONCO_HEMATOLOGICO, TipoArmazenamento.AMBIENTE,
                    new BigDecimal("15.0"), new BigDecimal("25.0"), "FRASCO_AMPOLA"
            );
            genuxal.setDosagem("1000 mg");
            genuxal.setFormaFarmaceutica("Po para solucao injetavel");
            genuxal.setRegistroAnvisa("1.0573.0034.003-8");
            medicamentoRepository.save(genuxal);

            Lote lotGen01 = new Lote("LOT-GEN-2024-01", genuxal, hoje.minusMonths(5), hoje.plusDays(25),
                    StatusLote.LIBERADO, 200, new BigDecimal("145.00"), "Baxter Hospitalar");
            lotGen01.setQuantidadeAtual(180);
            loteRepository.save(lotGen01);

            // 5. Granulokine 300mcg
            Medicamento filgrastim = new Medicamento(
                    "MED-SUP-005", "Granulokine 300mcg", "Filgrastim",
                    CategoriaMedicamento.SUPORTE_ONCOLOGICO, TipoArmazenamento.CADEIA_DE_FRIO,
                    new BigDecimal("2.0"), new BigDecimal("8.0"), "SERINGA_PREENCHIDA"
            );
            filgrastim.setDosagem("300 mcg / 30 MUI");
            filgrastim.setFormaFarmaceutica("Solucao injetavel seringa preenchida");
            filgrastim.setRegistroAnvisa("1.0043.0789.001-9");
            medicamentoRepository.save(filgrastim);

            Lote lotFil01 = new Lote("LOT-FIL-2024-01", filgrastim, hoje.minusMonths(4), hoje.plusDays(48),
                    StatusLote.LIBERADO, 500, new BigDecimal("185.00"), "Amgen Biotecnologia");
            lotFil01.setQuantidadeAtual(320);
            loteRepository.save(lotFil01);

            // 6. Meronem 1000mg
            Medicamento meronem = new Medicamento(
                    "MED-HOS-006", "Meronem 1000mg", "Meropenem",
                    CategoriaMedicamento.HOSPITALAR_INJETAVEL, TipoArmazenamento.AMBIENTE,
                    new BigDecimal("15.0"), new BigDecimal("25.0"), "FRASCO_AMPOLA"
            );
            meronem.setDosagem("1000 mg");
            meronem.setFormaFarmaceutica("Po para solucao injetavel IV");
            meronem.setRegistroAnvisa("1.0180.0345.001-1");
            medicamentoRepository.save(meronem);

            Lote lotMer01 = new Lote("LOT-MER-2024-01", meronem, hoje.minusMonths(5), hoje.plusDays(52),
                    StatusLote.LIBERADO, 600, new BigDecimal("92.50"), "AstraZeneca do Brasil");
            lotMer01.setQuantidadeAtual(480);
            loteRepository.save(lotMer01);

            Lote lotMer02 = new Lote("LOT-MER-2024-02", meronem, hoje.minusMonths(2), hoje.plusMonths(10),
                    StatusLote.LIBERADO, 1500, new BigDecimal("92.50"), "AstraZeneca do Brasil");
            lotMer02.setQuantidadeAtual(1400);
            loteRepository.save(lotMer02);

            // 7. Clexane 40mg
            Medicamento clexane = new Medicamento(
                    "MED-HOS-007", "Clexane 40mg", "Enoxaparina Sodica",
                    CategoriaMedicamento.HOSPITALAR_INJETAVEL, TipoArmazenamento.AMBIENTE,
                    new BigDecimal("15.0"), new BigDecimal("25.0"), "SERINGA_PREENCHIDA"
            );
            clexane.setDosagem("40 mg / 0.4 mL");
            clexane.setFormaFarmaceutica("Solucao injetavel seringa preenchida");
            clexane.setRegistroAnvisa("1.1300.0245.002-3");
            medicamentoRepository.save(clexane);

            Lote lotCle01 = new Lote("LOT-CLE-2024-01", clexane, hoje.minusMonths(3), hoje.plusMonths(7),
                    StatusLote.LIBERADO, 1000, new BigDecimal("38.50"), "Sanofi Medley");
            lotCle01.setQuantidadeAtual(850);
            loteRepository.save(lotCle01);

            // 8. Zofran 8mg
            Medicamento zofran = new Medicamento(
                    "MED-SUP-008", "Zofran 8mg", "Cloridrato de Ondansetrona",
                    CategoriaMedicamento.SUPORTE_ONCOLOGICO, TipoArmazenamento.AMBIENTE,
                    new BigDecimal("15.0"), new BigDecimal("25.0"), "AMPOULA"
            );
            zofran.setDosagem("8 mg / 4 mL");
            zofran.setFormaFarmaceutica("Solucao injetavel IV/IM");
            zofran.setRegistroAnvisa("1.0107.0198.001-7");
            medicamentoRepository.save(zofran);

            Lote lotOnd01 = new Lote("LOT-OND-2024-01", zofran, hoje.minusMonths(4), hoje.plusDays(40),
                    StatusLote.LIBERADO, 400, new BigDecimal("28.00"), "GSK Brasil");
            lotOnd01.setQuantidadeAtual(250);
            loteRepository.save(lotOnd01);

            // Inserção de movimentações iniciais
            movimentacaoRepository.save(new Movimentacao(lotHer01, TipoMovimentacao.ENTRADA_NOTA, 100, "NF-10492", "Carga inicial de estoque", "farmaceutico.rt"));
            movimentacaoRepository.save(new Movimentacao(lotMab01, TipoMovimentacao.ENTRADA_NOTA, 50, "NF-09341", "Carga inicial de estoque", "farmaceutico.rt"));
            movimentacaoRepository.save(new Movimentacao(lotKey01, TipoMovimentacao.ENTRADA_NOTA, 100, "NF-11002", "Carga inicial de estoque", "farmaceutico.rt"));

            log.info("Carga inicial de dados concluída com sucesso: {} medicamentos e lotes ativos carregados.", medicamentoRepository.count());
        };
    }
}
