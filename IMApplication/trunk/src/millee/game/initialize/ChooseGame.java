package millee.game.initialize;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Image;

import millee.game.ApplicationMain;


public class ChooseGame extends Screen {

	private Image horrorImage, comedyImage, actionImage;
	
	public ChooseGame(String title) {
		super(title);
		
	}
	
	public ChooseGame(String title, Vector devicesDiscoveredNames) {
		this(title);
		choiceGroup=new ChoiceGroup("Choose game to join",Choice.EXCLUSIVE);
		try {
	        horrorImage = Image.createImage("/flower2.png");
	        //comedyImage = Image.createImage("/mainScreen.png");
	        //actionImage = Image.createImage("/flower2.png");
        } catch (IOException e) {
        	
        }
        boolean finished = false;
        for(int i = 0; i<devicesDiscoveredNames.size(); i++) {
        	String currElem = (String) devicesDiscoveredNames.elementAt(i);
        	ApplicationMain.log.trace("found device: " + currElem);
        	if (!currElem.equals("finished")) {
            	choiceGroup.append(currElem,horrorImage);
        	} else {
        		finished = true;
        	}
        }
        
        //choiceGroup.append("Raj",horrorImage);
        //choiceGroup.append("Sri",comedyImage);
        //choiceGroup.append("Neha",actionImage);
        this.append(choiceGroup);
        if (!finished) {
        	this.append("...still searching...");
        }
        //this.addCommand(joinCommand);
        this.addCommand(backCommand);
	}
	
	
}
