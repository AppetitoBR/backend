package appetito.apicardapio.entity;

import appetito.apicardapio.enums.PapelUsuario;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estabelecimento_id")
    private Estabelecimento estabelecimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_dashboard_id")
    private UsuarioDashboard usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "papel", nullable = false)
    private PapelUsuario papel;

    public UsuarioEstabelecimento(List<Estabelecimento> estabelecimentoCriado, UsuarioDashboard usuarioDashboard, PapelUsuario papelUsuario) {
        this.papel = papelUsuario;
        this.usuario = usuarioDashboard;
        this.estabelecimento = estabelecimentoCriado.getFirst();
    }

}
