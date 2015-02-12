package edu.northwestern.cbits.xsi.facebook;

import android.content.Context;

import com.facebook.AccessToken;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

import java.util.List;

public class FacebookApi
{
    public static final String TOKEN = "facebook_auth_token";

    public static abstract class OnFriendsFetchedCallback
    {
        public abstract void onFriendsFetched(List<GraphUser> users);
    }

    public static abstract class OnRequestCallback
    {
        public abstract void onRequestCompleted(Response response);
    }

    public static void fetchRequest(Context context, final String token, final String path,  final FacebookApi.OnRequestCallback callback)
    {
        Session session = Session.getActiveSession();

        if (session == null)
        {
            AccessToken accessToken = AccessToken.createFromExistingAccessToken(token, null, null, null, null);

            Session.openActiveSessionWithAccessToken(context, accessToken, new Session.StatusCallback()
            {
                @Override
                public void call(final Session session, SessionState state, Exception exception)
                {
                    Request request = new Request(session, path, null, HttpMethod.GET, new Request.Callback()
                    {
                        public void onCompleted(Response response)
                        {
                            callback.onRequestCompleted(response);
                        }
                    });

                    request.executeAsync();
                }
            });
        }
        else
        {
            Request request = new Request(session, path, null, HttpMethod.GET, new Request.Callback()
            {
                public void onCompleted(Response response)
                {
                    callback.onRequestCompleted(response);
                }
            });

            request.executeAsync();
        }
    }
}
