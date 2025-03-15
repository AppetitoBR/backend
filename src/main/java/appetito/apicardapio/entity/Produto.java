package appetito.apicardapio.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long produto_id;

    @ManyToOne
    @JoinColumn(name = "cardapio_id", nullable = false)
    private Cardapio cardapio;

    private String nome_curto;
    private String nome_longo;
    private String categoria;
    private String tamanho; // Ex: Individual, 2 pessoas, 4 pessoas
    private BigDecimal preco_custo;
    private BigDecimal preco_venda;
    private Integer estoque;
    private Integer estoque_minimo;
    private Boolean ativo;
    private String unidade_medida;
    private String imagens;

    public Produto(){

    }
    public Produto(
                   String nome_curto,
                   String nome_longo,
                   String categoria,
                   String tamanho,
                   BigDecimal preco_custo,
                   BigDecimal preco_venda,
                   Integer estoque,
                   Integer estoque_minimo,
                   Boolean ativo,
                   String unidade_medida,
                   String imagens){

        this.nome_curto = nome_curto;
        this.nome_longo = nome_longo;
        this.categoria = categoria;
        this.tamanho = tamanho;
        this.preco_custo = preco_custo;
        this.preco_venda = preco_venda;
        this.estoque = estoque;
        this.estoque_minimo = estoque_minimo;
        this.ativo = ativo;
        this.unidade_medida = unidade_medida;
        this.imagens = imagens;
    }
}