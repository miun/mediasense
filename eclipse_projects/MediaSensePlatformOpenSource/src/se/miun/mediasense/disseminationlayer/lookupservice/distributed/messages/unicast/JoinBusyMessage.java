package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import se.miun.mediasense.disseminationlayer.communication.Message;

public class JoinBusyMessage extends Message {

	public JoinBusyMessage(String from, String to) {
		super(from, to, Message.JOIN_BUSY);
	}

	public String toString() {
		// Return message info
		return super.toString("MSG-JOIN_BUSY");
	}

	// Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount();
	}

	@Override
	public void serializeMessage(DataOutputStream oos) {
		super.serializeMessage(oos);
	}

	public static Message deserializeMessage(DataInputStream ois,
			String fromIp, String toIp) {
		return new JoinBusyMessage(fromIp, toIp);
	}
}
