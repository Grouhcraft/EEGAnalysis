package plot;
//test
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
	
	private WaveClass(String name){
		this.name = name;
	}
	
	protected final int MAX_AMPLITUDE = 2000;
	protected final int MAX_FREQUENCY = 2000;
	protected String name;
	
	public String getName() { 
		return name;
	}
	
	public static WaveClass get(String wcName) {
		wcName = wcName.toUpperCase();
		if(wcName.equals("ALPHA")) {
			return ALPHA;
		} else if (wcName.equals("BETA")) {
			return BETA;
		} else if (wcName.equals("THETA")) {
			return THETA;
		} else if (wcName.equals("DELTA")) {
			return DELTA;
		} else if (wcName.equals("GAMMA")) {
			return GAMMA;
		} else if (wcName.equals("MU")) {
			return MU;
		} else {
			return null;
		}
	}
	
	/**
	 * @return The upper limit of the frequency range of the wave class (in Hz)
	 */
	public abstract int getUpperFreq();
	
	/**
	 * @return The lower limit of the frequency range of the wave class (in Hz)
	 */
	public abstract int getLowerFreq();
	
	/**
	 * @return The upper limit of the amplitude range of the wave class (in µV)
	 */
	public abstract int getUpperAmpl();
	
	/**
	 * @return The lower limit of the amplitude range of the wave class (in µV)
	 */
	public abstract int getLowerAmpl();

	/**
	 * Active thinking, focus on outside world or resolving problems
	 */
	public static final WaveClass BETA = new WaveClass("BETA") {
		@Override
		public int getUpperFreq() { return 30; }
		@Override
		public int getLowerFreq() { return 13; }
		@Override
		public int getUpperAmpl() { return 30; }
		@Override
		public int getLowerAmpl() { return 5; }
	};
	
	/**
	 * Awareness, inattention
	 * Best catch at occipital and frontal cortex
	 */
	public static final WaveClass ALPHA = new WaveClass("ALPHA") {
		@Override
		public int getUpperFreq() { return 13; }
		@Override
		public int getLowerFreq() { return 8; }
		@Override
		public int getUpperAmpl() { return 50; }
		@Override
		public int getLowerAmpl() { return 30; }
	};
	
	/**
	 * Emotional stress, frustration or disappointment
	 */
	public static final WaveClass THETA = new WaveClass("THETA") {
		@Override
		public int getUpperFreq() { return 7; }
		@Override
		public int getLowerFreq() { return 4; }
		@Override
		public int getUpperAmpl() { return MAX_AMPLITUDE; }
		@Override
		public int getLowerAmpl() { return 20; }
	};
	
	/**
	 * Deep sleep, or physical defects in brain if awake 
	 */
	public static final WaveClass DELTA = new WaveClass("DELTA") {
		@Override
		public int getUpperFreq() { return 4; }
		@Override
		public int getLowerFreq() { return 0; }
		@Override
		public int getUpperAmpl() { return MAX_AMPLITUDE; }
		@Override
		public int getLowerAmpl() { return 0; }
	};
	
	/**
	 * Mechanism of consciousness
	 */
	public static final WaveClass GAMMA = new WaveClass("GAMMA") {
		@Override
		public int getUpperFreq() { return MAX_FREQUENCY; }
		@Override
		public int getLowerFreq() { return 35; }
		@Override
		public int getUpperAmpl() { return MAX_AMPLITUDE; }
		@Override
		public int getLowerAmpl() { return 0; }
	};
	
	/**
	 * Motor activities. Same frequencies as {@link #ALPHA} but recorded over motor cortex
	 */
	public static final WaveClass MU = new WaveClass("MU") {
		@Override
		public int getUpperFreq() { return 12; }
		@Override
		public int getLowerFreq() { return 8; }
		@Override
		public int getUpperAmpl() { return MAX_AMPLITUDE; }
		@Override
		public int getLowerAmpl() { return 0; }
	};
}
