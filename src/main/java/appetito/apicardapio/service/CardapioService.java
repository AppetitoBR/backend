package appetito.apicardapio.service;

import appetito.apicardapio.dto.GetAll.CardapioDados;
import appetito.apicardapio.dto.detalhamento.CardapioDetalhamento;
import appetito.apicardapio.entity.Cardapio;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.repository.CardapioRepository;
import appetito.apicardapio.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CardapioService {

    private final CardapioRepository cardapioRepository;

    public CardapioService(CardapioRepository cardapioRepository){
        this.cardapioRepository = cardapioRepository;
    }

    public List<CardapioDados> listarCardapiosComProdutosPorNomeFantasia(String nomeFantasia) {
        List<Cardapio> cardapios = cardapioRepository.findByEstabelecimentoNomeFantasia(nomeFantasia);

        return cardapios.stream()
                .map(CardapioDados::new)
                .toList();
    }

    public boolean deletarSePertencerAoEstabelecimento(Long cardapioId, Estabelecimento estabelecimento) {
        Optional<Cardapio> cardapioOpt = cardapioRepository.findByIdAndEstabelecimento(cardapioId, estabelecimento); // mudar o quanto antes

        if (cardapioOpt.isEmpty()) {
            return false;
        }

        cardapioRepository.deleteById(cardapioId);
        return true;
    }
}
