package com.oncovit.medsync.repository;

import com.oncovit.medsync.domain.enums.StatusLote;
import com.oncovit.medsync.domain.model.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {

    @Query("SELECT l FROM Lote l JOIN FETCH l.medicamento WHERE l.numeroLote = :numeroLote")
    Optional<Lote> findByNumeroLoteComMedicamento(@Param("numeroLote") String numeroLote);

    Optional<Lote> findByNumeroLote(String numeroLote);

    @Query("SELECT l FROM Lote l JOIN FETCH l.medicamento WHERE l.medicamento.id = :medicamentoId ORDER BY l.dataValidade ASC")
    List<Lote> findByMedicamentoIdOrdenadoPorValidade(@Param("medicamentoId") Long medicamentoId);

    /**
     * Consulta FEFO (First Expired, First Out):
     * Retorna lotes disponíveis para faturamento ordenados estritamente pela data de validade mais próxima.
     */
    @Query("""
        SELECT l FROM Lote l 
        JOIN FETCH l.medicamento m 
        WHERE (m.codigoSankhya = :codigoSankhya OR m.codigoSku = :codigoSankhya) 
          AND l.status = com.oncovit.medsync.domain.enums.StatusLote.LIBERADO 
          AND l.quantidadeAtual > 0 
          AND l.dataValidade >= :dataReferencia 
        ORDER BY l.dataValidade ASC
    """)
    List<Lote> findDisponiveisPorFefo(
            @Param("codigoSankhya") String codigoSankhya, 
            @Param("dataReferencia") LocalDate dataReferencia
    );

    /**
     * Consulta de Lotes Críticos (< 60 dias de validade):
     * Filtra lotes com status LIBERADO, saldo > 0, dentro da janela [dataAtual, dataLimite],
     * ordenados por data de validade ascendente (FEFO).
     */
    @Query("""
        SELECT l FROM Lote l 
        JOIN FETCH l.medicamento m 
        WHERE l.status = com.oncovit.medsync.domain.enums.StatusLote.LIBERADO 
          AND l.quantidadeAtual > 0 
          AND l.dataValidade >= :dataAtual 
          AND l.dataValidade <= :dataLimite 
        ORDER BY l.dataValidade ASC
    """)
    List<Lote> findLotesCriticos(
            @Param("dataAtual") LocalDate dataAtual, 
            @Param("dataLimite") LocalDate dataLimite
    );

    /**
     * Identifica lotes que ultrapassaram a data de validade mas ainda constam como LIBERADO.
     */
    @Query("""
        SELECT l FROM Lote l 
        WHERE l.status = com.oncovit.medsync.domain.enums.StatusLote.LIBERADO 
          AND l.dataValidade < :dataAtual
    """)
    List<Lote> findLotesExpiradosNaoAtualizados(@Param("dataAtual") LocalDate dataAtual);
}
