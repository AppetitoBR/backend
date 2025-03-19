package appetito.apicardapio.entity;

import appetito.apicardapio.dto.CardapioCadastro;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

    @Column(nullable = false)
    private String nome;

    private String secao;

    private String descricao;


    @Column(name = "estabelecimento_id", nullable = false)
    private Long estabelecimento;

    @Column(name = "colaborador_id", nullable = false)
    private Long colaborador_id;

    @Column(nullable = false)
    private LocalDate vigencia_inicio;

    @Column(nullable = false)
    private LocalDate vigencia_fim;

    @Column(nullable = false)
    private Boolean ativo = true;

    public Cardapio(CardapioCadastro cardapioCadastro) {
        this.nome = cardapioCadastro.nome();
        this.estabelecimento = cardapioCadastro.estabelecimento_id();
        this.colaborador_id = cardapioCadastro.colaborador_id();
        this.secao = cardapioCadastro.secao();
        this.descricao = cardapioCadastro.descricao();
        this.vigencia_inicio = LocalDate.now();
        this.vigencia_fim = LocalDate.now();
        this.ativo = true;
    }

}