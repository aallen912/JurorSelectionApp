package com.JurorSelectionApp.controllers;

import java.util.ArrayList;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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

  //Normal method for retrieving potential jurors most recent tweets
  static void TwitterAPI(String UserName){
    Twitter twitter = new TwitterFactory().getInstance();

    twitter.setOAuthConsumer(APIKey, APIKeySecret);
    twitter.setOAuthAccessToken(new AccessToken(AccessToken, AccessTokenSecret));

    try{
      //Requesting page 1, number of elements per page is 5
      ResponseList<Status> status = twitter.getUserTimeline(UserName, new Paging(1, 5));
      
      for(Status sts: status){
        System.out.println(sts.getText()); 
      }

    }catch(Exception e){
      System.out.println("Something went wrong with Twitter API");
    }
  }

  //Method overload for if the user gives a specific query
  //Outputs how many tweets match the query
  static void TwitterAPI(String UserName, Query query){
    final long startTime = System.nanoTime();
      Twitter twitter = new TwitterFactory().getInstance();
  
      twitter.setOAuthConsumer(APIKey, APIKeySecret);
      twitter.setOAuthAccessToken(new AccessToken(AccessToken, AccessTokenSecret));
  
      int numberOfTweets = 3200;
      long lastID = Long.MAX_VALUE;
      ArrayList<Status> qTweets = new ArrayList<Status>();
  
      do{
        if (numberOfTweets - qTweets.size() > 100)
          query.setCount(100);
        else
          query.setCount(numberOfTweets - qTweets.size());
        try{
          QueryResult result = twitter.search(query);
          qTweets.addAll(result.getTweets());
          System.out.println("Gathered " + qTweets.size() + " tweets");
          for(Status sts: qTweets)
            if(sts.getId() < lastID)
              lastID = sts.getId();
        }catch(TwitterException qException){
          System.out.println("Query error: " + qException);
        };
        query.setMaxId(lastID -1);
        final long duration = System.nanoTime() - startTime;
        if((5500 - duration / 1000000) > 0){
          System.out.println("SLEEPING FOR " + (6000 - duration / 1000000) + " miliseconds");
          try {
            Thread.sleep((5500 - duration / 1000000));
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      } while (qTweets.size() < numberOfTweets);
  }

  @RequestMapping(value = {"twitter/{UserName}","/twitter/{UserName}/{query}"})
  @ResponseBody
  public static void getTwitterStatus(@PathVariable(required = true) String UserName, @PathVariable(required = false) Query query){
    if(query == null) TwitterAPI(UserName);
    else if(query != null) TwitterAPI(UserName, query);
  }


}