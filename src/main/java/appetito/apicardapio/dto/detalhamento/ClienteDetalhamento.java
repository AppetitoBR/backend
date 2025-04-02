package appetito.apicardapio.dto.detalhamento;

import appetito.apicardapio.entity.Cliente;

public record ClienteDetalhamento(
    String nome_completo
) {
    public ClienteDetalhamento(Cliente cliente) {
        this(
                cliente.getNomeCompleto()
        );
    }
}
