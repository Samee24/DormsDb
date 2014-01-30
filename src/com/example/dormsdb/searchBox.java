package com.example.dormsdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class searchBox extends ListActivity{
	
public static final String TAG = searchBox.class.getSimpleName();
	
protected ProgressBar mProgressBar;	
protected JSONObject mRoomInfo;
public String ld;
public String ad;
public String pd;
public String sfd;
public ArrayList<HashMap <String, String>> roomPosts;


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view);
		
		Intent intent = getIntent();
		
		   if (intent.getExtras() != null) {
		   	 
			 
		    ld = intent.getStringExtra("LD");
		    ad = intent.getStringExtra("AD");
		    pd = intent.getStringExtra("PD");
		    sfd = intent.getStringExtra("SFD");
		    Log.v("DATA", ld + ad + pd);
		   }
		
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		
		if (isNetworkAvailable()) {
			mProgressBar.setVisibility(View.VISIBLE);
			getRoomData GetRoomData = new getRoomData ();
			GetRoomData.execute();
		}
		else { Toast.makeText(searchBox.this, "lolz", Toast.LENGTH_LONG).show();
		}
		
		    
		
		
		

}
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Intent intent1 = new Intent (searchBox.this, ExtraInfo.class);
		Map<String, String> map = roomPosts.get(position);
		
		String hall = map.get("hall");
		String room = map.get("room");
		String subfree = map.get("subfree");
		String raters = map.get("raters");
		String types = map.get("type");
		String ratings = map.get("rating");
		String clusters = map.get("cluster");
		
		intent1.putExtra("hall", hall);
		intent1.putExtra("room", room);
		intent1.putExtra("subfree", subfree);
		intent1.putExtra("type", types);
		intent1.putExtra("rating", ratings);
		intent1.putExtra("raters", raters);
		intent1.putExtra("cluster", clusters);
		
		searchBox.this.startActivity(intent1);
	
		
		
	}
	
	private void handleResponse() {
		mProgressBar.setVisibility(View.INVISIBLE);
		if (mRoomInfo == null) {
			//TODO
		}
		else {
			
			try {				
			
			JSONArray jsonPosts = mRoomInfo.getJSONArray("rooms");
			roomPosts = new ArrayList<HashMap <String, String>>();
			for (int i = 0;i<jsonPosts.length();i++) {
				JSONObject roomPost = jsonPosts.getJSONObject(i);
				
				JSONObject rm1 = roomPost.getJSONObject("room");
				
				String hall = rm1.getString("hall");
				hall = Html.fromHtml(hall).toString();
				
				String room = rm1.getString("room");				
				room = Html.fromHtml(room).toString();
				
				String subfree = rm1.getString("subfree");
				subfree = Html.fromHtml(subfree).toString();
				
				String type = rm1.getString("type");
				type = Html.fromHtml(type).toString();
				
				String rating = rm1.getString("rating");
				rating = Html.fromHtml(rating).toString();
				
				String raters = rm1.getString("raters");
				raters = Html.fromHtml(raters).toString();
				
				String cluster = rm1.getString("cluster");
				cluster = Html.fromHtml(cluster).toString();
				
				HashMap <String, String> post = new HashMap <String, String>();
				post.put("hall", hall);
				post.put("room", room);
				post.put("subfree", subfree);
				post.put("type", type);
				post.put("rating", rating);
				post.put("raters", raters);
				post.put("cluster", cluster);
				
				
				roomPosts.add(post);
			}
				
				int[] ids = {android.R.id.text1, android.R.id.text2};
				String[] keys = {"hall", "room"};
				SimpleAdapter adapter = new SimpleAdapter(this, 
						roomPosts,android.R.layout.simple_list_item_2, keys, ids);
				
				setListAdapter(adapter);
				 
				
			}
			catch (JSONException e){
				Log.e(TAG, "Exception caught:" + e);
				
			}
		}
			
		}
		
	
	
	private boolean isNetworkAvailable() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		
		boolean isAvailable = false;
		if (networkInfo != null && networkInfo.isConnected()) {
			isAvailable = true;
		}
		return isAvailable;
	}

 private class getRoomData extends AsyncTask<Object, Void, JSONObject>{

	    
	    
	   
	 
	 int responseCode = -1;
	 JSONObject jsonResponse = null;
	 
	 @Override 
	 protected JSONObject doInBackground(Object...arg0) {
		 
		try {
			//DefaultHttpClient defaultClient = new DefaultHttpClient();
			//HttpGet httpGetRequest = new HttpGet("https://dormsdb.alexthemitchell.com/api.php?format=JSON" 
			//+ ld + ad + pd);
			Log.v(TAG, ld + ad + pd );
			//HttpResponse connection = defaultClient.execute(s_url);
			URL url = new URL("https://dormsdb.alexthemitchell.com/api.php?format=JSON" 
					+ ld + ad + pd + sfd);
			
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){

				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					// TODO Auto-generated method stub
					return true;
				}
			});
			HttpsURLConnection s_url = (HttpsURLConnection) url.openConnection();
			//HttpResponse connection = defaultClient.execute((HttpUriRequest) s_url);
			
			Log.v(TAG, ld + ad + pd + sfd );
			
			
			 
			 responseCode = s_url.getResponseCode();
			 
			 if (responseCode == HttpURLConnection.HTTP_OK) {
				  BufferedReader reader = new BufferedReader(new InputStreamReader(s_url.getInputStream(), "UTF-8"));
				  String responseData = reader.readLine();
					
					
					jsonResponse = new JSONObject(responseData);
					reader.close();
				 
				 
			 }
			 else {responseCode = s_url.getResponseCode();
				Log.i(TAG, "Code: " + responseCode);
				 
			 }
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "Exception caught: " + e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "Exception caught: " + e);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "Exception caught: " + e);
		}
		 
		 
		 
		return jsonResponse;
		 
	 }

	@Override
	protected void onPostExecute (JSONObject result) {
		mRoomInfo = result;
		handleResponse();
		
	}

	

}
	
}