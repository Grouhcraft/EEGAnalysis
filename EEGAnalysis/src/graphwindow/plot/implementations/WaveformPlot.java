package graphwindow.plot.implementations;

import filters.utils.Range;
import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.dm.SGTMetaData;
import gov.noaa.pmel.sgt.dm.SimpleLine;
import graphwindow.graphlayouts.LinePlotLayout;
import graphwindow.plot.GraphSetting;
import graphwindow.plot.IPlot;
import graphwindow.plot.Plot;
import graphwindow.plot.graphtype;

import java.io.File;
import java.text.DecimalFormat;


@graphtype(	name = "Regular waveform",
			layout = LinePlotLayout.class )

public class WaveformPlot extends Plot {
	@GraphSetting("Clean X - Axis values")
	public boolean cleanXAxis = true;
	
	public WaveformPlot(int channel, File file) {
		super(channel, file);
	}

	public WaveformPlot(IPlot plot) {
		super(plot);
	}

	@Override
	protected Object setMetaData(Object data) {
	    ((SimpleLine)data).setXMetaData(new SGTMetaData("Time", "secondes", false, false));
	    ((SimpleLine)data).setYMetaData(new SGTMetaData("Potential", "�V", false, false));
	    return data;
	}

	@Override
	protected SGTData processSignal() {
		double[][] data = getRawData();
		if(cleanXAxis) data = cleanXAxis(data);
		data[X] = shiftTimeValues(data[X], time.getFrom());
	    return new SimpleLine(data[X], data[Y], null);
	}

	protected double[][] cleanXAxis(double[][] data) {
		double newData[][] = data.clone();
		DecimalFormat f = new DecimalFormat("#.##");
		for(int i=0; i<data[X].length; i++) {
			newData[X][i] = Double.valueOf(f.format(newData[X][i]));
		}
		return newData;
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
