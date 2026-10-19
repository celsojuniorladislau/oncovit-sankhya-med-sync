package com.oncovit.medsync.repository;

import com.oncovit.medsync.domain.enums.CategoriaMedicamento;
import com.oncovit.medsync.domain.enums.TipoArmazenamento;
import com.oncovit.medsync.domain.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

    Optional<Medicamento> findByCodigoSankhya(String codigoSankhya);

    Optional<Medicamento> findByCodigoSku(String codigoSku);

    List<Medicamento> findByCategoria(CategoriaMedicamento categoria);

    List<Medicamento> findByTipoArmazenamento(TipoArmazenamento tipoArmazenamento);

    @Query("SELECT DISTINCT m FROM Medicamento m LEFT JOIN FETCH m.lotes ORDER BY m.nomeComercial ASC")
    List<Medicamento> findAllComLotes();

    @Query("SELECT m FROM Medicamento m LEFT JOIN FETCH m.lotes WHERE m.id = :id")
    Optional<Medicamento> findByIdComLotes(@Param("id") Long id);

    boolean existsByCodigoSankhya(String codigoSankhya);
}
