package millee.game.initialize;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;


public class StartOrJoinGame extends Screen {

	public StartOrJoinGame(String title) {
		super(title);
		
		// TODO Auto-generated constructor stub
		// Create the Form
		//form=new Form("Choice Group Demo");
        choiceGroup=new ChoiceGroup("Would you like to:",Choice.EXCLUSIVE);
        /*try {
	        horrorImage = Image.createImage("/flower2.png");
	        comedyImage = Image.createImage("/mainScreen.png");
	        actionImage = Image.createImage("/flower2.png");
        } catch (IOException e) {
        	
        }*/
        
        choiceGroup.append("Start a new game?",null);
        choiceGroup.append("Join a game?",null);
        this.append(choiceGroup);
        this.addCommand(okCommand);
        this.addCommand(backCommand);
	}
	
	public void addMessage(String msg) {
		this.append(msg);
	}
}
