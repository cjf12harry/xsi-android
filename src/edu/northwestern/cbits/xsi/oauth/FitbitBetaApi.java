package edu.northwestern.cbits.xsi.oauth;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.api.DefaultApi20;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.OAuthConfig;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

public class FitbitBetaApi extends DefaultApi20
{
	public static final String CONSUMER_KEY = "fitbit_beta_consumer_key";
	public static final String CONSUMER_SECRET = "fitbit_beta_consumer_secret";
	public static final String USER_SECRET = "fitbit_beta_user_secret";
	public static final String USER_TOKEN = "fitbit_beta_user_token";
    public static final String OAUTH2_CLIENT_ID = "fitbit_beta_client_id";
    public static final String CALLBACK_URL = "fitbit_beta_callback_url";
    public static final String USER_ACCESS_TOKEN = "fitbit_beta_user_access_token";
    public static final String USER_REFRESH_TOKEN = "fitbit_beta_user_refresh_token";
    public static final String USER_TOKEN_EXPIRES = "fitbit_beta_token_expires";

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

        String url =  "https://www.fitbit.com/oauth2/authorize?response_type=code&client_id=" + Keystore.get(FitbitBetaApi.OAUTH2_CLIENT_ID) +
                "&redirect_uri=" + URLEncoder.encode(Keystore.get(FitbitBetaApi.CALLBACK_URL)) +
                "&scope=activity%20nutrition%20heartrate%20location%20nutrition%20profile%20settings%20sleep%20social%20weight";

        return url;
    }

    public static boolean refreshTokens(Context context, String accessKey, String refreshKey, String expiresKey)
    {
        try
        {
            URL u = new URL("https://api.fitbit.com/oauth2/token");
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("POST");

            String basicAuth = Keystore.get(FitbitBetaApi.OAUTH2_CLIENT_ID) + ":" + Keystore.get(FitbitBetaApi.CONSUMER_SECRET);
            conn.setRequestProperty("Authorization", "Basic " + Base64.encodeToString(basicAuth.getBytes(Charset.forName("UTF-8")), 0));

            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("grant_type", "refresh_token")
                    .appendQueryParameter("refresh_token", Keystore.get(FitbitBetaApi.USER_REFRESH_TOKEN));

            String query = builder.build().getEncodedQuery();

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

            conn.connect();;

            InputStream in = null;

            if (conn.getResponseCode() != 200)
                in = new BufferedInputStream(conn.getErrorStream());
            else
                in = new BufferedInputStream(conn.getInputStream());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[8192];
            int read = 0;

            while ((read = in.read(buffer, 0, buffer.length)) != -1)
            {
                baos.write(buffer, 0, read);
            }

            in.close();

            String body = baos.toString();

            JSONObject payload =  new JSONObject(body);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor e = prefs.edit();
            e.putString(accessKey, payload.getString("access_token"));
            e.putString(refreshKey, payload.getString("refresh_token"));
            e.putLong(expiresKey, System.currentTimeMillis() + (payload.getLong("expires_in") * 1000));
            e.commit();

            return true;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

	public static JSONObject fetch(Uri uri)
	{
		try
		{
            String accessToken = Keystore.get(FitbitBetaApi.USER_ACCESS_TOKEN);

            URL u = new URL(uri.toString());

            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);

            InputStream in = null;

            if (conn.getResponseCode() != 200)
                in = new BufferedInputStream(conn.getErrorStream());
            else
                in = new BufferedInputStream(conn.getInputStream());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[8192];
            int read = 0;

            while ((read = in.read(buffer, 0, buffer.length)) != -1)
            {
                baos.write(buffer, 0, read);
            }

            in.close();

            String body = baos.toString();

            return new JSONObject(body);
		} 
		catch (JSONException | OAuthException | IOException e)
		{
			e.printStackTrace();
		}

        return null;
	}
}
