XSI-Android is an eXternal Service Integration toolkit for Android apps. XSI wraps a variety of OAuth services and provides a quick mechanism for authenticating and retrieving tokens from client apps. *This is a work in progress.*

##Installation & Usage##

To begin using XSI-Android, you need to import it as a library project for an existing app. 

First, install and configure [the Anthracite client library](https://github.com/cbitstech/anthracite-clients-android). 

If you're using Eclipse, import the XSI project and configure it in the project properties as a library project. 

If you're using a Gradle-based build process, add the following lines to your settings.gradle:

```
include ':xsi-android'
project(':xsi-android').projectDir = new File(settingsDir, 'xsi-android')
```

Verify that the path points to the appropriate project location. Add the project dependency to your project's `build.gradle`:

```
dependencies {
    ...
    compile project(':xsi-android')
}
```

Once these steps are complete, you're now ready to program to the library.

To authenticate via OAuth, add the following activity declarations to your Android manifest:

```
    <activity android:name="edu.northwestern.cbits.xsi.oauth.OAuthActivity" 
              android:label="@string/app_name" 
              android:screenOrientation="portrait" />
    <activity android:name="edu.northwestern.cbits.xsi.oauth.OAuthWebActivity" 
              android:label="@string/app_name" 
              android:screenOrientation="portrait" />
```

XSI-Android doesn't store tokens, secrets, or any client authorization information on its own. It uses an in-memory keystore to hold on to these values, so before you can authenticate or make any calls to the library, you should initialize the keystore with your credentials:

```
    private boolean keystoreInited()
    {
        return Keystore.contains("inited");
    }

    private void initKeystore()
    {
        Keystore.put(GitHubApi.CONSUMER_KEY, MY_GITHUB_KEY);
        Keystore.put(GitHubApi.CONSUMER_SECRET, MY_GITHUB_SECRET);
        
        Keystore.put(FitbitApi.CONSUMER_KEY, MY_FITBIT_KEY);
        Keystore.put(FitbitApi.CONSUMER_SECRET, MY_FITBIT_SECRET);
        
        Keystore.put(JawboneApi.CONSUMER_KEY, MY_JAWBONE_KEY);
        Keystore.put(JawboneApi.CONSUMER_SECRET, MY_JAWBONE_SECRET);
        
        Keystore.put("inited", "true");
    }
``` 

Obtain your own tokens and secrets from each site's respective developer portals. Feel free to omit keys for services your app will not be using.


Before accessing XSI features in your code, you should check that the keystore is initialized and ready to go:

```
    if (this.keystoreInited() == false)
        this.initKeystore();
    
    // Now we can do stuff...
```

To launch the authentication activities, you need to create the appropriate launch intents:

```
    private void fetchGitHubAuth()
    {
        Intent intent = new Intent(this, OAuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        intent.putExtra(OAuthActivity.CONSUMER_KEY, MY_GITHUB_CLIENT_KEY);
        intent.putExtra(OAuthActivity.CONSUMER_SECRET, MY_GITHUB_CLIENT_SECRET);
        intent.putExtra(OAuthActivity.REQUESTER, "github");
        intent.putExtra(OAuthActivity.CALLBACK_URL, "http://tech.cbits.northwestern.edu/oauth/github");
        intent.putExtra(OAuthActivity.LOG_URL, MY_LOG_ENDPOINT);
        intent.putExtra(OAuthActivity.HASH_SECRET, MY_HASH_SECRET);
        
        this.startActivity(intent);
    }

    private void fetchFitbitAuth() 
    {
        Intent intent = new Intent(this, OAuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        intent.putExtra(OAuthActivity.CONSUMER_KEY, MY_FITBIT_CLIENT_KEY);
        intent.putExtra(OAuthActivity.CONSUMER_SECRET, MY_FITBIT_CLIENT_SECRET);
        intent.putExtra(OAuthActivity.REQUESTER, "fitbit");
        intent.putExtra(OAuthActivity.CALLBACK_URL, "http://tech.cbits.northwestern.edu/oauth/fitbit");
        intent.putExtra(OAuthActivity.LOG_URL, MY_LOG_ENDPOINT);
        intent.putExtra(OAuthActivity.HASH_SECRET, MY_HASH_SECRET);
        
        this.startActivity(intent);
    }

    private void fetchJawboneAuth() 
    {
        Intent intent = new Intent(this, OAuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        intent.putExtra(OAuthActivity.CONSUMER_KEY, MY_JAWBONE_CLIENT_KEY);
        intent.putExtra(OAuthActivity.CONSUMER_SECRET, MY_JAWBONE_CLIENT_SECRET);
        intent.putExtra(OAuthActivity.REQUESTER, "jawbone");
        intent.putExtra(OAuthActivity.CALLBACK_URL, "https://tech.cbits.northwestern.edu/oauth/jawbone");
        intent.putExtra(OAuthActivity.LOG_URL, MY_LOG_ENDPOINT);
        intent.putExtra(OAuthActivity.HASH_SECRET, MY_HASH_SECRET);
        
        this.startActivity(intent);
    }
```

The `LOG_URL` and `HASH_SECRET` parameters are used by the Anthracite logging library when enabled. Starting activities through these intents will open an embedded web view that manages the OAuth processes. At the end of the process, two new keys are added to the shared preferences, `oauth_SERVICE_secret` and `oauth_SERVICE_token` which contain the specific tokens for the user. `SERVICE` will be one of `fitbit`, `jawbone`, or `github`.

Your app is responsible for managing and deleting these tokens if the user decides to revoke authorization.

Once your user has authenticated with the service, you can request a `JSONObject` from the service using one of several convenience functions:

```
    FitbitApi.fetch(Uri uri);
    JawboneApi.fetch(Context context, Uri uri, String userAgent);
    GitHubApi.fetch(Uri uri);
    GitHubApi.fetchAll(Uri uri); // Returns a JSONArray object instead
```

For example, if you wanted to fetch the user's Jawbone goals:

```
    try
    {
        String token = prefs.getString("oauth_jawbone_token", "");
        
        Keystore.put(JawboneApi.USER_TOKEN, token);
        
        JSONObject body = JawboneApi.fetch(this._context, Uri.parse("https://jawbone.com/nudge/api/v.1.1/users/@me/goals"), "My App 1.0");

        int stepGoal = body.getJSONObject("data").getInt("move_steps");
        
        body = JawboneApi.fetch(this._context, Uri.parse("https://jawbone.com/nudge/api/v.1.1/users/@me/moves"), "My App 1.0");

        int steps = body.getJSONObject("data").getJSONArray("items").getJSONObject(0).getJSONObject("details").getInt("steps");
        
        if (steps >= stepGoal)
            metGoals.add(this._context.getString(R.string.feat_jawbone_steps));
    } 
    catch (JSONException e) 
    {
        LogManager.getInstance(me._context).logException(e);
    }
    catch (OAuthException e)
    {
        LogManager.getInstance(me._context).logException(e);
    }
    catch (IllegalArgumentException e)
    {
        LogManager.getInstance(me._context).logException(e);
    } 
```

Consult the relevant developer documentation for details about the structure of each site's payloads.

##Help & Feedback##

If you have any questions or feedback, please send it to [Chris Karr](mailto:c-karr@northwestern.edu) and he'll get back to you as time allows. This is an early release of this library, so expect some rough edges in the current version and evolution in API as it develops.
