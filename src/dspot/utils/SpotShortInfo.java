package dspot.utils;

public class SpotShortInfo {

	public SpotShortInfo(String name, String address, int id, String photo ){
		this.name = name;
		this.address = address;
		this.id = id;
		this.photoURL = photo;
	}
	
	
	public SpotShortInfo(String name, String address, int id){
		this.name = name;
		this.address = address;
		this.id = id;
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

	private String name;
	private String address;
	private int id;
	private String photoURL;

}
