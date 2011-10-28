package dspot.client;


public class User {
	
	public boolean isConnected() {
		return isConnected;
	}


	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public void setName(String name) {
		this.name = name;
	}


	public void setPhoto(String photo) {
		this.photo = photo;
	}


	public String getUsername() {
		return username;
	}


	public String getName() {
		return name;
	}




	public String getPhoto() {
		return photo;
	}
	
	
	public String toString(){
		return name;
	}


	private String username;
	private String name;
	private String photo;
	private boolean isConnected = true;
	
}
