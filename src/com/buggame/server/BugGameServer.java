package com.buggame.server;

import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.io.*;

public class BugGameServer {
	private static ArrayList<BugGameServerThread> playerThreads = new ArrayList<BugGameServerThread>();
	private static ArrayList<Socket> clientSockets = new ArrayList<Socket>();
	//private static ArrayList<String> serverWords = new ArrayList<String>();
	private static int[][] map;
	
    public static void main(String[] args) throws IOException {
    	if (args.length != 1) {
    		System.err.println("Usage: java BugGameServer <port number>");
    	    System.exit(1);
    	} 
    	int portNumber = Integer.parseInt(args[0]);  
    	new BugGameServerConnectionThread(portNumber).start();
    	
    	while(playerThreads.size() == 0) {
    		pause(.001);
    	}
    	broadcast("Waiting on one more player to join.");
    	while(playerThreads.size() <= 5) {
    		pause(.001);
    	}
        
        broadcast("The game is starting. In 20 seconds, a third letter will be flipped.");
        broadcastPlayerList();
        new BugGameGameThread().start();
    }
    
    private static void generateMap() {
    	//todo: map gen code
    }
    
    public static void broadcast(String str) {
    	
    	for(Socket clientSocket : clientSockets) {

			try {
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
				out.println(str);
				out.flush();
					
			} catch (IOException e) {
				e.printStackTrace();
			}
			
        }	
    	
    }
    
    private static void broadcastPlayerList() {
    	String playerList = "Player List: ";
    	for(BugGameServerThread player : playerThreads) {
    		playerList += player.getUsername() + " ";
        }
    	broadcast(playerList.trim());
    }
    
    public static ArrayList<Socket> getClientSockets() {
    	return clientSockets;
    }
    
    public static void addPlayer(BugGameServerThread player, Socket socket) {
    	playerThreads.add(player);
    	clientSockets.add(socket);
    }
    
    public static void removePlayer(BugGameServerThread player) {
    	for (int i = 0; i < playerThreads.size(); i++) {
			if (playerThreads.get(i).equals(player)) {
				playerThreads.remove(i);
			}
		}
    }
    
    
    private static void pause (double seconds) {
        Date start = new Date();
        Date end = new Date();
        while (end.getTime() - start.getTime() < seconds * 1000) {
            end = new Date();
        }
    }
    
    public static void endGame() {
    	String winner = null;
    	int highscore = -1;
    	for (BugGameServerThread player : playerThreads) {
    	}
    	if (highscore == -1)
    		broadcast("Error calculating winner.");
    	else broadcast("Player " + winner + " has won, with " + highscore + " letters. The game is now over.");
    }
}