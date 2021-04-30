package com.JurorSelectionApp.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;



@Controller
public class TwitterController {
  private static class QueryResult{
    public QueryResult(String Username, CharSequence Keyword, Integer NumFlaggedPosts, ArrayList<String> TweetsText, ArrayList<String> TweetsURL) {
      this.Username = Username;
      this.Keyword = Keyword;
      this.NumFlaggedPosts = NumFlaggedPosts;
      this.TweetsText = TweetsText;
      this.TweetsURL = TweetsURL;
    }
    public String Username;
    public CharSequence Keyword;
    public Integer NumFlaggedPosts;
    public ArrayList<String> TweetsText;
    public ArrayList<String> TweetsURL;

    public String tweetTextToString(){
      String resultText = "";
      int i = 1;
      for (String str : TweetsText) {
        resultText += i + ": " + str + ", <br><br>";
        i++;
      }
      if(TweetsText.size() > 1){
        resultText = resultText.substring(0, resultText.length());
      }
      return resultText;
    }

    public String tweetURLToString(){
      String resultURL = "";
      int i = 1;
      for (String str : TweetsURL) {
        resultURL += i + ": " + str + ", <br><br>";
        i++;
      }
      if(TweetsURL.size() > 1){
        resultURL = resultURL.substring(0, resultURL.length());
      }
      return resultURL;
    }
  }
  
  //Consumer Keys
  public static String APIKey = "jf17zQrbUvhFZBR4lNLWagB2n";
  public static String APIKeySecret = "e6UctqDOsbuwv19FqUbNeCvIOWRqz97oOQGBKjUb6yuWhpL3Es";
  
  //Access tokens
  public static String AccessToken = "1239719404088299525-jiEv8stzv85qToF6FYKJtbwZIAl0gJ";
  public static String AccessTokenSecret = "r5tzVEfewhx4Ed0ZXa1LyArMF8WqSusf66Zewi8nde6Gd";
  
  @RequestMapping(value = "/twitterSearchAPI")
  public static String getTwitterStatus(Model model, @RequestParam(required = true) String Username, @RequestParam(required = true) String Query){

    Boolean isLoggedInTwitter = loginController.isLoggedIn;

    ArrayList<String> Usernames = new ArrayList<String>();
    ArrayList<String> Keywords = new ArrayList<String>();
    
    
    //Splitting the String received into String arrays that contain each comman delimited word
    String[] ArrayUsername = Username.split(",",0);
    String[] ArrayQuery = Query.split(",",0);
    
    //Formatting the strings received to contain one space on the end to account for comma
    for (int i = 0; i < ArrayUsername.length; i++) {
      if(i == 0)
      ArrayUsername[i] = String.format(" %1s ", ArrayUsername[i]);
      else
      ArrayUsername[i] = String.format("%1s ", ArrayUsername[i]);
    }
    for (int i = 0; i < ArrayQuery.length; i++) {
      if(i == 0)
      ArrayQuery[i] = String.format(" %1s ", ArrayQuery[i]);
      else
      ArrayQuery[i] = String.format("%1s ", ArrayQuery[i]);
    }
    
    ArrayList<QueryResult> resultsArray = new ArrayList<QueryResult>();
    if(isLoggedInTwitter){
      //This loop will run through each keyword given once for each username given
      for (String name : ArrayUsername) {
        for(CharSequence word : ArrayQuery){
          ArrayList<Object> results = TwitterAPI(name, word);
          ArrayList<Status> temp = (ArrayList<Status>) results.get(0);
          ArrayList<String> temp2 = (ArrayList<String>) results.get(1);
          ArrayList<String> tweetsMadeText = new ArrayList<String>();
          ArrayList<String> urlsForTweetsMade = new ArrayList<String>();
          ArrayList<Status> tweetsMade = new ArrayList<Status>();
          tweetsMade.addAll(temp);
          urlsForTweetsMade.addAll(temp2);
          for (Status sts : tweetsMade) {
            tweetsMadeText.add(sts.getText());
          }
          QueryResult result = new QueryResult(name, word, temp.size(), tweetsMadeText, urlsForTweetsMade);
          resultsArray.add(result);
        }
      }
      for (String str : ArrayUsername) {
        Usernames.add(str);
      }
      for (String str : ArrayQuery) {
        Keywords.add(str);
      }
      model.addAttribute("QueryResults", resultsArray);
      return "twitterSearch.html";
    }else{
      return "index.html";
    }
  }

  //function for finding users pages and posts
  private static ArrayList<Object> TwitterAPI(String name, CharSequence word) {
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
        FlaggedPosts.addAll(twitter.getUserTimeline(name,page));
        if(FlaggedPosts.size() == size){
          break;
        }
        
        //Sleep functionality to avoid gettting locked out of the twitter api
        final long duration = System.nanoTime() - startTime;
        if((5500 - duration / 1000000) > 0){
          //System.out.println("SLEEPING FOR " + (6000 - duration / 1000000) + " miliseconds");
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
    
    ArrayList<String> url = new ArrayList<String>();

    //Searches each status and checks if it contains the keyword, if it does it adds it to filter
    for (Status i : FlaggedPosts) {
      //Printing results happens here
      if(i.getText().contains(word) == true){
        //Adding the post with the keyword to filter
        filter.add(i);

        //Stores the text so the user can see the posts in context

        //Constructing the url to show the user
        url.add("https://twitter.com/" + i.getUser().getScreenName() + "/status/" + i.getId());
      }
    }
    //System.out.println("\nTotal Flagged Posts for " + name + "for keyword" + word +": " + filter.size());

    ArrayList<Object> results = new ArrayList<Object>();
    results.add(filter);
    results.add(url);
    return results;
  }

  @RequestMapping("/twitter")
  public String displayPage(){
    return "twitterSearch.html";
  }
}