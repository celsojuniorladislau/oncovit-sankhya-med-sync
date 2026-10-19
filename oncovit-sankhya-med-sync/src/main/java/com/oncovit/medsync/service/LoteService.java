package com.oncovit.medsync.service;

import com.oncovit.medsync.domain.enums.StatusLote;
import com.oncovit.medsync.domain.model.Lote;
import com.oncovit.medsync.dto.LoteDTO;
import com.oncovit.medsync.exception.ResourceNotFoundException;
import com.oncovit.medsync.repository.LoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoteService {

    private static final Logger log = LoggerFactory.getLogger(LoteService.class);
    private final LoteRepository loteRepository;

    public LoteService(LoteRepository loteRepository) {
        this.loteRepository = loteRepository;
    }

    /**
     * Retorna os lotes em validade crítica (<= 60 dias da data atual), ordenados por FEFO.
     */
    @Transactional(readOnly = true)
    public List<LoteDTO> listarLotesCriticos() {
        return listarLotesCriticos(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<LoteDTO> listarLotesCriticos(LocalDate dataReferencia) {
        LocalDate dataLimite = dataReferencia.plusDays(60);
        List<Lote> lotes = loteRepository.findLotesCriticos(dataReferencia, dataLimite);
        return lotes.stream()
                .map(lote -> LoteDTO.fromEntity(lote, dataReferencia))
                .toList();
    }

    /**
     * Busca um lote por número identificador único.
     */
    @Transactional(readOnly = true)
    public Lote buscarPorNumeroLote(String numeroLote) {
        return loteRepository.findByNumeroLoteComMedicamento(numeroLote)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Lote '" + numeroLote + "' não encontrado no catálogo da Oncovit.",
                        "LOT_NOT_FOUND", 404));
    }

    /**
     * Busca um lote por ID.
     */
    @Transactional(readOnly = true)
    public Lote buscarPorId(Long id) {
        return loteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Lote com ID " + id + " não encontrado.",
                        "LOT_NOT_FOUND", 404));
    }

    /**
     * Retorna lotes disponíveis para faturamento ordenados por FEFO (First Expired, First Out).
     */
    @Transactional(readOnly = true)
    public List<Lote> buscarLotesDisponiveisFefo(String codigoSankhya) {
        return loteRepository.findDisponiveisPorFefo(codigoSankhya, LocalDate.now());
    }

    /**
     * Tarefa de saneamento: atualiza status para VENCIDO de lotes com data anterior à referência.
     */
    @Transactional
    public int atualizarLotesExpirados(LocalDate dataReferencia) {
        List<Lote> expirados = loteRepository.findLotesExpiradosNaoAtualizados(dataReferencia);
        for (Lote lote : expirados) {
            log.warn("Bloqueando lote vencido por sanitização: {} (Validade: {})", lote.getNumeroLote(), lote.getDataValidade());
            lote.setStatus(StatusLote.VENCIDO);
        }
        return expirados.size();
    }
}
