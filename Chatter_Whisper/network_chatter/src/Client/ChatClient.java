package Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class ChatClient {
	String person[] = {"Whisper"};	//JCombobox의 초기값 설정 및 사용될 배열 선언
    String serverAddress;
    String myName;		//JCombobox시 자신의 이름을 제외한 목록이 보일 수 있도록 자신의 이름을 선언
    Scanner in;		//클라이언트의 입력된 값을 받는 Scanner
    PrintWriter out;	//서버로 부터 전송되어 온 값이 들어오는 PrintWriter
    
    JFrame frame = new JFrame("Chatter");	//GUI의 초기 창 이름을 설정
    	
    JTextField textField = new JTextField(16);	//Client가 입력할 수 있는 JTextField 설정
    JTextArea messageArea = new JTextArea(16, 50);	//Client가 입력한 채칭내용이 표시되는 JTextArea 설정
    
    JButton exit = new JButton("Exit");		//Client가 종료할 때 누르는 종료 버튼 설정
    JButton send = new JButton("Send") ;	//Client가 메시지를 보낼 때 누르는 전송 버튼 설정
    JButton update = new JButton("...") ;	//Client가 유저 목록을 가져올 때 누르는 업데이트 버튼 설정
    
    JComboBox strCombo= new JComboBox(person);		//Client가 GUI상으로 편하게 Whisper할 수 있도록 JCombobox 선언
    
	private Socket socket;	//서버와 소통하는 클라이언트 소켓 선언
    
    public ChatClient (String serverAddress) {
    	this.serverAddress = serverAddress;	//해당하는 IP를 전달받은 후 연결
    	frame.setResizable(false);	//프레임의 크기는 조절할 수 없다. 고정적이다.
        textField.setEditable(false);	//닉네임을 설정하기 전 텍스트 입력란 비활성화
        messageArea.setEditable(false);	//채팅내용을 append하는 메시지창도 비활성화
        
        Border lineBorder = BorderFactory.createLineBorder(Color.gray, 1);	//textField 및 textArea의 테두리 설정, (색상 : 회색 / 두께 : 1)
        Border emptyBorder = BorderFactory.createEmptyBorder(7, 7, 7, 7);	//테두리 주변의 경계선의 여유거리 선언
        
        frame.setSize(800,400);	//프레임의 크기 지정
        frame.setLayout(null);	//프레임의 레이아웃 해제 (default : border layout)
        
        frame.getContentPane().add(messageArea);	//messageArea에 대한 설정
        messageArea.setBounds(10,10, 760, 300);		//messageArea위치 선언. 고정적.
        messageArea.setBorder(BorderFactory.createCompoundBorder(lineBorder, emptyBorder));	//messageArea의 테두리 및 주변 object와의 여유거리 설정
        
        frame.getContentPane().add(textField);		//textField에 대한 설정
        textField.setBounds(140,320, 450, 30);
        textField.setBorder(BorderFactory.createCompoundBorder(lineBorder, emptyBorder));
        
        frame.add(strCombo);	//strCombo에 대한 설정
        strCombo.setBounds(10, 320, 86, 30);
        
        frame.add(update);		//update버튼 설정
        update.setBounds(101, 320, 20, 30);
        
        frame.add(send);	//send버튼 설정
        send.setBounds(605, 320, 88, 30);
        	
        frame.add(exit);	//exit버튼 설정
        exit.setBounds(692, 320, 78, 30);
        
        frame.setVisible(true);	//프레임이 클라이언트에게 보이도록 설정
        
        strCombo.addActionListener(new ActionListener() {  	//strCombo에 대한 이벤트 설정
        	 public void actionPerformed(ActionEvent e) {
        	    String person = strCombo.getSelectedItem().toString();	//콤보박스 내에 선택한 항목을 가져온다.
        		textField.setText("/whisper " + person +" ");			//선택한 항목과 귓속말의 명령어가 자동으로 입력된다.
        		textField.requestFocus();	//바로 채팅 내용을 칠 수 있도록 커서를 textField로 향하게 한다.
        	   }
        	  });
        
		exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {	//exit버튼에 대한 이벤트 설정
            	System.exit(0);		//해당 버튼을 누르면 시스템이 자동 종료된다.
            }
        });
		
		update.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {	//update 버튼에 대한 이벤트 설정
            	textField.setText("/update");	//해당 버튼을 누르면 명령어가 자동으로 입력되고 자동으로 보내진다.
            	out.println(textField.getText());
                textField.setText("");	//보낸 후 다시 textField를 초기화 한다.
            }
        });
       
        send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {	//send버튼을 누르면 해당 textField에 있는 문자열이 전송된다.
                out.println(textField.getText());
                textField.setText("");
            }
        });
        
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {	//textField내에서 전송버튼이 아닌 엔터를 입력해도 바로 전송된다.
                out.println(textField.getText());
                textField.setText("");
            }
        });
        
    }
    
    public void updatelist (String line, String myName) {	//귓속말 콤보박스 옆에 있는 버튼으로, 누를 시 유저의 상태가 갱신된다.
    	String Sep = line.substring(17);	//시스템 코드를 제외한 유저목록을 받아온다.
    	String person [] = Sep.split(" ");		//string형태의 유저 목록을 공백을 기준으로 배열에 넣는다.
    	
    	List<String> same = new ArrayList<>(Arrays.asList(person));	//배열을 문자화 시킨후, 자신의 내용과 일치하는 항목이 있다면, 삭제하고
    	same.remove(myName);										//다시 배열로 만든다.
    	person = same.toArray(new String[same.size()]);
    	
    	/**추가하세요.**/
    	for(int index = 1; index < (strCombo.getItemCount()); index++) {	//초기값을 제외한 나머지의 JCombobox의 항목을 제거한다.
    		strCombo.removeItemAt(index);
    	}
    	/**추가하세요.**/
  
        for(String s : person){	//배열에 있는 자신을 제외한 유저들의 닉네임을 가져와서
        	strCombo.addItem(s);	//콤보박스에 한 항목 씩 집어넣는다.
        }
	}
    

	private String getName() {	//닉네임을 입력받는 input창
        return JOptionPane.showInputDialog(
            frame,
            "Enter the Nickname : ",
            "Client",
            JOptionPane.PLAIN_MESSAGE
        );
    }
  
    private void run() throws IOException {
        try {
        	socket = new Socket(serverAddress, 5000);	//포트 번호 설정 
            in = new Scanner(socket.getInputStream());	//유저의 문자열을 받아줄 scanner 선언
            out = new PrintWriter(socket.getOutputStream(), true);	//서버로 부터 온 문자열을 받아서 전달해주는 PrintWriter선언

            while (in.hasNextLine()) {	//Scanner에 전달해줄 다음 인자가 있는 경우 계속 반복한다.
                String line = in.nextLine();
                if (line.startsWith("SUBMITNAME")) {	//default값은 SUBMITNAME이다. 유저로부터 닉네임을 입력받는다.
                    out.println(getName());
                } else if (line.startsWith("NAMEACCEPTED")) {	//해당 닉네임이 공백이 아니고, 중복되지 않아 닉네임을 사용할 수 있다.
                    this.frame.setTitle(line.substring(13) + " 님의 채팅방");	//닉네임이 정해지면 유저의 채팅창의 이름이 설정된다.
                    myName = line.substring(13);	//그중 자신의 닉네임을 추출하여 후에 콤보박스에서 사용하도록 한다.
                    textField.setEditable(true);	//유저가 닉네임을 승락받은 후 문자를 보내는 textField이 활성홛 되었다.
                } else {
                	if (line.startsWith("<시스템> CODEUPDATE ")) {	//특정한 서식을 가진 문자열이 서버로 부터 온다면,
                		updatelist(line, myName);					//해당 문자열 line과 자신의 닉네임을 전달 해 주고  해당 함수를 불러오자.
                	}
                	else {
                		 messageArea.append(line + "\n");	//특정한 서식을 가지지 않는다면, 유저의 채팅창에 띄우도록 한다.
                	}
                   
                }
            }
        } finally {	//무조건 실행되는 부분
            frame.setVisible(false);	//클라이언트의 Scanner가 비었다면, 보이지 않도록 하고, 자원을 반납한다.
            frame.dispose();
        }
    }

    public static void main(String[] args) throws Exception {	//실행되는 메인 함수
        String ipaddress = null ;	//변수를 초기화한다.
		int portnumber = 0;
		
		 try{
	            File file = new File("ServerInfo.txt");	//파일포인터를 선언하여 해당하는 파일을 실행한다.
	            FileReader filereader = new FileReader(file);	///파일에서 문자열 읽을 FileReader를 선언한다.
	            BufferedReader bufReader = new BufferedReader(filereader);	//FileReader가 읽은 정보를 저장하는 공간을 선언한다.
	            String[] serverinfo = new String[2];
	            ipaddress = serverinfo[0];		
	            portnumber = Integer.parseInt(serverinfo[1]);        
	            bufReader.close();
	            
	        }catch (FileNotFoundException e) {
	        	System.out.println("파일정보를 불러오는데에 실패하였습니다."); 	
	        	System.out.println("IP주소와 PORT번호를 초기값으로 불러옵니다.");
	        	ipaddress = "127.0.0.1" ;		//만약 서버정보를 불러오는데에 실패하면, 초기값으로 실행한다.
	        	portnumber = 6789;
	        	
	        }catch(IOException e){
	            System.out.println(e);
	        }
        
		 ChatClient client = new ChatClient(ipaddress);	//연결할 서버의 정보를 가지고 있다.
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	//해당하는 프레임은 윈도우 창의 default값을 따른다.
        client.frame.setVisible(true);
        client.run();
    }
}