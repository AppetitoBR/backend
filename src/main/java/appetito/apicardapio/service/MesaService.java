package appetito.apicardapio.service;

import appetito.apicardapio.dto.cadastro.MesaCadastro;
import appetito.apicardapio.dto.detalhamento.MesaDetalhamento;
import appetito.apicardapio.entity.Mesa;
import appetito.apicardapio.repository.MesaRepository;
import appetito.apicardapio.repository.EstabelecimentoRepository;
import appetito.apicardapio.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MesaService {

    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;

    public MesaDetalhamento cadastrarMesa(MesaCadastro dadosMesa) {
        estabelecimentoRepository.findById(dadosMesa.estabelecimento_id()).orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado"));
        Mesa mesa = new Mesa(
                dadosMesa.nome(),
                dadosMesa.capacidade(),
                dadosMesa.status(),
                dadosMesa.qrCode(),
                dadosMesa.estabelecimento_id()
        );
        mesaRepository.save(mesa);
        return new MesaDetalhamento(mesa);
    }

    public MesaDetalhamento atualizarMesa(Long id, MesaCadastro dadosMesa) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada"));
        estabelecimentoRepository.findById(dadosMesa.estabelecimento_id()).orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado"));

        mesa.setNome(dadosMesa.nome());
        mesa.setCapacidade(dadosMesa.capacidade());
        mesa.setStatus(dadosMesa.status());
        mesa.setQrCode(dadosMesa.qrCode());
        mesa.setEstabelecimento(dadosMesa.estabelecimento_id());

        mesaRepository.save(mesa);
        return new MesaDetalhamento(mesa);
    }

    public void excluirMesa(Long id) {
        if (!mesaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mesa não encontrada");
        }
        mesaRepository.deleteById(id);
    }

    public MesaDetalhamento buscarMesaPorId(Long id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada"));
        return new MesaDetalhamento(mesa);
    }

    public List<MesaDetalhamento> listarMesas() {
        return mesaRepository.findAll().stream()
                .map(MesaDetalhamento::new)
                .collect(Collectors.toList());
    }
}