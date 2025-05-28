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

@RestController
@RequestMapping("/mesas")
public class MesaController {

    @Autowired
    private MesaService mesaService;

    private final MesaRepository mesaRepository;

    public MesaController(MesaRepository mesaRepository) {
        this.mesaRepository = mesaRepository;
    }

    @PostMapping("/{estabelecimentoId}")
    @PreAuthorize("@preAuthorizeService.podeGerenciarEstabelecimento(#estabelecimentoId,authentication.principal)")
    public ResponseEntity<MesaDetalhamento> cadastrarMesa(
            @PathVariable Long estabelecimentoId,
            @RequestBody @Valid MesaCadastro dadosMesa,
            UriComponentsBuilder uriBuilder) {

        MesaDetalhamento mesaDetalhamento = mesaService.cadastrarMesa(estabelecimentoId, dadosMesa);
        var uri = uriBuilder.path("/mesas/{id}").buildAndExpand(mesaDetalhamento.mesa_id()).toUri();
        return ResponseEntity.created(uri).body(mesaDetalhamento);
    }
    @PutMapping("/{estabelecimentoId}/{id}")
    @PreAuthorize("@preAuthorizeService.podeGerenciarEstabelecimento(#estabelecimentoId, authentication.principal)")
    public ResponseEntity<MesaDetalhamento> atualizarMesa(
            @PathVariable Long estabelecimentoId,
            @PathVariable Long id,
            @RequestBody @Valid MesaCadastro dadosMesa) {

        MesaDetalhamento mesaDetalhamento = mesaService.atualizarMesa(estabelecimentoId, id, dadosMesa);
        return ResponseEntity.ok(mesaDetalhamento);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirMesa(@PathVariable Long id) {
        mesaService.excluirMesa(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{nomeFantasia}/mesas")
    public ResponseEntity<List<MesaDetalhamento>> listarMesas(@PathVariable String nomeFantasia) {
        List<MesaDetalhamento> mesas = mesaService.listarMesasPorEstabelecimento(nomeFantasia);
        return ResponseEntity.ok(mesas);
    }

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