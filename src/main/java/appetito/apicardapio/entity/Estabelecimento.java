package appetito.apicardapio.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Estabelecimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long estabelecimento_id;

    @Column(nullable = false)
    private String razao_social;

    @Column(nullable = false)
    private String nome_fantasia;

    @Column(nullable = false, unique = true)
    private String cnpj;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(nullable = false)
    private LocalDateTime data_cadastro = LocalDateTime.now();

    private LocalDateTime data_atualizacao;

    public Estabelecimento() {}

    public Estabelecimento(String razao_social, String nome_fantasia, String cnpj, String tipo) {
        this.razao_social = razao_social;
        this.nome_fantasia = nome_fantasia;
        this.cnpj = cnpj;
        this.tipo = tipo;
    }
}