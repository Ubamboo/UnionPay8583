package com.unionpay.sdk.socket;



import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PostConstruct;

public class UnionPaySocketServer implements Runnable {

	private ServerSocket server;
	private ExecutorService cachedThreadPool;
	

	@PostConstruct
	public void init() throws IOException {
		String socketPort ="8080";

		server = new ServerSocket(Integer.parseInt(socketPort));
		cachedThreadPool = Executors.newCachedThreadPool();
		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		System.out.println("UnionPaySocket started...");
		Socket socket = null;
		while (true) {	
			try {
				
				socket = server.accept();
				System.out.println("----------socket accept-------");
				System.out.println("----------socket:"+socket);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			cachedThreadPool.submit(new UnionPaySocketServerThread(socket));
		}

	}

	public static void main(String[] args) {
		UnionPaySocketServer server=new UnionPaySocketServer();
		try {
			server.init();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
