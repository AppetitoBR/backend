package appetito.apicardapio.dto.GetAll;

import appetito.apicardapio.enums.PapelUsuario;

public record FuncionarioDados(
        Long id,
        String nome,
        String email,
        PapelUsuario papel
) {
}
