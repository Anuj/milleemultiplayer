package millee.game.mechanics;
import java.util.Random;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;


public class Player {
	private String name;
	private String imagePath;
	private Sprite sprite;
	private Random random;
	private boolean localPlayer;
	private boolean finishedRound;
	
	
	public Player(String name, String imagePath, boolean localPlayer) {
		
		random = new Random();
		
		this.name = name;
		this.imagePath = imagePath;
		this.localPlayer = localPlayer;
		this.finishedRound = false;
		
		try {
			Image img = Image.createImage(imagePath);
			sprite = new Sprite(img);
			sprite.setPosition(getInitialXPosition(), getInitialYPosition());
		} catch (Exception e) {
			System.out.println("Couldn't create player: " + name);
		}
	}
	
	private int getInitialXPosition() {
		return random.nextInt(100);
	}
	
	private int getInitialYPosition() {
		return random.nextInt(100);
	}
	
	public void updateSpriteLocation() {
		if (!finishedRound) {
			char command = getCommand();
			int x = sprite.getX();
			int y = sprite.getY();
			if (command == 'U')
				sprite.setPosition(x, y-4);
			else if (command == 'D')
				sprite.setPosition(x, y+4);
			else if (command == 'R')
				sprite.setPosition(x+4, y);
			else if (command == 'L')
				sprite.setPosition(x-4, y);
		}
	}
	
	private char getCommand() {
		char commands[] = {'U', 'D', 'R', 'L'};
		int index = random.nextInt(4)%4;
		return commands[index];
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	public void finishedRound() {
		this.finishedRound = true;
	}
}
