package appetito.apicardapio.controller;

import appetito.apicardapio.dto.cadastro.MesaCadastro;
import appetito.apicardapio.dto.detalhamento.MesaDetalhamento;
import appetito.apicardapio.entity.Mesa;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.MesaRepository;
import appetito.apicardapio.service.MesaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * Controlador REST responsável pelas operações relacionadas às mesas dos estabelecimentos.
 * <p>
 * Fornece endpoints para cadastro, atualização, exclusão, listagem e recuperação de QR Code das mesas.
 */
@RestController
@RequestMapping("/mesas")
public class MesaController {

    @Autowired
    private MesaService mesaService;

    private final MesaRepository mesaRepository;

    public MesaController(MesaRepository mesaRepository) {
        this.mesaRepository = mesaRepository;
    }

    /**
     * Cadastra uma nova mesa para um estabelecimento específico.
     *
     * @param estabelecimentoId ID do estabelecimento onde a mesa será cadastrada.
     * @param dadosMesa Dados da mesa a ser cadastrada.
     * @param uriBuilder Utilizado para construir a URI de resposta.
     * @return ResponseEntity com status 201 (Created) e os detalhes da mesa cadastrada.
     */
    @PostMapping("/{estabelecimentoId}")
    @PreAuthorize("@preAuthorizeService.podeGerenciarEstabelecimento(#estabelecimentoId, authentication.principal)")
    public ResponseEntity<MesaDetalhamento> cadastrarMesa(
            @PathVariable Long estabelecimentoId,
            @RequestBody @Valid MesaCadastro dadosMesa,
            UriComponentsBuilder uriBuilder) {

        MesaDetalhamento mesaDetalhamento = mesaService.cadastrarMesa(estabelecimentoId, dadosMesa);
        var uri = uriBuilder.path("/mesas/{id}").buildAndExpand(mesaDetalhamento.mesa_id()).toUri();
        return ResponseEntity.created(uri).body(mesaDetalhamento);
    }

    /**
     * Atualiza uma mesa existente de um estabelecimento.
     *
     * @param estabelecimentoId ID do estabelecimento associado à mesa.
     * @param id ID da mesa a ser atualizada.
     * @param dadosMesa Novos dados da mesa.
     * @return ResponseEntity com status 200 (OK) e os detalhes atualizados da mesa.
     */
    @PutMapping("/{estabelecimentoId}/{id}")
    @PreAuthorize("@preAuthorizeService.podeGerenciarEstabelecimento(#estabelecimentoId, authentication.principal)")
    public ResponseEntity<MesaDetalhamento> atualizarMesa(
            @PathVariable Long estabelecimentoId,
            @PathVariable Long id,
            @RequestBody @Valid MesaCadastro dadosMesa) {

        MesaDetalhamento mesaDetalhamento = mesaService.atualizarMesa(estabelecimentoId, id, dadosMesa);
        return ResponseEntity.ok(mesaDetalhamento);
    }

    /**
     * Exclui uma mesa de um determinado estabelecimento.
     *
     * @param estabelecimentoId ID do estabelecimento vinculado à mesa.
     * @param id ID da mesa a ser excluída.
     * @return ResponseEntity com status 204 (No Content) se a exclusão for bem-sucedida.
     */
    @DeleteMapping("/{estabelecimentoId}/{id}")
    @PreAuthorize("@preAuthorizeService.podeGerenciarEstabelecimento(#estabelecimentoId, authentication.principal)")
    public ResponseEntity<Void> excluirMesa(@PathVariable Long estabelecimentoId, @PathVariable Long id) {
        mesaService.excluirMesa(estabelecimentoId, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lista todas as mesas de um estabelecimento a partir do seu nome fantasia.
     *
     * @param nomeFantasia Nome fantasia do estabelecimento.
     * @return ResponseEntity com status 200 (OK) e a lista de mesas detalhadas.
     */
    @GetMapping("/{nomeFantasia}/mesas")
    @PreAuthorize("@preAuthorizeService.podeAtenderEstabelecimentoPorNomeFantasia(#nomeFantasia, authentication.principal)")
    public ResponseEntity<List<MesaDetalhamento>> listarMesas(@PathVariable String nomeFantasia) {
        List<MesaDetalhamento> mesas = mesaService.listarMesasPorEstabelecimento(nomeFantasia);
        return ResponseEntity.ok(mesas);
    }

    /**
     * Retorna a imagem do QR Code associado a uma mesa específica.
     *
     * @param id ID da mesa.
     * @return ResponseEntity com a imagem do QR Code em formato PNG ou status 204 (No Content) se não houver imagem.
     */
    @GetMapping("/{id}/qrcode")
    public ResponseEntity<byte[]> obterQRCodeDaMesa(@PathVariable Long id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada"));

        byte[] qrCodeBytes = mesa.getQrcode();
        if (qrCodeBytes == null || qrCodeBytes.length == 0) {
            return ResponseEntity.noContent().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(qrCodeBytes.length);

        return new ResponseEntity<>(qrCodeBytes, headers, HttpStatus.OK);
    }
}