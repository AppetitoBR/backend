package appetito.apicardapio.controller;

import appetito.apicardapio.dto.cadastro.ChamadoCadastro;
import appetito.apicardapio.dto.detalhamento.ChamadoDetalhamento;
import appetito.apicardapio.entity.Chamado;
import appetito.apicardapio.service.ChamadoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

/**
 * Controller REST para gerenciamento dos chamados.
 * Expõe endpoints para solicitação, listagem, atendimento e cancelamento de chamados.
 */
@RestController
@RequestMapping("/chamado")
public class ChamadoController {

    private final ChamadoService chamadoService;

    /**
     * Construtor para injeção do serviço de chamados.
     *
     * @param chamadoService Serviço responsável pela lógica de negócios dos chamados.
     */
    public ChamadoController(ChamadoService chamadoService) {
        this.chamadoService = chamadoService;
    }

    /**
     * Endpoint para solicitar a abertura de um novo chamado.
     *
     * @param dadosCadastro Dados da solicitação do chamado, validados.
     * @param request Objeto HTTP request para capturar informações adicionais (ex: IP).
     * @return Detalhamento do chamado criado.
     * @throws AccessDeniedException Se o usuário não tiver permissão para realizar a ação.
     */
    @PostMapping("/solicitar")
    public ResponseEntity<ChamadoDetalhamento> solicitarChamado(@RequestBody @Valid ChamadoCadastro dadosCadastro, HttpServletRequest request) throws AccessDeniedException {
        Chamado chamado = chamadoService.solicitarChamado(dadosCadastro, request);
        return ResponseEntity.ok(new ChamadoDetalhamento(chamado));
    }

    /**
     * Lista os chamados pendentes de um estabelecimento.
     * Acesso permitido somente para usuários autorizados a atender o estabelecimento.
     *
     * @param estabelecimentoId ID do estabelecimento.
     * @return Lista de chamados pendentes.
     */
    @GetMapping("/{estabelecimentoId}/pendentes")
    @PreAuthorize("@preAuthorizeService.podeAtenderEstabelecimento(#estabelecimentoId, authentication.principal)")
    public ResponseEntity<List<Chamado>> listarChamadosPendentes(@PathVariable Long estabelecimentoId) {
        List<Chamado> chamados = chamadoService.listarChamadosPendentes(estabelecimentoId);
        return ResponseEntity.ok(chamados);
    }

    /**
     * Marca um chamado como atendido.
     * Acesso restrito a usuários autorizados para o estabelecimento.
     *
     * @param estabelecimentoId ID do estabelecimento.
     * @param id ID do chamado a ser atendido.
     * @return Chamado atualizado com status atendido.
     * @throws AccessDeniedException Caso o usuário não tenha permissão.
     */
    @PutMapping("/{estabelecimentoId}/atender/{id}")
    @PreAuthorize("@preAuthorizeService.podeAtenderEstabelecimento(#estabelecimentoId, authentication.principal)")
    public ResponseEntity<Chamado> atenderChamado(@PathVariable Long estabelecimentoId, @PathVariable Long id) throws AccessDeniedException {
        Chamado chamado = chamadoService.atenderChamado(estabelecimentoId, id);
        return ResponseEntity.ok(chamado);
    }

    /**
     * Cancela um chamado.
     * Acesso restrito a usuários autorizados para o estabelecimento.
     *
     * @param estabelecimentoId ID do estabelecimento.
     * @param id ID do chamado a ser cancelado.
     * @return Detalhamento do chamado cancelado.
     * @throws AccessDeniedException Caso o usuário não tenha permissão.
     */
    @PutMapping("/{estabelecimentoId}/cancelar/{id}")
    @PreAuthorize("preAuthorizeService.podeAtenderEstabelecimento(#estabelecimentoId, authentication.principal)")
    public ResponseEntity<ChamadoDetalhamento> cancelarChamado(@PathVariable Long estabelecimentoId, @PathVariable Long id) throws AccessDeniedException {
        Chamado chamado = chamadoService.cancelarChamado(estabelecimentoId, id);
        return ResponseEntity.ok(new ChamadoDetalhamento(chamado));
    }

    /**
     * Lista os chamados realizados pelo cliente autenticado.
     * Acesso restrito a usuários com o papel CLIENTE.
     *
     * @return Lista de chamados detalhados do cliente autenticado.
     * @throws AccessDeniedException Caso o usuário não seja cliente autenticado.
     */
    @GetMapping("/meus")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<ChamadoDetalhamento>> listarMeusChamados() throws AccessDeniedException {
        List<ChamadoDetalhamento> chamados = chamadoService.listarChamadosDoCliente()
                .stream()
                .map(ChamadoDetalhamento::new)
                .toList();
        return ResponseEntity.ok(chamados);
    }

}