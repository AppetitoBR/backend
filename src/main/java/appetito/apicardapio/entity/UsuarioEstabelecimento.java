package appetito.apicardapio.entity;

import appetito.apicardapio.enums.PapelUsuario;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonBackReference
    private Estabelecimento estabelecimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_dashboard_id")
    @JsonIgnore
    private UsuarioDashboard usuario;



    @Enumerated(EnumType.STRING)
    @Column(name = "papel", nullable = false)
    private PapelUsuario papel;

    public UsuarioEstabelecimento(Estabelecimento estabelecimento, UsuarioDashboard usuarioDashboard, PapelUsuario papelUsuario) {
        this.papel = papelUsuario;
        this.usuario = usuarioDashboard;
        this.estabelecimento = estabelecimento;
    }

}
