package graphwindow.graphlayouts;

import java.util.Arrays;

import gov.noaa.pmel.sgt.LineAttribute;
import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.swing.JPlotLayout;
import gov.noaa.pmel.util.Domain;

public class LinePlotLayout extends JPlotLayout implements IGraphLayout {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8996373082285749233L;

	public LinePlotLayout(String id) {
		super(false, false, false, false, id, null, false);
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
		super.addData((SGTData)data, (LineAttribute)lineAttr);
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
		return getZoomBoundsU();
	}

	@Override
	public void setZoom(Object object) {
		try {
			super.setRangeNoVeto((Domain)object);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
