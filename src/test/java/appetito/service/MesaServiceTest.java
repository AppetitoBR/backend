package appetito.service;


import appetito.apicardapio.dto.cadastro.MesaCadastro;
import appetito.apicardapio.dto.detalhamento.MesaDetalhamento;
import appetito.apicardapio.entity.Mesa;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.repository.MesaRepository;
import appetito.apicardapio.repository.EstabelecimentoRepository;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.service.MesaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MesaServiceTest {

    @Mock
    private MesaRepository mesaRepository;

    @Mock
    private EstabelecimentoRepository estabelecimentoRepository;

    @InjectMocks
    private MesaService mesaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void cadastrarMesa_deveRetornarMesaDetalhamento() {
        MesaCadastro dadosMesa = new MesaCadastro("Mesa 1", 4, "Livre" , 1L);
        Estabelecimento estabelecimento = new Estabelecimento();
        estabelecimento.setId(1L);

        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));
        when(mesaRepository.save(any(Mesa.class))).thenAnswer(invocation -> {
            Mesa mesa = invocation.getArgument(0);
            mesa.setMesa_id(1L);
            return mesa;
        });

        MesaDetalhamento result = mesaService.cadastrarMesa(dadosMesa);

        assertNotNull(result);
        assertEquals(1L, result.mesa_id());
        assertEquals("Mesa 1", result.nome());
    }

    @Test
    void atualizarMesa_deveRetornarMesaDetalhamento() {
        MesaCadastro dadosMesa = new MesaCadastro("Mesa 1", 4, "Livre", 1L);
        Mesa mesa = new Mesa("Mesa 1", 4, "Livre", new Estabelecimento());
        mesa.setMesa_id(1L);

        when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesa));
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(new Estabelecimento()));
        when(mesaRepository.save(any(Mesa.class))).thenReturn(mesa);

        MesaDetalhamento result = mesaService.atualizarMesa(1L, dadosMesa);

        assertNotNull(result);
        assertEquals(1L, result.mesa_id());
        assertEquals("Mesa 1", result.nome());
    }

    @Test
    void buscarMesaPorId_deveRetornarMesaDetalhamento() {
        Mesa mesa = new Mesa("Mesa 1", 4, "Livre", new Estabelecimento());
        mesa.setMesa_id(1L);

        when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesa));

        MesaDetalhamento result = mesaService.buscarMesaPorId(1L);

        assertNotNull(result);
        assertEquals(1L, result.mesa_id());
    }

    @Test
    void listarMesas_deveRetornarListaDeMesaDetalhamento() {
        Mesa mesa = new Mesa("Mesa 1", 4, "Livre", new Estabelecimento());
        mesa.setMesa_id(1L);

        when(mesaRepository.findAll()).thenReturn(List.of(mesa));

        List<MesaDetalhamento> result = mesaService.listarMesas();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).mesa_id());
    }

    @Test
    void excluirMesa_deveLancarExcecaoQuandoMesaNaoEncontrada() {
        when(mesaRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> mesaService.excluirMesa(1L));
    }
}