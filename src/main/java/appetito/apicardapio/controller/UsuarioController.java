package appetito.apicardapio.controller;

import appetito.apicardapio.dto.cadastro.EstabelecimentoCadastro;
import appetito.apicardapio.dto.cadastro.UsuarioCadastro;
import appetito.apicardapio.dto.detalhamento.EstabelecimentoDetalhamento;
import appetito.apicardapio.dto.detalhamento.UsuarioDetalhamento;
import appetito.apicardapio.dto.GetAll.UsuarioDados;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.Usuario;
import appetito.apicardapio.enums.PerfilUsuario;
import appetito.apicardapio.repository.UsuarioRepository;
import appetito.apicardapio.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;


import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuário", description = "Gerenciamento de usuários e imagens de perfil")
public class UsuarioController {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    public UsuarioController(UsuarioRepository usuarioRepository, UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
    }


    @GetMapping
    @Transactional
    public ResponseEntity<List<UsuarioDados>> listarUsuarios() {
        var lista =  usuarioRepository.findAll().stream().map(UsuarioDados::new).toList();
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<UsuarioDetalhamento> cadastrarUsuario(@RequestBody @Valid UsuarioCadastro dadosUsuario, UriComponentsBuilder uriU){
        var usuario = new Usuario(dadosUsuario);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuarioRepository.save(usuario);
        var uri = uriU.path("/usuarios/{id}").buildAndExpand(usuario.getUsuario_id()).toUri();
        return ResponseEntity.created(uri).body(new UsuarioDetalhamento(usuario));
    }

    @Operation(summary = "Upload da imagem de perfil do usuário")
    @PostMapping(value = "/{id}/upload-imagem", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadImagemPerfil(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file
    ) {
        try {
            Usuario usuario = usuarioService.salvarImagemPerfil(id, file);
            if (usuario != null) {
                return ResponseEntity.ok("Imagem de perfil salva");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar imagem");
        }
    }

    @GetMapping("/{id}/imagem-perfil")
    public ResponseEntity<byte[]> buscarImagemPerfil(@PathVariable Long id) {
        byte[] imagem = usuarioService.obterImagemPerfil(id);
        if (imagem == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "image/png");
        return new ResponseEntity<>(imagem, headers, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioDetalhamento> meuPerfil() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof Usuario usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        var usuarioDetalhamento = new UsuarioDetalhamento(usuario);
        return ResponseEntity.ok(usuarioDetalhamento);
    }

    @GetMapping("/me/imagem")
    public ResponseEntity<byte[]> minhaImagem() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof Usuario usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        byte[] imagem = usuarioService.obterImagemPerfil(usuario.getUsuario_id());

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imagem);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioDetalhamento> deletarUsuario(@PathVariable Long id) {
        var usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        usuarioRepository.delete(usuario);
        return ResponseEntity.ok().build();
    }
// em teste ainda
    @PutMapping("/me")
    public ResponseEntity<UsuarioDetalhamento> atualizarUsuario(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof Usuario usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        var usuarioDetalhamento = new UsuarioDetalhamento(usuario);
        usuarioRepository.delete(usuario);
        return ResponseEntity.ok(usuarioDetalhamento);
    }


}
