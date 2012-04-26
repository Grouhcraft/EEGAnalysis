package plotframes.plots.fromXml;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import utils.Logger;

public class PlotXmlReader {

	private File xmlFile = null;
	private XmlPlot xmlPlot = null;

	public PlotXmlReader(File xmlFile) {
		this.xmlFile = xmlFile;
		try {
			readXml();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public XmlPlot getXmlPlot() {
		return xmlPlot;
	}

	private void readXml() throws Exception {
		JAXBContext context = JAXBContext.newInstance(XmlPlot.class);
		Unmarshaller unMarshaller = context.createUnmarshaller();
		Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
		                 .newSchema(Class.class.getResource("/plot.xsd"));
		unMarshaller.setSchema(schema);
		XmlPlot xmlPlot = (XmlPlot) unMarshaller.unmarshal(xmlFile);
		this.xmlPlot = xmlPlot;

		/// log
		String script = "";
		script += "\n" + xmlPlot.getMetas().getName();
		script += "\n" + "N° of Axis: " + xmlPlot.getMetas().getAxis().size();
		script += "\n" + "Code: " + xmlPlot.getCode().getScript().getValue();
		Logger.log(script);
	}
}
