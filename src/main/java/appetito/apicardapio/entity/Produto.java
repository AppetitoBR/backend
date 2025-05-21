package appetito.apicardapio.entity;

import appetito.apicardapio.dto.cadastro.ProdutoCadastro;
import com.fasterxml.jackson.annotation.JsonBackReference;
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

    /**
     * Identificador único do produto (chave primária).
     * Gerado automaticamente pelo banco de dados.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long produto_id;

    /**
     * Cardápio ao qual o produto pertence.
     * Relação muitos-para-um com a entidade Cardapio.
     * Mapeado com @JsonBackReference para evitar referência cíclica na serialização JSON.
     */
    @ManyToOne
    @JoinColumn(name = "cardapio_id", nullable = false)
    @JsonBackReference
    private Cardapio cardapio;

    /**
     * Nome curto do produto.
     * Usado para exibição rápida ou em listas compactas.
     */
    private String nome_curto;

    /**
     * Nome longo ou completo do produto.
     * Usado para descrições detalhadas.
     */
    private String nome_longo;

    /**
     * Categoria do produto, por exemplo "Bebida", "Sobremesa", "Prato Principal".
     * Pode ser usada para filtros e agrupamentos.
     */
    private String categoria;

    /**
     * Tamanho ou volume do produto, ex: "Pequeno", "Médio", "Grande".
     * Útil para variações de produto.
     */
    private String tamanho;

    /**
     * Preço de custo do produto.
     * Valor usado para controle financeiro e margem.
     */
    private BigDecimal preco_custo;

    /**
     * Preço de venda do produto.
     * Valor cobrado do cliente.
     */
    private BigDecimal preco_venda;

    /**
     * Quantidade atual em estoque do produto.
     * Deve ser atualizada conforme vendas e reposições.
     */
    private Integer estoque;

    /**
     * Estoque mínimo recomendado para alertas de reposição.
     */
    private Integer estoque_minimo;

    /**
     * Indica se o produto está ativo (disponível para venda).
     */
    private Boolean ativo;

    /**
     * Unidade de medida do produto, ex: "un", "ml", "kg".
     */
    private String unidade_medida;

    /**
     * Imagens do produto armazenadas em formato binário (blob).
     * Usado para exibir foto do produto no sistema.
     */
    @Lob
    @Column(name = "imagens")
    private byte[] imagens;

    /**
     * Construtor que inicializa um Produto a partir de um DTO ProdutoCadastro.
     *
     * @param produtoCadastro DTO contendo dados para criação do produto.
     */
    public Produto(ProdutoCadastro produtoCadastro) {
        this.nome_curto = produtoCadastro.nome_curto();
        this.nome_longo = produtoCadastro.nome_longo();
        this.categoria = produtoCadastro.categoria();
        this.tamanho = produtoCadastro.tamanho();
        this.preco_custo = produtoCadastro.preco_custo();
        this.preco_venda = produtoCadastro.preco_venda();
        this.ativo = produtoCadastro.ativo();
        this.estoque = produtoCadastro.estoque();
        this.estoque_minimo = produtoCadastro.estoque_minimo();
    }
    /**
     * Verifica se o produto está abaixo do estoque mínimo recomendado.
     * Pode ser usado para alertas de reposição.
     *
     * @return true se estoque atual for menor que estoque mínimo.
     */
    public boolean isEstoqueBaixo() {
        if (estoque == null || estoque_minimo == null) return false;
        return estoque < estoque_minimo;
    }

    /**
     * Calcula a margem de lucro percentual com base no custo e preço de venda.
     *
     * @return margem de lucro em porcentagem (ex: 25.0 para 25%)
     */
    public double calcularMargemLucro() {
        if (preco_custo == null || preco_custo.compareTo(BigDecimal.ZERO) == 0 || preco_venda == null) {
            return 0.0;
        }
        BigDecimal margem = preco_venda.subtract(preco_custo)
                .divide(preco_custo, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        return margem.doubleValue();
    }
}