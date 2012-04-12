package manager.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import manager.Communication;
import manager.Manager;
import manager.Network;
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
public class CircleGUI extends JFrame implements NodeListener, WindowListener, FingerChangeListener, KeepAliveListener {
	public static final int BORDER = 30;
	
	private Manager manager;
	
	//NORTH information Label
	private JLabel infoLabel;
	
	//CENTER Painting Surface
	private JPanel paintingSurface;
	private CirclePanel circleForNodes;
	private CirclePanel circleForIds;
	
	private CirclePanel changedFingersSinceLastKeepalive;
	
	private int circleRadius;
	private HashMap<Communication, JComponent[]> nodeObjects;
	//private Collection<Arrow> changedFingersSinceLastKeepAlive;
	
	//EAST control Panel
	private ControlPanel controlPanel;
	
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
		this.infoLabel = new JLabel("No KeepAlive received yet");
		getContentPane().add(infoLabel,BorderLayout.NORTH);
		
		//CENTER Painting surface things
		this.nodeObjects = new HashMap<Communication, JComponent[]>();
		//Create the painting surface which holds the graphical elements
		paintingSurface = new JPanel(null);
		paintingSurface.setBackground(Color.BLACK);
		Dimension d = new Dimension((circleRadius+BORDER+20)*2,(circleRadius+BORDER+20)*2);
		paintingSurface.setSize(d);
		paintingSurface.setPreferredSize(d);
		paintingSurface.setMinimumSize(d);
		getContentPane().add(paintingSurface, BorderLayout.CENTER);
		
		//Add the circle for nodes
		circleForNodes = new CirclePanel(circleRadius,BORDER, Color.cyan);
		paintingSurface.add(circleForNodes);
		
		//And the one for the identification
		circleForIds = new CirclePanel(circleRadius+20,BORDER-10, null);
		paintingSurface.add(circleForIds);
		
		//Add all Nodes that are already existing in the network
		for(Communication com: Network.getInstance().getClients()) 
			addNode(com);
		
		//A circlePanel which holds all fingerchanges since the last keepAlive initiation
		this.changedFingersSinceLastKeepalive = new CirclePanel(circleRadius,BORDER, null);
		paintingSurface.add(changedFingersSinceLastKeepalive);
		
		//EAST controlPanel
		this.controlPanel = new ControlPanel();
		this.getContentPane().add(controlPanel,BorderLayout.EAST);
		
		
		
		
		//Show the Frame
		this.pack();
		
		this.setVisible(true);
	}
	
	private void addNode(Communication com) {
		NodeID nodeID = com.getNodeID();
		//Get Points on the circles
		Point pNode = circleForNodes.getPosOnCircle(nodeID);
		Point pIden = circleForIds.getPosOnCircle(nodeID);
		
		//Create the objects
		NodePanel node = new NodePanel(com.getLocalIp(),pNode);
		
		NumberPanel id = new NumberPanel(com.getLocalIp(),pIden);
		
		//Add object to paintingsurface and to the HashMap
		circleForNodes.add(node);
		circleForIds.add(id);
		JComponent[] arr = new JComponent[2];
		arr[0] = node; arr[1] = id;
		nodeObjects.put(com, arr);
		
		//After validation repaint the painting surface
		paintingSurface.validate();
		paintingSurface.repaint();
		
	}
	
	
	@Override
	public void onNodeAdd(Communication com) {
		addNode(com);
	}

	@Override
	public void onNodeRemove(Communication com) {
		//Get the objects relating to this Communication
		JComponent[] arr = nodeObjects.get(com);
		
		if(arr == null) return;
		
		circleForNodes.remove(arr[0]);
		circleForNodes.remove(arr[1]);
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
	public void OnFingerChange(int changeType, NodeID node, NodeID finger) {
		Arrow a = null;
		//Filter the event type
		if(changeType==FINGER_CHANGE_ADD) {
			a = new Arrow(circleForNodes.getPosOnCircle(node), circleForNodes.getPosOnCircle(finger), (circleRadius+BORDER)*2, Color.YELLOW);
		}
		else if(changeType==FINGER_CHANGE_ADD_BETTER) {
			a = new Arrow(circleForNodes.getPosOnCircle(node), circleForNodes.getPosOnCircle(finger), (circleRadius+BORDER)*2, Color.GREEN);
		}
		else if(changeType==FINGER_CHANGE_REMOVE_WORSE) {
			a = new Arrow(circleForNodes.getPosOnCircle(node), circleForNodes.getPosOnCircle(finger), (circleRadius+BORDER)*2, Color.RED);
		}
		else if(changeType==FINGER_CHANGE_REMOVE) {
			a = new Arrow(circleForNodes.getPosOnCircle(node), circleForNodes.getPosOnCircle(finger), (circleRadius+BORDER)*2, Color.ORANGE);
		}
		//Shouldnt happen - unknown finger event
		if(a==null) return;
		
		//Put the arrow to its container
		changedFingersSinceLastKeepalive.add(a);
		
		//refresh the circle
		changedFingersSinceLastKeepalive.validate();
		changedFingersSinceLastKeepalive.repaint();
	}

	@Override
	public void OnKeepAliveEvent(Date date, NodeID key, String networkAddress) {
		this.infoLabel.setText("Last KeepAlive initiated by: {" + networkAddress + "} " + key + " on: " + date);
		if(controlPanel.isClearOnKeepalive()){
			paintingSurface.remove(changedFingersSinceLastKeepalive);
			this.changedFingersSinceLastKeepalive = new CirclePanel(circleRadius,BORDER, null);
			paintingSurface.add(changedFingersSinceLastKeepalive);
			changedFingersSinceLastKeepalive.validate();
			changedFingersSinceLastKeepalive.repaint();
		}
	}
	
}
