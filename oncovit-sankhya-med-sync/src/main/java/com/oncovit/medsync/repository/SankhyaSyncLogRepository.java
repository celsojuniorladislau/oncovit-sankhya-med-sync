package com.oncovit.medsync.repository;

import com.oncovit.medsync.domain.model.SankhyaSyncLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SankhyaSyncLogRepository extends JpaRepository<SankhyaSyncLog, Long> {
    List<SankhyaSyncLog> findByNumeroNotaOrderByTimestampDesc(Long numeroNota);
    List<SankhyaSyncLog> findByNumeroLoteOrderByTimestampDesc(String numeroLote);
    List<SankhyaSyncLog> findByStatusTransacao(String statusTransacao);
}
