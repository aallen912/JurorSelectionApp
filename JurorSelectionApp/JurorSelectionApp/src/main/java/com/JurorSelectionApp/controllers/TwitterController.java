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
  static void TwitterAPI(ArrayList<String> TotalUserName, ArrayList<CharSequence> Totalquery){
    //Used for the sleep function
    final long startTime = System.nanoTime();
    Twitter twitter = new TwitterFactory().getInstance();
  
    twitter.setOAuthConsumer(APIKey, APIKeySecret);
    twitter.setOAuthAccessToken(new AccessToken(AccessToken, AccessTokenSecret));
    
    int pagenum = 1;
    List FlaggedPosts = new ArrayList();

    //This loop will run for each username given and return the number of flagged posts under each username
    for (String UserName : TotalUserName) {
      //This loop will run through each keyword given once for each username
      for(CharSequence query : Totalquery){
        do{
          try{
            //Pulling every post from a users page
            int size = FlaggedPosts.size();
            Paging page = new Paging(pagenum++, 100);
            FlaggedPosts.addAll(twitter.getUserTimeline(UserName,page));
            
            //Filtering each post
            ArrayList<Status> filter = new ArrayList<Status>();
            filter.addAll(FlaggedPosts);
            
            //Searches each status and checks if it contains the keyword, if it doesn't it removes it from Flagged Posts
            for (Status i : filter) {
              if(i.getText().contains(query) == false){
                FlaggedPosts.remove(i);
              }
            }
            if(FlaggedPosts.size() == size){
              break;
            }
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
        
        System.out.println("Total Flagged Posts for " + UserName + " for keyword " + query +": " + FlaggedPosts.size());
      }
    }
  }



  @RequestMapping(value = {"twitter/{UserName}","/twitter/{Username}/{query}"})
  @ResponseBody
  public static void getTwitterStatus(@RequestParam(required = true) ArrayList<String> Username, @RequestParam(required = true) ArrayList<CharSequence> query){
    TwitterAPI(Username, query);
  }


}