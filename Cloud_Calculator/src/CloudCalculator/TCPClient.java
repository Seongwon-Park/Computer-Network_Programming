package CloudCalculator;

import java.io.*;
import java.net.*;

public class TCPClient {
	
	public static void main(String argv[]) throws Exception
	{
		String ipaddress = null ;	//initialize the variable 
		int portnumber = 0;
		
		 try{
	            File file = new File("ServerInfo.txt");	//create file object of file i.o
	            FileReader filereader = new FileReader(file);	//create input stream of file i.o
	            BufferedReader bufReader = new BufferedReader(filereader);	//create input buffer of file i.o
	            String line = "";
	            String[] serverinfo = new String[8];
	            int i = 0;	//number of storage space that store the server information 
	            while((line = bufReader.readLine()) != null){
	            	serverinfo[i] = line;	
	            	i++;
	            }
	            for (int j = 0; j < 5; j++) {		//0 through 4 contain the manual of the server¡¯s protocol
	            	System.out.println(serverinfo[j]);
	            }
	            
	            ipaddress = serverinfo[5];		//IP address is in the fifth array, port number is in the sixth array
	            portnumber = Integer.parseInt(serverinfo[6]);        
	            bufReader.close();
	            
	        }catch (FileNotFoundException e) {
	        	System.out.println("Failed to load server information."); 	//if failed to load server information, error message output and exit
	        	System.out.println("Load the server's IP address and port number as default.");
	        	ipaddress = "127.0.0.1" ;		//load the default value of server's IP address and port number
	        	portnumber = 6789;
	        	
	        }catch(IOException e){
	            System.out.println(e);
	        }
		
		while(true) {			//persistent TCP connection
			
			String sentence;		//string that input by the user 
			String modifiedSentence;	//string that received by the server
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			Socket clientSocket = new Socket();
			SocketAddress socketAddress = new InetSocketAddress(ipaddress, portnumber);
			try {
				clientSocket.connect(socketAddress, 5000);		//if there is no action, then connection will be cut off by timeout
			} catch(SocketTimeoutException ste) {
				System.out.println("Disconnected by timeout");
				clientSocket.close();
				System.exit(0);
			}
			
			DataOutputStream outToServer = 
					new DataOutputStream(clientSocket.getOutputStream());		//the sentence input by user will send to the server through OutputStream
			BufferedReader inFromServer = 										//the modified sentence will come to the client through InputStrem
					new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			sentence = inFromUser.readLine();		//the sentence is string that input by the user
			outToServer.writeBytes(sentence + '\n');	//the sentence will send through the outStream with String
			modifiedSentence = inFromServer.readLine();
	
				
			System.out.println(modifiedSentence);
			clientSocket.close();
			
			if (sentence.toUpperCase().equals("QUIT")) {		//if the client enters "quit", the input is terminated.
				System.exit(0);
			}
		}	
	}

}

