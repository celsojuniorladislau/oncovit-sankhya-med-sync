package com.oncovit.medsync.service;

import com.oncovit.medsync.dto.MedicamentoDTO;
import com.oncovit.medsync.exception.ResourceNotFoundException;
import com.oncovit.medsync.repository.MedicamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;

    public MedicamentoService(MedicamentoRepository medicamentoRepository) {
        this.medicamentoRepository = medicamentoRepository;
    }

    public List<MedicamentoDTO> listarTodos() {
        return medicamentoRepository.findAllComLotes().stream()
                .map(MedicamentoDTO::fromEntity)
                .toList();
    }

    public List<MedicamentoDTO> listarTodosComEstoqueDisponivel() {
        return listarTodos();
    }

    public MedicamentoDTO buscarPorId(Long id) {
        return medicamentoRepository.findByIdComLotes(id)
                .map(MedicamentoDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Medicamento com ID " + id + " não encontrado.", "PRODUCT_NOT_FOUND", 404));
    }

    public MedicamentoDTO buscarPorCodigoSankhya(String codigoSankhya) {
        return medicamentoRepository.findByCodigoSankhya(codigoSankhya)
                .map(MedicamentoDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Medicamento com código Sankhya " + codigoSankhya + " não encontrado.", "PRODUCT_NOT_FOUND", 404));
    }
}
