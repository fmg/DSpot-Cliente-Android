package dspot.client;

public class SpotShortInfo {
	
	public SpotShortInfo(String name, String address){
		this.name = name;
		this.address = address;
	}
	
	public String getName() {
		return name;
	}
	
	public String getAddress() {
		return address;
	}
	
	private String name;
	private String address;

}
