package appetito.apicardapio.entity;

import appetito.apicardapio.dto.EstabelecimentoCadastro;
import appetito.apicardapio.dto.forGet.UsuarioDados;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Table(name = "Estabelecimento")
@Data
@Entity(name = "Estabelecimentos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Estabelecimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "estabelecimento_id")
    private Long id; // Campo ID

    @Column(nullable = false)
    private String razao_social;

    @Column(nullable = false)
    private String nome_fantasia;

    @Column(nullable = false, unique = true)
    private String cnpj;

    @Lob
    private String contatos;

    @Lob
    private String endereco;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(nullable = false)
    private Boolean bloqueado = false;

    @Column(nullable = false)
    private LocalDateTime data_cadastro = LocalDateTime.now();

    @Column(nullable = false)
    private String segmento;

    @ManyToOne
    @JoinColumn(name = "usuario_cadastro_id", nullable = false)
    private Usuario usuario_cadastro;

    @ManyToOne
    @JoinColumn(name = "usuario_alteracao_id")
    private Usuario usuario_alteracao;

    @Lob
    private String observacao;

    @Lob
    private String logomarca;

    private String url_cardapio_digital;

    private String subdominio_appetito;

    public Estabelecimento(EstabelecimentoCadastro dadosEstabelecimento) {
        this.razao_social = dadosEstabelecimento.razao_social();
        this.nome_fantasia = dadosEstabelecimento.nome_fantasia();
        this.cnpj = dadosEstabelecimento.cnpj();
        this.tipo = dadosEstabelecimento.tipo();
        this.segmento = dadosEstabelecimento.segmento();
        this.usuario_cadastro = dadosEstabelecimento.usuario_cadastro_id();

    }


    public void setNome(String estabelecimentoTeste) {

    }
}