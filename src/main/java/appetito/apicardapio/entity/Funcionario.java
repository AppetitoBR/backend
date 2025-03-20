package appetito.apicardapio.entity;

import appetito.apicardapio.dto.cadastro.FuncionarioCadastro;
import appetito.apicardapio.enums.EstadoCivil;
import appetito.apicardapio.enums.Sexo;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Funcionario")
public class Funcionario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "funcionario_id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name = "estabelecimento_id", nullable = false, insertable = false, updatable = false)
    private Long estabelecimento;
    @Column(nullable = false, length = 50)
    private String nome;
    @Column(nullable = false, unique = true)
    private String cpf;

    // estou vendo sobre isso aqui ainda(idade nao pode ser salva no banco de dados, pois nao é um dado concreto, ele sofre alteração)
    private int idade;


    @Column(name = "data_nascimento", nullable = false, insertable = false, updatable = false)
    private LocalDate dataNascimento;
    @Column(nullable = false)
    private Sexo sexo;
    @Column(nullable = false, length = 50)
    private String departamento;
    @Column(nullable = false)
    private String cargo;
    @Column(name= "estado_civil",nullable = false)
    private EstadoCivil estadoCivil;
    @Column(nullable = false)
    private String nome_pai;
    @Column(nullable = false)
    private String nome_mae;
    @Column(nullable = false, length = 50)
    private BigDecimal salario;
    @Column(nullable = false)
    private boolean ativo;

    public Funcionario(FuncionarioCadastro funcionarioCadastro){
        this.nome = funcionarioCadastro.nome();
        this.cpf = funcionarioCadastro.cpf();
        this.dataNascimento = funcionarioCadastro.dataNascimento();
        this.sexo = funcionarioCadastro.sexo();
        this.departamento = funcionarioCadastro.departamento();
        this.cargo = funcionarioCadastro.cargo();
        this.estadoCivil = funcionarioCadastro.estadoCivil();
        this.nome_pai = funcionarioCadastro.nome_pai();
        this.nome_mae = funcionarioCadastro.nome_mae();
        this.salario = funcionarioCadastro.salario();
    }
}
