package manager.ui.gfx;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import manager.Manager;
import manager.dht.FingerEntry;
import manager.dht.NodeID;
import manager.dht.SHA1Generator;
import manager.listener.FingerChangeListener;

@SuppressWarnings("serial")
public class NodeInfo extends JFrame implements FingerChangeListener {
	private NodeID nodeID;
	private String address;
	private Manager manager;
	
	private JScrollPane scrollPane;
	private JTextArea console;

	/**
	 * Create the frame.
	 */
	public NodeInfo(String networkAddress, Manager manager) {
		super();
		this.address = networkAddress;
		this.nodeID = new NodeID(SHA1Generator.SHA1(String.valueOf(networkAddress)));
		this.manager = manager;
		//init
		this.setTitle(nodeID.toString() + " {" + networkAddress + "}");
		setBounds(1200, 100, 250, 200);
		
		console = new JTextArea("My finger table:\n" + manager.showFinger(networkAddress));
		console.setEditable(false);
		
		scrollPane = new JScrollPane(console);
		//setpreferred size and pack... stupid but works
		scrollPane.setPreferredSize(new Dimension(500,300));
		this.getContentPane().add(scrollPane,BorderLayout.CENTER);
		this.pack();
		this.setVisible(true);
		
		//register listener at manager
		manager.addFingerChangeListener(this);
	}

	@Override
	public void OnFingerChange(int changeType, FingerEntry node, FingerEntry finger) {
		//Only change if the change occurred on the finger we are responsible for
		if(nodeID.equals(node.getNodeID())) {
			if(changeType == FINGER_CHANGE_ADD) {
				console.setText("Last change (added finger " + finger.toString() + ") on: " + new Date() + "\n" + manager.showFinger(address));
			} else if(changeType == FINGER_CHANGE_REMOVE) {
				console.setText("Last change (removed finger " + finger.toString() + ") on: " + new Date() + "\n" + manager.showFinger(address));
			} else if(changeType == FINGER_CHANGE_ADD_BETTER) {
			console.setText("Last change (added BETTER finger " + finger.toString() + ") on: " + new Date() + "\n" + manager.showFinger(address));
			} else if(changeType == FINGER_CHANGE_REMOVE_WORSE) {
				console.setText("Last change (removed WORSE finger " + finger.toString() + ") on: " + new Date() + "\n" + manager.showFinger(address));
			}
		}
	}

}
