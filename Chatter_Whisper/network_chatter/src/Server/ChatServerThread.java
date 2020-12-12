package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class ChatServerThread implements Runnable{
	private String name;	//Ŭ���̾�Ʈ�� �г��� ����
	private Socket Serversocket;	//������ �����ϱ� ���� ���� ����
	private Scanner in;	//Ŭ���̾�Ʈ�� ���ڿ��� ������ scanner ����
	private PrintWriter out;	//Ŭ���̾�Ʈ���Է� ������ PrintWriter ����
	
	HashMap<String, PrintWriter> names;	//names��� �̸��� �ؽ��� ���� 
	
	public ChatServerThread (Socket Serversocket, HashMap names) {
		this.Serversocket = Serversocket;
		this.names = names;
		try {
			
			in = new Scanner(Serversocket.getInputStream());
			out = new PrintWriter(Serversocket.getOutputStream(), true);
			
			while (true) {
				out.println("SUBMITNAME");	//default�� ���� 
				name = in.nextLine();
				if (name == null) {	//�г����� �����̸� default���� �����־�, Ŭ���̾�Ʈ�κ��� �г����� �ٽ� �Է¹޴´�.
					return;
				}
				synchronized (names) {	//������ �ƴϸ�, Ŭ���̾�Ʈ�� ������ ��Ƴ��� �ؽ��ʰ� ����ȭ�� �õ��Ѵ�.
					if (name.length() > 1 && !names.containsKey(name)) {	//���� �г����� ���̰� 1���� ���, �ߺ����� �ʴ´ٸ�,
						names.put(name,out);	//Ŭ���̾�Ʈ�� �̸��� PrintWriter�� ���� ������ �ؽ��ʿ� �����Ѵ�.
						break;	//�г��� ������ �Ϸ�ǰ�, ���ѷ����� �����Ѵ�.
					}
				}
			}
			out.println("NAMEACCEPTED " + name);	//Ŭ���̾�Ʈ���� ���������� �г����� ������ ���� �˸���.
			
			
			
			System.out.println("<�ý���> " + Serversocket.getInetAddress()+" �� ���� �Ϸ��߽��ϴ�.");	//������ ������ Ŭ���̾�Ʈ�� IP�� ����Ѵ�.
			
			broadcast("<�ý���> " + name + " ���� ���Խ��ϴ�.");	//��� Ŭ���̾�Ʈ���� broadcast������� �˸���.
			
			System.out.println("<�ý���> " + name + " ���� ���Խ��ϴ�.");	//������ â���� ����Ѵ�.
			
	
		} catch (ClassCastException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	} 
	
	
	public void run() {
		
		try {
			while(true) {	//������ ����ؼ� Ŭ���̾�Ʈ�� ������ ���ڿ��� broadcast�Ѵ�.
				String input = in.nextLine();	//��� Ư���� ���� �����Ѵ�.
				
				if(input.toLowerCase().equals("/quit")) {	//���� Ŭ���̾�Ʈ�� /quit�̶�� ��ɾ �Է��ߴٸ�, ���ѷ����� ������.
					break;
				}
				else if (input.toLowerCase().startsWith("/whisper")){	//���� /whisper�� �����Ѵٸ� �ش��ϴ� Ŭ���̾�Ʈ���Ը� ���ڸ� ������.
					sendMsg(input);
				}
				else if (input.toLowerCase().startsWith("/update")) {	//���� /update��� 
					updateUserList();		//������ ���� �ؽ��ʿ� ����Ǿ� �ִ� Ŭ���̾�Ʈ����� �ش� Ŭ���̾�Ʈ���� �����Ѵ�.
				}
				else {	//���� ��찡 �ƴ� ��� ��ο��� broadcast������� ���ڸ� �����Ѵ�.
					broadcast(name + " : " + input);
				}
			}
		} catch(Exception e) {
			
		}	//���ѷ����� ������, �� Ŭ���̾�Ʈ�� ������ �����Ѵٸ�.
		finally {
			synchronized(names){
				names.remove(name);	//�ؽ��ʿ��� �ش� Ŭ���̾�Ʈ�� ������ �����.
			}	
			broadcast("<�ý���> " + name + " ���� �������ϴ�.");	//�ش� Ŭ���̾�Ʈ�� �������� �˸���.
			System.out.println("<�ý���> " + name + " ���� �������ϴ�.");
			try {
				if(Serversocket != null) {	//������ �������� ���� �ڿ��� ��ȯ ���� ���� ���
					Serversocket.close();	//�ش��ϴ� ������ ��ȯ�޴´�.
				}
			} catch(Exception e) {}
		}
	}
	
	public void broadcast(String message) {	//broadcast�Լ��� ����
		if (message != null) {	//���� ������ ���� ���ڿ��� ������ �ƴ� ���
			synchronized(names){	//�ؽ��ʿ��� ������ �ҷ��´�.
				for (PrintWriter out : names.values()) {	//�ؽ��ʿ� �ִ� ��� �����鿡�� �޽����� ������. (broadcast���)
					out.println(message);
					out.flush();
				}
			}
		}
	}
	
	public void sendMsg(String message) throws IOException {	//sendMsg�Լ� ���� (�ӼӸ� �Լ�)
		
		String values [] = message.split(" ");	//�ش��ϴ� ���ڿ��� ������ �������� ������.
		String toName = values[1];	//���� ����� 0��° �迭���� /whisper�� ���� ������ ���� 1��° �迭�� �������� ����ִ�.
		String fromName = name;	//�߽����� �޽����� ���� ���
		int msgindex = message.indexOf(values[2]);	//�޽��� �ε����� �����Ͽ�, ������ �ڿ� �޽��� �κ��� �������� �˾Ƴ���.
		String msg = message.substring(msgindex);	//�޽����� ���ۺκ� ���� message�� �����Ѵ�.
		PrintWriter out = names.get(toName);	//�ؽ��ʿ��� �����ο� ���� ������ �����´�.
		PrintWriter in = names.get(fromName);	//���������� �ؽ��ʿ��� �߽��ο� ���� ������ �����´�.
		
		if(msg == null) {
			in.println("<�ý���> ������ �Է��� �ּ���.");	//���� �ӼӸ� ������ ���� ��� ���� �޽����� �߽��ο��� ������.
		}
		
		if(out != null) {	//�ش��ϴ� �������� ������ ���
			
			if(toName.equals(name)) {	//���� �ڽ��� �г��Ӱ� ���� ���
				out.println("<�ý���> �ڱ� �ڽſ��Դ� �ӼӸ��� ���� �� �����ϴ�.");	//�ڱ� �ڽſ��Դ� �ӼӸ��� ���� �� ����.
				out.flush();
			}
			else {	//���������� �۵��� ���
				out.println(name + " �Կ��� ���� �ӼӸ� : " + msg);	//�����ο��� �߽����� ��������, �޽����� ������ �������� ����
				in.println(toName +" �Կ��� ���� �ӼӸ� : " + msg);	//�߽��ο��� �������� ���� �� �ڽ��� ���´� ������ ���
				out.flush();
			}
			
		}
		else if (out == null) {	//���� �ؽ��ʿ� �ش��ϴ� Ŭ���̾�Ʈ�� ���ٸ� ���� �޽����� ������.
			in.println("<�ý���> " + toName +"���� �������� �ʽ��ϴ�.");
		}
	}
	

	public void updateUserList(){	// Ư�� ��ɾ�(/update)�� ���� �ÿ� �۵��ϴ� �Լ��̴�.
		Set set = names.keySet();	
		Iterator iterator = set.iterator();	//�ݺ������� set�� Ž���� Iterator����
		out.print("<�ý���> CODEUPDATE ");	//���� �ش� ��� Ư���� ���ڿ��� ������ Ŭ���̾�Ʈ�� �ν��� �� �ֵ��� �Ѵ�.
		while(iterator.hasNext()){	
			  String key = (String)iterator.next();
			  out.print(key + " ");	//������ ������ ��, ������ �������� ���� �� �ֵ��� ����� �Բ� ����
			  out.flush();
			}
		out.println();
     }

}