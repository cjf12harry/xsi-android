package edu.northwestern.cbits.xsi.oauth;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class iHealthApi extends DefaultApi20
{
	public static final String CONSUMER_KEY = "ihealth_consumer_key";
	public static final String CONSUMER_SECRET = "ihealth_consumer_secret";
	public static final String USER_SECRET = "ihealth_user_secret";
	public static final String USER_TOKEN = "ihealth_user_token";

    public static final String ACTIVITY_SC = "ihealth_activity_sc";
    public static final String ACTIVITY_SV = "ihealth_activity_sv";
    public static final String SLEEP_SC = "ihealth_sleep_sc";
    public static final String SLEEP_SV = "ihealth_sleep_sv";
    public static final String WEIGHT_SC = "ihealth_weight_sc";
    public static final String WEIGHT_SV = "ihealth_weight_sv";
    public static final String OXYGEN_SC = "ihealth_oxygen_sc";
    public static final String OXYGEN_SV = "ihealth_oxygen_sv";
    public static final String PRESSURE_SC = "ihealth_pressure_sc";
    public static final String PRESSURE_SV = "ihealth_pressure_sv";
    public static final String GLUCOSE_SC = "ihealth_glucose_sc";
    public static final String GLUCOSE_SV = "ihealth_gulcose_sv";

    public static final String REDIRECT_URL = "ihealth_redirect_url";

    public iHealthApi()
	{
		super();
	}
	
	public String getAccessTokenEndpoint() 
	{
        return "https://api.ihealthlabs.com:8443/OpenApiV2/OAuthv2/userauthorization/";
	}

    public String getAuthorizationUrl(OAuthConfig oAuthConfig)
    {
        StringBuilder urlBuffer = new StringBuilder();
        urlBuffer.append("https://api.ihealthlabs.com:8443/OpenApiV2/OAuthv2/userauthorization");
        urlBuffer.append("?client_id=" + Keystore.get(iHealthApi.CONSUMER_KEY));
        urlBuffer.append("&response_type=code");
        urlBuffer.append("&redirect_uri=" + Keystore.get(iHealthApi.REDIRECT_URL));
        urlBuffer.append("&APIName=OpenApiBG+OpenApiBP+OpenApiActivity+OpenApiSleep+OpenApiSpO2+OpenApiWeight");

        return urlBuffer.toString();
    }

    public static JSONObject fetchActivity(Context context, long start, long end) throws IOException, JSONException
    {
        iHealthApi.refreshTokenAsNeeded(context);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String userId = prefs.getString("oauth_ihealth_user_id", "");

        StringBuilder urlBuffer = new StringBuilder();
        urlBuffer.append("https://api.ihealthlabs.com:8443/openapiv2/user/" + userId + "/activity.json/");
        urlBuffer.append("?client_id=" + Keystore.get(iHealthApi.CONSUMER_KEY));
        urlBuffer.append("&client_secret=" + Keystore.get(iHealthApi.CONSUMER_SECRET));
        urlBuffer.append("&sc=" + Keystore.get(iHealthApi.ACTIVITY_SC));
        urlBuffer.append("&sv=" + Keystore.get(iHealthApi.ACTIVITY_SV));
        urlBuffer.append("&access_token=" + Keystore.get(iHealthApi.USER_TOKEN));
        urlBuffer.append("&start_time=" + start);
        urlBuffer.append("&end_time=" + end);

        BufferedReader in = new BufferedReader(new InputStreamReader(new URL(urlBuffer.toString()).openStream()));

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

    private static void refreshTokenAsNeeded(Context context) throws IOException, JSONException
    {
        long now = System.currentTimeMillis();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        long expires = prefs.getLong("oauth_ihealth_expires", 0);

        if (now > expires)
        {
            String refreshToken = prefs.getString("oauth_ihealth_refresh_token", "");

            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append("https://api.ihealthlabs.com:8443/OpenApiV2/OAuthv2/userauthorization");
            urlBuilder.append("?client_id=" + Keystore.get(iHealthApi.CONSUMER_KEY));
            urlBuilder.append("&client_secret=" + Keystore.get(iHealthApi.CONSUMER_SECRET));
            urlBuilder.append("&redirect_uri=" + Keystore.get(iHealthApi.REDIRECT_URL));
            urlBuilder.append("&response_type=refresh_token");
            urlBuilder.append("&refresh_token=" + refreshToken);

            BufferedReader in= new BufferedReader(new InputStreamReader(new URL(urlBuilder.toString()).openStream()));

            StringBuilder all = new StringBuilder();
            String inputLine = null;

            while ((inputLine = in.readLine()) != null)
            {
                all.append(inputLine);
                all.append("\n");
            }

            in.close();

            JSONObject response = new JSONObject(all.toString());

            String userId = response.getString("UserID");
            String accessToken = response.getString("AccessToken");
            refreshToken = response.getString("RefreshToken");
            expires = now + (response.getLong("Expires") * 1000);

            SharedPreferences.Editor e = prefs.edit();
            e.putString("oauth_ihealth_secret", "");
            e.putString("oauth_ihealth_token", accessToken);
            e.putString("oauth_ihealth_access_token", accessToken);
            e.putString("oauth_ihealth_refresh_token", refreshToken);
            e.putString("oauth_ihealth_user_id", userId);
            e.putLong("oauth_ihealth_expires", expires);

            e.commit();
        }
    }

    public static JSONObject fetchSleep(long start, long end)
    {
        return null;
    }

    public static JSONObject fetchWeight(Context context, long start, long end) throws IOException, JSONException
    {
        iHealthApi.refreshTokenAsNeeded(context);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String userId = prefs.getString("oauth_ihealth_user_id", "");

        StringBuilder urlBuffer = new StringBuilder();
        urlBuffer.append("https://api.ihealthlabs.com:8443/openapiv2/user/" + userId + "/weight.json/");
        urlBuffer.append("?client_id=" + Keystore.get(iHealthApi.CONSUMER_KEY));
        urlBuffer.append("&client_secret=" + Keystore.get(iHealthApi.CONSUMER_SECRET));
        urlBuffer.append("&sc=" + Keystore.get(iHealthApi.WEIGHT_SC));
        urlBuffer.append("&sv=" + Keystore.get(iHealthApi.WEIGHT_SV));
        urlBuffer.append("&access_token=" + Keystore.get(iHealthApi.USER_TOKEN));
        urlBuffer.append("&start_time=" + start);
        urlBuffer.append("&end_time=" + end);

        BufferedReader in = new BufferedReader(new InputStreamReader(new URL(urlBuffer.toString()).openStream()));

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

    public static JSONObject fetchBloodPressure(Context context, long start, long end) throws IOException, JSONException
    {
        iHealthApi.refreshTokenAsNeeded(context);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String userId = prefs.getString("oauth_ihealth_user_id", "");

        StringBuilder urlBuffer = new StringBuilder();
        urlBuffer.append("https://api.ihealthlabs.com:8443/openapiv2/user/" + userId + "/bp.json/");
        urlBuffer.append("?client_id=" + Keystore.get(iHealthApi.CONSUMER_KEY));
        urlBuffer.append("&client_secret=" + Keystore.get(iHealthApi.CONSUMER_SECRET));
        urlBuffer.append("&sc=" + Keystore.get(iHealthApi.PRESSURE_SC));
        urlBuffer.append("&sv=" + Keystore.get(iHealthApi.PRESSURE_SV));
        urlBuffer.append("&access_token=" + Keystore.get(iHealthApi.USER_TOKEN));
        urlBuffer.append("&start_time=" + start);
        urlBuffer.append("&end_time=" + end);

        BufferedReader in = new BufferedReader(new InputStreamReader(new URL(urlBuffer.toString()).openStream()));

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

    public static JSONObject fetchBloodOxygen(long start, long end)
    {
        return null;
    }

    public static JSONObject fetchBloodGlucose(Context context, long start, long end) throws IOException, JSONException
    {
        iHealthApi.refreshTokenAsNeeded(context);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String userId = prefs.getString("oauth_ihealth_user_id", "");

        StringBuilder urlBuffer = new StringBuilder();
        urlBuffer.append("https://api.ihealthlabs.com:8443/openapiv2/user/" + userId + "/weight.json/");
        urlBuffer.append("?client_id=" + Keystore.get(iHealthApi.CONSUMER_KEY));
        urlBuffer.append("&client_secret=" + Keystore.get(iHealthApi.CONSUMER_SECRET));
        urlBuffer.append("&sc=" + Keystore.get(iHealthApi.GLUCOSE_SC));
        urlBuffer.append("&sv=" + Keystore.get(iHealthApi.GLUCOSE_SV));
        urlBuffer.append("&access_token=" + Keystore.get(iHealthApi.USER_TOKEN));
        urlBuffer.append("&start_time=" + start);
        urlBuffer.append("&end_time=" + end);

        BufferedReader in = new BufferedReader(new InputStreamReader(new URL(urlBuffer.toString()).openStream()));

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

    public static void completeLogin(Context context, String code) throws IOException, JSONException
    {
        long now = System.currentTimeMillis();

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("https://api.ihealthlabs.com:8443/OpenApiV2/OAuthv2/userauthorization");
        urlBuilder.append("?client_id=" + Keystore.get(iHealthApi.CONSUMER_KEY));
        urlBuilder.append("&client_secret=" + Keystore.get(iHealthApi.CONSUMER_SECRET));
        urlBuilder.append("&grant_type=authorization_code");
        urlBuilder.append("&redirect_uri=" + Keystore.get(iHealthApi.REDIRECT_URL));
        urlBuilder.append("&code=" + code);

        BufferedReader in= new BufferedReader(new InputStreamReader(new URL(urlBuilder.toString()).openStream()));

        StringBuilder all = new StringBuilder();
        String inputLine = null;

        while ((inputLine = in.readLine()) != null)
        {
            all.append(inputLine);
            all.append("\n");
        }

        in.close();

        JSONObject response = new JSONObject(all.toString());

        String userId = response.getString("UserID");
        String accessToken = response.getString("AccessToken");
        String refreshToken = response.getString("RefreshToken");
        long expires = now + (response.getLong("Expires") * 1000);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor e = prefs.edit();
        e.putString("oauth_ihealth_secret", "");
        e.putString("oauth_ihealth_token", accessToken);
        e.putString("oauth_ihealth_access_token", accessToken);
        e.putString("oauth_ihealth_refresh_token", refreshToken);
        e.putString("oauth_ihealth_user_id", userId);
        e.putLong("oauth_ihealth_expires", expires);

        e.commit();
    }
}
