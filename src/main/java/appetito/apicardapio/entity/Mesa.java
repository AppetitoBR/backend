package appetito.apicardapio.entity;

import appetito.apicardapio.dto.cadastro.MesaCadastro;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mesa")
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mesa_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "estabelecimento_id", nullable = false)
    private Estabelecimento estabelecimento; // estabelecimento vai ser pelo post, tenho que ver uma logica de trazer ele

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private Integer capacidade;

    @Column(nullable = false)
    private String status; // status vai ser o default e depois atualizado caso haja tal

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] qrcode;

    public Mesa(MesaCadastro mesaCadastro) {
        this.nome = mesaCadastro.nome();
        this.capacidade = mesaCadastro.capacidade();
    }
}