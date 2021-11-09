package com.buggame.server;

import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.io.*;

public class BugGameServer {
	
	private static File file = new File("dictionary.txt");
	private static ArrayList<Character> letterPool = new ArrayList<Character>(); 
	private static byte[] currentChars = new byte[26];
	private static ArrayList<BugGameServerThread> playerThreads = new ArrayList<BugGameServerThread>();
	private static ArrayList<Socket> clientSockets = new ArrayList<Socket>();
	private static ArrayList<String> serverWords = new ArrayList<String>();
	
    public static void main(String[] args) throws IOException {
    	if (args.length != 1) {
    		System.err.println("Usage: java BugGameServer <port number>");
    	    System.exit(1);
    	} 
    	int portNumber = Integer.parseInt(args[0]);  
    	fillLetterPool();
    	new BugGameServerConnectionThread(portNumber).start();
    	
    	while(playerThreads.size() == 0) {
    		pause(.001);
    	}
    	broadcast("Waiting on one more player to join.");
    	while(playerThreads.size() <= 1) {
    		pause(.001);
    	}
        
        broadcast("The game is starting. In 20 seconds, a third letter will be flipped.");
        broadcastPlayerList();
        new BugGameGameThread().start();
    }
    
    private static void generateMap() {
    	//todo: map gen code
    }
    
    private static void fillLetterPool() {
    	byte[] charCount = {13, 3, 3, 6, 18, 3, 4, 3, 12, 2, 2, 5, 3, 8, 11, 3, 2, 9, 6, 9, 6, 3, 3, 2, 3, 2};
    	for (int i = 0; i < charCount.length; i++) {
    		for (int j = 0; j < charCount[i]; j++) {
    			letterPool.add((char) ('a' + i));
    		}
    	}
    }
    
    public static ArrayList<Character> getLetterPool() {
    	return letterPool;
    }
    
    public static void removeLetters(byte[] chars) {
    	//currentChars = Word.difference(chars, currentChars);
    }
    
    public static void broadcast(String str) {
    	
    	for(Socket clientSocket : clientSockets) {

			try {
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
				out.println("Server: " + str);
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
    
    public static void broadcastLetterPool() {
    	String currentCharsString = "";
    	for (int i = 0; i < 26; i++) {
			for (byte j = 0; j < currentChars[i]; j++) {
				currentCharsString += (char) (i + 97) + " ";
			}
		}
    	broadcast("The letter pool currently is: " + currentCharsString.trim());
    }
    
    public static void broadcastWords() {
    	String currentCharsString = "";
    	if (serverWords.size() != 0) {
    		currentCharsString = currentCharsString + "The server has";
    		for (String word : serverWords) {
    			currentCharsString = currentCharsString + " " + word;
    		}
    		currentCharsString = currentCharsString + '.';
    		currentCharsString = currentCharsString + '\n';
    	}
    	for (BugGameServerThread player : playerThreads) {
    		if (player.getWords().size() != 0) {
    			currentCharsString = currentCharsString + player.getUsername() + " has";
    			for (String word : player.getWords()) {
    				currentCharsString = currentCharsString + " " + word;
    			}
        		currentCharsString = currentCharsString + '.';
        		currentCharsString = currentCharsString + '\n';
    		}
    	}
    	if (!currentCharsString.equals(""))
    		broadcast(currentCharsString);
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
				for (String word : player.getWords()) {
					serverWords.add(word);
				}
				playerThreads.remove(i);
			}
		}
    }
    
    public static void flip() {
    	char c = (char) (letterPool.remove((int)(Math.random() * letterPool.size())) - 'a');
    	currentChars[(int) c]++;
    	
    	broadcastLetterPool();
    }
/*    
    public static boolean isWordValid(String str) {
    
    	byte[] word = Word.createCharCount(str);
    	if (Word.isWithin(currentChars, word) && checkDictionary(str))
    		return true;
    	
    	for (String w : serverWords) {
			if (!str.equals(w) && Word.isWithin(Word.add(currentChars, Word.createCharCount(w)), word) && checkDictionary(str)) {
				for (int i = 0; i < serverWords.size(); i++) {
					if (w.equals(serverWords.get(i)));
						serverWords.remove(i);
				}
				broadcast("Word Stolen: " + "server " + w);
				return true;
			}
		}
    	
    	for (BugGameServerThread player : playerThreads) {
    		for (String w : player.getWords()) {
    			if (!str.equals(w) && Word.isWithin(Word.add(currentChars, Word.createCharCount(w)), word) && checkDictionary(str)) {
    				player.removeWord(w);
    				broadcast("Word Stolen: " + player.getUsername() + " " + w);
    				return true;
    			}
    		}
    	}
		return false;
    }
*/
    
    private static boolean checkDictionary(String str) {
    	try (
    		Scanner scanner = new Scanner(file);
    	)
    	{
			int strLen = str.length();
			while (scanner.hasNextLine()) {
	        	String word = scanner.nextLine().toLowerCase();
	        	if (word.length() > strLen)
	        		return false;
	        	if (word.equals(str.toLowerCase()))
	        		return true;
	        }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return false;
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
    		int score = 0;
    		for(String word : player.getWords()) {
    			score += word.length();
    		}
    		if (score > highscore) {
    			winner = player.getUsername();
    			highscore = score;
    		}
    	}
    	if (highscore == -1)
    		broadcast("Error calculating winner.");
    	else broadcast("Player " + winner + " has won, with " + highscore + " letters. The game is now over.");
    }
}