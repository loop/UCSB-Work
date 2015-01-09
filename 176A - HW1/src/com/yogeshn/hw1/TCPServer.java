package com.yogeshn.hw1;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class TCPServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        String x = null;  
        while( (x = input.readLine()) != null ) {    
            System.out.println(x); 
        }    
    
		
		String clientSentence;
		String capitalisedSentence;
		
		System.out.println(args[0]);
		
		
		ServerSocket welcomeSocket = new ServerSocket(Integer.parseInt(args[0]));
		
		
		
		while(true) {
			Socket connectionSocket = welcomeSocket.accept();
			
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			
			clientSentence = inFromClient.readLine();
			
			capitalisedSentence = clientSentence.toUpperCase() + '\n';
			
			outToClient.writeBytes(capitalisedSentence);
		}

	}

}
