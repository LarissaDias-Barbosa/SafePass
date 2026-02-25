package com.SafePass.auth.dto.googleAuthDTO;


import lombok.Data;

@Data
public class UsuarioResponse {
    private String nome;
    private String email;
    private String foto;


    public UsuarioResponse(String nome, String email, String foto) {
        this.nome = nome;
        this.email = email;
        this.foto = foto;
    }

}