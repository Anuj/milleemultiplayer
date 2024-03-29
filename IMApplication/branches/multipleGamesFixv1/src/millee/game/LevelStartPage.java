package millee.game;


import javax.microedition.lcdui.StringItem;

import millee.game.initialize.Screen;
import millee.network.Network;


public class LevelStartPage extends Screen {
	
	Network network;
	int characterChoice;
	private ApplicationMain _app;

	public LevelStartPage(String title, Network network, int characterChoice, boolean isServer, String myName, String myImagePath, ApplicationMain app) {
		super(title);
		// TODO Auto-generated constructor stub
		
		StringItem str = new StringItem("Colour Colour", "Level 1: Colours");
		StringItem str2 = new StringItem(null, "Ready! Set! Go!");
		
		this.characterChoice = characterChoice;
		this.network = network;
		//network.sendReceive();
		
		this._app = app;
		
		this.append(str);
		this.append(str2);
		this.addCommand(startCommand);
		this.addCommand(exitCommand);
	}
}
