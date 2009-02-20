package millee.game.initialize;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.io.StreamConnection;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.StringItem;

import millee.game.mechanics.GameGrid;
import millee.game.mechanics.Player;
import millee.network.ClientServer;
import millee.network.Network;
import millee.network.ReceiverThread;
import millee.network.SenderThread;


public class StartAGame extends Screen {
	
	public static ClientServer clientServer;
	private boolean m_bRunThread = false;
	private boolean m_bIsServer = true, isServer = true;
	private Network network;

	// TODO: move NUMCLIENTS + setupPlayers code elsewhere
	public static final int NUMCLIENTS = 3;
	
	
	public StartAGame(String title, Network network) {
		super(title);
		
		this.network = network;
		
		this.append("Started a game...");
		this.append("Waiting for players to join");
        this.addCommand(backCommand);
        
	}
	
	public Vector setupNetworkPlayers(String myName, String myImagePath) {
		//Network network = new Network();
		network.initializeNetwork(true, NUMCLIENTS);
		
		Thread thread = new Thread(network);
        thread.start();

        
        /*try {
	        synchronized(network.connected) {
	        	network.connected.wait();
	        }
        } catch (Exception e) {
        	e.printStackTrace();
        }*/
        
        /** Comment out until Nplayer intialization works
        System.out.println("Server on!");       
        
        
        Vector newPlayers = new Vector();
        Image tmpImage = null; 
        
        // Set up our own local player first
        tmpImage = Utilities.createImage(myImagePath);
        newPlayers.addElement(new Player(myName, tmpImage, 0));
		
		// Poll the network to build up client's player data
        String playerImagePath, playerName;
		for (int i = 1; i <= NUMCLIENTS; i++) {
			playerName = network.receiveNow(); // Blocks until the messages arrive
			playerImagePath = network.receiveNow();
			tmpImage = Utilities.createImage(playerImagePath);
			newPlayers.addElement(new Player(playerName, tmpImage, i));
			
			// Now to tell the new player their ID:
			network.send(i, String.valueOf(i));
		}
		
		
    	this.append("All the clients have connected.");
    	this.append("Choose START to begin the game");
    	this.addCommand(startCommand);
    	
    	return newPlayers;
    	*/
        return null;
	}
	
	public void run() {
		
		
		
		/** DEAD CODE
		int numClients = 1;
        //while (m_bRunThread) {
            try {
            	// Just by creating a new Application object, you run the application
            	// either as a client or server depending on the m_bIsServer variable.
                /*if (app == null) {
                	app = new IMApplication(m_bIsServer, 1);
                	//exitMIDlet();
                }
            	
            	clientServer = new ClientServer(isServer, numClients);
            	if (isServer) {
        			//this.numClients = numClients;
        			clientServer.InitServer(numClients);
        		} else {
        			clientServer.InitClient();
        			try {
        				// need to manually wait for client to be connected to server
        				// because client doesn't block until it's connected.
        				synchronized(clientServer.connected) {
        					clientServer.connected.wait();
        				}
        			} catch (Exception e) {
        				e.printStackTrace();
        			}
        		}
            	
                sendReceive();
            	
            	System.out.println("server connected!");
            	
            	this.append("All the clients have connected.");
            	this.append("Choose START to begin the game");
            	this.addCommand(startCommand);
            	
            	//clientServer.printToScreen("Application", "Connected.");
        		//runApplication(isServer);
                
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
            
        //}
         * 
         */
	}
	
	/*private void sendReceive() {
		SenderThread sendThread = null;
		ReceiverThread recvThread = null;
		StreamConnection[] streamConns;
		ClientServer clientServer;
		
		if (isServer) {
			System.out.println("before clientserver init");
			clientServer = StartAGame.clientServer;
			System.out.println("after clientserver init");
			streamConns = clientServer.getStreamConnections();
			System.out.println("after getStreamConnection(): " + streamConns);
			sendThread = new SenderThread(streamConns);
			System.out.println("after creating sendThread");
			recvThread = new ReceiverThread(streamConns[0], sendThread, isServer);
			System.out.println("after creating recvThread");
			recvThread.start();
			System.out.println("after starting recvThread");
	    	sendThread.start();
			System.out.println("after starting sendThread");

		} else {
			clientServer = JoinGame.clientServer;
			streamConns = clientServer.getStreamConnections();
			sendThread = new SenderThread(streamConns);
			recvThread = new ReceiverThread(streamConns[0], sendThread, isServer);
			recvThread.start();
	    	sendThread.start();
		}
		
		String msg = null;
		msg = clientServer.receiveMessage(recvThread);
		boolean exitFlag = false;
		
		//while (!exitFlag) {
			//if ((msg = clientServer.receiveMessage(recvThread)) != null) {
			//	System.out.println("msg received: " + msg);
			//}
		
		if (isServer) {
			sendThread.sendMsg("u", new Integer(-1));
			sendThread.sendMsg("d", new Integer(-1));
			sendThread.sendMsg("r", new Integer(-1));
			sendThread.sendMsg("l", new Integer(-1));
		} else {
			while (!exitFlag) {
				if ((msg = clientServer.receiveMessage(recvThread)) != null) {
					System.out.println("msg received: " + msg);
				}
				
				//sendThread.sendMsg("sending: blah blah", new Integer(-1));
			}
		}
		//}
	}*/
	
}
