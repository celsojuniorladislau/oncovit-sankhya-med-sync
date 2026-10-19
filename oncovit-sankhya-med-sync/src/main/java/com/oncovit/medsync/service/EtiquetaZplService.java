package com.oncovit.medsync.service;

import com.oncovit.medsync.domain.enums.TipoArmazenamento;
import com.oncovit.medsync.domain.model.Lote;
import com.oncovit.medsync.domain.model.Medicamento;
import com.oncovit.medsync.dto.EtiquetaDTO;
import com.oncovit.medsync.exception.ResourceNotFoundException;
import com.oncovit.medsync.repository.LoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EtiquetaZplService {

    private final LoteRepository loteRepository;

    public EtiquetaZplService(LoteRepository loteRepository) {
        this.loteRepository = loteRepository;
    }

    @Transactional(readOnly = true)
    public EtiquetaDTO gerarEtiquetaPorLoteId(Long loteId) {
        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new ResourceNotFoundException("Lote com ID " + loteId + " não encontrado.", "LOT_NOT_FOUND", 404));

        return gerarEtiqueta(lote);
    }

    /**
     * Gera a etiqueta a partir da entidade Lote populando metadados e quantidadeDisponivel.
     */
    public EtiquetaDTO gerarEtiqueta(Lote lote) {
        Medicamento med = lote.getMedicamento();
        String zplCode = formatarZpl(lote, med);

        String faixaTemp = (med.getTipoArmazenamento() != null && med.getTipoArmazenamento() == TipoArmazenamento.CADEIA_DE_FRIO)
                ? "2°C a 8°C (NÃO CONGELAR)"
                : "15°C a 25°C (TEMPERATURA AMBIENTE)";

        String gtin = "7891234567890";
        String dadosQrCode = "https://oncovit.com.br/rastreio/" + lote.getNumeroLote();

        EtiquetaDTO dto = new EtiquetaDTO(
                lote.getId(),
                lote.getNumeroLote(),
                med.getNomeComercial(),
                med.getPrincipioAtivo(),
                med.getTipoArmazenamento() != null ? med.getTipoArmazenamento().name() : "AMBIENTE",
                faixaTemp,
                lote.getDataFabricacao(),
                lote.getDataValidade(),
                gtin,
                dadosQrCode,
                "ZPL_II_203DPI",
                zplCode,
                lote.getQuantidadeAtual() != null ? lote.getQuantidadeAtual() : 0,
                lote.getStatus() != null ? lote.getStatus().name() : "DESCONHECIDO",
                lote.getDiasParaVencimento()
        );
        return dto;
    }

    public String formatarZpl(Lote lote, Medicamento med) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dtfValGs1 = DateTimeFormatter.ofPattern("yyMMdd");
        DateTimeFormatter dtfNow = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String dataFabStr = lote.getDataFabricacao() != null ? lote.getDataFabricacao().format(dtf) : "";
        String dataValStr = lote.getDataValidade() != null ? lote.getDataValidade().format(dtf) : "";
        String valGs1 = lote.getDataValidade() != null ? lote.getDataValidade().format(dtfValGs1) : "991231";
        String agoraStr = LocalDateTime.now().format(dtfNow);

        boolean isFrio = med.getTipoArmazenamento() == TipoArmazenamento.CADEIA_DE_FRIO;

        String tarjaZpl = isFrio
                ? "^FO30,205^GB740,50,50^FS\n" +
                  "^FO50,220^A0N,24,22^FR^FDCONSERVAR ENTRE 2C E 8C - TERMOLABIL / NAO CONGELAR^FS"
                : "^FO30,205^GB740,50,2^FS\n" +
                  "^FO50,220^A0N,24,22^FDCONSERVAR ENTRE 15C E 25C - TEMPERATURA AMBIENTE^FS";

        String gtin = "07891234567890";
        String sku = med.getCodigoSku() != null ? med.getCodigoSku() : med.getCodigoSankhya();
        String status = lote.getStatus() != null ? lote.getStatus().name() : "DESCONHECIDO";

        return """
            ^XA
            ^PW800
            ^LL480
            ^CI28
            ^FO30,20^GB740,55,2^FS
            ^FO45,35^A0N,28,26^FDONCOVIT DISTRIBUIDORA DE MEDICAMENTOS^FS
            ^FO550,42^A0N,18,16^FDEXPEDICAO / RASTREABILIDADE^FS
            ^FO30,90^A0N,32,30^FD%s (%s) %s^FS
            ^FO30,130^A0N,22,20^FD%s^FS
            ^FO30,160^A0N,19,18^FDSKU: %s  |  Reg. MS: %s^FS
            ^FO30,190^GB740,2,2^FS
            %s
            ^FO30,270^A0N,26,24^FDLOTE: %s^FS
            ^FO30,305^A0N,22,20^FDFAB: %s    VAL: %s^FS
            ^FO30,340^A0N,22,20^FDQTD: %d FRASCOS     STATUS: %s^FS
            ^FO30,375^A0N,18,16^FDOrigem: %s^FS
            ^FO420,270^BY2,3,60^BCN,60,Y,N,N^FD%s^FS
            ^FO670,360^BXN,5,200^FD01%s10%s17%s^FS
            ^FO30,440^A0N,16,14^FDSincronizado com ERP Sankhya | Impresso em %s^FS
            ^XZ
            """.formatted(
                sanitizarZpl(med.getNomeComercial()),
                sanitizarZpl(med.getPrincipioAtivo()),
                sanitizarZpl(med.getDosagem()),
                sanitizarZpl(med.getFormaFarmaceutica()),
                sanitizarZpl(sku),
                sanitizarZpl(med.getRegistroAnvisa()),
                tarjaZpl,
                sanitizarZpl(lote.getNumeroLote()),
                dataFabStr,
                dataValStr,
                lote.getQuantidadeAtual() != null ? lote.getQuantidadeAtual() : 0,
                status,
                sanitizarZpl(lote.getFornecedor()),
                sanitizarZpl(lote.getNumeroLote()),
                gtin,
                sanitizarZpl(lote.getNumeroLote()),
                valGs1,
                agoraStr
            ).strip();
    }

    private String sanitizarZpl(String input) {
        if (input == null) return "";
        return input.replace("^", "").replace("~", "").replace("\n", " ").replace("\r", "");
    }
}
