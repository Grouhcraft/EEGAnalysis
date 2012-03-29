package graphwindow.plot;

import java.io.File;

import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.dm.SGTMetaData;
import gov.noaa.pmel.sgt.dm.SimpleGrid;
import graphwindow.graphlayouts.GridPlotLayout;
import graphwindow.graphlayouts.IGraphLayout;

public class ShortTimeFourierPlot extends Plot {


	public ShortTimeFourierPlot(IPlot src) {
		super(src);
	}

	public ShortTimeFourierPlot(int channel, File file) {
		super(channel, file);
	}
	
	@Override
	public Class<? extends IGraphLayout> getGraphLayoutType() {
		return GridPlotLayout.class;
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
		double[] x = new double[]{0,1.5,2,8};
		double[] y = new double[]{0,12,2.33,23};
		double[] z = new double[]{1,2,3,4,8,2.4,7.5,4,2.2,2,5,4,11,2,6,4};
		return new SimpleGrid(z, x, y, "test");
	}

	@Override
	public void setDataId(SGTData data, String id) {
		((SimpleGrid)data).setId(id);
	}
}
