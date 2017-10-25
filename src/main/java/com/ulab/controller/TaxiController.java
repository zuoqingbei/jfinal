package com.ulab.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Page;
import com.ulab.aop.GlobalInterceptor;
import com.ulab.core.BaseController;
import com.ulab.model.Dgrid;
import com.ulab.model.TaxiLocationRealTime;
import com.ulab.model.TaxiLocationRealTimeBak;
/**
 * 
 * @time   2017年4月11日 上午10:59:00
 * @author zuoqb
 * @todo   出租车
 */
@ControllerBind(controllerKey = "/taxi", viewPath = "/test")
@Before({GlobalInterceptor.class})
public class TaxiController extends BaseController {
	public void index(){
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
	public void taxiLocationIfoAjax(){
    	 getResponse().addHeader("Access-Control-Allow-Origin", "*");
    	 List<TaxiLocationRealTime> list=TaxiLocationRealTime.dao.taxiLocationIfo();
    	 Map json=new HashMap();
    	 String callback = getPara("callback"); 
    	 json.put("data",list);
    	 String jsonp = callback+"("+ JsonKit.toJson(json)+")";//返回的json 格式要加callback()
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
    @SuppressWarnings("unchecked")
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
	public void gridTableDataAjax(){
   	 getResponse().addHeader("Access-Control-Allow-Origin", "*");
   	 String callback =getPara("callback"); 
   	 String baiduX =getPara("x"); 
   	 String baiduY =getPara("y");
   	 String p=getPara("pageNum","1");
   	 String size=getPara("pageSize","10");
   	 int pageNum =Integer.parseInt(p);
   	 int pageSize =Integer.parseInt(size);
   	 //根据网格确定出租车
   	 Page<TaxiLocationRealTimeBak> list=TaxiLocationRealTimeBak.dao.taxiLocationIfo(baiduX,baiduY,pageSize,pageNum);
   	 Map json=new HashMap();
   	 json.put("data",list);
   	 String jsonp = callback+"("+ JsonKit.toJson(json)+")";//返回的json 格式要加callback()
   	 renderJson(jsonp);
   }
}
