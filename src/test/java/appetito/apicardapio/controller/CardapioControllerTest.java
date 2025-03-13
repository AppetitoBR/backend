package appetito.apicardapio.controller;

import appetito.apicardapio.dto.CardapioCadastro;
import appetito.apicardapio.dto.CardapioDetalhamento;
import appetito.apicardapio.service.CardapioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class CardapioControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private CardapioService cardapioService;

    @InjectMocks
    private CardapioController cardapioController;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Registra o módulo para suportar LocalDate

        mockMvc = MockMvcBuilders.standaloneSetup(cardapioController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper)) // Configura o ObjectMapper no MockMvc
                .build();
    }

    @Test
    public void deveCadastrarCardapio() throws Exception {
        CardapioCadastro novoCardapio = new CardapioCadastro(
                "Cardápio Teste", // nome
                "Secao Teste", // secao
                "Descricao Teste", // descricao
                1L, // id
                1L, // colaborador_id
                LocalDate.now(), // vigencia_inicio
                LocalDate.now().plusDays(7) // vigencia_fim
        );

        CardapioDetalhamento resposta = new CardapioDetalhamento(
                1L, // cardapio_id
                "Cardápio Teste", // nome
                "Secao Teste", // secao
                "Descricao Teste", // descricao
                1L, // estabelecimento_id
                1L, // colaborador_id
                LocalDate.now(), // vigencia_inicio
                LocalDate.now().plusDays(7), // vigencia_fim
                true // ativo
        );

        when(cardapioService.cadastrarCardapio(any(CardapioCadastro.class))).thenReturn(resposta);

        mockMvc.perform(post("/cardapios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoCardapio)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cardapio_id").value(1L))
                .andExpect(jsonPath("$.nome").value("Cardápio Teste"));
    }

    @Test
    public void deveBuscarCardapioPorId() throws Exception {
        CardapioDetalhamento resposta = new CardapioDetalhamento(
                1L, // cardapio_id
                "Cardápio Teste", // nome
                "Secao Teste", // secao
                "Descricao Teste", // descricao
                1L, // estabelecimento_id
                1L, // colaborador_id
                LocalDate.now(), // vigencia_inicio
                LocalDate.now().plusDays(7), // vigencia_fim
                true // ativo
        );

        when(cardapioService.buscarCardapioPorId(1L)).thenReturn(resposta);

        mockMvc.perform(get("/cardapios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardapio_id").value(1L))
                .andExpect(jsonPath("$.nome").value("Cardápio Teste"));
    }

    @Test
    public void deveListarCardapiosPorEstabelecimento() throws Exception {
        List<CardapioDetalhamento> lista = List.of(
                new CardapioDetalhamento(
                        1L, // cardapio_id
                        "Cardápio 1", // nome
                        "Secao 1", // secao
                        "Descricao 1", // descricao
                        1L, // estabelecimento_id
                        1L, // colaborador_id
                        LocalDate.now(), // vigencia_inicio
                        LocalDate.now().plusDays(7), // vigencia_fim
                        true // ativo
                ),
                new CardapioDetalhamento(
                        2L, // cardapio_id
                        "Cardápio 2", // nome
                        "Secao 2", // secao
                        "Descricao 2", // descricao
                        2L, // estabelecimento_id
                        2L, // colaborador_id
                        LocalDate.now(), // vigencia_inicio
                        LocalDate.now().plusDays(7), // vigencia_fim
                        true // ativo
                )
        );

        when(cardapioService.listarCardapiosPorEstabelecimento(1L)).thenReturn(lista);

        mockMvc.perform(get("/cardapios/estabelecimento/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}