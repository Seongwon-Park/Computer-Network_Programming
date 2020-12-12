package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class ChatServerThread implements Runnable{
	private String name;	//클라이언트의 닉네임 선언
	private Socket Serversocket;	//서버와 연결하기 위한 소켓 생성
	private Scanner in;	//클라이언트의 문자열이 들어오는 scanner 선언
	private PrintWriter out;	//클라이언트에게로 나가는 PrintWriter 선언
	
	HashMap<String, PrintWriter> names;	//names라는 이름의 해쉬맵 선언 
	
	public ChatServerThread (Socket Serversocket, HashMap names) {
		this.Serversocket = Serversocket;
		this.names = names;
		try {
			
			in = new Scanner(Serversocket.getInputStream());
			out = new PrintWriter(Serversocket.getOutputStream(), true);
			
			while (true) {
				out.println("SUBMITNAME");	//default값 선언 
				name = in.nextLine();
				if (name == null) {	//닉네임이 공백이면 default으로 남아있어, 클라이언트로부터 닉네임을 다시 입력받는다.
					return;
				}
				synchronized (names) {	//공백이 아니면, 클라이언트의 정보를 담아놓은 해쉬맵과 동기화를 시도한다.
					if (name.length() > 1 && !names.containsKey(name)) {	//만약 닉네임의 길이가 1보다 길고, 중복되지 않는다면,
						names.put(name,out);	//클라이언트의 이름과 PrintWriter에 대한 정보를 해쉬맵에 저장한다.
						break;	//닉네임 설정이 완료되고, 무한루프를 종료한다.
					}
				}
			}
			out.println("NAMEACCEPTED " + name);	//클라이언트에게 최종적으로 닉네임이 수락된 것을 알린다.
			
			
			
			System.out.println("<시스템> " + Serversocket.getInetAddress()+" 와 연결 완료했습니다.");	//연결을 성공한 클라이언트의 IP를 출력한다.
			
			broadcast("<시스템> " + name + " 님이 들어왔습니다.");	//모든 클라이언트에게 broadcast방식으로 알린다.
			
			System.out.println("<시스템> " + name + " 님이 들어왔습니다.");	//서버의 창에도 출력한다.
			
	
		} catch (ClassCastException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	} 
	
	
	public void run() {
		
		try {
			while(true) {	//서버는 계속해서 클라이언트가 보내는 문자열을 broadcast한다.
				String input = in.nextLine();	//몇몇 특수한 경우는 제외한다.
				
				if(input.toLowerCase().equals("/quit")) {	//만약 클라이언트가 /quit이라는 명령어를 입력했다면, 무한루프를 나간다.
					break;
				}
				else if (input.toLowerCase().startsWith("/whisper")){	//만약 /whisper로 시작한다면 해당하는 클라이언트에게만 문자를 보낸다.
					sendMsg(input);
				}
				else if (input.toLowerCase().startsWith("/update")) {	//만약 /update라면 
					updateUserList();		//서버는 현재 해쉬맵에 저장되어 있는 클라이언트목록을 해당 클라이언트에게 전달한다.
				}
				else {	//위의 경우가 아닐 경우 모두에게 broadcast방식으로 문자를 전달한다.
					broadcast(name + " : " + input);
				}
			}
		} catch(Exception e) {
			
		}	//무한루프를 나오면, 즉 클라이언트가 접속을 종료한다면.
		finally {
			synchronized(names){
				names.remove(name);	//해쉬맵에서 해당 클라이언트의 정보를 지운다.
			}	
			broadcast("<시스템> " + name + " 님이 나갔습니다.");	//해당 클라이언트가 나갔음을 알린다.
			System.out.println("<시스템> " + name + " 님이 나갔습니다.");
			try {
				if(Serversocket != null) {	//유저는 나갔으나 아직 자원을 반환 받지 못한 경우
					Serversocket.close();	//해당하는 소켓을 반환받는다.
				}
			} catch(Exception e) {}
		}
	}
	
	public void broadcast(String message) {	//broadcast함수의 정의
		if (message != null) {	//만약 유저가 보낸 문자열이 공백이 아닐 경우
			synchronized(names){	//해쉬맵에서 정보를 불러온다.
				for (PrintWriter out : names.values()) {	//해쉬맵에 있는 모든 유저들에게 메시지를 보낸다. (broadcast방식)
					out.println(message);
					out.flush();
				}
			}
		}
	}
	
	public void sendMsg(String message) throws IOException {	//sendMsg함수 정의 (귓속말 함수)
		
		String values [] = message.split(" ");	//해당하는 문자열을 공백을 기준으로 나눈다.
		String toName = values[1];	//나눈 결과로 0번째 배열에는 /whisper가 들어가고 다음에 오는 1번째 배열에 수신인이 들어있다.
		String fromName = name;	//발신인은 메시지를 보낸 사람
		int msgindex = message.indexOf(values[2]);	//메시지 인덱스를 선언하여, 수신인 뒤에 메시지 부분의 시작점을 알아낸다.
		String msg = message.substring(msgindex);	//메시지의 시작부분 부터 message로 선언한다.
		PrintWriter out = names.get(toName);	//해쉬맵에서 수신인에 대한 정보를 가져온다.
		PrintWriter in = names.get(fromName);	//마찬가지로 해쉬맵에서 발신인에 대한 정보를 가져온다.
		
		if(msg == null) {
			in.println("<시스템> 내용을 입력해 주세요.");	//만약 귓속말 내용이 없을 경우 에러 메시지를 발신인에게 보낸다.
		}
		
		if(out != null) {	//해당하는 수신인이 존재할 경우
			
			if(toName.equals(name)) {	//만약 자신의 닉네임과 같을 경우
				out.println("<시스템> 자기 자신에게는 귓속말을 보낼 수 없습니다.");	//자기 자신에게는 귓속말을 보낼 수 없다.
				out.flush();
			}
			else {	//정상적으로 작동한 경우
				out.println(name + " 님에게 받은 귓속말 : " + msg);	//수신인에게 발신인이 누구인지, 메시지의 내용은 무엇인지 전달
				in.println(toName +" 님에게 보낸 귓속말 : " + msg);	//발신인에게 수신인의 정보 및 자신이 보냈던 내용을 출력
				out.flush();
			}
			
		}
		else if (out == null) {	//만약 해쉬맵에 해당하는 클라이언트가 없다면 에러 메시지를 보낸다.
			in.println("<시스템> " + toName +"님이 존재하지 않습니다.");
		}
	}
	

	public void updateUserList(){	// 특정 명령어(/update)를 받을 시에 작동하는 함수이다.
		Set set = names.keySet();	
		Iterator iterator = set.iterator();	//반복적으로 set을 탐색할 Iterator선언
		out.print("<시스템> CODEUPDATE ");	//만약 해당 경우 특수한 문자열로 시작해 클라이언트가 인식할 수 있도록 한다.
		while(iterator.hasNext()){	
			  String key = (String)iterator.next();
			  out.print(key + " ");	//유저가 수신할 시, 공백을 기준으로 나눌 수 있도록 공백과 함께 전송
			  out.flush();
			}
		out.println();
     }

}