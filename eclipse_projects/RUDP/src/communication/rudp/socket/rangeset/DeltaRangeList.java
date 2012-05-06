package communication.rudp.socket.rangeset;

import java.util.Iterator;
import java.util.TreeMap;

public class DeltaRangeList<K extends Comparable<K>,V extends Comparable<V>> {
	private TreeMap<K,V> set;
	private K origin;
	
	public DeltaRangeList() {
		set = new TreeMap<K,V>();
	}
	
	public DeltaRangeList(K origin) {
		super();
		this.origin = origin;
	}
	
	public void add(K key,V value) {
		V old_value = set.get(key);
		Iterator<K> it = set.keySet().iterator();
		
		//Maybe get the next lower and the next higher item, and think about origin 
		
		//it.
		
		//if(set.containsKey(key)
	}
	
	public K remove(K key) {
		//TODO create it
		return null;
	}
	
	public void clear() {
		set.clear();
	}
	
	public int size() {
		return set.size();
	}
	
	public boolean isEmpty() {
		return set.isEmpty();
	}
}
