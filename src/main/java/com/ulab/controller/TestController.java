package com.ulab.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.JsonKit;
import com.ulab.aop.GlobalInterceptor;
import com.ulab.core.BaseController;
import com.ulab.model.LabInfo;
import com.ulab.util.JsonUtils;
/**
 * 
 * @time   2017年4月11日 上午10:59:00
 * @author zuoqb
 * @todo   测试类
 */
@ControllerBind(controllerKey = "/test", viewPath = "/test")
@Before({GlobalInterceptor.class})
public class TestController extends BaseController {
    public void test() {
        render("test.html");
    }
    /**
     * 返回json格式数据
     */
    public void jsonFromDBAjax(){
    	List<LabInfo> list=LabInfo.dao.find("select * from t_b_lab_info ");
		renderJson(list);
    }
    /**
     * 
     * @time   2017年5月26日 下午2:13:12
     * @author zuoqb
     * @todo   获取json文件数据 如果文件不存在则从数据库读取并生成json文件 这个是在tomcat服务器下面可以
     * @param  
     * @return_type   void
     */
    public void getJsonFile(){
    	String fileName=getPara("fileName","");
    	SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
    	fileName=sdf.format(new Date())+"-"+fileName;//文件格式 日期+名称
    	String url=this.getRequest().getSession().getServletContext().getRealPath("/");
    	String path=url+"static\\data\\"+fileName;
    	System.err.println(path);
    	String data="";
    	if(JsonUtils.judeFileExists(path)){
    		//直接读取json文件
    		data=JsonUtils.readJson(path);
    	}else{
    		//从数据库读取
    		List<LabInfo> list=LabInfo.dao.find("select * from t_b_lab_info ");
    		//写入json文件
    		data=JsonKit.toJson(list);
    		JsonUtils.writeJson(url+"static\\data\\", data, fileName);
    	}
    	renderText(data);
    }

}
