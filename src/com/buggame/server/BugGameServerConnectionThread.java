package com.buggame.server;

import java.io.IOException;
import java.net.*;

public class BugGameServerConnectionThread extends Thread {
	private int portNumber;
	
	public BugGameServerConnectionThread(int portNumber) {
		this.portNumber = portNumber;
	}
	
	public void run() {
		//accepts new connections
		try {
			ServerSocket serverSocket = new ServerSocket(portNumber);
			int i = 0;
			while(true) {

				new BugGameServerThread(serverSocket.accept(), i).start();
				
				i++;
			}
		}
		catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
	}

}
