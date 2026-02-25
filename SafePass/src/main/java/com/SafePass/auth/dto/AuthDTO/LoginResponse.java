package com.SafePass.auth.dto.AuthDTO;



public record LoginResponse(
        String token,
        String nome,
        String email,
        String foto
        // StatusAcesso statusAcesso
) {
}
