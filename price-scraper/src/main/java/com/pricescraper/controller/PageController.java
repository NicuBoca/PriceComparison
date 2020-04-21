package com.pricescraper.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
    @RequestMapping(value="/home")
    public static String welcome() {
        return "index";
    }
}
