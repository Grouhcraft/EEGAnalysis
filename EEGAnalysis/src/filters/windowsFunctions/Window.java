package filters.windowsFunctions;

public abstract class Window {
	private double[] data;
	public Window() {}
	public Window(double[] data) {
		this.setData(data);
	}
	public abstract double[] get(double windowSize);	
	public abstract int getRecommandedOverlappingSize(int segmentLength);
	public double[] getData() {
		return data;
	}
	public Window setData(double[] data) {
		this.data = data;
		return this;
	}
}
