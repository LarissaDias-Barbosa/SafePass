package com.SafePass.auth.service;

import com.SafePass.auth.model.Usuario;
import com.SafePass.auth.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class LoginGoogleOperation {

    private final UsuarioRepository usuarioRepository;

    public LoginGoogleOperation(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Usuario execute(String email, String nome, String foto, HttpServletResponse response) throws IOException {

        return usuarioRepository.findByEmail(email)
                .map(u -> {
                    u.setNome(nome);
                    u.setFoto(foto);

                    if (!u.isEmailConfirmado()) {
                        u.setEmailConfirmado(true);
                        u.setTokenConfirmacao(null);
                    }

                    // Se criado manualmente antes, marca como Google agora
                    if (u.getDataPrimeiroLogin() != null && !u.isLoginGoogle()) {
                        u.setLoginGoogle(true);
                    }

                    return usuarioRepository.save(u);
                })
                .orElseGet(() -> {
                    Usuario novo = new Usuario();
                    novo.setNome(nome);
                    novo.setEmail(email);
                    novo.setSenha(null);
                    novo.setFoto(foto);
                    novo.setEmailConfirmado(true);
                    novo.setTokenConfirmacao(null);
                    novo.setLoginGoogle(true);
                    novo.setDataCriacao(LocalDateTime.now());

                    return usuarioRepository.save(novo);
                });
    }
}