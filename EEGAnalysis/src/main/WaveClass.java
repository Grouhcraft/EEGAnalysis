package main;

public abstract class WaveClass {
	protected final int MAX_AMPLITUDE = 2000;
	protected final int MAX_FREQUENCY = 2000;
	
	public abstract int getUpperFreq();
	public abstract int getLowerFreq();
	public abstract int getUpperAmpl();
	public abstract int getLowerAmpl();
}
