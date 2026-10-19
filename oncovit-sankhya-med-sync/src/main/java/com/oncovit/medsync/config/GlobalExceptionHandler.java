package com.oncovit.medsync.config;

import com.oncovit.medsync.dto.ErrorResponseDTO;
import com.oncovit.medsync.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 Bad Request: Validações de Bean Validation (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ErrorResponseDTO erro = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_FAILED",
                mensagem,
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    // 400 Bad Request: Argumentos inválidos
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        ErrorResponseDTO erro = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_PAYLOAD",
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    // 400 Bad Request: JSON malformado ou corpo ilegível
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        ErrorResponseDTO erro = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "BAD_REQUEST",
                "Corpo da requisição inválido ou JSON malformado.",
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    // 401 Unauthorized: Cabeçalhos Sankhya inválidos ou ausentes
    @ExceptionHandler(SankhyaAuthException.class)
    public ResponseEntity<ErrorResponseDTO> handleSankhyaAuth(SankhyaAuthException ex, HttpServletRequest request) {
        ErrorResponseDTO erro = new ErrorResponseDTO(
                HttpStatus.UNAUTHORIZED.value(),
                "UNAUTHORIZED",
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponseDTO> handleSecurity(SecurityException ex, HttpServletRequest request) {
        ErrorResponseDTO erro = new ErrorResponseDTO(
                HttpStatus.UNAUTHORIZED.value(),
                "UNAUTHORIZED",
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    // 404 Not Found: Entidade não encontrada
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ErrorResponseDTO erro = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                ex.getErrorCode() != null ? ex.getErrorCode() : "NOT_FOUND",
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    // 409 Conflict: Saldo insuficiente no lote
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponseDTO> handleInsufficientStock(InsufficientStockException ex, HttpServletRequest request) {
        ErrorResponseDTO erro = new ErrorResponseDTO(
                HttpStatus.CONFLICT.value(),
                "OUT_OF_STOCK",
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalState(IllegalStateException ex, HttpServletRequest request) {
        ErrorResponseDTO erro = new ErrorResponseDTO(
                HttpStatus.CONFLICT.value(),
                "OUT_OF_STOCK",
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    // 409 Conflict: Concorrência otimista / Conflito de transação simultânea
    @ExceptionHandler({ObjectOptimisticLockingFailureException.class, ConcurrencyFailureException.class})
    public ResponseEntity<ErrorResponseDTO> handleConcurrencyConflict(Exception ex, HttpServletRequest request) {
        ErrorResponseDTO erro = new ErrorResponseDTO(
                HttpStatus.CONFLICT.value(),
                "CONCURRENCY_CONFLICT",
                "Conflito de concorrência detectado: O lote foi modificado por outra transação simultânea. Tente novamente.",
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    // 422 Unprocessable Entity: Lote vencido
    @ExceptionHandler(ExpiredLotException.class)
    public ResponseEntity<ErrorResponseDTO> handleExpiredLot(ExpiredLotException ex, HttpServletRequest request) {
        ErrorResponseDTO erro = new ErrorResponseDTO(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "EXPIRED_LOT_REJECTED",
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(erro);
    }

    // 422 Unprocessable Entity: Lote com status não liberado
    @ExceptionHandler(InvalidLotStatusException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidLotStatus(InvalidLotStatusException ex, HttpServletRequest request) {
        ErrorResponseDTO erro = new ErrorResponseDTO(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "INVALID_STATUS",
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(erro);
    }

    // 422 Unprocessable Entity: Operações não permitidas / Barreira sanitária
    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnsupported(UnsupportedOperationException ex, HttpServletRequest request) {
        String errCode = (ex.getMessage() != null && ex.getMessage().contains("vencido"))
                ? "EXPIRED_LOT_REJECTED" : "INVALID_STATUS";
        ErrorResponseDTO erro = new ErrorResponseDTO(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                errCode,
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(erro);
    }

    // Generic BusinessException
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.resolve(ex.getHttpStatus());
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponseDTO erro = new ErrorResponseDTO(
                status.value(),
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(status).body(erro);
    }
}
