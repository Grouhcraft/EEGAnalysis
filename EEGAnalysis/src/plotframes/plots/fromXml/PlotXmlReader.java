package plotframes.plots.fromXml;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

public class PlotXmlReader {

	private File xmlFile = null;
	private XmlPlotObject xmlPlot = null;

	public PlotXmlReader(File xmlFile) throws Exception {
		this.xmlFile = xmlFile;
		readXml();
	}

	public XmlPlotObject getXmlPlot() {
		return xmlPlot;
	}

	private void readXml() throws Exception {
		JAXBContext context = JAXBContext.newInstance(XmlPlotObject.class);
		Unmarshaller unMarshaller = context.createUnmarshaller();
		Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
		                 .newSchema(Class.class.getResource("/plot.xsd"));
		unMarshaller.setSchema(schema);
		XmlPlotObject xmlPlot = (XmlPlotObject) unMarshaller.unmarshal(xmlFile);
		this.xmlPlot = xmlPlot;
	}
}
