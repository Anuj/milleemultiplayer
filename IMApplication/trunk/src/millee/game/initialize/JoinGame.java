package millee.game.initialize;

import java.io.IOException;

import javax.microedition.io.StreamConnection;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.StringItem;

import millee.network.ClientServer;
import millee.network.Network;
import millee.network.ReceiverThread;
import millee.network.SenderThread;


public class JoinGame extends Screen implements Runnable {

	private Image horrorImage, comedyImage, actionImage;
	private int characterChoice, gameChoice;
	StringItem msg;
	Gauge gauge;
	private Command cancelCommand;
	private Network network;
	
	public static ClientServer clientServer;
	private boolean m_bRunThread = false;
	private boolean m_bIsServer = false, isServer = false;
	 
	
	public JoinGame(String title, Network network) {
		super(title);
		
		cancelCommand = new Command("Cancel", Command.CANCEL, 0);
		
		msg = new StringItem(null, "You have joined game #" + gameChoice);
        try {
	        horrorImage = Image.createImage("/flower2.png");
	        comedyImage = Image.createImage("/mainScreen.png");
	        actionImage = Image.createImage("/flower2.png");
        } catch (IOException e) {
        	
        }
        
        gauge = new Gauge("Connecting to game #" + gameChoice + "...",
        					false,
        					Gauge.INDEFINITE,
        					Gauge.CONTINUOUS_RUNNING);
        
        
        this.network = network;
        
        this.append(msg);
        this.append(horrorImage);
        this.append(gauge);
        this.addCommand(cancelCommand);
	}
	
	public void setCharacterChoice(int characterChoice) {
		this.characterChoice = characterChoice;
	}
	
	public void setGameChoice (int gameChoice) {
		this.gameChoice = gameChoice;
	}
	
	public void initClient() {
		
		//Network network = new Network();
		network.initializeNetwork(false, 1);
		
		Thread thread = new Thread(network);
        thread.start();
        
        /*try {
			// need to manually wait for client to be connected to server
			// because client doesn't block until it's connected.
			synchronized(network.clientServer.connected) {
				network.clientServer.connected.wait();
			}
        } catch (Exception e) {
			e.printStackTrace();
		}*/
        
        /*while (network.isConnected != true) {
        	try {
        		Thread.sleep(150);
        	} catch ( InterruptedException e ){
        		e.printStackTrace();
			}
        }*/
        
        /*try {
	        synchronized(network.connected) {
	        	System.out.println("synchronized waiting for netwrk.connected");
				network.connected.wait();
			}
        } catch (Exception e) {
        	System.err.println(e);
        }*/
        
        //System.out.println("client connected!");

    	//this.append("All the clients have connected.");
    	//this.append("Choose START to begin the game");
    	this.addCommand(startCommand);
    	
        
        //m_bRunThread = true;
		
	}
	
	public void run() {
		int numClients = 1;
        while (m_bRunThread) {
            try {
            	// Just by creating a new Application object, you run the application
            	// either as a client or server depending on the m_bIsServer variable.
                /*if (app == null) {
                	app = new IMApplication(m_bIsServer, 1);
                	//exitMIDlet();
                }*/
            	
            	clientServer = new ClientServer(isServer, 0);
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
            	
                //sendReceive();
            	
            	System.out.println("client connected!");

            	this.append("All the clients have connected.");
            	this.append("Choose START to begin the game");
            	this.addCommand(startCommand);
            	//clientServer.printToScreen("Application", "Connected.");
        		//runApplication(isServer);
                
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
 
        }
        
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
		
		while (!exitFlag) {
			if ((msg = clientServer.receiveMessage(recvThread)) != null) {
				System.out.println("msg received: " + msg);
			}
			
			//sendThread.sendMsg("sending: blah blah", new Integer(-1));
		}
	}*/
}
