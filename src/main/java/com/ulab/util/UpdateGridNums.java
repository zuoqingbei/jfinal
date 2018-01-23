package com.ulab.util;

/**
 * 
 */
import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.ulab.model.Dgrid;
import com.ulab.model.TaxiLocationRealTime;

public class UpdateGridNums implements Runnable {
	List<Dgrid> grids;
    public UpdateGridNums(List<Dgrid> list){
    	this.grids=list;
    }
    public void run() {
        try {
        	 List<String> sqls=new ArrayList<String>();
        	 for(Dgrid g:grids){
     			StringBuffer sb=new StringBuffer();
     			sb.append(" select count(a.sim) as nums from( select distinct tin.carnumber,loc.longitude as lon,tin.orgion,t2.tel,loc.recivetime,loc.sim,loc.latitude as lat,loc.baidu_longitude,loc.baidu_latitude,loc.baidu_x,loc.baidu_y,t2.divername  from  dm_taxi_location_realtime loc left join  (  select t.* from taxi_transfer_information t inner join (  ");
     			sb.append(" select sim,max(satellitetime) as satellitetime  from taxi_transfer_information where checkstatus=0 group by sim) t1 ");
     			sb.append(" on t.sim=t1.sim and t.satellitetime=t1.satellitetime) p ");
     			sb.append(" left join taxi_driverinfo t2 on p.bankid=t2.bankcard ");
     			sb.append(" on loc.sim=p.sim left join taxi_taxiinfo tin on tin.sim=loc.sim");
     			sb.append(" where tin.orgion not in('文登测试专用','文登宏利出租','测试专用')  ");
     			sb.append(" and loc.longitude>='"+g.get("leftlon")+"' and loc.longitude<='"+g.get("rightlon")+"' ");
     			sb.append(" and loc.latitude>='"+g.get("rightlat")+"' and loc.latitude<='"+g.get("leftlat")+"')a");
     			TaxiLocationRealTime loc=TaxiLocationRealTime.dao.findFirst(sb.toString());
     			if(loc!=null&&!loc.get("nums").equals(g.get("nums"))){
     				String sql="update dm_taxi_grid set nums='"+
     						loc.get("nums")+"'"
           				 +" where id='"+g.get("id")+"'";
           		 sqls.add(sql);
     			}
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
