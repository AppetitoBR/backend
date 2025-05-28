
package appetito.apicardapio.controller;
import appetito.apicardapio.dto.GetAll.PedidoDados;
import appetito.apicardapio.dto.cadastro.PedidoCadastro;
import appetito.apicardapio.dto.detalhamento.PedidoDetalhamento;
import appetito.apicardapio.dto.put.ItemAtualizacao;
import appetito.apicardapio.entity.Pedido;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoDetalhamento> criarPedido(@RequestBody @Valid PedidoCadastro pedidoCadastro) {
        try {
            Pedido pedido = pedidoService.criarPedido(pedidoCadastro);
            return ResponseEntity.status(HttpStatus.CREATED).body(new PedidoDetalhamento(pedido));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{estabelecimentoId}")
    @PreAuthorize("@preAuthorizeService.podeAceitarPedidoCozinha(#estabelecimentoId, authentication.principal)")
    public ResponseEntity<List<PedidoDados>> listarPedidosPorEstabelecimento(@PathVariable Long estabelecimentoId) {
        List<PedidoDados> pedidos = pedidoService.listarPedidosPorEstabelecimento(estabelecimentoId)
                .stream()
                .map(PedidoDados::new)
                .toList();
        return ResponseEntity.ok(pedidos);
    }
    /*
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> excluirPedido(@PathVariable Long id) {
            try {
                pedidoService.excluirPedido(id);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }
     */
    @PutMapping("/{pedidoId}")
    public ResponseEntity<Pedido> atualizarItensPedido(
            @PathVariable Long pedidoId,
            @RequestBody List<ItemAtualizacao> itensAtualizacao) {
        try {
            Pedido pedidoAtualizado = pedidoService.atualizarItensPedido(pedidoId, itensAtualizacao);
            return ResponseEntity.ok(pedidoAtualizado);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}