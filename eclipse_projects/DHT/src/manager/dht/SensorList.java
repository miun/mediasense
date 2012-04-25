package manager.dht;

import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class SensorList {
	private TreeMap<NodeID,FingerEntry> allSensors;
	private TreeMap<FingerEntry,TreeSet<NodeID>> orderedToFingerEntry;
	
	public SensorList() {
		allSensors = new TreeMap<NodeID,FingerEntry>();
		orderedToFingerEntry = new TreeMap<FingerEntry, TreeSet<NodeID>>();
	}
	
	public synchronized void put(FingerEntry node, NodeID sensor) {
		//put sensor to allSensors, remember if it was present before
		FingerEntry oldNode = allSensors.put(sensor, node);
		
		//put it also to the reversed ordered list
		TreeSet<NodeID> nodeSet = orderedToFingerEntry.get(node);
		if(nodeSet == null) {
			//no sensor from this node yet, allocate new list
			nodeSet = new TreeSet<NodeID>();
			orderedToFingerEntry.put(node, nodeSet);
		} 
		
		//add the sensor to the list
		nodeSet.add(sensor);
		
		
		//Now check if we have to remove the sensor from an other set
		if(oldNode != null && !oldNode.equals(node)) {
			//The sensor is also present in an other set -> remove it from there
			TreeSet<NodeID> oldN = orderedToFingerEntry.get(oldNode);
			if(oldN != null) {
				// TODO use these later oldN.remove(sensor);
				if(!oldN.remove(sensor)) {
					//TODO just for checking
					System.out.println("Error in SensorList put");
				}
			} else {
				//TODO no else just for checking
				System.out.println("Error in SensorList put");				
			}
		}
	}
	
	//Check if we store sensors from that node
	public synchronized boolean contains(FingerEntry node) {
		return orderedToFingerEntry.containsKey(node);
	}
	
	//Check if we store that sensor
	public synchronized boolean contains(NodeID sensor) {
		return allSensors.containsKey(sensor);
	}
	
	//Remove all sensors that belong to this node
	public synchronized TreeSet<NodeID> remove(FingerEntry node) {
		//Get the sensors to remove
		TreeSet<NodeID> toRemove = orderedToFingerEntry.remove(node);
		if(toRemove != null) {
			//Remove this sensors also from the allSensors map
			for(NodeID sensor: toRemove) {
				allSensors.remove(sensor);
			}
		} 
		return toRemove;
	}
	
	//Remove this sensor
	public synchronized FingerEntry remove(NodeID sensor) {
		//Remove the sensor from allSensors and store the FingerEntry
		FingerEntry removeHereToo = allSensors.remove(sensor);
		if(removeHereToo != null) {
			//Remove the sensors also from the reversed ordered set
			TreeSet<NodeID> nodeSet = orderedToFingerEntry.get(removeHereToo);
			if(nodeSet != null) {
				nodeSet.remove(sensor);
			}
		}
		return removeHereToo;
	}
	
	//Returns the TreeSet conatining all sensors that belong to that node
	public synchronized TreeSet<NodeID> get(FingerEntry node) {
		return orderedToFingerEntry.get(node);
	}
	
	//Returns the FingerEntry where the sensor belongs to
	public synchronized FingerEntry get(NodeID sensor) {
		return allSensors.get(sensor);
	}
}
