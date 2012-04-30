package se.miun.mediasense.disseminationlayer.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GetMessage extends Message {
	
	public String uci;
	
	public GetMessage(String uci, String toIp, String fromIp) {
		super(fromIp,toIp,GET);
		
		this.uci = uci;
	}

	@Override
	public void serializeMessage(DataOutputStream oos) {
		try {
			super.serializeMessage(oos);
			oos.writeUTF(uci);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Message deserializeMessage(DataInputStream ois,String fromIp,String toIp) {
		try {
			String uci = ois.readUTF();
			return new GetMessage(uci,toIp,fromIp);
		}
		catch (IOException e) {
			return null;
		}
	}
}
