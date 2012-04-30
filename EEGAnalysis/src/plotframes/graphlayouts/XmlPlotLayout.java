package plotframes.graphlayouts;

import gov.noaa.pmel.sgt.Attribute;
import gov.noaa.pmel.sgt.JPane;
import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.dm.SimpleGrid;
import gov.noaa.pmel.sgt.dm.SimpleLine;
import gov.noaa.pmel.sgt.swing.JPlotLayout;
import gov.noaa.pmel.util.Domain;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.util.Arrays;

import javax.swing.JLabel;

public class XmlPlotLayout extends JPane implements IGraphLayout {

	/**
	 *
	 */
	private static final long serialVersionUID = -7675142599145170372L;
	private String id = null;
	private JPlotLayout jplotlayout = null;
	private boolean isGrid = false;
	private JLabel noPlotMsg = new JLabel("Please select an EEP plot file with the above menu.");


	public XmlPlotLayout (String id) {
		this.id = id;
		setLayout(new BorderLayout());

		noPlotMsg.setFont(noPlotMsg.getFont().deriveFont(Font.BOLD));
		add(noPlotMsg);
	}

	@Override
	public void beginOperations() {
		if(jplotlayout != null)
			jplotlayout.setBatch(true);
	}

	@Override
	public void endOperations() {
		if(jplotlayout != null)
			jplotlayout.setBatch(false);
	}

	protected Container getJPlotLayoutContainer() {
		return this;
	}

	private void createJPlotLayout(boolean asGrid) {
		if(jplotlayout != null) remove(jplotlayout);
		jplotlayout = new JPlotLayout(asGrid, false, false, false, id, null, true);
		noPlotMsg.setVisible(false);
		add(jplotlayout);
	}

	private void checkLayoutIsCorrect(Object data) {
		if(data instanceof SimpleLine) {
			if(jplotlayout == null || isGrid) {
				createJPlotLayout(false);
			}
		} else if(data instanceof SimpleGrid) {
			if(jplotlayout == null || !isGrid) {
				createJPlotLayout(true);
			}
		}
	}

	@Override
	public void addData(Object data, Object attrs) {
		checkLayoutIsCorrect(data);
		jplotlayout.addData((SGTData)data, (Attribute)attrs);
	}

	@Override
	public void addData(Object data) {
		checkLayoutIsCorrect(data);
		jplotlayout.addData((SGTData)data);
	}

	@Override
	public void clear() {
		if(jplotlayout != null) {
			jplotlayout.resetZoom();
			jplotlayout.clear();
		}
	}

	@Override
	public void setTitle(String title) {
		jplotlayout.setTitles(title, null, null);
	}

	@Override
	public void setTitles(String[] titles) {
		if(jplotlayout != null) {
			String[] t = Arrays.copyOfRange(titles, 0, 3);
			jplotlayout.setTitles(t[0], t[1], t[2]);
		}
	}

	@Override
	public boolean supportZooming() {
		return true;
	}

	@Override
	public Object getZoom() {
		if(jplotlayout != null)
			return jplotlayout.getRange();
		else
			return new Domain();
	}

	@Override
	public void setZoom(Object object) {
		try {
			if(jplotlayout != null)
				jplotlayout.setRangeNoVeto((Domain)object);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateLayout() {
		if(jplotlayout != null) {
			jplotlayout.resetZoom();
			jplotlayout.revalidate();
		}
	}

}
