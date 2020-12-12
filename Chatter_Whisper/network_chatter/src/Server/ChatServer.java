package Server;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ChatServer {
	ServerSocket server;	//Ŭ���̾�Ʈ�� ��û�� �޾��ִ� ���� ���� ����
	final static int port = 5000;	//������ ��Ʈ ��ȣ ����
	Socket child;	//Ŭ���̾�Ʈ�� ��û�� �޾��ְ� �����带 ������ �ִ� ���� ����
	
	HashMap<String, PrintWriter>names;	//Ŭ���̾�Ʈ�� ������ �����ϴ� ������ �ؽ��� ����
	
	public ChatServer() {
		try {
			server = new ServerSocket(port);	//���� ����
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("Server is Running. . . ");
		
		names = new HashMap<String, PrintWriter>();
		
		while(true) {
			try {
				child = server.accept();	//Ŭ���̾�Ʈ�� ��û�� �޾��ְ�
				ChatServerThread childThread = new ChatServerThread(child, names);	//�� ������ ���ο� ������ ����
				Thread t = new Thread(childThread);
				t.start();	//������ ����
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
