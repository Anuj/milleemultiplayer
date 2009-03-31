package millee.game;


import javax.microedition.lcdui.StringItem;

import millee.game.initialize.Screen;
import millee.network.Network;


public class LevelStartPage extends Screen {
	
	Network network;
	int characterChoice;
	//private ApplicationMain _app;

	public LevelStartPage(String title, Network network, int characterChoice, boolean isServer, String myName, String myImagePath, ApplicationMain app) {
		super(title);
		// TODO Auto-generated constructor stub
		
		StringItem str = new StringItem("Colour Colour", "Level 1: Colours\n");
		StringItem str2 = new StringItem(null, "Ready! Set! Go!\n\n");
	
		this.append(str);
		this.append(str2);
		
		this.append("--------------------");
		
		
		this.characterChoice = characterChoice;
		this.network = network;
		//network.sendReceive();
		
		//this._app = app;
		
		this.addCommand(exitCommand);
		
		if (isServer) {
			this.append("Press START to begin the next level!");
			this.addCommand(startCommand);
			ApplicationMain.log.trace("after adding command");
		} else {
			//this.addCommand(startCommand);
			this.append("Waiting for server to start the game . . .");
			
			//sendPlayerInfo(myName, myImagePath);
		}
	}
	
	public void addMessage(String msg) {
		this.append(msg);
	}
}
