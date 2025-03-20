package appetito.apicardapio.dto.GetAll;

import appetito.apicardapio.entity.Funcionario;

public record FuncionarioDados(String nome,
                               String cargo,
                               String departamento) {
    public FuncionarioDados(Funcionario funcionario) {
        this(
                funcionario.getNome(),
                funcionario.getCargo(),
                funcionario.getDepartamento()
        );
    }

}
