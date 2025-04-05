package appetito.apicardapio.dto.cadastro;

import jakarta.validation.constraints.*;

public record ClienteCadastro(

        @NotBlank(message = "O nome é obrigatório")
        @Pattern(
                regexp = "^[A-Za-zÀ-ÿ]+(\\s+[A-Za-zÀ-ÿ]+)+$",
                message = "Informe o nome completo (nome e sobrenome)"
        )
        String nomeCompleto,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "A senha deve conter ao menos 8 caracteres, uma letra maiúscula, um número e um símbolo"
        )
        String senha
) {}
