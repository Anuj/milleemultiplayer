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
    private DataOutputStream outputStream = null;
    private DataOutputStream[] outputStreams = null;

	public SenderThread (StreamConnection m_StrmConn) {
		
		msgQueue = new Vector();
		
		try {
			outputStream = m_StrmConn.openDataOutputStream();
	        
		} catch (Exception e) {
            e.printStackTrace();
        }

	}
	
public SenderThread (StreamConnection[] streamConns) {
		
		msgQueue = new Vector();
		
		try {
			outputStreams = new DataOutputStream[streamConns.length];
			for (int i = 0; i<streamConns.length; i++) {
				outputStreams[i] = streamConns[i].openDataOutputStream();
			}
	        
		} catch (Exception e) {
            e.printStackTrace();
        }

	}
	
	public void sendMsg (String msg) {
		synchronized (msgQueue) {
			msgQueue.addElement(msg);
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

						if (outputStreams != null) {
							for (int j = 0; j<outputStreams.length; j++) {
								outputStreams[j].write(data);
								outputStreams[j].flush();
							}
						} else {
							outputStream.write(data);
							outputStream.flush();
						}
				    	
						msgQueue.removeElementAt(i);
						
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