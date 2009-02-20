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
	
	String msg() {
		return _msg;
	}
	
	int recipient() {
		return _recipient;
	}
	
	int senderHashcode() {
		return _senderHashcode;
	}
}
