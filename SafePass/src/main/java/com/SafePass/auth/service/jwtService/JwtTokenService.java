package com.SafePass.auth.service.jwtService;

import com.SafePass.auth.model.Usuario;
import com.SafePass.infra.jwt.JwtService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService implements JwtTokenServiceInterface {

    private final JwtService jwtService;

    public JwtTokenService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public String gerarToken(Usuario usuario) {
        return jwtService.gerarToken(usuario);
    }

    @Override
    public boolean isTokenValid(String token) {
        return jwtService.isTokenValid(token);
    }

    @Override
    public boolean validarToken(String token, UserDetails userDetails) {
        return jwtService.validarToken(token, userDetails);
    }

    @Override
    public String getEmailFromToken(String token) {
        return jwtService.getEmailFromToken(token);
    }
}