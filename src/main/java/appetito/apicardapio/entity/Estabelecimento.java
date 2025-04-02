package appetito.apicardapio.entity;

import appetito.apicardapio.dto.cadastro.EstabelecimentoCadastro;
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
    private Long estabelecimento_id;

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

    @Column(name = "usuario_cadastro_id", nullable = false)
    private Long usuario_cadastro;

    @Column(nullable = false)
    private LocalDateTime data_alteracao_cadastro = LocalDateTime.now();

    @Column(name = "usuario_alteracao_id", nullable = false)
    private Long usuario_alteracao;

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
        this.usuario_cadastro = dadosEstabelecimento.usuario_cadastro_id();

    }


    public void setNome(String estabelecimentoTeste) {

    }
}