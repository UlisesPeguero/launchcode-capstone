package com.petboarding.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class WebController {
    @GetMapping("/checkout")
    public String checkout(Model model) {
        return "checkout/checkout";
    }


}
