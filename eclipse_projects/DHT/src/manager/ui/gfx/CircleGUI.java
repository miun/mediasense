package manager.ui.gfx;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import manager.Communication;
import manager.Manager;
import manager.Network;
import manager.dht.FingerEntry;
import manager.dht.Node;
import manager.dht.NodeID;
import manager.dht.Sensor;
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
public class CircleGUI extends JFrame 
implements NodeListener, WindowListener, FingerChangeListener, 
KeepAliveListener, ActionListener, ChangeListener {
	public static final int BORDER = 30;
	
	private Manager manager;
	
	//NORTH information Label
	private JPanel northPanel;
	private JLabel infoLabel;
	private JLabel healthLabel;
	
	//CENTER Painting Surface
	private PaintingSurface paintingSurface;
	private CirclePanel circleForNodes;
	
	private CirclePanel changedFingersSinceLastKeepalive;
	private CirclePanel sensorPanel;
	
	private int circleRadius;
	private HashMap<String, NodePanel> nodeObjects;
	
	private NodePanel activeNode;
	
	//WEST controlPanel
	private JPanel controlPanel;
	private JButton addButton;
	private JButton deleteFinger;
	private JSpinner networkDelay;
	private JCheckBox clearOnKeepalive;
	
	private JLabel nodeInfo;
	private JSpinner nodeDelay;
	private JButton nodeDeleteButton;
	
	
	
	//Timer for DHT health visualization
	Timer healthTimer;
	TimerTask healthTask;
	Object healthTaskLock = new Object();
	
	/**
	 * Create the frame.
	 */
	public CircleGUI(Manager manager, int circleRadius) {
		//super();
		//initiate manager object
		this.manager = manager;
		
		this.setLayout(new BorderLayout(20,20));
		
		if(circleRadius <= 0) circleRadius = 400;
		this.circleRadius = circleRadius;
		//this.setBounds(100, 100, 2*circleRadius+2*BORDER, 2*circleRadius+2*BORDER);
		
		//Register for own window events
		this.addWindowListener(this);
		
		//NORTH
		this.northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,20,10));
		getContentPane().add(northPanel,BorderLayout.NORTH);
		
		this.infoLabel = new JLabel("<html>No KeepAlive received yet</html>");
		northPanel.add(infoLabel);
		
		this.healthLabel = new JLabel("Health: "+manager.calculateHealthOfDHT(false));
		northPanel.add(healthLabel);
		
		//CENTER Painting surface things
		this.nodeObjects = new HashMap<String, NodePanel>();
		
		//Create the painting surface which holds the graphical elements
		paintingSurface = new PaintingSurface();
		paintingSurface.setBackground(Color.BLACK);
		Dimension d = new Dimension((circleRadius+BORDER+20)*2,(circleRadius+BORDER+20)*2);
		paintingSurface.setSize(d);
		paintingSurface.setPreferredSize(d);
		paintingSurface.setMinimumSize(d);
		paintingSurface.setMaximumSize(d);
		getContentPane().add(paintingSurface, BorderLayout.CENTER);
		
		//Add the circle for nodes
		circleForNodes = new CirclePanel(circleRadius,BORDER, Color.cyan, Color.cyan);
		paintingSurface.add(circleForNodes);
		
		//Create health timer
		healthTimer = new Timer();
		
		//Add all Nodes and their fingers that are already existing in the network
		for(Communication com: Network.getInstance().getClientList()) {
			NodePanel node = addNode(com);
			Node n = com.getNode();
			for(FingerEntry fe: n.getFingerTable().keySet()) {
				if(!fe.equals(n.getIdentity())) {
					node.addFinger(fe);
				}
				//add the predecessor if there is one
				FingerEntry pre = n.getPredecessor();
				if(pre != null) node.addFinger(pre);
			}
		}
		
		//A circlePanel which holds all finger-changes since the last keepAlive initiation
		this.changedFingersSinceLastKeepalive = new CirclePanel(circleRadius,BORDER, null, null);
		paintingSurface.add(changedFingersSinceLastKeepalive);		
		
		//West controlPanel
		this.controlPanel = new JPanel(new GridLayout(0,1));
		
		Dimension controlD = new Dimension(200, circleRadius+2*BORDER);
		controlPanel.setPreferredSize(controlD);
		controlPanel.setMaximumSize(controlD);
		
		//controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		getContentPane().add(controlPanel,BorderLayout.WEST);
		
		clearOnKeepalive = new JCheckBox("clear lines on KA");
		controlPanel.add(clearOnKeepalive);
		
		deleteFinger = new JButton("clear Lines");
		deleteFinger.addActionListener(this);
		controlPanel.add(deleteFinger);
		
		addButton = new JButton("add Node");
		addButton.addActionListener(this);
		controlPanel.add(addButton);
		
		//Dimension for the JSpinners
		Dimension sDim = new Dimension(150, 30);
		
		this.networkDelay = new JSpinner();
		networkDelay.setPreferredSize(sDim);
		networkDelay.setMaximumSize(sDim);
		networkDelay.addChangeListener(this);
		networkDelay.setValue(manager.getMessageDelay(null));
		controlPanel.add(networkDelay);
		
		//Seperator
		controlPanel.add(Box.createVerticalStrut(10));
		
		controlPanel.add(new JSeparator());
		
		controlPanel.add(Box.createVerticalStrut(10));
		
		//Node specific
		this.nodeInfo = new JLabel("Selected Node: -");
		controlPanel.add(nodeInfo);
		
		this.nodeDelay = new JSpinner();
		nodeDelay.setPreferredSize(sDim);
		nodeDelay.setMaximumSize(sDim);
		nodeDelay.addChangeListener(this);
		controlPanel.add(nodeDelay);
		
		this.nodeDeleteButton = new JButton("delete");
		nodeDeleteButton.addActionListener(this);
		controlPanel.add(nodeDeleteButton);
		
		//Show the Frame
		this.pack();
		
		this.setVisible(true);
	}
	
	private NodePanel addNode(Communication com) {
		NodeID nodeID = com.getNodeID();
		//Get Points on the circles
		Point pNode = circleForNodes.getPosOnCircle(nodeID,0);
		
		//Create the objects
		NodePanel node = new NodePanel(com,pNode, new CirclePanel(circleRadius, BORDER, null, null),this);
		
		nodeObjects.put(com.getLocalIp(), node);
		
		//Add object to PaintingSurface and to the HashMap
		circleForNodes.add(node);
		circleForNodes.add(node.getMyNumber());
		
		//After validation repaint the panel
		circleForNodes.validate();
		circleForNodes.repaint();
		
		return node;
	}
	
	//--------------------------------------//
	//this methods are called from NodePanel objects on clicks.
	//TODO can also be realized as Listener
	public void showFingers(NodePanel node) {
		this.paintingSurface.add(node.getMyFingers(),0);
		this.paintingSurface.validate();
		this.paintingSurface.repaint();
	}
	
	public void hideFingers(NodePanel node) {
		//Do not hide the fingers if the node is the active one
		if(activeNode!=null && activeNode.equals(node)) return;
		
		this.paintingSurface.remove(node.getMyFingers());
		this.paintingSurface.validate();
		this.paintingSurface.repaint();
	}
	
	public void setActiveNode(NodePanel node) {
		//Hide fingers from old activenode if there is one
		if(activeNode==null) {
			//Set the new active node
			this.activeNode = node;
		} else {
			//If that node was already the active node, make it inactive
			if(activeNode.equals(node)) {
				activeNode = null;
				//make the node control panel inactive
				nodeInfo.setText("active node: -");
				//remove the sensors from the painting surface
				if(sensorPanel != null) {
					paintingSurface.remove(sensorPanel);
					//refresh the paintingsurface
					paintingSurface.validate();
					paintingSurface.repaint();
				}
				return;
			} else {
				//Set the new active node
				NodePanel oldActive = activeNode;
				this.activeNode = node;
				//Hide Lines from the old active node
				hideFingers(oldActive);
			}
		}
		
		//remove the sensors from the painting surface
		if(sensorPanel != null) paintingSurface.remove(sensorPanel);
		
		//update the node control panel
		Communication com = activeNode.getCommunication();
		Node n = com.getNode();
		Map<Sensor,FingerEntry> sen = n.getSensors();
		nodeInfo.setText("<html>active Node: "+com.getNodeID()+com.getLocalIp());
		if(!sen.isEmpty()) {
			//Only if there are sensors
			
			//a circle to draw them
			sensorPanel = new CirclePanel(circleRadius, BORDER, null, null);
			
			Collection<Sensor> sensors = sen.keySet();
			Collection<Sensor> notRes = new HashSet<Sensor>();
			nodeInfo.setText(nodeInfo.getText()+"<br>My sensors:<br>");
			for(Sensor s: sensors) {
				//Show the sensors that belong to the node
				if(s.getOwner().getNetworkAddress().equals(com.getLocalIp())) {
					
					sensorPanel.add(new SensorPanel(sensorPanel.getPosOnCircle(s.getSensorHash(), 0), Color.RED));
					
					nodeInfo.setText(nodeInfo.getText() + s.getSensorHash() + " stored @ (" + sen.get(s).getNetworkAddress() +")<br>");
					
					//save those the node is not responsible for from the set
					if(!sen.get(s).getNetworkAddress().equals(com.getLocalIp())) {
						notRes.add(s);
					}
				}
			}
		
			nodeInfo.setText(nodeInfo.getText()+"Responsible for:<br>");
			for(Sensor s: sensors) {
				if(!notRes.contains(s)) {
					sensorPanel.add(new SensorPanel(sensorPanel.getPosOnCircle(s.getSensorHash(), 0), Color.GREEN));
				
					//Show the sensors that the node is responsible for
					nodeInfo.setText(nodeInfo.getText()+s.getSensorHash() + "from (" + s.getOwner().getNetworkAddress() + ")<br>");
				}
			}
			
			
			paintingSurface.add(sensorPanel);
			
		}
		nodeInfo.setText(nodeInfo.getText()+"</html>");
		
		if(!paintingSurface.isValid()) {
			//refresh the paintingsurface
			paintingSurface.validate();
			paintingSurface.repaint();
		}
			
		nodeDelay.setValue(manager.getMessageDelay(activeNode.getCommunication().getLocalIp()));
	}
	//----------------------------------------//
	
	@Override
	public void onNodeAdd(Date timeStamp,Communication com) {
		addNode(com);
		
		scheduleRefreshTimer();		
	}

	@Override
	public void onNodeRemove(Date timeStamp,Communication com) {
		//Get the objects relating to this Communication
		NodePanel node = nodeObjects.get(com.getLocalIp());
		
		if(node == null) return;
		//Remove the number and Node from the CirclePanel and the fingers from the PaintingSurface
		circleForNodes.remove(node.getMyNumber());
		circleForNodes.remove(node);
		paintingSurface.remove(node.getMyFingers());
		//Repaint the PaintingSurface
		paintingSurface.validate();
		paintingSurface.repaint();
		
		scheduleRefreshTimer();
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
	public void windowOpened(WindowEvent e) {
		//Register for NodeEvents, FingerEvents, KeepAliveEvents
		manager.addNodeListener(this);
		manager.addFingerChangeListener(this);
		manager.addKeepAliveListener(this);
	}

	@Override
	public void OnFingerChange(int changeType, FingerEntry node, FingerEntry finger) {
		Arrow a = null;
		//Filter the event type
		if(changeType==FINGER_CHANGE_ADD) {
			a = new Arrow(circleForNodes.getPosOnCircle(node.getNodeID(),0), circleForNodes.getPosOnCircle(finger.getNodeID(),0), circleForNodes.getPosOnCircle(finger.getNodeID(),-20), 20, (circleRadius+BORDER)*2, Arrow.ADD);
			NodePanel n = (NodePanel) nodeObjects.get(node.getNetworkAddress());
			if(n!=null)
				n.addFinger(finger);
		}
		else if(changeType==FINGER_CHANGE_ADD_BETTER) {
			a = new Arrow(circleForNodes.getPosOnCircle(node.getNodeID(),0), circleForNodes.getPosOnCircle(finger.getNodeID(),0), circleForNodes.getPosOnCircle(finger.getNodeID(),-10), 10, (circleRadius+BORDER)*2, Arrow.ADD_BETTER);
			NodePanel n = (NodePanel) nodeObjects.get(node.getNetworkAddress());
			if(n!=null)
				n.addFinger(finger);
		}
		else if(changeType==FINGER_CHANGE_REMOVE_WORSE) {
			a = new Arrow(circleForNodes.getPosOnCircle(node.getNodeID(),0), circleForNodes.getPosOnCircle(finger.getNodeID(),0), circleForNodes.getPosOnCircle(finger.getNodeID(),-30), 30, (circleRadius+BORDER)*2, Arrow.REMOVE_WORSE);
			NodePanel n = (NodePanel) nodeObjects.get(node.getNetworkAddress());
			if(n!=null)
				n.removeFinger(finger);
		}
		else if(changeType==FINGER_CHANGE_REMOVE) {
			a = new Arrow(circleForNodes.getPosOnCircle(node.getNodeID(),0), circleForNodes.getPosOnCircle(finger.getNodeID(),0), circleForNodes.getPosOnCircle(finger.getNodeID(),-40), 40, (circleRadius+BORDER)*2, Arrow.REMOVE);
			NodePanel n = (NodePanel) nodeObjects.get(node.getNetworkAddress());
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
		
		scheduleRefreshTimer();
	}

	@Override
	public void OnKeepAliveEvent(Date date, NodeID key, String networkAddress) {
		this.infoLabel.setText("<html>Last KeepAlive initiated by: {" + networkAddress + "} " + key + " on: " + date + "</html>");
		if(clearOnKeepalive.isSelected()){
			paintingSurface.remove(changedFingersSinceLastKeepalive);
			this.changedFingersSinceLastKeepalive = new CirclePanel(circleRadius,BORDER, null, null);
			paintingSurface.add(changedFingersSinceLastKeepalive);
			changedFingersSinceLastKeepalive.validate();
			changedFingersSinceLastKeepalive.repaint();
			
			scheduleRefreshTimer();
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
		} else if (e.getSource().equals(nodeDeleteButton)) {
			if(activeNode != null) {
				manager.removeNode(activeNode.getCommunication().getLocalIp());
			}
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		//Check which object triggered the event
		if(e.getSource().equals(nodeDelay)) {
			//The delay for one node should be changed
			//do nothing if no node is active (should not happen)
			if(activeNode == null) return;
			
			//Get the value of the JSpinner
			int value = (Integer) ((JSpinner) e.getSource()).getValue();
			//Set the delay
			manager.setMessageDelay(value,activeNode.getCommunication().getLocalIp());
		}else if(e.getSource().equals(networkDelay)) {
			//The delay for the whole network should be changed
			//Get the value of the JSpinner
			int value = (Integer) ((JSpinner) e.getSource()).getValue();
			//Set the delay
			manager.setMessageDelay(value,null);
		}
		
	}

	@Override
	public void onKillNode(Date timeStamp, Communication com) {
		// TODO Auto-generated method stub
		onNodeRemove(timeStamp,com);
	}
	
	private void scheduleRefreshTimer() {
		synchronized(healthTaskLock) {
			if(healthTask == null) {
				healthTask = new TimerTask() {

					@Override
					public void run() {
						//Update health
						healthLabel.setText("Health: "+manager.calculateHealthOfDHT(false));
											
						synchronized(healthTaskLock) {
							healthTask = null;
						}
					}
				};
				
				//Schedule timer
				healthTimer.schedule(healthTask,1000);
			}
		}
	}
}
