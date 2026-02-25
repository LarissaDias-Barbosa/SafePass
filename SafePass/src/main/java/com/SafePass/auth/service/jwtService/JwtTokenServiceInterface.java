package com.SafePass.auth.service.jwtService;

import com.SafePass.auth.model.Usuario;

public interface JwtTokenServiceInterface {
    String gerarToken(Usuario usuario);
    boolean isTokenValid(String token);
    boolean validarToken(String token, org.springframework.security.core.userdetails.UserDetails userDetails);
    String getEmailFromToken(String token);
}