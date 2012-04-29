package se.miun.mediasense.disseminationlayer.communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SetMessage extends Message {
	
	public String uci;
	public String value;

	
	public SetMessage(String uci, String value, String toIp, String fromIp) {
		super(fromIp,toIp,SET);
		
		this.uci = uci;
		this.value = value;
	}
	
	@Override
	public void serializeMessage(ObjectOutputStream oos) {
		try {
			super.serializeMessage(oos);
			oos.writeUTF(uci);
			oos.writeUTF(value);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Message deserializeMessage(ObjectInputStream ois,String fromIp,String toIp) {
		try {
			String uci = ois.readUTF();
			String value = ois.readUTF();
			return new SetMessage(uci,value,toIp,fromIp);
		}
		catch (IOException e) {
			return null;
		}
	}
}
