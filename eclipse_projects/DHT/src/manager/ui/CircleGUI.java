package manager.ui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import manager.Communication;
import manager.Manager;
import manager.Network;
import manager.listener.NodeListener;

@SuppressWarnings("serial")
public class CircleGUI extends JFrame implements NodeListener, WindowListener {

	private Manager manager;
	
	private CirclePanel circlePanel;
	
	/**
	 * Create the frame.
	 */
	public CircleGUI(Manager manager) {
		
		this.manager = manager;
		
		//Register for own window events
		this.addWindowListener(this);
		
		setBounds(100, 100, 800, 800);
		
		//Register for NodeEvents
		manager.addNodeListener(this);

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
	
}
