package appetito.apicardapio.controller;

import appetito.apicardapio.dto.DadosFuncionario;
import appetito.apicardapio.dto.cadastro.EstabelecimentoCadastro;
import appetito.apicardapio.dto.detalhamento.EstabelecimentoDetalhamento;
import appetito.apicardapio.dto.GetAll.EstabelecimentoDados;
import appetito.apicardapio.entity.Cliente;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.entity.UsuarioEstabelecimento;
import appetito.apicardapio.enums.PapelUsuario;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.EstabelecimentoRepository;
import appetito.apicardapio.repository.UsuarioDashboardRepository;
import appetito.apicardapio.repository.UsuarioEstabelecimentoRepository;
import appetito.apicardapio.security.DiscordAlert;
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

import static appetito.apicardapio.enums.PapelUsuario.ADMINISTRADOR;

@RestController
@RequestMapping("/estabelecimento")
public class EstabelecimentoController {
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository;
    private final UsuarioDashboardRepository usuarioDashboardRepository;

    public EstabelecimentoController(EstabelecimentoRepository estabelecimentoRepository, UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository, UsuarioDashboardRepository usuarioDashboardRepository) {
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.usuarioEstabelecimentoRepository = usuarioEstabelecimentoRepository;
        this.usuarioDashboardRepository = usuarioDashboardRepository;
    }
    @PostMapping
    @Transactional
    public ResponseEntity<EstabelecimentoDetalhamento> cadastrarEstabelecimento(
            @RequestBody @Valid EstabelecimentoCadastro dadosEstabelecimento,
            UriComponentsBuilder uriE,
            HttpServletRequest request) throws AccessDeniedException {
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
        boolean jaPossuiEstabelecimento = usuarioEstabelecimentoRepository.existsByUsuario(usuarioDashboard);
        if (jaPossuiEstabelecimento) {
            throw new AccessDeniedException("Você já possui um estabelecimento cadastrado.");
        }
        Estabelecimento estabelecimento = new Estabelecimento(dadosEstabelecimento);
        estabelecimento.setUsuarioCadastro(usuarioDashboard);
        estabelecimentoRepository.save(estabelecimento);

        UsuarioEstabelecimento usuariodoestabelecimento = new UsuarioEstabelecimento(estabelecimento, usuarioDashboard, ADMINISTRADOR);
        usuarioEstabelecimentoRepository.save(usuariodoestabelecimento);

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
       // HttpServletRequest request) throws AccessDeniedException
       // Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

       // if (!(principal instanceof Cliente)) {
        ///    String ip = Optional.ofNullable(request.getHeader("X-Forwarded-For"))
           //         .orElse(request.getRemoteAddr());
//
          //  Logger log = (Logger) LoggerFactory.getLogger(getClass());
        //    log.warn("🍯 HONEYPOT ALERT: Acesso indevido ao endpoint de cliente. IP: {}, User-Agent: {}, Tipo: {}",
         //           ip,
        //            request.getHeader("User-Agent"),
        //            principal.getClass().getSimpleName());
        //    new DiscordAlert().AlertDiscord("Acesso não autorizado ao listar estabelecimentos IP: " + ip);
//
         //   throw new AccessDeniedException("Acesso não autorizado");
       // }
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
    public ResponseEntity<Void> deletarMeuEstabelecimento() throws AccessDeniedException {
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
        new DiscordAlert().AlertDiscord("👨‍💼 **" + emailDoPatrao + "** adicionou 👷 **" + emailDoFuncionario + "** ao estabelecimento com sucesso!\n" + "🌐 IP: `" + ip + "`");

        UsuarioEstabelecimento vinculo = new UsuarioEstabelecimento(estabelecimento, funcionario, dto.papel());
        usuarioEstabelecimentoRepository.save(vinculo);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }
}
