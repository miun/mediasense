package communication.rudp.socket.rangeset;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class DeltaRangeList {
	
	private TreeSet<Range> set;
	
	public DeltaRangeList() {
		set = new TreeSet<Range>();
	}
	
	public DeltaRangeList(short[] diffArray) {
		//Create set
		this();
		
		//Construct ranges
		for(int i = 0; i < diffArray.length; i = i + 2) {
			set.add(new Range(diffArray[i],diffArray[i + 1] - 1));
		}
	}
	
	public void add(int key) {
		Range addRange = new Range(key,key);
		Range newRange = null;
		Range lower;
		Range higher;
		
		//Get range candidate
		lower = set.floor(addRange);
		higher = set.ceiling(addRange);

		//Try to merge lower range
		if(lower != null) {
			if((newRange = lower.merge(addRange)) != null) {
				set.remove(lower);
			}
		}
		
		//Try to merge higher range
		if(higher != null) {
			if((newRange = higher.merge(newRange != null ? newRange : addRange)) != null) {
				set.remove(higher);
			}
		}
		
		//Add new range
		if(newRange != null) {
			set.add(newRange);
		}
		else {
			set.add(addRange);
		}
	}
	
	public List<Integer> toElementArray() {
		List<Integer> result = new ArrayList<Integer>();
		
		for(Range r: set) {
			for(int i = r.getStart(); i <= r.getEnd(); i++) {
				result.add(i);
			}
		}
		
		return result;
	}
	
	public List<Integer> toDifferentialArray() {
		List<Integer> result = new ArrayList<Integer>();
		
		for(Range r: set) {
			result.add(r.getStart());
			result.add(r.getEnd() + 1);
		}
		
		return result;
	}
	
	public void remove(int key) {
		//TODO create it
	}
	
	public void remove(Range range) {
		
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
