package graphwindow.plot.implementations;

import java.util.Arrays;

import math.transform.jwave.Transform;
import math.transform.jwave.handlers.FastWaveletTransform;
import math.transform.jwave.handlers.wavelets.WaveletInterface;
import filters.utils.Filter;
import filters.utils.Range;
import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.dm.SGTMetaData;
import gov.noaa.pmel.sgt.dm.SimpleLine;
import graphwindow.graphlayouts.LinePlotLayout;
import graphwindow.plot.GraphSetting;
import graphwindow.plot.IPlot;
import graphwindow.plot.Plot;
import graphwindow.plot.graphtype;

@graphtype(	name = "Wavelet denoised",
			layout = LinePlotLayout.class )

public class WaveletDenoised extends Plot {
	
	@GraphSetting("Seuil")
	public double treshold = 3;
	
	@GraphSetting("Etapes")
	public int scales = 3;
	
	public enum Wavelet {
		Lege02, Lege04, Lege06,
		Haar02, 
		Haar02Orthogonal,
		Coif06, 
		Daub02, Daub03, Daub04
	} 
	
	@GraphSetting("Wavelet function")
	public Wavelet wavelet = Wavelet.Daub04;
	
	public WaveletDenoised(IPlot plot) {
		super(plot);
	}
	
	@Override
	protected Object setMetaData(Object data) {
	    ((SimpleLine)data).setXMetaData(new SGTMetaData("Time", "secondes", false, false));
	    ((SimpleLine)data).setYMetaData(new SGTMetaData("Potential", "µV", false, false));
	    return data;
	}
	
	@Override
	protected Object processSignal() {
		double[][] data = getRawData();
		try {
			String pkg = "math.transform.jwave.handlers.wavelets.";
			WaveletInterface wl = (WaveletInterface) Class.forName(pkg + wavelet.name()).newInstance();
		    Transform t = new Transform(new FastWaveletTransform(wl, scales));
		    
		    data[Y] = t.forward(data[Y]);
		    data[Y] = iterateScales(data[Y], 1, scales);
		    data[Y] = t.reverse(data[Y]);
		    data[X] = Filter.oneOfTwo(data[X]);
		    data[X] = shiftTimeValues(data[X], time.getFrom());
		    		
		    return new SimpleLine(data[X], data[Y], null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected double[] iterateScales(double[] data, int currentLevel, int nLevels) {
		double[] firstHalf = Arrays.copyOfRange(data, 0, data.length / 2);
		double[] secondHalf = Arrays.copyOfRange(data, firstHalf.length, firstHalf.length*2);
		
		secondHalf = processScale(secondHalf, Filter.stdDeviation(secondHalf));
		
		if(currentLevel < nLevels)
			firstHalf = iterateScales(firstHalf, currentLevel+1, nLevels);
		else
			firstHalf = processScale(firstHalf, Filter.stdDeviation(firstHalf));
		
    	double[] completeLevel = new double[data.length];
    	completeLevel = Arrays.copyOfRange(secondHalf, 0, secondHalf.length);
    	for(int i=secondHalf.length; i<completeLevel.length; i++) {
    		completeLevel[i] = firstHalf[i-secondHalf.length];
    	}
    	return completeLevel;
	}

	protected double[] processScale(double[] data, double stdDev) {
		return threeshold(data, treshold * stdDev);
	}

	protected double[] threeshold(double[] ds, double k) {
		for(int i=0; i<ds.length; i++)
			if(Math.abs(ds[i]) <= k) ds[i] = 0;
		return ds;
	}

	@Override
	public void setDataId(Object data, String id) {
		((SimpleLine)data).setId(id);
	}
	
	
	@Override
	public Range<Double> getXRange() {
		return new Range<Double>(
				(Double)((SGTData)getData()).getXRange().getStart().getObjectValue(),
				(Double)((SGTData)getData()).getXRange().getEnd().getObjectValue()
				);
	}

	@Override
	public Range<Double> getYRange() {
		return new Range<Double>(
				(Double)((SGTData)getData()).getYRange().getStart().getObjectValue(),
				(Double)((SGTData)getData()).getYRange().getEnd().getObjectValue()
				);
	}
}

