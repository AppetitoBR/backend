package appetito.apicardapio.entity;

import appetito.apicardapio.dto.cadastro.UsuarioDashboardCadastro;
import appetito.apicardapio.enums.Situacao;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Entidade que representa um usuário do sistema de dashboard.
 *
 * Essa classe implementa a interface UserDetails do Spring Security,
 * permitindo que seja utilizada no mecanismo de autenticação e autorização.
 *
 * Cada usuário pode ter vínculos com múltiplos estabelecimentos,
 * cada vínculo com um papel específico (ADMIN, GERENTE, etc).
 *
 * Os papéis do usuário são convertidos em authorities para o Spring Security
 * a partir dos vínculos estabelecidos.
 */
@Data
@Table(name = "usuario_dashboard")
@Entity(name = "UsuarioDashboard")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDashboard implements UserDetails {

    /**
     * Identificador único do usuário.
     * Gerado automaticamente pelo banco.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long usuario_dashboard_id;

    /**
     * Nome completo do usuário.
     */
    private String nome_completo;

    /**
     * Email do usuário, usado como username para autenticação.
     */
    private String email;

    /**
     * Senha do usuário, armazenada de forma segura (hash).
     */
    private String senha;

    private String telefone;

    /**
     * Imagem do perfil armazenada como array de bytes (blob).
     */
    private byte[] imagem_perfil;

    /**
     * Situação do usuário (ex: ATIVO, INATIVO).
     * Usado para controle de acesso e status.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "situacao")
    private Situacao situacao;

    /**
     * Data de cadastro do usuário no sistema.
     */
    private LocalDate data_cadastro;

    /**
     * Data da última atualização dos dados do usuário.
     */
    private LocalDate data_atualizacao;

    /**
     * Vínculos do usuário com estabelecimentos,
     * representando a associação do usuário com um estabelecimento e o papel dele nesse contexto.
     */
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<UsuarioEstabelecimento> vinculos;

    /**
     * Lista de estabelecimentos vinculados ao usuário,
     * carregada ansiosamente (fetch EAGER) para uso imediato em decisões de segurança.
     */
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<UsuarioEstabelecimento> estabelecimentos = new ArrayList<>();

    /**
     * Construtor que inicializa um usuário a partir de um DTO.
     *
     * @param dadosUsuario DTO contendo os dados para criação do usuário.
     */
    public UsuarioDashboard(UsuarioDashboardCadastro dadosUsuario) {
        this.nome_completo = dadosUsuario.nome_completo();
        this.email = dadosUsuario.email();
        this.senha = dadosUsuario.senha();
    }

    /**
     * Retorna as authorities (perfis) do usuário para o Spring Security.
     * Cada vínculo com estabelecimento gera uma ROLE específica no formato ROLE_<PAPEL>.
     * Caso não haja vínculo, retorna a ROLE_USUARIODASHBOARD como padrão.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (estabelecimentos == null || estabelecimentos.isEmpty()) {
            return List.of(new SimpleGrantedAuthority("ROLE_USUARIODASHBOARD"));
        }
        return estabelecimentos.stream()
                .map(est -> new SimpleGrantedAuthority("ROLE_" + est.getPapel().name()))
                .collect(Collectors.toList());
    }

    /**
     * Retorna a senha do usuário.
     */
    @Override
    public String getPassword() {
        return senha;
    }

    /**
     * Retorna o nome de usuário (email) usado para login.
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Indica se a conta não está expirada (sempre true no momento).
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indica se a conta não está bloqueada (sempre true no momento).
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indica se as credenciais não estão expiradas (sempre true no momento).
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indica se o usuário está habilitado (sempre true no momento).
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
