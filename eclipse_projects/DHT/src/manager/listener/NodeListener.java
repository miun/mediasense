package manager.listener;

import manager.Communication;

public interface NodeListener {
	public void onNodeAdd(Communication com);
	public void onNodeRemove(Communication com);
}
