package manager.ui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Date;

import javax.swing.JFrame;

import manager.Communication;
import manager.Manager;
import manager.Network;
import manager.dht.NodeID;
import manager.listener.FingerChangeListener;
import manager.listener.KeepAliveListener;
import manager.listener.NodeListener;

@SuppressWarnings("serial")
public class CircleGUI extends JFrame implements NodeListener, WindowListener, FingerChangeListener, KeepAliveListener {

	private Manager manager;
	
	private CirclePanel circlePanel;
	
	/**
	 * Create the frame.
	 */
	public CircleGUI(Manager manager) {
		//initiate manager object
		this.manager = manager;
		
		//Register for own window events
		this.addWindowListener(this);
		
		setBounds(100, 100, 800, 800);
		
		//Register for NodeEvents, FingerEvents, KeepAliveEvents
		manager.addNodeListener(this);
		manager.addFingerChangeListener(this);
		manager.addKeepAliveListener(this);

		//Add the circle
		circlePanel = new CirclePanel();
		add(circlePanel);
		
		//Add all Nodes that are already existing in the network
		for(Communication com: Network.getInstance().getClients()) 
			circlePanel.addNode(com);
		
		
		//Show the Frame
		this.setVisible(true);
	}

	@Override
	public void onNodeAdd(Communication com) {
		//Forward to circlePanel
		circlePanel.addNode(com);
	}

	@Override
	public void onNodeRemove(Communication com) {
		circlePanel.removeNode(com);
		
	}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {
		//Derregister from Node Events
		manager.removeNodeListener(this);
	}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void OnFingerChange(int changeType, NodeID node, NodeID finger) {
		//Forward to CirclePanel which stores the objects
		circlePanel.OnFingerChange(changeType, node, finger);		
	}

	@Override
	public void OnKeepAliveEvent(Date date, NodeID key, String networkAddress) {
		//Forward to circlePanel
		circlePanel.OnKeepAliveEvent(date, key, networkAddress);
	}
	
}
