package com.ulab.controller;

import java.util.HashMap;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.ulab.aop.GlobalInterceptor;
import com.ulab.core.BaseController;
import com.ulab.core.Constants;
import com.ulab.core.WebSocketServer;
import com.ulab.model.ImageInfo;
import com.ulab.util.FileUtil;
import com.ulab.util.JsonUtils;
import com.ulab.util.ServerUtil;

/**
 * 
 * @todo   TODO
 * @time   2018年4月14日 上午10:40:25
 * @author zuoqb
 */
@ControllerBind(controllerKey = "/test", viewPath = "/test")
@Before({ GlobalInterceptor.class })
public class TestController extends BaseController {
	public void home() {
		render("index.html");
	}
	public void socket1() {
		render("socket1.html");
	}
	public void socket2() {
		render("socket2.html");
	}
	/**
	 * 
	 * @todo   TODO
	 * @time   2018年3月19日 下午3:45:12
	 * @author zuoqb
	 * @return_type   给全部用户发送消息  
	 */
	public void send() {
		String msg=getPara("msg");
		String toUser=getPara("toUser");
		WebSocketServer.send(toUser, msg);
	    renderJson("{\"status\":\"success\",\"msg\":\"发送成功\"}");
	}
	/**
	 * @todo   扫描图片
	 * @time   2018年4月14日 下午5:06:20
	 * @author zuoqb
	 * @return_type   void
	 */
	public void scanImage(){
		Map<String,String> map=new HashMap<String,String>();
		/*String serverType = ServerUtil.getServerId();
		String fileName = getPara("fileName", "");
		String dir = "";
		if ("tomcat".equals(serverType)) {
			dir = getRequest().getRealPath("/") + "/static/barimage/";
		} else {
			dir = getWebRootPath() + "/src/main/webapp/static/barimage/";
		}
		String path = dir + fileName;
		String json = JsonUtils.readJson(path);*/
		map=FileUtil.translateImageFile(Constants.CREATE_FILE_PATH,Constants.CREATE_FILE_PATH, true);
		ImageInfo info=new ImageInfo(map);
		renderJson(info);
	}
	/**
	 * 
	 * @todo   扫描结果图片实体
	 * @time   2018年4月14日 下午5:05:18
	 * @author zuoqb
	 */
		
}
	 
