package com.SafePass.auth.dto.AuthDTO;

import com.SafePass.auth.model.Usuario;

public record AuthResponseDTO(String token, Usuario usuario) {}
