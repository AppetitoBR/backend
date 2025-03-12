package appetito.apicardapio.service;

import appetito.apicardapio.dto.EstabelecimentoCadastro;
import appetito.apicardapio.dto.EstabelecimentoDetalhamento;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.Usuario;
import appetito.apicardapio.repository.EstabelecimentoRepository;
import appetito.apicardapio.repository.UsuarioRepository;
import appetito.apicardapio.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstabelecimentoService {

    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public EstabelecimentoDetalhamento cadastrarEstabelecimento(EstabelecimentoCadastro dadosEstabelecimento) {
        Usuario usuarioCadastro = usuarioRepository.findById(dadosEstabelecimento.usuario_cadastro_id())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Estabelecimento estabelecimento = new Estabelecimento(
                dadosEstabelecimento.razao_social(),
                dadosEstabelecimento.nome_fantasia(),
                dadosEstabelecimento.cnpj(),
                dadosEstabelecimento.tipo(),
                dadosEstabelecimento.segmento(),
                usuarioCadastro
        );

        estabelecimentoRepository.save(estabelecimento);
        return new EstabelecimentoDetalhamento(estabelecimento);
    }
}