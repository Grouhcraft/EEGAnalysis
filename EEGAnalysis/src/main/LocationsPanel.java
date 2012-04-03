package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JPanel;

import filters.utils.ImageColorsUtil;

public class LocationsPanel extends JPanel {
	private static final long serialVersionUID = 5647450659672604274L;
	private static Image locationsImage;
	static {
		try {
			locationsImage = new javax.swing.ImageIcon(LocationsPanel.class.getResource("../channels_loc.jpg")).getImage();
			locationsImage = ImageColorsUtil.makeColorTransparent(locationsImage, Color.WHITE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		if(locationsImage == null) {
			super.paintComponent(g);
		} else {
			Graphics2D d = (Graphics2D) g;
			int w = locationsImage.getWidth(null);
			int h = locationsImage.getHeight(null);
			d.drawImage(locationsImage, 0, 0, w, h, null);
		}
	}
}
