import java.io.IOException;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;


public class ChooseGame extends Screen {

	private Image horrorImage, comedyImage, actionImage;
	
	public ChooseGame(String title) {
		super(title);
		choiceGroup=new ChoiceGroup("Which game would you like?",Choice.EXCLUSIVE);
		try {
	        horrorImage = Image.createImage("/flower2.png");
	        comedyImage = Image.createImage("/mainScreen.png");
	        actionImage = Image.createImage("/flower2.png");
        } catch (IOException e) {
        	
        }
        choiceGroup.append("Raj",horrorImage);
        choiceGroup.append("Sri",comedyImage);
        choiceGroup.append("Neha",actionImage);
        this.append(choiceGroup);
        this.addCommand(okCommand);
        this.addCommand(backCommand);
	}
}
