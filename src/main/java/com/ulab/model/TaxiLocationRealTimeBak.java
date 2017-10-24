
package com.ulab.model;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
/**
 * 
 * @author zuoqb
 * 出租车信息
 *
 */
@TableBind(tableName = "dm_taxi_location_realtime_bak",pkName="id")
public class TaxiLocationRealTimeBak extends Model<TaxiLocationRealTimeBak> {
	private static final long serialVersionUID = 4762813779629969917L;
	public static final TaxiLocationRealTimeBak dao = new TaxiLocationRealTimeBak();
	/**
	 * 
	 * @time   2017年6月19日 上午10:55:57
	 * @author zuoqb
	 * @todo  出租车位置信息
	 */
	public List<TaxiLocationRealTimeBak> taxiLocationIfo(){
		/*StringBuffer sb=new StringBuffer();
		sb.append(" select distinct tin.carnumber,loc.longitude as lon,tin.orgion,t2.tel,loc.recivetime,loc.sim,loc.latitude as lat,t2.divername,loc.baidu_longitude,loc.baidu_latitude,loc.baidu_x,loc.baidu_y from  dm_taxi_location_realtime_bak loc left join  ( ");
		sb.append(" select t.* from taxi_transfer_information t inner join (  ");
		sb.append(" select sim,max(satellitetime) as satellitetime  from taxi_transfer_information where checkstatus=0 group by sim) t1 ");
		sb.append(" on t.sim=t1.sim and t.satellitetime=t1.satellitetime) p ");
		sb.append(" left join taxi_driverinfo t2 on p.bankid=t2.bankcard ");
		sb.append(" on loc.sim=p.sim left join taxi_taxiinfo tin on tin.sim=loc.sim");
		sb.append(" where tin.orgion not in('文登测试专用','文登宏利出租','测试专用')  ");*/
		StringBuffer sb=new StringBuffer();
		sb.append(" SELECT tin.carnumber,loc.baidu_longitude,loc.baidu_latitude ");
		sb.append(" from ");
		sb.append("   dm_taxi_location_realtime_bak loc ");
		sb.append(" LEFT JOIN taxi_taxiinfo tin ON tin.sim = loc.sim ");
		sb.append(" where tin.orgion not in('文登测试专用','文登宏利出租','测试专用')  ");
		return TaxiLocationRealTimeBak.dao.find(sb.toString());
	}
	/*public Page<TaxiLocationRealTime> taxiLocationIfo(Dgrid grid,int pageSize,int pageNum){
		StringBuffer sb=new StringBuffer();
		StringBuffer select=new StringBuffer();
		select.append(" select tin.carnumber,loc.longitude as lon,tin.orgion,t2.tel,loc.recivetime,loc.sim,loc.latitude as lat,t2.divername ");
		sb.append(" from  dm_taxi_location_realtime_bak loc left join  (  select t.* from taxi_transfer_information t inner join (  ");
		sb.append(" select sim,max(satellitetime) as satellitetime  from taxi_transfer_information where checkstatus=0 group by sim) t1 ");
		sb.append(" on t.sim=t1.sim and t.satellitetime=t1.satellitetime) p ");
		sb.append(" left join taxi_driverinfo t2 on p.bankid=t2.bankcard ");
		sb.append(" on loc.sim=p.sim left join taxi_taxiinfo tin on tin.sim=loc.sim");
		sb.append(" where tin.orgion not in('文登测试专用','文登宏利出租','测试专用')  ");
		if(grid!=null){
			sb.append(" and  loc.latitude>="+grid.getStr("rightlat"));
			sb.append(" and  loc.latitude<="+grid.getStr("leftlat"));
			sb.append(" and loc.longitude>="+grid.getStr("leftlon"));
			sb.append(" and loc.longitude<="+grid.getStr("rightlon"));
		}
		
		//sb.append(" limit "+(Integer.valueOf(pageNum)-1)*Integer.valueOf(pageSize)+","+pageSize);
		Page<TaxiLocationRealTime> page = TaxiLocationRealTime.dao.paginate(pageNum,pageSize,select.toString(),sb.toString());  //所有订单  
		return page;
	}*/
	public Page<TaxiLocationRealTimeBak> taxiLocationIfo(String baiduX,String baiduY,int pageSize,int pageNum){
		StringBuffer sb=new StringBuffer();
		StringBuffer select=new StringBuffer();
		select.append(" select distinct tin.carnumber,loc.longitude as lon,tin.orgion,t2.tel,loc.recivetime,loc.sim,loc.latitude as lat,loc.baidu_longitude,loc.baidu_latitude,loc.baidu_x,loc.baidu_y,t2.divername ");
		sb.append(" from  dm_taxi_location_realtime_bak loc left join  (  select t.* from taxi_transfer_information t inner join (  ");
		sb.append(" select sim,max(satellitetime) as satellitetime  from taxi_transfer_information where checkstatus=0 group by sim) t1 ");
		sb.append(" on t.sim=t1.sim and t.satellitetime=t1.satellitetime) p ");
		sb.append(" left join taxi_driverinfo t2 on p.bankid=t2.bankcard ");
		sb.append(" on loc.sim=p.sim left join taxi_taxiinfo tin on tin.sim=loc.sim");
		sb.append(" where tin.orgion not in('文登测试专用','文登宏利出租','测试专用')  ");
		if(StringUtils.isNotBlank(baiduX)){
			sb.append(" and  loc.baidu_x='"+baiduX+"' ");
		}
		if(StringUtils.isNotBlank(baiduY)){
			sb.append(" and  loc.baidu_y='"+baiduY+"' ");
		}
		
		//sb.append(" limit "+(Integer.valueOf(pageNum)-1)*Integer.valueOf(pageSize)+","+pageSize);
		Page<TaxiLocationRealTimeBak> page = TaxiLocationRealTimeBak.dao.paginate(pageNum,pageSize,select.toString(),sb.toString());  //所有订单  
		return page;
	}
}
