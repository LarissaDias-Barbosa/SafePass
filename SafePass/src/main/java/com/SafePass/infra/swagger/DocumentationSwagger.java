package com.SafePass.infra.swagger;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.yaml.snakeyaml.tokens.Token.ID.Tag;

@Configuration
public class DocumentationSwagger { // <--- Nome da classe

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("SafePass API")
                        .version("1.0")
                        .description("")
                        .contact(new Contact()
                                .name("SafePass - Larissa & Matheus")
                                .email("safepass@gmail.com"))
                        .license(new License()
                                .name("Uso Restrito - Propriedade Larissa & Matheus")
                                .url("https://github.com/LarissaDias-Barbosa/SafePass"))
                )
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                )
                // Tags principais da documentação
                .addTagsItem(new Tag().name("Autenticação").description("Endpoints de login, cadastro e tokens"))
                .addTagsItem(new Tag().name("Usuário").description("Operações relacionadas ao usuário"))
                .addTagsItem(new Tag().name("Planos").description("Controle de acesso e tipo de plano"));
    }
}
