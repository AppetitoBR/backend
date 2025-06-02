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

/**
 * Serviço responsável pelas operações relacionadas ao cardápio,
 * incluindo cadastro, exclusão e listagens por estabelecimento ou mesa.
 */
@Service
public class CardapioService {

    private final CardapioRepository cardapioRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final MesaRepository mesaRepository;

    /**
     * Construtor para injeção de dependências.
     *
     * @param cardapioRepository           repositório de cardápios
     * @param estabelecimentoRepository    repositório de estabelecimentos
     * @param mesaRepository               repositório de mesas
     */
    public CardapioService(CardapioRepository cardapioRepository, EstabelecimentoRepository estabelecimentoRepository, MesaRepository mesaRepository){
        this.cardapioRepository = cardapioRepository;
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.mesaRepository = mesaRepository;
    }

    /**
     * Lista todos os cardápios de um estabelecimento com base no nome fantasia.
     *
     * @param nomeFantasia nome fantasia do estabelecimento
     * @return lista de DTOs com dados dos cardápios
     */
    public List<CardapioDados> listarCardapiosComProdutosPorNomeFantasia(String nomeFantasia) {
        List<Cardapio> cardapios = cardapioRepository.findByEstabelecimentoNomeFantasia(nomeFantasia);

        if (cardapios.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum cardapio encontrado com o nome fantasia informado.");
        }

        return cardapios.stream()
                .map(CardapioDados::new)
                .toList();
    }

    /**
     * Lista os cardápios associados a uma determinada mesa de um estabelecimento.
     *
     * @param nomeFantasia nome fantasia do estabelecimento
     * @param mesaId       identificador da mesa
     * @return lista de DTOs com dados dos cardápios
     * @throws ResourceNotFoundException caso o estabelecimento ou a mesa não sejam encontrados
     */
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

    /**
     * Remove um cardápio de um determinado estabelecimento, após validar a propriedade.
     *
     * @param estabelecimentoId ID do estabelecimento
     * @param cardapioId        ID do cardápio a ser deletado
     * @param usuario           usuário que está solicitando a operação
     * @throws AccessDeniedException caso o cardápio não pertença ao estabelecimento
     * @throws EntityNotFoundException caso o cardápio não exista
     */
    public void deletarCardapioDoEstabelecimento(Long estabelecimentoId, Long cardapioId, UsuarioDashboard usuario) throws AccessDeniedException {
        Cardapio cardapio = cardapioRepository.findById(cardapioId)
                .orElseThrow(() -> new EntityNotFoundException("Cardápio não encontrado"));

        if (!cardapio.getEstabelecimento().getEstabelecimentoId().equals(estabelecimentoId)) {
            throw new AccessDeniedException("Cardápio não pertence a este estabelecimento.");
        }

        cardapioRepository.delete(cardapio);
    }

    /**
     * Cadastra um novo cardápio em um estabelecimento.
     *
     * @param estabelecimentoId ID do estabelecimento
     * @param dadosCardapio     dados enviados no corpo da requisição
     * @param usuario           usuário que está cadastrando o cardápio
     * @return cardápio salvo
     * @throws EntityNotFoundException caso o estabelecimento não seja encontrado
     */
    public Cardapio cadastrarCardapio(Long estabelecimentoId, CardapioCadastro dadosCardapio, UsuarioDashboard usuario) {

        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId)
                .orElseThrow(() -> new EntityNotFoundException("Estabelecimento não encontrado."));

        Cardapio cardapio = new Cardapio(dadosCardapio);
        cardapio.setEstabelecimento(estabelecimento);
        cardapioRepository.save(cardapio);

        return cardapio;
    }
}