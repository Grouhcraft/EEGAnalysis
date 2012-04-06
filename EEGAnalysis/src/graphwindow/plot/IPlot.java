package graphwindow.plot;

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

	SGTData getData();

	void setDataId(SGTData data, String id);

	void setWaveClass(WaveClass waveClass);

	TimeFrame getTime();
}
