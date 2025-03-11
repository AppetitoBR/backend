package appetito.apicardapio.entity;

import appetito.apicardapio.dto.CardapioCadastro;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Table(name = "Cardapio")
@Entity(name = "Cardapios")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Cardapio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardapio_id;
    private int estabelecimento_id;
    private String nome;
    private String secao;
    private String descricao;
    private int colaborador_id;
    private LocalDate vigencia_inicio;
    private LocalDate vigencia_fim;
    private boolean ativo;
    @ElementCollection
    private List<String> restricoesAlimentares;

    public Cardapio(CardapioCadastro dadosCardapio){
        this.estabelecimento_id = dadosCardapio.estabelecimento_id();
        this.nome = dadosCardapio.nome();
        this.secao = dadosCardapio.secao();
        this.descricao = dadosCardapio.descricao();
        this.colaborador_id = dadosCardapio.colaborador_id();
        this.vigencia_inicio = dadosCardapio.vigencia_inicio();
        this.vigencia_fim = dadosCardapio.vigencia_fim();
        this.ativo = dadosCardapio.ativo();
        this.restricoesAlimentares = dadosCardapio.restricoesAlimentares();
    }

}
