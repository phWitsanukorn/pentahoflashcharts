package com.google.code.pentahoflashcharts.builder;

import java.util.List;

import ofc4j.model.Chart;
import ofc4j.model.elements.StackedBarChart;
import ofc4j.model.elements.StackedBarChart.StackKey;
import ofc4j.model.elements.StackedBarChart.StackValue;

import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;

public class StackedBarChartBuilder extends BarChartBuilder {
	
	
	
	protected void setupElements(Chart c, Node root, IPentahoResultSet data) {

		StackedBarChart sbc = new StackedBarChart();
		setupKeys(c,root,data,sbc);
		setupStacks(c,root,data,sbc);
		c.addElements(sbc);
	}
	
	private void setupStacks(Chart c, Node root, IPentahoResultSet data,
			StackedBarChart sbc) {
		
		int rowCount = data.getRowCount();
		int columnCount = data.getColumnCount();
		List colors =root.selectNodes("/chart/color-palette/color");
		for (int i = 1; i < columnCount; i++) {
			StackValue[] datas = new StackValue[rowCount];
			for (int k = 0; k < rowCount; k++) {
				Node colorNode = (Node)colors.get(k);
				datas[k] = new StackValue((Number) data.getValueAt(k, i),getNodeValue(colorNode));
			}
			sbc.newStack().addStackValues(datas);
		}
	}

	private void setupKeys(Chart c, Node root, IPentahoResultSet data,StackedBarChart sbc) {
		int rowCount = data.getRowCount();
		List colors =root.selectNodes("/chart/color-palette/color");
		for (int j = 0; j < rowCount; j++) {
			Object obj = data.getValueAt(j, 0);
			StackKey key1 =new StackKey();
			key1.setText(""+obj);
			key1.setFontSize("14");
			Node colorNode = (Node)colors.get(j);
			key1.setColour(getNodeValue(colorNode));
			sbc.addKeys(key1);
		}
			
		
	}

}
