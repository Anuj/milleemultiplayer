package millee.network;
import java.io.IOException;
import java.util.Vector;

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

import millee.imapplication.BlueToothExp;


/** ClientServer class is the entity that represents a client or server.  It holds
 * all the information pertaining to that client or server.  It also holds all the methods 
 * representing the actions that can be done on the client or server.
 * @author Priyanka Reddy
 *
 */
public class ClientServer implements DiscoveryListener {
	
    UUID[] RFCOMM_UUID = {new UUID(0xF542), new UUID(0xF543)};
    private DiscoveryAgent m_DscrAgent = null;
    public Object connected = new Object();
        
    boolean isServer;
    int numClients;
    int numDevicesDiscovered = 0;
    
    public Vector devicesDiscoveredNames = null;
    public Vector devicesDiscoveredUrls = null;
    StreamConnection[] streamConnections;
    SenderThread senderThread = null;
    ReceiverThread[] recvThreads = null;
    
    private LocalDevice m_LclDevice = null;
    private StreamConnectionNotifier m_StrmNotf = null;
    public boolean m_bIsServer = false,  m_bServerFound = false,  m_bInitServer = false,  m_bInitClient = false;
    private static String m_strUrl;
        
    public ClientServer (boolean isServer, int numClients) {
    	System.out.println("initializing clientServer");
    	this.isServer = isServer;
    	this.numClients = numClients;
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
    
    // Starts the server and waits until it is connected to 
    // numConnections devices.
    public void InitServer(int numConnections) {
        
        try {
        	System.out.println("Connecting...");
        	//ApplicationMain.theDisplay.setCurrent(new TextBox("Server: ", "Connecting...", 30, TextField.ANY));
        	//this.printToScreen("Application", "Connecting...");
        	streamConnections = new StreamConnection[numConnections];
        	
        	
        	for (int i = 0; i < numConnections; i++) {
        		m_strUrl = "btspp://localhost:" + RFCOMM_UUID[0] + ";name=colourcolour;authorize=false";
        		m_LclDevice = LocalDevice.getLocalDevice();
        		System.out.println("my name is: " + m_LclDevice.getFriendlyName());
                m_LclDevice.setDiscoverable(DiscoveryAgent.GIAC);
                System.out.println("waiting to connect to client #" + i);
                m_StrmNotf = (StreamConnectionNotifier) Connector.open(m_strUrl);
                StreamConnection m_StrmConn = m_StrmNotf.acceptAndOpen();                
                System.out.println("Just connected to client #" + i);
                streamConnections[i] = m_StrmConn;
                m_StrmNotf.close();
        	}
        	
            System.out.println("finished connecting");

        	
        } catch (BluetoothStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }
    
    /** Both client and server have only one senderThread.  Server has multiple
     * receiverThreads and the client has only 1 receiverThread
     */
    public void createIOThreads() {
    	System.out.println("Creating IOThreads");
    	senderThread = new SenderThread(streamConnections);
    	senderThread.start();
    	if (isServer) {
    		System.out.println("for the server");
    		recvThreads = new ReceiverThread[numClients];
    		ReceiverThread temp;
    		for (int i = 0; i<numClients; i++) {
    			temp = new ReceiverThread(streamConnections[i], senderThread, isServer, i);
    			temp.start();
    			recvThreads[i] = temp;
    			System.out.println("created recvThread #" + i);
    		}
			//streamConns = clientServer.getStreamConnections();
			/*sendThread = new SenderThread(streamConns);
			recvThread = new ReceiverThread(streamConns[0], sendThread, isServer);
			recvThread.start();
	    	sendThread.start();*/

		} else {
			recvThreads = new ReceiverThread[1];
			System.out.println("for the client");
			//streamConns = clientServer.getStreamConnections();
			//sendThread = new SenderThread(streamConns);
			System.out.println(streamConnections);
			System.out.println(streamConnections[0]);
			System.out.println(senderThread);
			ReceiverThread recv = new ReceiverThread(streamConnections[0], senderThread, isServer, 0);
			//recvThread = new ReceiverThread(streamConns[0], sendThread, isServer);
			System.out.println(recv);
			recv.start();
			recvThreads[0] = recv;
			System.out.println("after starting the receiver Thread");
	    	//sendThread.start();
		}
    	System.out.println("end of createIOThreads");
    }
    
    public void joinGame(int gameChoice) {
            
            try {
            	
            	System.out.println("shoudl be connecting here...");
            	//this.printToScreen("Application", "Connecting...");
            	System.out.println("Client Connecting...");
            	StreamConnection m_StrmConn = (StreamConnection) Connector.open((String) devicesDiscoveredUrls.elementAt(gameChoice));
            	System.out.println("m_StrmConn = " + m_StrmConn);
            	streamConnections = new StreamConnection[1];
            	
            	streamConnections[0] = m_StrmConn;
            	System.out.println("Connected");
            	//this.printToScreen("Application", "Connected.");
            	
            	synchronized(connected) {
            		connected.notifyAll();
				}
            	
            } catch (Exception ex) {
                ex.printStackTrace();
            }
    }
    
    // Starts the inquiry process for a client.
    public void InitClient() {
    	devicesDiscoveredNames = new Vector();
    	devicesDiscoveredUrls = new Vector();
    	System.out.println("initializing the client");
    	SearchAvailDevices();
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
    	
    	devicesDiscoveredNames.addElement("finished");
		numDevicesDiscovered++;

    }
    
    public void servicesDiscovered(int transID, ServiceRecord[] records) {
    	
    	System.out.println("records.length: " + records.length);
    	
        for (int i = 0; i < records.length; i++) {
            m_strUrl = records[i].getConnectionURL(ServiceRecord.AUTHENTICATE_ENCRYPT, false);
            System.out.println("m_strUrl: " + m_strUrl);
            
            if (m_strUrl.startsWith("btspp")) {
            	System.out.println("found colour colour game!");
            	try {
            		devicesDiscoveredNames.addElement(records[i].getHostDevice().getFriendlyName(true));
            		devicesDiscoveredUrls.addElement(m_strUrl);
            		numDevicesDiscovered++;
            		break;
            	} catch (IOException e) {
            		
            	}
            	
            	
                /*m_bServerFound = true;
                m_bInitClient = true;
                break;*/
            }
        }
    }
 
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
        try {
        
        	
        	System.out.println("device discovered");
        	System.out.println("btDevice name: " + btDevice.getFriendlyName(true));
        	System.out.println("btDevice addr: " + btDevice.getBluetoothAddress());
            UUID uuidSet[] = new UUID[1];
            uuidSet[0] = RFCOMM_UUID[0];
            int searchID = m_DscrAgent.searchServices(null, uuidSet, btDevice, this);
            System.out.println("after searchServices with searchID = " + searchID);
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
}

