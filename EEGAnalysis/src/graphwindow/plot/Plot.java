package graphwindow.plot;

import java.io.File;

import main.MainWindow;
import main.utils.Logger;
import filters.CutOff;
import filters.utils.AmplitudeRange;
import filters.utils.FrequencyRange;
import filters.utils.Range;
import gov.noaa.pmel.sgt.dm.SGTData;
import graphwindow.data.DataFileReader;
import graphwindow.data.DataInfos;
import graphwindow.data.WaveClass;

public abstract class Plot implements IPlot {
	public static class TimeFrame {
		public int getFrom() {
			return MainWindow.getPrefs().getInt(MainWindow.PREF_TIME_FROM, 30);
		}
		public void setFrom(int from) {
			MainWindow.getPrefs().putInt(MainWindow.PREF_TIME_FROM, from);
		}
		public int getTo() {
			return getFrom() + MainWindow.getPrefs().getInt(MainWindow.PREF_TIME_DURATION, 60);
		}
		public void setTo(int to) {
			MainWindow.getPrefs().putInt(MainWindow.PREF_TIME_DURATION, to - getFrom());
		}
	}

	public TimeFrame time = new TimeFrame();
	public FrequencyRange freqRange = new FrequencyRange(1,15);
	public AmplitudeRange amplitudeCutoff = new AmplitudeRange();
	public DataInfos dataInfo = new DataInfos();

	protected final int X = 0;
	protected final int Y = 1;
	protected final int Z = 2;
	public WaveClass waveClass;

	public static String name;
	public static String getCName () {
		return name;
	}

	private SGTData data = null;
	protected double[][] rawData = null; 
	
	public Plot(int channel, File file) {
		dataInfo.file = file;
		dataInfo.channel = channel;
	}

	public Plot(IPlot src) {
		dataInfo.file = src.getInfos().file;
		dataInfo.channel = src.getInfos().channel;
		setWaveClass(src.getWaveClass());
	}
	
	/**
	 * Reads the EEG data from {@link #dataFile}
	 * @return the SGTData data used by the graph layouts
	 */
	public void update() {
		boolean test = false;
		SGTData data;
		Logger.log("Test mode: " + (test ? true : false));
		rawData = new DataFileReader().dataReader.read(dataInfo, time);

		if(waveClass != WaveClass.NONE) {
			Logger.log("showing frequency range [" + freqRange.lower + " ; " + freqRange.higher + "]");
			rawData = CutOff.frequencyRange(rawData, freqRange, dataInfo.fs);
		}

		// Test sin waves
		if(test) setRawData(filters.utils.synthetizer.Sinusoidal.merge(
					filters.utils.synthetizer.Sinusoidal.generate(5, dataInfo.fs, time.getTo() - time.getFrom(), 0.10, 1),
					filters.utils.synthetizer.Sinusoidal.generate(15, dataInfo.fs, time.getTo() - time.getFrom(), 0.05, 1)
				));

		data = processSignal();
		setDataId(data, getDataId());
	    data = setMetaData(data);
		this.setData(data);
	}

	protected abstract SGTData setMetaData(SGTData data);

	protected abstract SGTData processSignal();

	/**
	 * Constructs the ID string for a data
	 * @return a string representing the data and its settings
	 */
	private String getDataId() {
		return dataInfo.file.getPath()
				+ "_" + dataInfo.channel + "_" + amplitudeCutoff.lower + "_" + amplitudeCutoff.higher
				+ "_" + time.getFrom() + "_" + time.getTo()
				+ "_" + freqRange.higher + "_" + freqRange.lower;
	}
	
	@Override
	public boolean areChannelsAveraged() {
		return dataInfo.areChannelsAveraged;
	}

	@Override
	public void setChannelsAveraged(boolean areChannelsAveraged) {
		if(areChannelsAveraged() == areChannelsAveraged) return;
		dataInfo.areChannelsAveraged = areChannelsAveraged;
		update();
	}

	@Override
	public SGTData getData() {
		return data;
	}

	@Override
	public DataInfos getInfos() {
		return dataInfo;
	}

	@Override
	public TimeFrame getTime() {
		return time;
	}

	@Override
	public WaveClass getWaveClass() {
		return waveClass;
	}

	/**
	 * Register the various settings related to the selected {@link WavesClasses wave class}
	 * @param wc the wave class
	 * @see {@link WavesClasses}
	 * @see {@link #freqRange}
	 * @see {@link #HighCutOff}
	 * @see {@link #LowCutOff}
	 */
	@Override
	public void setWaveClass(WaveClass wc) {
		waveClass = wc;
		//TODO: verify that wc' range object is not overwritten
		amplitudeCutoff = wc.getAmplitudeRange();
		freqRange = wc.getFrequencyRange();
	}

	protected double[][] getRawData() {
		return rawData;
	}

	protected void setRawData(double[][] rawData) {
		this.rawData = rawData;
	}

	public void setData(SGTData data) {
		this.data = data;
	}
	
	@Override
	public Range<Double> getXRange() {
		return new Range<Double>(
				(Double)data.getXRange().getStart().getObjectValue(),
				(Double)data.getXRange().getEnd().getObjectValue()
				);
	}

	@Override
	public Range<Double> getYRange() {
		return new Range<Double>(
				(Double)data.getYRange().getStart().getObjectValue(),
				(Double)data.getYRange().getEnd().getObjectValue()
				);
	}
}
