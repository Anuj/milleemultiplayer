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
			try {
				throw new Exception ("Number of clients is non-positive");
			} catch (Exception e) {
				System.out.println("Number of clients is non-positive");
				e.printStackTrace();
			}
		this.isServer = isServer;
		this.numClients = numClients;
		sendBuffer = new Vector();
		receiverBuffer = new Vector();

    	clientServer = new ClientServer(isServer, numClients, _app);
	}
	
	public void run() {
        //while (m_bRunThread) {
            try {
            	// Just by creating a new Application object, you run the application
            	// either as a client or server depending on the m_bIsServer variable.
                /*if (app == null) {
                	app = new IMApplication(m_bIsServer, 1);
                	//exitMIDlet();
                }*/
            	
            	if (isServer) {
            		
            		// Blocks until it connects to numClients
        			clientServer.InitServer(numClients);
        		} else {
        			int numDevicesDiscovered = 0;
        			int temp = 0;
        			clientServer.InitClient();
        			try {
        				
        				/*while (true) {
        					if ((temp = clientServer.numDevicesDiscovered) > numDevicesDiscovered) {
        						System.out.println("discovered a new device");
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
            	
            	System.out.println("_app: " + _app);
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
                System.out.println(ex.getMessage());
            }
	}
	
	/*public void send(String msg) {
		String finalMsg = msg.concat("\0");
		System.out.println("Sending: " + finalMsg);
		sendThread.sendMsg(finalMsg, new Integer(-1));
	}*/
	
	// Send a 'private' message to one client only
	public void send(int clientID, String msg) {
		String finalMsg = msg.concat("\0");
		System.out.println("Sending to client #" + clientID + ": " + msg);
		clientServer.send(finalMsg, clientID);
	}
	
	// Send a message to ALL CONNECTED CLIENTS
	public void broadcast(String msg) {
		String finalMsg = msg.concat("\0");
		System.out.println("Broadcasting: " + msg);
		clientServer.send(finalMsg);
	}
	
	/** Blocks until a msg is received */
	public Message receiveNow() {
		
		Message msg = null;
		
		while (msg == null) {
			if ((msg = clientServer.receiveMessage()) != null) {
				msg.removeNull();
				//msg = msg.substring(0, msg.length()-1);
				System.out.println("Received: " + msg.msg());
			}
		}
		
		return msg;
	}
	
	/** Sends a msg back if it's available, but doesn't block */
	public Message receiveLater() {
		Message msg = null;
		if ((msg = clientServer.receiveMessage()) != null) {
			System.out.println("Received: " + msg.msg());
			//msg = msg.substring(0, msg.length()-1);
			msg.removeNull();
		}
		return msg;
	}
	
	public void sendReceive() {
		/*System.out.println("connected!");
    	//connected.notifyAll();
    	
    	
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
		}*/
	}
}
