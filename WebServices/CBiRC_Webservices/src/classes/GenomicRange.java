package classes;

import edu.iastate.javacyco.Frame;

/**
 * A Range is defined with an upper and lower bound. The value of the upper and lower bounds are included in the range. A range can be compared to a single
 * value, or to another range. Comparing to a single value allows checking if that value falls within or outside of the range. Comparing to another Range
 * allows checking if that range is contained by this range, contains this range, overlaps the upper bound of this range, overlaps the lower bound of 
 * this range, has no overlap with this range at all, or is exactly equal to this range.
 * 
 * @author Jesse
 */
class GenomicRange {
	private int start;
	private int end;
	private Frame frame;
	
	public GenomicRange(int start, int end) {
		this.start = start;
		this.end = end;
		this.frame = null;
	}
	public GenomicRange(int start, int end, Frame frame) {
		this.start = start;
		this.end = end;
		this.frame = frame;
	}

	public int compareTo(int point) {
		if (this.start <= point && point <= this.end) return 0;
		else return -1;
	}
	
	public boolean overLap(GenomicRange thatRange) {
		return (thatRange.start <= this.end && this.start <= thatRange.end);
	}
	
	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
	}
	
	public Frame getFrame() {
		return frame;
	}
}
