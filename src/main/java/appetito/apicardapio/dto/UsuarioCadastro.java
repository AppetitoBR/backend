package appetito.apicardapio.dto;


import appetito.apicardapio.enums.PerfilUsuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioCadastro(
        @NotBlank String nome_completo,
        @NotBlank String cpf,
        @NotBlank String email,
        @NotBlank String senha,
        @NotNull PerfilUsuario perfil
) {}