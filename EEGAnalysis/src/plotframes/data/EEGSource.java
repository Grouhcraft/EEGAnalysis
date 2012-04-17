package plotframes.data;

import utils.types.TimeFrame;

public interface EEGSource {

	String getName();

	double[][] read(DataInfos dataInfo, TimeFrame time);
	
	boolean isContinuous();

	boolean isFile();
}
