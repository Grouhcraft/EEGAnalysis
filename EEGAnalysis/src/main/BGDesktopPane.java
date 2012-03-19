package main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JDesktopPane;

public class BGDesktopPane extends JDesktopPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 837183880331079234L;
	private Image backImage = null; // member variable

	public BGDesktopPane() {
		try {
			backImage = new javax.swing.ImageIcon(this.getClass().getResource("../bg.jpg")).getImage();
		} catch (Exception e) {
		//	Logger.log("Could not find file in folder: "
		//			+ this.getClass().getResource("../bg.jpg"));
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(backImage != null) {
			Graphics2D g2d = (Graphics2D) g;
			
			double mw = backImage.getWidth(null);
			double mh = backImage.getHeight(null);
			double sw = this.getWidth() / mw;
			double sh = this.getHeight() / mh;
			g2d.scale(sw, sh);		
			g2d.drawImage(backImage, 0, 0, this.getWidth(), this.getHeight(), this);
		}
	}
}
