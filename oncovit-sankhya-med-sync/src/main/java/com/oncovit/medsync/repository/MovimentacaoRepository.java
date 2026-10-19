package com.oncovit.medsync.repository;

import com.oncovit.medsync.domain.model.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {
    List<Movimentacao> findByLoteIdOrderByDataHoraDesc(Long loteId);
    List<Movimentacao> findByDocumentoReferencia(String documentoReferencia);
}
