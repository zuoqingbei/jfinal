package com.ulab.model;

public class Block {
	private String sim;
	private String lon;
	private String lat;
	public Block(){
		super();
	}
	public Block(String sim, String lon, String lat) {
		this.sim = sim;
		this.lon = lon;
		this.lat = lat;
	}
	public String getSim() {
		return sim;
	}
	public void setSim(String sim) {
		this.sim = sim;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
}
