package millee.network;

import java.util.Vector;

import javax.microedition.io.StreamConnection;

import millee.game.ApplicationMain;


public class Network implements Runnable {
	
	public ClientServer clientServer = null;
	private boolean m_bRunThread = true;
	private boolean isServer;
	private int numClients;
	Vector sendBuffer, receiverBuffer;
	SenderThread sendThread = null;
	ReceiverThread recvThread = null;
	StreamConnection[] streamConns;
	
	public Object connected = new Object();
	public boolean isConnected = false;
	
	//Constants
	private static final int SLEEP_TIME = 300;

	
	private ApplicationMain _app;
	
	public Network (ApplicationMain am) {
		_app = am;
	}
	
	public void initializeNetwork(boolean isServer, int numClients, ApplicationMain _app) {
		if (numClients <= 0)
				ApplicationMain.log.trace("Number of clients is non-positive");
		this.isServer = isServer;
		this.numClients = numClients;
		sendBuffer = new Vector();
		receiverBuffer = new Vector();

    	clientServer = new ClientServer(isServer, numClients, _app);
	}
	
	public void run() {
            try {
            	if (isServer) {
            		// Blocks until it connects to numClients
        			clientServer.InitServer(numClients);
        			ApplicationMain.log.info("Done connecting to all clients");
        		} else {
        			int numDevicesDiscovered = 0;
        			int temp = 0;
        			clientServer.InitClient();
        			try {
        				
        				/*while (true) {
        					if ((temp = clientServer.numDevicesDiscovered) > numDevicesDiscovered) {
        						ApplicationMain.log.trace("discovered a new device");
        						numDevicesDiscovered = temp;
        						_app.updateDevicesDiscovered(clientServer.devicesDiscoveredNames);
        						Thread.sleep(SLEEP_TIME);
        						if (((String) clientServer.devicesDiscoveredNames.elementAt(temp-1)).equals("finished")) {
        							break;
        						}
        					}
        				}*/
        				// need to manually wait for client to be connected to server
        				// because client doesn't block until it's connected.
        				synchronized(clientServer.connected) {
        					clientServer.connected.wait();
        				}
        			} catch (Exception e) {
        				e.printStackTrace();
        			}
        		}
            	
            	
            	/*synchronized(connected) {
            		connected.notifyAll();
				}*/

            	clientServer.createIOThreads();
            	
            	isConnected = true;
            	
            	ApplicationMain.log.trace("_app: " + _app);
            	_app.fullyConnected();
            	
            	/*
        		if (isServer) {
        			streamConns = clientServer.getStreamConnections();
        			sendThread = new SenderThread(streamConns);
        			recvThread = new ReceiverThread(streamConns[0], sendThread, isServer);
        			recvThread.start();
        	    	sendThread.start();

        		} else {
        			streamConns = clientServer.getStreamConnections();
        			sendThread = new SenderThread(streamConns);
        			recvThread = new ReceiverThread(streamConns[0], sendThread, isServer);
        			recvThread.start();
        	    	sendThread.start();
        		}*/
                
            } catch (Exception ex) {
                ApplicationMain.log.trace(ex.getMessage());
            }
	}
	
	/*public void send(String msg) {
		String finalMsg = msg.concat("\0");
		ApplicationMain.log.trace("Sending: " + finalMsg);
		sendThread.sendMsg(finalMsg, new Integer(-1));
	}*/
	
	// Send a 'private' message to one client only
	public void send(int clientID, String msg) {
		String finalMsg = msg.concat("\0");
		ApplicationMain.log.trace("Sending to client #" + clientID + ": " + msg);
		clientServer.send(finalMsg, clientID);
	}
	
	// Send a message to ALL CONNECTED CLIENTS
	public void broadcast(String msg) {
		String finalMsg = msg.concat("\0");
		ApplicationMain.log.trace("Broadcasting: " + msg);
		clientServer.send(finalMsg);
	}
	
	/** Blocks until a msg is received */
	public Message receiveNow() {
		
		Message msg = null;
		
		while (msg == null) {
			if ((msg = clientServer.receiveMessage()) != null) {
				msg.removeNull();
				//msg = msg.substring(0, msg.length()-1);
				ApplicationMain.log.trace("Received: " + msg.msg());
			}
		}
		
		return msg;
	}
	
	/** Sends a msg back if it's available, but doesn't block */
	public Message receiveLater() {
		Message msg = null;
		if ((msg = clientServer.receiveMessage()) != null) {
			ApplicationMain.log.trace("Received: " + msg.msg());
			//msg = msg.substring(0, msg.length()-1);
			msg.removeNull();
		}
		return msg;
	}
	
	public void sendReceive() {
		/*ApplicationMain.log.trace("connected!");
    	//connected.notifyAll();
    	
    	
		if (isServer) {
			sendThread.sendMsg("u", new Integer(-1));
			sendThread.sendMsg("d", new Integer(-1));
			sendThread.sendMsg("r", new Integer(-1));
			sendThread.sendMsg("l", new Integer(-1));
		} else {
			while (!exitFlag) {
				if ((msg = clientServer.receiveMessage(recvThread)) != null) {
					ApplicationMain.log.trace("msg received: " + msg);
				}
				
				//sendThread.sendMsg("sending: blah blah", new Integer(-1));
			}
		}*/
	}
}
