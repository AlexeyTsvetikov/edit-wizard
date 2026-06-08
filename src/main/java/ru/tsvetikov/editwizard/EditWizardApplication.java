package ru.tsvetikov.editwizard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class EditWizardApplication {

    public static void main(String[] args) {
        SpringApplication.run(EditWizardApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void openBrowser() {
        try {
            String url = "http://localhost:8080/bibliography";
            new ProcessBuilder("cmd", "/c", "start", url).start();
        } catch (Exception e) {
            // ignore
        }
    }
}
