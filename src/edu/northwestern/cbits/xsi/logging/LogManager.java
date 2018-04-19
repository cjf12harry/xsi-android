/* Copyright © 2018 by Northwestern University. All Rights Reserved. */

package edu.northwestern.cbits.xsi.logging;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.preference.PreferenceManager;
import edu.northwestern.cbits.anthracite.Logger;

public class LogManager 
{
	private static final String STACKTRACE = "stacktrace";

	private static LogManager _sharedInstance = null;
	
	private Logger _logger = null;
	
	private Context _context = null;
	private String _logUrl = null;
	private String _hashSecret = null;
	
	public LogManager(Context context, String logUrl, String hashSecret) 
	{
		this._context = context;
		this._logUrl = logUrl;
		this._hashSecret = hashSecret;

		this._logger = Logger.getInstance(context, this.getUserId());
		this._logger.setEnabled(true);
		this._logger.setWifiOnly(true);
		this._logger.setLiberalSsl(true);
		this._logger.setDebug(true);
		this._logger.setUploadInterval(300000);
		this._logger.setUploadUri(Uri.parse(logUrl));
	}

	public void attemptUpload()
	{
		final LogManager me = this;
		
		Runnable r = new Runnable()
		{
			public void run() 
			{
				me._logger.attemptUploads(true);
			}
		};
		
		Thread t = new Thread(r);
		t.start();
	}
	
	public static LogManager getInstance(Context context, String logUrl, String hashSecret)
	{
		if (LogManager._sharedInstance != null)
			return LogManager._sharedInstance;
		
		if (context != null)
			LogManager._sharedInstance = new LogManager(context.getApplicationContext(), logUrl, hashSecret);
		
		return LogManager._sharedInstance;
	}
	
	public String getUserId()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this._context);

		String userId = prefs.getString("config_user_id", null);

		if (userId == null)
		{
			userId = "unknown-user";

			AccountManager manager = (AccountManager) this._context.getSystemService(Context.ACCOUNT_SERVICE);
			Account[] list = manager.getAccountsByType("com.google");

			if (list.length == 0) {
				list = manager.getAccounts();
			}

			if (list.length > 0)
				userId = list[0].name;

			Editor e = prefs.edit();
			e.putString("config_user_id", userId);
			e.apply();
		}
		
		return userId;
	}
	
	private String createHash(Context context, String string)
	{
		if (string == null)
			return null;

		String hash = null;
		
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest((this._hashSecret + string).getBytes("UTF-8"));

			hash = (new BigInteger(1, digest)).toString(16);

			while (hash.length() < 32)
			{
				hash = "0" + hash;
			}
		}
		catch (NoSuchAlgorithmException | UnsupportedEncodingException e)
		{
			LogManager.getInstance(context, this._logUrl, this._hashSecret).logException(e);
		}

		return hash;
	}

	public boolean log(String event, Map<String, Object> payload)
	{
		if (payload == null)
			payload = new HashMap<>();
		
		if (payload.containsKey(Logger.USER_ID) == false)
		{
			String userId = this.getUserId();
			
			if (event.startsWith("consent_") == false)
				userId = this.createHash(this._context, userId);
			
			payload.put(Logger.USER_ID, userId);
		}

		return this._logger.log(event, payload);
	}

	public void logException(Throwable e) 
	{
		e.printStackTrace();

		Map<String, Object> payload = new HashMap<>();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		
		e.printStackTrace(out);
		
		out.close();
		
		String stacktrace = baos.toString();
		
		payload.put(LogManager.STACKTRACE, stacktrace);
		
		this.log("java_exception", payload);
	}
}
