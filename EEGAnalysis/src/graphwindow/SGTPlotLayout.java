package graphwindow;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;

import main.utils.Logger;

import gov.noaa.pmel.sgt.swing.JPlotLayout;

public class SGTPlotLayout extends JPlotLayout implements IGraphLayout {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8996373082285749233L;

	SGTPlotLayout(boolean isGrid, boolean isPoints, boolean isXTime, boolean isYTime, String id, Image img, boolean is_key_pane) {
		super(isGrid, isPoints, isXTime, isYTime, id, img, is_key_pane);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Logger.log("parent name:" + getParent().getClass().getName());
		Logger.log("self/parent/gdParent x = " 
				+ getWidth() + "/" 
				+ getParent().getWidth() + "/" 
				+ getParent().getParent().getWidth());
		
		Logger.log("Child comp:");
		for(Component c : getComponents()) {
			Logger.log("	[" + c.getWidth() + "]" + c.getClass().getName());
		}
		super.paintComponent(g);
	}
}
