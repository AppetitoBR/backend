package appetito.apicardapio.dto.cadastro;


import jakarta.validation.constraints.*;

import java.time.LocalDate;
public record UsuarioDashboardCadastro(
        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 5, max = 100, message = "O nome deve ter entre 5 e 100 caracteres") String nome_completo,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "A senha deve ter pelo menos 8 caracteres, incluindo uma letra maiúscula, um número e um caractere especial"
        )
        String senha
        //     @NotBlank String cpf,
     //   @Past LocalDate data_nascimento,
    //    @NotNull String idioma_padrao,
    //    @NotBlank String contatos,
   //     @NotBlank String endereco,
    //    @NotBlank String redes_sociais
) {}