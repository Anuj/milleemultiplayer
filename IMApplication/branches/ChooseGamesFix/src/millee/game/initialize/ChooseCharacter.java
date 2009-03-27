package millee.game.initialize;

import java.io.IOException;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Image;


public class ChooseCharacter extends Screen {

	Image rajImage, sriImage, nehaImage;
	
	public ChooseCharacter(String title) {
		super(title);
		// TODO Auto-generated constructor stub
		// Create the Form
		//form=new Form("Choice Group Demo");
        choiceGroup=new ChoiceGroup("Choose your character:",Choice.EXCLUSIVE);
        try {
	        rajImage = Image.createImage("/flower2.png");
	        sriImage = Image.createImage("/mainScreen.png");
	        nehaImage = Image.createImage("/flower2.png");
        } catch (IOException e) {
        	
        }
        choiceGroup.append("Raj",rajImage);
        choiceGroup.append("Sri",sriImage);
        choiceGroup.append("Neha",nehaImage);
        this.append(choiceGroup);
        this.addCommand(okCommand);
        this.addCommand(backCommand);
	}
	
	public void addMessage(String msg) {
		this.append(msg);
	}
}
