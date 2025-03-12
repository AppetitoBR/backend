package appetito.apicardapio.entity;

import appetito.apicardapio.enums.PerfilUsuario;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

@Entity
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long usuario_id;

    private String nome_completo;
    private String email;
    private String senha;
    private PerfilUsuario perfil;
    private String cpf;  // Adicionando o campo CPF

    // Construtor padrão (necessário para JPA)
    public Usuario() {
    }

    // Construtor com parâmetros, incluindo o CPF
    public Usuario(String nome_completo, String cpf, String email, String senha, PerfilUsuario perfil) {
        this.nome_completo = nome_completo;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        this.perfil = perfil;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_" + perfil.name()));
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

}
