package plotframes.plots.implementations;

import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.dm.SGTMetaData;
import gov.noaa.pmel.sgt.dm.SimpleLine;

import java.io.InputStream;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeJavaArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import plotframes.data.EEGSource;
import plotframes.graphlayouts.XmlPlotLayout;
import plotframes.plots.IPlot;
import plotframes.plots.Plot;
import plotframes.plots.annotations.GraphButton;
import plotframes.plots.annotations.GraphSetting;
import plotframes.plots.annotations.UserPlot;
import plotframes.plots.fromXml.XmlPlotObject;
import plotframes.plots.fromXml.XmlPlotObject.Metas.Axis;
import utils.Logger;
import utils.types.EEGAnalysisException;
import utils.types.Range;

@UserPlot(
		name = "EEP files plots",
		layout = XmlPlotLayout.class
		)

public class XmlPlot extends Plot {

	private XmlPlotObject xmlPlot = null;

	@GraphSetting(value="Plot script", rows=15, js=true)
	public String script = "";

	@GraphSetting("main function")
	public String main = "";

	@GraphSetting(value="Console", rows=5)
	public String console = "";

	@GraphButton("Reload/Reset")
	public void reloadContent() {
		script = xmlPlot.getCode().getScript().getValue();
		main = xmlPlot.getCode().getScript().getMain();
	}

	public XmlPlot(int channel, EEGSource dataSource) {
		super(channel, dataSource);
	}

	public XmlPlot(IPlot plot) {
		super(plot);
	}

	public void setXmlPlotObject(XmlPlotObject p) {
		this.xmlPlot = p;
		reloadContent();
		if(p.getMetas().getAxis().size() > 2) {
			try {
				throw new EEGAnalysisException(">2D plots from XML not yet implemented !");
			} catch (EEGAnalysisException e) {
				e.printStackTrace();
			}
		}
	}

	private void checkXmlPlotObjectNotNull() throws EEGAnalysisException {
		if(this.xmlPlot == null) {
			throw new EEGAnalysisException("no xml infos yet given");
		}
	}

	@Override
	public void setDataId(Object data, String id) {
		try {
			checkXmlPlotObjectNotNull();
			((SimpleLine)data).setId(id);
		} catch (EEGAnalysisException e) {
			Logger.log(e.getMessage());
		}

	}

	@Override
	protected Object setMetaData(Object data) {
		try {
			checkXmlPlotObjectNotNull();
			String xAxisLabel = "";
			String yAxisLabel = "";
			String xAxisUnit = "";
			String yAxisUnit = "";
			for(Axis a : xmlPlot.getMetas().getAxis()) {
				if(a.getType().equalsIgnoreCase("x")) {
					xAxisLabel = a.getValue();
					xAxisUnit = a.getUnit();
				} else if(a.getType().equalsIgnoreCase("y")) {
					yAxisLabel = a.getValue();
					yAxisUnit = a.getUnit();
				}
			}
		    ((SimpleLine)data).setXMetaData(new SGTMetaData(xAxisLabel, xAxisUnit, false, false));
		    ((SimpleLine)data).setYMetaData(new SGTMetaData(yAxisLabel, yAxisUnit, false, false));

		} catch (EEGAnalysisException e) {
			Logger.log(e.getMessage());
		}
		return data;
	}

	@Override
	protected SGTData processSignal() {
		double[][] data = getRawData();
		Context cx = Context.enter();
		try {
			checkXmlPlotObjectNotNull();
			Scriptable scope = cx.initStandardObjects();

			Object js_data =   Context.javaToJS( data, scope );
			ScriptableObject.putProperty(scope, "input", js_data);

			InputStream jsLibFile = getClass().getResourceAsStream("/EEGAnalysis.js");
			StringBuffer sb = new StringBuffer();
			int c = 0; do {
				c = jsLibFile.read();
				sb.append(c);
			} while(c != -1);

			String cmd = sb.toString()
					+ NL + script
					+ NL + "var output = " + main + "(input);";
			Logger.log("COMMMANNDD:" + cmd);
			cx.evaluateString(scope, cmd, "<" + xmlPlot.getMetas().getName() + ">", 1, null);

			Object js_output = scope.get("output", scope);
			data = (double[][]) ((NativeJavaArray)js_output).unwrap();
			setRawData(data);

			this.setData(new SimpleLine(data[X], data[Y], null));
		} catch (EEGAnalysisException e) {
			Logger.log(e.getMessage());
		} catch ( JavaScriptException
				| EvaluatorException
				| EcmaError e2) {
			javascriptError(e2);
		} catch (Exception e3) {
			e3.printStackTrace();
		} finally {
			Context.exit();
		}
	    return (SGTData) this.getData();
	}

	private void javascriptError(Exception e) {
		console += NL + "> " + Context.toString(e.getMessage());
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
