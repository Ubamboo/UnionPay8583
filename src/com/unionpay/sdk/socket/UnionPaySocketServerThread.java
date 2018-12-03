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
				// 1�����������Ӻ󣬴�socket�л�ȡ�����������������������ж�ȡ
				if(inputStream.available()<=0) {
					Thread.sleep(1000);		
					continue;
				}
				// 2�����ĳ���
				byte[] dataLen = new byte[4];
				inputStream.read(dataLen);
				
				
				int len = ((dataLen[0] & 0x0f) * 1000) + ((dataLen[1] & 0x0f) * 100) + (((dataLen[2] & 0x0f)) * 10)
						+ (dataLen[3] & 0x0f);
				System.out.println("��ȡ���ĳ��ȣ�"+len);
				if(len==0) {
					// ������������
					continue;
				}
				// 3��ҵ������
				byte[] msgTypeByte = new byte[4];
				inputStream.read(msgTypeByte);
				String msgType = new String(msgTypeByte, 0, 4, "UTF-8");
				
				if (msgTypeByte.length>=len) {
					System.out.println("��ȡ���ĳ��ȴ���");
					continue;
				}
				
				byte[] dataBytes = new byte[len - msgTypeByte.length];
				int inLen = inputStream.read(dataBytes);

				if (inLen != (len - msgTypeByte.length)) {
					System.out.println("inLen=" + inLen + ";len=" + len);
					System.out.println("���ĳ��ȴ���");
					continue;
				}
				// ������������
				byte[] recDataByte = new byte[dataLen.length + msgTypeByte.length + dataBytes.length];
				System.arraycopy(dataLen, 0, recDataByte, 0, dataLen.length);
				System.arraycopy(msgTypeByte, 0, recDataByte, dataLen.length, msgTypeByte.length);
				System.arraycopy(dataBytes, 0, recDataByte, dataLen.length + msgTypeByte.length, dataBytes.length);

				System.out.println("-----���յ�����-----");
				System.out.println(HexBinary.encode(recDataByte));
				System.out.println("-------���յ�����-----");
				// 4����ȡ���Ľ�������
				UnionPayMessage message = UnionPayMessage.newMessagefrommsgType(msgType);
				if (message == null) {
					System.out.println("---��ȡ���Ľ����쳣--");
					continue;
				}

				// 5����������
				message.parseMessage(recDataByte, 0);
				message.print(message);
				// 6����ȡҵ�����ݴ���
				String serverCode = message.serverCode();
				String className = "com.unionpay.sdk.service.impl.UnionPayBM" + serverCode;
				UnionPayBM uPayBM = null;
				try {
					Class classType = Class.forName(className);
					uPayBM = (UnionPayBM) classType.newInstance();
					returnMap = uPayBM.perform(message);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("ҵ��δ��ͨ��" + msgType);
				}				

				byte[] send = HexBinary.decode(returnMap.get("msg").toString());

				System.out.println("---���ز���" + HexBinary.encode(send));
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
