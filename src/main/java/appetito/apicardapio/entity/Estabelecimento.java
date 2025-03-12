package appetito.apicardapio.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Estabelecimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String razaoSocial;

    @Column(nullable = false)
    private String nomeFantasia;

    @Column(nullable = false, unique = true)
    private String cnpj;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private Boolean ativo = true;

    public Estabelecimento() {}

    public Estabelecimento(String razaoSocial, String nomeFantasia, String cnpj, String tipo) {
        this.razaoSocial = razaoSocial;
        this.nomeFantasia = nomeFantasia;
        this.cnpj = cnpj;
        this.tipo = tipo;
    }
}
