package dspot.client;

public class SpotShortInfo {
	
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
	
	private String name;
	private String address;
	private int id;

}
