package filters;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class FFT extends Filter {
	
	public static double[][] forward(double[][] data) {
		DoubleFFT_1D fft = new DoubleFFT_1D(data[Y].length);
		double[] y = data[Y].clone();
		fft.realForward(y);
		return new double[][] { data[X].clone(), y };
	}
	
	public static double[][] inverse(double[][] data) {
		DoubleFFT_1D fft = new DoubleFFT_1D(data[Y].length);
		double[] y = data[Y];
		fft.realInverse(y, false);
		return new double[][] { data[X].clone(), y };
	}
}
