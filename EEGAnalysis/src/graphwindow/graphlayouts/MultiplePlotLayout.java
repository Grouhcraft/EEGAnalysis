package graphwindow.graphlayouts;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;

import gov.noaa.pmel.sgt.LineAttribute;
import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.swing.JPlotLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import main.utils.Logger;

import org.junit.Assert;

public class MultiplePlotLayout extends JScrollPane implements IGraphLayout {
	
	private static final long serialVersionUID = -1624666263504930548L;
	private ArrayList<JPlotLayout> layouts = new ArrayList<JPlotLayout>();
	private String Id;
	private JPanel panel;
	
	public MultiplePlotLayout(String id) {
		this.Id = id;
		panel = new JPanel();
		setViewportView(panel);
	}
	
	@Override
	public void beginOperations() {
		for(JPlotLayout layout : layouts) 
			layout.setBatch(true);
	}

	@Override
	public void endOperations() {
		for(JPlotLayout layout : layouts) 
			layout.setBatch(false);
	}
	
	private void createLayouts(int n) {
		Logger.log("datas.length=>" + n);
		panel.setLayout(new GridLayout(n,1));
		for(int i=0; i<n; i++) {
			Logger.log("creating " + i + "th layout");
			JPanel innerPanel = new JPanel(new BorderLayout());
			JPlotLayout plotLayout = new JPlotLayout(false, false, false, false, this.Id + "_" + i, null, false);
			plotLayout.setMouseEventsEnabled(false);
			
			innerPanel.add(plotLayout);
			panel.add(innerPanel);
			
			layouts.add(plotLayout);
		}
	}

	@Override
	public void addData(Object data, Object attrs) {
		SGTData[] datas;
		LineAttribute[] lineAttrs;
		try {
			datas = (SGTData[]) data;
			lineAttrs = (LineAttribute[]) attrs;
			if(layouts.isEmpty()) {
				createLayouts(datas.length);
			} else {
				Assert.assertTrue(datas.length == layouts.size());
				Assert.assertTrue(lineAttrs.length == layouts.size());
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Wrong data or number of data");
		}
		for(int i=0; i<layouts.size(); i++) 
			layouts.get(i).addData(datas[i], lineAttrs[i]);
	}

	@Override
	public void addData(Object data) {
		SGTData[] datas;
		try {
			datas = (SGTData[]) data;
			if(layouts.isEmpty()) createLayouts(datas.length);
			else Assert.assertTrue(datas.length == layouts.size());		
		} catch (Exception e) {
			throw new IllegalArgumentException("Wrong data or number of data");
		}
		for(int i=0; i<layouts.size(); i++) 
			layouts.get(i).addData(datas[i]);
	}

	@Override
	public void clear() {
		for(JPlotLayout layout : layouts) {
			layout.resetZoom();
			layout.clear();
		}
	}

	@Override
	public void setTitle(String title) {
		int i=0; for(JPlotLayout layout : layouts) { 
			layout.setTitles(title + " scale#" + (i+1), null, null);
			i++;
		}
	}

	@Override
	public void setTitles(String[] titles) {
		String[] t = Arrays.copyOfRange(titles, 0, 3);
		int i=0; for(JPlotLayout layout : layouts) { 
			layout.setTitles(t[0] + " scale#" + (i+1), t[1] + " " + t[2], "");
			i++;
		}

	}

	@Override
	public boolean supportZooming() {
		return false;
	}

	@Override
	public Object getZoom() { return null; }

	@Override
	public void setZoom(Object object) {}

}
