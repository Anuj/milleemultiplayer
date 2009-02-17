package millee.game.initialize;

import java.io.IOException;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;


public class ChooseCharacter extends Screen {

	Image horrorImage, comedyImage, actionImage;
	
	public ChooseCharacter(String title) {
		super(title);
		// TODO Auto-generated constructor stub
		// Create the Form
		//form=new Form("Choice Group Demo");
        choiceGroup=new ChoiceGroup("Choose your character:",Choice.EXCLUSIVE);
        try {
	        horrorImage = Image.createImage("/flower2.png");
	        comedyImage = Image.createImage("/mainScreen.png");
	        actionImage = Image.createImage("/flower2.png");
        } catch (IOException e) {
        	
        }
        choiceGroup.append("Horror",horrorImage);
        choiceGroup.append("Comedy",comedyImage);
        choiceGroup.append("Action",actionImage);
        this.append(choiceGroup);
        this.addCommand(okCommand);
        this.addCommand(backCommand);
	}
}
