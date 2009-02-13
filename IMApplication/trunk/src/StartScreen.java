import java.io.IOException;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;


public class StartScreen extends Form {
	Command cmd;
	Image startImage;
	
	public StartScreen (String title) {
		super(title);
		// TODO Auto-generated constructor stub
        cmd=new Command("Start",Command.SCREEN,1);
        try {
	        startImage = Image.createImage("/mainScreen.png");
        } catch (IOException e) {
        	
        }
        this.append(startImage);
        this.addCommand(cmd);
	}

}
