package com.appetito.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping("/mensagem")
public class IdiomaController {

    @Autowired
    private MessageSource messageSource;

    @GetMapping("/{lang}")
    public String getMensagem(@PathVariable String lang) {
        Locale locale = new Locale(lang);
        return messageSource.getMessage("greeting", null, locale);
    }
}
