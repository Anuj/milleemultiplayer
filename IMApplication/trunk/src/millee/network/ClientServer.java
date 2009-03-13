package millee.network;
import java.io.IOException;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

import millee.game.ApplicationMain;
import millee.imapplication.BlueToothExp;


/** ClientServer class is the entity that represents a client or server.  It holds
 * all the information pertaining to that client or server.  It also holds all the methods 
 * representing the actions that can be done on the client or server.
 * @author Priyanka Reddy
 *
 */
public class ClientServer implements DiscoveryListener {
	
    UUID[] RFCOMM_UUID = {new UUID(0x0003), new UUID(0x0004), new UUID(0x0005)};
    private DiscoveryAgent m_DscrAgent = null;
    public Object connected = new Object();
        
    boolean isServer;
    int numClients;
    StreamConnection[] streamConnections;
    SenderThread senderThread = null;
    ReceiverThread[] recvThreads = null;
    
    
    private LocalDevice m_LclDevice = null;
    private StreamConnectionNotifier m_StrmNotf = null;
    public boolean m_bIsServer = false,  m_bServerFound = false,  m_bInitServer = false,  m_bInitClient = false;
    private static String m_strUrl;
    
    ApplicationMain _app = null;
    String serverName = null;
    
    public ClientServer (boolean isServer, int numClients, ApplicationMain _app) {
    	ApplicationMain.log.trace("initializing clientServer");
    	this.isServer = isServer;
    	this.numClients = numClients;
    	this._app = _app;
    }
    
    public void writeToDisplay(Form form) {
    	BlueToothExp.display.setCurrent(form);
    }
    
    public void writeToDisplay(TextField textField) {
    	BlueToothExp.display.setCurrentItem(textField);
    }
    
    public void printToScreen(String title, String msg) {
    	BlueToothExp.textBox = new TextBox(title, msg, BlueToothExp.MAX_TEXT_SIZE, TextField.ANY);
    	BlueToothExp.display.setCurrent(BlueToothExp.textBox);
    }
    
    public void appendToScreen(String msg) {
    	BlueToothExp.textBox.insert(msg, BlueToothExp.textBox.size());
    	BlueToothExp.display.setCurrent(BlueToothExp.textBox);
    }
    
    public StreamConnection[] getStreamConnections() {
    	return streamConnections;
    }
    
    public String getDeviceName() {
    	try {
			m_LclDevice = LocalDevice.getLocalDevice();
		} catch (BluetoothStateException e) {
			ApplicationMain.log.error("Couldn't access the local device");
			e.printStackTrace();
		}
    	return m_LclDevice.getFriendlyName();
    }
    
    // Starts the server and waits until it is connected to 
    // numConnections devices.
    public void InitServer(int numConnections) {
        
        try {
        	ApplicationMain.log.trace("Connecting...");
        	//ApplicationMain.theDisplay.setCurrent(new TextBox("Server: ", "Connecting...", 30, TextField.ANY));
        	//this.printToScreen("Application", "Connecting...");
        	streamConnections = new StreamConnection[numConnections];
        	
            updateServerScreenStatus("Waiting for " + numConnections + " player(s) to join");
        	
        	for (int i = 0; i < numConnections; i++) {
        		m_strUrl = "btspp://localhost:" + RFCOMM_UUID[i] + ";name=rfcommtest;authorize=false";
        		m_LclDevice = LocalDevice.getLocalDevice();
                m_LclDevice.setDiscoverable(DiscoveryAgent.GIAC);
                ApplicationMain.log.trace("waiting to connect to client #" + i);
                m_StrmNotf = (StreamConnectionNotifier) Connector.open(m_strUrl);
               
                
                ApplicationMain.log.trace("after updateGameScreen...");
                
                StreamConnection m_StrmConn = m_StrmNotf.acceptAndOpen();
                updateServerScreenStatus((i+1) + " player(s)have joined.  Waiting for " + (numConnections-1-i) + " player(s) to join.");
 
                ApplicationMain.log.trace("Just connected to client #" + i);
                streamConnections[i] = m_StrmConn;
                m_StrmNotf.close();
        	}
        	
        	// TODO: find out why this update message causes an IndexOutOfBounds error
        	updateServerScreenStatus("All players have joined.\n.  Push START to begin.");
        	
            ApplicationMain.log.trace("finished connecting");

        	
        } catch (BluetoothStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }
    
    void updateServerScreenStatus(String msg) {
    	ApplicationMain.log.info("Current Status: " + msg);
    	new Thread(new ScreenUpdater(ApplicationMain.START_A_GAME, "Current status: " + msg)).start();
    }
    
    /** Both client and server have only one senderThread.  Server has multiple
     * receiverThreads and the client has only 1 receiverThread
     */
    public void createIOThreads() {
    	ApplicationMain.log.trace("Creating IOThreads");
    	senderThread = new SenderThread(streamConnections);
    	senderThread.start();
    	if (isServer) {
    		ApplicationMain.log.trace("for the server");
    		recvThreads = new ReceiverThread[numClients];
    		ReceiverThread temp;
    		for (int i = 0; i<numClients; i++) {
    			temp = new ReceiverThread(streamConnections[i], senderThread, isServer, i);
    			temp.start();
    			recvThreads[i] = temp;
    			ApplicationMain.log.trace("created recvThread #" + i);
    		}
			//streamConns = clientServer.getStreamConnections();
			/*sendThread = new SenderThread(streamConns);
			recvThread = new ReceiverThread(streamConns[0], sendThread, isServer);
			recvThread.start();
	    	sendThread.start();*/

		} else {
			recvThreads = new ReceiverThread[1];
			ApplicationMain.log.trace("for the client");
			//streamConns = clientServer.getStreamConnections();
			//sendThread = new SenderThread(streamConns);
			ApplicationMain.log.trace(streamConnections);
			ApplicationMain.log.trace(streamConnections[0]);
			ApplicationMain.log.trace(senderThread);
			ReceiverThread recv = new ReceiverThread(streamConnections[0], senderThread, isServer, 0);
			//recvThread = new ReceiverThread(streamConns[0], sendThread, isServer);
			ApplicationMain.log.trace(recv);
			recv.start();
			recvThreads[0] = recv;
			ApplicationMain.log.trace("after starting the receiver Thread");
	    	//sendThread.start();
		}
    	ApplicationMain.log.trace("end of createIOThreads");
    }
    
    /*public void updateGameScreen(int gameScreen, String msg) {
    	updateGameScreen(ApplicationMain.START_A_GAME, "Waiting to connect to client #1");
    	Thread thread = new Thread(new Runnable () {public void run() { _app.updateGameScreen(gameScreen, msg); }});
        thread.start();
        
    	
    }*/
    
    public void closeAllConnections() {
    	for (int i = 0; i<streamConnections.length; i++) {
    		StreamConnection conn = streamConnections[i];
    		try {
    			System.out.println("closing connection: " + conn.toString());
				conn.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    
    // Starts the inquiry process for a client.
    public void InitClient() {
    	ApplicationMain.log.trace("initializing the client");
    	SearchAvailDevices();
    }
    
    public String getServerName() {
    	if (isServer) {
    		return this.getDeviceName();
    	}
    	if (serverName != null) {
    		return serverName.toUpperCase();
    	}
    	return serverName;
    }
    
    public void SearchAvailDevices() {
        try {
            m_LclDevice = LocalDevice.getLocalDevice();
            m_DscrAgent = m_LclDevice.getDiscoveryAgent();
            
            m_DscrAgent.startInquiry(DiscoveryAgent.GIAC, this);
        } catch (BluetoothStateException ex) {
            ex.printStackTrace();
        }
    }

    public void inquiryCompleted(int discType) {
    }
 
    public void serviceSearchCompleted(int transID, int respCode) {
        if (m_bServerFound) {
            
            try {

            	//this.printToScreen("Application", "Connecting...");
            	ApplicationMain.log.trace("Client Connecting...");
            	StreamConnection m_StrmConn = (StreamConnection) Connector.open(m_strUrl);
            	ApplicationMain.log.trace("m_StrmConn = " + m_StrmConn);
            	streamConnections = new StreamConnection[1];
            	
            	streamConnections[0] = m_StrmConn;
            	ApplicationMain.log.trace("Connected");
            	//this.printToScreen("Application", "Connected.");
            	
            	synchronized(connected) {
            		connected.notifyAll();
				}
            	
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void servicesDiscovered(int transID, ServiceRecord[] records) {
    	
    	ApplicationMain.log.trace("records.length: " + records.length);
    	
        for (int i = 0; i < records.length; i++) {
            m_strUrl = records[i].getConnectionURL(ServiceRecord.AUTHENTICATE_ENCRYPT, false);
            ApplicationMain.log.trace("m_strUrl: " + m_strUrl);
 
            if (m_strUrl.startsWith("btspp")) {
                m_bServerFound = true;
                m_bInitClient = true;
                // TODO: Ensure that this code is only entered once.  Make sure the serverName
                // doesn't get reset.
                // Turn off all device discovery and search discovery once the client is connected.
                try {
					serverName = records[i].getHostDevice().getFriendlyName(true);
				} catch (IOException e) {
					ApplicationMain.log.error("Couldn't access the remove Device");
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                break;
            }
        }
    }
 
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
    	try {
            System.out.println("btDevice name: " + btDevice.getFriendlyName(true));
            System.out.println("btDevice addr: " + btDevice.getBluetoothAddress());
            UUID uuidSet[] = new UUID[1];
        
	        for (int i = 0; i <numClients; i++) {
	                uuidSet[0] = RFCOMM_UUID[i];
	                int searchID = m_DscrAgent.searchServices(null, uuidSet, btDevice, this);
	                System.out.println("after searchServices #" + i + " with searchID = " + searchID);
	        }
	    } catch (Exception e) {
	            System.out.println("Exception in deviceDiscovered");
	        e.printStackTrace();
	    }
    }
    
    public Message receiveMessage() {
    	//String msg = null;
    	Message message = null;
    	synchronized (ReceiverThread.receivedMessages) {
    		
			if (ReceiverThread.receivedMessages.size() > 0) {
				message = (Message) ReceiverThread.receivedMessages.firstElement();
				ReceiverThread.receivedMessages.removeElementAt(0);
				return message;
			}
			
    	}
		return message;
    }
    
    public void send (String msg, int client) {
    	senderThread.sendMsg(msg, client, new Integer(0));
    }
    
    public void send (String msg) {
    	senderThread.sendMsg(msg, new Integer(0));
    }
    
    

	/*public void run() {
		// TODO Auto-generated method stub
		//if (this.m_bIsServer) {
			this.InitServer(1);
		//} else {
		//	this.InitClient();
		//}
	}*/
    
    
    private class ScreenUpdater implements Runnable {
    	
    	int gameScreen;
    	String msg = null;
    	
    	public ScreenUpdater(int gameScreen, String msg) {
    		this.gameScreen = gameScreen;
    		this.msg = msg;
    	}
		public void run() {
			_app.replaceMsgOnGameScreen(gameScreen, msg);
			ApplicationMain.log.trace("replaced the msg on the screen.  thread about to die.");
		}
    }
}

