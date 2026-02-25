package com.SafePass.infra.security;


import com.SafePass.auth.model.Usuario;
import com.SafePass.auth.repository.UsuarioRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UsuarioRepository usuarioRepository;

    public CustomOAuth2UserService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String nome = oAuth2User.getAttribute("name");
        String foto = oAuth2User.getAttribute("picture");

        // Salva ou atualiza o usuário no banco
        usuarioRepository.findByEmail(email)
                .orElseGet(() -> {
                    Usuario novo = new Usuario();
                    novo.setEmail(email);
                    novo.setNome(nome);
                    novo.setFoto(foto);
                    novo.setEmailConfirmado(true);
                    novo.setLoginGoogle(true);
                    return usuarioRepository.save(novo);
                });

        return oAuth2User;
    }
}