package dspot.utils;

public class SpotShortInfo {
	
	public SpotShortInfo(String name, String address, int id){
		this.name = name;
		this.address = address;
		this.id = id;
	}

	public SpotShortInfo(String name, String address, int id, String photo ){
		this.name = name;
		this.address = address;
		this.id = id;
		this.photoURL = photo;
	}
	
	
	public SpotShortInfo(String name, String address, int id, byte[] photo ){
		this.name = name;
		this.address = address;
		this.id = id;
		this.photoBlob = photo;
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


	public byte[] getPhotoBlob() {
		return photoBlob;
	}

	
	private String name;
	private String address;
	private int id;
	private String photoURL;
	private byte[] photoBlob;

}
