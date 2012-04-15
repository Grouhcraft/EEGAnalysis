package utils.types;

import org.junit.Assert;
import org.junit.Test;

public class ComplexTest {

	@Test
	public void testComplex() {
		Complex c = new Complex(3, 4);
		Assert.assertTrue(c.real == 3.);
		Assert.assertTrue(c.im == 4.);
	}

	@Test
	public void testFromFFTArray() {
		double[] in = { 3, 4, 5, 6 };
		Complex[] out = Complex.fromFFTArray(in);
		Assert.assertTrue(out[0].real == 3.);
		Assert.assertTrue(out[0].im == 4.);
		Assert.assertTrue(out[1].real == 5.);
		Assert.assertTrue(out[1].im == 6.);
	}

	@Test
	public void testToFFTArray() {
		Complex[] in = {
				new Complex(1, 2),
				new Complex(3, 4)
		};
		double[] out = Complex.toFFTArray(in);
		Assert.assertTrue(out[0] == 1.);
		Assert.assertTrue(out[1] == 2.);
		Assert.assertTrue(out[2] == 3.);
		Assert.assertTrue(out[3] == 4.);
	}

	@Test
	public void testZero() {
		Complex c = new Complex(3, 4);
		c.zero();
		Assert.assertTrue(c.im == 0.);
		Assert.assertTrue(c.real == 0.);
	}

}
