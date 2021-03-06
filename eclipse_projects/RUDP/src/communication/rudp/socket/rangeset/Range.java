package communication.rudp.socket.rangeset;

//For range definition
public class Range implements Comparable<Range> {
	private short start,end;
	
	public Range(short start,short end) {
		this.start = start;
		this.end = end;
	}

	public short getStart() {
		return start;
	}

	public short getEnd() {
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
