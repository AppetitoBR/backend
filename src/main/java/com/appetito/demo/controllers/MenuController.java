package com.appetito.demo.controllers;

import com.appetito.demo.models.MenuItem;
import com.appetito.demo.services.MenuService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public List<MenuItem> listarMenu() {
        return menuService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> buscarPorId(@PathVariable Long id) {
        Optional<MenuItem> item = menuService.buscarPorId(id);
        return item.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public MenuItem adicionarItem(@RequestBody MenuItem item) {
        return menuService.salvar(item);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarItem(@PathVariable Long id) {
        menuService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
