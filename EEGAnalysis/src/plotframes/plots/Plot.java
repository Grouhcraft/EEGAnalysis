package plotframes.plots;

import plotframes.data.DataInfos;
import plotframes.data.EEGSource;
import plotframes.data.WaveClass;
import plotframes.plots.annotations.GraphSetting;
import utils.Logger;
import utils.types.AmplitudeRange;
import utils.types.FrequencyRange;
import utils.types.TimeFrame;
import filters.generators.GaussianWhiteNoiseGenerator;
import filters.generators.SineWaveGenerator;
import filters.implementations.BandPassFilter;

public abstract class Plot implements IPlot {

	public TimeFrame time = new TimeFrame();
	public FrequencyRange freqRange = new FrequencyRange(1,15);
	public AmplitudeRange amplitudeCutoff = new AmplitudeRange();
	public DataInfos dataInfo = new DataInfos();
	public EEGSource dataSource	= null;
	public WaveClass waveClass;

	@Override
	public void setDataSource(EEGSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public EEGSource getDataSource() {
		return dataSource;
	}

	protected final int X = 0;
	protected final int Y = 1;
	protected final int Z = 2;
	protected Object data = null;
	protected double[][] rawData = null;

	public Plot(int channel, EEGSource dataSrc) {
		dataSource = dataSrc;
		dataInfo.channel = channel;
	}

	public Plot(IPlot src) {
		dataSource = src.getDataSource();
		dataInfo.channel = src.getInfos().channel;
		setWaveClass(src.getWaveClass());
	}

	/**
	 * Reads the EEG data from {@link #dataSource}
	 * @return the SGTData data used by the graph layouts
	 */
	@Override
	public void update() {
		Object data;
		readData();

		if(waveClass != WaveClass.NONE) {
			Logger.log("showing frequency range [" + freqRange.lower + " ; " + freqRange.higher + "]");
			setRawData(BandPassFilter.frequencyRange(getRawData(), freqRange, dataInfo.fs));
		}

		data = processSignal();
		setDataId(data, getDataId());
	    data = setMetaData(data);
	    setData(data);
	}

	@GraphSetting("Test mode ?")
	public boolean testMode = false;

	@GraphSetting("Noise amount (Test mode)")
	public int testModeAmountOfNoise = 0;

	@GraphSetting("Phase (test mode)")
	public double testModePhase = 1.;

	@GraphSetting("Freq (test mode)")
	public double testModeFreq = 5.;

	/**
	 * Reads the raw data from the source input (eg. data file)
	 * and registers it with {@link #setRawData(double[][])}.
	 * The data can then be accessed with {@link #getRawData()}
	 */
	protected void readData() {
		Logger.log("Test mode: " + (testMode ? true : false));
		if(testMode) setRawData(GaussianWhiteNoiseGenerator.addNoise(SineWaveGenerator.generate(testModeFreq, dataInfo.fs, time.getTo() - time.getFrom(), 0.10, testModePhase),testModeAmountOfNoise));
		else setRawData(dataSource.read(dataInfo, time));
	}

	/**
	 * Sets the metadata in the data object if needed.
	 * Meta datas can be anything, depending on the plot.
	 * For instance, the labels.
	 * @param data
	 * @return the data Object now containing the meta data
	 */
	protected abstract Object setMetaData(Object data);

	/**
	 * Applies the needed treatments to the raw signal
	 * and return it in a data Object form, corresponding
	 * to whatever the plot component can handle.<br/>
	 * Note: The raw data can be accessed by {@link #getRawData()}
	 * @return the plottable Object data
	 */
	protected abstract Object processSignal();

	/**
	 * Constructs the ID string for a data
	 * @return a string representing the data and its settings
	 */
	private String getDataId() {
		// FIXME use something more like:
		// dataInfo.hashCode() + data.hashCode();
		return dataSource.getName()
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
	public Object getData() {
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
	 * Tool method usable by time-domain plot implementations to shift the
	 * x-axis by <i>n</i> seconds.<br/>It's just a<br/>
	 * <code>for each x : x = x + n</code>
	 * @param timeArr values to shift
	 * @param seconds number of secondes to shift. Usualy corresponds to the starting point in data
	 * @return shiffted values array
	 */
	protected double[] shiftTimeValues(double[] timeArr, double seconds) {
		double[] newArr = new double[timeArr.length];
		for(int i=0; i<timeArr.length; i++)
			newArr[i] = timeArr[i] + seconds;
		return newArr;
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
		amplitudeCutoff = wc.getAmplitudeRange();
		freqRange = wc.getFrequencyRange();
	}

	/**
	 * Returns the raw data
	 */
	protected double[][] getRawData() {
		return rawData;
	}

	/**
	 * Registers the raw data
	 */
	protected void setRawData(double[][] rawData) {
		this.rawData = rawData;
	}

	/**
	 * Registers the data in its plot data object form.<br/>
	 * The type of the Object depends on the plot layout component.
	 */
	public void setData(Object data) {
		this.data = data;
	}
}
