package com.ulab.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.ulab.model.BaiduLocation;
import com.ulab.model.Block;

public class AppSendUtils {
	    public static String connectURL(String dest_url, String commString) {  
	        String rec_string = "";  
	        URL url = null;  
	        HttpURLConnection urlconn = null;  
	        OutputStream out = null;  
	        BufferedReader rd = null;  
	        try {  
	            url = new URL(dest_url);  
	            urlconn = (HttpURLConnection) url.openConnection();  
	            urlconn.setReadTimeout(1000 * 30);  
	            //urlconn.setRequestProperty("content-type", "text/html;charset=UTF-8");  
	            urlconn.setRequestMethod("POST");  
	            urlconn.setDoInput(true);   
	            urlconn.setDoOutput(true);  
	            out = urlconn.getOutputStream();  
	            out.write(commString.getBytes("UTF-8"));  
	            out.flush();   
	            out.close();  
	            rd = new BufferedReader(new InputStreamReader(urlconn.getInputStream()));  
	            StringBuffer sb = new StringBuffer();  
	            int ch;  
	            while ((ch = rd.read()) > -1)  
	                sb.append((char) ch);  
	            rec_string = sb.toString();  
	        } catch (Exception e) {  
	            return "";  
	        } finally {  
	            try {  
	                if (out != null) {  
	                    out.close();  
	                }  
	                if (urlconn != null) {  
	                    urlconn.disconnect();  
	                }  
	                if (rd != null) {  
	                    rd.close();  
	                }  
	            } catch (Exception e) {  
	            }  
	        }  
	        return rec_string;  
	    }  
	    public static String joinCoords(List<Block> list){
	    	String coords="";
	    	for(Block b:list){
	    		coords+=b.getLon()+","+b.getLat()+";";
	    	}
	    	if(StringUtils.isNotBlank(coords)){
	    		coords=coords.substring(0,coords.length()-1);
	    	}
	    	return coords;
	    }
	    public static void main(String[] args) {  
	        String coords = "122.093060,37.187003;122.093060,37.187003";  
	        String result =connectURL("http://api.map.baidu.com/geoconv/v1/?coords="+coords+"&from=1&to=5&output=json&ak=yxaHr6cPDn9Cwag0fdxefV2s","");  
	        System.out.println(result);  
	        List<BaiduLocation> location = json2List(result);
	        for(BaiduLocation l:location ){
	        	System.out.println(l.getX());
	        }
	    }
	    public static List<BaiduLocation> readHTmlByHtmlUnitMany(List<Block> list) {
	    	 String coords = joinCoords(list);
	    	 String result =connectURL("http://api.map.baidu.com/geoconv/v1/?coords="+coords+"&from=1&to=5&output=json&ak=yxaHr6cPDn9Cwag0fdxefV2s","");  
		     System.out.println(result);  
		    List<BaiduLocation> location = json2List(result);
		    for(int x=0;x<location.size();x++){
		    	Block pre=list.get(x);
		    	location.get(x).setSim(pre.getSim());
		    	location.get(x).setLat(location.get(x).getY());
		    	location.get(x).setLng(location.get(x).getX());
		    }
		    return location;
	    }
		private static List<BaiduLocation> json2List(String result) {
			List<BaiduLocation> location=new ArrayList<BaiduLocation>();
	        if (StringUtils.isNotBlank(result)) {
				//根据[]拆分字符串
				//将json转集合
				JSONObject o=new JSONObject(result);
				if(o.get("result")!=null){
					List<Object> list=HttpJsonHelper.toJavaBeanList(o.get("result").toString(), BaiduLocation.class);
					for(Object obj:list){
						location.add((BaiduLocation)obj);
					};
				}
			}
			return location;
		}  
}
