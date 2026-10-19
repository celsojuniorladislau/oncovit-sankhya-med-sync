package com.oncovit.medsync.config;

import com.oncovit.medsync.service.SankhyaIntegracaoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SankhyaAuthInterceptor implements HandlerInterceptor {

    private final SankhyaIntegracaoService sankhyaIntegracaoService;

    public SankhyaAuthInterceptor(SankhyaIntegracaoService sankhyaIntegracaoService) {
        this.sankhyaIntegracaoService = sankhyaIntegracaoService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        // Intercepta requisições de integração Sankhya antes do parsing do payload e da validação (@Valid)
        String appKey = request.getHeader("X-Sankhya-AppKey");
        String token = request.getHeader("X-Sankhya-Token");

        sankhyaIntegracaoService.validarCredenciaisSankhya(appKey, token);
        return true;
    }
}
