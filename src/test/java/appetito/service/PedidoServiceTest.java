package appetito.service;

import appetito.apicardapio.dto.cadastro.PedidoCadastro;
import appetito.apicardapio.entity.*;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.PedidoItemRepository;
import appetito.apicardapio.repository.PedidoRepository;
import appetito.apicardapio.repository.ProdutoRepository;
import appetito.apicardapio.service.PedidoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PedidoItemRepository pedidoItemRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PedidoService pedidoService;

    private Usuario usuario;
    private Produto produto;
    private PedidoCadastro pedidoCadastro;
    private PedidoItem itemCadastro;

    @Test
    void criarPedido_deveLancarExcecaoQuandoUsuarioNaoAutenticado() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn("anonymousUser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> pedidoService.criarPedido(pedidoCadastro));
    }


    @Test
    void atualizarPedido_deveLancarExcecaoQuandoPedidoNaoExiste() {
        // Arrange
        when(pedidoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> pedidoService.atualizarPedido(1L, pedidoCadastro));
    }

    @Test
    void buscarPedido_deveRetornarPedidoQuandoExistir() {
        // Arrange
        Pedido pedido = new Pedido(1L);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // Act
        Pedido resultado = pedidoService.buscarPedido(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getUsuario_id());
    }

    @Test
    void buscarPedido_deveLancarExcecaoQuandoPedidoNaoExiste() {
        // Arrange
        when(pedidoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> pedidoService.buscarPedido(1L));
    }

    @Test
    void listarPedidos_deveRetornarListaDePedidos() {
        // Arrange
        Pedido pedido = new Pedido(1L);
        when(pedidoRepository.findAll()).thenReturn(Collections.singletonList(pedido));

        // Act
        List<Pedido> resultado = pedidoService.listarPedidos();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getUsuario_id());
    }

    @Test
    void excluirPedido_deveExcluirPedidoQuandoExistir() {
        // Arrange
        Pedido pedido = new Pedido(1L);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // Act
        pedidoService.excluirPedido(1L);

        // Assert
        verify(pedidoRepository).delete(pedido);
    }

    @Test
    void excluirPedido_deveLancarExcecaoQuandoPedidoNaoExiste() {
        // Arrange
        when(pedidoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> pedidoService.excluirPedido(1L));
    }
}