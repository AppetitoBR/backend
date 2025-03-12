package appetito.apicardapio.controller;

import appetito.apicardapio.dto.UsuarioCadastro;
import appetito.apicardapio.entity.Usuario;
import appetito.apicardapio.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<Usuario> cadastrarUsuario(
            @RequestBody @Valid UsuarioCadastro dadosUsuario,
            UriComponentsBuilder uriBuilder) {
        Usuario usuario = usuarioService.cadastrarUsuario(dadosUsuario);
        var uri = uriBuilder.path("/usuarios/{id}").buildAndExpand(usuario.getUsuario_id()).toUri();
        return ResponseEntity.created(uri).body(usuario);
    }
}
