package appetito.apicardapio.service;

import appetito.apicardapio.dto.MesaCadastro;
import appetito.apicardapio.dto.MesaDetalhamento;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.Mesa;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.EstabelecimentoRepository;
import appetito.apicardapio.repository.MesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MesaService {

    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;

    public MesaDetalhamento cadastrarMesa(MesaCadastro dadosMesa) {
        // Verifica se o estabelecimento existe
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(dadosMesa.estabelecimentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado"));

        // Cria a mesa
        Mesa mesa = new Mesa(
                dadosMesa.nome(),
                dadosMesa.capacidade(),
                dadosMesa.status(),
                dadosMesa.qrCode(),
                estabelecimento
        );

        // Salva a mesa no banco de dados
        mesaRepository.save(mesa);

        // Retorna os detalhes da mesa
        return new MesaDetalhamento(mesa);
    }

    public MesaDetalhamento buscarMesaPorQrCode(String qrCode) {
        Mesa mesa = mesaRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada"));
        return new MesaDetalhamento(mesa);
    }
}