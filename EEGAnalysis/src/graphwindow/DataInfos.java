package graphwindow;

import java.io.File;

public class DataInfos  {
	public int 		channelsCount 	= 59;
	public int	 	fs 				= 100;
	public File 	file 			= null;
	public File 	markerFile 		= null;
	public int		channel 		= 1;
	public String[] channelsCodes 	= new String[]
			{ "AF3", "AF4", "F5", "F3", "F1",
			"Fz", "F2", "F4", "F6", "FC5", "FC3", "FC1", "FCz", "FC2",
			"FC4", "FC6", "CFC7", "CFC5", "CFC3", "CFC1", "CFC2", "CFC4",
			"CFC6", "CFC8", "T7", "C5", "C3", "C1", "Cz", "C2", "C4", "C6",
			"T8", "CCP7", "CCP5", "CCP3", "CCP1", "CCP2", "CCP4", "CCP6",
			"CCP8", "CP5", "CP3", "CP1", "CPz", "CP2", "CP4", "CP6", "P5",
			"P3", "P1", "Pz", "P2", "P4", "P6", "PO1", "PO2", "O1" };
	public String getChannelCode() {
		return channelsCodes[channel];
	}
}