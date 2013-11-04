package com.example.twitdemokt;

import java.io.IOException;
import java.util.TreeMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

import com.example.twitterdemo.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;

public class AccountPreferenceActivity extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_preference);
		
		Button signInBtn = (Button) findViewById(R.id.sign_in_button);
		signInBtn.setOnClickListener(this);
		
		AccountPreference accountPref = AccountPreference.getInstance();
		accountPref.initialize(this.getApplicationContext());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		
		if(intent == null) return;
		
		Log.i("AccountPreferenceActivity", "onNewIntent");
		
		if(Intent.ACTION_VIEW.equals(intent.getAction())) {
			WebView webView = (WebView) findViewById(R.id.account_setting_browser_page);
			webView.stopLoading();
			webView.setVisibility(View.GONE);
			
			View resultView = findViewById(R.id.account_setting_browser_page);
			resultView.setVisibility(View.VISIBLE);
			
			AuthManager auth = AuthManager.getInstance();
			auth.setVerifier(intent.getData().getQueryParameter(Constants.QUERY_VERIFIER));
			new GetOAuthAccessTokenTask().execute();
		}
		return;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.account_preference, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		new GetOAuthRequestTokenTask().execute();
	}

	public class GetOAuthAccessTokenTask extends AsyncTask<Void, Void, String> {

		private static final String REQUEST_URL = Constants.TWITTER_API_ACCESS_TOKEN;
		private static final String LOG_TAG = "GetOAuthAccessTokenTask";
		
		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			HttpClientWrapper client = new HttpClientWrapper();
			HttpPost request = new HttpPost(REQUEST_URL);
			
			String requestResult = null;
			try{
				requestResult = client.executeAuthorize(AuthManager.Step.ACCESS_TOKEN, request,
						new ResponseHandler<String>() {

							@Override
							public String handleResponse(HttpResponse response)
									throws ClientProtocolException, IOException {
								// TODO Auto-generated method stub
								switch(response.getStatusLine().getStatusCode()) {
									case HttpStatus.SC_OK:
										return EntityUtils.toString(response.getEntity(), "UTF-8");
									default:
										Log.e(LOG_TAG, "Failed to call request API. Unknown connection error. [" +
												response.getStatusLine().getStatusCode() + "]");
										Log.e(LOG_TAG, "Response Body : " +
												EntityUtils.toString(response.getEntity(), "UTF-8"));
										return null;
								}
							}	
				});
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(requestResult == null) {
					return null;
				}
			}
			
			TreeMap<String, String> params = new TreeMap<String, String>();
			for (String line : requestResult.split("&")) {
				String[] keyValue = line.split("=");
				params.put(keyValue[0], keyValue[1]);
			}
			
			AuthManager auth = AuthManager.getInstance();
			auth.setAccessTokenAndSecret(params.get(Constants.QUERY_TOKEN), params.get(Constants.QUERY_TOKEN_SECRET));
			
			AccountPreference accountPref = AccountPreference.getInstance();
			accountPref.setUserId(params.get(Constants.PARAM_USER_ID));
			accountPref.setScreenName(params.get(Constants.PARAM_SCREEN_NAME));
			
			return params.get(Constants.PARAM_SCREEN_NAME);
		}
		
		@Override
		protected void onPostExecute(String result) {
			finish();
			return;
		}
	}
	
	private class GetOAuthRequestTokenTask extends AsyncTask<Void, Void, String> {
		private static final String AUTHENTICATE_URL = Constants.TWITTER_API_AUTHENTICATE;
		private static final String REQUEST_URL = Constants.TWITTER_API_REQUEST_TOKEN;
		private static final String LOG_TAG = "GetOAuthRequestTokenTask";
		
		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			HttpClientWrapper client = new HttpClientWrapper();
			HttpPost request = new HttpPost(REQUEST_URL);
			
			String requestResult = null;
			try {
                requestResult = client.executeAuthorize(AuthManager.Step.REQUEST_TOKEN, request,
                          new ResponseHandler<String>() {
                     @Override
                     public String handleResponse(HttpResponse response)
                               throws ClientProtocolException, IOException {
                          switch (response.getStatusLine().getStatusCode()) {
                          case HttpStatus.SC_OK:
                        	  Log.i(LOG_TAG, "result is OK");
                        	  return EntityUtils.toString(response.getEntity(), "UTF-8");
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
           } finally {
                if(requestResult == null) {
                     Log.e(LOG_TAG, "Failed to get a result of rest API.");
                     return null;
                }
           }
          
           // parse result and store it.
           TreeMap<String, String> params = new TreeMap<String, String>();
           for(String line : requestResult.split("&")) {
                String[] keyValue = line.split("=");
                params.put(keyValue[0], keyValue[1]);
           }
           if("true".equals(params.get(Constants.QUERY_CALLBACK_CONFIRM)) == false) {
                Log.e(LOG_TAG, "API response is not confirmed.");
                return null;
           }
          
           AuthManager oauth = AuthManager.getInstance();
           oauth.setRequestTokenAndSecret(params.get(Constants.QUERY_TOKEN),
                     params.get(Constants.QUERY_TOKEN_SECRET));
          
           return params.get(Constants.QUERY_TOKEN);	
		}
		
		@Override
        protected void onPostExecute(String result) {
             if(result != null) {
                  String auth_redirect_url = AUTHENTICATE_URL + "?oauth_token=" + result;
                  Log.i(LOG_TAG, "redirect [" + auth_redirect_url + "]");
                  View introductionView = findViewById(R.id.account_setting_introduction_page);
                  introductionView.setVisibility(View.GONE);
                 
                  WebView webView = (WebView) findViewById(R.id.account_setting_browser_page);
                  webView.loadUrl(auth_redirect_url);
                  webView.setVisibility(View.VISIBLE);
                  webView.requestFocus(View.FOCUS_DOWN);
                  Log.i(LOG_TAG, "Authorization success!");
             } else {
                  Log.e(LOG_TAG, "Authorization failed!");
             }
             return;
        }
	}
}
