package appetito.apicardapio.service;

import appetito.apicardapio.controller.CardapioController;
import appetito.apicardapio.dto.CardapioCadastro;
import appetito.apicardapio.dto.CardapioDetalhamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class CardapioControllerTest {

    @Mock
    private CardapioService cardapioService;

    @InjectMocks
    private CardapioController cardapioController;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks((this));
    }

    @Test
    void testCadastrarCardapio(){

        //Entrada de dados
        CardapioCadastro dadosCardapio = new CardapioCadastro(
                "Cardapio de teste",
                "Sobremessas", //trocar secao por categoria
                "SObremesas saborosas",
                1L,
                1L,
                LocalDate.now(),// inicio
                LocalDate.now().plusDays(1) //fim
        );

        // Mock do detalhamento
        CardapioDetalhamento cardapioDetalhamento = new CardapioDetalhamento(
          1L,
          "Cardapio de teste",
          "Sobremessa",
          "Sobremesas saborosas",
          1L,
          1L,
          LocalDate.now(),
          LocalDate.now().plusDays(1),
          true
        );
        when(cardapioService.cadastrarCardapio(dadosCardapio)).thenReturn(cardapioDetalhamento);

        //Executar
        ResponseEntity<CardapioDetalhamento> resposta = cardapioController.cadastrarCardapio(
                dadosCardapio,
                UriComponentsBuilder.newInstance()
        );

        //verificadores
        assertNotNull(resposta);
        assertEquals(201, resposta.getStatusCodeValue());
        assertEquals(cardapioDetalhamento, resposta.getBody());

    }

    @Test
    void testBuscarCardapioPorId() {
        //Cardapio detalhamento
        CardapioDetalhamento cardapioDetalhamento = new CardapioDetalhamento(
                1L,
                "Cardapio de teste",
                "Sobremessa",
                "Sobremesas saborosas",
                1L,
                1L,
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                true
        );
        when(cardapioService.buscarCardapioPorId(1L)).thenReturn(cardapioDetalhamento);

        //Executa o metodo
        ResponseEntity<CardapioDetalhamento> resposta = cardapioController.buscarCardapioPorId(1L);

        //verificacoes
        assertNotNull(resposta);
        assertEquals(200, resposta.getStatusCodeValue());
        assertEquals(cardapioDetalhamento, resposta.getBody());
    }

    @Test
    void testListarCardapioPorEstabelecimento() {
        //mock para lista o cardapio detalhamento
        //Cardapio detalhamento
        List<CardapioDetalhamento> cardapios = List.of(
                new CardapioDetalhamento(
                        1L,
                        "Cardapio de teste",
                        "Sobremessa",
                        "Sobremesas saborosas",
                        1L,
                        1L,
                        LocalDate.now(),
                        LocalDate.now().plusDays(1),
                        true
                )
        );
        when(cardapioService.listarCardapiosPorEstabelecimento(1L)).thenReturn(cardapios);

        //executar o metodo
        ResponseEntity<List<CardapioDetalhamento>> resposta = cardapioController.listarCardapiosPorEstabelecimento(1L);

        //verificacoes
        assertNotNull(resposta);
        assertEquals(200, resposta.getStatusCodeValue());
        assertEquals(cardapios, resposta.getBody());
    }

}
