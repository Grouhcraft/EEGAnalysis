package plotframes.plots.implementations;

import plotframes.graphlayouts.LinePlotLayout;
import plotframes.plots.IPlot;
import plotframes.plots.annotations.GraphType;
import filters.Filter;

@GraphType(	name = "TestEOGRemoving", 
			layout = LinePlotLayout.class )

public class TestEOGRemovingPlot extends WaveletDenoisedPlot {

	public TestEOGRemovingPlot(IPlot plot) {
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
