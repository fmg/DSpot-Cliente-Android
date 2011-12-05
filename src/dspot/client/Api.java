package dspot.client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;

import dspot.utils.MyLocation;
import dspot.utils.Sport;
import dspot.utils.User;

import android.app.Application;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;

public class Api extends Application {
	
	public static String cookie;
	public static String IP = "http://89.214.145.37:3000";
	
	public static User user = new User();
	public static int radious;
	
	public static dspot.client.database.DatabaseAdapter dbAdapter;
	
	static public Facebook facebook = new Facebook("177730402315866");
	static public final String FILENAME = "DSpot_data";
    static public SharedPreferences mPrefs;
    public static AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);
	
    
	/////////////////////////////////////////////////////////////////////
	//					CHAMADAS AO SERVIDOR						 ////
	/////////////////////////////////////////////////////////////////////
    
		
	public int login(String username, String password) throws JSONException, ClientProtocolException, IOException{
		
		if (username.length() == 0 || password.length() == 0) {
			return -1;
		}
		
		final HttpClient httpClient =  new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 3000);
		
		HttpResponse response=null;
               
        String url = IP + "/login"; 
        
        System.out.println(url);

        HttpPost httpPost = new HttpPost(url);         
        JSONObject jsonuser=new JSONObject();
        
        httpPost.setHeader("Accept", "application/json");
        	
		jsonuser.put("username", username);
		jsonuser.put("password", password);
				
		String POSTText = jsonuser.toString();
        StringEntity entity; 
    	 
		entity = new StringEntity(POSTText, "UTF-8");
		BasicHeader basicHeader = new BasicHeader(HTTP.CONTENT_TYPE, "application/json");
        httpPost.getParams().setBooleanParameter("http.protocol.expect-continue", false);
        entity.setContentType(basicHeader);
        httpPost.setEntity(entity);
        response = httpClient.execute(httpPost);
            
        int code = response.getStatusLine().getStatusCode();
        if (code == 200) {
        	cookie = response.getFirstHeader("Set-Cookie").getValue().toString();
        	user.setUsername(username);
        	System.out.println(cookie);
        	return 0;
        	 
        } else if (code == 401){
        	return -2;
        } else
        	return -3;
        
	}
	
	
	
	public boolean getProfile(){
		
		final HttpClient httpClient =  new DefaultHttpClient();
		 HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 3000);
		HttpResponse response=null;
        try {
        	
            String url = IP + "/users/profile";       
            System.out.println(url);
            
            HttpGet httpget = new HttpGet(url);
            
            httpget.setHeader("Accept", "application/json");
            httpget.setHeader("Cookie", cookie);
            
            response = httpClient.execute(httpget);
            
            if(response.getStatusLine().getStatusCode() == 200){
            	
                InputStream instream = response.getEntity().getContent();
                String tmp = read(instream);
                
            	
    	        JSONObject messageReceived = new JSONObject(tmp.toString());
            	System.out.println(messageReceived.toString());
            	
            	//guardar os dados comuns
            	user.setName(messageReceived.getString("name"));
            	user.setEmail(messageReceived.getString("email"));
            	user.setId(messageReceived.getInt("id"));
            	user.setPhoto(messageReceived.getString("avatar_file_name"));
            	
            	//elimina tudo do user
            	resetUserInfo();
            	
            	createUserInfo(user.getId(), user.getUsername(), user.getName(), user.getEmail(), user.getPhoto());
            	
            	//cria amigos
            	JSONArray friends = messageReceived.getJSONArray("friends");
            	createFriends(friends,user.getId());
            	
            	
            	//cria favoritos
            	JSONArray favourites = messageReceived.getJSONArray("spot_favs");
            	createFavourites(favourites,user.getId());
            	
            	//TODO:compor url da photo
            	//String photoURL = IP+"/public/system/avatares/"+ user.getId()+ "/medium/avatar_file_name
            	
            	
      	
            	return true;
            }	
            
        } catch (IOException ex) {
        	ex.printStackTrace();    	
        } catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
        
        return false;
		
	}

	
	public int registrate(String username, String pass, String nome, String email,String pictureURL){
		
		final HttpClient httpClient =  new DefaultHttpClient();
		 HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 3000);
		 
		 HttpResponse response=null;
		 
		 String url = IP + "/users";  
		 
		
         HttpPost httpPost = new HttpPost(url);         
         JSONObject jsonuser=new JSONObject();
         JSONObject jsonuserinfo=new JSONObject();
         httpPost.setHeader("Accept", "application/json");
         try {
        	
        	 
        	 jsonuserinfo.put("username", username);
        	 jsonuserinfo.put("password", pass);
        	 jsonuserinfo.put("password_confirmation", pass);
        	 jsonuserinfo.put("name", nome);
        	 jsonuserinfo.put("email", email);
         	
         	//TODO: ver cena da foto
         	//jsonuser.put("picture", pictureURL);
         	jsonuser.put("user", jsonuserinfo);
        
            String POSTText = jsonuser.toString();
            System.out.println(POSTText);
            
            StringEntity entity = new StringEntity(POSTText, "UTF-8");
			BasicHeader basicHeader = new BasicHeader(HTTP.CONTENT_TYPE, "application/json");
	        httpPost.getParams().setBooleanParameter("http.protocol.expect-continue", false);
	        entity.setContentType(basicHeader);
	        httpPost.setEntity(entity);
	        response = httpClient.execute(httpPost);
         
	        System.out.println(url);
	        
	        if(response.getStatusLine().getStatusCode() == 201){
            	
	        	 System.out.println("success");
            	
            	 return 0;
            	 
            }else{
            	System.out.println("erro -> "+ response.getStatusLine().getStatusCode());
            	return -1;
            }
         } catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return -3;
         } catch (ClientProtocolException e) {
			e.printStackTrace();
			return -3;
         } catch (IOException e) {
			e.printStackTrace();
			return -3;
         } catch (JSONException e) {
			e.printStackTrace();
			return -3;
		}

	}
	
	
	
	public boolean logout() {
		
		final HttpClient httpClient =  new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 3000);
		
		HttpResponse response = null;
        try {
        	
            String url = IP + "/logout";  
            
            System.out.println(url);
 
            HttpDelete httdel = new HttpDelete(url);
            
            
            httdel.setHeader("Cookie", cookie);
            httdel.setHeader("Accept", "application/json");
            
            response = httpClient.execute(httdel);
            
            if(response.getStatusLine().getStatusCode() == 200){
            	System.out.println("fim");
            	cookie = "";     	
            	
            	 return true;
            	 
            }else
            	return false;
            
        } catch (IOException ex) {
        	ex.printStackTrace();
    	
        }
		return false;	
	}
	
	
	private String read(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
		for (String line = r.readLine(); line != null; line = r.readLine()) {
		    sb.append(line);
		}
		in.close();
		return sb.toString();
	}	
	
	
	
	
	
	
	/////////////////////////////////////////////////////////////////////
	//					CHAMADAS A BD								////
   /////////////////////////////////////////////////////////////////////

	
	public void populateBatabase(){
		dbAdapter.open();
		
		/*
		dbAdapter.createFriend(1, "Fernando",2);
		dbAdapter.createFriend(2, "André",2);
		dbAdapter.createFriend(3, "Nuno",2);
		dbAdapter.createFriend(4, "Gaspar",2);
		
		dbAdapter.createFriend(5, "Claudio",2);
		dbAdapter.createFriend(6, "Jorge",2);
		dbAdapter.createFriend(7, "Daniel",2);
		dbAdapter.createFriend(8, "Yuno",2);
		
		dbAdapter.createFriend(9, "Diogo",2);
		dbAdapter.createFriend(10, "José",2);
		dbAdapter.createFriend(11, "Filipe",2);
		dbAdapter.createFriend(12, "Francisco",2);
		*/
		
		
		dbAdapter.createLocation(1, "Porto");
		dbAdapter.createLocation(2, "Lisboa");
		dbAdapter.createLocation(3, "Viseu");
		dbAdapter.createLocation(4, "Vila Real");
		
		
		dbAdapter.createSport(1, "Soccer");
		dbAdapter.createSport(2, "Swimming");
		dbAdapter.createSport(3, "FootBall");
		dbAdapter.createSport(4, "BasketBall");

		dbAdapter.close();
	}
	
	public void resetDatabase(){
		dbAdapter.open();
		dbAdapter.resetAll();
		dbAdapter.close();
	}
	
	public void resetUserInfo(){
		dbAdapter.open();
		dbAdapter.resetUserInfo(user.getId());
		dbAdapter.close();
	}
	
	
	
	
	public void createUserInfo(int id, String username, String name, String email, String photo){
		dbAdapter.open();
		dbAdapter.createUser(id, username, name, email);
		dbAdapter.close();
	}
	
	
/////////////////////////////////////////////////////////////////////
	
	public ArrayList<Sport> getSports(){
		
		dbAdapter.open();
		ArrayList<Sport> sports = dbAdapter.getSports();
		dbAdapter.close();
		
		return sports;
	}
	
	
	public void updateSportsCheck(int id, int check){
		dbAdapter.open();
		dbAdapter.updateSportState(id, check);
		dbAdapter.close();
	}
	
	
	
	/////////////////////////////////////////////////////////////////////
	
	public ArrayList<MyLocation> getLocations(){
		
		dbAdapter.open();		
		ArrayList<MyLocation> locations = dbAdapter.getLocations();
		dbAdapter.close();
		
		return locations;
	}
	
	
	/////////////////////////////////////////////////////////////////////
	
	public ArrayList<User> getFriends(){	
		
		dbAdapter.open();
		ArrayList<User> friends = dbAdapter.getFriends(user.getId());
		dbAdapter.close();
		
		return friends;
	}
	
	public void updateFrienState(int id, int check){
		
		dbAdapter.open();
		dbAdapter.updateFrienState(id, check);
		dbAdapter.close();
		
		
	}
	
	
	public void createFriends(JSONArray friends, int user_id){
		dbAdapter.open();
		
		for(int i = 0; i < friends.length(); i++){
			JSONObject friend;
			try {
				friend = friends.getJSONObject(i);
				dbAdapter.createFriend(friend.getInt("id"), friend.getString("name"), user_id);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		
		dbAdapter.close();
	}
	
	
	
	//////////////////////////////////////////////////////////////////
	
	public void createFavourites(JSONArray favoutires, int user_id){
		dbAdapter.open();
		
		for(int i = 0; i < favoutires.length(); i++){
			JSONObject favoutire;
			try {
				favoutire = favoutires.getJSONObject(i);
				dbAdapter.createFavourite(favoutire.getInt("id"), favoutire.getString("name"), favoutire.getString("address"),user_id);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		
		dbAdapter.close();
	}

}
