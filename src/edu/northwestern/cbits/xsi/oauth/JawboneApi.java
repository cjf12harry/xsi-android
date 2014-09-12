package edu.northwestern.cbits.xsi.oauth;

import java.io.IOException;
import java.net.URLEncoder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;

import android.content.Context;
import android.net.Uri;
import android.net.http.AndroidHttpClient;

public class JawboneApi extends DefaultApi20 
{
	public static final String CONSUMER_KEY = "fd6P6eL5d9g";
	public static final String CONSUMER_SECRET = "1a7357e886f0b42c4f2fc611ec91f25454e4c67a";

	public String getAccessTokenEndpoint() 
	{
		return "https://jawbone.com/auth/oauth2/token";
	}

	@SuppressWarnings("deprecation")
	public String getAuthorizationUrl(OAuthConfig arg0) 
	{
		return "https://jawbone.com/auth/oauth2/auth?response_type=code&client_id=" +  JawboneApi.CONSUMER_KEY + "&scope=move_read%20basic_read&redirect_uri=" + URLEncoder.encode("https://tech.cbits.northwestern.edu/oauth/jawbone");
	}
	
	public static JSONObject fetch(Context context, String token, Uri uri, String userAgent)
	{
		AndroidHttpClient androidClient = AndroidHttpClient.newInstance(userAgent, context);
		
        HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

		SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
		registry.register(new Scheme("https", socketFactory, 443));

		HttpParams params = androidClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 180000);
		HttpConnectionParams.setSoTimeout(params, 180000);

		ThreadSafeClientConnManager mgr = new ThreadSafeClientConnManager(params, registry);
		HttpClient httpClient = new DefaultHttpClient(mgr, params);

		HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
		
		HttpGet httpGet = new HttpGet("https://jawbone.com/nudge/api/v.1.1/users/@me/goals");
		httpGet.addHeader("Authorization", "Bearer " + token);
		httpGet.addHeader("Accept", "application/json");
		httpGet.addHeader("X-Target-URI", "https://jawbone.com");
		httpGet.addHeader("X-HostCommonName", "jawbone.com");

		try 
		{
			HttpResponse response = httpClient.execute(httpGet);

			HttpEntity httpEntity = response.getEntity();

			String result = EntityUtils.toString(httpEntity);
			
			
			
			return new JSONObject(result);		
		} 
		catch (ClientProtocolException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			androidClient.close();
		}

		return null;
	}

}
