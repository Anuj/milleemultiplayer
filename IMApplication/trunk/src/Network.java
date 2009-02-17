import java.util.Vector;

import javax.microedition.io.StreamConnection;


public class Network implements Runnable {
	
	ClientServer clientServer = null;
	private boolean m_bRunThread = true;
	private boolean isServer;
	private int numClients;
	Vector sendBuffer, receiverBuffer;
	SenderThread sendThread = null;
	ReceiverThread recvThread = null;
	StreamConnection[] streamConns;
	
	public Object connected = new Object();
	
	public Network () {
		
	}
	
	public void initializeNetwork(boolean isServer, int numClients) {
		this.isServer = isServer;
		this.numClients = numClients;
		System.out.println("initialized network class");
		sendBuffer = new Vector();
		receiverBuffer = new Vector();

    	clientServer = new ClientServer(isServer, numClients);
	}
	
	public void run() {
        //while (m_bRunThread) {
            try {
            	System.out.println("inside Network.  trying to connect client");
            	// Just by creating a new Application object, you run the application
            	// either as a client or server depending on the m_bIsServer variable.
                /*if (app == null) {
                	app = new IMApplication(m_bIsServer, 1);
                	//exitMIDlet();
                }*/
            	
            	if (isServer) {
        			//this.numClients = numClients;
        			clientServer.InitServer(numClients);
        		} else {
        			System.out.println("about to initClient()");
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
            	
            	System.out.println("finished connecting...");
            	
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
        		}
                
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
	}
	
	public void send(String msg) {
		String finalMsg = msg.concat("\0");
		sendThread.sendMsg(finalMsg, new Integer(-1));
	}
	
	/** Blocks until a msg is received */
	public String receiveNow() {
		String msg = null;
		
		while (msg == null) {
			if ((msg = clientServer.receiveMessage(recvThread)) != null) {
				msg = msg.substring(0, msg.length()-1);
				System.out.println("msg received: " + msg);
			}
		}
		
		return msg;
	}
	
	/** Sends a msg back if it's available, but doesn't block */
	public String receiveLater() {
		String msg = null;
		
		if ((msg = clientServer.receiveMessage(recvThread)) != null) {
			System.out.println("msg received: " + msg);
			msg = msg.substring(0, msg.length()-1);
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
