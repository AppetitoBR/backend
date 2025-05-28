package appetito.apicardapio.service;

import appetito.apicardapio.dto.cadastro.MesaCadastro;
import appetito.apicardapio.dto.detalhamento.MesaDetalhamento;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.Mesa;
import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.entity.UsuarioEstabelecimento;
import appetito.apicardapio.repository.EstabelecimentoRepository;
import appetito.apicardapio.repository.MesaRepository;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.UsuarioEstabelecimentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MesaService {


    private final MesaRepository mesaRepository;
    private final UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;
    public MesaService(MesaRepository mesaRepository, UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository, EstabelecimentoRepository estabelecimentoRepository) {
        this.mesaRepository = mesaRepository;
        this.usuarioEstabelecimentoRepository = usuarioEstabelecimentoRepository;
        this.estabelecimentoRepository = estabelecimentoRepository;
    }

    public MesaDetalhamento cadastrarMesa(Long estabelecimentoId, MesaCadastro dadosMesa) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId)
                .orElseThrow(() -> new EntityNotFoundException("Estabelecimento não encontrado"));

        Mesa mesa = new Mesa();
        mesa.setNome(dadosMesa.nome());
        mesa.setCapacidade(dadosMesa.capacidade());
        mesa.setEstabelecimento(estabelecimento);
        mesaRepository.save(mesa);


        String url = "http://localhost:8080/" + estabelecimento.getNomeFantasia() + "/mesa/" + mesa.getId() + "/cardapio";
        byte[] qrCodeBytes = QRCodeGeneratorService.gerarQRCode(url);
        mesa.setQrcode(qrCodeBytes);

        mesaRepository.save(mesa);

        return new MesaDetalhamento(mesa);
    }

    public MesaDetalhamento atualizarMesa(Long estabelecimentoId, Long mesaId, MesaCadastro dadosMesa) {
        Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada."));

        if (!mesa.getEstabelecimento().getEstabelecimentoId().equals(estabelecimentoId)) {
            throw new AccessDeniedException("A mesa não pertence a este estabelecimento.");
        }

        mesa.setNome(dadosMesa.nome());
        mesa.setCapacidade(dadosMesa.capacidade());

        mesaRepository.save(mesa);
        return new MesaDetalhamento(mesa);
    }

    public void excluirMesa(Long estabelecimentoId, Long mesaId) {
        Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada."));

        if (!mesa.getEstabelecimento().getEstabelecimentoId().equals(estabelecimentoId)) {
            throw new AccessDeniedException("A mesa não pertence a este estabelecimento.");
        }

        mesaRepository.delete(mesa);
    }

    public List<MesaDetalhamento> listarMesasPorEstabelecimento(String nomeFantasia) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth.getPrincipal() instanceof UsuarioDashboard usuario)) {
            throw new AccessDeniedException("Usuário não autenticado.");
        }

        Estabelecimento estabelecimento = usuarioEstabelecimentoRepository.findAllByUsuario(usuario).stream()
                .map(UsuarioEstabelecimento::getEstabelecimento)
                .filter(e -> e.getNomeFantasia().equalsIgnoreCase(nomeFantasia))
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("Você não tem acesso a esse estabelecimento."));

        List<Mesa> mesas = mesaRepository.findAllByEstabelecimento(estabelecimento);
        return mesas.stream().map(MesaDetalhamento::new).toList();
    }

}