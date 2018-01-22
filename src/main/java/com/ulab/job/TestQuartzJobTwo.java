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
 * 新增的定时器需要在quartz.properties进行配置 客户端转化坐标
 */
public class TestQuartzJobTwo implements Job {

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		// TODO Auto-generated method stub
	//	System.out.println("定时器");
		TaxiLocationRealTime.quartzLocationClient();
	}


}
