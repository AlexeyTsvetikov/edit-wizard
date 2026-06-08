package ru.tsvetikov.editwizard.bibliography.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShutdownController {

    @PostMapping("/shutdown")
    public String shutdown() {
        new Thread(() -> {
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            System.exit(0);
        }).start();
        return "Приложение закрывается...";
    }
}
