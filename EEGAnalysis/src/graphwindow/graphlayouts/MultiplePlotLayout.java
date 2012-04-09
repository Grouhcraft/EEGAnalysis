package graphwindow.graphlayouts;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;

import gov.noaa.pmel.sgt.LineAttribute;
import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.swing.JPlotLayout;

import javax.swing.JPanel;

import org.junit.Assert;

public class MultiplePlotLayout extends JPanel implements IGraphLayout {
	
	private static final long serialVersionUID = -1624666263504930548L;
	private ArrayList<JPlotLayout> layouts = new ArrayList<JPlotLayout>();
	
	public MultiplePlotLayout(String id, int numberOfGraphs) {
		setLayout(new GridLayout());
		for(int i=0; i<numberOfGraphs; i++) {
			JPlotLayout plotLayout = new JPlotLayout(false, false, false, false, id, null, false);
			layouts.add(plotLayout);
			add(plotLayout);
		}
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

	@Override
	public void addData(Object data, Object attrs) {
		SGTData[] datas;
		LineAttribute[] lineAttrs;
		try {
			datas = (SGTData[]) data;
			lineAttrs = (LineAttribute[]) attrs;
			Assert.assertTrue(datas.length == layouts.size());
			Assert.assertTrue(lineAttrs.length == layouts.size());
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
			Assert.assertTrue(datas.length == layouts.size());				
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
		for(JPlotLayout layout : layouts) 
			layout.setTitles(title, null, null);
	}

	@Override
	public void setTitles(String[] titles) {
		String[] t = Arrays.copyOfRange(titles, 0, 3);
		for(JPlotLayout layout : layouts) 
			layout.setTitles(t[0], t[1], t[2]);

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
