package com.ulab.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.jfinal.kit.JsonKit;
import com.ulab.model.BaiduLocation;
import com.ulab.model.Block;
/**
 * 
 * @className HttpRequestUtils.java
 * @time   2017年7月19日 下午9:33:23
 * @author zuoqb
 * @todo   出租车GPS坐标通过百度api转百度坐标以及平面坐标工具
 */
public class HttpRequestUtils {
	/**
	 * 服务器上点转化页面地址
	 * 其中block_points.html页面是单点转化
	 * block_points2.html是多点转化
	 */
	//服务器地址
	public static final String DOMAIN_URL = "http://60.212.191.147:8080/weihai";
	//本地测试地址
	//public static final String DOMAIN_URL = "http://localhost:8088/weihai";
	public static void main(String[] args) {
		/**
		 * 一次转化一个点
		 */
		//readHTmlByHtmlUnit("120", "31");
		
		/**
		 * 一次转化多个点 一次只能转10个点
		 */
		//模拟多个出租车GPS信息
		List<Block> list=new ArrayList<Block>();
		for(int x=0;x<10;x++){
			Random random=new Random();
			int lng = random.nextInt(10);
			int lat = random.nextInt(10);
			Block b=new Block("A0001"+x, 120+lng+"", 30+lat+"");
			list.add(b);
		}
		String param=JsonKit.toJson(list);
		//System.out.println(param);
		//System.out.println(DOMAIN_URL+"?param="+param);
		readHTmlByHtmlUnitMany(param);
	}
	public static List<BaiduLocation> readHTmlByHtmlUnitMany(String params) {
		return readHTmlByHtmlUnitMany(params, DOMAIN_URL);
	}
	/**
	 * 说明：百度api批量转化点个数为10  
	 * @time   2017年7月19日 下午3:58:52
	 * @author zuoqb
	 * @todo   一次转化多个点 格式
	 * [{"sim":"A00010","lon":"120","lat":"31"},
	 * {"sim":"A00011","lon":"121","lat":"32"},
	 * {"sim":"A00012","lon":"122","lat":"33"},
	 * {"sim":"A00013","lon":"123","lat":"34"},
	 * {"sim":"A00014","lon":"124","lat":"35"}]
	 * @return_type   Map<String, Object>
	 */
	public static List<BaiduLocation> readHTmlByHtmlUnitMany(String params,String url) {
		url = url + "/block_points2.html?param="+params;
		System.out.println(url);
		WebClient webClient = new WebClient(BrowserVersion.CHROME);//模拟浏览器
		// 启动js
		webClient.getOptions().setJavaScriptEnabled(true);
		// 关闭css渲染
		webClient.getOptions().setCssEnabled(false);
		// js运行时错误，是否抛出异常
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		HtmlPage page;
		List<BaiduLocation> location=new ArrayList<BaiduLocation>();
		long start=System.currentTimeMillis();
		try {
			// 3.获取页面
			page = webClient.getPage(url);
			// 等待js渲染执行 waitime等待时间(ms)
			webClient.waitForBackgroundJavaScript(3000);
			// 获取标签hed的内容
			// HtmlDivision div=(HtmlDivision)page.getElementById("points_box");
			String result = page.asText();
			if (StringUtils.isNotBlank(result) && result.indexOf("[") != -1) {
				String data = "";
				//根据[]拆分字符串
				data = result.substring(result.indexOf("["));
				data = data.substring(0, data.indexOf("]") + 1);
				webClient.closeAllWindows();// 关闭窗口
				//将json转集合
				List<Object> list=HttpJsonHelper.toJavaBeanList(data, BaiduLocation.class);
				for(Object obj:list){
					location.add((BaiduLocation)obj);
				};
				long end=System.currentTimeMillis();
				long time=(end-start)/1000l;
				System.out.println("请求结束，耗时："+time);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return location;
	}
	public static Map<String, Object> readHTmlByHtmlUnit(String lon, String lat) {
		return readHTmlByHtmlUnit(lon, lat, DOMAIN_URL);
	}
	/**
	 * 
	 * @time   2017年7月19日 下午3:58:52
	 * @author zuoqb
	 * @todo   一次转化一个点
	 * @param  @param lon :出租车GPS经度
	 * @param  @param lat ：出租车GPS维度
	 * @return_type   Map<String, Object>
	 */
	public static Map<String, Object> readHTmlByHtmlUnit(String lon, String lat,String url) {
		url = url + "/block_points.html?lon=" + lon + "&lat=" + lat;
		WebClient webClient = new WebClient(BrowserVersion.CHROME);//模拟浏览器
		// 启动js
		webClient.getOptions().setJavaScriptEnabled(true);
		// 关闭css渲染
		webClient.getOptions().setCssEnabled(false);
		// js运行时错误，是否抛出异常
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		HtmlPage page;
		Map<String, Object> map =new HashMap<String, Object>();
		try {
			// 3.获取页面
			page = webClient.getPage(url);
			// 等待js渲染执行 waitime等待时间(ms)
			webClient.waitForBackgroundJavaScript(3000);
			// 获取标签hed的内容
			// HtmlDivision div=(HtmlDivision)page.getElementById("points_box");
			String result = page.asText();
			if (StringUtils.isNotBlank(result) && result.indexOf("{") != -1) {
				String data = "";
				data = result.substring(result.indexOf("{"));
				data = data.substring(0, data.indexOf("}") + 1);
				webClient.closeAllWindows();// 关闭窗口
				map = convertJsonStrToMap(data);
				System.out.println(map.get("lng"));
				System.out.println(map.get("lat"));
				System.out.println(map.get("x"));
				System.out.println(map.get("y"));
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 将json转化成map
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static Map<String, Object> convertJsonStrToMap(String jsonStr) {

		Map<String, Object> map = JSON.parseObject(jsonStr,
				new TypeReference<Map<String, Object>>() {
				});

		return map;
	}
}
