package appetito.apicardapio.controller;
import appetito.apicardapio.dto.MesaCadastro;
import appetito.apicardapio.dto.MesaDetalhamento;
import appetito.apicardapio.service.MesaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/mesas")
public class MesaController {

    @Autowired
    private MesaService mesaService;

    @PostMapping
    public ResponseEntity<MesaDetalhamento> cadastrarMesa(
            @RequestBody @Valid MesaCadastro dadosMesa,
            UriComponentsBuilder uriBuilder) {

        MesaDetalhamento mesaDetalhamento = mesaService.cadastrarMesa(dadosMesa);

        var uri = uriBuilder.path("/mesas/{id}").buildAndExpand(mesaDetalhamento.id()).toUri();
        return ResponseEntity.created(uri).body(mesaDetalhamento);
    }

    @GetMapping("/{qrCode}")
    public ResponseEntity<MesaDetalhamento> buscarMesaPorQrCode(@PathVariable String qrCode) {
        MesaDetalhamento mesaDetalhamento = mesaService.buscarMesaPorQrCode(qrCode);
        return ResponseEntity.ok(mesaDetalhamento);
    }
}
