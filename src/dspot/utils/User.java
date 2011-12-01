package dspot.utils;


public class User {

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

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
	
	
	public User(int id, String name, int check){
		this.id = id;
		this.name = name;
		if(check == 0)
			this.isSelected = false;
		else
			this.isSelected = true;
	}

	public User(){}


	private String username;
	private String name;
	private String photo;
	private String email;
	private int id;
	private boolean isConnected = true;
	private boolean isSelected = false;
	
}
