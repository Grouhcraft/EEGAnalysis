package graphwindow.plot.implementations;

import java.util.Arrays;

import main.utils.Logger;
import math.transform.jwave.Transform;
import math.transform.jwave.handlers.FastWaveletTransform;
import math.transform.jwave.handlers.wavelets.WaveletInterface;
import filters.utils.Filter;
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
	public double treshold = 0.5;
	
	@GraphSetting("Etapes")
	public int scales = 3;
	
	public enum Wavelet {
		Lege02, Lege04, Lege06,
		Haar02, Haar02Orthogonal,
		Coif06
	} 
	
	@GraphSetting("Wavelet function")
	public Wavelet wavelet = Wavelet.Lege06;
	
	public WaveletDenoised(IPlot plot) {
		super(plot);
	}
	
	@Override
	protected SGTData setMetaData(SGTData data) {
	    ((SimpleLine)data).setXMetaData(new SGTMetaData("Time", "secondes", false, false));
	    ((SimpleLine)data).setYMetaData(new SGTMetaData("Potential", "µV", false, false));
	    return data;
	}
	
	@Override
	protected SGTData processSignal() {
		double[][] data = getRawData();
		try {
			String pkg = "math.transform.jwave.handlers.wavelets.";
			WaveletInterface wl = (WaveletInterface) getClass().forName(pkg + wavelet.name()).newInstance();
		    Transform t = new Transform(new FastWaveletTransform(wl, scales));
		    
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
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
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

