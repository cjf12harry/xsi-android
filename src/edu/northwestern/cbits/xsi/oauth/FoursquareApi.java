package edu.northwestern.cbits.xsi.oauth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import edu.northwestern.cbits.xsi.R;
import edu.northwestern.cbits.xsi.logging.LogManager;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.util.Log;

public class FoursquareApi extends DefaultApi20 
{
	public static final String CONSUMER_KEY = "foursquare_consumer_key";
	public static final String CONSUMER_SECRET = "foursquare_consumer_secret";
	public static final String USER_SECRET = "foursquare_user_secret";
	public static final String USER_TOKEN = "foursquare_user_token";
	
	private static HashSet<String> _exclude = null;
	
	public static class Place
	{
		public double latitude = 0.0;
		public double longitude = 0.0;
		public String address = null;
		public String name = null;
		public Uri imageUri = null;
		public String id = null;
		public Uri infoUri = null;
		
		public int socialFlags = -1; 
		public int costFlags = -1; 
		public int purposeFlags = -1; 
		
		public Uri getVenueImage(Context context)
		{
			if (this.imageUri != null)
				return this.imageUri;
			
			this.imageUri = FoursquareApi.fetchVenuePhotoUri(context, this.id);
		
			return this.imageUri;
		}
	}

	public String getAccessTokenEndpoint() 
	{
		return null;
	}

	public String getAuthorizationUrl(OAuthConfig arg0) 
	{
		return null;
	}
	
	public static JSONObject anonymousFetch(Uri uri)
	{
		final OAuthRequest request = new OAuthRequest(Verb.GET, uri.toString());

		Response response = request.send();

		try 
		{
			return new JSONObject(response.getBody());
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}

	public static JSONObject fetch(Uri uri)
	{
		Token accessToken = new Token(Keystore.get(FoursquareApi.USER_TOKEN), Keystore.get(FoursquareApi.USER_SECRET));
    	
		final OAuthRequest request = new OAuthRequest(Verb.GET, uri.toString());

    	ServiceBuilder builder = new ServiceBuilder();
    	builder = builder.provider(FitbitApi.class);
    	builder = builder.apiKey(Keystore.get(FoursquareApi.CONSUMER_KEY));
    	builder = builder.apiSecret(Keystore.get(FoursquareApi.CONSUMER_SECRET));

    	final OAuthService service = builder.build();

		service.signRequest(accessToken, request);

		Response response = request.send();

		try 
		{
			return new JSONObject(response.getBody());
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static List<Place> searchPlaces(Context context, double latitude, double longitude, String query)
	{
		ArrayList<Place> places = new ArrayList<Place>();
		
		String clientId = Keystore.get(FoursquareApi.CONSUMER_KEY);
		String clientSecret = Keystore.get(FoursquareApi.CONSUMER_SECRET);
		
		String uri = context.getString(R.string.uri_foursquare_explore, clientId, clientSecret, latitude, longitude, query);
		
		JSONObject response = FoursquareApi.anonymousFetch(Uri.parse(uri));
		
		if (FoursquareApi._exclude == null)
		{
			FoursquareApi._exclude = new HashSet<String>();
			
			String[] excluded = context.getResources().getStringArray(R.array.foursquare_excluded_categories);
			
			for (String exclude : excluded)
				FoursquareApi._exclude.add(exclude);
		}
		
		try 
		{
			ArrayList<JSONObject> venues = new ArrayList<JSONObject>();
					
			if (response.getJSONObject("response").has("venues"))
			{
				JSONArray venuesArray = response.getJSONObject("response").getJSONArray("venues");
				
				for (int i = 0; i < venuesArray.length(); i++)
				{
					venues.add(venuesArray.getJSONObject(i));
				}
			}
			else if (response.getJSONObject("response").has("groups"))
			{
				JSONArray groups = response.getJSONObject("response").getJSONArray("groups");
				
				for (int i = 0; i < groups.length(); i++)
				{
					JSONObject group = groups.getJSONObject(i);
					
					if (group.has("items"))
					{
						JSONArray items = group.getJSONArray("items");
						
						for (int j = 0; j < items.length(); j++)
						{
							JSONObject item = items.getJSONObject(j);
							
							venues.add(item.getJSONObject("venue"));
						}
					}
						
				}
			}
			
			for (JSONObject venue : venues)
			{
				boolean include = true;

				JSONArray categories = venue.getJSONArray("categories");
				for (int j = 0; j < categories.length() && include == true; j++)
				{
					JSONObject category = categories.getJSONObject(j);
					
					if (FoursquareApi._exclude.contains(category.getString("shortName")))
						include = false;
				}
				
				if (include)
				{
					JSONObject location = venue.getJSONObject("location");

					Place place = new Place();
					
					place.name = venue.getString("name");
					
					place.latitude = location.getDouble("lat");
					place.longitude = location.getDouble("lng");
					place.id = venue.getString("id");
					
					place.infoUri = Uri.parse("https://foursquare.com/v/foursquare-hq/" + place.id);
					
					StringBuffer address = new StringBuffer();
					
					JSONArray addressLines = location.getJSONArray("formattedAddress");
					
					for (int j = 0; j < addressLines.length(); j++)
					{
						if (address.length() > 0)
							address.append(System.getProperty("line.separator"));
						
						address.append(addressLines.getString(j));
					}
					
					place.address = address.toString();
				
					places.add(place);
				}
			}
		} 
		catch (JSONException e) 
		{
			LogManager.getInstance(context, "", "").logException(e);
		}
		
/*        LocationManager locations = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        
        final Location here = locations.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
		
		Collections.sort(places, new Comparator<Place>()
		{
			public int compare(Place placeOne, Place placeTwo) 
			{
				Location one = new Location(LocationManager.PASSIVE_PROVIDER);
				one.setLatitude(placeOne.latitude);
				one.setLongitude(placeOne.longitude);

				Location two = new Location(LocationManager.PASSIVE_PROVIDER);
				two.setLatitude(placeTwo.latitude);
				two.setLongitude(placeTwo.longitude);

				if (one.distanceTo(here) > two.distanceTo(here))
					return 1;
				else if (one.distanceTo(here) < two.distanceTo(here))
					return -1;
				
				return 0;
			}
		}); */

		return places;
	}

	public static Uri fetchVenuePhotoUri(Context context, String id) 
	{
		String clientId = Keystore.get(FoursquareApi.CONSUMER_KEY);
		String clientSecret = Keystore.get(FoursquareApi.CONSUMER_SECRET);

		JSONObject response = FoursquareApi.anonymousFetch(Uri.parse(context.getString(R.string.uri_foursquare_venue_photo, id,  clientId, clientSecret)));
		
		try
		{
			Log.e("XSI", "IMAGE JSON: " + response.toString(2));
			
			if (response.getJSONObject("response").has("photos"))
			{
				JSONObject photos = response.getJSONObject("response").getJSONObject("photos");
				JSONArray items = photos.getJSONArray("items");
				
				if (items.length() > 0)
				{
					JSONObject item = items.getJSONObject(0);
					
					return Uri.parse(item.getString("prefix") + "width640" + item.getString("suffix"));
				}
			}
		} 
		catch (JSONException e) 
		{
			LogManager.getInstance(context, "", "").logException(e);
		}

		return null;
	}
}
