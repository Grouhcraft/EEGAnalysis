package main;

public interface WavesClasses {

	public static final WaveClass BETA = new WaveClass() {
		@Override
		public int getUpperFreq() { return 30; }
		@Override
		public int getLowerFreq() { return 13; }
		@Override
		public int getUpperAmpl() { return 30; }
		@Override
		public int getLowerAmpl() { return 5; }
	};
	
	public static final WaveClass ALPHA = new WaveClass() {
		@Override
		public int getUpperFreq() { return 13; }
		@Override
		public int getLowerFreq() { return 8; }
		@Override
		public int getUpperAmpl() { return 50; }
		@Override
		public int getLowerAmpl() { return 30; }
	};
	
	public static final WaveClass THETA = new WaveClass() {
		@Override
		public int getUpperFreq() { return 7; }
		@Override
		public int getLowerFreq() { return 4; }
		@Override
		public int getUpperAmpl() { return MAX_AMPLITUDE; }
		@Override
		public int getLowerAmpl() { return 20; }
	};
	
	public static final WaveClass DELTA = new WaveClass() {
		@Override
		public int getUpperFreq() { return 4; }
		@Override
		public int getLowerFreq() { return 0; }
		@Override
		public int getUpperAmpl() { return MAX_AMPLITUDE; }
		@Override
		public int getLowerAmpl() { return 0; }
	};
	
	public static final WaveClass GAMMA = new WaveClass() {
		@Override
		public int getUpperFreq() { return MAX_FREQUENCY; }
		@Override
		public int getLowerFreq() { return 35; }
		@Override
		public int getUpperAmpl() { return MAX_AMPLITUDE; }
		@Override
		public int getLowerAmpl() { return 0; }
	};
	
	public static final WaveClass MU = new WaveClass() {
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
