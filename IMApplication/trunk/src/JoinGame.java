import java.io.IOException;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.StringItem;


public class JoinGame extends Screen {

	private Image horrorImage, comedyImage, actionImage;
	private int characterChoice, gameChoice;
	StringItem msg;
	Gauge gauge;
	private Command cancelCommand;
	
	public JoinGame(String title) {
		super(title);
		
		cancelCommand = new Command("Cancel", Command.CANCEL, 0);
		
		msg = new StringItem(null, "You have joined game #" + gameChoice);
        try {
	        horrorImage = Image.createImage("/flower2.png");
	        comedyImage = Image.createImage("/mainScreen.png");
	        actionImage = Image.createImage("/flower2.png");
        } catch (IOException e) {
        	
        }
        
        gauge = new Gauge("Connecting to game #" + gameChoice + "...",
        					false,
        					Gauge.INDEFINITE,
        					Gauge.CONTINUOUS_RUNNING);
        
        this.append(msg);
        this.append(horrorImage);
        this.append(gauge);
        this.addCommand(okCommand);
        this.addCommand(cancelCommand);
	}
	
	public void setCharacterChoice(int characterChoice) {
		this.characterChoice = characterChoice;
	}
	
	public void setGameChoice (int gameChoice) {
		this.gameChoice = gameChoice;
	}
}
