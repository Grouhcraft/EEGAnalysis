package windowsFunctions;

public class HannWindow extends Window {

	public HannWindow(double[] data) {
		super(data);
	}

	public HannWindow() {
		super();
	}
	
	@Override
	public double[] get(double windowSize) {
		// https://en.wikipedia.org/wiki/Hanning_window
		double[] window = new double[(int) windowSize];
		int start = (int) (((double)getData().length - windowSize)/2d);
		for(int i=start; i<windowSize + start; i++) {  
			double multiplier = 0.5d * ( 1d - Math.cos((2.0d * Math.PI * (double)(i-start)) / (windowSize-1d)) );
			window[i-start] = getData()[i] * multiplier;
		}
		window[0] = getData()[start] * 0.5d * ( 1d + Math.cos(0 / (windowSize-1d)) );
		return window;
	}

	@Override
	public int getRecommandedOverlappingSize() {
		if(getData().length % 2 == 0)
			return (int) (((double)getData().length) / 2d);
		else
			return (int) ((((double)getData().length) - 1d) / 2d);
	}
}
