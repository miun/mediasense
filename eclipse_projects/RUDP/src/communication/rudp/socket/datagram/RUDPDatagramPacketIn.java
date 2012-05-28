package communication.rudp.socket.datagram;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import communication.rudp.socket.exceptions.InvalidRUDPPacketException;

public class RUDPDatagramPacketIn extends RUDPDatagramPacket {

	//This constructor deserializes a packet, and throws an exception
	//if the data is not a valid packet
	public RUDPDatagramPacketIn(byte[] packet) throws InvalidRUDPPacketException {
		ByteArrayInputStream bis;
		DataInputStream dis;
		int flag;
		int ack_count;
		
		bis = new ByteArrayInputStream(packet);
		dis = new DataInputStream(bis);
		
		//Read flag
		try {
			//TODO remove debug
			id = dis.readInt();
			
			//Read and analyze flags
			flag = dis.readByte();
			flag_first = (flag & FLAG_FIRST) != 0 ? true : false; 
			flag_reset = (flag & FLAG_RESET) != 0 ? true : false; 
			flag_ack = (flag & FLAG_ACK) != 0 ? true : false; 
			flag_data = (flag & FLAG_DATA) != 0 ? true : false; 
			flag_fragment = (flag & FLAG_FRAGMENT) != 0 ? true : false; 
			flag_resend = (flag & FLAG_RESEND) != 0 ? true : false;
			flag_persist = (flag & FLAG_PERSIST) != 0 ? true : false;
			
			//Read static fields
			packet_seq = dis.readInt();
			ack_window_start = dis.readInt();				
			window_size = dis.readInt();
			frag_nr = dis.readShort();
			frag_count = dis.readShort();
			
			//Read variable length ACK data, if available
			if(flag_ack) {
				//Count means the number of ranges!
				ack_count = dis.readShort();
				
				//Check that the ACK field is not too long
				if(ack_count > RESERVED_ACK_COUNT) throw new InvalidRUDPPacketException();
				
				//Get size of ACK field
				ack_seq_data = new ArrayList<Short>();
				
				//Take the count times 2, because every range has 2 elements 
				for(int i = 0; i < ack_count * 2; i++) {
					ack_seq_data.add(dis.readShort());				
				}
			}

			//Read data if available
			if(flag_data) {
				int dataSize = bis.available();
				if(dataSize < 0) throw new InvalidRUDPPacketException();
				data = new byte[dataSize];
				dis.readFully(data,0,dataSize);
			}
			
			dis.close();
			bis.close();
		}
		catch (IOException e) {
			//Transform to invalid packet exception
			throw new InvalidRUDPPacketException();
		}
	}
}
