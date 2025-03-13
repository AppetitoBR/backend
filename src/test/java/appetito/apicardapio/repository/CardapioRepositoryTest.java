package appetito.apicardapio.repository;

import appetito.apicardapio.entity.Cardapio;
import appetito.apicardapio.entity.Colaborador;
import appetito.apicardapio.entity.Usuario;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.enums.PerfilUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Usa o banco de dados normal
public class CardapioRepositoryTest {

    @Autowired
    private CardapioRepository cardapioRepository;

    @Autowired
    private ColaboradorRepository colaboradorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;

    private Colaborador colaborador;
    private Estabelecimento estabelecimento;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        // Cria um Usuario
        Usuario usuario = new Usuario(
                "João Silva", // nome_completo
                PerfilUsuario.CLIENTE // perfil
        );
        usuarioRepository.save(usuario);

        // Cria um Estabelecimento
        estabelecimento = new Estabelecimento();
        estabelecimento.setRazaoSocial("Restaurante Teste LTDA"); // Define a razão social
        estabelecimento.setNomeFantasia("Restaurante Teste"); // Define o nome fantasia
        estabelecimento.setCnpj("12.345.688/0001-99"); // Define o CNPJ
        estabelecimento.setTipo("Restaurante"); // Define o tipo
        estabelecimento.setSegmento("Alimentação"); // Define o segmento
        estabelecimento.setUsuarioCadastro(usuario); // Associa o usuário de cadastro
        estabelecimentoRepository.save(estabelecimento);

        // Cria um Colaborador associado ao Usuario e Estabelecimento
        colaborador = new Colaborador();
        colaborador.setUsuario(usuario); // Associa o Usuario
        colaborador.setEstabelecimento(estabelecimento); // Associa o Estabelecimento
        colaborador.setCargo("Gerente"); // Campo obrigatório
        colaborador.setDataContratacao(LocalDate.now()); // Campo obrigatório
        colaborador.setCalendarioTrabalho("Segunda a Sexta"); // Campo opcional
        colaborador.setInicioTurno(LocalDateTime.now()); // Campo opcional
        colaborador.setTerminoTurno(LocalDateTime.now().plusHours(8)); // Campo opcional
        colaborador.setNotificacoes("Receber notificações por email"); // Campo opcional
        colaboradorRepository.save(colaborador); // Salva o Colaborador
    }
    @Test
    public void deveSalvarEEncontrarCardapioPorId() {
        // Cria um Cardapio associado ao Colaborador e Estabelecimento
        Cardapio cardapio = new Cardapio();
        cardapio.setNome("Cardápio Teste");
        cardapio.setColaborador(colaborador); // Associa o Colaborador
        cardapio.setEstabelecimento(colaborador.getEstabelecimento()); // Associa o Estabelecimento
        cardapioRepository.save(cardapio); // Salva o Cardapio

        // Busca o Cardapio por ID
        Cardapio encontrado = cardapioRepository.findById(cardapio.getId()).orElseThrow();
        assertNotNull(encontrado);
        assertEquals("Cardápio Teste", encontrado.getNome());
    }

    @Test
    @Transactional // Garante que as alterações sejam revertidas após o teste
    public void deveListarCardapiosPorEstabelecimento() {
        // Cria dois Cardápios associados ao mesmo Estabelecimento
        Cardapio cardapio1 = new Cardapio();
        cardapio1.setNome("Cardápio 1");
        cardapio1.setColaborador(colaborador); // Associa o Colaborador
        cardapio1.setEstabelecimento(estabelecimento);
        cardapioRepository.save(cardapio1); // Salva o Cardápio 1

        Cardapio cardapio2 = new Cardapio();
        cardapio2.setNome("Cardápio 2");
        cardapio2.setColaborador(colaborador); // Associa o Colaborador

        cardapio2.setEstabelecimento(estabelecimento);
        cardapioRepository.save(cardapio2); // Salva o Cardápio 2

        // Busca os Cardápios por Estabelecimento
        List<Cardapio> cardapios = cardapioRepository.findByEstabelecimentoId(colaborador.getEstabelecimento().getId());
        assertEquals(2, cardapios.size()); // Verifica se há 2 cardápios
    }
}