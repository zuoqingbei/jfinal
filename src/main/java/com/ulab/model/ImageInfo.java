package com.ulab.model;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("serial")
public class ImageInfo implements Serializable {
	private String key;//图片唯一标示
	private String leftTopLat;//左上角坐标
	private String leftTopLon;//左上角坐标
	private String width;//图片宽度
	private String height;//图片高度
	private String fullName;//图片全部名称
	private String fromat;//图片后缀
	private String path;
	public ImageInfo() {

	}

	public ImageInfo(Map<String,String> map) {
		String fullName=map.get("name");
		String path=map.get("path");
		if (StringUtils.isNotBlank(fullName)) {
			this.fullName = fullName;
			String[] splits = fullName.split("\\.");
			this.fromat = splits[1];
			String[] loc = splits[0].split("_");
			this.key=loc[0];
			this.leftTopLat = loc[1];
			this.leftTopLon = loc[2];
			this.width = loc[3];
			this.height = loc[4];
			this.path=path;
		}
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFromat() {
		return fromat;
	}

	public void setFromat(String fromat) {
		this.fromat = fromat;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getLeftTopLat() {
		return leftTopLat;
	}

	public void setLeftTopLat(String leftTopLat) {
		this.leftTopLat = leftTopLat;
	}

	public String getLeftTopLon() {
		return leftTopLon;
	}

	public void setLeftTopLon(String leftTopLon) {
		this.leftTopLon = leftTopLon;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

}
