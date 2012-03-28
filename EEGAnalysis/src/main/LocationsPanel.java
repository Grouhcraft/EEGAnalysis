package main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JPanel;

public class LocationsPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5647450659672604274L;
	private static Image locationsImage = null;

	public LocationsPanel() {
		try {
			locationsImage = new javax.swing.ImageIcon(this.getClass().getResource("../channels_loc.jpg")).getImage();
		} catch (Exception e) {}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		if(locationsImage == null) {
			super.paintComponent(g);
		} else {
			Graphics2D d = (Graphics2D) g;
			int h = locationsImage.getHeight(null);
			int w = locationsImage.getWidth(null);
			d.setXORMode(d.getBackground());
			d.drawImage(locationsImage, 0, 0, w, h, null);
		}
	}
}
