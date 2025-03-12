package appetito.apicardapio.entity;


import appetito.apicardapio.enums.PerfilUsuario;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long usuario_id;

    @Column(nullable = false)
    private String nome_completo;

    private String apelido;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String senha;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerfilUsuario perfil;

    private LocalDate data_nascimento;

    private Integer idioma_padrao = 0;

    private String nacionalidade;

    private String caminho_imagem_perfil;

    private String situacao = "ativo";

    @Lob
    private String contatos;

    @Lob
    private String endereco;

    @Lob
    private String redes_sociais;

    @Column(nullable = false, updatable = false)
    private LocalDateTime data_cadastro = LocalDateTime.now();

    private LocalDateTime data_atualizacao;

    public Usuario() {}

    public Usuario(String nome_completo, String cpf, String email, String senha, PerfilUsuario perfil) {
        this.nome_completo = nome_completo;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        this.perfil = perfil;
    }
}