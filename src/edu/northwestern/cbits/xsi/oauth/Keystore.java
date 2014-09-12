package edu.northwestern.cbits.xsi.oauth;

import java.util.HashMap;

public class Keystore 
{
	private static HashMap<String, String> _store = new HashMap<String, String>();
	
	public static String get(String key)
	{
		return Keystore._store.get(key);
	}

	public static void put(String key, String value)
	{
		Keystore._store.put(key, value);
	}

	public static boolean contains(String key)
	{
		return Keystore._store.containsKey(key);
	}
}
