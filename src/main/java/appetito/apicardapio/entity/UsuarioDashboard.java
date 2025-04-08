package appetito.apicardapio.entity;

import appetito.apicardapio.dto.cadastro.UsuarioDashboardCadastro;
import appetito.apicardapio.enums.Situacao;
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

@Data
@Table(name = "usuario_dashboard")
@Entity(name = "UsuarioDashboard")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDashboard implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long usuario_dashboard_id;

    private String nome_completo;
 //   private String cpf;
    private String email;
    private String senha;
   // private LocalDate data_nascimento;
  //  private String idioma_padrao;
   // private String nacionalidade;
   // private byte[] imagem_perfil;
   @Enumerated(EnumType.STRING)
   @Column(name = "situacao")
   private Situacao situacao;
    //private String situacao;
    //private String contatos;
   // private String endereco;
  //  private String redes_sociais;
    private LocalDate data_cadastro;
    private LocalDate data_atualizacao;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<UsuarioEstabelecimento> estabelecimentos = new ArrayList<>();

    public UsuarioDashboard(UsuarioDashboardCadastro dadosUsuario) {
        this.nome_completo = dadosUsuario.nome_completo();
      //  this.cpf = dadosUsuario.cpf();
        this.email = dadosUsuario.email();
        this.senha = dadosUsuario.senha();
      //  this.data_nascimento = dadosUsuario.data_nascimento();
       // this.idioma_padrao = dadosUsuario.idioma_padrao();
       // this.endereco = dadosUsuario.endereco();
      //  this.redes_sociais = dadosUsuario.redes_sociais();
      //  this.contatos = dadosUsuario.contatos();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (estabelecimentos == null || estabelecimentos.isEmpty()) {
            return List.of(new SimpleGrantedAuthority("ROLE_USUARIODASHBOARD"));
        }
        return estabelecimentos.stream()
                .map(est -> new SimpleGrantedAuthority("ROLE_" + est.getPapel().name()))
                .collect(Collectors.toList());
    }


    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
