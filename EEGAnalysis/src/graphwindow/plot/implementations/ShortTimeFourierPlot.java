package graphwindow.plot.implementations;

import filters.ShortTimeFourier;
import filters.utils.Range;
import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.dm.SGTMetaData;
import gov.noaa.pmel.sgt.dm.SimpleGrid;
import graphwindow.graphlayouts.GridPlotLayout;
import graphwindow.plot.GraphSetting;
import graphwindow.plot.IPlot;
import graphwindow.plot.Plot;
import graphwindow.plot.graphtype;

import java.io.File;

@graphtype(	name = "Short Time Fourier",
			layout = GridPlotLayout.class )

public class ShortTimeFourierPlot extends Plot {
	@GraphSetting(value="Resolution (factor)", limits={0.1,10})
	public double resolutionFactor = 1;

	public ShortTimeFourierPlot(IPlot src) {
		super(src);
	}

	public ShortTimeFourierPlot(int channel, File file) {
		super(channel, file);
	}

	@Override
	protected Object setMetaData(Object data) {
		((SimpleGrid)data).setXMetaData(new SGTMetaData("X", "a", false, false));
	    ((SimpleGrid)data).setYMetaData(new SGTMetaData("Y", "b", false, false));
	    ((SimpleGrid)data).setZMetaData(new SGTMetaData("Z", "c", false, false));
	    return data;
	}

	@Override
	protected SGTData processSignal() {
		double[][] data = getRawData();
		data = ShortTimeFourier.compute(data, resolutionFactor, dataInfo.fs);
		data[X] = shiftTimeValues(data[X], time.getFrom());
		return new SimpleGrid(data[Z], data[X], data[Y], "test");
	}

	@Override
	public void setDataId(Object data, String id) {
		((SimpleGrid)data).setId(id);
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
