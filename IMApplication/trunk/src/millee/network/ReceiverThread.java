package millee.network;

import java.io.DataInputStream;
import java.util.Vector;

import javax.microedition.io.StreamConnection;

/** ReceiverThread creates an inputStream and receives data
 * 
 * @author Priyanka Reddy
 *
 */
public class ReceiverThread extends Thread {
	
	private DataInputStream input = null;
    private SenderThread senderThread = null;
    private int currIndex = 0;
    private StringBuffer str = new StringBuffer();
    public Vector rcvMsg;
    private boolean isServer = false;
    private int hashcode = -1;

	public ReceiverThread (StreamConnection m_StrmConn, SenderThread senderThread, boolean isServer) {
		
		try {
			this.isServer = isServer;
			input = m_StrmConn.openDataInputStream();
			hashcode = m_StrmConn.hashCode();
	        this.senderThread = senderThread;
	        rcvMsg = new Vector();
	        
	        
		} catch (Exception e) {
            e.printStackTrace();
		}
	}
	
	public String getMsg() {
		if (rcvMsg.size() > 0) {
			return (String) rcvMsg.firstElement();
		} else{
			return null;
		}
	}
	
	public void run() {
		
		
		try {
			str = new StringBuffer();
			while(true) {
				
				
				char charData = (char) input.read();
			        str.append(charData);
					currIndex++;
					//System.out.println("just received: " + charData);

					if (charData == '\0') {
						synchronized (rcvMsg) {
							rcvMsg.addElement(str);
							System.out.println("Just added to rcvMsg: " + rcvMsg.elementAt(0));
						}
						System.out.println("received: " + str);
		        		/*if (isServer) {
		        			senderThread.sendMsg(str.toString(), new Integer(hashcode));
		        		}*/
				    	str = new StringBuffer();
				    	currIndex = 0;
			        }
				}
		} catch (Exception e) {
            e.printStackTrace();
        }
	}
}
