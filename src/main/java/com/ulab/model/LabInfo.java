
package com.ulab.model;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Model;
@TableBind(tableName = "t_b_lab_info",pkName="id")
public class LabInfo extends Model<LabInfo> {
	private static final long serialVersionUID = 4762813779629969917L;
	public static final LabInfo dao = new LabInfo();
}
