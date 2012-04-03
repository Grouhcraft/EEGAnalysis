package graphwindow.plot;

import java.util.Arrays;

import main.utils.Logger;
import math.transform.jwave.Transform;
import math.transform.jwave.handlers.FastWaveletTransform;
import math.transform.jwave.handlers.wavelets.Coif06;
import math.transform.jwave.handlers.wavelets.Daub02;
import math.transform.jwave.handlers.wavelets.Daub03;
import math.transform.jwave.handlers.wavelets.Daub04;
import math.transform.jwave.handlers.wavelets.Haar02;
import math.transform.jwave.handlers.wavelets.Lege02;
import math.transform.jwave.handlers.wavelets.Lege06;
import filters.utils.Filter;
import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.dm.SGTMetaData;
import gov.noaa.pmel.sgt.dm.SimpleLine;
import graphwindow.graphlayouts.LinePlotLayout;

@graphtype(	name = "Test !!",
			layout = LinePlotLayout.class )


public class TestPlot extends Plot {
	public TestPlot(IPlot plot) {
		super(plot);
	}
	
	@Override
	protected SGTData setMetaData(SGTData data) {
	    ((SimpleLine)data).setXMetaData(new SGTMetaData("Time", "secondes", false, false));
	    ((SimpleLine)data).setYMetaData(new SGTMetaData("Potential", "µV", false, false));
	    return data;
	}

	@Override
	protected SGTData processSignal(double[][] data) {
		int scales = 3;
	    Transform t = new Transform(new FastWaveletTransform(new Lege06(), scales));
	    
	    data[Y] = t.forward(data[Y]);
	    
	    data[Y] = iterateScales(data[Y], 1, scales, getStdDev(data[Y]));
	    
	    data[Y] = t.reverse(data[Y]);
	    
	    Logger.log("dyl " + data[Y].length);
	    Logger.log("dxl " + data[X].length);
	    
	    //data[Y] = Arrays.copyOf(data[Y], (int) Math.floor(data[Y].length/2));
	   
	    data[X] = Filter.oneOfTwo(data[X]);
	    
	    Logger.log("dyl " + data[Y].length);
	    Logger.log("dxl " + data[X].length);
	    		
	    return new SimpleLine(data[X], data[Y], null);
	}
	private double treshold = 0.5;
	
	
	private double[] iterateScales(double[] data, int currentLevel, int nLevels, double stdDev) {
		
		double[] firstHalf = Arrays.copyOfRange(data, 0, data.length / 2);
		double[] secondHalf = Arrays.copyOfRange(data, data.length / 2, data.length);
		
		firstHalf = processScale(firstHalf, stdDev);
		
		if(currentLevel < nLevels)
			secondHalf = iterateScales(secondHalf, currentLevel+1, nLevels, getStdDev(firstHalf));
		else
			secondHalf = processScale(secondHalf, stdDev);
		
    	double[] completeLevel = new double[data.length];
    	completeLevel = Arrays.copyOfRange(firstHalf, 0, firstHalf.length);
    	for(int i=firstHalf.length; i<completeLevel.length; i++) {
    		completeLevel[i] = secondHalf[i-firstHalf.length];
    	}
    	return completeLevel;
	}
	
	private double[] processScale(double[] data, double stdDev) {
		return threeshold(data, treshold * stdDev);
	}

	private double[] threeshold(double[] ds, double k) {
		for(int i=0; i<ds.length; i++)
			if(Math.abs(ds[i]) <= k) ds[i] = 0;
		return ds;
	}
	
	private double getStdDev(double[] data) { 
		return Math.sqrt(getVariance(data)); 
	}

	private double getVariance(double[] ds) {
		double mean = getAverage(ds);
		double v = 0;
		double x; 
		for(double i : ds) {
			x = mean - i;
			v += x*x;
		}
		return v/ds.length;
	}
	
	private double getAverage(double[] ds) {
		double t = 0;
		for(double i : ds) t+=i;
		return t / ds.length;
	}

	@Override
	public void setDataId(SGTData data, String id) {
		((SimpleLine)data).setId(id);
	}

}
