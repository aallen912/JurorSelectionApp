package com.JurorSelectionApp.controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.util.Chars;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

@Controller
public class TwitterController {

  //Consumer Keys
  public static String APIKey = "jf17zQrbUvhFZBR4lNLWagB2n";
  public static String APIKeySecret = "e6UctqDOsbuwv19FqUbNeCvIOWRqz97oOQGBKjUb6yuWhpL3Es";

  //Access tokens
  public static String AccessToken = "1239719404088299525-jiEv8stzv85qToF6FYKJtbwZIAl0gJ";
  public static String AccessTokenSecret = "r5tzVEfewhx4Ed0ZXa1LyArMF8WqSusf66Zewi8nde6Gd";

  //Test class for new method of finding tweets on a users page
  static void TwitterAPI(String UserName, CharSequence Query){
    //Used for the sleep function
    final long startTime = System.nanoTime();
    Twitter twitter = new TwitterFactory().getInstance();
  
    twitter.setOAuthConsumer(APIKey, APIKeySecret);
    twitter.setOAuthAccessToken(new AccessToken(AccessToken, AccessTokenSecret));
    
    int pagenum = 1;
    List<Status> FlaggedPosts = new ArrayList<Status>();
    int numFlagged = 0;
    
    do{
      try{
        //Pulling every post from a users page
        int size = FlaggedPosts.size();
        Paging page = new Paging(pagenum++, 100);
        FlaggedPosts.addAll(twitter.getUserTimeline(UserName,page));

        //Filtering each post
        ArrayList<Status> filter = new ArrayList<Status>();
        
        //Searches each status and checks if it contains the keyword, if it doesn't it removes it from Flagged Posts
        for (Status i : FlaggedPosts) {
          if(i.getText().contains(Query) == true){
            filter.add(i);
            // System.out.println(i.getText()); used for debugging
          }
        }
        
        //Functionality for showing the post to the user
        // ArrayList<String> PrintedStatus = new ArrayList();
        // for (Status status : filter) {
        //   if(PrintedStatus.contains(status.getText())  == false){
        //     System.out.println(UserName + " made a post that contained " + Query + " :");
        //     PrintedStatus.add(status.getText());
        //     System.out.println(status.getText());
        //     String url= "https://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId();
        //     System.out.println(url); 
        //   }else{
        //     System.out.println("this post was already shown");
        //   }
        // }
        // System.out.println(PrintedStatus.toString());

        if(FlaggedPosts.size() == size){
          break;
        }
        numFlagged = filter.size();

      }catch(TwitterException e){
        System.out.println("There was an error finding posts on a users page: ");
        e.printStackTrace();
      }
      //Sleep functionality to avoid gettting locked out of the twitter api
      final long duration = System.nanoTime() - startTime;
      if((5500 - duration / 1000000) > 0){
        System.out.println("SLEEPING FOR " + (6000 - duration / 1000000) + " miliseconds");
        try {
          Thread.sleep((5500 - duration / 1000000));
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }while(true);

    
    
    System.out.println(" \n Total Flagged Posts for " + UserName + " for keyword " + Query +": " + numFlagged);
  }



  @RequestMapping(value = {"/twitter"})
  @ResponseBody
  public static void getTwitterStatus(@RequestParam(required = true) String Username, @RequestParam(required = true) String query){

    String[] ArrayUsername = Username.split(",",0);
    String[] ArrayQuery = query.split(",",0);

    //Formatting the strings received to contain one space on either side of them
    for (String str : ArrayUsername) {
      String.format("%" + 1 + "s", str);
      String.format("%" + (-1) + "s", str);
      //System.out.println(str); Used for debugging
    }
    for (String str : ArrayQuery) {
      String.format("%" + 1 + "s", str);
      String.format("%" + (-1) + "s", str);
      //System.out.println(str); Used for debugging
    }

    //This loop will run through each keyword given once for each username given
    for (String name : ArrayUsername) {
      for(CharSequence word : ArrayQuery){
        TwitterAPI(name, word);
      }
    }
  }


}