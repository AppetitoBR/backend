package appetito.apicardapio.dto.GetAll;

import appetito.apicardapio.entity.UsuarioDashboard;

public record UsuarioDados(Long usuario_id,String nome_completo) {
    public UsuarioDados (UsuarioDashboard usuario){
        this(usuario.getUsuario_dashboard_id(),
                usuario.getNome_completo());
    }
}
