import java.io.IOException;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.StringItem;


public class StartAGame extends Screen {

	public StartAGame(String title) {
		super(title);
		
		StringItem str = new StringItem("", "Starting a game...");
		// TODO Auto-generated constructor stub
		// Create the Form
		//form=new Form("Choice Group Demo");
        //choiceGroup=new ChoiceGroup("Would you like to:",Choice.EXCLUSIVE);
        /*try {
	        horrorImage = Image.createImage("/flower2.png");
	        comedyImage = Image.createImage("/mainScreen.png");
	        actionImage = Image.createImage("/flower2.png");
        } catch (IOException e) {
        	
        }*/
        
        /*choiceGroup.append("Start a new game?",null);
        choiceGroup.append("Join a game?",null);
        this.append(choiceGroup);*/
        
		this.append(str);
		this.addCommand(okCommand);
        this.addCommand(backCommand);
	}
}
