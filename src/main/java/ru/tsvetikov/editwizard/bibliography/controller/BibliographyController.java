package ru.tsvetikov.editwizard.bibliography.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.tsvetikov.editwizard.bibliography.service.BibliographyService;
import ru.tsvetikov.editwizard.core.dto.ValidationPage;

@Controller
@RequestMapping("/bibliography")
@RequiredArgsConstructor
public class BibliographyController {

    private final BibliographyService service;

    @GetMapping
    public String checkPage(Model model) {
        model.addAttribute("page", null);
        return "bibliography/check";
    }

    @PostMapping
    public String validate(@RequestParam("sourceText") String sourceText, Model model) {
        ValidationPage page = service.validate(sourceText);
        model.addAttribute("page", page);
        return "bibliography/check";
    }
}
