import java.io.IOException;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;


public class StartScreen extends Screen {
	private Image startImage;
	
	public StartScreen (String title) {
		super(title);
		// TODO Auto-generated constructor stub
		
        try {
	        startImage = Image.createImage("/mainScreen.png");
        } catch (IOException e) {
        	
        }
        this.append(startImage);
        this.addCommand(startCommand);
	}
	
	public Command startCommand() {
		return startCommand;
	}
}
