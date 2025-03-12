package appetito.apicardapio.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mesa_id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private Integer capacidade;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, unique = true)
    private String qrCode;

    @ManyToOne
    @JoinColumn(name = "estabelecimento_id", nullable = false)
    private Estabelecimento estabelecimento;

    public Mesa() {}

    public Mesa(String nome, Integer capacidade, String status, String qrCode, Estabelecimento estabelecimento ) {
        this.nome = nome;
        this.capacidade = capacidade;
        this.status = status;
        this.qrCode = qrCode;
        this.estabelecimento = estabelecimento;
    }
}