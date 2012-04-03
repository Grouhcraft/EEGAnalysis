package graphwindow.plot;

import filters.ShortTimeFourier;
import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.dm.SGTMetaData;
import gov.noaa.pmel.sgt.dm.SimpleGrid;
import graphwindow.graphlayouts.GridPlotLayout;

import java.io.File;

@graphtype(	name = "Short Time Fourier",
			layout = GridPlotLayout.class )

public class ShortTimeFourierPlot extends Plot {
	public ShortTimeFourierPlot(IPlot src) {
		super(src);
	}

	public ShortTimeFourierPlot(int channel, File file) {
		super(channel, file);
	}

	@Override
	protected SGTData setMetaData(SGTData data) {
		((SimpleGrid)data).setXMetaData(new SGTMetaData("X", "a", false, false));
	    ((SimpleGrid)data).setYMetaData(new SGTMetaData("Y", "b", false, false));
	    ((SimpleGrid)data).setZMetaData(new SGTMetaData("Z", "c", false, false));
	    return data;
	}

	@Override
	protected SGTData processSignal(double[][] data) {
		data = ShortTimeFourier.compute(data);
		return new SimpleGrid(data[Z], data[X], data[Y], "test");
	}

	@Override
	public void setDataId(SGTData data, String id) {
		((SimpleGrid)data).setId(id);
	}
}
