package dspot.utils;

import java.util.ArrayList;

public class SpotFullInfo {


	private int id;
	private String name;
	private String address;
	private double latitude;
	private double longitude;
	private String phoneNumber;
	private String description;
	private String location;
	private double rating;
	private ArrayList<String> photosURL;
	private ArrayList<Comment> comments;
	private ArrayList<String> sports;
	private boolean canComment;
	
	
	
	public SpotFullInfo(){
		photosURL = new ArrayList<String>();
		comments = new ArrayList<Comment>();
		sports = new ArrayList<String>();
	}
	
	
	public void addPhoto(String photo){
		photosURL.add(photo);
	}
	
	
	public void addComment(Comment comment){
		comments.add(comment);
	}
	
	
	public void addSports(String sport){
		sports.add(sport);
	}
	
	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public double getLatitude() {
		return latitude;
	}


	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}


	public double getLongitude() {
		return longitude;
	}


	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}


	public String getPhoneNumber() {
		return phoneNumber;
	}


	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	public ArrayList<String> getPhotosURL() {
		return photosURL;
	}


	public ArrayList<Comment> getComments() {
		return comments;
	}


	public ArrayList<String> getSports() {
		return sports;
	}
	
	
	public double getRating() {
		return rating;
	}


	public void setRating(double rating) {
		this.rating = rating;
	}


	public boolean isCanComment() {
		return canComment;
	}


	public void setCanComment(boolean canComment) {
		this.canComment = canComment;
	}
}
