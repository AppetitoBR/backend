package com.appetito.demo.services;

import com.appetito.demo.models.MenuItem;
import com.appetito.demo.repositories.MenuRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuService {

    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    // Lista todos os itens do cardápio
    public List<MenuItem> listarTodos() {
        return menuRepository.findAll();
    }

    // Busca um item pelo ID
    public Optional<MenuItem> buscarPorId(Long id) {
        return menuRepository.findById(id);
    }

    // Salva um item no cardápio
    public MenuItem salvar(MenuItem item) {
        return menuRepository.save(item);
    }

    // Deleta um item pelo ID
    public void deletar(Long id) {
        menuRepository.deleteById(id);
    }
}
