package com.JurorSelectionApp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class faqController {

    @RequestMapping("/faq")
    public String faq(){
        return "faq";
    }
}
