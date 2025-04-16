package appetito.apicardapio.controller;

import appetito.apicardapio.dto.DadosFuncionario;
import appetito.apicardapio.dto.GetAll.CardapioDados;
import appetito.apicardapio.dto.GetAll.FuncionarioDados;
import appetito.apicardapio.dto.cadastro.EstabelecimentoCadastro;
import appetito.apicardapio.dto.detalhamento.EstabelecimentoDetalhamento;
import appetito.apicardapio.dto.GetAll.EstabelecimentoDados;
import appetito.apicardapio.entity.*;
import appetito.apicardapio.enums.PapelUsuario;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.*;
import appetito.apicardapio.security.DiscordAlert;
import appetito.apicardapio.service.CardapioService;
import ch.qos.logback.classic.Logger;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
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
import java.util.Optional;
import java.util.stream.Collectors;

import static appetito.apicardapio.enums.PapelUsuario.ADMINISTRADOR;

@RestController
@RequestMapping("/estabelecimento")
public class EstabelecimentoController {
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository;
    private final UsuarioDashboardRepository usuarioDashboardRepository;
    private final CardapioService cardapioService;
    private final MesaRepository mesaRepository;
    private final CardapioRepository cardapioRepository;

    public EstabelecimentoController(EstabelecimentoRepository estabelecimentoRepository, UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository, UsuarioDashboardRepository usuarioDashboardRepository, MesaRepository mesaRepository, CardapioService cardapioService, CardapioRepository cardapioRepository) {
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.usuarioEstabelecimentoRepository = usuarioEstabelecimentoRepository;
        this.usuarioDashboardRepository = usuarioDashboardRepository;
        this.mesaRepository = mesaRepository;
        this.cardapioService = cardapioService;
        this.cardapioRepository = cardapioRepository;
    }
    @PostMapping
    @Transactional
    public ResponseEntity<EstabelecimentoDetalhamento> cadastrarEstabelecimento(
            @RequestBody @Valid EstabelecimentoCadastro dadosEstabelecimento,
            UriComponentsBuilder uriE,
            HttpServletRequest request) throws AccessDeniedException {

        // Verificar se o usuário tem permissão para cadastrar um estabelecimento
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UsuarioDashboard usuarioDashboard)) {
            String ip = Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                    .orElse(request.getRemoteAddr());
            Logger log = (Logger) LoggerFactory.getLogger(getClass());
            log.warn("🍯 HONEYPOT ALERT: Tentativa de criar estabelecimento sem ser UsuarioDashboard. IP: {}, Tipo: {}",
                    ip,
                    principal.getClass().getSimpleName());
            throw new AccessDeniedException("Acesso negado. Somente usuários autorizados podem cadastrar estabelecimentos.");
        }

        // Verificar se o usuário já possui um estabelecimento
        boolean jaPossuiEstabelecimento = usuarioEstabelecimentoRepository.existsByUsuario(usuarioDashboard);
        if (jaPossuiEstabelecimento) {
            throw new AccessDeniedException("Você já possui um estabelecimento cadastrado.");
        }

        // Criar o estabelecimento
        Estabelecimento estabelecimento = new Estabelecimento(dadosEstabelecimento);
        estabelecimento.setUsuarioCadastro(usuarioDashboard);
        estabelecimentoRepository.save(estabelecimento);

        // Gerar URL do cardápio digital, se necessário
        if (estabelecimento.getUrl_cardapio_digital() == null) {
            String urlCardapio = "https://" + estabelecimento.getNomeFantasia() + ".localhost:8080";
            estabelecimento.setUrl_cardapio_digital(urlCardapio);
            estabelecimentoRepository.save(estabelecimento);
        }

        // Vincular o usuário ao estabelecimento com o papel de administrador
        UsuarioEstabelecimento usuariodoestabelecimento = new UsuarioEstabelecimento(estabelecimento, usuarioDashboard, ADMINISTRADOR);
        usuarioEstabelecimentoRepository.save(usuariodoestabelecimento);

        // Criar a URI de resposta
        var uri = uriE.path("/estabelecimento/{id}").buildAndExpand(estabelecimento.getEstabelecimentoId()).toUri();
        return ResponseEntity.created(uri).body(new EstabelecimentoDetalhamento(estabelecimento));
    }


    @GetMapping("/dashboard/me")
    public ResponseEntity<List<EstabelecimentoDados>> listarEstabelecimentosDoUsuario(HttpServletRequest request) throws AccessDeniedException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UsuarioDashboard usuario)) {
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty()) {
                ip = request.getRemoteAddr();
            }
            Logger log = (Logger) LoggerFactory.getLogger(getClass());
            log.warn("Token indevido acessou endpoint de dashboard. IP: {}, Tipo: {}, Endpoint: dashboard/me",
                    ip, principal.getClass().getSimpleName());
            throw new AccessDeniedException("HONEY POT 🍯");
        }
        List<Estabelecimento> estabelecimentos = usuarioEstabelecimentoRepository
                .findAllByUsuario(usuario)
                .stream()
                .map(UsuarioEstabelecimento::getEstabelecimento)
                .toList();

        List<EstabelecimentoDados> detalhamentos = estabelecimentos.stream()
                .map(EstabelecimentoDados::new)
                .toList();
        return ResponseEntity.ok(detalhamentos);
    }

    @GetMapping("estabelecimento/{nomeFantasia}")
    @Transactional
    public ResponseEntity<List<EstabelecimentoDados>> listarEstabelecimentoPorNomeFantasia(@PathVariable String nomeFantasia){
        List<Estabelecimento> estabelecimentos = estabelecimentoRepository
                .findByNomeFantasiaContainingIgnoreCase(nomeFantasia);
        List<EstabelecimentoDados> resultado = estabelecimentos.stream()
                .map(EstabelecimentoDados::new)
                .toList();
        return ResponseEntity.ok(resultado);
    }


    @DeleteMapping("/dashboard/me")
    @Transactional
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> deletarMeuEstabelecimento() {
        UsuarioDashboard usuario = (UsuarioDashboard) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<UsuarioEstabelecimento> vinculos = usuarioEstabelecimentoRepository.findAllByUsuario(usuario);
        if (vinculos.isEmpty()) {
            throw new ResourceNotFoundException("Você não possui estabelecimento cadastrado.");
        }
        Estabelecimento estabelecimento = vinculos.getFirst().getEstabelecimento();
        estabelecimentoRepository.delete(estabelecimento);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/funcionarios")
    @Transactional
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> vincularFuncionario(
            @RequestBody @Valid DadosFuncionario dto) throws AccessDeniedException {

        UsuarioDashboard administrador = (UsuarioDashboard) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Estabelecimento estabelecimento = usuarioEstabelecimentoRepository
                .findAllByUsuario(administrador)
                .stream()
                .filter(v -> v.getPapel() == PapelUsuario.ADMINISTRADOR)
                .map(UsuarioEstabelecimento::getEstabelecimento)
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("Você não possui um estabelecimento como administrador."));

        if (dto.papel() == null || !papelPermitido(dto.papel())) {
            throw new IllegalArgumentException("Papel de usuário inválido ou não permitido.");
        }

        if (dto.papel() == PapelUsuario.ADMINISTRADOR) {
            throw new IllegalArgumentException("Você não pode vincular outro administrador.");
        }

        UsuarioDashboard funcionario = usuarioDashboardRepository.findByEmail(dto.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário com e-mail não encontrado."));

        boolean jaVinculado = usuarioEstabelecimentoRepository.existsByUsuarioAndEstabelecimento(funcionario, estabelecimento);
        if (jaVinculado) {
            throw new IllegalArgumentException("Usuário já está vinculado a este estabelecimento.");
        }

        var emailDoFuncionario = funcionario.getEmail();
        var emailDoPatrao = administrador.getEmail();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        var ip = request.getRemoteAddr();
        new DiscordAlert().AlertDiscord("👨‍💼 **" + emailDoPatrao + "** adicionou 👷 **" + emailDoFuncionario + "** ao estabelecimento com sucesso!\n" + " 🌐 IP: " + ip );

        UsuarioEstabelecimento vinculo = new UsuarioEstabelecimento(estabelecimento, funcionario, dto.papel());

        estabelecimentoRepository.save(estabelecimento);
        usuarioEstabelecimentoRepository.save(vinculo);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/funcionarios")
    @Transactional
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> atualizarPapelFuncionario(@RequestBody @Valid DadosFuncionario dto) throws AccessDeniedException {
        UsuarioDashboard administrador = (UsuarioDashboard) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Estabelecimento estabelecimento = usuarioEstabelecimentoRepository
                .findAllByUsuario(administrador)
                .stream()
                .filter(v -> v.getPapel() == PapelUsuario.ADMINISTRADOR)
                .map(UsuarioEstabelecimento::getEstabelecimento)
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("Você não possui um estabelecimento como administrador."));

        if (dto.papel() == null || !papelPermitido(dto.papel())) {
            throw new IllegalArgumentException("Papel de usuário inválido ou não permitido.");
        }

        UsuarioDashboard funcionario = usuarioDashboardRepository.findByEmail(dto.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário com e-mail não encontrado."));

        UsuarioEstabelecimento vinculo = usuarioEstabelecimentoRepository
                .findByUsuarioAndEstabelecimento(funcionario, estabelecimento)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não está vinculado ao seu estabelecimento."));

        vinculo.setPapel(dto.papel());
        usuarioEstabelecimentoRepository.save(vinculo);

        var emailDoFuncionario = funcionario.getEmail();
        var emailDoPatrao = administrador.getEmail();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        var ip = request.getRemoteAddr();
        new DiscordAlert().AlertDiscord("✏️ **" + emailDoPatrao + "** alterou o papel de 👷 **" + emailDoFuncionario + "** para **" + dto.papel().name() + "** 🌐 IP: " + ip);

        return ResponseEntity.noContent().build();
    }

    private boolean papelPermitido(PapelUsuario papel) {
        return papel == PapelUsuario.ATENDENTE
                || papel == PapelUsuario.GERENTE
                || papel == PapelUsuario.COZINHEIRO;
    }

    @GetMapping("/funcionarios")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<FuncionarioDados>> listarFuncionarios() throws AccessDeniedException {
        UsuarioDashboard administrador = (UsuarioDashboard) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Estabelecimento estabelecimento = usuarioEstabelecimentoRepository
                .findAllByUsuario(administrador)
                .stream()
                .filter(v -> v.getPapel() == PapelUsuario.ADMINISTRADOR)
                .map(UsuarioEstabelecimento::getEstabelecimento)
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("Você não possui um estabelecimento como administrador."));

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

}
