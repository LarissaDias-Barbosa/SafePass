package com.SafePass.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/google")
public class GoogleAuthController {

    /**
     * Endpoint necessário apenas para que o Spring Security reconheça o sucesso do login.
     * O OAuth2LoginSuccessHandler lida com a geração do token e redirecionamento.
     */
    @GetMapping("/success")
    public void success() {
        // Nada aqui: o OAuth2LoginSuccessHandler já redireciona o usuário
    }
}