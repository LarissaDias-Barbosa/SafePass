package com.SafePass.auth.service;

import com.SafePass.auth.EmailService;
import com.SafePass.auth.model.Usuario;
import com.SafePass.auth.repository.UsuarioRepository;
import com.SafePass.infra.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class CadastroManualOperation {

    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final UploadFotoOperation uploadFotoOperation;

    public CadastroManualOperation(UsuarioRepository usuarioRepository,
                                   EmailService emailService,
                                   PasswordEncoder passwordEncoder,
                                   UploadFotoOperation uploadFotoOperation) {
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.uploadFotoOperation = uploadFotoOperation;
    }

    @Transactional
    public Usuario execute(String nome, String email, String senha, MultipartFile foto, String baseUrl, String path) throws IOException {

        validarCampos(email, senha, path);

        Optional<Usuario> existenteOpt = usuarioRepository.findByEmail(email);

        if (existenteOpt.isPresent()) {
            Usuario existente = existenteOpt.get();

            if (existente.isLoginGoogle()) {
                throw new ApiException(
                        "Este e-mail já foi utilizado em um cadastro via Google. Faça login com o Google para continuar.",
                        HttpStatus.BAD_REQUEST,
                        path
                );
            }

            if (!existente.isEmailConfirmado()) {
                throw new ApiException(
                        "E-mail já cadastrado, mas ainda aguarda confirmação. Verifique sua caixa de entrada.",
                        HttpStatus.BAD_REQUEST,
                        path
                );
            }

            throw new ApiException("E-mail já cadastrado.", HttpStatus.BAD_REQUEST, path);
        }

        Usuario usuario = criarNovoUsuario(nome, email, senha, foto);
        usuarioRepository.save(usuario);
        enviarEmailConfirmacao(usuario, baseUrl);
        return usuario;
    }

    private void validarCampos(String email, String senha, String path) {
        if (email == null || email.isBlank()) {
            throw new ApiException("O e-mail é obrigatório.", HttpStatus.BAD_REQUEST, path);
        }
        if (senha == null || senha.length() < 6) {
            throw new ApiException("A senha deve ter pelo menos 6 caracteres.", HttpStatus.BAD_REQUEST, path);
        }
    }

    private Usuario criarNovoUsuario(String nome, String email, String senha, MultipartFile foto) throws IOException {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setEmailConfirmado(false);
        usuario.setLoginGoogle(false);
        usuario.setDataCriacao(LocalDateTime.now());

        if (foto != null && !foto.isEmpty()) {
            usuario.setFotoUpload(uploadFotoOperation.salvarFoto(foto));
        }

        usuario.setTokenConfirmacao(UUID.randomUUID().toString());
        usuario.setDataEnvioConfirmacao(LocalDateTime.now());

        return usuario;
    }

    private void enviarEmailConfirmacao(Usuario usuario, String baseUrl) {
        String linkConfirmacao = baseUrl + "/auth/confirmar?token=" + usuario.getTokenConfirmacao();
        emailService.enviarConfirmacao(usuario.getEmail(), linkConfirmacao);
    }
}