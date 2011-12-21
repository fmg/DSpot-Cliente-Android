package dspot.utils;

public class Sport {
	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	private int id;
	private String name;
	private boolean isChecked;
	
	public Sport(int id, String name, int checked){
		this.id = id;
		this.name = name;
		if(checked> 0)
			this.isChecked = true;
		else
			this.isChecked = false;
			
	}
	
	
	public String toString(){
		return name;
	}
}
