package com.buggame.server;

import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.io.*;

public class BugGameServerThread extends Thread {
    private Socket socket = null;
    private String username = null;
	int playerId;
	public float x;
	public float y;
    private MessageProcessor messageProcessor;
    
    public BugGameServerThread(Socket socket, int playerId) {
        super("BugGameServerThread");
        this.socket = socket;
        this.playerId = playerId;
        messageProcessor = new MessageProcessor(this);
    }
    
    public void run() {
    	MapGenerator gen = new MapGenerator(101);
    	
        try {
        	PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String fromUser;
            out.println("Please enter a username.");
            out.println("<id>");
            out.println(playerId);
            out.println("</end>");
            out.println("<map>");
            out.println(gen.getMapAsString(10, 10));
            out.println("</end>");
            while ((fromUser = in.readLine()) == null) {
        	}
            //System.out.println(fromUser);
            /*
            if (fromUser.indexOf(' ') != -1)
            	username = '"' + fromUser + '"';
            username = fromUser;
            */
            BugGameServer.addPlayer(this, socket);
            //BugGameServer.broadcast("Player " + username + " has connected.");
            while(true) {
            	while ((fromUser = in.readLine()) == null) {
            		pause(0.01);
                }
            	messageProcessor.processLine(fromUser);
            }
            //out.flush();
            //in.flush();
            //socket.close();
        } catch (SocketException e) {
        	BugGameServer.removePlayer(this);
        	BugGameServer.broadcast("Player " + username + " has disconnected.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void broadcast(String str) {
    	ArrayList<Socket> clientSockets = BugGameServer.getClientSockets();
    	
    	for(Socket clientSocket : clientSockets) {
			try {
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				out.println(str);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }	
    	
    }
	
	public String getUsername() {
		return username;
	}
	
	private static void pause (double seconds) {
        Date start = new Date();
        Date end = new Date();
        while (end.getTime() - start.getTime() < seconds * 1000) {
            end = new Date();
        }
    }
}