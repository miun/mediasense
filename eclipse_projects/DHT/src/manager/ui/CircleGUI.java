package manager.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Date;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import manager.Communication;
import manager.Manager;
import manager.Network;
import manager.dht.FingerEntry;
import manager.dht.Node;
import manager.dht.NodeID;
import manager.listener.FingerChangeListener;
import manager.listener.KeepAliveListener;
import manager.listener.NodeListener;

/**
 * This class manages all the components joining and leaving the gui
 * acts as a kind of model..
 * @author florian
 *
 */
@SuppressWarnings("serial")
public class CircleGUI extends JFrame implements NodeListener, WindowListener, FingerChangeListener, KeepAliveListener, ActionListener {
	public static final int BORDER = 30;
	
	private Manager manager;
	
	//NORTH information Label
	private JPanel northPanel;
	private JLabel infoLabel;
	private JLabel healthLabel;
	
	//CENTER Painting Surface
	private PaintingSurface paintingSurface;
	private CirclePanel circleForNodes;
	private CirclePanel circleForIds;
	
	private CirclePanel changedFingersSinceLastKeepalive;
	
	private int circleRadius;
	private HashMap<String, JComponent[]> nodeObjects;
	//private Collection<Arrow> changedFingersSinceLastKeepAlive;
	
	//EAST control Panel
	private JPanel controlPanel;
	private JButton addButton;
	private JButton deleteFinger;
	
	private JCheckBox clearOnKeepalive;
	
	/**
	 * Create the frame.
	 */
	public CircleGUI(Manager manager, int circleRadius) {
		//super();
		//initiate manager object
		this.manager = manager;
		
		this.setLayout(new BorderLayout());
		
		if(circleRadius <= 0) circleRadius = 250;
		this.circleRadius = circleRadius;
		//this.setBounds(100, 100, 2*circleRadius+2*BORDER, 2*circleRadius+2*BORDER);
		
		//Register for NodeEvents, FingerEvents, KeepAliveEvents
		manager.addNodeListener(this);
		manager.addFingerChangeListener(this);
		manager.addKeepAliveListener(this);
		
		//Register for own window events
		//this.addWindowListener(this);
		
		//NORTH
		this.northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,20,10));
		getContentPane().add(northPanel,BorderLayout.NORTH);
		
		this.infoLabel = new JLabel("No KeepAlive received yet");
		northPanel.add(infoLabel);
		
		this.healthLabel = new JLabel("Health: "+manager.calculateHealthOfDHT(false));
		northPanel.add(healthLabel);
		
		//CENTER Painting surface things
		this.nodeObjects = new HashMap<String, JComponent[]>();
		
		//Create the painting surface which holds the graphical elements
		paintingSurface = new PaintingSurface();
		paintingSurface.setBackground(Color.BLACK);
		Dimension d = new Dimension((circleRadius+BORDER+20)*2,(circleRadius+BORDER+20)*2);
		paintingSurface.setSize(d);
		paintingSurface.setPreferredSize(d);
		paintingSurface.setMinimumSize(d);
		getContentPane().add(paintingSurface, BorderLayout.CENTER);
		
		//Add the circle for nodes
		circleForNodes = new CirclePanel(circleRadius,BORDER, Color.cyan, null);
		paintingSurface.add(circleForNodes);
		
		//And the one for the identification
		circleForIds = new CirclePanel(circleRadius+20,BORDER-10, null, Color.cyan);
		paintingSurface.add(circleForIds);
		
		//Add all Nodes and their fingers that are already existing in the network
		for(Communication com: Network.getInstance().getClients()) {
			NodePanel node = addNode(com);
			Node n = com.getNode();
			for(FingerEntry fe: n.getFingerTable().keySet()) {
				if(!fe.equals(n.getIdentity())) {
					node.addFinger(fe);
				}
			}
		}
		
		//A circlePanel which holds all fingerchanges since the last keepAlive initiation
		this.changedFingersSinceLastKeepalive = new CirclePanel(circleRadius,BORDER, null, null);
		paintingSurface.add(changedFingersSinceLastKeepalive);
		
		//EAST controlPanel
		this.controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		getContentPane().add(controlPanel,BorderLayout.EAST);
		
		clearOnKeepalive = new JCheckBox("clear on KA");
		controlPanel.add(clearOnKeepalive);
		
		addButton = new JButton("add Node");
		addButton.addActionListener(this);
		controlPanel.add(addButton);
		
		deleteFinger = new JButton("delete Lines");
		deleteFinger.addActionListener(this);
		controlPanel.add(deleteFinger);
		
		//Show the Frame
		this.pack();
		
		this.setVisible(true);
	}
	
	private NodePanel addNode(Communication com) {
		NodeID nodeID = com.getNodeID();
		//Get Points on the circles
		Point pNode = circleForNodes.getPosOnCircle(nodeID,0);
		Point pIden = circleForIds.getPosOnCircle(nodeID,0);
		
		//Create the objects
		NodePanel node = new NodePanel(com,pNode, new CirclePanel(circleRadius, BORDER, null, null),this);
		
		NumberPanel id = new NumberPanel(com.getLocalIp(),pIden);
		
		//Add object to paintingsurface and to the HashMap
		circleForNodes.add(node);
		circleForIds.add(id);
		JComponent[] arr = new JComponent[2];
		arr[0] = node; arr[1] = id;
		nodeObjects.put(com.getLocalIp(), arr);
		
		//After validation repaint the painting surface
		paintingSurface.validate();
		paintingSurface.repaint();
		
		return node;
	}
	
	public void showFingers(CirclePanel cp) {
		this.paintingSurface.add(cp,0);
		this.paintingSurface.validate();
		this.paintingSurface.repaint();
	}
	
	public void hideFingers(CirclePanel cp) {
		this.paintingSurface.remove(cp);
		this.paintingSurface.validate();
		this.paintingSurface.repaint();
	}
	
	
	@Override
	public void onNodeAdd(Communication com) {
		addNode(com);
		
		healthLabel.setText("Health: "+manager.calculateHealthOfDHT(false));
	}

	@Override
	public void onNodeRemove(Communication com) {
		//Get the objects relating to this Communication
		JComponent[] arr = nodeObjects.get(com.getLocalIp());
		
		if(arr == null) return;
		//TODO remove the fingers if they are currently present
		circleForNodes.remove(arr[0]);
		circleForIds.remove(arr[1]);
		
		healthLabel.setText("Health: "+manager.calculateHealthOfDHT(false));
	}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {
		//Derregister from all the listeners
		manager.removeNodeListener(this);
		manager.removeFingerChangeListener(this);
		manager.removeKeepAliveListener(this);
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
	public void OnFingerChange(int changeType, FingerEntry node, FingerEntry finger) {
		Arrow a = null;
		//Filter the event type
		if(changeType==FINGER_CHANGE_ADD) {
			a = new Arrow(circleForNodes.getPosOnCircle(node.getNodeID(),0), circleForNodes.getPosOnCircle(finger.getNodeID(),0), (circleRadius+BORDER)*2, Arrow.ADD);
			NodePanel n = (NodePanel) nodeObjects.get(node.getNetworkAddress())[0];
			if(n!=null)
				n.addFinger(finger);
		}
		else if(changeType==FINGER_CHANGE_ADD_BETTER) {
			a = new Arrow(circleForNodes.getPosOnCircle(node.getNodeID(),0), circleForNodes.getPosOnCircle(finger.getNodeID(),0), (circleRadius+BORDER)*2, Arrow.ADD_BETTER);
			NodePanel n = (NodePanel) nodeObjects.get(node.getNetworkAddress())[0];
			if(n!=null)
				n.addFinger(finger);
		}
		else if(changeType==FINGER_CHANGE_REMOVE_WORSE) {
			a = new Arrow(circleForNodes.getPosOnCircle(node.getNodeID(),0), circleForNodes.getPosOnCircle(finger.getNodeID(),0), (circleRadius+BORDER)*2, Arrow.REMOVE_WORSE);
			NodePanel n = (NodePanel) nodeObjects.get(node.getNetworkAddress())[0];
			if(n!=null)
				n.removeFinger(finger);
		}
		else if(changeType==FINGER_CHANGE_REMOVE) {
			a = new Arrow(circleForNodes.getPosOnCircle(node.getNodeID(),0), circleForNodes.getPosOnCircle(finger.getNodeID(),0), (circleRadius+BORDER)*2, Arrow.REMOVE);
			NodePanel n = (NodePanel) nodeObjects.get(node.getNetworkAddress())[0];
			if(n!=null)
				n.removeFinger(finger);
		}
		//Shouldn't happen - unknown finger event
		if(a==null) return;
		
		//Put the arrow to its container
		changedFingersSinceLastKeepalive.add(a);
		
		//refresh the circle
		changedFingersSinceLastKeepalive.validate();
		changedFingersSinceLastKeepalive.repaint();
		
		healthLabel.setText("Health: "+manager.calculateHealthOfDHT(false));
	}

	@Override
	public void OnKeepAliveEvent(Date date, NodeID key, String networkAddress) {
		this.infoLabel.setText("Last KeepAlive initiated by: {" + networkAddress + "} " + key + " on: " + date);
		if(clearOnKeepalive.isSelected()){
			paintingSurface.remove(changedFingersSinceLastKeepalive);
			this.changedFingersSinceLastKeepalive = new CirclePanel(circleRadius,BORDER, null, null);
			paintingSurface.add(changedFingersSinceLastKeepalive);
			changedFingersSinceLastKeepalive.validate();
			changedFingersSinceLastKeepalive.repaint();
			
			healthLabel.setText("Health: "+manager.calculateHealthOfDHT(false));
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(addButton)) {
			manager.addNode(String.valueOf(0));
		} 
		else if (e.getSource().equals(deleteFinger)){
			paintingSurface.remove(changedFingersSinceLastKeepalive);
			this.changedFingersSinceLastKeepalive = new CirclePanel(circleRadius,BORDER, null, null);
			paintingSurface.add(changedFingersSinceLastKeepalive);
			changedFingersSinceLastKeepalive.validate();
			changedFingersSinceLastKeepalive.repaint();
		}
	}
	
}
