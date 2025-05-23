package appetito.apicardapio.controller;

import appetito.apicardapio.dto.DadosFuncionario;
import appetito.apicardapio.dto.GetAll.CardapioDados;
import appetito.apicardapio.dto.GetAll.FuncionarioDados;
import appetito.apicardapio.dto.cadastro.EstabelecimentoCadastro;
import appetito.apicardapio.dto.GetAll.EstabelecimentoDados;
import appetito.apicardapio.dto.detalhamento.EstabelecimentoDetalhamento;
import appetito.apicardapio.entity.*;
import appetito.apicardapio.enums.PapelUsuario;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.*;
import appetito.apicardapio.security.DiscordAlert;
import appetito.apicardapio.service.CardapioService;
import appetito.apicardapio.service.EstabelecimentoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/estabelecimento")
public class EstabelecimentoController {
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final CardapioService cardapioService;
    private final EstabelecimentoService estabelecimentoService;

    public EstabelecimentoController(EstabelecimentoRepository estabelecimentoRepository, CardapioService cardapioService, EstabelecimentoService estabelecimentoService) {
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.cardapioService = cardapioService;
        this.estabelecimentoService = estabelecimentoService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<EstabelecimentoDetalhamento> cadastrarEstabelecimento(
            @RequestBody @Valid EstabelecimentoCadastro dadosEstabelecimento,
            UriComponentsBuilder uriE) {

        UsuarioDashboard usuarioDashboard = (UsuarioDashboard) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Estabelecimento estabelecimento = estabelecimentoService.cadastrarEstabelecimento(dadosEstabelecimento, usuarioDashboard);
        var uri = uriE.path("/estabelecimento/{id}").buildAndExpand(estabelecimento.getEstabelecimentoId()).toUri();
        return ResponseEntity.created(uri).body(new EstabelecimentoDetalhamento(estabelecimento));
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/dashboard/me")
    public ResponseEntity<List<EstabelecimentoDados>> listarEstabelecimentosDoUsuario() {
        UsuarioDashboard usuario = (UsuarioDashboard) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<EstabelecimentoDados> detalhamentos = estabelecimentoService.listarEstabelecimentosDoUsuario(usuario);
        return ResponseEntity.ok(detalhamentos);
    }

    @GetMapping("/{nomeFantasia}")
    @Transactional
    public ResponseEntity<List<EstabelecimentoDados>> listarEstabelecimentoPorNomeFantasia(@PathVariable String nomeFantasia) {
        List<EstabelecimentoDados> resultado = estabelecimentoService.listarPorNomeFantasia(nomeFantasia);
        return ResponseEntity.ok(resultado);
    }


    @DeleteMapping("/dashboard/me/{id}")
    @Transactional
    @PreAuthorize("@preAuthorizeService.ehAdministrador(authentication.principal, #estabelecimento)")
    public ResponseEntity<Void> deletarEstabelecimento(@PathVariable("id") Estabelecimento estabelecimento) {
        estabelecimentoRepository.delete(estabelecimento);
        return ResponseEntity.noContent().build();
    }

    // Adicionar no UsuarioDashboard Service depois
    @PostMapping
    @Transactional
    @PreAuthorize("@preAuthorizeService.podeGerenciarEstabelecimento(#dto.estabelecimentoId, authentication.principal)")
    public ResponseEntity<Void> vincularFuncionario(@RequestBody @Valid DadosFuncionario dto) {
        UsuarioDashboard administrador = (UsuarioDashboard) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String ip = request.getRemoteAddr();

        estabelecimentoService.vincularFuncionario(dto, administrador, ip);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    // Adicionar no UsuarioDashboard Service depois
    @PutMapping
    @Transactional
    @PreAuthorize("@preAuthorizeService.podeGerenciarEstabelecimento(#dto.estabelecimentoId, authentication.principal)")
    public ResponseEntity<Void> atualizarPapelFuncionario(@RequestBody @Valid DadosFuncionario dto) throws AccessDeniedException {
        UsuarioDashboard administrador = (UsuarioDashboard) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String ip = request.getRemoteAddr();

        estabelecimentoService.atualizarPapelFuncionario(dto, administrador, ip);

        return ResponseEntity.noContent().build();
    }
    // Adicionar no UsuarioDashboard Service depois

    @GetMapping
    @PreAuthorize("@preAuthorizeService.ehAdministrador(authentication.principal, #estabelecimentoId)")
    public ResponseEntity<List<FuncionarioDados>> listarFuncionarios(@RequestParam Long estabelecimentoId) throws AccessDeniedException {
        UsuarioDashboard administrador = (UsuarioDashboard) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<FuncionarioDados> funcionarios = estabelecimentoService.listarFuncionarios(estabelecimentoId, administrador);

        return ResponseEntity.ok(funcionarios);
    }

    @GetMapping("/{nomeFantasia}/cardapio")
    public ResponseEntity<List<CardapioDados>> listarCardapiosDoEstabelecimento(
            @PathVariable String nomeFantasia
    ) {
        List<CardapioDados> cardapios = cardapioService
                .listarCardapiosComProdutosPorNomeFantasia(nomeFantasia);

        if (cardapios == null || cardapios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(cardapios);
    }

    @GetMapping("/{nomeFantasia}/mesa/{id}/cardapio")
    public ResponseEntity<List<CardapioDados>> listarCardapiosPorMesa(
            @PathVariable String nomeFantasia,
            @PathVariable Long id) {

        List<CardapioDados> cardapios = cardapioService.listarCardapiosPorMesa(nomeFantasia, id);

        if (cardapios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(cardapios);
    }

}
