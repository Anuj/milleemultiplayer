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

/** ClientServer class is the entity that represents a client or server.  It holds
 * all the information pertaining to that client or server.  It also holds all the methods 
 * representing the actions that can be done on the client or server.
 * @author Priyanka Reddy
 *
 */
public class ClientServer implements DiscoveryListener {
	
    UUID[] RFCOMM_UUID = {new UUID(0x0003)};
    private DiscoveryAgent m_DscrAgent = null;
    public Object connected = new Object();
        
    boolean isServer;
    int numClients;
    StreamConnection[] streamConnections;
    
    private LocalDevice m_LclDevice = null;
    private StreamConnectionNotifier m_StrmNotf = null;
    public boolean m_bIsServer = false,  m_bServerFound = false,  m_bInitServer = false,  m_bInitClient = false;
    private static String m_strUrl;
        
    public ClientServer (boolean isServer, int numClients) {
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
        	this.printToScreen("Application", "Connecting...");
        	streamConnections = new StreamConnection[numConnections];
        	
        	
        	for (int i = 0; i < numConnections; i++) {
        		m_strUrl = "btspp://localhost:" + RFCOMM_UUID[0] + ";name=rfcommtest;authorize=true";
        		m_LclDevice = LocalDevice.getLocalDevice();
                m_LclDevice.setDiscoverable(DiscoveryAgent.GIAC);
                m_StrmNotf = (StreamConnectionNotifier) Connector.open(m_strUrl);
                
                StreamConnection m_StrmConn = m_StrmNotf.acceptAndOpen();                
                streamConnections[i] = m_StrmConn;
        	}
        	
        } catch (BluetoothStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }
    
    // Starts the inquiry process for a client.
    public void InitClient() {
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
        if (m_bServerFound) {
            
            try {

            	this.printToScreen("Application", "Connecting...");
            	StreamConnection m_StrmConn = (StreamConnection) Connector.open(m_strUrl);
            	streamConnections = new StreamConnection[1];
            	
            	streamConnections[0] = m_StrmConn;
            	this.printToScreen("Application", "Connected.");
            	
            	synchronized(connected) {
            		connected.notifyAll();
				}
            	
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void servicesDiscovered(int transID, ServiceRecord[] records) {
    	
        for (int i = 0; i < records.length; i++) {
            m_strUrl = records[i].getConnectionURL(ServiceRecord.AUTHENTICATE_ENCRYPT, false);
 
            if (m_strUrl.startsWith("btspp")) {
                m_bServerFound = true;
                m_bInitClient = true;
                break;
            }
 
        }
    }
 
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
        try {
            UUID uuidSet[] = new UUID[1];
            uuidSet[0] = RFCOMM_UUID[0];
            int searchID = m_DscrAgent.searchServices(null, uuidSet, btDevice, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String receiveMessage(ReceiverThread recvThread) {
    	String msg = null;
	
		if (recvThread.rcvMsg.size() > 0) {
			msg = recvThread.rcvMsg.firstElement().toString();
			recvThread.rcvMsg.removeElementAt(0);
			return msg;
		}
		return msg;
    }
}

