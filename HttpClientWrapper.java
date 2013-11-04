package com.example.twitdemokt;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.Intent;

public class HttpClientWrapper {
    
     private DefaultHttpClient mClient = null;
     private Context mContext = null;
    
     private final static boolean USE_PROXY = false;
     private final static String PROXY_SERVER = PrivateConstants.PROXY_SERVER;
     private final static int PORT_NUM = PrivateConstants.PROXY_PORT_NUM;

     public HttpClientWrapper() {
          mClient = new DefaultHttpClient();
          if(USE_PROXY) {
               HttpHost proxy = new HttpHost(PROXY_SERVER, PORT_NUM);
               mClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
          }
     }
    
     public HttpClientWrapper(Context context) {
          mClient = new DefaultHttpClient();
          mContext = context;
          if(USE_PROXY) {
               HttpHost proxy = new HttpHost(PROXY_SERVER, PORT_NUM);
               mClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
          }
     }
    
     public HttpResponse execute(HttpUriRequest request, boolean needAuthorize)
               throws ClientProtocolException, IOException {
          AuthManager oauth = AuthManager.getInstance();
          if(needAuthorize == true) {
               if(oauth.isAuthorized() == false) {
                    if(mContext != null) {
                         Intent intent = new Intent(mContext, AccountPreferenceActivity.class);
                         mContext.startActivity(intent);
                         return null;
                    } else {
                         return null;
                    }
               }
               request.setHeader("Authorization", oauth.getAuthHeader(request));
          }
          return mClient.execute(request);
     }
    
     public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler)
               throws ClientProtocolException, IOException {
          AuthManager oauth = AuthManager.getInstance();
          if(oauth.isAuthorized() == false) {
               if(mContext != null) {
                    Intent intent = new Intent(mContext, AccountPreferenceActivity.class);
                    mContext.startActivity(intent);
                    return null;
               } else {
                    return null;
               }
          }
          request.setHeader("Authorization", oauth.getAuthHeader(request));
          return mClient.execute(request, responseHandler);
     }
    
     public <T> T executeAuthorize(AuthManager.Step step, HttpUriRequest request, ResponseHandler<? extends T> responseHandler)
               throws ClientProtocolException, IOException {
          AuthManager oauth = AuthManager.getInstance();
          request.setHeader("Authorization",
                    oauth.getAuthHeader(step, request.getMethod(), request.getURI().toString()));
          return mClient.execute(request, responseHandler);
     }

     public ClientConnectionManager getConnectionManager() {
          return mClient.getConnectionManager();
     }
}