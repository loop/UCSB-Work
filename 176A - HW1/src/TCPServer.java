

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		

    
		
		String clientSentence;
		String capitalisedSentence = "";
		
		System.out.println(args[0]);
		
		
		ServerSocket welcomeSocket = new ServerSocket(Integer.parseInt(args[0]));
		
		
		
		while(true) {
			Socket connectionSocket = welcomeSocket.accept();
			try{
				BufferedReader br = 
		                      new BufferedReader(new InputStreamReader(System.in));
		 
				String input;
		 
				while((input=br.readLine())!=null){
					capitalisedSentence = input;					
				}
		 
			}catch(IOException io){
				io.printStackTrace();
			}	
			
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			
			outToClient.writeBytes("Welcome asdf: " + capitalisedSentence + "Enter a command: (send, print, or exit)" + '\n');
		}

	}

}
