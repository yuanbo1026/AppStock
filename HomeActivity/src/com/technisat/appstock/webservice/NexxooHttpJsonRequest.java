package com.technisat.appstock.webservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.util.Log;

public class NexxooHttpJsonRequest {
	
	private String mBasicAuth = null;
	
	/**
	 * Constructor for get requests using basic auth on webservice 
	 * @param basicAuth Authentification parameter, username:password
	 */
	public NexxooHttpJsonRequest(String basicAuth){
		mBasicAuth = basicAuth;
	}
	
	/**
	 * Constructor for get requests without basic authentification.
	 */
	public NexxooHttpJsonRequest(){
		
	}
	
	public String performJsonRequest(String url, int task, String token, List<NameValuePair> additionalParams){
		try {
	        HttpClient httpclient = createHttpClient(); //new DefaultHttpClient();
	        HttpPost httpPostRequest = new HttpPost(url);
	        
	        if (mBasicAuth != null){ 
	        	httpPostRequest.addHeader("Authorization", "Basic " + mBasicAuth);
	        }

	        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("requesttask", Integer.toString(task)));
            postParameters.add(new BasicNameValuePair("token", token));
            
            if (additionalParams != null){
            	postParameters.addAll(additionalParams);
            }
            
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(postParameters, "UTF-8");
            httpPostRequest.setEntity(form);

	        //long t = System.currentTimeMillis();
	        HttpResponse response = (HttpResponse) httpclient.execute(httpPostRequest);
	        //Log.i("Nexxoo", "HTTPResponse received in [" + (System.currentTimeMillis()-t) + "ms]");

	        HttpEntity entity = response.getEntity();

	        if (entity != null) {
	            InputStream instream = entity.getContent();	            

	            String resultString = convertStreamToString(instream);
	            instream.close();

	            return resultString;
	        } 

	    } catch (ClientProtocolException e) {	    	
	    	Log.e("Exception", "ClientProtocolExpcetion: " + e.getMessage());
	    	return "ClientProtocolExpcetion: " + e.getMessage();
	    } catch (IOException e) {
	        Log.e("Exception", "IOException: " + e.getMessage());
	        return "IOException: " + e.getMessage();
	    }
		
	    return null;
	}
	
	private HttpClient createHttpClient()
	{
	    HttpParams params = new BasicHttpParams();
	    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	    HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
	    HttpProtocolParams.setUseExpectContinue(params, true);

	    SchemeRegistry schReg = new SchemeRegistry();
	    schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	    schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
	    ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);

	    return new DefaultHttpClient(conMgr, params);
	}
	
	public static String convertStreamToString(InputStream is) {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return sb.toString();
	}
}
