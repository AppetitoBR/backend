package appetito.apicardapio.service;

import appetito.apicardapio.dto.CardapioCadastro;
import appetito.apicardapio.dto.CardapioDetalhamento;
import appetito.apicardapio.entity.Cardapio;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.Colaborador;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.CardapioRepository;
import appetito.apicardapio.repository.EstabelecimentoRepository;
import appetito.apicardapio.repository.ColaboradorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardapioServiceTest {

    @Mock
    private CardapioRepository cardapioRepository;
    @Mock
    private EstabelecimentoRepository estabelecimentoRepository;
    @Mock
    private ColaboradorRepository colaboradorRepository;

    @InjectMocks
    private CardapioService cardapioService;

    private Estabelecimento estabelecimento;
    private Colaborador colaborador;
    private Cardapio cardapio;

    @BeforeEach
    void setUp() {
        estabelecimento = new Estabelecimento();
        colaborador = new Colaborador();
        cardapio = new Cardapio("Teste", "Principal", "Descrição", estabelecimento, colaborador, LocalDate.now(), LocalDate.now().plusDays(10));
    }

    @Test
    void deveCadastrarCardapioComSucesso() {
        CardapioCadastro dto = new CardapioCadastro("Teste", "Principal", "Descrição", 1L, 1L, LocalDate.now(), LocalDate.now().plusDays(10));

        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));
        when(colaboradorRepository.findById(1L)).thenReturn(Optional.of(colaborador));
        when(cardapioRepository.save(any(Cardapio.class))).thenReturn(cardapio);

        CardapioDetalhamento detalhamento = cardapioService.cadastrarCardapio(dto);

        assertNotNull(detalhamento);
        assertEquals("Teste", detalhamento.nome());
    }

    @Test
    void deveLancarErroAoCadastrarComEstabelecimentoInvalido() {
        CardapioCadastro dto = new CardapioCadastro("Teste", "Principal", "Descrição", 99L, 1L, LocalDate.now(), LocalDate.now().plusDays(10));

        when(estabelecimentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cardapioService.cadastrarCardapio(dto));
    }
}
