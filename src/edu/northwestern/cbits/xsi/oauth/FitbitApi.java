package edu.northwestern.cbits.xsi.oauth;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import android.net.Uri;

public class FitbitApi extends DefaultApi10a 
{
	public static final String CONSUMER_KEY = "942cc901ff16414a81a599668a1987d6";
	public static final String CONSUMER_SECRET = "8182965179ef4494ba6294ff77602b3c";

	public FitbitApi()
	{
		super();
	}
	
	public String getAccessTokenEndpoint() 
	{
		return "https://api.fitbit.com/oauth/access_token";
	}

	public String getAuthorizationUrl(Token token) 
	{
		return "https://www.fitbit.com/oauth/authenticate?oauth_token=" + token.getToken();
	}

	public String getRequestTokenEndpoint() 
	{
		return "https://api.fitbit.com/oauth/request_token";
	}
	
	public static JSONObject fetch(String token, String secret, Uri uri)
	{
		Token accessToken = new Token(token, secret);
    	
		final OAuthRequest request = new OAuthRequest(Verb.GET, uri.toString());

    	ServiceBuilder builder = new ServiceBuilder();
    	builder = builder.provider(FitbitApi.class);
    	builder = builder.apiKey(FitbitApi.CONSUMER_KEY);
    	builder = builder.apiSecret(FitbitApi.CONSUMER_SECRET);

    	final OAuthService service = builder.build();

		service.signRequest(accessToken, request);

		Response response = request.send();

		try 
		{
			return new JSONObject(response.getBody());
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
}
