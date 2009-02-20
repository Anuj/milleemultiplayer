package millee.game;


import java.util.Vector;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.StringItem;

import millee.game.initialize.Screen;
import millee.game.initialize.StartAGame;
import millee.game.initialize.Utilities;
import millee.game.mechanics.Player;
import millee.network.Network;


public class LevelStartPage extends Screen {
	
	Network network;
	int characterChoice;

	public LevelStartPage(String title, Network network, int characterChoice, boolean isServer, String myName, String myImagePath) {
		super(title);
		// TODO Auto-generated constructor stub
		
		StringItem str = new StringItem("Colour Colour", "Level 1: Colours");
		StringItem str2 = new StringItem(null, "Ready! Set! Go!");
		
		this.characterChoice = characterChoice;
		this.network = network;
		//network.sendReceive();
		
		
		this.append(str);
		this.append(str2);
		this.addCommand(exitCommand);
		
		if (isServer) {
			this.append("All clients are connected.  Press START to begin the game");
			this.addCommand(startCommand);
		} else {
			this.append("All clients are connected.  Waiting for server to start the game");
			sendPlayerInfo(myName, myImagePath);
		}
	}
	
	public Vector setupPlayers(String myName, String myImagePath) {
		 System.out.println("Server on!");       
	        
	        
	        Vector newPlayers = new Vector();
	        Image tmpImage = null; 
	        
	        // Set up our own local player first
	        tmpImage = Utilities.createImage(myImagePath);
	        newPlayers.addElement(new Player(myName, tmpImage, 0));
			
			// Poll the network to build up client's player data
	        String playerImagePath, playerName;
			for (int i = 1; i <= StartAGame.NUMCLIENTS; i++) {
				System.out.println("beginning of for loop at iteration: " + i);
				playerName = network.receiveNow(); // Blocks until the messages arrive
				System.out.println("playerName = " + playerName);
				playerImagePath = network.receiveNow();
				System.out.println("playerImagePath = " + playerImagePath);
				
				tmpImage = Utilities.createImage(playerImagePath);
				newPlayers.addElement(new Player(playerName, tmpImage, i));
				
				// Now to tell the new player their ID:
				network.send(i, String.valueOf(i));
			}
			
			
	    	//this.append("All the clients have connected.");
	    	//this.append("Choose START to begin the game");
	    	//this.addCommand(startCommand);
	    	
	    	return newPlayers;
	}
	
	/** Only used by clients.  They only have 1 connection, at index 0 */
	public void sendPlayerInfo(String myName, String myImagePath) {
		network.broadcast(myName);
		network.broadcast(myImagePath);
	}

}
