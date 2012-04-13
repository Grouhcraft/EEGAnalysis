package graphwindow.plot.implementations;

import filters.utils.Filter;
import graphwindow.graphlayouts.LinePlotLayout;
import graphwindow.plot.IPlot;
import graphwindow.plot.graphtype;

@graphtype(	name = "TestEOGRemoving", 
			layout = LinePlotLayout.class )

public class TestEOGRemoving extends WaveletDenoised {

	public TestEOGRemoving(IPlot plot) {
		super(plot);
		treshold = 70;
	}
	
	@Override
	protected double[] processScale(double[] data, double stdDev) {
		return threeshold(data, treshold * (1/(Filter.average(data)+ Filter.stdDeviation(data))));	
	}

	@Override
	protected double[] threeshold(double[] ds, double k) {
		for(int i=0; i<ds.length; i++)
			if(Math.abs(ds[i]) > k) ds[i] *= -0.7;
		return ds;
	}
}
