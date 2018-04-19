/* Copyright © 2018 by Northwestern University. All Rights Reserved. */

package edu.northwestern.cbits.xsi.facebook;

import android.support.v7.app.AppCompatActivity;

public class FacebookLoginActivity extends AppCompatActivity
{
/*
    public static final String APP_ID = "facebook_app_id";

    private boolean _inited = false;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.layout_facebook_activity);
    }

    protected void onResume()
    {
        super.onResume();

        if (this._inited)
            return;

        this._inited = true;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Editor e = prefs.edit();
        e.remove(FacebookApi.TOKEN);
        e.apply();

        final FacebookLoginActivity me = this;

        String appId = this.getIntent().getStringExtra(FacebookLoginActivity.APP_ID);

        Session.Builder builder = new Session.Builder(this);

        Session session = builder.setApplicationId(appId).build();

        session.addCallback(new Session.StatusCallback()
        {
            public void call(Session session, SessionState state, Exception exception)
            {
                if (SessionState.OPENED == state || SessionState.OPENED_TOKEN_UPDATED == state)
                    me.onSessionStateChange(session, state, exception);
                else
                    session.addCallback(this);
            }
        });

        Session.OpenRequest request = new Session.OpenRequest(this);
        request.setPermissions("read_stream");
        request.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
        request.setDefaultAudience(SessionDefaultAudience.ONLY_ME);

        Session.setActiveSession(session);

        session.openForRead(request);
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception)
    {
        if (state.isOpened())
            this.go(session);

        final FacebookLoginActivity me = this;

        if (Session.getActiveSession() == null || Session.getActiveSession().isClosed())
        {
            Session.openActiveSession(this, true, new Session.StatusCallback()
            {
                public void call(Session session, SessionState state, Exception exception)
                {
                    session = Session.getActiveSession();

                    me.onSessionStateChange(session, state, exception);
                }
            });
        }
        else
            this.go(session);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (Session.getActiveSession() != null)
            Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    private void go(Session session)
    {
        final FacebookLoginActivity me = this;

        String token = session.getAccessToken();

        if (token != null && token.trim().length() > 0)
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            Editor e = prefs.edit();
            e.putString(FacebookApi.TOKEN, token);
            e.apply();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder = builder.setTitle(R.string.title_facebook_success);
            builder = builder.setMessage(R.string.message_facebook_success);
            builder = builder.setPositiveButton(R.string.confirm_facebook_success,
                    new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            me.finish();
                        }
                    });

            builder.create().show();
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder = builder.setTitle(R.string.title_facebook_failure);
            builder = builder.setMessage(R.string.message_facebook_failure);
            builder = builder.setPositiveButton(R.string.confirm_facebook_success,
                    new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            me.finish();
                        }
                    });

            builder = builder.setNegativeButton(R.string.confirm_facebook_try_again,
                    new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            me.finish();

                            Intent intent = new Intent(me, FacebookLoginActivity.class);
                            me.startActivity(intent);
                        }
                    });

            builder.create().show();
        }
    }
    */
}
