/* Copyright © 2018 by Northwestern University. All Rights Reserved. */

package edu.northwestern.cbits.xsi.oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.api.DefaultApi20;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.OAuthConfig;

import android.net.Uri;

public class GitHubApi extends DefaultApi20 
{
	public static final String CONSUMER_KEY = "github_consumer_key";
	public static final String CONSUMER_SECRET = "github_consumer_secret";
	public static final String USER_TOKEN = "github_user_token";

	public String getAccessTokenEndpoint() 
	{
		return "https://github.com/login/oauth/access_token";
	}

	public String getAuthorizationUrl(OAuthConfig arg0) 
	{
		return "https://github.com/login/oauth/authorize?client_id=" + Keystore.get(GitHubApi.CONSUMER_KEY) + "&scope=repo";
	}
	
	public static JSONArray fetchAll(Uri uri)
	{
        try 
        {
        	URL url = null;

            if (uri.toString().contains("?"))
                url = new URL(uri.toString() + "&access_token=" + Keystore.get(GitHubApi.USER_TOKEN));
            else
                url = new URL(uri.toString() + "?access_token=" + Keystore.get(GitHubApi.USER_TOKEN));

            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            StringBuilder all = new StringBuilder();
            String inputLine = null;

            while ((inputLine = in.readLine()) != null)
			{
				all.append(inputLine);
				all.append("\n");
			}

            in.close();
            
            return new JSONArray(all.toString());
		} 
        catch (IOException | OAuthException | JSONException e)
        {
			e.printStackTrace();
		}


		return null;
	}
	
	public static JSONObject fetch(Uri uri)
	{
        try 
        {
        	URL url = new URL(uri.toString() + "?access_token=" + Keystore.get(GitHubApi.USER_TOKEN));
        	
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            StringBuilder all = new StringBuilder();
            String inputLine = null;

            while ((inputLine = in.readLine()) != null)
			{
				all.append(inputLine);
				all.append("\n");
			}

            in.close();

            return new JSONObject(all.toString());
		} 
        catch (IOException | OAuthException | JSONException e)
        {
			e.printStackTrace();
		}

		return null;
	}
}
