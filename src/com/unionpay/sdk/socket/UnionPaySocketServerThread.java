package com.unionpay.sdk.socket;




import java.io.DataInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.unionpay.sdk.message.UnionPayMessage;
import com.unionpay.sdk.service.UnionPayBM;
import com.unionpay.sdk.util.HexBinary;

public class UnionPaySocketServerThread implements Runnable {

	private Socket socket;

	private DataInputStream inputStream;
	private OutputStream outputStream;

	public UnionPaySocketServerThread(Socket socket) {
		try {
			this.socket = socket;	
			this.socket.setKeepAlive(true);			
			this.inputStream = new DataInputStream(socket.getInputStream());
			this.outputStream = socket.getOutputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			while (!socket.isClosed()&&!socket.isInputShutdown()) {
				// 1、建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
				if(inputStream.available()<=0) {
					Thread.sleep(1000);		
					continue;
				}
				// 2、报文长度
				byte[] dataLen = new byte[4];
				inputStream.read(dataLen);
				
				
				int len = ((dataLen[0] & 0x0f) * 1000) + ((dataLen[1] & 0x0f) * 100) + (((dataLen[2] & 0x0f)) * 10)
						+ (dataLen[3] & 0x0f);
				System.out.println("获取报文长度："+len);
				if(len==0) {
					// 不处理心跳包
					continue;
				}
				// 3、业务类型
				byte[] msgTypeByte = new byte[4];
				inputStream.read(msgTypeByte);
				String msgType = new String(msgTypeByte, 0, 4, "UTF-8");
				
				if (msgTypeByte.length>=len) {
					System.out.println("获取报文长度错误");
					continue;
				}
				
				byte[] dataBytes = new byte[len - msgTypeByte.length];
				int inLen = inputStream.read(dataBytes);

				if (inLen != (len - msgTypeByte.length)) {
					System.out.println("inLen=" + inLen + ";len=" + len);
					System.out.println("报文长度错误");
					continue;
				}
				// 拷贝所有数据
				byte[] recDataByte = new byte[dataLen.length + msgTypeByte.length + dataBytes.length];
				System.arraycopy(dataLen, 0, recDataByte, 0, dataLen.length);
				System.arraycopy(msgTypeByte, 0, recDataByte, dataLen.length, msgTypeByte.length);
				System.arraycopy(dataBytes, 0, recDataByte, dataLen.length + msgTypeByte.length, dataBytes.length);

				System.out.println("-----接收到参数-----");
				System.out.println(HexBinary.encode(recDataByte));
				System.out.println("-------接收到参数-----");
				// 4、获取报文解析处理
				UnionPayMessage message = UnionPayMessage.newMessagefrommsgType(msgType);
				if (message == null) {
					System.out.println("---获取报文解析异常--");
					continue;
				}

				// 5、解析数据
				message.parseMessage(recDataByte, 0);
				message.print(message);
				// 6、获取业务数据处理
				String serverCode = message.serverCode();
				String className = "com.unionpay.sdk.service.impl.UnionPayBM" + serverCode;
				UnionPayBM uPayBM = null;
				try {
					Class classType = Class.forName(className);
					uPayBM = (UnionPayBM) classType.newInstance();
					returnMap = uPayBM.perform(message);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("业务未开通：" + msgType);
				}				

				byte[] send = HexBinary.decode(returnMap.get("msg").toString());

				System.out.println("---返回参数" + HexBinary.encode(send));
				outputStream.write(send);
			}
			System.out.println("socket close------");
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		/*finally {
			System.out.println("-------socket-----close------");
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
				if(socket!=null) {
					socket.close();
					
				}
				
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}*/

	}
	

}
