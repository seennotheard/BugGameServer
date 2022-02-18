package com.buggame.server;

import java.util.HashMap;

public class MessageProcessor {
	
	HashMap<String, LineParser> hashMap = new HashMap<String, LineParser>();
	LineParser currentRunnable;
	private BugGameServerThread parent;

	class MoveParser implements LineParser {
		
		int moveCount = 0;

		public void parseLine(String line) {
			if (moveCount == 0) {
				parent.x = Float.parseFloat(line);
				moveCount ++;
			}
			else if (moveCount == 1) {
				parent.y = Float.parseFloat(line);
				moveCount ++;
			}
			else {
				parent.rotation = Integer.parseInt(line);
				parent.broadcast("<move>");
				parent.broadcast("" + parent.playerId);
				parent.broadcast("" + parent.x);
				parent.broadcast("" + parent.y);
				parent.broadcast("" + parent.rotation);
				parent.broadcast("</end>");
			}
		}
		
		public void end() {
			moveCount = 0;
		}
	}
	
	
	LineParser moveParser = new MoveParser();
	
	public MessageProcessor(BugGameServerThread parent) {
		this.parent = parent;
		hashMap.put("<move>", moveParser);
		currentRunnable = null;
	}
	
	public void processLine(String line) {
		if (currentRunnable == null) {
			currentRunnable = hashMap.get(line);
		}
		else {
			if (line.equals("</end>")) {
				currentRunnable.end();
				currentRunnable = null;
			}
			else {
				currentRunnable.parseLine(line);
			}
		}
	}
}