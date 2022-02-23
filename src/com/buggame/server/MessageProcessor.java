package com.buggame.server;

import java.util.HashMap;

public class MessageProcessor {
	
	HashMap<String, LineParser> hashMap = new HashMap<String, LineParser>();
	LineParser currentRunnable;
	private BugGameServerThread parent;

	class MoveParser implements LineParser {
		
		int lineCount = 0;

		public void parseLine(String line) {
			if (lineCount == 0) {
				parent.x = Float.parseFloat(line);
			}
			else if (lineCount == 1) {
				parent.y = Float.parseFloat(line);
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
			lineCount++;
		}
		
		public void end() {
			lineCount = 0;
		}
	}
	
	class AntHillMoveParser implements LineParser {
		
		boolean firstLine = true;

		public void parseLine(String line) {
		}
		
		public void end() {
			firstLine = true;
		}
	}
	
	class CreateAntHillParser implements LineParser {
		
		boolean firstLine = true;

		public void parseLine(String line) {
		}
		
		public void end() {
			firstLine = true;
		}
	}
	
	
	LineParser moveParser = new MoveParser();
	LineParser antHillMoveParser = new AntHillMoveParser();
	LineParser createAntHillParser = new CreateAntHillParser();
	
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