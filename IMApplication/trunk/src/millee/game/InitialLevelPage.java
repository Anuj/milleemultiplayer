package millee.game;


import java.util.Vector;

import millee.game.initialize.Screen;
import millee.game.initialize.StartAGame;
import millee.game.initialize.Utilities;
import millee.game.mechanics.Player;
import millee.network.Message;
import millee.network.Network;


public class InitialLevelPage extends Screen {
	
	Network network;
	private int _myID;
	private String myName;
	
	public InitialLevelPage(String title, Network network, int characterChoice, boolean isServer, String myName, String myImagePath, ApplicationMain app) {
		super(title);
		
		this.myName = myName;
		//StringItem str = new StringItem("Colour Colour", network.clientServer.getDeviceName() + "'s game\n");
		
		//this.append(str);
		
		if (isServer) {
			this.append(network.clientServer.getServerName() + "'s game\n");
			this.append("-----------------------------\n");
		}
		
		this.network = network;
		//network.sendReceive();
		
		//this.addCommand(exitCommand);
		
		ApplicationMain.log.trace("in levelstartpage");
		
		if (isServer) {
			this.append("All players are now connected.\nPress START to begin the game!\n");
			//this.addCommand(startCommand);
			ApplicationMain.log.trace("after adding command");
		} else {
			//this.addCommand(startCommand);
			String serverName = network.clientServer.getServerName();
			this.append("You are connected to " + serverName + "'s game.\nWaiting for " + serverName + " to start the game . . .\n");
			
			//sendPlayerInfo(myName, myImagePath);
		}
	}
	
	public Vector setupPlayers(String myName, String myImagePath) {
		 ApplicationMain.log.trace("Server on!");       
		 
		 System.out.println("inside setupPlayers");
		 
		 myName = this.myName;
		 
	     Vector newPlayers = new Vector();
	     StringBuffer initialBroadcast = new StringBuffer("");
	     
	     System.out.println("myName = " + myName);
	     
	     // Set up our own local player first
	     Player player = new Player(myName, myImagePath, 0);
	     newPlayers.addElement(player);
	     //myImagePath = player.getImagePath();
	     //System.out.println("imagePath = " + myImagePath);
	     //Image img = Utilities.createImage(myImagePath);
	     //this.append("\n\nYour character is: ");
	     //this.append(img);
	     
	     System.out.println("created player");
	     
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
	     
	     
	     for (int i = 1; i <= StartAGame.numClients; i++) {
	    	 //ApplicationMain.log.trace("beginning of for loop at iteration: " + i);
	    	 initialBroadcast.append(i);
	    	 initialBroadcast.append(",");
	    	 msg = network.receiveNow(); // Blocks until the messages arrive
	    	 msgs = Utilities.split(msg.msg(), ",", 2);
	    	 
	    	 
	    	 playerName = msgs[0];
	    	 initialBroadcast.append(playerName);
	    	 initialBroadcast.append(",");
	    	 //ApplicationMain.log.trace("playerName = " + playerName);
	    	 
	    	 playerImagePath = msgs[1];
	    	 
	    	 initialBroadcast.append(playerImagePath);
	    	 initialBroadcast.append(";");
	    	 //ApplicationMain.log.trace("playerImagePath = " + playerImagePath);
	    	 
	    	 newPlayers.addElement(new Player(playerName, playerImagePath, i));
				
	    	 // Now to tell the new player their ID:
	    	 network.send(msg.recipient(), String.valueOf(i));
	     }
			
	     network.broadcast(initialBroadcast.toString());
			
	     return newPlayers;
	}
	
	/** Only used by clients.  They only have 1 connection, at index 0 */
	public int sendPlayerInfo(String myName, String myImagePath) {
		network.broadcast(myName + "," + myImagePath);
		//network.broadcast(myImagePath);
		
		_myID = Integer.parseInt(network.receiveNow().msg());
		return _myID;
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
			tmpPlayer = new Player(playerInfo[1], playerInfo[2], Integer.parseInt(playerInfo[0]));
			
			/*
			if (i == _myID) {
				String myImagePath = tmpPlayer.getImagePath();
				System.out.println("imagePath = " + myImagePath);
			    Image img = Utilities.createImage(myImagePath);
			    this.append("\n\nYour name is: " + tmpPlayer.getName().toUpperCase());
			    this.append("\nYour character is: ");
			    this.append(img);
			}
			*/
			
			players.addElement(tmpPlayer);
		}
		
		return players;
		
		
	}
	
	public void addMessage(String msg) {
		this.append(msg);
	}
	
}
