package filters.generators;

/**
 * A simple sinusoidal wave generator and merger.
 * Useful to test the signal analysis stuff
 * @author knoodrake
 */
public class SineWaveGenerator {
	private static final int X = 0;
	private static final int Y = 1;

	/**
	 * Generates a simple sin wave
	 * @param freq	Frequency in Hz
	 * @param fs	Sampling rate
	 * @param len	Length in seconds
	 * @param ampl	Amplitude (in.. something ?)
	 * @param phase	Phase (in Samples) @TODO: test it !
	 * @return
	 * the wave data where index 0 is X and 1 is Y
	 */
	public static double[][] generate(double freq, double fs, double len, double ampl, double phase) {
		double freq_RPS = 2. * Math.PI * freq;
		
		double[][] curve = new double[][] {
				new double[(int) (len * fs)],
				new double[(int) (len * fs)]
		};

		for(double i=0; i<curve[X].length; i++) {
			double y = ampl * Math.sin((i * (freq_RPS/fs))+phase);
			curve[X][(int) i] = i/fs;
			curve[Y][(int) i] = y;
		}

		return curve;
	}

	/**
	 * Merges two waves. Waves <b>Must</b> have the same sampling rate and length
	 * @param a	first signal
	 * @param b	second signal
	 * @return
	 * A signal with is the merge of the two given
	 * @see {@link #generate(double, double, double, double, double)}
	 */
	public static double[][] merge(double[][] a, double[][] b) {
		double[][] merged = new double[][] {
			new double[a[X].length],
			new double[a[X].length]
		};
		for(int i=0; i<a[X].length; i++) {
			merged[X][i] = i;
			merged[Y][i] = (a[Y][i] + b[Y][i]) / 2;
		}
		return merged;
	}

	public static double[][] interpolateX2(double[][] data) {
		return new double[][] {
			interpolateX2(data[X]),
			interpolateX2(data[Y])
		};
	}

	public static double[] interpolateX2(double[] data) {
		double[] newData = new double[data.length * 2];
		for(int i=0; i<newData.length-2; i+=2) {
			newData[i] = data[i/2];
			newData[i+1] = (data[i/2] + data[i/2+1])/2;
		}
		int i=newData.length-2;
		newData[i] = data[i/2];
		newData[i+1] = data[i/2];

		return newData;
	}
}
