package CloudCalculator;

	import java.io.BufferedReader;
	import java.io.DataOutputStream;
	import java.io.IOException;
	import java.io.InputStreamReader;
	import java.net.ServerSocket;
	import java.net.Socket;

	public class TCPServer {

		public static void main(String[] args) throws IOException 
		{
			String resultString = null;		
			//string that contain correct calculation result 
			String messageString = null;	
			//string that contain incorrect error message
			String clientSentence;			
			//string that is received by the client
			String calculatedSentence = null;	
			//final string buffer that will send to the client 
			ServerSocket welcomeSocket;			
			//server socket that accept the client's request
			
			int correct = 0 ;		
			//to know the received data is correct format, it changes if incorrect then 0, if correct then 1
			int result = 0;			
			//space for the correct calculation result
			int num1 = 0;
			int num2 = 0;
				
			ServerSocket serverSocket = welcomeSocket = new ServerSocket(6789);
			System.out.println("Server start . . \n");		
			//created socket message of activation
			
			while(true) {	//server have to always open
				Socket connectionSocket = welcomeSocket.accept();		
				//when server socket is received request by client, then server socket accept the request
				BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));	
				//storage space that store string sent by the client
				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());		
				//outgoing to the client by output_stream
				clientSentence = inFromClient.readLine(); 		
				// clientSentence contain the data from the client through the socket
				
				System.out.println("FROM CLIENT : " + clientSentence );
				String values [] = clientSentence.split(" ");		
				//split the clientSentence to enter the array and calculate
				int ArrayLength = values.length;		
				//calculate the array's length to check the format is correct
				
				if (ArrayLength > 3) {		
					//if array is longer than 3, that is too many argument
					correct = 0;
					messageString = "Too many arguments." ;		
					//format is incorrect, so error correct number will be 0, and error message contain "too many argument"
				}
				else if ((ArrayLength < 3) && (values[0].toUpperCase().contains("EXIT") == false) ) {	
					//also array is smaller than 3, that format is incorrect. there is no sufficient argument
					correct = 0;
					messageString = "Enter the operation and two arguments." ;	
					//explain the correct format to the client with error message
				}
				else if (values[0].toUpperCase().equals("ADD") || values[0].toUpperCase().equals("MINUS") || values[0].toUpperCase().equals("DIV") || values[0].toUpperCase().equals("MULTI")) {
						//the first array data have to arithmetic operation and that format have to equal server's protocol
					if (values[0].toUpperCase().equals("ADD")) {		
						//if add operation, then add next two number
						correct = 1;									
						//length and operation is correct, so that is correct format. so correct number will be 1
						try {
							num1 = Integer.parseInt(values[1]);			
							//string can't be calculated as integer, so array's number have to change the string to integer
							num2 = Integer.parseInt(values[2]);
						} catch (NumberFormatException e) {
							correct = 0;
							messageString = "Value of number format must be an integer." ;		
							//this case is incorrect case. 2nd and 3rd value must be an integer
						}
						result = num1 + num2;							
						//calculated result also have to integer	
						resultString = Integer.toString(result);		
						//to send the client, integer have to change the string format
						}
					else if (values[0].toUpperCase().equals("MINUS")) {		
						//if minus operation, then minus next two number
						correct = 1;									
						//length and operation is correct, so that is correct format. so correct number will be 1
						try {
							num1 = Integer.parseInt(values[1]);			
							//string can't be calculated as integer, so array's number have to change the string to integer
							num2 = Integer.parseInt(values[2]);
						} catch (NumberFormatException e) {
							correct = 0;
							messageString = "Value of number format must be an integer." ;		
							//this case is incorrect case. 2nd and 3rd value must be an integer
							}
						result = num1 - num2;							
						//calculated result also have to integer	
						resultString = Integer.toString(result);		
						//to send the client, integer have to change the string format
						}
					else if (values[0].toUpperCase().equals("DIV")) {		
						//if division operation, then division next two number
						correct = 1;									
						//length and operation is correct, so that is correct format. so correct number will be 1		
						try {
							num1 = Integer.parseInt(values[1]);			
							//string can't be calculated as integer, so array's number have to change the string to integer
							num2 = Integer.parseInt(values[2]);
						} catch (NumberFormatException e) {
							correct = 0;
							messageString = "Value of number format must be an integer." ;		
							//this case is incorrect case. 2nd and 3rd value must be an integer
						}
					
						if(num1 < num2){			
							//this is integer calculate only, so, if dividend is smaller than divisor that result always 0
							correct = 0;						
							//so in this case, length and operation is correct, but incorrect in server's protocol
							messageString = "Dividend is smaller than divisor." ;		
							//this case is incorrect case. dividend must bigger than divisor
						} else {
							try {
								result = num1 / num2;		
								//when perform the division for two number, if divisor is 0, then occurs eclipse error because that is incorrect with math
							} catch(ArithmeticException e) {		
								//exception handling to prevent eclipse error
								correct = 0;					
								//if divisor is 0, that is contradict the math rule. so correct number will be 0
								messageString = "Devided by zero." ;	
							}
							resultString = Integer.toString(result);		
							//calculated correct result will add correct result with string format
						}
					}
					else if (values[0].toUpperCase().equals("MULTI")) {		
						//if multiplication operation, then multiplication next two number
						correct = 1;										
						//length and operation is correct, so that is correct format. so correct number will be 1
						try {
							num1 = Integer.parseInt(values[1]);			
							//string can't be calculated as integer, so array's number have to change the string to integer
							num2 = Integer.parseInt(values[2]);
						} catch (NumberFormatException e) {
							correct = 0;
							messageString = "Value of number format must be an integer." ;		
							//this case is incorrect case. 2nd and 3rd value must be an integer
							}
						result = num1 * num2;								
						//calculated result also have to integer
						resultString = Integer.toString(result);			
						//to send the client, integer have to change the string format
					}
				}
				else {		
					//if arithmetic operation is incorrect in compare with server's protocol, then that is error with "incorrect arithmetic operations
					correct = 0;
					messageString = "Incorrect arthmetic operations." ;
				}
				
				if (correct == 1) {		
					//if correct number is 1, the clientSentence format is correct and contain resultString with answer in calculatedSentence
					System.out.println("Result : " + resultString);
					calculatedSentence = "Answer : " + resultString + '\n' ;
				}else if (values[0].toUpperCase().equals("QUIT")) {		
					//if client enter the quit, then server will send the thank message
					System.out.println("Clients connection is terminated.");
					calculatedSentence = "Thanks for your using." + '\n' ;
				}else if (correct == 0) {	
					//if correct number is 0, then clientSentence format is incorrect. there is contain error message in calculatedSentence
					System.out.println("Error : " + messageString);
					calculatedSentence = "Error message : " + messageString + '\n' ;
				}

				
				outToClient.writeBytes(calculatedSentence);			
				//finally calculatedSentence will be send by the server through the out_stream
			}
		}
	}
