package appetito.controller;

import appetito.apicardapio.controller.PedidoController;
import appetito.apicardapio.dto.GetAll.PedidoDados;
import appetito.apicardapio.dto.cadastro.PedidoCadastro;
import appetito.apicardapio.dto.detalhamento.PedidoDetalhamento;
import appetito.apicardapio.entity.Pedido;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoControllerTest {

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private PedidoController pedidoController;

    private Pedido pedido;
    private PedidoCadastro pedidoCadastro;

    @BeforeEach
    void setUp() {
        pedido = new Pedido(1L);
        pedido.setPedido_id(1L);
        pedido.setTotal(BigDecimal.valueOf(50.0));

        pedidoCadastro = new PedidoCadastro(1L, List.of());
    }

    @Test
    void criarPedido_DeveRetornarCreated() {
        when(pedidoService.criarPedido(any(PedidoCadastro.class))).thenReturn(pedido);

        ResponseEntity<PedidoDetalhamento> response = pedidoController.criarPedido(pedidoCadastro);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().pedido_id());
        verify(pedidoService, times(1)).criarPedido(any(PedidoCadastro.class));
    }

    @Test
    void criarPedido_QuandoFalhar_DeveRetornarBadRequest() {
        when(pedidoService.criarPedido(any(PedidoCadastro.class)))
                .thenThrow(ResourceNotFoundException.class);

        ResponseEntity<PedidoDetalhamento> response = pedidoController.criarPedido(pedidoCadastro);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void atualizarPedido_DeveRetornarOk() {
        when(pedidoService.atualizarPedido(anyLong(), any(PedidoCadastro.class))).thenReturn(pedido);

        ResponseEntity<PedidoDetalhamento> response = pedidoController.atualizarPedido(1L, pedidoCadastro);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().pedido_id());
    }

    @Test
    void atualizarPedido_QuandoNaoEncontrado_DeveRetornarNotFound() {
        when(pedidoService.atualizarPedido(anyLong(), any(PedidoCadastro.class)))
                .thenThrow(ResourceNotFoundException.class);

        ResponseEntity<PedidoDetalhamento> response = pedidoController.atualizarPedido(1L, pedidoCadastro);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void listarPedidos_DeveRetornarLista() {
        Pedido pedido2 = new Pedido(2L);
        pedido2.setPedido_id(2L);
        pedido2.setTotal(BigDecimal.valueOf(30.0));

        when(pedidoService.listarPedidos()).thenReturn(Arrays.asList(pedido, pedido2));

        ResponseEntity<List<PedidoDados>> response = pedidoController.listarPedidos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).pedido_id());
        assertEquals(2L, response.getBody().get(1).pedido_id());
    }

    @Test
    void buscarPedido_DeveRetornarPedido() {
        when(pedidoService.buscarPedido(1L)).thenReturn(pedido);

        ResponseEntity<PedidoDetalhamento> response = pedidoController.buscarPedido(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().pedido_id());
    }

    @Test
    void buscarPedido_QuandoNaoEncontrado_DeveRetornarNotFound() {
        when(pedidoService.buscarPedido(anyLong())).thenReturn(null);

        ResponseEntity<PedidoDetalhamento> response = pedidoController.buscarPedido(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void excluirPedido_DeveRetornarNoContent() {
        doNothing().when(pedidoService).excluirPedido(1L);

        ResponseEntity<Void> response = pedidoController.excluirPedido(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(pedidoService, times(1)).excluirPedido(1L);
    }

    @Test
    void excluirPedido_QuandoNaoEncontrado_DeveRetornarNotFound() {
        doThrow(ResourceNotFoundException.class).when(pedidoService).excluirPedido(1L);

        ResponseEntity<Void> response = pedidoController.excluirPedido(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}