package dspot.utils;

public class MyLocation {
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	private int id;
	private String name;
	
	public MyLocation(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	
	public String toString(){
		return name;
	}

}
