package appetito.apicardapio.service;

import appetito.apicardapio.dto.GetAll.CardapioDados;
import appetito.apicardapio.dto.cadastro.CardapioCadastro;
import appetito.apicardapio.dto.detalhamento.CardapioDetalhamento;
import appetito.apicardapio.entity.Cardapio;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.Mesa;
import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.enums.PapelUsuario;
import appetito.apicardapio.repository.CardapioRepository;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.EstabelecimentoRepository;
import appetito.apicardapio.repository.MesaRepository;
import appetito.apicardapio.repository.UsuarioEstabelecimentoRepository;
import appetito.apicardapio.security.DiscordAlert;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CardapioService {

    private final CardapioRepository cardapioRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final MesaRepository mesaRepository;

    public CardapioService(CardapioRepository cardapioRepository, EstabelecimentoRepository estabelecimentoRepository, MesaRepository mesaRepository){
        this.cardapioRepository = cardapioRepository;
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.mesaRepository = mesaRepository;
    }

    public List<CardapioDados> listarCardapiosComProdutosPorNomeFantasia(String nomeFantasia) {
        List<Cardapio> cardapios = cardapioRepository.findByEstabelecimentoNomeFantasia(nomeFantasia);

        return cardapios.stream()
                .map(CardapioDados::new)
                .toList();
    }

    public List<CardapioDados> listarCardapiosPorMesa(String nomeFantasia, Long mesaId) {

        Estabelecimento estabelecimento = estabelecimentoRepository
                .findByNomeFantasia(nomeFantasia)
                .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado"));

        Mesa mesa = mesaRepository.findById(mesaId)
                .filter(m -> m.getEstabelecimento().equals(estabelecimento))
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada ou não pertence ao estabelecimento"));

        List<Cardapio> cardapios = cardapioRepository.findByEstabelecimentoNomeFantasia(nomeFantasia);

        return cardapios.stream()
                .map(CardapioDados::new)
                .collect(Collectors.toList());
    }

    public void deletarCardapioDoEstabelecimento(Long estabelecimentoId, Long cardapioId, UsuarioDashboard usuario) throws AccessDeniedException {
        Cardapio cardapio = cardapioRepository.findById(cardapioId)
                .orElseThrow(() -> new EntityNotFoundException("Cardápio não encontrado"));

        if (!cardapio.getEstabelecimento().getEstabelecimentoId().equals(estabelecimentoId)) {
            throw new AccessDeniedException("Cardápio não pertence a este estabelecimento.");
        }

        cardapioRepository.delete(cardapio);
    }

    public Cardapio cadastrarCardapio(Long estabelecimentoId, CardapioCadastro dadosCardapio, UsuarioDashboard usuario) {

        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId)
                .orElseThrow(() -> new EntityNotFoundException("Estabelecimento não encontrado."));

        Cardapio cardapio = new Cardapio(dadosCardapio);
        cardapio.setEstabelecimento(estabelecimento);
        cardapioRepository.save(cardapio);

        return cardapio;
    }
}
