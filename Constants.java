package com.example.twitdemokt;

public class Constants {
	
	public static final String TWITTER_API_USER_TIMELINE =
			"https://api.twitter.com/1/statuses/user_timeline.json";
	
	public static final String TWITTER_API_AUTHENTICATE =
			"https://api.twitter.com/oauth/authenticate";
	
	public static final String TWITTER_API_REQUEST_TOKEN =
			"https://api.twitter.com/oauth/request_token";
	
	public static final String TWITTER_API_ACCESS_TOKEN =
			"https://api.twitter.com/oauth/access_token";
	
	public static final String PREFIX_OAUTH =
			"oauth";
	
	public static final String QUERY_VERIFIER = 
			PREFIX_OAUTH + "_" + "verifier";
	
	public static final String QUERY_TOKEN = 
			PREFIX_OAUTH + "_" + "token";
	
	public static final String QUERY_TOKEN_SECRET =
			PREFIX_OAUTH + "_" + "token_secret";
	
	public static final String QUERY_CALLBACK =
			PREFIX_OAUTH + "_" + "callback";
	
	public static final String QUERY_CALLBACK_CONFIRM =
			PREFIX_OAUTH + "_" + "callback_confirmed";
	
	public static final String QUERY_CONSUMER_KEY =
			PREFIX_OAUTH + "_" + "consumer_key";
	
	public static final String QUERY_SIGNATURE =
			PREFIX_OAUTH + "_" + "signature";
	
	public static final String QUERY_SIGNATURE_METHOD =
			PREFIX_OAUTH + "_" + "signature_method";
	
	public static final String QUERY_TIMESTAMP =
			PREFIX_OAUTH + "_" + "timestamp";
	
	public static final String QUERY_NONCE =
			PREFIX_OAUTH + "_" + "nonce";
	
	public static final String QUERY_VERSION =
			PREFIX_OAUTH + "_" + "version";
	
	public static final String PARAM_USER_ID =
			"user_id";
	
	public static final String PARAM_SCREEN_NAME =
			"screen_name";
}
