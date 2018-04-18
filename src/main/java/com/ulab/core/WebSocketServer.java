package com.ulab.core;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.lang3.StringUtils;

import com.ulab.util.ClientSocketUtil;

/**
 * 环境
 * 浏览器：firefox，google chrome
 * tomcat7.0.69
 * jdk7.0.79
 * 1 每个浏览器代表一个用户，与服务端建立连接后，实现服务端与浏览器的交互
 * 2 暴露websocket推送接口，其他服务端或者业务类调用该接口，向指定用户进行消息推送
 * @author caihao
 *
 */
//URI注解，无需在web.xml中配置。
@ServerEndpoint("/websocket")
public class WebSocketServer {

	//浏览器与服务端的回话，浏览器每new一个WebSocket就创建一个session，关闭或刷新浏览器，session关闭
	private Session session;
	//代表浏览器
	private String userid;

	/**
	 * 推送消息接口
	 * 外部可以进行调用
	 * @param sendMsg
	 * @throws IOException
	 */
	public void sendMsg(String sendMsg) {
		System.out.println(this.session + ";" + this.userid + ";" + sendMsg);
		try {
			this.session.getBasicRemote().sendText(sendMsg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//设置Map,存放每个用户的连接
	public static Map<String, WebSocketServer> webSocketSet = new HashMap<String, WebSocketServer>();

	@OnOpen
	public void onOpen(Session session) throws IOException {
		this.session = session;
		//后期换成当前登录人ID  
		webSocketSet.put(userid, this);
		//webSocketSet.put(this.session.getId(), this);//存在bug
		System.out.println(this + "有新连接,session=" + session + ";userid=" + userid);
	}

	@OnClose
	public void onClose() {
		webSocketSet.remove(this.userid);
		System.out.println(this + "；连接关闭");
	}

	@OnMessage
    public void onMessage(String order){
        System.out.println(this+"；来自网页的指令:" + order);
      /*  if(info.contains("userid")){
            this.userid = info.split("userid=")[1];
            System.out.println(this+",this.session="+this.session+";this.userid="+this.userid);
            webSocketSet.put(userid, this);
        }*/
       // String result=SocketClient.sendOrder(order);
      /*  ClientSocketUtil client=new ClientSocketUtil();
        client.send(order);
        DataInputStream dis=null;
        InputStream is=null;
       try {
    	   is = client.server.getInputStream();
           dis=new DataInputStream(is);
           while(true){
           	System.out.println(dis.readUTF());
           }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}*/
      /*  BufferedReader br=client.recieve();
		String s = "";        
        try {
			while((s = br.readLine()) != null)
			    System.out.println(s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        client.close();*/
       // sendAll(result);
        //接收消息
        ClientSocketUtil client=new ClientSocketUtil();
        client.send(order);
        if("getflystatus".equals(order)){
        	String result;
        	try {
        		while (true) {
        			result=readInputStream(new BufferedInputStream(client.server.getInputStream()));
        			System.out.println(result);
        			if (result.contains("|")) break;
        		}
        		sendAll(result);
        		/*Random r=new Random();
        		sendAll("100|"+r.nextInt()+"|56|88|12|0|0|0|0|0");*/
        		//client.close();
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        }else{
        	//client.close();
        }
        
        
    }
	private static String readInputStream(BufferedInputStream paramBufferedInputStream)
		    throws IOException
		  {
		    String str = "";
		    int i = paramBufferedInputStream.read();
		    if (i == -1)
		      return null;
		    str = str + "" + (char)i;
		    int j = paramBufferedInputStream.available();
		    System.out.println("Len got : " + j);
		    if (j > 0)
		    {
		      byte[] arrayOfByte = new byte[j];
		      paramBufferedInputStream.read(arrayOfByte);
		      str = str + new String(arrayOfByte);
		    }
		    return str;
		  }
	@OnError
	public void onError(Throwable error) {
		System.out.println(this + "；发生错误");
		error.printStackTrace();
	}

	public static void send(String toUser, String msg) {
		Map<String, WebSocketServer> webSocketSet = WebSocketServer.webSocketSet;
		//遍历用户，依据用户的id向用户发送指定的内容
		for (Map.Entry<String, WebSocketServer> entry : webSocketSet.entrySet()) {
			String key = entry.getKey();
			WebSocketServer socketServer = webSocketSet.get(key);
			String sendMsg = "向页面发送消息   " + msg;
			System.out.println(sendMsg);
			if (StringUtils.isNotBlank(toUser)) {
				if (key.equals(toUser)) {
					socketServer.sendMsg(msg);
				}
			} else {
				//发送全部
				socketServer.sendMsg(msg);
			}
		}
	}

	public static void sendAll(String msg) {
		Map<String, WebSocketServer> webSocketSet = WebSocketServer.webSocketSet;
		//遍历用户，依据用户的id向用户发送指定的内容
		for (Map.Entry<String, WebSocketServer> entry : webSocketSet.entrySet()) {
			String key = entry.getKey();
			WebSocketServer socketServer = webSocketSet.get(key);
			String sendMsg = "向页面发送消息   " + msg;
			System.out.println(sendMsg);
			//发送全部
			socketServer.sendMsg(msg);
		}
	}
}