package millee.network;

public class Message {
	private String _msg;
	private int _recipient;
	private int _senderHashcode;
	
	Message(String msg, int recipient, Integer senderHashcode) {
		_msg = msg;
		_recipient = recipient;
		_senderHashcode = senderHashcode.intValue();
	}
	
	public String msg() {
		return _msg;
	}
	
	public int recipient() {
		return _recipient;
	}
	
	public int senderHashcode() {
		return _senderHashcode;
	}
	
	public void removeNull() {
		_msg = _msg.substring(0, _msg.length()-1);
	}
}
