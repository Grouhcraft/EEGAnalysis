package main;

/**
 * Contains the characteristics of a a brain emitted wave class
 * @author knoodrake
 * @see {@link WavesClasses}
 */
public abstract class WaveClass {
	protected final int MAX_AMPLITUDE = 2000;
	protected final int MAX_FREQUENCY = 2000;
	
	/**
	 * @return The upper limit of the frequency range of the wave class (in Hz)
	 */
	public abstract int getUpperFreq();
	
	/**
	 * @return The lower limit of the frequency range of the wave class (in Hz)
	 */
	public abstract int getLowerFreq();
	
	/**
	 * @return The upper limit of the amplitude range of the wave class (in µV)
	 */
	public abstract int getUpperAmpl();
	
	/**
	 * @return The lower limit of the amplitude range of the wave class (in µV)
	 */
	public abstract int getLowerAmpl();
}
