package filters.utils;

public class AmplitudeRange implements IRange<Integer> {

	public int lower = 0;
	public int higher = 0;

	public AmplitudeRange(int lower, int higher) {
		this.lower = lower;
		this.higher = higher;
	}

	public AmplitudeRange(){}

	@Override
	public Integer getLower() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getHigher() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLower(Integer lower) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setHigher(Integer higher) {
		// TODO Auto-generated method stub

	}

}
