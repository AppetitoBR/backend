package appetito.apicardapio.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
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

    private LocalDateTime data_atualizacao;

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

    // Construtor padrão (necessário para JPA)
    public Estabelecimento() {}

    // Construtor para DTO
    public Estabelecimento(String razao_social, String nome_fantasia, String cnpj, String tipo, String segmento, Usuario usuario_cadastro) {
        this.razao_social = razao_social;
        this.nome_fantasia = nome_fantasia;
        this.cnpj = cnpj;
        this.tipo = tipo;
        this.segmento = segmento;
        this.usuario_cadastro = usuario_cadastro;
    }
}