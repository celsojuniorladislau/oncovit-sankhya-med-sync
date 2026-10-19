package com.oncovit.medsync.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final SankhyaAuthInterceptor sankhyaAuthInterceptor;

    public WebMvcConfig(SankhyaAuthInterceptor sankhyaAuthInterceptor) {
        this.sankhyaAuthInterceptor = sankhyaAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sankhyaAuthInterceptor)
                .addPathPatterns("/api/integracao/sankhya/**");
    }
}
