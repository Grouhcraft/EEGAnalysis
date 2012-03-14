package filters;

import java.util.ArrayList;
import java.util.List;

import main.Logger;

public class CutOff extends Filter {
	protected static enum AMPLITUDE { High, Low }
	
	public static double[][] lowAmplitude(double[][] data, double threshold) {
		return amplitude(data, threshold, AMPLITUDE.Low); 
	}
	
	public static double[][] highAmplitude(double[][] data, double threshold) {
		return amplitude(data, threshold, AMPLITUDE.High);
	}
	
	private static double[][] amplitude(double[][] data, double threshold, AMPLITUDE ampl ) {
		double f[] = data[Y];
		List<Integer> toErease = new ArrayList<Integer>();
		
		int xx = 1;
		while(xx < f.length-2) {
			int x = xx;
			
			if(isAscending(x, f)) {			
				x = nextDrop(x, f);
				if((ampl == AMPLITUDE.High && Math.abs(f[xx] - f[x]) < threshold)
						|| (ampl == AMPLITUDE.Low && Math.abs(f[xx] - f[x]) > threshold)) {
					toErease = addRangeTo(toErease, xx, x);
				}
				xx = x+1;
				continue;
			} 
			
			else if (isDropping(x, f)) {	
				x = nextAscent(x, f);
				if((ampl == AMPLITUDE.High && Math.abs(f[xx] - f[x]) < threshold) ||
						(ampl == AMPLITUDE.Low && Math.abs(f[xx] - f[x]) > threshold)) {
					toErease = addRangeTo(toErease, xx, x);
				}
				xx = x+1;
				continue;
			} 
			
			else {
				x = nextNonFlat(x, f);
				xx = x+1;
				continue;
			}
		}
		Logger.log("cutoff: " + toErease.size() + " samples droped");
		return removePoints(toErease, data);
	}
}
