package com.ulab.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ulab.model.TaxiLocationRealTime;

/**
 * 
 * @time   2017年5月25日 上午10:41:40
 * @author zuoqb
 * @todo   定时器测试
 * 新增的定时器需要在quartz.properties进行配置 覆盖物
 */
public class TestQuartzJobOne implements Job {
	
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		//TaxiLocationRealTime.quartzLocation();
		 TaxiLocationRealTime.quartzLocation();
	}

}
