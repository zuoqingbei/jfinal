package com.ulab.job;

import java.util.ArrayList;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.ulab.model.Block;
import com.ulab.util.UpdateTaxiGPS;

/**
 * 
 * @time   2017年5月25日 上午10:41:40
 * @author zuoqb
 * @todo   定时器测试
 * 新增的定时器需要在quartz.properties进行配置
 */
public class TestQuartzJobOne implements Job {
	public static final String DOMAIN_URL = "http://60.212.191.147:8082/weihai";

	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		String taxisql = "select sim,longitude,latitude from dm_taxi_location_realtime where transform_status='0' ";
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

				Thread rthread = new Thread(new UpdateTaxiGPS(params, DOMAIN_URL));
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

}
