package dspot.utils;

import java.util.ArrayList;

public class SpotShortInfo {
	
	public SpotShortInfo(String name, String address, int id, int rating){
		spots = new ArrayList<String>();
		this.name = name;
		this.address = address;
		this.id = id;
		this.rating = rating;
	}
	
	public String getName() {
		return name;
	}
	
	public String getAddress() {
		return address;
	}
	
	public int getId(){
		return id;
	}
	
	
	public String getPhotoURL() {
		return photoURL;
	}
	
	
	public int getRating() {
		return rating;
	}
	
	
	public double getLatitude() {
		return latitude;
	}


	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}


	public double getLongitude() {
		return longitude;
	}


	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}


	public String toString(){
		return name;
	}
	
	
	public void addSport(String s){
		spots.add(s);
	}
	
	
	public String getSportsFormated(){
		String ret = "";
		
		for(int i = 0; i < spots.size(); i++){
			if(i == 0)
				ret += spots.get(i);
			else
				ret += ", " + spots.get(i);
		}
		
		
		return ret;
	}

	private String name;
	private String address;
	private int id;
	private String photoURL;
	private int rating;
	private double latitude, longitude;
	private ArrayList<String> spots;
	public int count_click = 0;

}
