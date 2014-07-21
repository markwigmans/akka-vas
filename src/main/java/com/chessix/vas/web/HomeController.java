package com.chessix.vas.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

/**
 * Handles requests for the application home page.
 */
@RestController
@Slf4j
public class HomeController {


    /**
     * Simply selects the home view to render by returning its name.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(final Locale locale, final Model model) {
        log.info("Welcome home! The client locale is {}.", locale);

        return "test";
    }

}