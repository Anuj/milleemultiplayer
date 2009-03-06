package millee.imapplication;

import javax.microedition.io.StreamConnection;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import millee.network.ClientServer;
import millee.network.ReceiverThread;
import millee.network.SenderThread;

/** IMApplication is a sample application that allows for instant messaging over 
 * bluetooth between many phones.  Currently, this application supports IM between 
 * 2 phones, but it can be easily extended to more phones.
 * @author Priyanka Reddy
 *
 */
public class IMApplication extends Application {
	
	int numClients = -1;
	ClientServer clientServer = null;
    static final int MAX_TEXT_SIZE = 10000;
	
    private Command cmExit;
    private Command cmSend;

	private boolean sendingFlag = false;
	private boolean exitFlag = false;
	private Object waitingForUserInput = new Object();
	
	public IMApplication(boolean isServer, int numClients) {
		
		// creates the object that represents this device, whether it acts
		// like a client or server.  If isServer is true, that numClients 
		// will be >0, otherwise, it's ignored.
		//clientServer = new ClientServer(isServer, numClients);
		
		if (isServer) {
			this.numClients = numClients;
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
		// Once the client is connected to the server, runApplication will run
		// the actual application.
		clientServer.printToScreen("Application", "Connected.");
		runApplication(isServer);
	}
	
	public int runApplication(boolean isServer) {
		try {
    	
			clientServer.printToScreen("IMApplication", "Starting IMApplication...");
			
    		SenderThread sendThread = null;
    		ReceiverThread recvThread = null;
    		
    		// StreamConnections represents the number of devices this device is 
    		// directly connected to.  If it's a client, it should only hold
    		// one streamConnection.  If it's a server, it will hold as many
    		// streamConnections as it has clients.
    		StreamConnection[] streamConns = clientServer.getStreamConnections();
    		
    		// A SenderThread can send to multiple devices (which a server requires),
    		// but a ReceiverThread can only receive from one device.  So, for a 
    		// server, it needs to create as many ReceiverThreads as it has clients.
    		if (isServer) {
    			sendThread = new SenderThread(streamConns);
				//recvThread = new ReceiverThread(streamConns[0], sendThread, isServer);
				recvThread.start();
		    	sendThread.start();
    		}
    		else {
    			sendThread = new SenderThread(streamConns);
    			//recvThread = new ReceiverThread(streamConns[0], sendThread, isServer);
    			recvThread.start();
    	    	sendThread.start();
    		}
    		
    		// Started all the sending and receiving threads by now
    		
	    	String msg = null;
	    	
    		Form form = new Form("IM Application");
    		cmSend = new Command("Send", Command.OK, 2);
	        cmExit = new Command("Exit", Command.EXIT, 1);
	        form.addCommand(cmSend);
	        form.addCommand(cmExit);
	        
	        TextField outputField = new TextField("Text to send: ", "", MAX_TEXT_SIZE, TextField.ANY);
	        TextField inputField = new TextField("Text received: ", "", MAX_TEXT_SIZE, TextField.ANY);

        	form.append(inputField);
        	form.append(outputField);
        	clientServer.writeToDisplay(form);
        	
        	form.setCommandListener(this);
        	
    		while (!exitFlag) {
    			clientServer.writeToDisplay(outputField);
    			
    			/*if ((msg = clientServer.receiveMessage(recvThread)) != null) {
    				inputField.insert("\nReceived: " + msg, inputField.size());
    			}*/
    			clientServer.writeToDisplay(outputField);
    			
    			if (sendingFlag) {
    				sendingFlag = false;
    				String receivedText = outputField.getString();
    				inputField.insert("\nSent: " + receivedText, inputField.size());
    				sendThread.sendMsg(receivedText, new Integer(-1));
    				outputField.setString("");
    			}
    		}
    		return 0;
    	} catch (Exception e) {
    		return 1;
    	}
	}
	
    public void commandAction(Command c, Displayable s)
    {
      if (c == cmExit) {
    	  exitFlag = true;
      }
      else if (c == cmSend) {
    	  synchronized(waitingForUserInput) {
          	waitingForUserInput.notify();
          }
    	  sendingFlag = true;
      }
    } 
}
