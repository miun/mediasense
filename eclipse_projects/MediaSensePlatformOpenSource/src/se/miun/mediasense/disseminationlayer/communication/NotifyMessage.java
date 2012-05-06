package se.miun.mediasense.disseminationlayer.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class NotifyMessage extends Message {
	
	public String uci;
	public String value;
	
	public NotifyMessage(String uci, String value, String toIp, String fromIp) {
		super(fromIp,toIp,Message.NOTIFY);
		
		this.value = value;
		this.uci = uci;
	}
	
	@Override
	public void serializeMessage(DataOutputStream oos) {
		try {
			super.serializeMessage(oos);
			oos.writeUTF(uci);
			oos.writeUTF(value);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Message deserializeMessage(DataInputStream ois,String fromIp,String toIp) {
		try {
			String uci = ois.readUTF();
			String value = ois.readUTF();
			return new NotifyMessage(uci,value,toIp,fromIp);
		}
		catch (IOException e) {
			return null;
		}
	}
}
