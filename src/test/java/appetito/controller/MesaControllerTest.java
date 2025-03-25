package appetito.controller;


import appetito.apicardapio.controller.MesaController;
import appetito.apicardapio.dto.cadastro.MesaCadastro;
import appetito.apicardapio.dto.detalhamento.MesaDetalhamento;
import appetito.apicardapio.service.MesaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MesaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MesaService mesaService;
    @InjectMocks
    private MesaController mesaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mesaController).build();
    }

    @Test
    void cadastrarMesa_deveRetornarCreated() throws Exception {
        MesaDetalhamento mesaDetalhamento = new MesaDetalhamento(1L, "Mesa 1", 4, "Livre",  1L);

        when(mesaService.cadastrarMesa(any(MesaCadastro.class))).thenReturn(mesaDetalhamento);

        mockMvc.perform(post("/mesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Mesa 1\",\"capacidade\":4,\"status\":\"Livre\",\"qrCode\":\"QR123\",\"estabelecimento_id\":1}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.mesa_id").value(1L));
    }

    @Test
    void atualizarMesa_deveRetornarOk() throws Exception {
        MesaDetalhamento mesaDetalhamento = new MesaDetalhamento(1L, "Mesa 1", 4, "Livre", 1L);

        when(mesaService.atualizarMesa(any(Long.class), any(MesaCadastro.class))).thenReturn(mesaDetalhamento);

        mockMvc.perform(put("/mesas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Mesa 1\",\"capacidade\":4,\"status\":\"Livre\",\"qrCode\":\"QR123\",\"estabelecimento_id\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mesa_id").value(1L));
    }

    @Test
    void excluirMesa_deveRetornarNoContent() throws Exception {
        mockMvc.perform(delete("/mesas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void buscarMesaPorId_deveRetornarOk() throws Exception {
        MesaDetalhamento mesaDetalhamento = new MesaDetalhamento(1L, "Mesa 1", 4, "Livre", 1L);

        when(mesaService.buscarMesaPorId(1L)).thenReturn(mesaDetalhamento);

        mockMvc.perform(get("/mesas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mesa_id").value(1L));
    }

    @Test
    void listarMesas_deveRetornarOk() throws Exception {
        MesaDetalhamento mesaDetalhamento = new MesaDetalhamento(1L, "Mesa 1", 4, "Livre", 1L);

        when(mesaService.listarMesas()).thenReturn(List.of(mesaDetalhamento));

        mockMvc.perform(get("/mesas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].mesa_id").value(1L));
    }
}