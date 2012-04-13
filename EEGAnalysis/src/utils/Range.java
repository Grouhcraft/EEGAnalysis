package utils;

public class Range<T extends Number> {
	public T lower;
	public T higher; 
	
	public Range(T lower, T higher) {
		this.lower = lower;
		this.higher = higher;
	}
	
	public Range() {
	}
	
	public T getLower() {
		return lower;
	}
	public T getHigher() {
		return higher;
	}
	public void setLower(T lower) {
		this.lower = lower;
	}
	public void setHigher(T higher) {
		this.higher = higher;
	}
}
