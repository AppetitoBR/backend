package appetito.controller;

import appetito.apicardapio.controller.EstabelecimentoController;
import appetito.apicardapio.dto.cadastro.EstabelecimentoCadastro;
import appetito.apicardapio.dto.detalhamento.EstabelecimentoDetalhamento;
import appetito.apicardapio.dto.GetAll.EstabelecimentoDados;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.EstabelecimentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstabelecimentoControllerTest {

    @Mock
    private EstabelecimentoRepository estabelecimentoRepository;

    @InjectMocks
    private EstabelecimentoController estabelecimentoController;

    private Estabelecimento estabelecimento;
    private EstabelecimentoCadastro cadastroDto;
    private UriComponentsBuilder uriBuilder;

    @BeforeEach
    void setUp() {
        cadastroDto = new EstabelecimentoCadastro(
                "Razão Social Teste",
                "Nome Fantasia Teste",
                "12345678901234",
                "Endereço Teste",
                "Tipo Teste",
                "Segmento Teste",
                1L
        );

        estabelecimento = new Estabelecimento();
        estabelecimento.setId(1L);
        estabelecimento.setRazao_social("Razão Social Teste");
        estabelecimento.setNome_fantasia("Nome Fantasia Teste");
        estabelecimento.setCnpj("12345678901234");
        estabelecimento.setEndereco("Endereço Teste");
        estabelecimento.setTipo("Tipo Teste");
        estabelecimento.setSegmento("Segmento Teste");
        estabelecimento.setUsuario_cadastro(1L);
        estabelecimento.setData_cadastro(LocalDateTime.now());
        estabelecimento.setData_alteracao_cadastro(LocalDateTime.now());
        estabelecimento.setUsuario_alteracao(1L);

        uriBuilder = UriComponentsBuilder.newInstance();
    }

    @Test
    void cadastrarEstabelecimento_DeveRetornarCreated() {
        // Configura o mock para retornar o objeto com ID
        when(estabelecimentoRepository.save(any(Estabelecimento.class)))
                .thenAnswer(invocation -> {
                    Estabelecimento est = invocation.getArgument(0);
                    est.setId(1L); // Simula a definição do ID pelo banco
                    return est;
                });

        ResponseEntity<EstabelecimentoDetalhamento> response =
                estabelecimentoController.cadastrarEstabelecimento(cadastroDto, uriBuilder);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().estabelecimento_id()); // Agora não será mais null
        assertTrue(response.getHeaders().getLocation().toString().contains("/estabelecimento/1"));
    }

    @Test
    void obterEstabelecimento_QuandoExistir_DeveRetornarOk() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));

        ResponseEntity<EstabelecimentoDetalhamento> response = estabelecimentoController.obterEstabelecimento(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(estabelecimento.getId(), response.getBody().estabelecimento_id());
    }

    @Test
    void obterEstabelecimento_QuandoNaoExistir_DeveLancarExcecao() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            estabelecimentoController.obterEstabelecimento(1L);
        });
    }

    @Test
    void listarEstabelecimentos_QuandoExistirem_DeveRetornarLista() {
        Estabelecimento estabelecimento2 = new Estabelecimento();
        estabelecimento2.setId(2L);
        estabelecimento2.setNome_fantasia("Outro Nome");
        estabelecimento2.setCnpj("98765432109876");
        estabelecimento2.setObservacao("Outra observação");

        when(estabelecimentoRepository.findAll()).thenReturn(Arrays.asList(estabelecimento, estabelecimento2));

        ResponseEntity<List<EstabelecimentoDados>> response = estabelecimentoController.listarEstabelecimentos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void listarEstabelecimentos_QuandoNaoExistirem_DeveLancarExcecao() {
        when(estabelecimentoRepository.findAll()).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> {
            estabelecimentoController.listarEstabelecimentos();
        });
    }

    @Test
    void deletarEstabelecimento_QuandoExistir_DeveRetornarNoContent() {
        when(estabelecimentoRepository.existsById(1L)).thenReturn(true);

        ResponseEntity<Void> response = estabelecimentoController.deletarEstabelecimento(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(estabelecimentoRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletarEstabelecimento_QuandoNaoExistir_DeveRetornarNoContent() {
        when(estabelecimentoRepository.existsById(1L)).thenReturn(false);

        ResponseEntity<Void> response = estabelecimentoController.deletarEstabelecimento(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(estabelecimentoRepository, never()).deleteById(1L);
    }
}