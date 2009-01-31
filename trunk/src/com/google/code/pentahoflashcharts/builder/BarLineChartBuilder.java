package com.google.code.pentahoflashcharts.builder;

import java.util.List;

import ofc4j.model.Chart;
import ofc4j.model.elements.BarChart;
import ofc4j.model.elements.LineChart;

import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;

public class BarLineChartBuilder  extends LineChartBuilder {

	
	public Chart build(Node root, IPentahoResultSet data) {
		 Chart c = super.build(root, data);
		 
		 return c;
	}
	
	protected void setupElements(Chart c, Node root, IPentahoResultSet data) {
		int rowCount = data.getRowCount();
		setupBarElements(c, root, data);
		
		setupLineElements(c, root, data, rowCount);
		setupXAxisLabels(data, c, rowCount);
	}

	protected void setupLineElements(Chart c, Node root,
			IPentahoResultSet data, int rowCount) {
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
		e.setRightYAxis();
		c.addElements(e);
	}

	protected void setupBarElements(Chart c, Node root, IPentahoResultSet data) {
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
		
	}
	
}
