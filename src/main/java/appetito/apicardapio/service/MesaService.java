package appetito.apicardapio.service;

import appetito.apicardapio.dto.cadastro.MesaCadastro;
import appetito.apicardapio.dto.detalhamento.MesaDetalhamento;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.Mesa;
import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.entity.UsuarioEstabelecimento;
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
                .findFirst()
                .map(UsuarioEstabelecimento::getEstabelecimento)
                .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado para o usuário"));

        Mesa mesa = new Mesa();
        mesa.setNome(dadosMesa.nome());
        mesa.setCapacidade(dadosMesa.capacidade());
        mesa.setEstabelecimento(estabelecimento);
        mesaRepository.save(mesa);

        String nomeFantasia = estabelecimento.getNomeFantasia();
        String url = "http://localhost:3000/" + nomeFantasia + "/mesa/" + mesa.getId();
        byte[] qrCodeBytes = QRCodeGeneratorService.gerarQRCode(url);
        mesa.setQrcode(qrCodeBytes);
        mesaRepository.save(mesa);
        return new MesaDetalhamento(mesa);
    }

    // mudar ainda
    public MesaDetalhamento atualizarMesa(Long id, MesaCadastro dadosMesa) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada"));

        mesa.setNome(dadosMesa.nome());
        mesa.setCapacidade(dadosMesa.capacidade());

        mesaRepository.save(mesa);
        return new MesaDetalhamento(mesa);
    }
    //mudar ainda
    public void excluirMesa(Long id) {
        if (!mesaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mesa não encontrada");
        }
        mesaRepository.deleteById(id);
    }

    //mudar ainda
    public List<MesaDetalhamento> listarMesas() {
        return mesaRepository.findAll().stream()
                .map(MesaDetalhamento::new)
                .collect(Collectors.toList());
        }

}