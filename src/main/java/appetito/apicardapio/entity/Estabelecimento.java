package appetito.apicardapio.entity;

import appetito.apicardapio.dto.cadastro.EstabelecimentoCadastro;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa um estabelecimento comercial registrado no sistema.
 * Contém informações legais, contatos, tipo, segmento e vinculação com usuários.
 */
@Table(name = "estabelecimento")
@Data
@Entity(name = "Estabelecimentos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Estabelecimento {

    /**
     * Identificador único do estabelecimento.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "estabelecimento_id")
    private Long estabelecimentoId;

    /**
     * Razão social do estabelecimento. Campo obrigatório.
     */
    @Column(nullable = false)
    private String razao_social;

    /**
     * Nome fantasia do estabelecimento. Campo obrigatório.
     */
    @Column(name= "nome_fantasia",nullable = false)
    private String nomeFantasia;

    /**
     * CNPJ único do estabelecimento. Campo obrigatório e único.
     */
    @Column(nullable = false, unique = true)
    private String cnpj;

    /**
     * Contatos do estabelecimento, armazenados em formato texto longo.
     */
    @Lob
    private String contatos;

    /**
     * Endereço do estabelecimento, armazenado em formato texto longo.
     */
    @Lob
    private String endereco;

    /**
     * Tipo do estabelecimento (ex: restaurante, bar, etc.). Campo obrigatório.
     */
    @Column(nullable = false)
    private String tipo;

    /**
     * Data e hora em que o cadastro do estabelecimento foi criado.
     */
    @Column(nullable = false)
    private LocalDateTime data_cadastro = LocalDateTime.now();

    /**
     * Segmento do estabelecimento (ex: comida, bebida, etc.). Campo obrigatório.
     */
    @Column(nullable = false)
    private String segmento;

    /**
     * Usuário que realizou o cadastro do estabelecimento.
     */
    @ManyToOne
    @JoinColumn(name = "usuario_cadastro_id", nullable = false)
    private UsuarioDashboard usuarioCadastro;

    /**
     * Data e hora da última alteração no cadastro do estabelecimento.
     */
    @Column(nullable = false)
    private LocalDateTime data_alteracao_cadastro = LocalDateTime.now();

    /**
     * Lista de vínculos entre usuários e este estabelecimento.
     * As alterações em vínculos são propagadas para o banco (cascade) e remoções são orfãs.
     */
    @OneToMany(mappedBy = "estabelecimento", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<UsuarioEstabelecimento> vinculos = new ArrayList<>();

    /**
     * Observações adicionais sobre o estabelecimento, armazenadas em texto longo.
     */
    @Lob
    private String observacao;

    /**
     * Logomarca do estabelecimento armazenada em formato texto longo (base64, url ou similar).
     */
    @Lob
    private byte[] logomarca;

    /**
     * URL do cardápio digital vinculado ao estabelecimento.
     */
    private String url_cardapio_digital;

    /**
     * Subdomínio do estabelecimento na plataforma Appetito.
     */
    private String subdominio_appetito;

    private Time abertura;
    private Time fechamento;

    @Transient
    private boolean isOpen;

    public void atualizarStatus() {
        if (abertura == null || fechamento == null) {
            isOpen = false; // não tem horário definido
            return;
        }

        LocalTime agora = LocalTime.now();
        LocalTime abre = abertura.toLocalTime();
        LocalTime fecha = fechamento.toLocalTime();

        if (abre.isBefore(fecha)) {
            isOpen = !agora.isBefore(abre) && agora.isBefore(fecha);
        } else {
            isOpen = !agora.isBefore(abre) || agora.isBefore(fecha);
        }
    }
    public boolean isOpen() {
        atualizarStatus();
        return isOpen;
    }
    private Float nota;

    /**
     * Construtor que cria um estabelecimento a partir de dados fornecidos no DTO EstabelecimentoCadastro.
     * @param dadosEstabelecimento Dados para inicializar o estabelecimento.
     */
    public Estabelecimento(EstabelecimentoCadastro dadosEstabelecimento) {
        this.razao_social = dadosEstabelecimento.razao_social();
        this.nomeFantasia = dadosEstabelecimento.nome_fantasia();
        this.cnpj = dadosEstabelecimento.cnpj();
        this.tipo = dadosEstabelecimento.tipo();
        this.segmento = dadosEstabelecimento.segmento();
    }
}
