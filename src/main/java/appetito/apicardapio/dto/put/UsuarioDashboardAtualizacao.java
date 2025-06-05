package appetito.apicardapio.dto.put;

import appetito.apicardapio.enums.Situacao;

import java.time.LocalDateTime;

public record UsuarioDashboardAtualizacao(
        String nome_completo,
        String telefone,
        Situacao situacao, // Enum (ATIVO, INATIVO etc.)
        LocalDateTime data_atualizacao
) {}
