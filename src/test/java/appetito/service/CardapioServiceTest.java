package appetito.service;

import appetito.apicardapio.dto.detalhamento.CardapioDetalhamento;
import appetito.apicardapio.entity.Cardapio;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.CardapioRepository;
import appetito.apicardapio.service.CardapioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardapioServiceTest { // Não esta com erro ok, como nao existe cardapio ao inves de so retorna a mensagem o codigo deveria retorna apenas a mensagem sem a execeção

    @Mock
    private CardapioRepository cardapioRepository;

    @InjectMocks
    private CardapioService cardapioService;

    private Cardapio cardapio;

    @BeforeEach
    void setUp() {
        cardapio = new Cardapio();
        cardapio.setId(1L);
        cardapio.setNome("Cardápio de Teste");
        cardapio.setSecao("Entradas");
        cardapio.setDescricao("Descrição do cardápio");
    }

    // Teste para o método buscarCardapioPorId
    @Test
    void testBuscarCardapioPorId_QuandoCardapioExiste() {
        // Arrange
        when(cardapioRepository.findById(1L)).thenReturn(Optional.of(cardapio));

        // Act
        CardapioDetalhamento resultado = cardapioService.buscarCardapioPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.cardapio_id());
        assertEquals("Cardápio de Teste", resultado.nome());
        verify(cardapioRepository, times(1)).findById(1L);
    }

    @Test
    void testBuscarCardapioPorId_QuandoCardapioNaoExiste() {
        // Arrange
        when(cardapioRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            cardapioService.buscarCardapioPorId(1L);
        });

        verify(cardapioRepository, times(1)).findById(1L);
    }

    // Teste para o método listarCardapiosPorEstabelecimento
    @Test
    void testListarCardapiosPorEstabelecimento_QuandoExistemCardapios() {
        // Arrange
        when(cardapioRepository.findByEstabelecimento(1L)).thenReturn(Collections.singletonList(cardapio));

        // Act
        List<CardapioDetalhamento> resultado = cardapioService.listarCardapiosPorEstabelecimento(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Cardápio de Teste", resultado.get(0).nome());
        verify(cardapioRepository, times(1)).findByEstabelecimento(1L);
    }

    @Test
    void testListarCardapiosPorEstabelecimento_QuandoNaoExistemCardapios() {
        // Arrange
        when(cardapioRepository.findByEstabelecimento(1L)).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            cardapioService.listarCardapiosPorEstabelecimento(1L);
        });

        verify(cardapioRepository, times(1)).findByEstabelecimento(1L);
    }

    // Teste para o método deletarCardapio
    @Test
    void testDeletarCardapio_QuandoCardapioExiste() {
        // Arrange
        when(cardapioRepository.existsById(1L)).thenReturn(true);

        // Act
        cardapioService.deletarCardapio(1L);

        // Assert
        verify(cardapioRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeletarCardapio_QuandoCardapioNaoExiste() {
        // Arrange
        when(cardapioRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            cardapioService.deletarCardapio(1L);
        });

        verify(cardapioRepository, times(1)).existsById(1L);
        verify(cardapioRepository, never()).deleteById(1L);
    }
}