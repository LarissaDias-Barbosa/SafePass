package com.SafePass.auth.service;

import com.SafePass.auth.dto.AuthDTO.LoginResponse;
import com.SafePass.auth.dto.AuthDTO.LoginUsuarioDTO;
import com.SafePass.auth.model.Usuario;
import com.SafePass.auth.repository.UsuarioRepository;
import com.SafePass.auth.service.jwtService.JwtTokenServiceInterface;
import com.SafePass.infra.exception.ApiException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class LoginManualOperation {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenServiceInterface jwtTokenService;

    public LoginManualOperation(UsuarioRepository usuarioRepository,
                                PasswordEncoder passwordEncoder,
                                JwtTokenServiceInterface jwtTokenService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional
    public LoginResponse execute(LoginUsuarioDTO loginRequest,
                                 String path,
                                 HttpServletResponse response) {

        // 1. Busca usuário
        Usuario usuario = usuarioRepository.findByEmail(loginRequest.email())
                .orElseThrow(() ->
                        new ApiException("Usuário não encontrado", HttpStatus.NOT_FOUND, path));

        // 2. Verifica email confirmado
        if (!usuario.isEmailConfirmado()) {
            throw new ApiException("Email não confirmado", HttpStatus.BAD_REQUEST, path);
        }

        // 3. Valida senha
        if (usuario.isLoginGoogle()) {
            // Conta Google agora recebe senha manual
            usuario.setSenha(passwordEncoder.encode(loginRequest.senha()));
            usuario.setLoginGoogle(false);
            usuarioRepository.save(usuario);
        } else if (!passwordEncoder.matches(loginRequest.senha(), usuario.getSenha())) {
            throw new ApiException("Senha inválida", HttpStatus.UNAUTHORIZED, path);
        }

        // 4. Gera token
        String token = jwtTokenService.gerarToken(usuario);

        Cookie jwtCookie = new Cookie("jwt_token", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false); // true em produção com HTTPS
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(jwtCookie);

        // 5. Retorna resposta
        return new LoginResponse(
                token,
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getFoto()
        );
    }
}