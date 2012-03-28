package filters.utils;

public class FrequencyRange implements IRange<Double> {
	public double lower = 0.0;
	public double higher = 0.0;
	
	public FrequencyRange() {}
	
	public FrequencyRange(double lower, double higher) {
		this.lower = lower;
		this.higher = higher;
	}

	@Override
	public Double getLower() {
		return lower;
	}

	@Override
	public Double getHigher() {
		return higher;
	}

	@Override
	public void setLower(Double lower) {
		this.lower = lower;
	}

	@Override
	public void setHigher(Double higher) {
		this.higher = higher;
	}

}
