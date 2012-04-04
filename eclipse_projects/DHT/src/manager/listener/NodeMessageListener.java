package manager.listener;

import java.util.Date;

import manager.Message;

public interface NodeMessageListener {
	
	public void OnNodeMessage(Date timeStamp,Message msg);

}
