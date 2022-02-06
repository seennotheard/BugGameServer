package com.buggame.server;

import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

public class BugGameServerThread extends Thread {
    private Socket socket = null;
    private String username = null;
	private ArrayList<String> words = new ArrayList<String>();
	int playerId;
	int x;
	int y;
    
    
    public BugGameServerThread(Socket socket, int playerId) {
    	
        super("BugGameServerThread");
        this.socket = socket;
        this.playerId = playerId;
        
    }
    
    public void run() {
    	MapGenerator gen = new MapGenerator(101);
    	
        try {
        	PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String fromUser;
            out.println("Please enter a username.");
            out.println("<map>");
            out.println(gen.getMapAsString(10, 10));
            out.println("</end>");
            while ((fromUser = in.readLine()) == null) {
        	}
            //System.out.println(fromUser);
            if (fromUser.indexOf(' ') != -1)
            	username = '"' + fromUser + '"';
            username = fromUser;
            BugGameServer.addPlayer(this, socket);
            BugGameServer.broadcast("Player " + username + " has connected.");
            while(true) {
            	while ((fromUser = in.readLine()) == null) {
            		pause(0.01);
                }
            	if(fromUser.substring(0, 6).equals("chat=")) {
            		broadcast(fromUser.substring(6));
            	}
            	if(fromUser.substring(0, 6).equals("move=")) {
            		String xString = "";
            		for (int i = 5; i < fromUser.length(); i++) {
            			if (fromUser.charAt(i) == ',') {
            				x = Integer.parseInt(fromUser.substring(6, i));
            				y = Integer.parseInt(fromUser.substring(i + 1));
            				break;
            			}
            			else {
            				xString = xString + fromUser.charAt(i);
            			}
            		}
            		//broadcast the move or smth, wip
            	}
            	//System.out.println(fromUser);
            	//broadcast(fromUser);
                //Pattern pattern = Pattern.compile("\\w+");
                //Matcher matcher = pattern.matcher(fromUser);
                /*
                while (matcher.find()) {
                	String word = matcher.group();
                    if (word != null && BugGameServer.isWordValid(word)) {
                    	//BugGameServer.removeLetters(Word.createCharCount(word));
                    	BugGameServer.broadcast(username + " got \"" + word + "\".");
                    	words.add(word);
                    	BugGameServer.broadcastLetterPool();
                    	BugGameServer.broadcastWords();
                    }
                }
                */
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
    
    private void broadcast(String str) {
    	ArrayList<Socket> clientSockets = BugGameServer.getClientSockets();
    	
    	for(Socket clientSocket : clientSockets) {
			try {
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				out.println(username + ": " + str);
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