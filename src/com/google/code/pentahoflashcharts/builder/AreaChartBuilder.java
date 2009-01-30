package com.google.code.pentahoflashcharts.builder;

import java.util.ArrayList;
import java.util.List;

import ofc4j.model.Chart;
import ofc4j.model.axis.XAxis;
import ofc4j.model.axis.YAxis;
import ofc4j.model.axis.Label.Rotation;
import ofc4j.model.elements.AreaHollowChart;

import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;

public class AreaChartBuilder extends ChartBuilder {

	

	public Chart build(Node root, IPentahoResultSet data) {
		Chart c = new Chart();
		AreaHollowChart e = new AreaHollowChart();
		int rowCount = data.getRowCount();
		List<Number> datas = new ArrayList<Number>(30);
		
		for (float i = 0; i < 6.2; i += 0.2) {
			datas.add(new Float(Math.sin(i) * 1.9));
		}
		e.addValues(datas);
		e.setWidth(1);
		XAxis xaxis = new XAxis();
		xaxis.setSteps(2);
		xaxis.getLabels().setSteps(4);
		xaxis.getLabels().setRotation(Rotation.VERTICAL);

		YAxis yaxis = new YAxis();
		yaxis.setRange(-2, 2, 2);
		yaxis.setOffset(false);

		c .setYAxis(yaxis);
		c.setXAxis(xaxis);
		c.addElements(e);
		return c;
	}

}
