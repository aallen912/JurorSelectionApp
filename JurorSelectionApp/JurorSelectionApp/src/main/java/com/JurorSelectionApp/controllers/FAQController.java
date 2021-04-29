package com.JurorSelectionApp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FAQController {

	@RequestMapping("/faq")
	public String test( ) {
		return "faq.html";
	}
}
