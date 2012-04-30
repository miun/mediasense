package se.miun.mediasense.disseminationlayer.communication.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.communication.MessageSerializer;

public class BinaryMessageSerializer implements MessageSerializer {
	
	@Override
	public byte[] serializeMessage(Message message) {
		try {
			ByteArrayOutputStream bos;
			DataOutputStream oos;
			
			//Serialize it
			bos = new ByteArrayOutputStream();
			oos = new DataOutputStream(bos);
			message.serializeMessage(oos);
			oos.flush();
			oos.close();
			bos.close();
			
			//Return byte array
			return bos.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} 
	}

	@Override
	public Message deserializeMessage(byte[] data,String fromIp,String toIp) {
		ByteArrayInputStream bis;
		DataInputStream ois;
		Message obj;
		
		try {
			//Try to read the type
		    bis = new ByteArrayInputStream(data);
		    ois = new DataInputStream (bis);
		    
		    //Start deserialization
		    obj = Message.deserializeMessage(ois, fromIp, toIp);
		    
		    ois.close();
		    bis.close();
		    return obj;
		}
		catch (IOException ex) {
			return null;
		}
	}
}
