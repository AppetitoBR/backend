package appetito.apicardapio.dto.GetAll;

import appetito.apicardapio.entity.Usuario;

public record UsuarioDados(Long usuario_id,String nome_completo) {
    public UsuarioDados (Usuario usuario){
        this(usuario.getUsuario_id(), usuario.getNome_completo());
    }
}
