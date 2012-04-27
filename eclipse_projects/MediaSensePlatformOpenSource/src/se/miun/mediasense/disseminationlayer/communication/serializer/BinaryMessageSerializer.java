package se.miun.mediasense.disseminationlayer.communication.serializer;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.communication.MessageSerializer;

public class BinaryMessageSerializer implements MessageSerializer {

	@Override
	public byte[] serializeMessage(Message message) {
		return message.toByteArray();
	}

	@Override
	public Message deserializeMessage(byte[] data) {
		return Message.fromByteArray(data);
	}
}
