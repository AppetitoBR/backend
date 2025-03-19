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
    public List<CardapioDetalhamento> listarCardapiosPorEstabelecimento(Long estabelecimento_id) {
        List<Cardapio> cardapios = cardapioRepository.findByEstabelecimento(estabelecimento_id);
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
}
