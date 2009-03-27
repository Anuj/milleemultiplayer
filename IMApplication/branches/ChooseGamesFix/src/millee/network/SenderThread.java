package millee.network;
import java.io.DataOutputStream;
import java.util.Vector;

import javax.microedition.io.StreamConnection;

/** SenderThread creates an outputStream and sends data
 * 
 * @author Priyanka Reddy
 *
 */
public class SenderThread extends Thread {

    private Vector msgQueue;
    //private Vector msgHashcodeQueue;	/* holds the hashcode of the sender of the msg */
    
    private DataOutputStream[] outputStreams = null;
    private int[] outputStreamsHashcodes = null;	/* holds the hashcodes of each of the devices */
    
    /* Hashcodes are used to compare who originally sent the msg to who is receiving it
     * and prevents sending a msg back to the sender.  Currently, it's using a very
     * bad way to store it...this needs to be fixed soon!
     */

    public SenderThread (StreamConnection[] streamConns) {
		
		msgQueue = new Vector();
		//msgHashcodeQueue = new Vector();
		
		try {
			
			outputStreams = new DataOutputStream[streamConns.length];
			outputStreamsHashcodes = new int[streamConns.length];
			for (int i = 0; i<streamConns.length; i++) {
				outputStreams[i] = streamConns[i].openDataOutputStream();
				outputStreamsHashcodes[i] = streamConns[i].hashCode();
			}
	        
		} catch (Exception e) {
            e.printStackTrace();
        }
	}
	
    /** Broadcasts msg to all clients */
	public void sendMsg (String msg, Integer senderHashcode) {
		synchronized (msgQueue) {
			for (int j = 0; j<outputStreams.length; j++) {
				msgQueue.addElement(new Message(msg, j, senderHashcode));
				msgQueue.notify();
			}
			
			//msgQueue.addElement(msg);
			//msgHashcodeQueue.addElement(senderHashcode);
			//msgQueue.notify();
		}
	}
	
	/** Sends msg only to the client given */
	public void sendMsg(String msg, int client, Integer senderHashcode) {
		synchronized (msgQueue) {
			
			msgQueue.addElement(new Message(msg, client, senderHashcode));
			//msgHashcodeQueue.addElement(senderHashcode);
			msgQueue.notify();
		}
	}
	
	public void run() {
		int i = 0;
		try {
			
			while (true) {
				
				synchronized (msgQueue) {
					if (msgQueue.size() > 0 && i < msgQueue.size()) {
						
						Message msg = ((Message) msgQueue.elementAt(i));
						
						byte[] data = msg.msg().getBytes();
						
						
						//data[data.length] = 0;	// need to end a string terminator
						//int hashcode = ((Integer) msgHashcodeQueue.elementAt(i)).intValue();
						//int hashcode = msg.hashCode();
						
						
						// TODO: deal with sending a client's data back to the client
						
						outputStreams[msg.recipient()].write(data);
						outputStreams[msg.recipient()].flush();
						
						
						/*for (int j = 0; j<outputStreams.length; j++) {
							if (hashcode != outputStreamsHashcodes[j]) {
								ApplicationMain.log.trace("hashcode: " + hashcode);
								ApplicationMain.log.trace("outputStreamsHashcodes: " + outputStreamsHashcodes[j]);
								ApplicationMain.log.trace("sending: " + data);
								outputStreams[j].write(data);
								outputStreams[j].flush();
							}
						}*/
						//} else {
						/*	outputStream.write(data);
							outputStream.flush();
						}*/
				    	
						msgQueue.removeElementAt(i);
						//msgHashcodeQueue.removeElementAt(i);
						
						i++;
						if (i >= msgQueue.size())
							i = 0;
					}
					
					try {
				    	if (msgQueue.size() == 0) {
				    		msgQueue.wait();
				    	}
					} catch (InterruptedException e) {
						
					}
				}
			}

		} catch (Exception e) {
        }
	}
}