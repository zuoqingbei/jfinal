
package com.ulab.core;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.ActiveRecordException;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;
import com.jfinal.upload.UploadFile;
import com.ulab.util.StringKit;
import com.ulab.util.TypeConverter;

public class BaseController extends Controller {

	
	public String getParasUrl() {
		String parasUrl = null;
		if (getParaMap() != null && getParaMap().size() > 0) {
			Set<String> keySet = getParaMap().keySet();
			parasUrl = "?";
			for (String key : keySet) {
				parasUrl = parasUrl + key + "=" + getParaMap().get(key)[0]
						+ "&";
			}
			parasUrl = parasUrl.substring(0, parasUrl.length() - 1);
		}
		return parasUrl;
	}


	public String getRequestUrl() {
		return getRequest().getRequestURL()
				+ (getParasUrl() == null ? "" : getParasUrl());
	}

	public String getRequestShortUrl() {
		return getRequest().getRequestURI()
				+ (getParasUrl() == null ? "" : getParasUrl());
	}

	public String getIpAddr() {
		HttpServletRequest request = this.getRequest();
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}



	// ***********************重写cookie操作，用于对汉字的转码************************//

	@Override
	public Controller setCookie(String name, String value, int maxAgeInSeconds,
			String path) {
		return setCookie(name, value, maxAgeInSeconds, path, null);
	}

	
	public Controller setCookie(String name, String value, int maxAgeInSeconds,
			String path, String domain) {
		if (value != null) {
			try {
				value = URLEncoder.encode(value, "UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return super.setCookie(name, value, maxAgeInSeconds, path, domain);
	}

	@Override
	public String getCookie(String name) {
		String value = super.getCookie(name);
		if (value != null) {
			try {
				value = URLDecoder.decode(value, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return value;
	}

	
	/**
	 * 获取制定的参数，并自动赋值给model对象
	 * 
	 * @author yongtree
	 * @date Apr 18, 20142:24:23 PM
	 * @param paras
	 * @return
	 */
	public <T> T getModel(Class<T> modelClass, String[] paras) {
		Object model = null;
		try {
			model = modelClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		Map<String, String[]> parasMap = getRequest().getParameterMap();
		if (model instanceof Model) {
			Table tableInfo = TableMapping.me().getTable(
					((Model) model).getClass());
			for (String para : paras) {
				if (parasMap.containsKey(para)) {
					Class colType = tableInfo.getColumnType(para);
					if (colType == null)
						throw new ActiveRecordException("The model attribute "
								+ para + " is not exists.");
					String[] paraValue = parasMap.get(para);
					try {
						Object value = paraValue[0] != null ? TypeConverter
								.convert(colType, paraValue[0]) : null;
						((Model) model).set(para, value);
					} catch (Exception ex) {
						throw new RuntimeException(ex);
					}
				}
			}
		} else {
			Method[] methods = modelClass.getMethods();
			for (Method method : methods) {
				String methodName = method.getName();
				if (methodName.startsWith("set") == false) // only setter method
					continue;
				Class<?>[] types = method.getParameterTypes();
				if (types.length != 1) // only one parameter
					continue;

				String attrName = StringKit.firstCharToLowerCase(methodName
						.substring(3));
				boolean f = false;
				for (String para : paras) {
					if (attrName.equals(para)) {
						f = true;
						break;
					}
				}
				if (f) {
					if (getRequest().getParameterMap().containsKey(attrName)) {
						String value = getRequest().getParameter(attrName);
						try {
							method.invoke(model,
									TypeConverter.convert(types[0], value));
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}

				}
			}
		}
		return (T) model;
	}

	/**
	 * 获取制定的参数，并自动赋值给record对象
	 * 
	 * @author yongtree
	 * @date Apr 18, 20142:24:23 PM
	 * @param paras
	 * @return
	 */
	public Record getRecord(String[] paras) {
		Record model = new Record();
		Map<String, String[]> parasMap = getRequest().getParameterMap();
		for (String para : paras) {
			if (parasMap.containsKey(para)) {
				String[] paraValue = parasMap.get(para);
				Object value = StringKit.notBlank(paraValue[0]) ? (paraValue.length == 1 ? paraValue[0]
						: StringKit.array2str(paraValue, ","))
						: null;
				if(value!=null){
					model.set(para, value);
				}
				
			}
		}
		return model;
	}

	/**
	 * 获取以modelName开头的参数，并自动赋值给record对象
	 * 
	 * @author yongtree
	 * @date 2013-9-25上午9:14:00
	 * @param modelName
	 * @return
	 */
	public Record getRecord(String modelName) {
		String modelNameAndDot = modelName + ".";
		Record model = new Record();
		boolean exist = false;
		Map<String, String[]> parasMap = getRequest().getParameterMap();
		for (Entry<String, String[]> e : parasMap.entrySet()) {
			String paraKey = e.getKey();
			if (paraKey.startsWith(modelNameAndDot)) {
				String paraName = paraKey.substring(modelNameAndDot.length());
				String[] paraValue = e.getValue();
				Object value = StringKit.notBlank(paraValue[0]) ? (paraValue.length == 1 ? paraValue[0]
						: StringKit.array2str(paraValue, ","))
						: null;
				
				if(value!=null){
					model.set(paraName, value);
					exist = true;
				}
			}
		}
		if (exist) {
			return model;
		} else {
			return null;
		}

	}

	/**
	 * 获取前端传来的数组对象并响应成Record列表
	 * 
	 * @author yongtree
	 * @date 2013-9-26上午10:21:38
	 * @param modelName
	 * @return
	 */
	public List<Record> getRecords(String modelName) {
		List<String> nos = getModelsNoList(modelName);
		List<Record> list = new ArrayList<Record>();
		for (String no : nos) {
			Record r = getRecord(modelName + "[" + no + "]");
			if (r != null) {
				list.add(r);
			}
		}
		return list;
	}

	/**
	 * 获取前端传来的数组对象并响应成Model列表
	 * 
	 * @author yongtree
	 * @param <T>
	 * @date 2013-9-26上午10:22:38
	 * @param modelClass
	 * @param modelName
	 * @return
	 */
	public <T> List<T> getModels(Class<T> modelClass, String modelName) {
		List<String> nos = getModelsNoList(modelName);
		List<T> list = new ArrayList<T>();
		for (String no : nos) {
			T m = getModel(modelClass, modelName + "[" + no + "]");
			if (m != null) {
				list.add(m);
			}
		}
		return list;
	}

	/**
	 * 提取model对象数组的标号
	 * 
	 * @author yongtree
	 * @date 2013-9-26上午10:17:14
	 * @param modelName
	 * @return
	 */
	private List<String> getModelsNoList(String modelName) {
		// 提取标号
		List<String> list = new ArrayList<String>();
		String modelNameAndLeft = modelName + "[";
		Map<String, String[]> parasMap = getRequest().getParameterMap();
		for (Entry<String, String[]> e : parasMap.entrySet()) {
			String paraKey = e.getKey();
			if (paraKey.startsWith(modelNameAndLeft)) {
				String no = paraKey.substring(paraKey.indexOf("[") + 1,
						paraKey.indexOf("]"));
				if (!list.contains(no)) {
					list.add(no);
				}
			}
		}
		return list;
	}

	
	
	public List<UploadFile> getFiles(String parameterName) {
		List<UploadFile> uploadFiles = getFiles();
		List<UploadFile> files = new ArrayList<UploadFile>();
		for (UploadFile uploadFile : uploadFiles) {
			if (uploadFile.getParameterName().equals(parameterName)) {
				files.add(uploadFile);
			}
		}
		return files;
	}

	/**** XXX jfinal1.5中的getParaToDate不能满足需求，增加相应方法 ，静待jfinal升级支持 ****/
	public Date getParaToDate(String para, String format) {
		return getParaToDate(para, format, null);
	}

	public Date getParaToDate(String para, String format, Date defaultValue) {
		String value = getPara(para);
		if (value == null || "".equals(value.trim()))
			return defaultValue;
		try {
			return new java.text.SimpleDateFormat(format).parse(value);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

	}

	protected static final int DEFAULT_PAGE_LIMIT = 10;
	
	
	public void renderForReg(){
		if(isParaExists("jsonpCallback")){
			Enumeration<String> names=this.getAttrNames();
			Record record = new Record();
			while(names.hasMoreElements()){
				String name = names.nextElement();
				record.set(name, getAttr(name));
			}
			renderHtml(getPara("jsonpCallback")+"("+record.toJson()+")");
		}else{
			renderJson();
		}
	}
	
	public static void setFileDownloadHeader(HttpServletRequest request, HttpServletResponse response, String fileName) {
		final String userAgent = request.getHeader("USER-AGENT");
		try {
			String finalFileName = null;
			if(StringUtils.contains(userAgent, "MSIE")){
				finalFileName = URLEncoder.encode(fileName,"UTF8");
			}else if(StringUtils.contains(userAgent, "Mozilla")){
				finalFileName = new String(fileName.getBytes(), "ISO8859-1");
			}else{
				finalFileName = URLEncoder.encode(fileName,"UTF8");
			}
			response.setHeader("Content-Disposition", "attachment; filename=\"" + finalFileName + "\"");
		} catch (UnsupportedEncodingException e) {
		}
	}
	public static String getWebRootPath(){
		try {
			String path = Class .class.getResource("/").toURI().getPath();
			String url=new File(path).getParentFile().getParentFile().getCanonicalPath();
			return url;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
