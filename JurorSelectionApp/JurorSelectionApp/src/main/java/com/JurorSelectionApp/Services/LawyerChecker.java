package com.JurorSelectionApp.Services;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Controller
public class LawyerChecker {


	private final OkHttpClient httpClient = new OkHttpClient();

	@RequestMapping(value="/verifyLawyer", method=RequestMethod.GET)
	private String sendPost(
			Model model,
			@RequestParam(value="firstName", required=true) String firstName,
			@RequestParam(value="lastName", required=true) String lastName,
			@RequestParam(value="city", required=true) String city
			) throws Exception {

		// form parameters
		RequestBody formBody = new FormBody.Builder()
				.add("firstName", firstName)
				.add("lastName", lastName)
				.add("city", city)
				.build();

		Request request = new Request.Builder()
				.url("https://www.gabar.org/membersearchresults.cfm")
				.addHeader("User-Agent", "OkHttp Bot")
				.post(formBody)
				.build();

		try (Response response = httpClient.newCall(request).execute()) {

			if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

			// Get response body
			String responseBody = response.body().string();
			boolean isLawyer = !responseBody.contains("No records were found that matched your search criteria.");
			model.addAttribute("isLawyer", isLawyer);
			
			if (isLawyer) {
				model.addAttribute("displayText", firstName + " " + lastName + ", you have been verified as a Georgia lawyer.");
			}
			else {
				model.addAttribute("displayText", firstName + " " + lastName + ", you are not authorized you use this site. Only Georgia Lawyers in good standing with the State Bar of Georgia are allowed to use this site.");
			}
		
		}

		return "lawyerChecker.html";
	}
	
	@RequestMapping("/verify")
	public String displayPage() {
		return "lawyerChecker.html";
	}
}
