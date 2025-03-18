package appetito.apicardapio.controller;

import appetito.apicardapio.dto.UsuarioCadastro;
import appetito.apicardapio.dto.UsuarioDetalhamento;
import appetito.apicardapio.dto.forGet.UsuarioDados;
import appetito.apicardapio.entity.Usuario;
import appetito.apicardapio.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }


    @PostMapping
    @Transactional
    public ResponseEntity<UsuarioDetalhamento> cadastrarUsuario(@RequestBody @Valid UsuarioCadastro dadosUsuario, UriComponentsBuilder uriBuilder1) {
        var usuario = new Usuario(dadosUsuario);
        usuarioRepository.save(usuario);
        var uri = uriBuilder1.path("/usuarios/{id}").buildAndExpand((usuario.getUsuario_id())).toUri();
        return ResponseEntity.created(uri).body(new UsuarioDetalhamento(usuario));
    }

    @GetMapping
    @Transactional
    public ResponseEntity<List<UsuarioDados>> listarUsuarios() {
        var lista =  usuarioRepository.findAll().stream().map(UsuarioDados::new).toList();
        return ResponseEntity.ok(lista);
    }
}
