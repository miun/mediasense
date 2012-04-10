package manager.listener;

import java.util.Date;

import manager.dht.NodeID;

public interface KeepAliveListener {
	public void OnKeepAliveEvent(Date date,NodeID key,String networkAddress);
}
