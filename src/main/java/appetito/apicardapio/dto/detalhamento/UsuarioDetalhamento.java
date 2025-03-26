package appetito.apicardapio.dto.detalhamento;

import appetito.apicardapio.entity.Usuario;
import appetito.apicardapio.enums.PapelUsuario;

import java.time.LocalDate;

public record UsuarioDetalhamento(
        Long usuario_id,
        String nome_completo,
        String cpf,
        String apelido,
        LocalDate data_nascimento,
        String idioma_padrao,
        String nacionalidade,
        String situacao,
        String contatos,
        String endereco,
        String redes_sociais,
        LocalDate data_cadastro,
        LocalDate data_atualizacao
) {
    public UsuarioDetalhamento(Usuario usuario) {
      this(
              usuario.getUsuario_id(),
              usuario.getNome_completo(),
              usuario.getCpf(),
              usuario.getApelido(),
              usuario.getData_nascimento(),
              usuario.getIdioma_padrao(),
              usuario.getNacionalidade(),
              usuario.getSituacao(),
              usuario.getContatos(),
              usuario.getEndereco(),
              usuario.getRedes_sociais(),
              usuario.getData_cadastro(),
              usuario.getData_atualizacao()
      );
    }
}
