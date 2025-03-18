package appetito.apicardapio.service;

import appetito.apicardapio.dto.ColaboradorCadastro;
import appetito.apicardapio.dto.ColaboradorDetalhamento;
import appetito.apicardapio.entity.Colaborador;
import appetito.apicardapio.entity.Usuario;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.repository.ColaboradorRepository;
import appetito.apicardapio.repository.UsuarioRepository;
import appetito.apicardapio.repository.EstabelecimentoRepository;
import appetito.apicardapio.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ColaboradorService {

    @Autowired
    private ColaboradorRepository colaboradorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;

    // Buscar um colaborador por ID
    public ColaboradorDetalhamento buscarColaboradorPorId(Long id) {
        Colaborador colaborador = colaboradorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Colaborador não encontrado"));
        return new ColaboradorDetalhamento(colaborador);
    }

    public List<ColaboradorDetalhamento> listarColaboradoresPorEstabelecimento(Long estabelecimentoId) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado"));

        return colaboradorRepository.findByEstabelecimento(estabelecimento).stream()
                .map(ColaboradorDetalhamento::new)
                .collect(Collectors.toList());
    }



}
