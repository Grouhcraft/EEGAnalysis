package plotframes.plots.implementations;

import filters.Filter;
import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.dm.SGTMetaData;
import gov.noaa.pmel.sgt.dm.SimpleLine;

import java.util.Arrays;

import math.transform.jwave.Transform;
import math.transform.jwave.handlers.FastWaveletTransform;
import math.transform.jwave.handlers.wavelets.Lege06;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import plotframes.graphlayouts.LinePlotLayout;
import plotframes.plots.IPlot;
import plotframes.plots.Plot;
import plotframes.plots.annotations.GraphButton;
import plotframes.plots.annotations.GraphSetting;
import plotframes.plots.annotations.UserPlot;
import utils.types.Range;

@UserPlot(	name = "Test !!",
			layout = LinePlotLayout.class )

public class TestPlot extends Plot {
	public TestPlot(IPlot plot) {
		super(plot);
	}

	@Override
	protected Object setMetaData(Object data) {
	    ((SimpleLine)data).setXMetaData(new SGTMetaData("Time", "secondes", false, false));
	    ((SimpleLine)data).setYMetaData(new SGTMetaData("Potential", "µV", false, false));
	    return data;
	}

	@GraphSetting(value="Etapes", list={1,3,6})
	public int scales = 3;

	@Override
	protected SGTData processSignal() {
		double[][] data = getRawData();
	    Transform t = new Transform(new FastWaveletTransform(new Lege06(), scales));

	    data[Y] = t.forward(data[Y]);
	    data[Y] = iterateScales(data[Y], 1, scales, getStdDev(data[Y]));
	    data[Y] = t.reverse(data[Y]);
	    data[X] = Filter.oneOfTwo(data[X]);

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
	public void setDataId(Object data, String id) {
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

			String cmd = "var filters = JavaImporter(Packages.filters.utils.FFT); with(filters){" + script + "}";

			cx.evaluateString(scope, cmd, "<cmd>", 1, null);

			double[] js_result_x = (double[]) ((NativeJavaArray)scope.get("X", scope)).unwrap();
			double[] js_result_y = (double[]) ((NativeJavaArray)scope.get("Y", scope)).unwrap();
			data[X] = js_result_x;
			data[Y] = js_result_y;
			setRawData(data);

			scriptResult = "DONE.";
			//update();
			this.setData(new SimpleLine(data[X], data[Y], null));
		} catch (Exception e) {
			scriptResult = "ERROR: " + Context.toString(e.getMessage());
			e.printStackTrace();
		} finally {
			Context.exit();
		}
	}

	@Override
	public Range<Double> getXRange() {
		return new Range<Double>(
				(Double)((SGTData)getData()).getXRange().getStart().getObjectValue(),
				(Double)((SGTData)getData()).getXRange().getEnd().getObjectValue()
				);
	}

	@Override
	public Range<Double> getYRange() {
		return new Range<Double>(
				(Double)((SGTData)getData()).getYRange().getStart().getObjectValue(),
				(Double)((SGTData)getData()).getYRange().getEnd().getObjectValue()
				);
	}
}
