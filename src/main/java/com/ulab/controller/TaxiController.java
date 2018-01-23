package com.ulab.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Page;
import com.ulab.aop.GlobalInterceptor;
import com.ulab.core.BaseController;
import com.ulab.model.Dgrid;
import com.ulab.model.GridThreshold;
import com.ulab.model.TaxiLocationRealTime;

/**
 * 
 * @time   2017年4月11日 上午10:59:00
 * @author zuoqb
 * @todo   出租车
 */
@ControllerBind(controllerKey = "/taxi", viewPath = "/test")
@Before({ GlobalInterceptor.class })
public class TaxiController extends BaseController {
	public void index() {
		render("test.html");
	}

	/**
	 * 
	 * @time   2017年6月19日 下午2:37:06
	 * @author zuoqb
	 * @todo   获取出租车实时位置信息(跨域)
	 * @param  
	 * @return_type   void
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void taxiLocationIfoAjax() {
		getResponse().addHeader("Access-Control-Allow-Origin", "*");
		//数据刷新时间间隔 单位毫秒
		String intervalSecond = getPara("intervalSecond");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String lastUpdate = "";
		if (StringUtils.isNotBlank(intervalSecond)) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.SECOND, 0 - Integer.parseInt(intervalSecond) / 1000);
			Date lastDate = cal.getTime();
			lastUpdate = sdf.format(lastDate);
		}
		System.out.println(intervalSecond);
		List<TaxiLocationRealTime> list = TaxiLocationRealTime.dao.taxiLocationIfo(lastUpdate);
		Map json = new HashMap();
		String callback = getPara("callback");
		json.put("data", list);
		String jsonp = callback + "(" + JsonKit.toJson(json) + ")";//返回的json 格式要加callback()
		renderJson(jsonp);
	}

	/**
	 * 
	 * @time   2017年6月19日 下午2:37:06
	 * @author zuoqb
	 * @todo   获取某个网格内出租车信息(跨域)
	 * @param  
	 * @return_type   void
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	/*	public void gridTableDataAjax(){
	    	 getResponse().addHeader("Access-Control-Allow-Origin", "*");
	    	 String callback =getPara("callback"); 
	    	 String lat =getPara("lat"); 
	    	 String lon =getPara("lon");
	    	 int pageNum =getParaToInt("pageNum");
	    	 int pageSize =getParaToInt("pageSize");
	    	 //先通过经纬度 确定一个网格
	    	 Dgrid grid= Dgrid.dao.gridInfoByLatLng(lat, lon);
	    	 //根据网格确定出租车
	    	 Page<TaxiLocationRealTime> list=TaxiLocationRealTime.dao.taxiLocationIfo(grid,pageSize,pageNum);
	    	 Map json=new HashMap();
	    	 json.put("data",list);
	    	 String jsonp = callback+"("+ JsonKit.toJson(json)+")";//返回的json 格式要加callback()
	    	 renderJson(jsonp);
	    }*/
	public void gridTableDataAjax() {
		getResponse().addHeader("Access-Control-Allow-Origin", "*");
		String callback = getPara("callback");
		String baiduX = getPara("x");
		String baiduY = getPara("y");
		String p = getPara("pageNum", "1");
		String size = getPara("pageSize", "10");
		int pageNum = Integer.parseInt(p);
		int pageSize = Integer.parseInt(size);
		//根据网格确定出租车
		Page<TaxiLocationRealTime> list = TaxiLocationRealTime.dao.taxiLocationIfo(baiduX, baiduY, pageSize, pageNum);
		Map json = new HashMap();
		json.put("data", list);
		String jsonp = callback + "(" + JsonKit.toJson(json) + ")";//返回的json 格式要加callback()
		renderJson(jsonp);
	}

	/**
	 * 
	 * @time   2017年11月9日 下午6:29:57
	 * @author zuoqb
	 * @todo   
	 * @param  
	 * @return_type   void
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void gridTableDataAjaxNew() {
		getResponse().addHeader("Access-Control-Allow-Origin", "*");
		String callback = getPara("callback");
		String leftLat = getPara("leftLat");
		String leftLng = getPara("leftLng");
		String rightLat = getPara("rightLat");
		String rightLng = getPara("rightLng");
		String p = getPara("pageNum", "1");
		String size = getPara("pageSize", "10");
		int pageNum = Integer.parseInt(p);
		int pageSize = Integer.parseInt(size);
		//根据网格确定出租车
		Page<TaxiLocationRealTime> list = TaxiLocationRealTime.dao.taxiLocationIfoNew(leftLat, leftLng, rightLat,
				rightLng, pageSize, pageNum);
		Map json = new HashMap();
		json.put("data", list);
		String jsonp = callback + "(" + JsonKit.toJson(json) + ")";//返回的json 格式要加callback()
		renderJson(jsonp);
	}

	/**
	 * 
	 * @time   2017年11月7日 下午1:00:04
	 * @author zuoqb
	 * @todo   使用js api进行坐标转换 GPS-->百度坐标（包含图块坐标转换）
	 * @param  
	 * @return_type   void
	 */
	public void quart() {
		TaxiLocationRealTime.quartzLocation();
		renderJson("success");
	}

	/**
	 * 
	 * @time   2017年11月7日 下午12:58:29
	 * @author zuoqb
	 * @todo   后台坐标转换：GPS-->百度坐标（无法实现图块坐标转换）
	 * @param  
	 * @return_type   void
	 */
	public void quartClient() {
		TaxiLocationRealTime.quartzLocationClient();
		renderJson("success");
	}
	/**
	 * 
	 * @time   2018年1月5日 下午2:28:04
	 * @author zuoqb
	 * @todo   区域覆盖物分布
	 * @param  
	 * @return_type   void
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void taxiAreaFGInfoAjax() {
		getResponse().addHeader("Access-Control-Allow-Origin", "*");
		String value = getPara("value","80");//计算阈值
		List<TaxiLocationRealTime> list = TaxiLocationRealTime.dao.taxiAreaFGInfo(value);
		Map json = new HashMap();
		String callback = getPara("callback");
		json.put("data", list);
		String jsonp = callback + "(" + JsonKit.toJson(json) + ")";//返回的json 格式要加callback()
		renderJson(jsonp);
	}
	/**
	 * 
	 * @time   2018年1月22日 下午4:10:14
	 * @author zuoqb
	 * @todo   更新网格点
	 * @param  
	 * @return_type   void
	 */
	public void updateGridLoc(){
		Dgrid.dao.updateGrid();
		renderNull();
	}
	/**
	 * 
	 * @time   2018年1月22日 下午4:10:27
	 * @author zuoqb
	 * @todo   更新每个格子内出租车数量
	 * @param  
	 * @return_type   void
	 */
	public void updateGridNums(){
		Dgrid.dao.updateGridNumsClient();
		renderNull();
	}
	
	/**
	 * 
	 * @time   2018年1月22日 下午3:09:10
	 * @author zuoqb
	 * @todo   网格 覆盖物
	 * @param  
	 * @return_type   void
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getGridData(){
		getResponse().addHeader("Access-Control-Allow-Origin", "*");
		GridThreshold gridThreshold=GridThreshold.dao.gridThreshold();
		String value ="80" ;//默认80
		if(gridThreshold!=null){
			value=gridThreshold.get("threshold_value").toString();//计算阈值
		}
		List<Dgrid> grids=Dgrid.dao.getGridData(value);
		Map json = new HashMap();
		String callback = getPara("callback");
		json.put("data", grids);
		String jsonp = callback + "(" + JsonKit.toJson(json) + ")";//返回的json 格式要加callback()
		renderJson(jsonp);
	}
	/**
	 * 
	 * @time   2018年1月22日17:14:25
	 * @author zuoqb
	 * @todo   网格遮盖物网格
	 * @param  
	 * @return_type   void
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void gridCoveringTables() {
		getResponse().addHeader("Access-Control-Allow-Origin", "*");
		String callback = getPara("callback");
		String lon = getPara("lon");
		String lat = getPara("lat");
		
		String p = getPara("pageNum", "1");
		String size = getPara("pageSize", "10");
		int pageNum = Integer.parseInt(p);
		int pageSize = Integer.parseInt(size);
		Dgrid dgrid=Dgrid.dao.getGridByLonLat(lon, lat);
		//根据网格确定出租车
		Page<TaxiLocationRealTime> list = TaxiLocationRealTime.dao.gridCoveringTables(dgrid, pageSize, pageNum);
		Map json = new HashMap();
		json.put("data", list);
		String jsonp = callback + "(" + JsonKit.toJson(json) + ")";//返回的json 格式要加callback()
		renderJson(jsonp);
	}
}
