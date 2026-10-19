package com.oncovit.medsync.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Oncovit Sankhya Med Sync API")
                        .version("1.0.0")
                        .description("API REST de Gestão e Rastreabilidade de Medicamentos Oncológicos e Hospitalares integrada às regras de negócio do ERP Sankhya, barreira sanitária ANVISA e impressão térmica ZPL II.")
                        .contact(new Contact()
                                .name("Oncovit Distribuidora de Medicamentos")
                                .email("ti@oncovit.com.br")
                                .url("https://oncovit.com.br"))
                        .license(new License()
                                .name("Proprietário Oncovit")
                                .url("https://oncovit.com.br")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Ambiente Local de Desenvolvimento"),
                        new Server().url("http://[2a01:4f8:c014:395c::1]:8080").description("Ambiente de Produção VPS 2")
                ))
                .components(new Components()
                        .addSecuritySchemes("sankhyaAppKey", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-Sankhya-AppKey")
                                .description("Chave de aplicação do parceiro Sankhya"))
                        .addSecuritySchemes("sankhyaToken", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-Sankhya-Token")
                                .description("Token de autenticação e sessão Sankhya")));
    }
}
