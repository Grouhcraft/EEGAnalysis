package filters;

public class EnergySpectralDensity extends Filter {
	public static double[][] compute(double[][] signal) {
		FFT fft = new FFT(signal);
		fft.forward();
		int fftLen = fft.getData().y.length;
		double[] sum = new double[fftLen];
		int i=0;
		for(Complex c : fft.getData().y) {
			sum[i] = sq(module(c));
			i++;
		}
		return new double[][] { fft.getData().x, sum };
	}
	
	public static double[] compute(double[] signal) {
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
