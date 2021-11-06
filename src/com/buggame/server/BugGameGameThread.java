package com.buggame.server;

import java.util.Date;

public class BugGameGameThread extends Thread {
	
	public BugGameGameThread() {
        super("BugGameGameThread");
    }
	
	public void run() {
		BugGameServer.flip();
		BugGameServer.flip();
		while (BugGameServer.getLetterPool().size() != 0) {
			pause(20);
			BugGameServer.flip();
		}
		BugGameServer.broadcast("There are no more unflipped letters. The game will automatically end in 60 seconds.");
		pause(40);
		BugGameServer.endGame();
	}
	private static void pause (double seconds) {
        Date start = new Date();
        Date end = new Date();
        while (end.getTime() - start.getTime() < seconds * 1000) {
            end = new Date();
        }
    }
}