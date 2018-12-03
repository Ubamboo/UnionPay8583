package com.unionpay.sdk.socket;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

import com.unionpay.sdk.eunm.UnionPayValueType;
import com.unionpay.sdk.message.UnionPayMessage;

public class UnionPaySocket {	
	

	  public static void main0100(String args[]) throws Exception {
		  UnionPayMessage message=UnionPayMessage.newMessagefrommsgType("0100");
			message.setValue(3, 310000, UnionPayValueType.NUMERIC, 6);
			message.setValue(7, new Date(), UnionPayValueType.DATE10, 0);
			message.setValue(11, 930908, UnionPayValueType.NUMERIC, 6);
			message.setValue(12, new Date(), UnionPayValueType.TIME, 0);
			message.setValue(13, new Date(), UnionPayValueType.DATE4, 0);
			message.setValue(15, new Date(), UnionPayValueType.DATE4, 0);
			message.setValue(18, 4814, UnionPayValueType.NUMERIC, 4);
			message.setValue(22, 012, UnionPayValueType.NUMERIC, 3);
			message.setValue(25, 92, UnionPayValueType.NUMERIC, 2);
			message.setValue(32, "00163000", UnionPayValueType.LLVAR, 0);
			message.setValue(33, "M0000202", UnionPayValueType.LLVAR, 0);
			message.setValue(37, "171608086527", UnionPayValueType.NUMERIC, 12);
			message.setValue(41, 2, UnionPayValueType.NUMERIC, 8);
			message.setValue(42, "300000054113002", UnionPayValueType.NUMERIC, 15);
			message.setValue(48, "FDDHN10352000000001     0000000042235#", UnionPayValueType.LLLVAR, 0);
			message.setValue(49, 156, UnionPayValueType.NUMERIC, 3);
			message.setValue(59, "BI2011110200#", UnionPayValueType.LLLVAR, 0);
			message.setValue(60, "00000000160000", UnionPayValueType.LLLVAR, 0);
			message.setValue(61, "999", UnionPayValueType.LLLVAR, 0);
			message.setValue(103, "DUZF3054", UnionPayValueType.LLLVAR, 0);
			message.setValue(128, "", UnionPayValueType.LLVAR, 0);
			
	    // 要连接的服务端IP地址和端口
	    String host = "127.0.0.1";
	    int port = 9080;
	    // 与服务端建立连接
	    Socket socket = new Socket(host, port);
	    // 建立连接后获得输出流
	    OutputStream outputStream = socket.getOutputStream();
	    String sendMsg=message.writeInternal();
	    System.out.println("sendMsg="+sendMsg);
	    socket.getOutputStream().write(sendMsg.getBytes("UTF-8"));
	    
	    //通过shutdownOutput高速服务器已经发送完数据，后续只能接受数据
	    socket.shutdownOutput();
	    
	    InputStream inputStream = socket.getInputStream();
	    byte[] bytes = new byte[1024];
	    int len;
	    StringBuilder sb = new StringBuilder();
	    while ((len = inputStream.read(bytes)) != -1) {
	      //注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
	      sb.append(new String(bytes, 0, len,"UTF-8"));
	    }
	    System.out.println("get message from server: " + sb);
	    
	    inputStream.close();
	    outputStream.close();
	    socket.close();
	  }
	  
	  public static void main2(String[] args) throws Exception {
		  UnionPayMessage message=UnionPayMessage.newMessagefrommsgType("0200");
			
			message.setValue(2,"6221235200000000001", UnionPayValueType.LLVAR, 0);
			message.setValue(3, "190000",UnionPayValueType.NUMERIC, 6);
			message.setValue(4, "200",UnionPayValueType.NUMERIC, 12);		
			message.setValue(7, new Date(),UnionPayValueType.DATE10, 0);
			message.setValue(11,"099993",UnionPayValueType.NUMERIC, 6);
			message.setValue(12, new Date(),UnionPayValueType.TIME, 0);
			message.setValue(13, new Date(),UnionPayValueType.DATE4, 0);
			message.setValue(15,new Date(),UnionPayValueType.DATE4, 0);
			message.setValue(18,"4814", UnionPayValueType.NUMERIC, 4);
			message.setValue(22,"012", UnionPayValueType.NUMERIC, 3);
			message.setValue(25,"81", UnionPayValueType.NUMERIC, 2);
			message.setValue(32, "48021910",UnionPayValueType.LLVAR, 0);
			message.setValue(33,"M0000184", UnionPayValueType.LLVAR, 0);
			message.setValue(37, "181031220015",UnionPayValueType.NUMERIC, 12);
			message.setValue(41, "01007611",UnionPayValueType.NUMERIC, 8);
			message.setValue(42, "898150148141650",UnionPayValueType.NUMERIC, 15);
			message.setValue(48, "PADHN10352000000001     0000000042235#",UnionPayValueType.LLLVAR, 0);
			message.setValue(49, "156",UnionPayValueType.NUMERIC, 3);
			message.setValue(60, "00000000030000",UnionPayValueType.LLLVAR, 0);
			message.setValue(103, "000M0000184",UnionPayValueType.LLVAR, 0);
			message.setValue(128, "",UnionPayValueType.LLVAR, 0);
			
			  // 要连接的服务端IP地址和端口
		    String host = "127.0.0.1";
		    int port = 9080;
		    // 与服务端建立连接
		    Socket socket = new Socket(host, port);
		    // 建立连接后获得输出流
		    OutputStream outputStream = socket.getOutputStream();
		    String sendMsg=message.writeInternal();
		    System.out.println("sendMsg="+sendMsg);
		    socket.getOutputStream().write(sendMsg.getBytes("UTF-8"));
		    
		    //通过shutdownOutput高速服务器已经发送完数据，后续只能接受数据
		    socket.shutdownOutput();
		    
		    InputStream inputStream = socket.getInputStream();
		    byte[] bytes = new byte[1024];
		    int len;
		    StringBuilder sb = new StringBuilder();
		    while ((len = inputStream.read(bytes)) != -1) {
		      //注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
		      sb.append(new String(bytes, 0, len,"UTF-8"));
		    }
		    System.out.println("get message from server: " + sb);
		    
		    inputStream.close();
		    outputStream.close();
		    socket.close();
	}
	  public static void main(String[] args) throws Exception {
		  UnionPayMessage message=UnionPayMessage.newMessagefrommsgType("0800");
			message.setValue(7, new Date(), UnionPayValueType.DATE10, 0);
			message.setValue(11, 930908, UnionPayValueType.NUMERIC, 6);
			message.setValue(33, "M0000202", UnionPayValueType.LLVAR, 0);
			message.setValue(53, "1000000000000000", UnionPayValueType.NUMERIC, 16);
			message.setValue(70, 101, UnionPayValueType.NUMERIC, 3);
			message.setValue(96, "1111111111111112", UnionPayValueType.LLVAR, 0);
			message.setValue(128, "", UnionPayValueType.ALPHA, 16);
			
			  // 要连接的服务端IP地址和端口
		    String host = "120.253.100.66";
		    int port = 8081;
		    // 与服务端建立连接
		    Socket socket = new Socket(host, port);
		    // 建立连接后获得输出流
		    OutputStream outputStream = socket.getOutputStream();
		    String sendMsg=message.writeInternal();
		    System.out.println("sendMsg="+sendMsg);
		    socket.getOutputStream().write(sendMsg.getBytes("UTF-8"));
		    
		    //通过shutdownOutput高速服务器已经发送完数据，后续只能接受数据
		    socket.shutdownOutput();
		    
		    InputStream inputStream = socket.getInputStream();
		    byte[] bytes = new byte[1024];
		    int len;
		    StringBuilder sb = new StringBuilder();
		    while ((len = inputStream.read(bytes)) != -1) {
		      //注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
		      sb.append(new String(bytes, 0, len,"UTF-8"));
		    }
		    System.out.println("get message from server: " + sb);
		    
		    inputStream.close();
		    outputStream.close();
		    socket.close();
	}
}
