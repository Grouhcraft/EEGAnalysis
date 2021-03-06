package plotframes.graphlayouts;

import gov.noaa.pmel.sgt.GridAttribute;
import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.swing.JPlotLayout;

import java.awt.Rectangle;
import java.util.Arrays;

public class GridPlotLayout extends JPlotLayout implements IGraphLayout {

	/**
	 *
	 */
	private static final long serialVersionUID = -3782057629938737061L;

	public GridPlotLayout(String id) {
		super(true, false, false, false, id, null, true);
	}

	@Override
	public void beginOperations() {
		super.setBatch(true);
	}

	@Override
	public void endOperations() {
		super.setBatch(false);
	}

	@Override
	public void addData(Object data, Object lineAttr) {
		super.addData((SGTData)data, (GridAttribute)lineAttr);
	}

	@Override
	public void addData(Object data) {
		super.addData((SGTData)data);
	}

	@Override
	public void setTitle(String title) {
		super.setTitles(title, null, null);
	}

	@Override
	public void setTitles(String[] titles) {
		String[] t = Arrays.copyOfRange(titles, 0, 3);
		super.setTitles(t[0], t[1], t[2]);
	}

	@Override
	public void clear() {
		resetZoom();
		super.clear();
	}

	@Override
	public boolean supportZooming() {
		return true;
	}

	@Override
	public Object getZoom() {
		//beginOperations();
		resetZoom();
		Rectangle r = (Rectangle) getZoomBounds().getBounds().clone();
		setZoom(r);
		//endOperations();
		return r;
	}

	@Override
	public void setZoom(Object object) {
		resetZoom();
		getZoomBounds().setBounds((Rectangle)object);
	}
}
