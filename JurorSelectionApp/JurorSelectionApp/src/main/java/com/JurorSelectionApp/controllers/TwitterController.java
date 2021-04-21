package com.JurorSelectionApp.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import twitter4j.Paging;
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
    
    //Used for getting every post on a users page
    int pagenum = 1;
    //Stores all the posts a user has made
    List<Status> FlaggedPosts = new ArrayList<Status>();
    
    //Filter to store posts that contain key word each post
    ArrayList<Status> filter = new ArrayList<Status>();

    //Gets every post the user has made and puts it into FlaggedPosts
    while(true){
      try{
        //Pulling every post from a users page
        int size = FlaggedPosts.size();
        Paging page = new Paging(pagenum++, 100);
        FlaggedPosts.addAll(twitter.getUserTimeline(UserName,page));
        if(FlaggedPosts.size() == size){
          break;
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
      }
      catch(TwitterException e){
        System.out.println("There was an error finding posts on a users page: ");
        e.printStackTrace();
      }
    }
    
    //Searches each status and checks if it contains the keyword, if it does it adds it to filter
    for (Status i : FlaggedPosts) {
      if(i.getText().contains(Query) == true){
        //Adding the post with the keyword to filter
        filter.add(i);
        //Stores the text so the user can see the posts in context
        System.out.println(i.getText());
        //Constructing the url to show the user
        String url = "https://twitter.com/" + i.getUser().getScreenName() + "/status/" + i.getId();
        System.out.println("This post can be viewed here: " + url);
      }
    }
    //Showing the user the final results for a username/query combo
    System.out.println("\nTotal Flagged Posts for" + UserName + "for keyword" + Query +": " + filter.size());
  }



  @RequestMapping(value = {"/twitter"})
  @ResponseBody
  public static void getTwitterStatus(@RequestParam(required = true) String Username, @RequestParam(required = true) String query){

    //Splitting the String received into String arrays that contain each comman delimited word
    String[] ArrayUsername = Username.split(",",0);
    String[] ArrayQuery = query.split(",",0);

    //Formatting the strings received to contain one space on either side of them
    for (int i = 0; i < ArrayUsername.length; i++) {
      ArrayUsername[i] = String.format(" %1s ", ArrayUsername[i]);
    }
    for (int i = 0; i < ArrayQuery.length; i++) {
      ArrayQuery[i] = String.format(" %1s ", ArrayQuery[i]);
    }

    //This loop will run through each keyword given once for each username given
    for (String name : ArrayUsername) {
      for(CharSequence word : ArrayQuery){
        TwitterAPI(name, word);
      }
    }
  }


}