package graphwindow.plot.implementations;

import graphwindow.graphlayouts.LinePlotLayout;
import graphwindow.plot.IPlot;
import graphwindow.plot.graphtype;

@graphtype(	name = "Test 2 (EOG Remover)", 
			layout = LinePlotLayout.class )

public class Test2 extends WaveletDenoised {

	public Test2(IPlot plot) {
		super(plot);
		treshold = 70;
	}
	
	@Override
	protected double[] processScale(double[] data, double stdDev) {
		return threeshold(data, treshold * (1/(getAverage(data)+ getStdDev(data))));	}

	@Override
	protected double[] threeshold(double[] ds, double k) {
		for(int i=0; i<ds.length; i++)
			if(Math.abs(ds[i]) > k) ds[i] *= -0.7;
		return ds;
	}
}
