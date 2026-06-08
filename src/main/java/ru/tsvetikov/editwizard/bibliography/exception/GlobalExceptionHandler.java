package ru.tsvetikov.editwizard.bibliography.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        ModelAndView mav = new ModelAndView("bibliography/check");
        mav.addObject("error", "Произошла ошибка при проверке. Проверьте формат списка и попробуйте снова.");
        mav.addObject("page", null);
        return mav;
    }
}
