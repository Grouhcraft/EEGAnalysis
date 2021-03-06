package plotframes.graphlayouts;

public interface IGraphLayout {
	void beginOperations();
	void endOperations();
	void addData(Object data, Object attrs);
	void addData(Object data);
	void clear();
	void setTitle(String title);
	void setTitles(String[] titles);
	boolean supportZooming();
	Object getZoom();
	void setZoom(Object object);
}
