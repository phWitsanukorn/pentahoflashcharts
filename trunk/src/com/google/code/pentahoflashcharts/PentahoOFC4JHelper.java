package com.google.code.pentahoflashcharts;

import ofc4j.model.Chart;
import ofc4j.model.Text;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;
import org.pentaho.platform.uifoundation.chart.ChartDefinition;

public class PentahoOFC4JHelper {
	
	public static String CHART_NODE_NAME = "chart";
	
	public static String DEFAULT_DATASET_TYPE = ChartDefinition.CATAGORY_DATASET_STR;
	
	

	
	public static Chart convert(Document doc, IPentahoResultSet data) {
		Chart c = new Chart();
		Text t = new Text();
		Element root = doc.getRootElement();
		Node chartNode = root.selectSingleNode("/" + PentahoOFC4JHelper.CHART_NODE_NAME);
		Node temp;
		
		// Chart Type
		String cType;
		// Chart Dataset Type
		String dType;
		
		// Chart Type and Dataset Type are required
		
		temp = chartNode.selectSingleNode("/" + ChartDefinition.TYPE_NODE_NAME);
		cType = temp.getText().trim();
		
		temp = chartNode.selectSingleNode("/" + ChartDefinition.DATASET_TYPE_NODE_NAME);
		if ( temp != null )
			dType = temp.getText().trim();
		else
			// Default is CategoricalDataset
			dType = ChartDefinition.CATAGORY_DATASET_STR;
		
		
			
		
		
		
		
		Node titleNode = root.selectSingleNode("/chart/title");
		if (titleNode != null && titleNode.getText().length() > 0) {
			c.setTitle(t.setText(titleNode.getText()));
		}

		// in the Pentaho chart, range-title equals yLengend title
		Node yLengendNode = root.selectSingleNode("/chart/range-title");
		// in the Pentaho chart, domain-title equals xLengend title
		Node xLengendNode = root.selectSingleNode("/chart/domain-title");
		Node is_3DNode = root.selectSingleNode("/chart/is-3D");
		Node is_glassNode = root.selectSingleNode("/chart/is-glass");
		Node is_sketchNode = root.selectSingleNode("/chart/is-sketch");
		
		if (yLengendNode != null && yLengendNode.getText().length() > 0) {
			Text text = new Text();
			text.setText(yLengendNode.getText().trim());
			text.setStyle("color: #736AFF; font-size: 14px;");
			c.setYLegend(text);
		}

		if (xLengendNode != null && xLengendNode.getText().length() > 0) {
			Text text = new Text();
			text.setText(xLengendNode.getText().trim());
			text.setStyle("color: #736AFF; font-size: 14px;");
			c.setXLegend(text);
		}
		
		switch (cType) {
		case ChartDefinition.BAR_CHART_STR :
			
			
		
		}
		
		return c;
}
