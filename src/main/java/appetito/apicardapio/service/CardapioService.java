package appetito.apicardapio.service;

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


    // Buscar um cardápio por ID
    public CardapioDetalhamento buscarCardapioPorId(Long cardapio_id) {
        Cardapio cardapio = cardapioRepository.findById(cardapio_id)
                .orElseThrow(() -> new ResourceNotFoundException("Cardápio não encontrado"));
        return new CardapioDetalhamento(cardapio);
    }

    // Listar cardápios por estabelecimento
    public List<CardapioDetalhamento> listarCardapiosPorEstabelecimento(Estabelecimento estabelecimento) {
        List<Cardapio> cardapios = cardapioRepository.findByEstabelecimento(estabelecimento);// mudar o quanto antes
        if (cardapios.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum cardápio encontrado para o estabelecimento informado");
        }
        return cardapios.stream()
                .map(CardapioDetalhamento::new)
                .collect(Collectors.toList());
    }

    public void deletarCardapio(Long id) {
        if (!cardapioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cardapio não encontrada");
        }
        cardapioRepository.deleteById(id);
    }
    // deleta o estabelecimento apenas se ele pertencer a ele
    public boolean deletarSePertencerAoEstabelecimento(Long cardapioId, Estabelecimento estabelecimento) {
        Optional<Cardapio> cardapioOpt = cardapioRepository.findByIdAndEstabelecimento(cardapioId, estabelecimento); // mudar o quanto antes

        if (cardapioOpt.isEmpty()) {
            return false;
        }

        cardapioRepository.deleteById(cardapioId);
        return true;
    }
}
