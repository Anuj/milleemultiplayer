package millee.game;


import java.util.Vector;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.StringItem;

import millee.game.initialize.Screen;
import millee.game.initialize.StartAGame;
import millee.game.initialize.Utilities;
import millee.game.mechanics.Player;
import millee.network.Message;
import millee.network.Network;


public class InitialLevelPage extends Screen {
	
	Network network;
	int characterChoice;
	private ApplicationMain _app;

	public InitialLevelPage(String title, Network network, int characterChoice, boolean isServer, String myName, String myImagePath, ApplicationMain app) {
		super(title);
		
		StringItem str = new StringItem("Colour Colour", "Level 1: Colours");
		StringItem str2 = new StringItem(null, "Ready! Set! Go!");
		
		this.characterChoice = characterChoice;
		this.network = network;
		//network.sendReceive();
		
		this._app = app;
		
		this.append(str);
		this.append(str2);
		this.addCommand(exitCommand);
		
		System.out.println("in levelstartpage");
		
		if (isServer) {
			this.append("All clients are connected.  Press START to begin the game");
			//this.addCommand(startCommand);
			System.out.println("after adding command");
		} else {
			//this.addCommand(startCommand);
			this.append("You are connected.  Waiting for server to start the game");
			
			//sendPlayerInfo(myName, myImagePath);
		}
	}
	
	public Vector setupPlayers(String myName, String myImagePath) {
		 System.out.println("Server on!");       
	        
	        
	        Vector newPlayers = new Vector();
	        Image tmpImage = null; 
	        StringBuffer initialBroadcast = new StringBuffer("");
	        
	        // Set up our own local player first
	        tmpImage = Utilities.createImage(myImagePath);
	        newPlayers.addElement(new Player(myName, tmpImage, Player.BLUE, 0, -1));
			
	        initialBroadcast.append(0);
			initialBroadcast.append(",");
			initialBroadcast.append(myName);
			initialBroadcast.append(",");
			initialBroadcast.append(myImagePath);
			initialBroadcast.append(";");
			
			// Poll the network to build up client's player data
	        String playerImagePath, playerName;
	        Message msg;
	        String[] msgs = null;
	        
			for (int i = 1; i <= StartAGame.NUMCLIENTS; i++) {
				//System.out.println("beginning of for loop at iteration: " + i);
				initialBroadcast.append(i);
				initialBroadcast.append(",");
				msg = network.receiveNow(); // Blocks until the messages arrive
				msgs = Utilities.split(msg.msg(), ",", 2);
				
				
				playerName = msgs[0];
				initialBroadcast.append(playerName);
				initialBroadcast.append(",");
				//System.out.println("playerName = " + playerName);
				
				playerImagePath = msgs[1];
				
				initialBroadcast.append(playerImagePath);
				initialBroadcast.append(";");
				//System.out.println("playerImagePath = " + playerImagePath);
				
				tmpImage = Utilities.createImage(playerImagePath);
				newPlayers.addElement(new Player(playerName, tmpImage, Player.RED, i, msg.recipient()));
				
				// Now to tell the new player their ID:
				network.send(msg.recipient(), String.valueOf(i));
			}
			
			network.broadcast(initialBroadcast.toString());
			
			
			
			
	    	//this.append("All the clients have connected.");
	    	//this.append("Choose START to begin the game");
	    	//this.addCommand(startCommand);
	    	
	    	return newPlayers;
	}
	
	/** Only used by clients.  They only have 1 connection, at index 0 */
	public int sendPlayerInfo(String myName, String myImagePath) {
		network.broadcast(myName + "," + myImagePath);
		//network.broadcast(myImagePath);
		
		return Integer.parseInt(network.receiveNow().msg());
		// need to save this to the player at some point and pass into Round
	}
	
	public Vector createPlayersByClients() {
		Vector players = new Vector();
		Message initialMessage = network.receiveNow();
		String[] sPlayers = Utilities.split(initialMessage.msg(), ";", 0);
		String[] playerInfo = null;
		Player tmpPlayer = null;
		
		for (int i = 0; i<sPlayers.length; i++) {
			playerInfo = Utilities.split(sPlayers[i], ",", 3);
			tmpPlayer = new Player(playerInfo[1], Utilities.createImage(playerInfo[2]), Player.BLACK, Integer.parseInt(playerInfo[0]), 0);
			players.addElement(tmpPlayer);
		}
		
		return players;
		
		
	}
	
}
