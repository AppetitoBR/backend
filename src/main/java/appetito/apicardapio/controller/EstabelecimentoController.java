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
    private final UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository;
    private final UsuarioDashboardRepository usuarioDashboardRepository;
    private final CardapioService cardapioService;
    private final MesaRepository mesaRepository;
    private final CardapioRepository cardapioRepository;
    private final EstabelecimentoService estabelecimentoService;

    public EstabelecimentoController(EstabelecimentoRepository estabelecimentoRepository, UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository, UsuarioDashboardRepository usuarioDashboardRepository, MesaRepository mesaRepository, CardapioService cardapioService, CardapioRepository cardapioRepository, EstabelecimentoService estabelecimentoService) {
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.usuarioEstabelecimentoRepository = usuarioEstabelecimentoRepository;
        this.usuarioDashboardRepository = usuarioDashboardRepository;
        this.mesaRepository = mesaRepository;
        this.cardapioService = cardapioService;
        this.cardapioRepository = cardapioRepository;
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
    @GetMapping("/funcionarios")
    @PreAuthorize("@preAuthorizeService.ehAdministrador(authentication.principal, #estabelecimentoId)")
    public ResponseEntity<List<FuncionarioDados>> listarFuncionarios(@RequestParam Long estabelecimentoId) throws AccessDeniedException {
        UsuarioDashboard administrador = (UsuarioDashboard) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId)
                .orElseThrow(() -> new AccessDeniedException("Estabelecimento não encontrado ou acesso negado"));

        List<FuncionarioDados> funcionarios = usuarioEstabelecimentoRepository
                .findAllByEstabelecimento(estabelecimento)
                .stream()
                .filter(v -> !v.getUsuario().getUsuario_dashboard_id().equals(administrador.getUsuario_dashboard_id()))
                .map(v -> new FuncionarioDados(
                        v.getUsuario().getUsuario_dashboard_id(),
                        v.getUsuario().getNome_completo(),
                        v.getUsuario().getEmail(),
                        v.getPapel()))
                .toList();

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

        Estabelecimento estabelecimento = estabelecimentoRepository
                .findByNomeFantasia(nomeFantasia)
                .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado"));

        Mesa mesa = mesaRepository.findById(id)
                .filter(m -> m.getEstabelecimento().equals(estabelecimento))
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada ou não pertence ao estabelecimento"));

        List<Cardapio> cardapios = cardapioRepository.findByEstabelecimentoNomeFantasia(nomeFantasia);

        if (cardapios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<CardapioDados> cardapioDados = cardapios.stream()
                .map(CardapioDados::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(cardapioDados);
    }

    // vou colocar no service dps
    private boolean papelPermitido(PapelUsuario papel) {
        return papel == PapelUsuario.ATENDENTE
                || papel == PapelUsuario.GERENTE
                || papel == PapelUsuario.COZINHEIRO;
    }

}
