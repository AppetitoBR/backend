package appetito.apicardapio.service;

import appetito.apicardapio.dto.EstabelecimentoCadastro;
import appetito.apicardapio.dto.EstabelecimentoDetalhamento;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.EstabelecimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstabelecimentoService {

    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;

    public EstabelecimentoDetalhamento cadastrarEstabelecimento(EstabelecimentoCadastro dadosEstabelecimento) {
        if (estabelecimentoRepository.findByCnpj(dadosEstabelecimento.cnpj()).isPresent()) {
            throw new IllegalArgumentException("CNPJ já cadastrado");
        }
        Estabelecimento estabelecimento = new Estabelecimento(
                dadosEstabelecimento.razaoSocial(),
                dadosEstabelecimento.nomeFantasia(),
                dadosEstabelecimento.cnpj(),
                dadosEstabelecimento.tipo()
        );
        estabelecimentoRepository.save(estabelecimento);
        return new EstabelecimentoDetalhamento(estabelecimento);
    }

    public EstabelecimentoDetalhamento buscarEstabelecimentoPorId(Long id) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado"));
        return new EstabelecimentoDetalhamento(estabelecimento);
    }
}
