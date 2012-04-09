package manager.listener;

import manager.dht.NodeID;

public interface FingerChangeListener {

	public static final int FINGER_CHANGE_ADD = 1;
	public static final int FINGER_CHANGE_REMOVE = 2;
	
	public void OnFingerChange(int changeType,NodeID node,NodeID finger);
	
}