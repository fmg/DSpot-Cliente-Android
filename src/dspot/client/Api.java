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

import dspot.utils.Comment;
import dspot.utils.MyLocation;
import dspot.utils.Sport;
import dspot.utils.SpotFullInfo;
import dspot.utils.SpotShortInfo;
import dspot.utils.User;

import android.app.Application;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;

public class Api extends Application {
	
	public static String cookie;
	public static String IP = "http://172.30.1.57:3000";
	
	public static User user = new User();
	public static int radious;
	
	public static dspot.client.database.DatabaseAdapter dbAdapter;
	
	static public Facebook facebook = new Facebook("177730402315866");
	static public final String FILENAME = "DSpot_data";
    static public SharedPreferences mPrefs;
    public static AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);
    
    public static int last_visited_spot = 0;
    
    public static final int numberCommentsPerPage = 5;
	
    
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
	
	
	
	public boolean getProfile() throws ClientProtocolException, IOException, JSONException{
		
		final HttpClient httpClient =  new DefaultHttpClient();
		 HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 3000);
		HttpResponse response=null;
        	
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
        	
        	//TODO:corrigir link
        	String photoURL = IP+"/public/system/avatares/"+ user.getId()+ "/medium/"+messageReceived.getString("avatar_file_name");
        	user.setPhoto(photoURL);
        	
        	//elimina tudo do user
        	resetUserInfo();
        	
        	createUserInfo(user.getId(), user.getUsername(), user.getName(), user.getEmail(), user.getPhoto());
        	
        	//cria amigos
        	JSONArray friends = messageReceived.getJSONArray("friends");
        	createFriends(friends,user.getId());
        	
        	
        	//cria favoritos
        	JSONArray favourites = messageReceived.getJSONArray("spot_favs");
        	createFavourites(favourites,user.getId());
        	
        	
        	
        	
  	
        	return true;
        }	
        
        return false;
		
	}
	
	
	
	public int updateApplicationDefinitions() throws ClientProtocolException, IOException, JSONException{
		final HttpClient httpClient =  new DefaultHttpClient();
		 HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 3000);
		HttpResponse response=null;
       
       	
           String url = IP + "/version/show";       
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
			   	
			   	
			   	//TODO: ver cena das versoes
			   	
			   	JSONArray cities = messageReceived.getJSONArray("localidades");
			   	createLocations(cities);
			   	
			   	JSONArray sports = messageReceived.getJSONArray("sports");
			   	createSports(sports);
			   	
			   	
           }
       
		
		return 0;
	}
	
	
	
	public ArrayList<SpotShortInfo> getSpotsByLocation(int id) throws ClientProtocolException, IOException, JSONException{
		
		ArrayList<SpotShortInfo> spotlist = new ArrayList<SpotShortInfo>();
		
		final HttpClient httpClient =  new DefaultHttpClient();
		 HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 3000);
		HttpResponse response=null;
       
       	
           String url = IP + "/search_json";       
           System.out.println(url);
           
           HttpPost httpPost = new HttpPost(url);         
           JSONObject jsonuser=new JSONObject();
           
           httpPost.setHeader("Accept", "application/json");
           httpPost.setHeader("Cookie", cookie);
           	
   			jsonuser.put("local_id", id);
   			
   			
   			ArrayList<Integer> sel_sports = getSelectedSports();
   			
   			String sports ="";
   			for(int i = 0; i < sel_sports.size(); i++){
   				if(i == sel_sports.size()-1){
   					sports += sel_sports.get(i);
   					
   				}else
   					sports+= sel_sports.get(i) + ",";
   			}
   			
   			jsonuser.put("sport_ids", sports);

   				
   			String POSTText = jsonuser.toString();
   			StringEntity entity; 
       	 
   			entity = new StringEntity(POSTText, "UTF-8");
   			BasicHeader basicHeader = new BasicHeader(HTTP.CONTENT_TYPE, "application/json");
           httpPost.getParams().setBooleanParameter("http.protocol.expect-continue", false);
           entity.setContentType(basicHeader);
           httpPost.setEntity(entity);
           response = httpClient.execute(httpPost);
               
           
           if(response.getStatusLine().getStatusCode() == 200){
        	   
			   InputStream instream = response.getEntity().getContent();
			   String tmp = read(instream);
			
			    JSONArray messageReceived = new JSONArray(tmp.toString());
			   	System.out.println(messageReceived.toString());
			   	
			   	for(int i = 0; i<messageReceived.length(); i++){
			   		JSONObject spot = messageReceived.getJSONObject(i);
			   		
			   		SpotShortInfo ssi = new SpotShortInfo(spot.getString("name"), spot.getString("address"), spot.getInt("id"));
			   		
			   		spotlist.add(ssi);
			   		
			   	}
	   	
           }
		
		return spotlist;
	}
	
	
	
	public int sendComment(int spot_id, int rating, String body) throws ClientProtocolException, IOException, JSONException{
		
		ArrayList<SpotShortInfo> spotlist = new ArrayList<SpotShortInfo>();
		
		final HttpClient httpClient =  new DefaultHttpClient();
		 HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 3000);
		HttpResponse response=null;
       
       	
           String url = IP + "/comments/create";       
           System.out.println(url);
           
           HttpPost httpPost = new HttpPost(url);         
           JSONObject jsonuser=new JSONObject();
           
           httpPost.setHeader("Accept", "application/json");
           httpPost.setHeader("Cookie", cookie);
           System.out.println(cookie);	
           
   			jsonuser.put("value", rating);
   			jsonuser.put("body", body);
   			jsonuser.put("spot_id", spot_id);
   		
   				
   			String POSTText = jsonuser.toString();
   			StringEntity entity; 
       	 
   			entity = new StringEntity(POSTText, "UTF-8");
   			BasicHeader basicHeader = new BasicHeader(HTTP.CONTENT_TYPE, "application/json");
           httpPost.getParams().setBooleanParameter("http.protocol.expect-continue", false);
           entity.setContentType(basicHeader);
           httpPost.setEntity(entity);
           response = httpClient.execute(httpPost);
                          
           if(response.getStatusLine().getStatusCode() == 201){	   
        	   return 0;
           }
		
		return -1;
	}
	
	
	
	public ArrayList<SpotShortInfo> getSpotsByName(String keyword) throws ClientProtocolException, IOException, JSONException{
		
		ArrayList<SpotShortInfo> spotlist = new ArrayList<SpotShortInfo>();
		
		final HttpClient httpClient =  new DefaultHttpClient();
		 HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 3000);
		HttpResponse response=null;
       
       	
           String url = IP + "/search_key?keywords="+keyword;       
           System.out.println(url);
           
           
           HttpGet httpget = new HttpGet(url);
           
           httpget.setHeader("Accept", "application/json");
           httpget.setHeader("Cookie", cookie);
           
           response = httpClient.execute(httpget);
               
           
           if(response.getStatusLine().getStatusCode() == 200){
        	   
			   InputStream instream = response.getEntity().getContent();
			   String tmp = read(instream);
			
			    JSONArray messageReceived = new JSONArray(tmp.toString());
			   	System.out.println(messageReceived.toString());
			   	
			   	for(int i = 0; i<messageReceived.length(); i++){
			   		JSONObject spot = messageReceived.getJSONObject(i);
			   		
			   		SpotShortInfo ssi = new SpotShortInfo(spot.getString("name"), spot.getString("address"), spot.getInt("id"));
			   		
			   		spotlist.add(ssi);
			   		
			   	}
	   	
           }
		
		return spotlist;
	}
	
	
	
	public SpotFullInfo getSpotsFullInfo(int id) throws ClientProtocolException, IOException, JSONException{
		
		SpotFullInfo spot = new SpotFullInfo();
				
		final HttpClient httpClient =  new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 3000);
		HttpResponse response=null;
		       
		       	
		String url = IP + "/spots/"+id;       
		System.out.println(url);
		   
		HttpGet httpget = new HttpGet(url);
		   
		httpget.setHeader("Accept", "application/json");
		httpget.setHeader("Cookie", cookie);
		   
		response = httpClient.execute(httpget);
		   
		System.out.println(response.getStatusLine().getStatusCode());
		   
		if(response.getStatusLine().getStatusCode() == 200){
			   
			InputStream instream = response.getEntity().getContent();
			String tmp = read(instream);
				
			JSONObject messageReceived = new JSONObject(tmp.toString());
			System.out.println(messageReceived.toString());
			
			
			JSONArray pictures = messageReceived.getJSONArray("pictures");
			for(int i = 0; i < pictures.length(); i++){
				String photo = IP + "/system/pictures/"+
								(pictures.getJSONObject(i)).getInt("id")+
								"/medium/" + 
								(pictures.getJSONObject(i)).getString("picture_file_name");
								
				spot.addPhoto(photo);
			}
			
			
			JSONArray sports = messageReceived.getJSONArray("sports");
			for(int i = 0; i < sports.length(); i++){
				spot.addSports((sports.getJSONObject(i)).getString("name"));
			}
			
			JSONArray comments = messageReceived.getJSONArray("comments10");
			for(int i = 0; i < comments.length(); i++){
				Comment c = new Comment((comments.getJSONObject(i)).getString("username"),
										(comments.getJSONObject(i)).getString("body"),
										(comments.getJSONObject(i)).getInt("value"));
				
				spot.addComment(c);
			}
			
			spot.setId(messageReceived.getInt("id"));
			spot.setName(messageReceived.getString("name"));
			spot.setAddress(messageReceived.getString("address"));
			spot.setLatitude(messageReceived.getDouble("latitude"));
			spot.setLongitude(messageReceived.getDouble("longitude"));
			spot.setLocation(((JSONObject)messageReceived.get("localidade")).getString("name"));
			spot.setPhoneNumber(messageReceived.getString("phone"));
			spot.setDescription(messageReceived.getString("description"));
			
			spot.setRating(messageReceived.getDouble("rating"));
			
			
			
		}
			
		return spot;
	}
	
	
	
	public ArrayList<Comment> getCommentsPage(int spot_id, int page) throws ClientProtocolException, IOException, JSONException{
		
		ArrayList<Comment> comments = new ArrayList<Comment>();
		
		final HttpClient httpClient =  new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 3000);
		HttpResponse response=null;
		       
		       	
		String url = IP + "/index_spot?id_spot="+spot_id+
									"&limit="+(page*numberCommentsPerPage);  
		
		System.out.println(url);
		   
		HttpGet httpget = new HttpGet(url);
		   
		httpget.setHeader("Accept", "application/json");
		httpget.setHeader("Cookie", cookie);
		   
		response = httpClient.execute(httpget);
		   
		System.out.println(response.getStatusLine().getStatusCode());
		   
		if(response.getStatusLine().getStatusCode() == 200){
			   
			InputStream instream = response.getEntity().getContent();
			String tmp = read(instream);
				
			JSONArray messageReceived = new JSONArray(tmp.toString());
			System.out.println(messageReceived.toString());
			
			
			for(int i = 0; i < messageReceived.length(); i++){
				JSONObject o = messageReceived.getJSONObject(i);
				Comment c = new Comment(((JSONObject)o.get("user")).getString("username"),
										o.getString("body"),
										o.getInt("value"));
				
				comments.add(c);
			}

		}
			
		return comments;
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
	
	public void resetDefinitions(){
		dbAdapter.open();
		dbAdapter.resetDefinitions();
		dbAdapter.close();
	}
	

	public void resetUserInfo(){
		dbAdapter.open();
		dbAdapter.resetUserInfo(user.getId());
		dbAdapter.close();
	}

	
	private void createUserInfo(int id, String username, String name, String email, String photo){
		dbAdapter.open();
		dbAdapter.createUser(id, username, name, email);
		dbAdapter.close();
	}
	
	
	public User getUserInfo(){
		dbAdapter.open();
		User u = dbAdapter.getUserInfo(user.getId());
		dbAdapter.close();
		
		return u;
	}
	
	
/////////////////////////////////////////////////////////////////////
	
	private void createSports(JSONArray sports){
		dbAdapter.open();
		
		for(int i = 0; i < sports.length(); i++){
			JSONObject sport;
			try {
				sport = sports.getJSONObject(i);
				dbAdapter.createSport(sport.getInt("id"), sport.getString("name"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		dbAdapter.close();
	}
	
	
	
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
	
	
	
	public ArrayList<Integer> getSelectedSports(){
		dbAdapter.open();
		ArrayList<Integer> ret = dbAdapter.getSelectedSports();
		dbAdapter.close();
		
		
		return ret;
	}
	
	
	
	/////////////////////////////////////////////////////////////////////
	
	
	private void createLocations(JSONArray cities){
		dbAdapter.open();
		
		for(int i = 0; i < cities.length(); i++){
			JSONObject city;
			try {
				city = cities.getJSONObject(i);
				dbAdapter.createLocation(city.getInt("id"), city.getString("name"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		dbAdapter.close();
	}
	
	
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
	
	
	private void createFriends(JSONArray friends, int user_id){
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
	
	private void createFavourites(JSONArray favoutires, int user_id){
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
	
	
	public ArrayList<SpotShortInfo> getFavourites(){
		
		dbAdapter.open();
		ArrayList<SpotShortInfo> fav = dbAdapter.getFavourites(user.getId());
		dbAdapter.close();
		
		return fav;
	}

}
