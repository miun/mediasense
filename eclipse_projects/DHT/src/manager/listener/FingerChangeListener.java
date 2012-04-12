package manager.listener;

import manager.dht.FingerEntry;

public interface FingerChangeListener {

	public static final int FINGER_CHANGE_ADD = 1;
	public static final int FINGER_CHANGE_REMOVE = 2;
	public static final int FINGER_CHANGE_ADD_BETTER = 3;
	public static final int FINGER_CHANGE_REMOVE_WORSE = 4;

<<<<<<< HEAD
	public void OnFingerChange(int changeType,NodeID node,NodeID finger);	
=======
	public void OnFingerChange(int changeType,FingerEntry node,FingerEntry finger);	
>>>>>>> b71bfc81358352d92881047858eb2da5dfb4b72e
}
