package appetito.apicardapio.service;

import appetito.apicardapio.dto.cadastro.MesaCadastro;
import appetito.apicardapio.dto.detalhamento.MesaDetalhamento;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.Mesa;
import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.entity.UsuarioEstabelecimento;
import appetito.apicardapio.repository.MesaRepository;
import appetito.apicardapio.repository.EstabelecimentoRepository;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.UsuarioEstabelecimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
                .orElseThrow(() -> new RuntimeException("Estabelecimento não encontrado para o usuário"));

        Mesa mesa = new Mesa();
        mesa.setNome(dadosMesa.nome());
        mesa.setCapacidade(dadosMesa.capacidade());
        mesa.setEstabelecimento(estabelecimento);
        mesaRepository.save(mesa);

        // quero ver se consigo fazer de forma dinamica
        String url = "http://localhost:3000/mesa/" + mesa.getId();
        byte[] qrCodeBytes = QRCodeGeneratorService.gerarQRCode(url);
        mesa.setQrcode(qrCodeBytes);
        mesaRepository.save(mesa);
        return new MesaDetalhamento(mesa);
    }
    public MesaDetalhamento atualizarMesa(Long id, MesaCadastro dadosMesa) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada"));

        mesa.setNome(dadosMesa.nome());
        mesa.setCapacidade(dadosMesa.capacidade());

        mesaRepository.save(mesa);
        return new MesaDetalhamento(mesa);
    }

    public void excluirMesa(Long id) {
        if (!mesaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mesa não encontrada");
        }
        mesaRepository.deleteById(id);
    }

    public List<MesaDetalhamento> listarMesas() {
        return mesaRepository.findAll().stream()
                .map(MesaDetalhamento::new)
                .collect(Collectors.toList());
        }

}