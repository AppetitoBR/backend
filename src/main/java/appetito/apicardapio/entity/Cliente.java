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


/**
 * Representa um cliente no sistema, implementando a interface UserDetails para integração
 * com o Spring Security.
 * Contém informações pessoais, de contato, status e autenticação do cliente.
 */
@Entity
@Table(name = "cliente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente implements UserDetails {

    /**
     * Identificador único do cliente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cliente_id")
    private Long id;

    /**
     * Nome completo do cliente. Campo obrigatório.
     */
    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;

    /**
     * Apelido do cliente, se houver.
     */
    @Column(name = "apelido")
    private String apelido;

    /**
     * CPF único do cliente.
     */
    @Column(name = "cpf", unique = true)
    private String cpf;

    /**
     * E-mail do cliente, usado como nome de usuário no sistema. Campo obrigatório e único.
     */
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * Senha do cliente para autenticação. Campo obrigatório.
     */
    @Column(name = "senha", nullable = false)
    private String senha;

    /**
     * Data de nascimento do cliente.
     */
    @Column(name = "data_nascimento")
    @Temporal(TemporalType.DATE)
    private LocalDate dataNascimento;

    /**
     * Idioma padrão preferido do cliente.
     */
    @Column(name = "idioma_padrao")
    private String idiomaPadrao;

    /**
     * Nacionalidade do cliente.
     */
    @Column(name = "nacionalidade")
    private String nacionalidade;

    /**
     * Imagem de perfil do cliente armazenada como array de bytes.
     */
    @Lob
    @Column(name = "imagem_perfil")
    private byte[] imagemPerfil;

    /**
     * Situação atual do cliente (ex: ATIVO, INATIVO, BLOQUEADO). Valor padrão é ATIVO.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false)
    private Situacao situacao = Situacao.ATIVO;

    /**
     * Contatos adicionais do cliente.
     */
    @Column(name = "contatos")
    private String contatos;

    /**
     * Endereço do cliente.
     */
    @Column(name = "endereco")
    private String endereco;

    /**
     * Redes sociais vinculadas ao cliente.
     */
    @Column(name = "redes_sociais")
    private String redesSociais;

    /**
     * Data em que o cliente foi cadastrado. Não pode ser atualizada.
     */
    @Column(name = "data_cadastro", updatable = false)
    private LocalDate dataCadastro;

    /**
     * Data da última atualização dos dados do cliente.
     */
    @Column(name = "data_atualizacao")
    private LocalDate dataAtualizacao;


    /**
     * Retorna as autoridades/grupos de permissões do cliente para o Spring Security.
     * @return Lista com a autoridade ROLE_CLIENTE.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"));
    }

    /**
     * Retorna a senha para autenticação.
     * @return Senha do cliente.
     */
    @Override
    public String getPassword() {
        return senha;
    }

    /**
     * Retorna o nome de usuário para autenticação, que é o e-mail do cliente.
     * @return E-mail do cliente.
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Construtor para criar um Cliente a partir de um DTO ClienteCadastro.
     * @param clienteCadastro Dados do cliente para cadastro.
     */
    public Cliente(ClienteCadastro clienteCadastro){
        this.nomeCompleto = clienteCadastro.nomeCompleto();
        this.email = clienteCadastro.email();
        this.senha = clienteCadastro.senha();
    }
}