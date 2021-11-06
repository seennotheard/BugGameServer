package com.buggame.server;

import java.io.IOException;
import java.net.*;

public class BugGameServerConnectionThread extends Thread {
	private int portNumber;
	
	public BugGameServerConnectionThread(int portNumber) {
		this.portNumber = portNumber;
	}
	
	public void run() {
		
		try {
			ServerSocket serverSocket = new ServerSocket(portNumber);
			while(true) {
				while (true) {
					new BugGameServerThread(serverSocket.accept()).start();
				}
			}
		}
		catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
	}

}
