package com.SafePass.infra.filter;

import com.SafePass.auth.model.Usuario;
import com.SafePass.auth.service.AuthenticationService;
import com.SafePass.infra.jwt.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final String URL_FRONTEND;

    public OAuth2LoginSuccessHandler(AuthenticationService authenticationService,
                                     JwtService jwtService,
                                     @Value("${app.frontend.url}") String URL_FRONTEND) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
        this.URL_FRONTEND = URL_FRONTEND;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        var attributes = authToken.getPrincipal().getAttributes();

        String email = attributes.get("email").toString();
        String nome = attributes.get("name").toString();
        String foto = attributes.get("picture").toString();

        // Cria ou atualiza usuário via serviço
        Usuario usuario = authenticationService.loginOrRegisterGoogle(email, nome, foto, response);

        // Gera JWT
        String token = jwtService.gerarToken(usuario);

        // Cria cookie seguro com token
        Cookie jwtCookie = new Cookie("jwt_token", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false); // true em produção com HTTPS
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(jwtCookie);
        response.sendRedirect(URL_FRONTEND);
    }
}