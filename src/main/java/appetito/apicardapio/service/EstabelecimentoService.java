package appetito.apicardapio.service;

import appetito.apicardapio.dto.EstabelecimentoCadastro;
import appetito.apicardapio.dto.EstabelecimentoDetalhamento;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.EstabelecimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstabelecimentoService {

    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;

    // Cadastrar um novo estabelecimento
    public EstabelecimentoDetalhamento cadastrarEstabelecimento(EstabelecimentoCadastro dadosEstabelecimento) {
        Estabelecimento estabelecimento = new Estabelecimento(
                dadosEstabelecimento.razao_social(),
                dadosEstabelecimento.nome_fantasia(),
                dadosEstabelecimento.cnpj(),
                dadosEstabelecimento.tipo()
        );

        estabelecimentoRepository.save(estabelecimento);
        return new EstabelecimentoDetalhamento(estabelecimento);
    }

    // Buscar um estabelecimento por ID
    public EstabelecimentoDetalhamento buscarEstabelecimentoPorId(Long id) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado"));
        return new EstabelecimentoDetalhamento(estabelecimento);
    }

    // Listar todos os estabelecimentos
    public List<EstabelecimentoDetalhamento> listarEstabelecimentos() {
        return estabelecimentoRepository.findAll().stream()
                .map(EstabelecimentoDetalhamento::new)
                .collect(Collectors.toList());
    }

    // Atualizar um estabelecimento
    public EstabelecimentoDetalhamento atualizarEstabelecimento(Long id, EstabelecimentoCadastro dadosAtualizados) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado"));

        estabelecimento.setRazao_social(dadosAtualizados.razao_social());
        estabelecimento.setNome_fantasia(dadosAtualizados.nome_fantasia());
        estabelecimento.setCnpj(dadosAtualizados.cnpj());
        estabelecimento.setTipo(dadosAtualizados.tipo());

        estabelecimentoRepository.save(estabelecimento);
        return new EstabelecimentoDetalhamento(estabelecimento);
    }

    // Desativar um estabelecimento (soft delete)
    public void desativarEstabelecimento(Long id) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado"));

        estabelecimento.setAtivo(false);
        estabelecimentoRepository.save(estabelecimento);
    }
}
