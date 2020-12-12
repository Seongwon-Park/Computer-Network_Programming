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
	String person[] = {"Whisper"};	//JCombobox�� �ʱⰪ ���� �� ���� �迭 ����
    String serverAddress;
    String myName;		//JCombobox�� �ڽ��� �̸��� ������ ����� ���� �� �ֵ��� �ڽ��� �̸��� ����
    Scanner in;		//Ŭ���̾�Ʈ�� �Էµ� ���� �޴� Scanner
    PrintWriter out;	//������ ���� ���۵Ǿ� �� ���� ������ PrintWriter
    
    JFrame frame = new JFrame("Chatter");	//GUI�� �ʱ� â �̸��� ����
    	
    JTextField textField = new JTextField(16);	//Client�� �Է��� �� �ִ� JTextField ����
    JTextArea messageArea = new JTextArea(16, 50);	//Client�� �Է��� äĪ������ ǥ�õǴ� JTextArea ����
    
    JButton exit = new JButton("Exit");		//Client�� ������ �� ������ ���� ��ư ����
    JButton send = new JButton("Send") ;	//Client�� �޽����� ���� �� ������ ���� ��ư ����
    JButton update = new JButton("...") ;	//Client�� ���� ����� ������ �� ������ ������Ʈ ��ư ����
    
    JComboBox strCombo= new JComboBox(person);		//Client�� GUI������ ���ϰ� Whisper�� �� �ֵ��� JCombobox ����
    
	private Socket socket;	//������ �����ϴ� Ŭ���̾�Ʈ ���� ����
    
    public ChatClient (String serverAddress) {
    	this.serverAddress = serverAddress;	//�ش��ϴ� IP�� ���޹��� �� ����
    	frame.setResizable(false);	//�������� ũ��� ������ �� ����. �������̴�.
        textField.setEditable(false);	//�г����� �����ϱ� �� �ؽ�Ʈ �Է¶� ��Ȱ��ȭ
        messageArea.setEditable(false);	//ä�ó����� append�ϴ� �޽���â�� ��Ȱ��ȭ
        
        Border lineBorder = BorderFactory.createLineBorder(Color.gray, 1);	//textField �� textArea�� �׵θ� ����, (���� : ȸ�� / �β� : 1)
        Border emptyBorder = BorderFactory.createEmptyBorder(7, 7, 7, 7);	//�׵θ� �ֺ��� ��輱�� �����Ÿ� ����
        
        frame.setSize(800,400);	//�������� ũ�� ����
        frame.setLayout(null);	//�������� ���̾ƿ� ���� (default : border layout)
        
        frame.getContentPane().add(messageArea);	//messageArea�� ���� ����
        messageArea.setBounds(10,10, 760, 300);		//messageArea��ġ ����. ������.
        messageArea.setBorder(BorderFactory.createCompoundBorder(lineBorder, emptyBorder));	//messageArea�� �׵θ� �� �ֺ� object���� �����Ÿ� ����
        
        frame.getContentPane().add(textField);		//textField�� ���� ����
        textField.setBounds(140,320, 450, 30);
        textField.setBorder(BorderFactory.createCompoundBorder(lineBorder, emptyBorder));
        
        frame.add(strCombo);	//strCombo�� ���� ����
        strCombo.setBounds(10, 320, 86, 30);
        
        frame.add(update);		//update��ư ����
        update.setBounds(101, 320, 20, 30);
        
        frame.add(send);	//send��ư ����
        send.setBounds(605, 320, 88, 30);
        	
        frame.add(exit);	//exit��ư ����
        exit.setBounds(692, 320, 78, 30);
        
        frame.setVisible(true);	//�������� Ŭ���̾�Ʈ���� ���̵��� ����
        
        strCombo.addActionListener(new ActionListener() {  	//strCombo�� ���� �̺�Ʈ ����
        	 public void actionPerformed(ActionEvent e) {
        	    String person = strCombo.getSelectedItem().toString();	//�޺��ڽ� ���� ������ �׸��� �����´�.
        		textField.setText("/whisper " + person +" ");			//������ �׸�� �ӼӸ��� ��ɾ �ڵ����� �Էµȴ�.
        		textField.requestFocus();	//�ٷ� ä�� ������ ĥ �� �ֵ��� Ŀ���� textField�� ���ϰ� �Ѵ�.
        	   }
        	  });
        
		exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {	//exit��ư�� ���� �̺�Ʈ ����
            	System.exit(0);		//�ش� ��ư�� ������ �ý����� �ڵ� ����ȴ�.
            }
        });
		
		update.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {	//update ��ư�� ���� �̺�Ʈ ����
            	textField.setText("/update");	//�ش� ��ư�� ������ ��ɾ �ڵ����� �Էµǰ� �ڵ����� ��������.
            	out.println(textField.getText());
                textField.setText("");	//���� �� �ٽ� textField�� �ʱ�ȭ �Ѵ�.
            }
        });
       
        send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {	//send��ư�� ������ �ش� textField�� �ִ� ���ڿ��� ���۵ȴ�.
                out.println(textField.getText());
                textField.setText("");
            }
        });
        
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {	//textField������ ���۹�ư�� �ƴ� ���͸� �Է��ص� �ٷ� ���۵ȴ�.
                out.println(textField.getText());
                textField.setText("");
            }
        });
        
    }
    
    public void updatelist (String line, String myName) {	//�ӼӸ� �޺��ڽ� ���� �ִ� ��ư����, ���� �� ������ ���°� ���ŵȴ�.
    	String Sep = line.substring(17);	//�ý��� �ڵ带 ������ ��������� �޾ƿ´�.
    	String person [] = Sep.split(" ");		//string������ ���� ����� ������ �������� �迭�� �ִ´�.
    	
    	List<String> same = new ArrayList<>(Arrays.asList(person));	//�迭�� ����ȭ ��Ų��, �ڽ��� ����� ��ġ�ϴ� �׸��� �ִٸ�, �����ϰ�
    	same.remove(myName);										//�ٽ� �迭�� �����.
    	person = same.toArray(new String[same.size()]);
    	
    	/**�߰��ϼ���.**/
    	for(int index = 1; index < (strCombo.getItemCount()); index++) {	//�ʱⰪ�� ������ �������� JCombobox�� �׸��� �����Ѵ�.
    		strCombo.removeItemAt(index);
    	}
    	/**�߰��ϼ���.**/
  
        for(String s : person){	//�迭�� �ִ� �ڽ��� ������ �������� �г����� �����ͼ�
        	strCombo.addItem(s);	//�޺��ڽ��� �� �׸� �� ����ִ´�.
        }
	}
    

	private String getName() {	//�г����� �Է¹޴� inputâ
        return JOptionPane.showInputDialog(
            frame,
            "Enter the Nickname : ",
            "Client",
            JOptionPane.PLAIN_MESSAGE
        );
    }
  
    private void run() throws IOException {
        try {
        	socket = new Socket(serverAddress, 5000);	//��Ʈ ��ȣ ���� 
            in = new Scanner(socket.getInputStream());	//������ ���ڿ��� �޾��� scanner ����
            out = new PrintWriter(socket.getOutputStream(), true);	//������ ���� �� ���ڿ��� �޾Ƽ� �������ִ� PrintWriter����

            while (in.hasNextLine()) {	//Scanner�� �������� ���� ���ڰ� �ִ� ��� ��� �ݺ��Ѵ�.
                String line = in.nextLine();
                if (line.startsWith("SUBMITNAME")) {	//default���� SUBMITNAME�̴�. �����κ��� �г����� �Է¹޴´�.
                    out.println(getName());
                } else if (line.startsWith("NAMEACCEPTED")) {	//�ش� �г����� ������ �ƴϰ�, �ߺ����� �ʾ� �г����� ����� �� �ִ�.
                    this.frame.setTitle(line.substring(13) + " ���� ä�ù�");	//�г����� �������� ������ ä��â�� �̸��� �����ȴ�.
                    myName = line.substring(13);	//���� �ڽ��� �г����� �����Ͽ� �Ŀ� �޺��ڽ����� ����ϵ��� �Ѵ�.
                    textField.setEditable(true);	//������ �г����� �¶����� �� ���ڸ� ������ textField�� Ȱ���U �Ǿ���.
                } else {
                	if (line.startsWith("<�ý���> CODEUPDATE ")) {	//Ư���� ������ ���� ���ڿ��� ������ ���� �´ٸ�,
                		updatelist(line, myName);					//�ش� ���ڿ� line�� �ڽ��� �г����� ���� �� �ְ�  �ش� �Լ��� �ҷ�����.
                	}
                	else {
                		 messageArea.append(line + "\n");	//Ư���� ������ ������ �ʴ´ٸ�, ������ ä��â�� ��쵵�� �Ѵ�.
                	}
                   
                }
            }
        } finally {	//������ ����Ǵ� �κ�
            frame.setVisible(false);	//Ŭ���̾�Ʈ�� Scanner�� ����ٸ�, ������ �ʵ��� �ϰ�, �ڿ��� �ݳ��Ѵ�.
            frame.dispose();
        }
    }

    public static void main(String[] args) throws Exception {	//����Ǵ� ���� �Լ�
        String ipaddress = null ;	//������ �ʱ�ȭ�Ѵ�.
		int portnumber = 0;
		
		 try{
	            File file = new File("ServerInfo.txt");	//���������͸� �����Ͽ� �ش��ϴ� ������ �����Ѵ�.
	            FileReader filereader = new FileReader(file);	///���Ͽ��� ���ڿ� ���� FileReader�� �����Ѵ�.
	            BufferedReader bufReader = new BufferedReader(filereader);	//FileReader�� ���� ������ �����ϴ� ������ �����Ѵ�.
	            String[] serverinfo = new String[2];
	            ipaddress = serverinfo[0];		
	            portnumber = Integer.parseInt(serverinfo[1]);        
	            bufReader.close();
	            
	        }catch (FileNotFoundException e) {
	        	System.out.println("���������� �ҷ����µ��� �����Ͽ����ϴ�."); 	
	        	System.out.println("IP�ּҿ� PORT��ȣ�� �ʱⰪ���� �ҷ��ɴϴ�.");
	        	ipaddress = "127.0.0.1" ;		//���� ���������� �ҷ����µ��� �����ϸ�, �ʱⰪ���� �����Ѵ�.
	        	portnumber = 6789;
	        	
	        }catch(IOException e){
	            System.out.println(e);
	        }
        
		 ChatClient client = new ChatClient(ipaddress);	//������ ������ ������ ������ �ִ�.
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	//�ش��ϴ� �������� ������ â�� default���� ������.
        client.frame.setVisible(true);
        client.run();
    }
}