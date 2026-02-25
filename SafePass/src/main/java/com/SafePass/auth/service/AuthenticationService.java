package com.SafePass.auth.service;

import com.SafePass.auth.dto.AuthDTO.LoginResponse;
import com.SafePass.auth.dto.AuthDTO.LoginUsuarioDTO;
import com.SafePass.auth.model.Usuario;
import com.SafePass.infra.jwt.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class AuthenticationService implements IAuthenticationService {

    private final CadastroManualOperation cadastroManual;
    private final LoginManualOperation loginManual;
    private final ConfirmarEmailOperation confirmarEmailOperation;
    private final LoginGoogleOperation loginGoogle;
    private final JwtService jwtService;
    private final UploadFotoOperation uploadFotoOperation;


    public AuthenticationService(CadastroManualOperation cadastroManual,
                                 LoginManualOperation loginManual,
                                 ConfirmarEmailOperation confirmarEmailOperation,
                                 LoginGoogleOperation loginGoogle,
                                 JwtService jwtService,
                                 UploadFotoOperation uploadFotoOperation) {
        this.cadastroManual = cadastroManual;
        this.loginManual = loginManual;
        this.confirmarEmailOperation = confirmarEmailOperation;
        this.loginGoogle = loginGoogle;
        this.jwtService = jwtService;
        this.uploadFotoOperation = uploadFotoOperation;
    }

    @Override
    public Usuario cadastrarManual(String nome, String email, String senha, MultipartFile foto, String baseUrl, String path) throws IOException {
        return cadastroManual.execute(nome, email, senha, foto, baseUrl, path);
    }

    @Override
    public boolean confirmarEmail(String token) {
        return confirmarEmailOperation.execute(token);
    }

    @Override
    public LoginResponse loginManual(String email, String senha, String path, HttpServletResponse response) {
        LoginUsuarioDTO dto = new LoginUsuarioDTO(email, senha);
        return loginManual.execute(dto, path, response);
    }

    @Override
    public Usuario loginOrRegisterGoogle(String email, String nome, String foto, HttpServletResponse response) throws IOException {
        return loginGoogle.execute(email, nome, foto, response);
    }

    @Override
    public String gerarToken(Usuario usuario) {
        return jwtService.gerarToken(usuario);
    }

    @Override
    public String salvarFoto(MultipartFile foto) throws IOException {
        return uploadFotoOperation.salvarFoto(foto);
    }
}