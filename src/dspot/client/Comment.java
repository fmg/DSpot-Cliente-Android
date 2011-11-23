package dspot.client;

public class Comment {
	public String getUsername() {
		return username;
	}
	public String getComment() {
		return comment;
	}
	public int getRating() {
		return rating;
	}
	public Comment(String u , String c, int r){
		this.username = u;
		this.comment = c;
		this.rating = r;
	}
	String username;
	String comment;
	int rating;
}
