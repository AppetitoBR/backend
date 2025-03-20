package appetito.apicardapio.repository;


import appetito.apicardapio.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByCardapio(Long id); // Para listar produtos de um cardápio específico
   List<Produto> findAllByAtivoTrue();
}
