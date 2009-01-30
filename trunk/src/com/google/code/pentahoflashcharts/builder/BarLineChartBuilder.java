package com.google.code.pentahoflashcharts.builder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ofc4j.model.Chart;
import ofc4j.model.axis.XAxis;
import ofc4j.model.axis.YAxis;
import ofc4j.model.axis.Label.Rotation;
import ofc4j.model.elements.AreaHollowChart;
import ofc4j.model.elements.BarChart;
import ofc4j.model.elements.LineChart;

import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;

import com.google.code.pentahoflashcharts.OFC4JHelper;

public class BarLineChartBuilder  extends ChartBuilder {
	private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
	

	public Chart build(Node root, IPentahoResultSet data) {
		Chart c = new Chart();
		List bars = root.selectNodes("/chart/bars/bar");
		
		BarChart[] values = null;
		int rowCount = data.getRowCount();

		int barNum = bars.size();
		values = new BarChart[barNum];
		for (int i = 0; i < barNum; i++) {
			Node bar = (Node) bars.get(i);
			Node colorNode = bar.selectSingleNode("color");
			Node textNode = bar.selectSingleNode("text");
			BarChart e = new BarChart(BarChart.Style.NORMAL);
			
			Number[] datas = new Number[rowCount];
			if (colorNode != null && colorNode.getText().length() > 2) {
				String str = colorNode.getText().trim();
				e.setColour(str);
			}
			if(textNode!=null&&textNode.getText().length()>0)
			{
				e.setText(textNode.getText().trim());
			}
			Node colIndexNode = bar.selectSingleNode("sql-column-index");
			if (colIndexNode != null && colIndexNode.getText().length() > 0) {
				int index = Integer.parseInt(colIndexNode.getText().trim());
				for (int j = 0; j < rowCount; j++) {
					datas[j] = (Number) data.getValueAt(j, index - 1);
					e.addBars(new BarChart.Bar(datas[j].doubleValue()));
					// e.addValues(datas[j].doubleValue());
				}
			}
			values[i] = e;
		}
		c.addElements(values);
		
		LineChart e = new LineChart(LineChart.Style.DOT);

		Node colIndexNode = root.selectSingleNode("/chart/line/sql-column-index");
		Node linetooltipNode = root.selectSingleNode("/chart/line/tooltip");
		
		if(getValue(linetooltipNode)!=null)
		{
			e.setTooltip(getValue(linetooltipNode));
		}
			
		if (colIndexNode != null && colIndexNode.getText().length() > 0) {
			int index = Integer.parseInt(colIndexNode.getText().trim());
			
			for (int j = 0; j < rowCount; j++) {
				double value = ((Number) data.getValueAt(j, index - 1)).doubleValue();
				e.addValues(value) ;

			}
		}
		else
		{
			for (int j = 0; j < rowCount; j++) {
				double value = ((Number) data.getValueAt(j, data.getColumnCount() - 1)).doubleValue();
				e.addValues(value) ;
				
			}
		}
		
		Node colorNode = root.selectSingleNode("/chart/line/color");
		if (colorNode != null && colorNode.getText().length() > 0) {
			e.setColour(colorNode.getText().trim());
		}
		Node textNode = root.selectSingleNode("/chart/line/text");
		if (textNode != null && textNode.getText().length() > 0) {
			e.setText(textNode.getText().trim());
		}
		YAxis y_axis_right= new YAxis();
		Node rightstepsNode = root.selectSingleNode("/chart/y-axis-right/y-steps");
		Node rightyMaxNode = root.selectSingleNode("/chart/y-axis-right/y-max");
		Node rightyMinNode = root.selectSingleNode("/chart/y-axis-right/y-min");
		Node rightyLablesNode = root.selectSingleNode("/chart/y-axis-right/labels");
		
		if(getValue(rightyMaxNode)!=null)
		{
			y_axis_right.setMax(Integer.valueOf(getNodeValue(rightyMaxNode)));
		}
		
		
		if(getValue(rightyMinNode)!=null)
		{
			y_axis_right.setMin(Integer.valueOf(getNodeValue(rightyMinNode)));
		}
		
		if(getValue(rightstepsNode)!=null)
		{
			y_axis_right.setSteps(Integer.valueOf(getNodeValue(rightstepsNode)));
		}
		else
			y_axis_right.setSteps(Integer.valueOf(10));
		
		if(getValue(rightyLablesNode)!=null)
		{
			addLabels(y_axis_right, rightyLablesNode);
		}
		
		c.setYAxisRight(y_axis_right);
		e.setYaxis("right");
		c.addElements(e);
		
		String[] labels = new String[rowCount];
		for (int j = 0; j < rowCount; j++) {
			Object obj = data.getValueAt(j, 0);
			if (obj instanceof java.sql.Timestamp
					|| obj instanceof java.util.Date) {
				labels[j] = sf.format(obj);
			} else {
				labels[j] = obj.toString();
			}
		}
		c.setXAxis(new XAxis().addLabels(labels));
		Node stepsNode = root.selectSingleNode("/chart/y-axis/y-steps");
		Node yMaxNode = root.selectSingleNode("/chart/y-axis/y-max");
		setYAxisRange(c, stepsNode, yMaxNode);
		
		return c;
	}

}
