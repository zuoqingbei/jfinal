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
import com.ulab.model.Block;

public class UpdateTaxiGPSClient implements Runnable {
	List<Block> list;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
    public UpdateTaxiGPSClient(List<Block> list){
    	this.list=list;
    }
    /**
     * 实时输出日志信息
     */
    public void run() {
    	System.out.println("start---------------------------------------");
        try {
        	 List<BaiduLocation> location=AppSendUtils.readHTmlByHtmlUnitMany(list);
        	 List<String> sqls=new ArrayList<String>();
        	 for(BaiduLocation loc:location){
        		 System.out.println(loc.getLat()+"---"+loc.getLng()+"---"+loc.getSim());
        		 //更新信息
        		 String sql="update dm_taxi_location_realtime set baidu_longitude='"+
        				 loc.getLng()+"',"+
        				 "baidu_latitude='"+loc.getLat()+"',"+
        				/* "baidu_x='"+loc.getX()+"',"+
        				 "baidu_y='"+loc.getY()+"',"+*/
        				 "transform_status='1',"+
        				 "transform_time='"+dateFormat.format(new Date())+"'"
        				 +" where sim='"+loc.getSim()+"'";
        		 sqls.add(sql);
        	 }
        	 System.out.println("---------------------------------------sqls.size()="+sqls.size());
        	 if(sqls.size()>0){
        		 Db.batch(sqls, sqls.size());
        		 System.out.println("批量更新成功!");
        	 }
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

}
