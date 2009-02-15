package millee.game.mechanics;
import java.util.Random;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;


public class Player {
	private String name;
	
	private Random random;
	private boolean localPlayer;
	private boolean finishedRound;
	
	// Location in cell coordinates
	protected int x;
	protected int y;
	
	// Sprite must have its own coordinates
	protected Sprite sprite;
	
	public Player(String name, Image avatar, boolean localPlayer) {
		
		random = new Random();
		
		this.name = name;
		this.localPlayer = localPlayer;
		this.finishedRound = false;
		
		sprite = new Sprite(avatar);
	}
	
	protected int getInitialXPosition() {
		return random.nextInt(12);
	}
	
	protected int getInitialYPosition() {
		return random.nextInt(11);
	}
	
//	public void interpretMove() {
//		if (!finishedRound) {
//			char command = getCommand();
//			
//			switch (command) {
//				case 'U':
//					y--;
//					sprite.move(0, -15);
//					break;
//				case 'D':
//					y++;
//					sprite.move(0, 15);
//					break;
//				case 'R':
//					x++;
//					sprite.move(15, 0);
//					break;
//				case 'L':
//					x--;
//					sprite.move(-15, 0);
//					break;
//			}
//
//			/*
//			if (command == 'U')
//				sprite.setPosition(x, y-1);
//			else if (command == 'D')
//				sprite.setPosition(x, y+1);
//			else if (command == 'R')
//				sprite.setPosition(x+1, y);
//			else if (command == 'L')
//				sprite.setPosition(x-1, y);
//				
//				*/
//		}
//	}
//	
//	private char getCommand() {
//		int index = random.nextInt(4);
//		return _possibleCommands[index];
//	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	public void finishedRound() {
		this.finishedRound = true;
	}
}
