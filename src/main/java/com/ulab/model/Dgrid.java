
package com.ulab.model;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Model;
/**
 * 
 * @author zuoqb
 * 网格信息
 *
 */
@TableBind(tableName = "d_grid",pkName="id")
public class Dgrid extends Model<Dgrid> {
	private static final long serialVersionUID = 4762813779629969917L;
	public static final Dgrid dao = new Dgrid();
	/**
	 * 
	 * @time   2017年6月19日 上午10:55:57
	 * @author zuoqb
	 * @todo  根据一个点经纬度确定当前点所在的网格位置信息
	 */
	public Dgrid gridInfoByLatLng(String lat,String lon){
		StringBuffer sb=new StringBuffer();
		sb.append(" select * from `d_grid` g where g.leftlat>="+lat+" and g.rightlat<="+lat+"  and g.leftlon<="+lon+"  and g.rightlon>="+lon+"  ");
		return Dgrid.dao.findFirst(sb.toString());
	}
}
