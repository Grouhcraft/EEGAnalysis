package plotframes.plots.implementations;

import filters.Filter;
import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.dm.SGTMetaData;
import gov.noaa.pmel.sgt.dm.SimpleLine;

import java.util.Arrays;

import math.transform.jwave.Transform;
import math.transform.jwave.handlers.FastWaveletTransform;
import math.transform.jwave.handlers.wavelets.WaveletInterface;
import plotframes.data.EEGSource;
import plotframes.graphlayouts.MultiplePlotLayout;
import plotframes.plots.IPlot;
import plotframes.plots.Plot;
import plotframes.plots.annotations.GraphSetting;
import plotframes.plots.annotations.UserPlot;
import utils.types.Range;

@UserPlot(	name = "Wavelet",
			layout = MultiplePlotLayout.class)

public class WaveletPlot extends Plot {

	@GraphSetting("Etapes")
	public int scales = 8;

	public enum Wavelet {
		Lege02, Lege04, Lege06,
		Haar02,
		Haar02Orthogonal,
		Coif06,
		Daub02, Daub03, Daub04
	}

	@GraphSetting("Wavelet function")
	public Wavelet wavelet = Wavelet.Daub02;

	public WaveletPlot(int channel, EEGSource dataSrc) {
		super(channel, dataSrc);
	}

	public WaveletPlot(IPlot plot) {
		super(plot);
	}

	@Override
	public void setDataId(Object data, String id) {
		int i=0;
		for(SimpleLine aData : (SimpleLine[])data) {
			aData.setId(id + "_" + i);
			i++;
		}
	}

	@Override
	protected Object setMetaData(Object data) {
		SGTMetaData x = new SGTMetaData("Time", "secondes", false, false);
		SGTMetaData y = new SGTMetaData("Potential", "µV", false, false);
		for(SimpleLine aData : (SimpleLine[])data) {
			aData.setXMetaData(x);
			aData.setYMetaData(y);
		}
	    return data;
	}

	@Override
	protected SGTData[] processSignal() {
		double[][] data = getRawData();
		try {
			String pkg = "math.transform.jwave.handlers.wavelets.";
			WaveletInterface wl = (WaveletInterface) Class.forName(pkg + wavelet.name()).newInstance();
		    Transform t = new Transform(new FastWaveletTransform(wl, scales));

		    SimpleLine[] sgtDatas = new SimpleLine[scales];
			data[X] = shiftTimeValues(data[X], time.getFrom());
		    data[Y] = t.forward(data[Y]);
		    iterateScales(data, 1, scales, sgtDatas);
		    return sgtDatas;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	protected void iterateScales(double[][] data, int currentLevel, int nLevels, SimpleLine[] sgtDatas) {
		double[] firstHalf = Arrays.copyOfRange(data[Y], 0, data[Y].length / 2);
		double[] secondHalf = Arrays.copyOfRange(data[Y], firstHalf.length, firstHalf.length*2);
		double[] xHalf = Filter.oneOfTwo(data[X]);

    	sgtDatas[currentLevel-1] = new SimpleLine(xHalf, secondHalf, null);

		if(currentLevel < nLevels)	iterateScales(new double[][]{ xHalf, firstHalf }, currentLevel+1, nLevels, sgtDatas);
		else sgtDatas[currentLevel-1] = new SimpleLine(xHalf, firstHalf, null);
	}


	@Override
	public Range<Double> getXRange() {
		double 	minx = Double.MAX_VALUE,
				maxx = Double.MIN_VALUE;
		for(SimpleLine aData : (SimpleLine[])getData()) {
			double min = (Double)aData.getXRange().getStart().getObjectValue();
			double max = (Double)aData.getXRange().getEnd().getObjectValue();
			if(min < minx) minx = min;
			if(max > maxx) maxx = max;
		}
		return new Range<Double>(minx, maxx);
	}

	@Override
	public Range<Double> getYRange() {
		double 	miny = Double.MAX_VALUE,
				maxy = Double.MIN_VALUE;
		for(SimpleLine aData : (SimpleLine[])getData()) {
			double min = (Double)aData.getYRange().getStart().getObjectValue();
			double max = (Double)aData.getYRange().getEnd().getObjectValue();
			if(min < miny) miny = min;
			if(max > maxy) maxy = max;
		}
		return new Range<Double>(miny, maxy);
	}

}
