package com.SafePass.auth.service;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class UploadFotoOperation {

    public String salvarFoto(MultipartFile foto) throws IOException {
        if (foto == null || foto.isEmpty()) {
            return null;
        }

        String nomeArquivo = System.currentTimeMillis() + "_" + foto.getOriginalFilename();
        Path caminho = Paths.get("uploads", nomeArquivo);
        Files.createDirectories(caminho.getParent());
        Files.write(caminho, foto.getBytes());

        return caminho.toString();
    }
}