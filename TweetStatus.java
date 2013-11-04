package com.example.twitdemokt;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TweetStatus {
	public TweetStatus() {
    }
    public TweetStatus(long id, String screenName, String text) {
         this.id = id;
         this.screenName = screenName;
         this.text = text;
    }

    public long id;
    public long userId;
    public String screenName;
    public String profileImageUrl;
    public String text;
    public Date datetime;
   
    public List<String> mentionedUsers;
    public List<String> linkedUrls;
    public List<String> hashTags;
   
    @Override
    public boolean equals(Object obj){
         TweetStatus status = (TweetStatus) obj;
         return id == status.id;
    }
}
