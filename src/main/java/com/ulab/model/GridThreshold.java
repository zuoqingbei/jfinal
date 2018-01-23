
package com.ulab.model;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Model;
/**
 * 
 * @className GridThreshold.java
 * @time   2018年1月23日 上午11:09:57
 * @author zuoqb
 * @todo   出租车网格覆盖物阈值属性表
 */
@TableBind(tableName = "dm_grid_threshold",pkName="id")
public class GridThreshold extends Model<GridThreshold> {
	private static final long serialVersionUID = 4762813779629969917L;
	public static final GridThreshold dao = new GridThreshold();
	
	public GridThreshold gridThreshold(){
		StringBuffer sb=new StringBuffer();
		sb.append("  select * from dm_grid_threshold ");
		return GridThreshold.dao.findFirst(sb.toString());
	}
}
