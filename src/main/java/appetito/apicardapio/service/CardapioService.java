package appetito.apicardapio.service;

import appetito.apicardapio.dto.CardapioCadastro;
import appetito.apicardapio.dto.CardapioDetalhamento;
import appetito.apicardapio.entity.Cardapio;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.Colaborador;
import appetito.apicardapio.repository.CardapioRepository;
import appetito.apicardapio.repository.EstabelecimentoRepository;
import appetito.apicardapio.repository.ColaboradorRepository;
import appetito.apicardapio.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardapioService {

    @Autowired
    private final CardapioRepository cardapioRepository;

    @Autowired
    private final EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    private final ColaboradorRepository colaboradorRepository;

    public CardapioService(CardapioRepository cardapioRepository, EstabelecimentoRepository estabelecimentoRepository, ColaboradorRepository colaboradorRepository) {
        this.cardapioRepository = cardapioRepository;
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.colaboradorRepository = colaboradorRepository;
    }

    // Cadastrar um cardápio
    public CardapioDetalhamento cadastrarCardapio(CardapioCadastro dadosCardapio) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(dadosCardapio.estabelecimento_id())
                .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado"));

        Colaborador colaborador = null;
        if (dadosCardapio.colaborador_id() != null) {
            colaborador = colaboradorRepository.findById(dadosCardapio.colaborador_id())
                    .orElseThrow(() -> new ResourceNotFoundException("Colaborador não encontrado"));
        }

        Cardapio cardapio = new Cardapio(
                dadosCardapio.nome(),
                dadosCardapio.secao(),
                dadosCardapio.descricao(),
                estabelecimento,
                colaborador,
                dadosCardapio.vigencia_inicio(),
                dadosCardapio.vigencia_fim()
        );

        cardapioRepository.save(cardapio);
        return new CardapioDetalhamento(cardapio);
    }

    // Buscar um cardápio por ID
    public CardapioDetalhamento buscarCardapioPorId(Long id) {
        Cardapio cardapio = cardapioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cardápio não encontrado"));
        return new CardapioDetalhamento(cardapio);
    }

    // Listar cardápios por estabelecimento
    public List<CardapioDetalhamento> listarCardapiosPorEstabelecimento(Long estabelecimentoId) {
        return cardapioRepository.findByEstabelecimentoId(estabelecimentoId).stream()
                .map(CardapioDetalhamento::new)
                .collect(Collectors.toList());
    }
}