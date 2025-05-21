package appetito.apicardapio.entity;

import appetito.apicardapio.enums.PapelUsuario;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Entidade que representa o vínculo entre um usuário do dashboard e um estabelecimento.
 *
 * Cada usuário pode estar associado a múltiplos estabelecimentos,
 * e em cada associação o usuário terá um papel específico (ADMINISTRADOR, GERENTE, ATENDENTE, etc).
 *
 * Essa entidade é usada para controlar permissões e papéis dos usuários dentro de cada estabelecimento.
 */
@Data
@Entity
@Table(name = "usuario_estabelecimento")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioEstabelecimento {

    /**
     * Identificador único do vínculo entre usuário e estabelecimento.
     * Gerado automaticamente pelo banco.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long usuario_estabelecimento_id;

    /**
     * Estabelecimento ao qual o usuário está vinculado.
     * Utiliza fetch LAZY para evitar carregamento desnecessário.
     *
     * Usado para identificar o estabelecimento no contexto do vínculo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estabelecimento_id")
    @JsonBackReference // Para evitar loop infinito na serialização JSON
    private Estabelecimento estabelecimento;

    /**
     * Usuário do dashboard vinculado ao estabelecimento.
     * Utiliza fetch LAZY para melhor performance.
     *
     * Está ignorado na serialização JSON para proteger dados sensíveis e evitar ciclos.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_dashboard_id")
    @JsonIgnore
    private UsuarioDashboard usuario;

    /**
     * Papel do usuário dentro do estabelecimento,
     * como ADMINISTRADOR, GERENTE, ATENDENTE, etc.
     * Armazenado como string no banco.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "papel", nullable = false)
    private PapelUsuario papel;

    /**
     * Construtor que inicializa o vínculo com estabelecimento, usuário e papel.
     *
     * @param estabelecimento o estabelecimento relacionado
     * @param usuarioDashboard o usuário relacionado
     * @param papelUsuario o papel do usuário nesse estabelecimento
     */
    public UsuarioEstabelecimento(Estabelecimento estabelecimento, UsuarioDashboard usuarioDashboard, PapelUsuario papelUsuario) {
        this.papel = papelUsuario;
        this.usuario = usuarioDashboard;
        this.estabelecimento = estabelecimento;
    }

}
