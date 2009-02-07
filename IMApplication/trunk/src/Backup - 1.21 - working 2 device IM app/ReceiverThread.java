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

	public ReceiverThread (StreamConnection m_StrmConn, SenderThread senderThread, boolean isServer) {
		
		try {
			this.isServer = isServer;
			input = m_StrmConn.openDataInputStream();
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

			        if (currIndex >= 10) {			        	
		        		rcvMsg.addElement(str);
		        		if (isServer) {
		        			senderThread.sendMsg(str.toString());
		        		}
				    	str = new StringBuffer();
				    	currIndex = 0;
			        }
				}
		} catch (Exception e) {
            e.printStackTrace();
        }
	}
}
