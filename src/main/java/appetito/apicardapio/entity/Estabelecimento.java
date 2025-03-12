package appetito.apicardapio.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Estabelecimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "estabelecimento_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "Razão social é obrigatória")
    private String razao_social;

    @Column(name = "nome_fantasia")
    private String nomeFantasia;

    @Column(nullable = false, unique = true)
    @NotNull(message = "CNPJ é obrigatório")
    private String cnpj;

    @Column(nullable = false)
    @NotNull(message = "Tipo de estabelecimento é obrigatório")
    private String tipo;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(nullable = false)
    private LocalDateTime data_cadastro = LocalDateTime.now();

    private LocalDateTime data_atualizacao;

    public Estabelecimento() {}

    public Estabelecimento(String razao_social, String nomeFantasia, String cnpj, String tipo) {
        this.razao_social = razao_social;
        this.nomeFantasia = nomeFantasia;
        this.cnpj = cnpj;
        this.tipo = tipo;
    }

    @PreUpdate
    public void preUpdate() {
        this.data_atualizacao = LocalDateTime.now();
    }
}
