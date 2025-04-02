package appetito.apicardapio.entity;

import appetito.apicardapio.enums.PapelUsuario;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "usuario_estabelecimento")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioEstabelecimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long usuario_estabelecimento_id;

    @ManyToOne
    @JoinColumn(name = "usuario_dashboard_id")
    private UsuarioDashboard usuario;

    @ManyToOne
    @JoinColumn(name = "estabelecimento_id", nullable = false)
    private Estabelecimento estabelecimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "papel", nullable = false)
    private PapelUsuario papel;

    public UsuarioEstabelecimento(UsuarioDashboard usuario, Estabelecimento estabelecimento, PapelUsuario papel) {
        this.usuario = usuario;
        this.estabelecimento = estabelecimento;
        this.papel = papel;
    }
}
