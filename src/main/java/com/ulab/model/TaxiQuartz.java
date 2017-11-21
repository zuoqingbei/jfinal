
package com.ulab.model;

import java.util.Calendar;
import java.util.Date;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Model;
/**
 * 
 * @author zuoqb
 * 出租车定时属性信息
 *
 */
@TableBind(tableName = "dm_taix_quartz",pkName="id")
public class TaxiQuartz extends Model<TaxiQuartz> {
	private static final long serialVersionUID = 4762813779629969917L;
	public static final TaxiQuartz dao = new TaxiQuartz();
	
	public TaxiQuartz taxiQuartzPro(){
		StringBuffer sb=new StringBuffer();
		sb.append("  select id, intervals,last_transform_time from dm_taix_quartz ");
		return TaxiQuartz.dao.findFirst(sb.toString());
	}
	/**
	 * 
	 * @time   2017年11月21日 下午2:18:21
	 * @author zuoqb
	 * @todo   判断能否转化坐标
	 * @param  @return
	 * @return_type   boolean
	 */
	public boolean canTransform(){
		TaxiQuartz quartzPro=taxiQuartzPro();
		if(quartzPro!=null){
			//当前时间大于上次更新时间+时间间隔 才可以转化
			Date lastTransformTime=quartzPro.getDate("last_transform_time");
			int intervals=quartzPro.getInt("intervals");
			Calendar cal=Calendar.getInstance(); 
			cal.setTime(lastTransformTime); 
			cal.add(Calendar.SECOND, intervals); 
			Date lastDate=cal.getTime(); 
			Date now=new Date();
			boolean canTransform=now.getTime()>lastDate.getTime();
			if(canTransform){
				//更新转化时间
				quartzPro.set("last_transform_time", new Date()).update();
				System.out.println("canTransform="+canTransform);
			}
			return canTransform;
		}
		return false;
	}
	
}
