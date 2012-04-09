package manager.ui;

import java.awt.BorderLayout;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import manager.Manager;
import manager.dht.NodeID;
import manager.dht.SHA1Generator;
import manager.listener.FingerChangeListener;

@SuppressWarnings("serial")
public class NodeInfo extends JFrame implements FingerChangeListener {
	private NodeID nodeID;
	private String address;
	private Manager manager;
	
	private JPanel contentPane;
	private JTextArea console;

	/**
	 * Create the frame.
	 */
	public NodeInfo(String networkAddress, Manager manager) {
		super(SHA1Generator.convertToHex(SHA1Generator.SHA1(String.valueOf(networkAddress))) + "{" + networkAddress + "}");
		this.address = networkAddress;
		this.nodeID = new NodeID(SHA1Generator.SHA1(String.valueOf(networkAddress)));
		this.manager = manager;
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		console = new JTextArea("My finger table:\n");
		console.setEditable(false);
		contentPane.add(console);
		this.setVisible(true);
	}

	@Override
	public void OnFingerChange(int changeType, NodeID node, NodeID finger) {
		//Only change if the change occured on the finger we are responsible for
		if(node.equals(nodeID)) {
			if(changeType == FINGER_CHANGE_ADD) {
				console.setText("Last change (added finger " + node.toString() + ") on: " + new Date() + "\n" + manager.showFinger(address));
			} else if(changeType == FINGER_CHANGE_REMOVE) {
				console.setText("Last change (removed finger " + node.toString() + ") on: " + new Date() + "\n" + manager.showFinger(address));
			}
		}
	}



}
