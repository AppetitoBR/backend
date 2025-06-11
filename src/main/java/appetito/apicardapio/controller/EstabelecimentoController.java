package appetito.apicardapio.controller;

import appetito.apicardapio.dto.DadosFuncionario;
import appetito.apicardapio.dto.GetAll.CardapioDados;
import appetito.apicardapio.dto.GetAll.FuncionarioDados;
import appetito.apicardapio.dto.cadastro.EstabelecimentoCadastro;
import appetito.apicardapio.dto.GetAll.EstabelecimentoDados;
import appetito.apicardapio.dto.detalhamento.EstabelecimentoDetalhamento;
import appetito.apicardapio.entity.*;
import appetito.apicardapio.repository.*;
import appetito.apicardapio.service.CardapioService;
import appetito.apicardapio.service.EstabelecimentoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Controlador responsável por operações relacionadas a estabelecimentos,
 * como cadastro, listagem, exclusão, vinculação de funcionários e acesso ao cardápio.
 */
@RestController
@RequestMapping("/estabelecimento")
public class EstabelecimentoController {

    private final EstabelecimentoRepository estabelecimentoRepository;
    private final CardapioService cardapioService;
    private final EstabelecimentoService estabelecimentoService;

    public EstabelecimentoController(
            EstabelecimentoRepository estabelecimentoRepository,
            CardapioService cardapioService,
            EstabelecimentoService estabelecimentoService) {
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.cardapioService = cardapioService;
        this.estabelecimentoService = estabelecimentoService;
    }

    /**
     * Cadastra um novo estabelecimento e vincula automaticamente o usuário autenticado como ADMINISTRADOR.
     *
     * @param dadosEstabelecimento dados do estabelecimento
     * @param uriE builder para construção da URI de retorno
     * @return detalhes do estabelecimento criado com status 201
     */
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

    /**
     * Lista os estabelecimentos vinculados ao usuário autenticado.
     *
     * @return lista de estabelecimentos vinculados
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/dashboard/me")
    public ResponseEntity<List<EstabelecimentoDados>> listarEstabelecimentosDoUsuario() {
        UsuarioDashboard usuario = (UsuarioDashboard) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<EstabelecimentoDados> detalhamentos = estabelecimentoService.listarEstabelecimentosDoUsuario(usuario);
        return ResponseEntity.ok(detalhamentos);
    }

    /**
     * Lista estabelecimentos que possuem o nome fantasia informado.
     *
     * @param nomeFantasia nome fantasia a ser buscado
     * @return lista de estabelecimentos encontrados
     */
    @GetMapping("/{nomeFantasia}")
    @Transactional
    public ResponseEntity<List<EstabelecimentoDados>> listarEstabelecimentoPorNomeFantasia(@PathVariable String nomeFantasia) {
        List<EstabelecimentoDados> resultado = estabelecimentoService.listarPorNomeFantasia(nomeFantasia);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Exclui um estabelecimento, desde que o usuário autenticado seja administrador dele.
     *
     * @param estabelecimento entidade injetada pelo ID
     * @return resposta sem conteúdo (204)
     */
    @DeleteMapping("/dashboard/me/{id}")
    @Transactional
    @PreAuthorize("@preAuthorizeService.ehAdministrador(authentication.principal, #estabelecimento)")
    public ResponseEntity<Void> deletarEstabelecimento(@PathVariable("id") Estabelecimento estabelecimento) {
        estabelecimentoRepository.delete(estabelecimento);
        return ResponseEntity.noContent().build();
    }

    /**
     * Vincula um funcionário a um estabelecimento. A requisição só pode ser feita por administradores do estabelecimento.
     *
     * @param dto dados do funcionário e estabelecimento
     * @return resposta com status 201 (Created)
     */
    @PostMapping("/funcionario")
    @Transactional
    @PreAuthorize("@preAuthorizeService.podeGerenciarEstabelecimento(#dto.estabelecimentoId, authentication.principal)")
    public ResponseEntity<Void> vincularFuncionario(@RequestBody @Valid DadosFuncionario dto) {
        UsuarioDashboard administrador = (UsuarioDashboard) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String ip = request.getRemoteAddr();
        estabelecimentoService.vincularFuncionario(dto, administrador, ip);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Atualiza o papel de um funcionário em um estabelecimento. A requisição só pode ser feita por administradores.
     *
     * @param dto dados do funcionário e novo papel
     * @return resposta sem conteúdo (204)
     */
    @PutMapping
    @Transactional
    @PreAuthorize("@preAuthorizeService.podeGerenciarEstabelecimento(#dto.estabelecimentoId, authentication.principal)")
    public ResponseEntity<Void> atualizarPapelFuncionario(@RequestBody @Valid DadosFuncionario dto) {
        UsuarioDashboard administrador = (UsuarioDashboard) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String ip = request.getRemoteAddr();
        estabelecimentoService.atualizarPapelFuncionario(dto, administrador, ip);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lista todos os funcionários vinculados a um estabelecimento específico.
     *
     * @param estabelecimentoId ID do estabelecimento
     * @return lista de funcionários vinculados
     */
    @GetMapping
    @PreAuthorize("@preAuthorizeService.ehAdministrador(authentication.principal, #estabelecimentoId)")
    public ResponseEntity<List<FuncionarioDados>> listarFuncionarios(@RequestParam Long estabelecimentoId) {
        UsuarioDashboard administrador = (UsuarioDashboard) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<FuncionarioDados> funcionarios = estabelecimentoService.listarFuncionarios(estabelecimentoId, administrador);
        return ResponseEntity.ok(funcionarios);
    }

    /**
     * Lista os cardápios públicos do estabelecimento com o nome fantasia informado.
     *
     * @param nomeFantasia nome fantasia do estabelecimento
     * @return lista de cardápios com produtos, ou 204 se vazio
     */
    @GetMapping("/{nomeFantasia}/cardapio")
    public ResponseEntity<List<CardapioDados>> listarCardapiosDoEstabelecimento(@PathVariable String nomeFantasia) {
        List<CardapioDados> cardapios = cardapioService.listarCardapiosComProdutosPorNomeFantasia(nomeFantasia);
        return ResponseEntity.ok(cardapios);
    }

    /**
     * Lista os cardápios do estabelecimento relacionados à mesa específica.
     *
     * @param nomeFantasia nome fantasia do estabelecimento
     * @param id           ID da mesa
     * @return lista de cardápios relacionados à mesa, ou 204 se não houver
     */
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
    @PreAuthorize("@preAuthorizeService.podeGerenciarEstabelecimento(id, authentication.principal)")
    @PostMapping(value = "/{id}/upload-imagem", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImagemPerfil(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UsuarioEstabelecimento usuarioEstabelecimento) {

        if (!usuarioEstabelecimento.getUsuario().getUsuario_dashboard_id().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não tem permissão para atualizar a imagem de outro estabelecimento.");
        }

        try {
            estabelecimentoService.uploadLogoMarca(id, file);
            return ResponseEntity.ok("Imagem de perfil salva com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Erro ao salvar imagem: " + e.getMessage());
        }
    }
    @GetMapping("/{nomeFantasia}/imagem-perfil")
    public ResponseEntity<byte[]> buscarImagemPerfil(@PathVariable String nomeFantasia) {
        byte[] imagem = estabelecimentoService.obterLogoMarca(nomeFantasia);
        if (imagem == null) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "image/png");
        return new ResponseEntity<>(imagem, headers, HttpStatus.OK);
    }



}

