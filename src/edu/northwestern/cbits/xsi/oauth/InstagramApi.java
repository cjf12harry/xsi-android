package edu.northwestern.cbits.xsi.oauth;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.DefaultApi20;
import org.scribe.exceptions.OAuthException;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.extractors.JsonTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuth20ServiceImpl;
import org.scribe.oauth.OAuthService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class InstagramApi extends DefaultApi20 
{
    public static final String CONSUMER_KEY = "instagram_consumer_key";
    public static final String CONSUMER_SECRET = "instagram_consumer_secret";
    public static final String USER_TOKEN = "instagram_user_token";
    public static final String USER_SECRET = "instagram_user_secret";

    private static final String URL = "https://api.instagram.com/oauth/authorize/?client_id=%s&redirect_uri=%s&response_type=code";
	public static final String CALLBACK = "http://tech.cbits.northwestern.edu/oauth/instagram";

    public Verb getAccessTokenVerb()
	{
		return Verb.POST;
	}
	 
	public String getAccessTokenEndpoint() 
	{
		return "https://api.instagram.com/oauth/access_token";
	}
	 
	public String getAuthorizationUrl(OAuthConfig config) 
	{
        try
        {
            return String.format(URL, config.getApiKey(), URLEncoder.encode(InstagramApi.CALLBACK, "utf-8"));
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        return null;
    }
	 
	public AccessTokenExtractor getAccessTokenExtractor() 
	{
		return new JsonTokenExtractor();
	}
	 
	public OAuthService createService(final OAuthConfig config) 
	{
		return new OAuth20ServiceImpl(this, config) 
		{
			public Token getAccessToken(Token requestToken, Verifier verifier) 
			{
				OAuthRequest request = new OAuthRequest(getAccessTokenVerb(), getAccessTokenEndpoint());

				request.addBodyParameter("grant_type", "authorization_code");
				request.addBodyParameter(OAuthConstants.CLIENT_ID, config.getApiKey());
				request.addBodyParameter(OAuthConstants.CLIENT_SECRET, config.getApiSecret());
				request.addBodyParameter(OAuthConstants.CODE, verifier.getValue());
				request.addBodyParameter(OAuthConstants.REDIRECT_URI, InstagramApi.CALLBACK);

				if (config.hasScope())
                    request.addBodyParameter(OAuthConstants.SCOPE, config.getScope());

				Response response = request.send();
				return getAccessTokenExtractor().extract(response.getBody());
			}
		};
	}

    public static JSONObject fetch(String url)
    {
        try
        {
            Token accessToken = new Token(Keystore.get(InstagramApi.USER_TOKEN), Keystore.get(InstagramApi.USER_SECRET));

            ServiceBuilder builder = new ServiceBuilder();
            builder = builder.provider(InstagramApi.class);
            builder = builder.apiKey(Keystore.get(InstagramApi.CONSUMER_KEY));
            builder = builder.apiSecret(Keystore.get(InstagramApi.CONSUMER_SECRET));
            builder = builder.apiSecret(Keystore.get(InstagramApi.CONSUMER_SECRET));
            builder = builder.callback(InstagramApi.CALLBACK);

            final OAuthService service = builder.build();

            final OAuthRequest request = new OAuthRequest(Verb.GET, url);
            service.signRequest(accessToken, request);

            Response response = request.send();

            String body = response.getBody();

            return new JSONObject(body);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        catch (OAuthException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
