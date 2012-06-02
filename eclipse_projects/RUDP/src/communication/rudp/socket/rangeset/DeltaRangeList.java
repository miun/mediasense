package communication.rudp.socket.rangeset;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class DeltaRangeList {
	
	private TreeSet<Range> set;
	
	public DeltaRangeList() {
		set = new TreeSet<Range>();
	}
	
	public DeltaRangeList(List<Short> diffArray) {
		//Create set
		this();
		
		//Construct ranges
		for(int i = 0; i < diffArray.size() - 1; i = i + 2) {
			set.add(new Range(diffArray.get(i),(short)(diffArray.get(i + 1) - 1)));
		}
	}
	
	public void add(short key) {
		Range addRange = new Range(key,key);
		Range newRange = null;
		Range lower;
		Range higher;
		
		//Get range candidate
		synchronized(this) {
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
			
			//System.out.println(this.toString());
		}
	}
	
	public List<Integer> toElementArray() {
		List<Integer> result = new ArrayList<Integer>();
		
		synchronized(this) {
			for(Range r: set) {
				for(int i = r.getStart(); i <= r.getEnd(); i++) {
					result.add(i);
				}
			}
		}
		
		return result;
	}
	
	public List<Short> toDifferentialArray() {
		List<Short> result = new ArrayList<Short>();
		
		synchronized(this) {
			for(Range r: set) {
				result.add(r.getStart());
				result.add((short)(r.getEnd() + 1));
			}
		}
		
		return result;
	}
	
	public Range get(short key) {
		Range range;
		
		synchronized(this) {
			range = set.ceiling(new Range(key,key));
		}
		
		//Return the range the key is in
		if(range != null && range.getEnd() >= key) {
			return range;
		}
		else {
			return null;
		}
	}
	
	public void remove(int key) {
		//TODO create it
	}
	
	public void remove(Range range) {
		//not implemented, because not needed
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
	
	public void shiftRanges(short delta) {
		int newStart,newEnd;
		TreeSet<Range> newSet = new TreeSet<Range>();
		
		synchronized(this) {
			for(Range r: set) {
				//Calculate new positions
				newStart = r.getStart() + delta;
				newEnd = r.getEnd() + delta;
				
				//Check if range has to be dropped
				if(newEnd >= 0 && newStart >= 0) {
					newSet.add(new Range((short)newStart,(short)newEnd));
				}
				else if(newEnd >= 0) {
					//Correct start limit
					newStart = 0;
					newSet.add(new Range((short)newStart,(short)newEnd));
				}
			}
		
			//Remove all the ranges that are fully gone
			set = newSet;
		}

		//System.out.println(this.toString());
	}
	
	public String toString() {
		return toString(0);
	}

	public String toString(int offset) {
		String result = "";
		
		synchronized(this) {
			for(Range r: set) {
				result += (r.getStart() + offset) + "->" + (r.getEnd() + offset) + ","; 
			}
		}
		
		return result;
	}
}
