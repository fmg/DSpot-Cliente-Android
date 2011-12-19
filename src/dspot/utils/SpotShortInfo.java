package dspot.utils;

public class SpotShortInfo {
	
	public String toString(){
		return name;
	}
	
	
	public SpotShortInfo(String name, String address, int id, int rating){
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

	private String name;
	private String address;
	private int id;
	private String photoURL;
	private int rating;

}
