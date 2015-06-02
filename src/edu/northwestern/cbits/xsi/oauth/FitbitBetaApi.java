package edu.northwestern.cbits.xsi.oauth;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.DefaultApi20;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import edu.northwestern.cbits.xsi.XSI;

public class FitbitBetaApi extends DefaultApi20
{
	public static final String CONSUMER_KEY = "fitbit_beta_consumer_key";
	public static final String CONSUMER_SECRET = "fitbit_beta_consumer_secret";
	public static final String USER_SECRET = "fitbit_beta_user_secret";
	public static final String USER_TOKEN = "fitbit_beta_user_token";
    public static final String OAUTH2_CLIENT_ID = "fitbit_beta_client_id";

    public FitbitBetaApi()
	{
		super();
	}

    @Override
    public String getAccessTokenEndpoint()
    {
        return "https://api.fitbit.com/oauth2/token";
    }

    @Override
    public String getAuthorizationUrl(OAuthConfig oAuthConfig)
    {

        String url =  "https://www.fitbit.com/oauth2/authorize?response_type=code&client_id=" + FitbitBetaApi.OAUTH2_CLIENT_ID +
                "&redirect_uri=http%3A%2F%2Ftech.cbits.northwestern.edu%2Fpurple-robot%2F" +
                "&scope=activity%20nutrition%20heartrate%20location%20nutrition%20profile%20settings%20sleep%20social%20weight";

        Log.e("PR", "FB OAUTH CONFIG: " + oAuthConfig);
        Log.e("PR", "FB URL: " + url);

        return url;
    }

	public static JSONObject fetch(Uri uri)
	{
		try
		{
            Token accessToken = new Token(Keystore.get(FitbitBetaApi.USER_TOKEN), Keystore.get(FitbitBetaApi.USER_SECRET));

            final OAuthRequest request = new OAuthRequest(Verb.GET, uri.toString());
            request.addHeader("User-Agent", XSI.getUserAgent());

            ServiceBuilder builder = new ServiceBuilder();
            builder = builder.provider(FitbitBetaApi.class);
            builder = builder.apiKey(Keystore.get(FitbitBetaApi.CONSUMER_KEY));
            builder = builder.apiSecret(Keystore.get(FitbitBetaApi.CONSUMER_SECRET));

            final OAuthService service = builder.build();

            service.signRequest(accessToken, request);

            Response response = request.send();

			return new JSONObject(response.getBody());
		} 
		catch (JSONException | OAuthException e)
		{
			e.printStackTrace();
		}

        return null;
	}
}
