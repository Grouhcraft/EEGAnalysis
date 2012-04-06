package graphwindow.plot.implementations;

import java.util.Arrays;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import main.utils.Logger;
import math.transform.jwave.Transform;
import math.transform.jwave.handlers.FastWaveletTransform;
import math.transform.jwave.handlers.wavelets.Lege06;
import filters.utils.Filter;
import filters.utils.FrequencyRange;
import filters.utils.Range;
import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.dm.SGTMetaData;
import gov.noaa.pmel.sgt.dm.SimpleLine;
import graphwindow.graphlayouts.LinePlotLayout;
import graphwindow.plot.GraphButton;
import graphwindow.plot.GraphSetting;
import graphwindow.plot.IPlot;
import graphwindow.plot.Plot;
import graphwindow.plot.graphtype;

@graphtype(	name = "Test !!",
			layout = LinePlotLayout.class )

public class TestPlot extends Plot {
	public TestPlot(IPlot plot) {
		super(plot);
	}
	
	@Override
	protected SGTData setMetaData(SGTData data) {
	    ((SimpleLine)data).setXMetaData(new SGTMetaData("Time", "secondes", false, false));
	    ((SimpleLine)data).setYMetaData(new SGTMetaData("Potential", "�V", false, false));
	    return data;
	}
	
	@GraphSetting(value="range", limits={1,20})
	public FrequencyRange range = new FrequencyRange(3.4, 9);
	
	@GraphSetting(value="numberLimited", limits={1,20})
	public int numLimited = 3;
	
	//@GraphSetting("test semi-enum")
	//public String aliment = null;
	
	public enum _enum { VAL_A, VAL_B }
	
	@GraphSetting("test vrai enum")
	public _enum foo = _enum.VAL_A;
	
	@GraphSetting(value="Etapes", list={1,3,6})
	public int scales = 3;
	
	@GraphSetting("Test !")
	public String plop = "test string";
	
	@GraphSetting("Test booleen")
	public boolean trueFalse = true;
	
	@GraphButton("do That")
	public void doSomething() {
		Logger.log("doSomething() called !!");
	}
	
	@Override
	protected SGTData processSignal() {
		double[][] data = getRawData();
	    Transform t = new Transform(new FastWaveletTransform(new Lege06(), scales));
	    
	    data[Y] = t.forward(data[Y]);
	    
	    data[Y] = iterateScales(data[Y], 1, scales, getStdDev(data[Y]));
	    
	    data[Y] = t.reverse(data[Y]);
	    
	    Logger.log("dyl " + data[Y].length);
	    Logger.log("dxl " + data[X].length);
	    
	    //data[Y] = Arrays.copyOf(data[Y], (int) Math.floor(data[Y].length/2));
	   
	    data[X] = Filter.oneOfTwo(data[X]);
	    
	    Logger.log("dyl " + data[Y].length);
	    Logger.log("dxl " + data[X].length);
	    		
	    return new SimpleLine(data[X], data[Y], null);
	}
	
	@GraphSetting("Seuil")
	public double treshold = 0.5;
	
	private double[] iterateScales(double[] data, int currentLevel, int nLevels, double stdDev) {
		
		double[] firstHalf = Arrays.copyOfRange(data, 0, data.length / 2);
		double[] secondHalf = Arrays.copyOfRange(data, data.length / 2, data.length);
		
		firstHalf = processScale(firstHalf, stdDev);
		
		if(currentLevel < nLevels)
			secondHalf = iterateScales(secondHalf, currentLevel+1, nLevels, getStdDev(firstHalf));
		else
			secondHalf = processScale(secondHalf, stdDev);
		
    	double[] completeLevel = new double[data.length];
    	completeLevel = Arrays.copyOfRange(firstHalf, 0, firstHalf.length);
    	for(int i=firstHalf.length; i<completeLevel.length; i++) {
    		completeLevel[i] = secondHalf[i-firstHalf.length];
    	}
    	return completeLevel;
	}

	private double[] processScale(double[] data, double stdDev) {
		return threeshold(data, treshold * stdDev);
	}

	private double[] threeshold(double[] ds, double k) {
		for(int i=0; i<ds.length; i++)
			if(Math.abs(ds[i]) <= k) ds[i] = 0;
		return ds;
	}
	
	private double getStdDev(double[] data) { 
		return Math.sqrt(getVariance(data)); 
	}

	private double getVariance(double[] ds) {
		double mean = getAverage(ds);
		double v = 0;
		double x; 
		for(double i : ds) {
			x = mean - i;
			v += x*x;
		}
		return v/ds.length;
	}
	
	private double getAverage(double[] ds) {
		double t = 0;
		for(double i : ds) t+=i;
		return t / ds.length;
	}

	@Override
	public void setDataId(SGTData data, String id) {
		((SimpleLine)data).setId(id);
	}
	
	@GraphSetting(value="Script", rows=10, js=true)
	public String script = 	"/**\n * plot data => var X[], var Y[], var dataLength\n"
							+ "*/\n"
							+ "for(var i=0; i<dataLength; i++) \n{\n"
							+ "\t// Y[i] = Y[i]*2;\n"
							+ "}\n";
	
	@GraphSetting("Results")
	public String scriptResult = "";
	
	@GraphButton("Run script")
	public void runScript() {
		Context cx = Context.enter();
		double[][] data = getRawData();
		try {
			Scriptable scope = cx.initStandardObjects();		
			
			Object js_x = Context.javaToJS(data[X], scope);
			Object js_y = Context.javaToJS(data[Y], scope);
			Object js_len =Context.javaToJS(data[X].length, scope);
			ScriptableObject.putProperty(scope, "X", js_x);
			ScriptableObject.putProperty(scope, "Y", js_y);
			ScriptableObject.putProperty(scope, "dataLength", js_len);
			
			Object result = cx.evaluateString(scope, script, "<cmd>", 1, null);
			
			double[] js_result_x = (double[]) ((NativeJavaArray)scope.get("X", scope)).unwrap();
			double[] js_result_y = (double[]) ((NativeJavaArray)scope.get("Y", scope)).unwrap();
			data[X] = js_result_x;
			data[Y] = js_result_y;
			setRawData(data);
			
			scriptResult = "DONE.";
			update();
		} catch (Exception e) {
			scriptResult = "ERROR: " + Context.toString(e.getMessage());
			e.printStackTrace();
		} finally {
			Context.exit();
		}
	}
}