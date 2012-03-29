package graphwindow.graphlayouts;

import java.awt.Graphics;
import java.util.Arrays;

import gov.noaa.pmel.sgt.LineAttribute;
import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.swing.JPlotLayout;

public class LinePlotLayout extends JPlotLayout implements IGraphLayout {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8996373082285749233L;

	public LinePlotLayout(String id) {
		super(false, false, false, false, id, null, false);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		/*
		Logger.log("parent name:" + getParent().getClass().getName());
		Logger.log("self/parent/gdParent x = " 
				+ getWidth() + "/" 
				+ getParent().getWidth() + "/" 
				+ getParent().getParent().getWidth());
		
		Logger.log("Child comp:");
		for(Component c : getComponents()) {
			Logger.log("	[" + c.getWidth() + "]" + c.getClass().getName());
		}
		*/
		super.paintComponent(g);
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
}
