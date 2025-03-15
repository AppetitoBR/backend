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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardapioService {

    private final CardapioRepository cardapioRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final ColaboradorRepository colaboradorRepository;

    public CardapioService(CardapioRepository cardapioRepository,
                           EstabelecimentoRepository estabelecimentoRepository,
                           ColaboradorRepository colaboradorRepository) {
        this.cardapioRepository = cardapioRepository;
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.colaboradorRepository = colaboradorRepository;
    }

    public CardapioDetalhamento cadastrarCardapio(CardapioCadastro dadosCardapio) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(dadosCardapio.id())
                .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado"));

// Verifique se o colaborador está presente no banco
        Colaborador colaborador = null;
        if (dadosCardapio.colaborador_id() != null) {
            colaborador = colaboradorRepository.findById(dadosCardapio.colaborador_id())
                    .orElseThrow(() -> new ResourceNotFoundException("Colaborador não encontrado"));
        }

// Aqui você garante que o colaborador (se fornecido) e o estabelecimento estão salvos
        if (colaborador != null) {
            colaboradorRepository.save(colaborador);  // Salva colaborador se necessário
        }

        Cardapio cardapio = new Cardapio(
                dadosCardapio.nome(),
                dadosCardapio.secao(),
                dadosCardapio.descricao(),
                estabelecimento,  // Certifique-se que o estabelecimento existe
                colaborador,
                dadosCardapio.vigencia_inicio(),
                dadosCardapio.vigencia_fim()
        );

        cardapioRepository.save(cardapio);
        return new CardapioDetalhamento(cardapio);

    }


    // Buscar um cardápio por ID
    public CardapioDetalhamento buscarCardapioPorId(Long cardapio_id) {
        Cardapio cardapio = cardapioRepository.findById(cardapio_id)
                .orElseThrow(() -> new ResourceNotFoundException("Cardápio não encontrado"));
        return new CardapioDetalhamento(cardapio);
    }

    // Listar cardápios por estabelecimento
    public List<CardapioDetalhamento> listarCardapiosPorEstabelecimento(Long estabelecimento_id) {
        return cardapioRepository.findByEstabelecimentoId(estabelecimento_id).stream()
                .map(CardapioDetalhamento::new)
                .collect(Collectors.toList());
    }

    public void deletarCardapio(Long id) {
        if (!cardapioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mesa não encontrada");
        }
        cardapioRepository.deleteById(id);
    }
}
