package plotframes.plots.implementations;

import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.dm.SGTMetaData;
import gov.noaa.pmel.sgt.dm.SimpleLine;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import plotframes.data.EEGSource;
import plotframes.graphlayouts.LinePlotLayout;
import plotframes.plots.IPlot;
import plotframes.plots.Plot;
import plotframes.plots.annotations.UserPlot;
import plotframes.plots.fromXml.XmlPlot;
import plotframes.plots.fromXml.XmlPlot.Metas.Axis;
import utils.Logger;
import utils.types.EEGAnalysisException;
import utils.types.Range;

@UserPlot(
		name = "2D EEP files plots",
		layout = LinePlotLayout.class
		)

public class Xml2DPlot extends Plot {

	private XmlPlot xmlPlot = null;

	public Xml2DPlot(int channel, EEGSource dataSource) {
		super(channel, dataSource);
	}

	public Xml2DPlot(IPlot plot) {
		super(plot);
	}

	public void setXmlPlot(XmlPlot p) {
		this.xmlPlot = p;
	}

	private void checkXmlPlotNotNull() throws EEGAnalysisException {
		if(this.xmlPlot == null) {
			throw new EEGAnalysisException("no xml infos yet given");
		}
	}

	@Override
	public void setDataId(Object data, String id) {
		((SimpleLine)data).setId(id);
	}

	@Override
	protected Object setMetaData(Object data) {
		try {
			checkXmlPlotNotNull();
			String xAxisLabel = "";
			String yAxisLabel = "";
			for(Axis a : xmlPlot.getMetas().getAxis()) {
				if(a.getType().equalsIgnoreCase("x")) {
					xAxisLabel = a.getValue();
				} else if(a.getType().equalsIgnoreCase("y")) {
					yAxisLabel = a.getValue();
				}
			}
		    ((SimpleLine)data).setXMetaData(new SGTMetaData(xAxisLabel, "", false, false));
		    ((SimpleLine)data).setYMetaData(new SGTMetaData(yAxisLabel, "", false, false));

		} catch (EEGAnalysisException e) {
			e.printStackTrace();
		}
		return data;
	}

	@Override
	protected SGTData processSignal() {
		double[][] data = getRawData();
		Context cx = Context.enter();
		try {
			checkXmlPlotNotNull();
			Scriptable scope = cx.initStandardObjects();

			Object js_data =   Context.javaToJS( data, scope );
			ScriptableObject.putProperty(scope, "input", js_data);

			String jimports = ""
					+ "Packages.filters.utils.FFT,"
					+ "Packages.filters.SineWaveGenerator"
					;

			String cmd = "var filters = JavaImporter(" + jimports + ");"
						+"with(filters) {"
						+ 	xmlPlot.getCode().getScript().getValue()
						+ 	"var output = " + xmlPlot.getCode().getScript().getMain() + "(input);"
						+ "}";

			cx.evaluateString(scope, cmd, "<" + xmlPlot.getMetas().getName() + ">", 1, null);

			Object js_output = scope.get("output", scope);
			data = (double[][]) ((NativeJavaArray)js_output).unwrap();
			setRawData(data);

			this.setData(new SimpleLine(data[X], data[Y], null));
		} catch (Exception e) {
			Logger.log("ERROR: " + Context.toString(e.getMessage()));
			e.printStackTrace();
		} finally {
			Context.exit();
		}
	    return (SGTData) this.getData();
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
