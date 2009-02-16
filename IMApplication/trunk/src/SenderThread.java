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
    private Vector msgHashcodeQueue;	/* holds the hashcode of the sender of the msg */
    
    private DataOutputStream[] outputStreams = null;
    private int[] outputStreamsHashcodes = null;	/* holds the hashcodes of each of the devices */
    
    /* Hashcodes are used to compare who originally sent the msg to who is receiving it
     * and prevents sending a msg back to the sender.  Currently, it's using a very
     * bad way to store it...this needs to be fixed soon!
     */

    public SenderThread (StreamConnection[] streamConns) {
		
		msgQueue = new Vector();
		msgHashcodeQueue = new Vector();
		
		try {
			System.out.println("before outputStreams");
			outputStreams = new DataOutputStream[streamConns.length];
			System.out.println("after outputStreams");
			outputStreamsHashcodes = new int[streamConns.length];
			System.out.println("after outputStreamsHashcodes");
			for (int i = 0; i<streamConns.length; i++) {
				System.out.println("start of for loop: " + i);
				System.out.println("streamConns: " + streamConns);
				System.out.println("streamConns[i]: " + streamConns[i]);
				outputStreams[i] = streamConns[i].openDataOutputStream();
				System.out.println("middle of for loop");
				outputStreamsHashcodes[i] = streamConns[i].hashCode();
				System.out.println("end of for loop");
			}
			System.out.println("after for loop");
	        
		} catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public void sendMsg (String msg, Integer senderHashcode) {
		synchronized (msgQueue) {
			msgQueue.addElement(msg);
			msgHashcodeQueue.addElement(senderHashcode);
			msgQueue.notify();
		}
	}
	
	public void run() {
		int i = 0;
		try {
			
			while (true) {
				
				synchronized (msgQueue) {
					if (msgQueue.size() > 0 && i < msgQueue.size()) {
						
						byte[] data = ((String) msgQueue.elementAt(i)).getBytes();
						//data[data.length] = 0;	// need to end a string terminator
						int hashcode = ((Integer) msgHashcodeQueue.elementAt(i)).intValue();
						//if (outputStreams != null) {
						for (int j = 0; j<outputStreams.length; j++) {
							if (hashcode != outputStreamsHashcodes[j]) {
								System.out.println("hashcode: " + hashcode);
								System.out.println("outputStreamsHashcodes: " + outputStreamsHashcodes[j]);
								System.out.println("sending: " + data);
								outputStreams[j].write(data);
								outputStreams[j].flush();
							}
						}
						//} else {
						/*	outputStream.write(data);
							outputStream.flush();
						}*/
				    	
						msgQueue.removeElementAt(i);
						msgHashcodeQueue.removeElementAt(i);
						
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