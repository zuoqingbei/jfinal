package com.ulab.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * 
 * @className HttpJsonHelper.java
 * @time   2017年7月19日 下午9:30:48
 * @author zuoqb
 * @todo   处理json字符串（转对象或者集合）
 */
public class HttpJsonHelper {
	// 根据key获取value
	public static String getValueByKey(String jsonStr, String key) {
		JSONObject jsonObj = null;
		if(jsonStr.indexOf("Error")!=-1){
			return "timeout";
		}
		try {
			jsonObj = new JSONObject(jsonStr);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		try {
			if(jsonObj==null||"".equals(jsonObj)){
				return "";
			}
			return jsonObj.get(key) + "";
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "timeout";
	}

	
	public static Object toJavaBean(String result, Class innerObject) {
		JSONObject innerObj=null;
		try {
			innerObj = new JSONObject(result);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Method[] innermethods = innerObject.getDeclaredMethods();
		Object objBean = null;
		try {
			objBean = innerObject.newInstance();
			for (Method inmethod : innermethods) {
				String in = inmethod.getName();
				if (in.startsWith("set")) {
					String infield = inmethod.getName();
					infield = infield.substring(infield.indexOf("set") + 3);
					infield = infield.toLowerCase().charAt(0)
							+ infield.substring(1);
					if(innerObj.toString().indexOf(infield)!=-1){
						String value=innerObj.get(infield)+"";
						if("null".equals(value)){
							value="";
						}
						inmethod.invoke(objBean,
								new Object[] {value});
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objBean;
	}
	
	public static Object toJavaBean(JSONArray innerObjArr, Class innerObject) {
		Method[] innermethods = innerObject.getDeclaredMethods();
		Object objBean = null;
		try {
			if(innerObjArr==null||innerObjArr.length()==0){
				return null;
			}
			JSONObject innerObj=innerObjArr.getJSONObject(0);
			objBean = innerObject.newInstance();
			for (Method inmethod : innermethods) {
				String in = inmethod.getName();
				if (in.startsWith("set")) {
					String infield = inmethod.getName();
					infield = infield.substring(infield.indexOf("set") + 3);
					infield = infield.toLowerCase().charAt(0)
							+ infield.substring(1);
					if(innerObj.toString().indexOf(infield)!=-1){
						inmethod.invoke(objBean,
								new Object[] { innerObj.get(infield) + "" });
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objBean;
	}
	/**
	 * 普通的list集合
	 * @param javaBean
	 * @param jsonString
	 * @return
	 */
	public static List<Object> toJavaBeanList(String result,Class javaBean) {
		List<Object> list = new ArrayList<Object>();
		JSONArray array;
		try {
			array = new JSONArray(result);
			if(array==null||array.length()==0){
				return null;
			}
			for (int i = 0; i < array.length(); i++) {
				JSONObject jsonObj = array.getJSONObject(i);
				list.add(toJavaBean(jsonObj.toString(),javaBean));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}
