package appetito.apicardapio.entity;

import appetito.apicardapio.dto.cadastro.UsuarioCadastro;
import appetito.apicardapio.enums.PerfilUsuario;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Data
@Table(name = "Usuario")
@Entity(name = "Usuarios")
@AllArgsConstructor
@NoArgsConstructor

public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long usuario_id;

    private String nome_completo;
    private String apelido;
    private String cpf;
    private String email;
    private String senha;
    private PerfilUsuario perfil;
    private LocalDate data_nascimento;
    private Integer idioma_padrao;
    private String nacionalidade;
    private byte[] imagem_perfil;
    private String situacao;
    private String contatos;
    private String endereco;
    private String redes_sociais;
    private LocalDate data_cadastro;
    private LocalDate data_atualizacao;

    public Usuario(UsuarioCadastro dadosUsuario){
        this.nome_completo = dadosUsuario.nome_completo();
        this.cpf = dadosUsuario.cpf();
        this.email = dadosUsuario.email();
        this.senha = dadosUsuario.senha();
        this.data_nascimento = dadosUsuario.data_nascimento();
        this.idioma_padrao = dadosUsuario.idioma_padrao();
        this.endereco = dadosUsuario.endereco();
        this.redes_sociais = dadosUsuario.redes_sociais();
        this.contatos = dadosUsuario.contatos();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_"+ perfil.name()));
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