package com.ulab.util;

/**
 * Created by Kevin.Li on 2017/6/14.
 */
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.ulab.model.BaiduLocation;

public class UpdateTaxiGPS implements Runnable {
    private String params;
    private String url;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
    public UpdateTaxiGPS(String params,String url){
    	this.params=params;
    	this.url=url;
    }
    /**
     * 实时输出日志信息
     */
    public void run() {
        try {
        	 List<BaiduLocation> location=HttpRequestUtils.readHTmlByHtmlUnitMany(params,url);
        	 List<String> sqls=new ArrayList<String>();
        	 for(BaiduLocation loc:location){
        		// System.out.println(loc.getLat()+"---"+loc.getLng());
        		 //更新信息
        		 String sql="update dm_taxi_location_realtime set "+
        				 "baidu_x='"+loc.getX()+"',"+
        				 "baidu_y='"+loc.getY()+"'"+
        				 " where sim='"+loc.getSim()+"'";
        		 sqls.add(sql);
        	 }
        	 if(sqls.size()>0){
        		 Db.batch(sqls, sqls.size());
        		 System.out.println("批量更新成功!");
        	 }
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

}
