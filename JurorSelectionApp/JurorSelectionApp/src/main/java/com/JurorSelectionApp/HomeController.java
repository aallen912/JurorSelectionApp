package com.JurorSelectionApp;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import com.restfb.types.Post;

@Controller
public class HomeController {

	@RequestMapping("/")
	public String test( ) {	
		return "lawyerChecker";
	}
}
