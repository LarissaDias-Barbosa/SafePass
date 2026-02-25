package com.SafePass.auth.service;

import com.SafePass.auth.model.Usuario;
import com.SafePass.auth.repository.UsuarioRepository;
import com.SafePass.infra.jwt.JwtService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class UserAuthenticatedService {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public UserAuthenticatedService(JwtService jwtService, UsuarioRepository usuarioRepository) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    public ResponseEntity<?> getUsuarioPorToken(String token) {

        if (token == null || token.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Usuário não está logado."));
        }

        if (!jwtService.isTokenValid(token)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Token inválido ou expirado."));
        }

        String email = jwtService.getEmailFromToken(token);

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", "Usuário não encontrado."));
        }

        Usuario usuario = usuarioOpt.get();

        Map<String, Object> dadosUsuario = Map.of(
                "id", usuario.getId(),
                "nome", usuario.getNome(),
                "email", usuario.getEmail(),
                "foto", usuario.getFoto() != null ? usuario.getFoto() : "/placeholder-user.jpg"
        );

        return ResponseEntity.ok(dadosUsuario);
    }

    @Cacheable(cacheNames = "usuario", key = "#id")
    public Usuario buscarUsuarioPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do usuário inválido.");
        }

        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }
}