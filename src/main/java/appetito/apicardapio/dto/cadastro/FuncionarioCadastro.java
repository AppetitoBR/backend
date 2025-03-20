package appetito.apicardapio.dto.cadastro;

import appetito.apicardapio.enums.EstadoCivil;
import appetito.apicardapio.enums.Sexo;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

public record FuncionarioCadastro(
        @NotBlank String nome,
        @NotBlank String cpf,
        @NotBlank @Past LocalDate dataNascimento,
        @NotBlank @Enumerated  Sexo sexo,
        @NotBlank   String departamento,
        @NotBlank   String cargo,
        @NotBlank @Enumerated  EstadoCivil estadoCivil,
        @NotBlank  String nome_pai,
        @NotBlank  String nome_mae,
        @NotBlank   BigDecimal salario
) {
    public int getIdade() {
        if (dataNascimento == null) {
            throw new IllegalStateException("Data de nascimento não pode ser nula.");
        }
        LocalDate hoje = LocalDate.now();
        return Period.between(dataNascimento, hoje).getYears();
    }

}
