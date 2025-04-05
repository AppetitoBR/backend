package appetito.apicardapio.controller;

import appetito.apicardapio.dto.cadastro.EstabelecimentoCadastro;
import appetito.apicardapio.dto.detalhamento.EstabelecimentoDetalhamento;
import appetito.apicardapio.dto.GetAll.EstabelecimentoDados;
import appetito.apicardapio.entity.Cliente;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.EstabelecimentoRepository;
import appetito.apicardapio.repository.UsuarioDashboardRepository;
import ch.qos.logback.classic.Logger;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/estabelecimento")
public class EstabelecimentoController {
    private final EstabelecimentoRepository estabelecimentoRepository;
    public EstabelecimentoController(EstabelecimentoRepository estabelecimentoRepository) {
        this.estabelecimentoRepository = estabelecimentoRepository;
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
        boolean jaPossuiEstabelecimento = estabelecimentoRepository.existsByUsuarioCadastro(usuarioDashboard);
        if (jaPossuiEstabelecimento) {
            throw new AccessDeniedException("Você já possui um estabelecimento cadastrado.");
        }
        Estabelecimento estabelecimento = new Estabelecimento(dadosEstabelecimento);
        estabelecimento.setUsuarioCadastro(usuarioDashboard);
        estabelecimentoRepository.save(estabelecimento);
        var uri = uriE.path("/estabelecimento/{id}").buildAndExpand(estabelecimento.getEstabelecimento_id()).toUri();
        return ResponseEntity.created(uri).body(new EstabelecimentoDetalhamento(estabelecimento));
    }

    @GetMapping("/dashboard/me")
    public ResponseEntity<List<EstabelecimentoDetalhamento>> listarEstabelecimentosDoUsuario(HttpServletRequest request) throws AccessDeniedException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UsuarioDashboard)) {
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty()) {
                ip = request.getRemoteAddr();
            }
            Logger log = (Logger) LoggerFactory.getLogger(getClass());
            log.warn("Token indevido acessou endpoint de dashboard. IP: {}, Tipo: {}, Endpoint: dashboard/me",
                    ip, principal.getClass().getSimpleName());
            throw new AccessDeniedException("HONEY POT 🍯");
        }
        UsuarioDashboard usuario = (UsuarioDashboard) principal;
        List<Estabelecimento> estabelecimentos = estabelecimentoRepository.findAllByUsuarioCadastro(usuario);
        List<EstabelecimentoDetalhamento> detalhamentos = estabelecimentos.stream()
                .map(EstabelecimentoDetalhamento::new)
                .toList();
        return ResponseEntity.ok(detalhamentos);
    }

    @GetMapping("estabelecimento/{nomeFantasia}")
    @Transactional
    public ResponseEntity<List<EstabelecimentoDados>> listarEstabelecimentoPorNomeFantasia(
            @PathVariable String nomeFantasia,
            HttpServletRequest request) throws AccessDeniedException {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof Cliente)) {
            String ip = Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                    .orElse(request.getRemoteAddr());

            Logger log = (Logger) LoggerFactory.getLogger(getClass());
            log.warn("🍯 HONEYPOT ALERT: Acesso indevido ao endpoint de cliente. IP: {}, User-Agent: {}, Tipo: {}",
                    ip,
                    request.getHeader("User-Agent"),
                    principal.getClass().getSimpleName());

            throw new AccessDeniedException("Acesso não autorizado");
        }

        Cliente cliente = (Cliente) principal;

        List<Estabelecimento> estabelecimentos = estabelecimentoRepository
                .findByNomeFantasiaContainingIgnoreCase(nomeFantasia);

        List<EstabelecimentoDados> resultado = estabelecimentos.stream()
                .map(EstabelecimentoDados::new)
                .toList();

        return ResponseEntity.ok(resultado);
    }


    @DeleteMapping("/{id}")
    @Transactional
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> deletarEstabelecimento(@PathVariable Long id) {
        if(estabelecimentoRepository.existsById(id)) {
            estabelecimentoRepository.deleteById(id);
        }
       return ResponseEntity.noContent().build();
    }

}
