package edu.northwestern.cbits.xsi.oauth;

import org.json.JSONArray;
import org.json.JSONException;
import org.scribe.builder.ServiceBuilder;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

public class TwitterApi extends org.scribe.builder.api.TwitterApi
{
	public static final String CONSUMER_KEY = "twitter_consumer_key";
	public static final String CONSUMER_SECRET = "twitter_consumer_secret";
    public static final String USER_TOKEN = "twitter_user_token";
    public static final String USER_SECRET = "twitter_user_secret";

    public static JSONArray fetchAll(String url)
    {
        try
        {
            Token accessToken = new Token(Keystore.get(TwitterApi.USER_TOKEN), Keystore.get(TwitterApi.USER_SECRET));

            ServiceBuilder builder = new ServiceBuilder();
            builder = builder.provider(org.scribe.builder.api.TwitterApi.class);
            builder = builder.apiKey(Keystore.get(TwitterApi.CONSUMER_KEY));
            builder = builder.apiSecret(Keystore.get(TwitterApi.CONSUMER_SECRET));

            final OAuthService service = builder.build();

            final OAuthRequest request = new OAuthRequest(Verb.GET, url);
            service.signRequest(accessToken, request);

            Response response = request.send();

            String body = response.getBody();

            return new JSONArray(body);
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
