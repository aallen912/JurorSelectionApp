package com.JurorSelectionApp.Services;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

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
			@RequestParam(value="city", required=true) String city,
			@RequestParam(value="uName", required=true) String uName,
			@RequestParam(value="pWord", required=true) String pWord
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
			Class.forName("com.mysql.cj.jdbc.Driver");
            Connection cn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jurorselectionapplication", "root", "");
            Statement s = cn.createStatement();
			String checkUser = "select * from user_info where ";
			checkUser += "uName like '%" + uName + "%';";
			boolean userExists = false;

			ResultSet rs = s.executeQuery(checkUser);

			while(rs.next()){
				String userName = rs.getString(1);
				String passWord = rs.getString(2);
				String fName = rs.getString(3);
				String lName = rs.getString(4); 
				String location = rs.getString(5);
				if((userName.equalsIgnoreCase(uName) && passWord.equalsIgnoreCase(pWord)) || (fName.equalsIgnoreCase(firstName) && lName.equalsIgnoreCase(lastName) && location.equalsIgnoreCase(city))){
					userExists = true;
				}
			}

			if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

			// Get response body
			String responseBody = response.body().string();
			boolean isLawyer = !responseBody.contains("No records were found that matched your search criteria.");
			model.addAttribute("isLawyer", isLawyer);
			
			if (isLawyer && !userExists) {
				model.addAttribute("displayText", firstName + " " + lastName + ", you have been verified as a Georgia lawyer and have been registered as a user.");
				String createNewUser = "insert into user_info values ('" + uName + "', '" + pWord + "','" + firstName + "','"+ lastName + "','" + city + "');";
				Statement newS = cn.createStatement();
				int nrs = newS.executeUpdate(createNewUser);
			}
			else if(userExists){
				model.addAttribute("displayText", uName + " already exists as a user. Please log in or choose a new user name");
			}
			else {
				model.addAttribute("displayText", firstName + " " + lastName + ", you are not authorized you use this site. Only Georgia Lawyers in good standing with the State Bar of Georgia are allowed to use this site.");
			}
		
		}

		return "lawyerChecker";
	}
	
	@RequestMapping("/verify")
	public String displayPage() {
		return "lawyerChecker.html";
	}
}