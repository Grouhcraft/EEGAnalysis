package plotframes;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class GraphContextualMenu extends JPopupMenu implements ActionListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 5872282492000995707L;
	JMenuItem copyZoomItem;
	JMenuItem pasteZoomItem;
	private PlotFrame parentWindow;

	public GraphContextualMenu(PlotFrame parentWindow, boolean withZoomingFeatures) {
		super();
		setParentWindow(parentWindow);
		if(withZoomingFeatures) {
			copyZoomItem = new JMenuItem("Copy zoom");
			pasteZoomItem = new JMenuItem("Paste zoom");
			copyZoomItem.addActionListener(this);
			pasteZoomItem.addActionListener(this);
			add(copyZoomItem);
			add(pasteZoomItem);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Component c = (Component) e.getSource();
		try {
			if(c == copyZoomItem) {
				getParentWindow().copyZoom();
			} else if (c == pasteZoomItem) {
				getParentWindow().pasteZoom();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public PlotFrame getParentWindow() {
		return parentWindow;
	}

	public void setParentWindow(PlotFrame parentWindow) {
		this.parentWindow = parentWindow;
	}
}
