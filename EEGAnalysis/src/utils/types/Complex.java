package utils.types;

public class Complex {
	public double im;
	public double real;

	public Complex(double real, double im) {
		this.im =  im;
		this.real = real;
	}

	/**
	 * Opposite of {@link #toFFTArray(Complex[])}
	 * @param in
	 */
	public static Complex[] fromFFTArray(double[] in) {
		Complex[] c = new Complex[in.length/2];
		for(int i=0; i<in.length/2; i++) {
			c[i] = new Complex(in[i*2], in[i*2+1]);
		}
		return c;
	}

	/**
	 * Transforms a complex array to a double array
	 * where index = im, index +1 = real
	 * @param c
	 */
	public static double[] toFFTArray(Complex[] c) {
		double[] d = new double[c.length * 2];
		for(int i=0; i<c.length; i++) {
			d[i*2] = c[i].real;
			d[i*2+1] = c[i].im;
		}
		return d;
	}

	/**
	 * Sets both im and real parts to 0
	 */
	public void zero() {
		im = 0;
		real = 0;
	}
}
