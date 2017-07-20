package com.ulab.model;
/**
 * 
 * @className BaiduLocation.java
 * @time   2017年7月19日 下午9:12:41
 * @author zuoqb
 * @todo   调用百度api将GPS坐标转换为区块坐标及百度坐标
 */
public class BaiduLocation {
	private String sim;//出租车sim
	private String x;//图块坐标x值
	private String y;//图块坐标y值
	private String lng;//百度经度坐标
	private String lat;//百度维度坐标
	public String getSim() {
		return sim;
	}
	public void setSim(String sim) {
		this.sim = sim;
	}
	public String getX() {
		return x;
	}
	public void setX(String x) {
		this.x = x;
	}
	public String getY() {
		return y;
	}
	public void setY(String y) {
		this.y = y;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
}
