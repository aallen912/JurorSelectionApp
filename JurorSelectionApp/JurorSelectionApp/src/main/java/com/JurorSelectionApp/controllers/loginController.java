package com.JurorSelectionApp.controllers;

import java.sql.Connection;
import java.sql.DriverManager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import java.sql.ResultSet;
import java.sql.Statement;


@Controller
public class loginController {
    
    static Boolean isLoggedIn = false;

    @RequestMapping(value ="/login", method = RequestMethod.GET)
    public String login(Model model, @RequestParam(value="username", required = true) String uname, @RequestParam(value="password", required = true) String pword) throws Exception{
        
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection cn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jurorselectionapplication", "root", "");
        Statement s = cn.createStatement();
        String checkUser = "select * from user_info where ";
        checkUser += "uName like '%" + uname + "%';";

        String userName = "";
        String passWord = "";

        ResultSet rs = s.executeQuery(checkUser);
        while(rs.next()){
            userName = rs.getString(1);
            passWord = rs.getString(2);
            String firstName = rs.getString(3);
            String lastName = rs.getString(4);
            String location = rs.getString(5);
        }
        
        if(userName.compareTo(uname) == 0 && passWord.compareTo(pword) == 0){
            model.addAttribute("displaylogin", "Welcome back to the Juror Selection Application User: " + uname);            
            isLoggedIn = true;
        }
        else if(userName.compareTo(uname) != 0){
            model.addAttribute("displaylogin", "This user name does not exist: " + uname);
        }
        else if(passWord.compareTo(pword) != 0){
            model.addAttribute("displaylogin", "This is the incorrect password for the User: " + uname);
        }
        else{
            model.addAttribute("displaylogin", "Please register using the above tab or use the correct username");
        }


        return "index";
    }

    
}