package appetito.apicardapio.entity;

import appetito.apicardapio.dto.cadastro.ProdutoCadastro;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Table(name = "Produto")
@Entity(name = "Produtos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "produto_id")
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long produto_id;

    @Column(name = "cardapio_id", nullable = false)
    private Long cardapio;

    private String nome_curto;
    private String nome_longo;
    private String categoria;
    private String tamanho;
    private BigDecimal preco_custo;
    private BigDecimal preco_venda;
    private Integer estoque;
    private Integer estoque_minimo;
    private Boolean ativo;
    private String unidade_medida;
    private String imagens;

    public Produto(ProdutoCadastro produtoCadastro) {
        this.cardapio = produtoCadastro.cardapio();
        this.nome_curto = produtoCadastro.nome_curto();
        this.nome_longo = produtoCadastro.nome_longo();
        this.categoria = produtoCadastro.categoria();
        this.tamanho = produtoCadastro.tamanho();
        this.preco_custo = produtoCadastro.preco_custo();
        this.preco_venda = produtoCadastro.preco_venda();
        this.ativo = produtoCadastro.ativo();
        this.estoque = produtoCadastro.estoque();
        this.estoque_minimo = produtoCadastro.estoque_minimo();
        this.imagens = produtoCadastro.imagens();
    }
}