package com.example.twitdemokt;

import java.net.URI;
import java.net.URLEncoder;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

import android.util.Base64;

public class AuthManager {
	
	private final static AuthManager sInstance = new AuthManager();
	
	private final static String CALLBACK_URI = PrivateConstants.TWITTER_CALLBACK_URL;
	private final static String mConsumerKey = PrivateConstants.CONSUMER_KEY;
	private final static String mConsumerSecret = PrivateConstants.CONSUMER_SECRET_KEY;
	
	private static String mAccessToken = null;
	private static String mAccessTokenSecret = null;
	private static String mRequestToken = null;
	private static String mRequestTokenSecret = null;
	private static String mVerifier = null;
	
	public enum Step {
		REQUEST_TOKEN, ACCESS_TOKEN
	}
	
	private AuthManager() {}
	
	public static synchronized AuthManager getInstance() {
		if(mAccessToken == null){
			AccountPreference accountPref = AccountPreference.getInstance();
			mAccessToken = accountPref.getAccessToken();
			mAccessTokenSecret = accountPref.getAccessTokenSecret();
		}
		return sInstance;
	}
	
	public synchronized boolean isAuthorized() {
		return mAccessToken != null;
	}
	
	public synchronized void clearToken() {
		mAccessToken = null;
		mRequestToken = null;
		mAccessTokenSecret = null;
	}
	
	public synchronized void setVerifier(String verifier) {
		mVerifier = verifier;
	}
	
	public synchronized void setRequestTokenAndSecret(String token, String secret) {
		mRequestToken = token;
		mRequestTokenSecret = secret;
	}
	
	public synchronized void setAccessTokenAndSecret(String token, String secret) {
		mAccessToken = token;
		mAccessTokenSecret = secret;
		
		AccountPreference accountPref = AccountPreference.getInstance();
		accountPref.setAccessToken(token);
		accountPref.setAccessTokenSecret(secret);
	}
	
	public synchronized String getAuthHeader(HttpUriRequest request) {
		String method = request.getMethod();
		URI uri = request.getURI();
		String queries = uri.getQuery();
		String url = uri.getScheme() + "://" + uri.getHost() + uri.getPath();
		
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put(Constants.QUERY_CONSUMER_KEY, mConsumerKey);
		params.put(Constants.QUERY_SIGNATURE_METHOD, "HMAC-SHA1");
		params.put(Constants.QUERY_TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000));
		params.put(Constants.QUERY_NONCE, String.valueOf(Math.round(Math.random() * 3000000)));
		params.put(Constants.QUERY_VERSION, "1.0");
		params.put(Constants.QUERY_TOKEN, mAccessToken);
		
		TreeMap<String, String> queryParams = (TreeMap<String, String>) params.clone();
		if(queries != null) {
			for(String query : queries.split("&")) {
				String[] keyValues = query.split("=");
				queryParams.put(keyValues[0], keyValues[1]);
			}
		}
		
		String body = "";
		if("POST".equals(method)) {
			HttpPost post = (HttpPost)request;
			try{
				body = EntityUtils.toString(post.getEntity());
				if(body.length() > 0) {
					for(String query : body.split("&")) {
						String[] keyValues = query.split("=");
						queryParams.put(keyValues[0], keyValues[1]);
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		String paramStr = "";
		for (Entry<String, String> param : queryParams.entrySet()) {
			paramStr += "&" + param.getKey() + "=" + param.getValue();
		}
		paramStr = paramStr.substring(1);
		
		String text = method + "&" + URLEncoder.encode(url) + "&" + URLEncoder.encode(paramStr);
		
		String key = mConsumerSecret + "&" + mAccessTokenSecret;
		
		String signature = null;
		SecretKeySpec signinKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
		try {
			Mac mac = Mac.getInstance(signinKey.getAlgorithm());
			mac.init(signinKey);
			signature = Base64.encodeToString(mac.doFinal(text.getBytes()), Base64.DEFAULT);
		}catch(Exception e) {
			throw new RuntimeException("Could not get sigin key.");
		}
		params.put(Constants.QUERY_SIGNATURE, URLEncoder.encode(signature));
		
		String header = "";
		for (Entry<String, String> param : params.entrySet()) {
			header += ", " + param.getKey() + "=" + param.getValue();
		}
		header = "OAuth " + header.substring(2);
		
		return header;
	}
	
	public synchronized String getAuthHeader(Step step, String method, String url) {
	
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put(Constants.QUERY_CONSUMER_KEY, mConsumerKey);
		params.put(Constants.QUERY_SIGNATURE_METHOD, "HMAC-SHA1");
		params.put(Constants.QUERY_TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000));
		params.put(Constants.QUERY_NONCE, String.valueOf(Math.round(Math.random() * 3000000)));
		params.put(Constants.QUERY_VERSION, "1.0");
		if(step == Step.ACCESS_TOKEN) {
			params.put(Constants.QUERY_TOKEN, mAccessToken);
			params.put(Constants.QUERY_VERIFIER, mRequestToken);
		} else {
			params.put(Constants.QUERY_CALLBACK, URLEncoder.encode(CALLBACK_URI));
		}
		
		String paramStr = "";
		for (Entry<String, String> param : params.entrySet()) {
			paramStr += "&" + param.getKey() + "=" + param.getValue();
		}
		paramStr = paramStr.substring(1);
		
		String text = method + "&" + URLEncoder.encode(url) + "&" + URLEncoder.encode(paramStr);
		String key = mConsumerSecret + "&";
		if(step == Step.ACCESS_TOKEN) {
			key += mRequestTokenSecret;
		}
		
		String signature = null;
		SecretKeySpec signinKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
		try {
			Mac mac = Mac.getInstance(signinKey.getAlgorithm());
			mac.init(signinKey);
			signature = Base64.encodeToString(mac.doFinal(text.getBytes()), Base64.DEFAULT);
		}catch(Exception e) {
			throw new RuntimeException("Could not get sigin key.");
		}
		params.put(Constants.QUERY_SIGNATURE, URLEncoder.encode(signature));
		
		String header = "";
		for (Entry<String, String> param : params.entrySet()) {
			header += ", " + param.getKey() + "=" + param.getValue();
		}
		header = "OAuth " + header.substring(2);
		
		return header;
	}
}
