package com.example.twitdemokt;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class TimelineRestUpdateTask extends AsyncTask<Void, Void, ArrayList<TweetStatus>>{
	private final static String LOG_TAG = TimelineRestUpdateTask.class.getName();
    
    private static SimpleDateFormat mDateFormat = new SimpleDateFormat(
              "EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);
   
    public TimelineRestUpdateTask() {
    }
   
    @Override
    protected ArrayList<TweetStatus> doInBackground(Void... params) {
         // prepare parameters
         String paramString = "count=30";
        
         HttpClientWrapper client = new HttpClientWrapper();
         HttpGet request = new HttpGet(Constants.TWITTER_API_USER_TIMELINE);
        
         String requestResult = null;
         try {
              requestResult = client.execute(request, new ResponseHandler<String>() {
                   @Override
                   public String handleResponse(HttpResponse response)
                             throws ClientProtocolException, IOException {
                        switch (response.getStatusLine().getStatusCode()) {
                        case HttpStatus.SC_OK:
                            return EntityUtils.toString(response.getEntity(), "UTF-8");
                        case HttpStatus.SC_BAD_REQUEST:
                             Log.e(LOG_TAG, "Failed to call rest API. API limit.");
                             return null;
                        default:
                             Log.e(LOG_TAG, "Failed to call rest API. Unknown connection error. ["+
                                       response.getStatusLine().getStatusCode()+"]");
                             Log.e(LOG_TAG, "Response Body : " +
                                       EntityUtils.toString(response.getEntity(), "UTF-8"));
                             return null;
                        }
                   }
              });
         } catch(Exception e) {
              Log.e(LOG_TAG, "Failed to get rest API.");
              e.printStackTrace();
         }
        
         client.getConnectionManager().shutdown();
        
         return parseJson(requestResult);
    }
   
    @Override
    public void onPostExecute(ArrayList<TweetStatus> statuses) {
         if(statuses == null) {
              Log.w(LOG_TAG, "No status update.");
              return;
         }
        
         for(TweetStatus status : statuses) {
           // use request result here!
         }
    }
   
    public ArrayList<TweetStatus> parseJson(String strJson) {
         // parse result.
         ArrayList<TweetStatus> statuses = new ArrayList<TweetStatus>();
         try {
              JSONArray jsonArray = new JSONArray(strJson);
             
              for(int i = 0; i < jsonArray.length(); ++i) {
                   TweetStatus status = new TweetStatus();
                   JSONObject jsonStatus = jsonArray.getJSONObject(i);
                  
                   if(!jsonStatus.has("user")) {
                        continue;
                   }
                  
                   try {
                        status.id = jsonStatus.getLong("id");
                        status.userId = jsonStatus.getJSONObject("user").getLong("id");
                        status.screenName = jsonStatus.getJSONObject("user").getString("screen_name");
                        status.profileImageUrl = jsonStatus.getJSONObject("user").getString("profile_image_url");
                        status.text = jsonStatus.getString("text");
                        // parse date time.
                        String strDate = jsonStatus.getString("created_at");
                        mDateFormat.parse(strDate);
                        status.datetime = mDateFormat.parse(strDate);
                        // parse entities
                        JSONArray jsonUrls = jsonStatus.getJSONObject("entities").getJSONArray("urls");
                        status.linkedUrls = new ArrayList<String>();
                        for(int j = 0; j < jsonUrls.length(); ++j) {
                             String expandedUrl = jsonUrls.getJSONObject(j).getString("expanded_url");
                             if(expandedUrl != null) {
                                  Log.d(LOG_TAG, expandedUrl);
                                  status.linkedUrls.add(expandedUrl);
                             }
                             status.linkedUrls.add(jsonUrls.getJSONObject(j).getString("url"));
                        }
                        JSONArray jsonMentions = jsonStatus.getJSONObject("entities").getJSONArray("user_mentions");
                        status.mentionedUsers = new ArrayList<String>();
                        for(int j = 0; j < jsonMentions.length(); ++j) {
                             status.mentionedUsers.add(jsonMentions.getJSONObject(j).getString("screen_name"));
                        }
                        JSONArray jsonHashTags = jsonStatus.getJSONObject("entities").getJSONArray("hashtags");
                        status.hashTags = new ArrayList<String>();
                        for(int j = 0; j < jsonHashTags.length(); ++j) {
                             status.hashTags.add(jsonHashTags.getJSONObject(j).getString("text"));
                        }
                   } catch(NullPointerException e) {
                        Log.w(LOG_TAG, "Could not get status params.");
                   }
                  
                   statuses.add(status);
              }
             
         } catch(Exception e) {
              Log.w(LOG_TAG, "Failed to parse json.");
              e.printStackTrace();
         }
        
         return statuses;
    }
	
}
