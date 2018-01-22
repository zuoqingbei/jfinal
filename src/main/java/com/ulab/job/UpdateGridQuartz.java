package com.ulab.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ulab.model.Dgrid;
/**
 * 
 * @time   2018年1月22日14:01:04
 * @author zuoqb
 * @todo   更新网格内出租车数量
 */
public class UpdateGridQuartz implements Job {

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		Dgrid.dao.updateGridNums();
	}


}
