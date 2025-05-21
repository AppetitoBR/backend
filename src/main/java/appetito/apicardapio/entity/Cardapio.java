package appetito.apicardapio.entity;

import appetito.apicardapio.dto.cadastro.CardapioCadastro;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Representa o cardápio de um estabelecimento, contendo informações como nome, seção, descrição,
 * período de vigência e produtos associados.
 *
 * <p>Um cardápio está vinculado a um {@link Estabelecimento} e pode conter múltiplos {@link Produto}s.</p>
 */
@Data
@Entity
@Getter
@Setter
@Table(name = "Cardapio")
@AllArgsConstructor
@NoArgsConstructor
public class Cardapio {

    /**
     * Identificador único do cardápio.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cardapio_id", nullable = false)
    private Long id;

    /**
     * Estabelecimento ao qual o cardápio pertence.
     */
    @ManyToOne
    @JoinColumn(name = "estabelecimento_id")
    private Estabelecimento estabelecimento;

    /**
     * Nome do cardápio.
     */
    @Column(nullable = false)
    private String nome;

    /**
     * Seção do cardápio, por exemplo "Entradas", "Pratos principais".
     */
    private String secao;

    /**
     * Descrição adicional do cardápio.
     */
    private String descricao;

    /**
     * Data de início da vigência do cardápio.
     */
    @Column(nullable = false)
    private LocalDate vigencia_inicio;

    /**
     * Data de fim da vigência do cardápio.
     */
    @Column(nullable = false)
    private LocalDate vigencia_fim;

    /**
     * Indica se o cardápio está ativo.
     */
    @Column(nullable = false)
    private Boolean ativo = true;

    /**
     * Lista dos produtos associados a este cardápio.
     */
    @OneToMany(mappedBy = "cardapio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Produto> produtos;

    /**
     * Construtor que inicializa o cardápio a partir de um objeto {@link CardapioCadastro}.
     * Define as datas de vigência como a data atual e marca o cardápio como ativo.
     *
     * @param cardapioCadastro Dados para cadastro do cardápio.
     */
    public Cardapio(CardapioCadastro cardapioCadastro) {
        this.nome = cardapioCadastro.nome();
        this.secao = cardapioCadastro.secao();
        this.descricao = cardapioCadastro.descricao();
        this.vigencia_inicio = LocalDate.now();
        this.vigencia_fim = LocalDate.now();
        this.ativo = true;
    }

}
