package utils;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

/**
 * This class is used for making an Image in which a designated color is made
 * transparent.
 *
 * @author Faisal Feroz
 *
 */
public class ImageColorsUtil {

	/**
	 * This method makes the color in image transparent.
	 *
	 * @param im
	 *            The image who's color needs to be made transparent.
	 * @param color
	 *            The color which needs to be filtered out of the image.
	 * @return Image from which the color has been removed.
	 */
	public static Image makeColorTransparent(Image im, final Color color) {

		ImageFilter filter = new RGBImageFilter() {

			// the color we are looking for... Alpha bits are set to opaque
			public int markerRGB = color.getRGB() | 0xFF000000;

			@Override
			public final int filterRGB(int x, int y, int rgb) {
				if ((rgb | 0xFF000000) == markerRGB) {
					// Mark the alpha bits as zero - transparent
					return 0x00FFFFFF & rgb;
				} else {
					// nothing to do
					return rgb;
				}
			}
		};

		ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
		return Toolkit.getDefaultToolkit().createImage(ip);
	}
}
