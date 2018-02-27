
package com.ulab.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.ulab.util.AppSendUtils;
import com.ulab.util.UpdateTaxiGPS;
import com.ulab.util.UpdateTaxiGPSClient;
/**
 * 
 * @author zuoqb
 * 出租车信息
 *
 */
@TableBind(tableName = "dm_taxi_location_realtime",pkName="id")
public class TaxiLocationRealTime extends Model<TaxiLocationRealTime> {
	private static final long serialVersionUID = 4762813779629969917L;
	public static final TaxiLocationRealTime dao = new TaxiLocationRealTime();
	//public static final String DOMAIN_URL = "http://60.212.191.147:8082/weihai";
	public static final String DOMAIN_URL = "http://localhost:8088/weihai";
	/**
	 * 
	 * @time   2017年6月19日 上午10:55:57
	 * @author zuoqb
	 * @todo  出租车位置信息
	 */
	public List<TaxiLocationRealTime> taxiLocationIfo(String lastUpdate){
		/*StringBuffer sb=new StringBuffer();
		sb.append(" select distinct tin.carnumber,loc.longitude as lon,tin.orgion,t2.tel,loc.recivetime,loc.sim,loc.latitude as lat,t2.divername,loc.baidu_longitude,loc.baidu_latitude,loc.baidu_x,loc.baidu_y from  dm_taxi_location_realtime loc left join  ( ");
		sb.append(" select t.* from taxi_transfer_information t inner join (  ");
		sb.append(" select sim,max(satellitetime) as satellitetime  from taxi_transfer_information where checkstatus=0 group by sim) t1 ");
		sb.append(" on t.sim=t1.sim and t.satellitetime=t1.satellitetime) p ");
		sb.append(" left join taxi_driverinfo t2 on p.bankid=t2.bankcard ");
		sb.append(" on loc.sim=p.sim left join taxi_taxiinfo tin on tin.sim=loc.sim");
		sb.append(" where tin.orgion not in('文登测试专用','文登宏利出租','测试专用')  ");
		return TaxiLocationRealTime.dao.find(sb.toString());*/
		StringBuffer sb=new StringBuffer();
		sb.append(" SELECT tin.carnumber,loc.baidu_longitude,loc.baidu_latitude,loc.sim ");
		sb.append(" from ");
		sb.append("   dm_taxi_location_realtime loc ");
		sb.append(" LEFT JOIN taxi_taxiinfo tin ON tin.sim = loc.sim ");
		sb.append(" where tin.orgion not in('文登测试专用','文登宏利出租','测试专用')  ");
		if(lastUpdate!=null&&StringUtils.isNotBlank(lastUpdate)&&!"null".equals(lastUpdate)){
			sb.append(" and loc.transform_time>='"+lastUpdate+"' ");
		}
		return TaxiLocationRealTime.dao.find(sb.toString());
	}
	/*public Page<TaxiLocationRealTime> taxiLocationIfo(Dgrid grid,int pageSize,int pageNum){
		StringBuffer sb=new StringBuffer();
		StringBuffer select=new StringBuffer();
		select.append(" select tin.carnumber,loc.longitude as lon,tin.orgion,t2.tel,loc.recivetime,loc.sim,loc.latitude as lat,t2.divername ");
		sb.append(" from  dm_taxi_location_realtime loc left join  (  select t.* from taxi_transfer_information t inner join (  ");
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
	public Page<TaxiLocationRealTime> taxiLocationIfo(String baiduX,String baiduY,int pageSize,int pageNum){
		StringBuffer sb=new StringBuffer();
		StringBuffer select=new StringBuffer();
		select.append(" select distinct tin.carnumber,loc.longitude as lon,tin.orgion,t2.tel,loc.recivetime,loc.sim,loc.latitude as lat,loc.baidu_longitude,loc.baidu_latitude,loc.baidu_x,loc.baidu_y,t2.divername ");
		sb.append(" from  dm_taxi_location_realtime loc left join  (  select t.* from taxi_transfer_information t inner join (  ");
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
		Page<TaxiLocationRealTime> page = TaxiLocationRealTime.dao.paginate(pageNum,pageSize,select.toString(),sb.toString());  //所有订单  
		return page;
	}
	public Page<TaxiLocationRealTime> taxiLocationIfoNew(String leftLat,String leftLng,String rightLat,String rightLng,int pageSize,int pageNum){
		StringBuffer sb=new StringBuffer();
		StringBuffer select=new StringBuffer();
		select.append(" select distinct tin.carnumber,loc.longitude as lon,tin.orgion,t2.tel,loc.recivetime,loc.sim,loc.latitude as lat,loc.baidu_longitude,loc.baidu_latitude,loc.baidu_x,loc.baidu_y,t2.divername ");
		sb.append(" from  dm_taxi_location_realtime loc left join  (  select t.* from taxi_transfer_information t inner join (  ");
		sb.append(" select sim,max(satellitetime) as satellitetime  from taxi_transfer_information where checkstatus=0 group by sim) t1 ");
		sb.append(" on t.sim=t1.sim and t.satellitetime=t1.satellitetime) p ");
		sb.append(" left join taxi_driverinfo t2 on p.bankid=t2.bankcard ");
		sb.append(" on loc.sim=p.sim left join taxi_taxiinfo tin on tin.sim=loc.sim");
		sb.append(" where tin.orgion not in('文登测试专用','文登宏利出租','测试专用')  ");
		if(StringUtils.isNotBlank(leftLng)){
			sb.append(" and  loc.baidu_longitude>='"+leftLng+"' ");
		}
		if(StringUtils.isNotBlank(rightLng)){
			sb.append(" and  loc.baidu_longitude<='"+rightLng+"' ");
		}
		if(StringUtils.isNotBlank(leftLat)){
			sb.append(" and  loc.baidu_latitude>='"+leftLat+"' ");
		}
		if(StringUtils.isNotBlank(rightLat)){
			sb.append(" and  loc.baidu_latitude<='"+rightLat+"' ");
		}
		Page<TaxiLocationRealTime> page = TaxiLocationRealTime.dao.paginate(pageNum,pageSize,select.toString(),sb.toString());  //所有订单  
		return page;
	}
	/**
	 * 
	 * @time   2017年11月6日 下午8:59:44
	 * @author zuoqb
	 * @todo  定时转换坐标代码 js
	 * @param  
	 * @return_type   void
	 */
	public static void quartzLocation() {
		String taxisql = "select sim,longitude,latitude from dm_taxi_location_realtime  ";
		//String taxisql="select sim,longitude,latitude from dm_taxi_location_realtime  ";
		List<Record> taxi = new ArrayList<Record>();
		List<Block> list = new ArrayList<Block>();
		int pageSize = 10, totalPage = 0;
		try {
			taxi = Db.find(taxisql);
			for (Record r : taxi) {
				Block block = new Block(r.getStr("sim"), r.getStr("longitude"), r.getStr("latitude"));
				list.add(block);
			}
			//每10个为一组调用api
			totalPage = list.size() / pageSize;
			if (list.size() % pageSize > 0) {
				totalPage++;
			}
			for (int page = 0; page < totalPage; page++) {
				List<Block> currentData = new ArrayList<Block>();
				if (page == totalPage - 1) {
					currentData = list.subList(page * pageSize, list.size());
				} else {
					currentData = list.subList(page * pageSize, page * pageSize + pageSize);
				}
				String params = JsonKit.toJson(currentData);
				//通过线程 并发执行  但是由于并发太多 需要主动休眠（效率比顺序执行高）

				Thread rthread = new Thread(new UpdateTaxiGPS(params,DOMAIN_URL));
				rthread.start();
				//必须休眠 不然线程太多会报错
				Thread.sleep(2000);
			}
			//线程休眠5分钟
			// Thread.sleep(1000*60*10);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void quartzLocationClient() {
		//先校验定时器配置文件属性是否允许坐标转化dm_taix_quartz
		if(TaxiQuartz.dao.canTransform()){
			String taxisql = "select sim,longitude,latitude from dm_taxi_location_realtime where transform_status=0 ";
			//String taxisql="select sim,longitude,latitude from dm_taxi_location_realtime  ";
			List<Record> taxi = new ArrayList<Record>();
			List<Block> list = new ArrayList<Block>();
			int pageSize = 100, totalPage = 0;
			try {
				taxi = Db.find(taxisql);
				//System.out.println("num="+taxi.size());
				for (Record r : taxi) {
					Block block = new Block(r.getStr("sim"), r.getStr("longitude"), r.getStr("latitude"));
					list.add(block);
				}
				//每x个为一组调用api
				totalPage = list.size() / pageSize;
				if (list.size() % pageSize > 0) {
					totalPage++;
				}
				for (int page = 0; page < totalPage; page++) {
					List<Block> currentData = new ArrayList<Block>();
					if (page == totalPage - 1) {
						currentData = list.subList(page * pageSize, list.size());
					} else {
						currentData = list.subList(page * pageSize, page * pageSize + pageSize);
					}
					//通过线程 并发执行  但是由于并发太多 需要主动休眠（效率比顺序执行高）
					
					Thread rthread = new Thread(new UpdateTaxiGPSClient(currentData));
					rthread.start();
					//必须休眠 不然线程太多会报错
					//Thread.sleep(2000);
				}
				//线程休眠5分钟
				// Thread.sleep(1000*60*10);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 
	 * @time   2018年1月5日 下午2:26:59
	 * @author zuoqb
	 * @todo   区域覆盖率统计
	 * @param  @return
	 * @return_type   List<TaxiLocationRealTime>
	 */
	public List<TaxiLocationRealTime> taxiAreaFGInfo(String value){
		StringBuffer sb=new StringBuffer();
		sb.append(" SELECT count(1) as nums,count(1)/"+value+"*100 as rate,loc.baidu_x,baidu_y,(loc.baidu_x+0.5)*80 as px,(loc.baidu_y+0.5)*80 as py FROM  ");
		sb.append(" dm_taxi_location_realtime loc ");
		sb.append(" LEFT JOIN taxi_taxiinfo tin ON tin.sim = loc.sim ");
		sb.append(" WHERE  tin.orgion not in('文登测试专用','文登宏利出租','测试专用') and baidu_x is not null group by baidu_x,baidu_y ");
		return TaxiLocationRealTime.dao.find(sb.toString());
	}
	
	public Page<TaxiLocationRealTime> gridCoveringTables(Dgrid dgrid,int pageSize,int pageNum){
		StringBuffer sb=new StringBuffer();
		StringBuffer select=new StringBuffer();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.DATE,-1);
		Date time=cal.getTime();
		String now=sdf.format(time);
		select.append("select a.* ");
		sb.append("from( select distinct tin.carnumber,loc.longitude as lon,tin.orgion,t2.tel,loc.recivetime,loc.sim,loc.latitude as lat,loc.baidu_longitude,loc.baidu_latitude,loc.baidu_x,loc.baidu_y,t2.divername  from  dm_taxi_location_realtime loc left join  (  select t.* from taxi_transfer_information t inner join (  ");
		sb.append(" select sim,max(satellitetime) as satellitetime  from taxi_transfer_information where checkstatus=0 and substr(satellitetime,1,8)='"+now+"'  group by sim) t1 ");
		sb.append(" on t.sim=t1.sim and t.satellitetime=t1.satellitetime) p ");
		sb.append(" left join taxi_driverinfo t2 on p.bankid=t2.bankcard ");
		sb.append(" on loc.sim=p.sim left join taxi_taxiinfo tin on tin.sim=loc.sim");
		sb.append(" where tin.orgion not in('文登测试专用','文登宏利出租','测试专用')  ");
		sb.append(" and loc.longitude>='"+dgrid.get("leftlon")+"' and loc.longitude<='"+dgrid.get("rightlon")+"' ");
		sb.append(" and loc.latitude>='"+dgrid.get("rightlat")+"' and loc.latitude<='"+dgrid.get("leftlat")+"')a");
		Page<TaxiLocationRealTime> page = TaxiLocationRealTime.dao.paginate(pageNum,pageSize,select.toString(),sb.toString());  //所有订单  
		return page;
	}
	public static void main(String[] args) {
		List<Block> currentData = new ArrayList<Block>();
		Block b=new Block();
		b.setLat("37.497628");
		b.setLon("122.116315");
		b.setSim("13406304004");
		currentData.add(b);
		Block b1=new Block();
		b1.setLat("37.526663");
		b1.setLon("122.067488");
		b1.setSim("13406304144");
		currentData.add(b1);
		 List<BaiduLocation> location=AppSendUtils.readHTmlByHtmlUnitMany(currentData);
    	 for(BaiduLocation loc:location){
    		 System.out.println(loc.getLat()+"---"+loc.getLng()+"---"+loc.getSim());
    	 }
		//System.out.println(JsonKit.toJson(currentData));
	}
}
