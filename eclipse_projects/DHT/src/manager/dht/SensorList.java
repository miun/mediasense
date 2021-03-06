package manager.dht;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class SensorList {
	private TreeMap<Sensor,FingerEntry> allSensors;
	private TreeMap<FingerEntry,Set<Sensor>> orderedToFingerEntry;
	
	public SensorList() {
		allSensors = new TreeMap<Sensor,FingerEntry>();
		orderedToFingerEntry = new TreeMap<FingerEntry, Set<Sensor>>();
	}
	
	public synchronized void put(FingerEntry node, Sensor sensor) {
		//put sensor to allSensors, remember if it was present before
		FingerEntry oldNode = allSensors.put(sensor, node);
		
		//put it also to the reversed ordered list
		Set<Sensor> nodeSet = orderedToFingerEntry.get(node);
		if(nodeSet == null) {
			//no sensor from this node yet, allocate new list
			nodeSet = new HashSet<Sensor>();
			orderedToFingerEntry.put(node, nodeSet);
		} 
		
		//add the sensor to the list
		nodeSet.add(sensor);
		
		
		//Now check if we have to remove the sensor from an other set
		if(oldNode != null && !oldNode.equals(node)) {
			//The sensor is also present in an other set -> remove it from there
			Set<Sensor> oldN = orderedToFingerEntry.get(oldNode);
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
	public synchronized boolean contains(Sensor sensor) {
		return allSensors.containsKey(sensor);
	}
	
	//Remove all sensors that belong to this node
	public synchronized Set<Sensor> remove(FingerEntry node) {
		//Get the sensors to remove
		Set<Sensor> toRemove = orderedToFingerEntry.remove(node);
		
		if(toRemove != null) {
			//Remove this sensors also from the allSensors map
			for(Sensor sensor: toRemove) {
				allSensors.remove(sensor);
			}
		} 
		return toRemove;
	}
	
	//Remove this sensor
	public synchronized FingerEntry remove(Sensor sensor) {
		//Remove the sensor from allSensors and store the FingerEntry
		FingerEntry removeHereToo = allSensors.remove(sensor);
		if(removeHereToo != null) {
			//Remove the sensors also from the reversed ordered set
			Set<Sensor> nodeSet = orderedToFingerEntry.get(removeHereToo);
			if(nodeSet != null) {
				nodeSet.remove(sensor);
			}
		}
		return removeHereToo;
	}
	
	//Returns the TreeSet containing all sensors that belong to that node
	public synchronized Set<Sensor> get(FingerEntry node) {
		Set<Sensor> result = new HashSet<Sensor>();
		Set<Sensor> content = orderedToFingerEntry.get(node);
		if(content != null) result.addAll(content);
		return result;
	}
	
	//Returns the FingerEntry where the sensor belongs to
	public synchronized FingerEntry get(Sensor sensor) {
		return allSensors.get(sensor);
	}
	
	public synchronized List<Sensor> getSensorInRange(NodeID start,NodeID end) {
		ArrayList<Sensor> result = new ArrayList<Sensor>();
		Sensor currentSensor;
		
		//return empty list if the list is empty
		if(!allSensors.isEmpty()) {
		
			if(start.compareTo(end) > 0) {
				//Start -> MAX; MIN -> END - 1
				currentSensor = allSensors.ceilingKey(new Sensor(start,null));
				while(currentSensor!=null) {
					result.add(currentSensor);
					currentSensor = allSensors.higherKey(currentSensor);
				}
				currentSensor = allSensors.firstKey();
				while(currentSensor!=null && currentSensor.compareTo(new Sensor(end, null)) < 0) {
					result.add(currentSensor);
					currentSensor = allSensors.higherKey(currentSensor);
				}
			}
			else if(start.compareTo(end) < 0) {
				//Start -> END - 1
				currentSensor = allSensors.ceilingKey(new Sensor(start,null));
				while(currentSensor!=null && currentSensor.compareTo(new Sensor(end, null)) < 0) {
					result.add(currentSensor);
					currentSensor = allSensors.higherKey(currentSensor);
				}
			}
		}

		return result;
	}
	
	public synchronized Set<Sensor> getAllSensors() {
		return new HashSet<Sensor>(allSensors.keySet());
	}
	
	//TODO remove debug
	public synchronized Map<Sensor,FingerEntry> getCopyOfAll() {
		return (Map<Sensor,FingerEntry>) allSensors.clone();
	}
}
