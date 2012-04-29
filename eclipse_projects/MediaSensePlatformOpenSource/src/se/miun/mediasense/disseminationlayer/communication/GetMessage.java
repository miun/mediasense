package se.miun.mediasense.disseminationlayer.communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class GetMessage extends Message {
	
	public String uci;
	
	public GetMessage(String uci, String toIp, String fromIp) {
		super(fromIp,toIp,GET);
		
		this.uci = uci;
	}

	@Override
	public void serializeMessage(ObjectOutputStream oos) {
		try {
			super.serializeMessage(oos);
			oos.writeUTF(uci);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Message deserializeMessage(ObjectInputStream ois,String fromIp,String toIp) {
		try {
			String uci = ois.readUTF();
			return new GetMessage(uci,toIp,fromIp);
		}
		catch (IOException e) {
			return null;
		}
	}
}
