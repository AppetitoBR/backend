package appetito.apicardapio.service;

import appetito.apicardapio.dto.cadastro.MesaCadastro;
import appetito.apicardapio.dto.detalhamento.MesaDetalhamento;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.Mesa;
import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.entity.UsuarioEstabelecimento;
import appetito.apicardapio.enums.PapelUsuario;
import appetito.apicardapio.repository.MesaRepository;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.UsuarioEstabelecimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MesaService {

    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private final UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository;

    public MesaService(UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository) {
        this.usuarioEstabelecimentoRepository = usuarioEstabelecimentoRepository;
    }


    public MesaDetalhamento cadastrarMesa(MesaCadastro dadosMesa) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UsuarioDashboard usuario)) {
            throw new AccessDeniedException("Apenas usuários do dashboard podem cadastrar mesas");
        }

        Estabelecimento estabelecimento = usuarioEstabelecimentoRepository
                .findByUsuario(usuario)
                .stream()
                .filter(v -> v.getPapel() == PapelUsuario.ADMINISTRADOR || v.getPapel() == PapelUsuario.GERENTE)
                .findFirst()
                .map(UsuarioEstabelecimento::getEstabelecimento)
                .orElseThrow(() -> new AccessDeniedException("Você não possui permissão para cadastrar mesas"));

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


    public MesaDetalhamento atualizarMesa(Long id, MesaCadastro dadosMesa) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth.getPrincipal() instanceof UsuarioDashboard usuario)) {
            throw new AccessDeniedException("Usuário não autenticado.");
        }

        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada."));

        boolean vinculado = usuarioEstabelecimentoRepository.existsByUsuarioAndEstabelecimento(usuario, mesa.getEstabelecimento());
        if (!vinculado) {
            throw new AccessDeniedException("Você não tem permissão para atualizar esta mesa.");
        }
        mesaRepository.save(mesa);
        return new MesaDetalhamento(mesa);
    }

    public void excluirMesa(Long id) {

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth.getPrincipal() instanceof UsuarioDashboard usuario)) {
            throw new AccessDeniedException("Usuário não autenticado.");
        }

        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada."));

        boolean vinculado = usuarioEstabelecimentoRepository.existsByUsuarioAndEstabelecimento(usuario, mesa.getEstabelecimento());
        if (!vinculado) {
            throw new AccessDeniedException("Você não tem permissão para excluir esta mesa.");
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