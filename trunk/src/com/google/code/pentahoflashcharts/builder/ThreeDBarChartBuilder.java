package com.google.code.pentahoflashcharts.builder;

import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;

import ofc4j.model.Chart;
import ofc4j.model.elements.BarChart.Style;

public class ThreeDBarChartBuilder extends BarChartBuilder {
	
	@Override
	protected Style getStyle() {
		
		return Style.THREED;
	}
	
	
	protected void setupOthers(Chart c, Node root, IPentahoResultSet data) {
		super.setupOthers(c, root, data);
		c.getXAxis().set3D(5);
	}
}
