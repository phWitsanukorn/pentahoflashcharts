package com.google.code.pentahoflashcharts.builder;

import org.dom4j.Node;

import ofc4j.model.Chart;
import ofc4j.model.elements.BarChart.Style;

public class ThreeDBarChartBuilder extends BarChartBuilder {
	
	@Override
	protected Style getStyle() {
		
		return Style.THREED;
	}
	
	@Override
	protected void setupOthers(Chart c, Node root) {
		
		c.getXAxis().set3D(5);
	}
}
