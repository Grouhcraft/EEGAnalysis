package filters;

import main.Logger;

/**
 * Energy Spectral Density computation methods
 * @author knoodrake
 */
public class EnergySpectralDensity extends Filter {
	
	public static double[][] test(double fs) {
		//double[][] a = synthetizer.Sinusoidal.generate(13, fs, 30, 0.20, 1);
		double[][] b = synthetizer.Sinusoidal.generate(20, fs, 1, 0.10, 1);
		//double[][] m = synthetizer.Sinusoidal.merge(a, b);
		return b;
	}
	
	
	/**
	 * Computes a simple Energy SD periodogram for the given 2D signal.
	 * Please don't forget that the length of the signal (in s) have to be
	 * at least 2 times the periods to catch ; and there are obviously the  
	 * same kind of considerations regarding to the sampling rate ! 
	 * @param signal			the 2D signal, where index 0 is X, 1 is Y
	 * @param fs				the sampling rate
	 * @param freqUpperLimit	the frequency upper limit shown in the periodogram
	 * @return
	 * 		The periodogram plot data
	 */
	public static double[][] compute(
			double[][] signal, 
			double fs, 
			double freqLowerLimit, 
			double freqUpperLimit
	) {
		return compute(signal, fs, freqLowerLimit, freqUpperLimit, true);
	}
	
	/**
	 * same as {@link #compute(double[][], double, double)} but without logarithmic Y axis
	 */
	public static double[][] computeNoLog(
			double[][] signal, 
			double fs, 
			double freqLowerLimit, 
			double freqUpperLimit
	) {
		return compute(signal, fs, freqLowerLimit, freqUpperLimit, false);
	}
	
	/**
	 * @see {@link #compute(double[][], double, double)}
	 */
	private static double[][] compute(
			double[][] signal, 
			double fs,
			double freqLowerLimit,
			double freqUpperLimit, 
			boolean logarithmicY 
	){
		if(freqUpperLimit > fs/2) {
			throw new IllegalArgumentException("data sampling must be at least 2 times the upper frequency limit");
		}
		FFT fft = new FFT(signal);
		fft.forward();
		double signalLen = (double)fft.getData().y.length;
		int len = (int) ((signalLen / fs) * freqUpperLimit);
		int from = (int) ((signalLen / fs) * freqLowerLimit);
		double[] sumy = new double[len - from];
		double[] sumx = new double[len - from];
		for(int i=from; i<len; i++) {
			sumy[i-from] = logarithmicY 
					? Math.log10(sq(module(fft.getData().y[i])))
					: sq(module(fft.getData().y[i]))
					;
		}
		
		for(int i=from; i<len; i++) {
			sumx[i-from] = ((double)i) * fs / signalLen;
		}
		return new double[][] { sumx, sumy };
	}
	
	/**
	 * Same as {@link #compute(double[][], double, double)} but in a raw fashion, 
	 * meaning without logarithmic Y axis and with implicit X axis.  
	 * Also, the complete frequency spectrum is returned.
	 * @param signal			the 2D signal, where just Y axis is passed, 
	 * 							and X value is considered to be equal to Y array index. 
	 * @param fs				the sampling rate
	 * @return
	 * 		The raw periodogram plot data
	 */
	public static double[] compute(double[] signal, double fs) {
		FFT fft = new FFT(signal);
		fft.forward();
		int fftLen = fft.getData().y.length;
		double[] sum = new double[fftLen];
		int i=0;
		for(Complex c : fft.getData().y) {
			sum[i] = sq(module(c));
			i++;
		}
		return sum;
	}
}
