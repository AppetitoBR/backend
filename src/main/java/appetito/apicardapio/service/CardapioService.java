package appetito.apicardapio.service;

import appetito.apicardapio.dto.GetAll.CardapioDados;
import appetito.apicardapio.dto.detalhamento.CardapioDetalhamento;
import appetito.apicardapio.entity.Cardapio;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.enums.PapelUsuario;
import appetito.apicardapio.repository.CardapioRepository;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.EstabelecimentoRepository;
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
    private final UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository;

    public CardapioService(CardapioRepository cardapioRepository, UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository){
        this.cardapioRepository = cardapioRepository;
        this.usuarioEstabelecimentoRepository = usuarioEstabelecimentoRepository;
    }

    public List<CardapioDados> listarCardapiosComProdutosPorNomeFantasia(String nomeFantasia) {
        List<Cardapio> cardapios = cardapioRepository.findByEstabelecimentoNomeFantasia(nomeFantasia);

        return cardapios.stream()
                .map(CardapioDados::new)
                .toList();
    }

    @Transactional
    public void deletarCardapio(Long cardapioId, UsuarioDashboard usuario) throws AccessDeniedException {
        Cardapio cardapio = cardapioRepository.findById(cardapioId)
                .orElseThrow(() -> new ResourceNotFoundException("Cardápio não encontrado"));

        Estabelecimento estabelecimento = cardapio.getEstabelecimento();

        boolean vinculado = usuarioEstabelecimentoRepository
                .existsByUsuarioAndEstabelecimento(usuario, estabelecimento);

        if (!vinculado) {
            throw new AccessDeniedException("Você não está vinculado a este estabelecimento.");
        }
        var email = usuario.getEmail();
        new DiscordAlert().AlertDiscord("O "+ email + "Deletou um cardapio com o ID: "+ cardapioId + " Do estabelecimento: "+ estabelecimento.getRazao_social());

        cardapioRepository.delete(cardapio);
    }
}
