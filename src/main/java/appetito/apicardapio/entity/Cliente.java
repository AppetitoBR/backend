package appetito.apicardapio.entity;

import appetito.apicardapio.dto.cadastro.ClienteCadastro;
import appetito.apicardapio.enums.Situacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "cliente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cliente_id")
    private Long id;

    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;

    @Column(name = "apelido")
    private String apelido;

    @Column(name = "cpf", unique = true)
    private String cpf;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "senha", nullable = false)
    private String senha;

    @Column(name = "data_nascimento")
    @Temporal(TemporalType.DATE)
    private LocalDate dataNascimento;

    @Column(name = "idioma_padrao")
    private String idiomaPadrao;

    @Column(name = "nacionalidade")
    private String nacionalidade;

    @Lob
    @Column(name = "imagem_perfil")
    private byte[] imagemPerfil;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false)
    private Situacao situacao = Situacao.ATIVO;

    @Column(name = "contatos")
    private String contatos;

    @Column(name = "endereco")
    private String endereco;

    @Column(name = "redes_sociais")
    private String redesSociais;

    @Column(name = "data_cadastro", updatable = false)
    private LocalDate dataCadastro;

    @Column(name = "data_atualizacao")
    private LocalDate dataAtualizacao;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public Cliente(ClienteCadastro clienteCadastro){
        this.nomeCompleto = clienteCadastro.nomeCompleto();
        this.email = clienteCadastro.email();
        this.senha = clienteCadastro.senha();
    }
}