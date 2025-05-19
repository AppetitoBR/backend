package appetito.apicardapio.security;

import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.entity.UsuarioEstabelecimento;
import appetito.apicardapio.enums.PapelUsuario;
import appetito.apicardapio.repository.UsuarioEstabelecimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
public class PreAuthorizeService {

    @Autowired
    private UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository;

    public boolean podeGerenciarEstabelecimento(Estabelecimento estabelecimento, UsuarioDashboard usuarioDashboard){
        return usuarioEstabelecimentoRepository
                .findByUsuarioAndEstabelecimentoAndPapelIn(
                        usuarioDashboard,
                        estabelecimento,
                        List.of(PapelUsuario.GERENTE, PapelUsuario.ADMINISTRADOR)
                )
                .isPresent();
    }
    public boolean ehAdministrador(Object principal, Estabelecimento estabelecimento){
        if(!(principal instanceof UsuarioDashboard usuarioDashboard)){
            return false; // return false se o cara nao for usuario
        }
        Optional<UsuarioEstabelecimento> vinculo = usuarioEstabelecimentoRepository.findByUsuarioAndEstabelecimento(usuarioDashboard, estabelecimento);
        return vinculo
                .map(v -> v.getPapel() == PapelUsuario.ADMINISTRADOR)
                .orElse(false);

    }

    public boolean estaVinculadoAoEstabelecimento(Estabelecimento estabelecimento, UsuarioDashboard usuarioDashboard){
        return usuarioEstabelecimentoRepository.existsByUsuarioAndEstabelecimento(usuarioDashboard, estabelecimento);

    }

//  00  ->  00
    // ROLE = Usuario ->

}
