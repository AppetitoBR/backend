package appetito.apicardapio.dto.detalhamento;

import appetito.apicardapio.entity.Funcionario;
import appetito.apicardapio.enums.EstadoCivil;
import appetito.apicardapio.enums.Sexo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

public record FuncionarioDetalhamento(
        Long estabelecimento_id,
        String nome,
        String cpf,
        LocalDate dataNascimento,
        Sexo sexo,
        int idade,
        String departamento,
        String cargo,
        EstadoCivil estadoCivil,
        String nome_pai,
        String nome_mae,
        BigDecimal salario,
        boolean ativo
) {
    public FuncionarioDetalhamento(Funcionario funcionario) {
        this(
                funcionario.getEstabelecimento(),
                funcionario.getNome(),
                funcionario.getCpf(),
                funcionario.getDataNascimento(),
                funcionario.getSexo(),
                calcularIdade(funcionario.getDataNascimento()),
                funcionario.getDepartamento(),
                funcionario.getCargo(),
                funcionario.getEstadoCivil(),
                funcionario.getNome_pai(),
                funcionario.getNome_mae(),
                funcionario.getSalario(),
                funcionario.isAtivo()
        );
    }
    private static int calcularIdade(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            throw new IllegalArgumentException("Data de nascimento não pode ser nula.");
        }

        LocalDate hoje = LocalDate.now();
        return Period.between(dataNascimento, hoje).getYears();
    }
}
