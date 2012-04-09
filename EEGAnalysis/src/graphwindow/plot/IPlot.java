package graphwindow.plot;

import filters.utils.Range;
import gov.noaa.pmel.sgt.dm.SGTData;
import graphwindow.data.DataInfos;
import graphwindow.data.WaveClass;
import graphwindow.plot.Plot.TimeFrame;

import java.util.ArrayList;


public interface IPlot {

	static ArrayList<Class<? extends IPlot>> graphTypes = new ArrayList<Class<? extends IPlot>>();

	WaveClass getWaveClass();

	DataInfos getInfos();

	/**
	 * Reloads and parse data from file
	 */
	void update();

	Object getData();

	void setDataId(Object data, String id);

	void setWaveClass(WaveClass waveClass);

	TimeFrame getTime();

	Range<Double> getXRange();
	
	Range<Double> getYRange();

	boolean areChannelsAveraged();

	void setChannelsAveraged(boolean areChannelsAveraged);
}
