package com.SafePass.auth.controller;

import com.SafePass.auth.model.Usuario;
import com.SafePass.auth.repository.UsuarioRepository;
import com.SafePass.auth.service.AuthenticationService;
import com.SafePass.infra.jwt.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UsuarioController {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final AuthenticationService authenticationService;

    public UsuarioController(JwtService jwtService,
                             UsuarioRepository usuarioRepository,
                             AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/api/usuario")
    public ResponseEntity<?> getUsuario(@CookieValue(name = "jwt_token", required = false) String token) {
        if (token == null || !jwtService.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuário não logado ou token inválido");
        }

        String email = jwtService.getEmailFromToken(token);
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuário não encontrado");
        }

        Map<String, Object> resposta = Map.of(
                "nome", usuario.getNome(),
                "email", usuario.getEmail(),
                "foto", (usuario.getFoto() != null && !usuario.getFoto().isBlank())
                        ? usuario.getFoto()
                        : "/placeholder-user.jpg"
        );

        return ResponseEntity.ok(resposta);
    }

    @PostMapping("/api/pagamento")
    public ResponseEntity<?> atualizarPlano(@RequestParam String email,
                                            @RequestParam int duracaoDias) {
        Usuario usuarioAtualizado = authenticationService.atualizarPlano(email, duracaoDias);

        return ResponseEntity.ok(Map.of(
                "mensagem", "Plano atualizado com sucesso!",
                "usuario", usuarioAtualizado
        ));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt_token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // true em produção com HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        return ResponseEntity.ok("Logout realizado com sucesso.");
    }
}