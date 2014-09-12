package edu.northwestern.cbits.xsi.oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;

import android.net.Uri;

public class GitHubApi extends DefaultApi20 
{
	public static final String CONSUMER_KEY = "c523b93bbf14cea549a0";
	public static final String CONSUMER_SECRET = "f58db5af03d2ef3c18c81edca1a983ea148b2f47";

	public String getAccessTokenEndpoint() 
	{
		return "https://github.com/login/oauth/access_token";
	}

	public String getAuthorizationUrl(OAuthConfig arg0) 
	{
		return "https://github.com/login/oauth/authorize?client_id=" +  GitHubApi.CONSUMER_KEY + "&scope=repo:status";
	}
	
	public static JSONArray fetchAll(String token, Uri uri)
	{
        try 
        {
        	URL url = new URL(uri.toString() + "?access_token=" + token);

            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            StringBuffer all = new StringBuffer();
            String inputLine = null;

            while ((inputLine = in.readLine()) != null)
			{
				all.append(inputLine);
				all.append("\n");
			}

            in.close();
            
            return new JSONArray(all.toString());
		} 
        catch (IOException e) 
        {
			e.printStackTrace();
		} 
        catch (JSONException e) 
        {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static JSONObject fetch(String token, Uri uri)
	{
        try 
        {
        	URL url = new URL(uri.toString() + "?access_token=" + token);
        	
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            StringBuffer all = new StringBuffer();
            String inputLine = null;

            while ((inputLine = in.readLine()) != null)
			{
				all.append(inputLine);
				all.append("\n");
			}

            in.close();

            return new JSONObject(all.toString());
		} 
        catch (IOException e) 
        {
			e.printStackTrace();
		} 
        catch (JSONException e) 
        {
			e.printStackTrace();
		}
		
		return null;
	}
}
