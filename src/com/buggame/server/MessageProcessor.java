package com.buggame.server;

import java.util.HashMap;

public class MessageProcessor {
	
	HashMap<String, LineParser> hashMap = new HashMap<String, LineParser>();
	LineParser currentRunnable;
	private BugGameServerThread parent;

	class MoveParser implements LineParser {
		
		boolean firstLine = true;

		public void parseLine(String line) {
			if (firstLine) {
				parent.x = Float.parseFloat(line);
				System.out.println("x " + parent.x);
				firstLine = false;
			}
			else {
				parent.y = Float.parseFloat(line);
				System.out.println("y " + parent.y);
				parent.broadcast("<move>");
				parent.broadcast("" + parent.playerId);
				parent.broadcast("" + parent.x);
				parent.broadcast("" + parent.y);
				parent.broadcast("</end>");
			}
		}
		
		public void end() {
			firstLine = true;
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
