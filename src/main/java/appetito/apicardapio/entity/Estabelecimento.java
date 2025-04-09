package appetito.apicardapio.entity;

import appetito.apicardapio.dto.cadastro.EstabelecimentoCadastro;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "estabelecimento")
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
    private Long estabelecimentoId;

    @Column(nullable = false)
    private String razao_social;

    @Column(name= "nome_fantasia",nullable = false)
    private String nomeFantasia;

    @Column(nullable = false, unique = true)
    private String cnpj;

    @Lob
    private String contatos;

    @Lob
    private String endereco;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private LocalDateTime data_cadastro = LocalDateTime.now();

    @Column(nullable = false)
    private String segmento;

    @ManyToOne
    @JoinColumn(name = "usuario_cadastro_id", nullable = false)
    private UsuarioDashboard usuarioCadastro;

    @Column(nullable = false)
    private LocalDateTime data_alteracao_cadastro = LocalDateTime.now();

    @OneToMany(mappedBy = "estabelecimento", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<UsuarioEstabelecimento> vinculos = new ArrayList<>();

 //   @ManyToOne
  //  @JoinColumn(name = "usuario_alteracao_id")
  //  private UsuarioDashboard usuarioAlteracao;

    @Lob
    private String observacao;

    @Lob
    private String logomarca;

    private String url_cardapio_digital;

    private String subdominio_appetito;

    public Estabelecimento(EstabelecimentoCadastro dadosEstabelecimento) {
        this.razao_social = dadosEstabelecimento.razao_social();
        this.nomeFantasia = dadosEstabelecimento.nome_fantasia();
        this.cnpj = dadosEstabelecimento.cnpj();
        this.tipo = dadosEstabelecimento.tipo();
        this.segmento = dadosEstabelecimento.segmento();

    }
}