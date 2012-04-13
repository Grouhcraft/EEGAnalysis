package plotframes.data;

import utils.types.AmplitudeRange;
import utils.types.FrequencyRange;

/**
 * Define the various waves classes emitted by the brain
 * @author knoodrake
 * @see {@link #ALPHA}
 * @see {@link #BETA}
 * @see {@link #GAMMA}
 * @see {@link #THETA}
 * @see {@link #MU}
 * @see {@link #DELTA}
 */
public abstract class WaveClass {
	protected final int MAX_AMPLITUDE = 2000;
	protected final double MAX_FREQUENCY = 50;
	protected String name;

	private WaveClass(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static WaveClass get(String wcName) {
		wcName = wcName.toUpperCase();
		WaveClass wc = null;
		try {
			wc = (WaveClass) WaveClass.class.getDeclaredField(wcName).get(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wc;
	}

	protected AmplitudeRange amplitudeRange;
	protected FrequencyRange frequencyRange;

	/**
	 * @return The frequency range of the wave class (in Hz)
	 */
	public FrequencyRange getFrequencyRange() {
		return frequencyRange;
	}

	/**
	 * @return The amplitude range of the wave class (in µV)
	 */
	public AmplitudeRange getAmplitudeRange() {
		return amplitudeRange;
	}

	public static final WaveClass NONE = new WaveClass("NONE") {{
			frequencyRange = new FrequencyRange(0, MAX_FREQUENCY);
			amplitudeRange = new AmplitudeRange(0, MAX_AMPLITUDE);
	}};

	/**
	 * Active thinking, focus on outside world or resolving problems
	 */
	public static final WaveClass BETA = new WaveClass("BETA") {{
			frequencyRange = new FrequencyRange(13, 30);
			amplitudeRange = new AmplitudeRange(5, 30);
	}};

	/**
	 * Awareness, inattention
	 * Best catch at occipital and frontal cortex
	 */
	public static final WaveClass ALPHA = new WaveClass("ALPHA") {{
			frequencyRange = new FrequencyRange(8, 13);
			amplitudeRange = new AmplitudeRange(30, 50);
	}};

	/**
	 * Emotional stress, frustration or disappointment
	 */
	public static final WaveClass THETA = new WaveClass("THETA") {{
			frequencyRange = new FrequencyRange(4, 7);
			amplitudeRange = new AmplitudeRange(20, MAX_AMPLITUDE);
	}};

	/**
	 * Deep sleep, or physical defects in brain if awake
	 */
	public static final WaveClass DELTA = new WaveClass("DELTA") {{
			frequencyRange = new FrequencyRange(0, 4);
			amplitudeRange = new AmplitudeRange(0, MAX_AMPLITUDE);
	}};

	/**
	 * Mechanism of consciousness
	 */
	public static final WaveClass GAMMA = new WaveClass("GAMMA") {{
			frequencyRange = new FrequencyRange(35, MAX_FREQUENCY);
			amplitudeRange = new AmplitudeRange(0, MAX_AMPLITUDE);
	}};

	/**
	 * Motor activities. Same frequencies as {@link #ALPHA} but recorded over motor cortex
	 */
	public static final WaveClass MU = new WaveClass("MU") {{
			frequencyRange = new FrequencyRange(8, 12);
			amplitudeRange = new AmplitudeRange(0, MAX_AMPLITUDE);
	}};
}
