package appetito.apicardapio.dto.cadastro;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;
public record UsuarioCadastro(
        @NotBlank String nome_completo,
        @NotBlank String cpf,
        @NotBlank String email,
        @NotBlank String senha,
        @Past LocalDate data_nascimento,
        @NotNull Integer idioma_padrao,
        @NotBlank String contatos,
        @NotBlank String endereco,
        @NotBlank String redes_sociais
) {}