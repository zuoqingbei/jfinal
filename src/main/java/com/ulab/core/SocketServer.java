package com.ulab.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

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
public class SocketServer{


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
        System.out.println(this.session+";"+this.userid+";"+sendMsg);
        try {
			this.session.getBasicRemote().sendText(sendMsg);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    //设置Map,存放每个用户的连接
    public static Map<String,SocketServer> webSocketSet = new HashMap<String,SocketServer>();



    @OnOpen
    public void onOpen(Session session) throws IOException {
        this.session = session;
        //后期换成当前登录人ID  
       // webSocketSet.put(userid, this);
        webSocketSet.put(this.session.getId(), this);//存在bug
        System.out.println(this+"有新连接,session="+session+";userid="+userid);
    }

    @OnClose
    public void onClose() {
        webSocketSet.remove(this.userid);
        System.out.println(this+"；连接关闭");
    }

    @OnMessage
    public void onMessage(String info){
        System.out.println(this+"；来自客户端的消息:" + info);
        String msg = "服务端接收到了来自客户端的消息："+info;
      /*  if(info.contains("userid")){
            this.userid = info.split("userid=")[1];
            System.out.println(this+",this.session="+this.session+";this.userid="+this.userid);
            webSocketSet.put(userid, this);
        }*/
    }

    @OnError
    public void onError(Throwable error) {
        System.out.println(this+"；发生错误");
        error.printStackTrace();
    }


}