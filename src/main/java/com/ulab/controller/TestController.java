package com.ulab.controller;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.ulab.aop.GlobalInterceptor;
import com.ulab.core.BaseController;
import com.ulab.core.SocketServer;

/**
 * 
 * @time   2017年4月11日 上午10:59:00
 * @author zuoqb
 * @todo   出租车
 */
@ControllerBind(controllerKey = "/test", viewPath = "/test")
@Before({ GlobalInterceptor.class })
public class TestController extends BaseController {
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
		 Map<String, SocketServer> webSocketSet  = SocketServer.webSocketSet;
	      //遍历用户，依据用户的id向用户发送指定的内容
	      for(Map.Entry<String, SocketServer> entry:webSocketSet.entrySet()){
	          String key = entry.getKey();
	          SocketServer socketServer = webSocketSet.get(key);
	          String sendMsg = "向"+key+"发送消息   "+msg;
	          System.out.println(sendMsg);
	          if(StringUtils.isNotBlank(toUser)){
	        	  if(key.equals(toUser)){
	        		  socketServer.sendMsg(msg);
	        	  }
	          }else{
	        	  //发送全部
	        	  socketServer.sendMsg(msg);
	          }
	      }
	}
}
	 
