package appetito.apicardapio.entity;

import appetito.apicardapio.dto.cadastro.CardapioCadastro;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Getter
@Setter
@Table(name = "Cardapio")
@AllArgsConstructor
@NoArgsConstructor
public class Cardapio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cardapio_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "estabelecimento_id")
    private Estabelecimento estabelecimento;

    @Column(nullable = false)
    private String nome;

    private String secao;

    private String descricao;

    @Column(nullable = false)
    private LocalDate vigencia_inicio;

    @Column(nullable = false)
    private LocalDate vigencia_fim;

    @Column(nullable = false)
    private Boolean ativo = true;

    @OneToMany(mappedBy = "cardapio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Produto> produtos;

    public Cardapio(CardapioCadastro cardapioCadastro) {
        this.nome = cardapioCadastro.nome();
        this.secao = cardapioCadastro.secao();
        this.descricao = cardapioCadastro.descricao();
        this.vigencia_inicio = LocalDate.now();
        this.vigencia_fim = LocalDate.now();
        this.ativo = true;
    }

}