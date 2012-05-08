package communication.rudp.socket.rangeset;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class DeltaRangeList {
	//For range definition
	private class Range implements Comparable<Range> {
		private int start,end;
		
		public Range(int start,int end) {
			this.start = start;
			this.end = end;
		}

		public int getStart() {
			return start;
		}

		public int getEnd() {
			return end;
		}
		
		public Range merge(Range merge_range) {
			//Not merge-able cases
			if(merge_range == null) return null;
			if(this.start > merge_range.end + 1 || merge_range.start > this.end + 1) return null;
			
			//Merge
			return new Range(this.start < merge_range.start ? this.start : merge_range.start,this.end > merge_range.end ? this.end : merge_range.end);
		}

		@Override
		public int compareTo(Range o) {
			return start - o.start;
		}
	}
	
	private TreeSet<Range> set;
	
	public DeltaRangeList() {
		set = new TreeSet<Range>();
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
	
	public Iterator<Range> iterator() {
		return set.iterator();
	}
	
	public Integer[] toDifferentialArray() {
		List<Integer> result = new ArrayList<Integer>();
		
		for(Range r: set) {
			result.add(r.getStart());
			result.add(r.getEnd() + 1);
		}
		
		return result.toArray(new Integer[result.size()]);
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
