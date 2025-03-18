package appetito.apicardapio.dto;

import appetito.apicardapio.entity.Cardapio;
import appetito.apicardapio.entity.Colaborador;
import appetito.apicardapio.entity.Estabelecimento;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CardapioDetalhamentoTest {

    @Test
    void testConstrutorComCardapio(){

        //dados de entrada
        Estabelecimento estabelecimento = new Estabelecimento();
        estabelecimento.setId(1L);

        Colaborador colaborador = new Colaborador();
        colaborador.setColaborador_id(1L);

        Cardapio cardapio = new Cardapio(
                "Cardapio teste",
                "Sobremesas",
                "Cardapio Sobremesa",
                estabelecimento,
                colaborador,
                LocalDate.now(),
                LocalDate.now().plusDays(1)
        );
        cardapio.setId(1L);

        //Executado do metodo
        CardapioDetalhamento cardapioDetalhamento = new CardapioDetalhamento(cardapio);

        //verificadores
        assertEquals(1L, cardapioDetalhamento.cardapio_id());
        assertEquals("Cardapio teste", cardapioDetalhamento.nome());
        assertEquals("Sobremesas", cardapioDetalhamento.secao());
        assertEquals(1L, cardapioDetalhamento.estabelecimento_id());
        assertEquals(1L,cardapioDetalhamento.colaborador_id());
    }
}
