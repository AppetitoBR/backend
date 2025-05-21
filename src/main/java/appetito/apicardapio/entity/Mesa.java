package appetito.apicardapio.entity;

import appetito.apicardapio.dto.cadastro.MesaCadastro;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Representa uma mesa dentro de um estabelecimento.
 * Contém informações sobre a capacidade, status e QR Code para identificação.
 */
@Data
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mesa")
public class Mesa {

    /**
     * Identificador único da mesa.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mesa_id")
    private Long id;

    /**
     * Estabelecimento ao qual a mesa pertence.
     * Será definido via POST; lógica para carregar o estabelecimento deve ser implementada.
     */
    @ManyToOne
    @JoinColumn(name = "estabelecimento_id", nullable = false)
    private Estabelecimento estabelecimento;

    /**
     * Nome da mesa. Campo obrigatório.
     */
    @Column(nullable = false)
    private String nome;

    /**
     * Capacidade máxima da mesa (número de pessoas). Campo obrigatório.
     */
    @Column(nullable = false)
    private Integer capacidade;

    /**
     * Status atual da mesa (ex: disponível, ocupada, reservada).
     * Valor padrão definido no momento da criação e pode ser atualizado posteriormente.
     */
    @Column(nullable = false)
    private String status;

    /**
     * QR Code da mesa armazenado como array de bytes.
     * Utilizado para identificação rápida via leitura.
     */
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] qrcode;

    /**
     * Construtor que inicializa a mesa com dados fornecidos no DTO MesaCadastro.
     * @param mesaCadastro Dados para inicializar a mesa.
     */
    public Mesa(MesaCadastro mesaCadastro) {
        this.nome = mesaCadastro.nome();
        this.capacidade = mesaCadastro.capacidade();
    }
}