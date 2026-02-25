package com.SafePass.auth.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true, nullable = false)
    private String email;

    private String senha;

    @Column(name = "foto_google")
    private String foto;

    @Column
    private String fotoUpload;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_primeiro_login")
    private LocalDateTime dataPrimeiroLogin;

    @Column(nullable = false)
    private boolean emailConfirmado = false;

    @Column
    private String tokenConfirmacao;

    @Column
    private LocalDateTime dataEnvioConfirmacao;

    @Column
    private String codigoRecuperacao;

    @Column(name = "login_google", nullable = false)
    private boolean loginGoogle = false;


    @PrePersist
    public void prePersist() {
        if (dataCriacao == null) dataCriacao = LocalDateTime.now();
        if (dataPrimeiroLogin == null) dataPrimeiroLogin = LocalDateTime.now();
    }
}