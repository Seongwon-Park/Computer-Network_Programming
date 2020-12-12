package Server;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ChatServer {
	ServerSocket server;	//클라이언트의 요청을 받아주는 서버 소켓 생성
	final static int port = 5000;	//서버의 포트 번호 선언
	Socket child;	//클라이언트의 요청을 받아주고 쓰레드를 생성해 주는 소켓 생성
	
	HashMap<String, PrintWriter>names;	//클라이언트의 정보를 보관하는 서버의 해쉬맵 선언
	
	public ChatServer() {
		try {
			server = new ServerSocket(port);	//서버 시작
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("Server is Running. . . ");
		
		names = new HashMap<String, PrintWriter>();
		
		while(true) {
			try {
				child = server.accept();	//클라이언트의 요청을 받아주고
				ChatServerThread childThread = new ChatServerThread(child, names);	//그 때마다 새로운 쓰레드 생성
				Thread t = new Thread(childThread);
				t.start();	//쓰레드 시작
			}catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
	}
	public static void main(String[] args) {
		new ChatServer();
	}
}
