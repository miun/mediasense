package manager.listener;

import java.util.Date;

import manager.Communication;

public interface NodeListener {
	public void onNodeAdd(Date timeStamp,Communication com);
	public void onNodeRemove(Date timeStamp,Communication com);
	public void onKillNode(Date timeStamp,Communication com);
}
