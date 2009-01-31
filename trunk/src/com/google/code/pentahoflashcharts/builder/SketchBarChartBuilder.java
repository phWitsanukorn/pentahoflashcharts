package com.google.code.pentahoflashcharts.builder;

import java.util.List;

import ofc4j.model.Chart;
import ofc4j.model.elements.BarChart;
import ofc4j.model.elements.SketchBarChart;

import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;

public class SketchBarChartBuilder extends BarChartBuilder {
	
	public Chart build(Node root, IPentahoResultSet data) {
		Chart c = super.build(root, data);
		return c;
	}
	
	protected void setupElements(Chart c, Node root, IPentahoResultSet data) {
		BarChart[] values = null;
		int rowCount = data.getRowCount();
		List bars = root.selectNodes("/chart/bars/bar");
		int barNum = bars.size();
		values = new BarChart[barNum];
		int functor = 5;
		for (int i = 0; i < barNum; i++) {
			Node bar = (Node) bars.get(i);
		
			Node textNode = bar.selectSingleNode("text");
			Node colorNode = bar.selectSingleNode("color");
			Node outlineColorNode = bar.selectSingleNode("outlineColor");
			String color="";
			String outlineColor="";
			if (colorNode != null && colorNode.getText().length() > 0) {
				color= colorNode.getText().trim();
			}
			if (outlineColorNode != null && outlineColorNode.getText().length() > 0) {
				outlineColor=outlineColorNode.getText().trim();
			}
			BarChart e = new SketchBarChart(color,outlineColor,functor);
			setBarchartData(data, rowCount, bar, colorNode, textNode, e);
			setOnClick(e,root,"/chart/bars/bar/on-click");
			setLink(e, root, "/chart/bars/bar/link");
			values[i] = e;
		}
		c.addElements(values);
	}


}
