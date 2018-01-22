package com.ulab.model;

import java.util.List;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

/**
 * 
 * @author zuoqb
 * 网格信息
 *
 */
@TableBind(tableName = "dm_taxi_grid", pkName = "id")
public class Dgrid extends Model<Dgrid> {
	private static final long serialVersionUID = 4762813779629969917L;
	public static final Dgrid dao = new Dgrid();


	public void updateGrid() {
		Db.update("delete from dm_taxi_grid");
		//划定范围
		Grid startGrid = new Grid(121.8f, 36.828427f);
		Grid endGrid = new Grid(122.69158f, 37.565801f);
		for (float i = startGrid.getLon(); i < endGrid.getLon(); i = i + 0.023625f) {
			for (float j = endGrid.getLat(); j > startGrid.getLat(); j = j - 0.018f) {
				Dgrid g=new Dgrid();
				g.set("leftlon", i);
				g.set("leftlat", j);
				g.set("rightlon", i+0.023625);
				g.set("rightlat", j-0.018);
				g.save();
			}
		}
	}
	public List<Dgrid> getGridData(String value){
		String sql="select *,format(nums/"+value+"*100,2) as rate from dm_taxi_grid";
		List<Dgrid> grids=Dgrid.dao.find(sql);
		/*int x=0;
		for(Dgrid g:grids){
			String s="select count(sim) as nums from dm_taxi_location_realtime loc where ";
			s+=" left join taxi_taxiinfo tin ON tin.sim = loc.sim ";
			s+=" where tin.orgion not in('文登测试专用','文登宏利出租','测试专用')  ";
			s+=" loc.longitude>='"+g.get("leftlon")+"' and loc.longitude<='"+g.get("rightlon")+"' ";
			s+=" and loc.latitude>='"+g.get("rightlat")+"' and loc.latitude<='"+g.get("leftlat")+"'";
			TaxiLocationRealTime loc=TaxiLocationRealTime.dao.findFirst(s);
			if(loc!=null){
				g.put("nums", loc.get("nums"));
				System.out.println(loc.get("nums"));
				x=x+Integer.parseInt(loc.get("nums")+"");
			}else{
				g.put("nums", 0);
			}
		}
		System.out.println(x);*/
		return grids;
	}
	public void updateGridNums(){
		String sql="select * from dm_taxi_grid";
		List<Dgrid> grids=Dgrid.dao.find(sql);
		for(Dgrid g:grids){
			/*String s="select count(loc.sim) as nums from dm_taxi_location_realtime loc  ";
			s+=" left join taxi_taxiinfo tin ON tin.sim = loc.sim ";
			s+=" where tin.orgion not in('文登测试专用','文登宏利出租','测试专用')  ";
			s+=" and loc.longitude>='"+g.get("leftlon")+"' and loc.longitude<='"+g.get("rightlon")+"' ";
			s+=" and loc.latitude>='"+g.get("rightlat")+"' and loc.latitude<='"+g.get("leftlat")+"'";*/
			
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
				g.set("nums", loc.get("nums")).update();
			}
		}
	}
	/**
	 * 
	 * @time   2018年1月22日 下午5:19:56
	 * @author zuoqb
	 * @todo   根据一个点经纬度 确定当前所在网格信息
	 * @param  @param lon
	 * @param  @param lat
	 * @param  @return
	 * @return_type   Dgrid
	 */
	public Dgrid getGridByLonLat(String lon,String lat){
		String sql="SELECT * from dm_taxi_grid where rightlat<='"+lat+"' and leftlat>='"+lat+"'";
		sql+=" and leftlon<='"+lon+"' and rightlon>='"+lon+"' ";
		return Dgrid.dao.findFirst(sql);
	}
	
}

class Grid {
	private float lon;
	private float lat;

	public Grid() {
	};

	public Grid(float lon, float lat) {
		this.lon = lon;
		this.lat = lat;
	};

	public float getLon() {
		return lon;
	}

	public void setLon(float lon) {
		this.lon = lon;
	}

	public float getLat() {
		return lat;
	}

	public void setLat(float lat) {
		this.lat = lat;
	}
}
