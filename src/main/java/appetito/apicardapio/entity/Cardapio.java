package appetito.apicardapio.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
public class Cardapio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardapio_id;

    @Column(nullable = false)
    private String nome;

    private String secao;

    private String descricao;

    @ManyToOne
    @JoinColumn(name = "estabelecimento_id", nullable = false)
    private Estabelecimento estabelecimento;

    @ManyToOne
    @JoinColumn(name = "colaborador_id")
    private Colaborador colaborador;

    @Column(nullable = false)
    private LocalDate vigencia_inicio;

    @Column(nullable = false)
    private LocalDate vigencia_fim;

    @Column(nullable = false)
    private Boolean ativo = true;

    public Cardapio() {}

    public Cardapio(String nome, String secao, String descricao, Estabelecimento estabelecimento, Colaborador colaborador, LocalDate vigencia_inicio, LocalDate vigencia_fim) {
        this.nome = nome;
        this.secao = secao;
        this.descricao = descricao;
        this.estabelecimento = estabelecimento;
        this.colaborador = colaborador;
        this.vigencia_inicio = vigencia_inicio;
        this.vigencia_fim = vigencia_fim;
    }
}