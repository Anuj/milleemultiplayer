package millee.game.initialize;

import java.io.IOException;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;


public class StartScreen extends Screen {
	private Image startImage;
	
	// Nokia parameters
	private static final int SCREEN_WIDTH = 120;
	private static final int SCREEN_HEIGHT = 115;
	
	public StartScreen (String title) {
		super(title);
		
        try {
	        startImage = Image.createImage("/mainScreen.png");
        } catch (IOException e) {}
        
        this.append(Utilities.resizeImage(startImage, getWidth(), getHeight())); //SCREEN_WIDTH, SCREEN_HEIGHT));
        this.addCommand(startCommand);
	}
	
	public Command getStartCommand() {
		return startCommand;
	}
}
