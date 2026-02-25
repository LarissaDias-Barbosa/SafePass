package com.SafePass.auth.service;

import com.SafePass.auth.EmailService;
import com.SafePass.auth.model.Usuario;
import com.SafePass.auth.repository.UsuarioRepository;
import com.SafePass.infra.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UpdatePasswordService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private final Map<String, VerificationCode> codigoMap = new HashMap<>();

    public UpdatePasswordService(UsuarioRepository usuarioRepository,
                                 PasswordEncoder passwordEncoder,
                                 EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public void sendVerificationCode(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("Email não encontrado", HttpStatus.NOT_FOUND, "/api/auth/esqueceu-senha"));

        String codigo = gerarCodigo();
        LocalDateTime expiracao = LocalDateTime.now().plusMinutes(10);

        codigoMap.put(email, new VerificationCode(codigo, expiracao));

        usuario.setCodigoRecuperacao(codigo);
        usuarioRepository.save(usuario);

        emailService.enviarEmail(email, usuario.getNome(), "Código de recuperação - SafePass", "Seu código é: " + codigo);
    }

    public void resetPassword(String email, String codigo, String novaSenha) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("Usuário não encontrado", HttpStatus.NOT_FOUND, "/api/auth/redefinir-senha"));

        VerificationCode verificationCode = codigoMap.get(email);

        if (verificationCode == null) {
            throw new ApiException("Código de verificação inválido ou expirado", HttpStatus.BAD_REQUEST, "/api/auth/redefinir-senha");
        }

        if (!verificationCode.getCodigo().equals(codigo)) {
            throw new ApiException("Código de verificação inválido", HttpStatus.BAD_REQUEST, "/api/auth/redefinir-senha");
        }

        if (verificationCode.getExpiracao().isBefore(LocalDateTime.now())) {
            codigoMap.remove(email);
            throw new ApiException("Código de verificação expirado", HttpStatus.BAD_REQUEST, "/api/auth/redefinir-senha");
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuario.setCodigoRecuperacao(null);
        usuarioRepository.save(usuario);

        codigoMap.remove(email);
    }

    private String gerarCodigo() {
        Random random = new Random();
        int numero = 100000 + random.nextInt(900000);
        return String.valueOf(numero);
    }

    private static class VerificationCode {
        private final String codigo;
        private final LocalDateTime expiracao;

        public VerificationCode(String codigo, LocalDateTime expiracao) {
            this.codigo = codigo;
            this.expiracao = expiracao;
        }

        public String getCodigo() { return codigo; }
        public LocalDateTime getExpiracao() { return expiracao; }
    }
}