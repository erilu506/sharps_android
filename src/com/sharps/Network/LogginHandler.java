package com.sharps.Network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.os.AsyncTask;


public class LogginHandler extends AsyncTask<String, Integer, Void>{
	private String username;
	private String password;
	private NetworkMediator mediator=NetworkMediator.getSingletonObject();
	private boolean loggedIn=false;
	public LogginHandler(String username,String password) {
		super();
		this.username=username;
		this.password=password;
		execute();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		mediator.loginListener.loginFinished(mediator.isLoggedIn());
		super.onPostExecute(result);
	}

	@Override
	protected Void doInBackground(String... params) {
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 3000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT) 
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 3000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		HttpClient hc = new DefaultHttpClient(httpParameters);
		HttpPost post = new HttpPost(
				"http://www.sharps.se/forums/login.php?do=login");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
		nameValuePairs
				.add(new BasicNameValuePair("vb_login_username", username));
		nameValuePairs
				.add(new BasicNameValuePair("vb_login_password", password));
		nameValuePairs.add(new BasicNameValuePair("securitytoken", "guest"));
		nameValuePairs.add(new BasicNameValuePair("do", "login"));
		nameValuePairs.add(new BasicNameValuePair("cookieuser", "1"));
		try {
			// Create local HTTP context
			HttpContext localContext = new BasicHttpContext();
			// Bind custom cookie store to the local context
			localContext.setAttribute(ClientContext.COOKIE_STORE,mediator.getCockies());
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = hc.execute(post, localContext);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
