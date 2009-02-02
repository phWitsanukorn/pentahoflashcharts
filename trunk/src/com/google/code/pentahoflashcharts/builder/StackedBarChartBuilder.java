package com.google.code.pentahoflashcharts.builder;

import ofc4j.model.Chart;
import ofc4j.model.elements.StackedBarChart;
import ofc4j.model.elements.StackedBarChart.StackValue;

import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;

public class StackedBarChartBuilder extends BarChartBuilder {
	
	
	
	protected void setupElements(Chart c, Node root, IPentahoResultSet data) {
		//TODO hard-coded for test
		StackedBarChart sbc = new StackedBarChart();
		sbc.newStack().addValues(2.5f, 5);
		sbc.newStack().addValues(7.5f);
		sbc.newStack().addValues(5);
		sbc.lastStack().addStackValues(new StackValue(5, "#ff0000"));
		sbc.newStack().addValues(2, 2, 2, 2);
		sbc.lastStack().addStackValues(new StackValue(2, "#ff00ff"));
		c.addElements(sbc);
	}
}
