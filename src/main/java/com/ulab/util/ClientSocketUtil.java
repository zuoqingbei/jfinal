package com.ulab.util;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.jfinal.kit.PropKit;

public class ClientSocketUtil {

	public static Socket server;
	private boolean isConnected=true;
	public ClientSocketUtil(){//链接自己，本机上测试的时候用
		try {
			if(server==null)
			server = new Socket(PropKit.get("socket.domain"),Integer.parseInt(PropKit.get("socket.port")));
		} catch (Exception e) {
			isConnected=false;
		} 
	}
	public ClientSocketUtil(String url,int port){
		try {
			server = new Socket(url,port);
		} catch (Exception e) {
			isConnected=false;
		} 
	}
//		 向服务端程序发送数据
	public void send(String data){
		try {
			OutputStreamWriter osw = new OutputStreamWriter(server.getOutputStream());
			BufferedWriter bw = new BufferedWriter(osw);
			bw.write(data+"\r\n");
			bw.flush();
			/*DataInputStream dos=new DataInputStream(server.getInputStream());
			//dos.read(data.getBytes());
			String res=dos.readUTF();
			dos.close();*/
		} catch (IOException e) {
		}
	}
	/**
	 * 从服务端程序接收数据,返回一个BufferedReader
	 * @return
	 */
	public BufferedReader recieve(){
		InputStreamReader isr=null;
		BufferedReader br=null;
		try {
			isr = new InputStreamReader(server.getInputStream(),"GBK");
			br = new BufferedReader(isr);
		} catch (IOException e) {
		}
		return br;
	}

	public void close(){
		 try {
			 if(server!=null||server.isConnected()){
				 server.close();
			 }
		} catch (IOException e) {
		}
	}
	public boolean isConnected() {
		return isConnected;
	}
	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}
	
	public static void main(String[] args) {
		ClientSocketUtil util=new ClientSocketUtil();
		util.send("getflystatus");
		BufferedReader br=util.recieve();
		String s = "";        
        try {
			while((s = br.readLine()) != null)
			    System.out.println(s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		util.close();
	}
	
}
