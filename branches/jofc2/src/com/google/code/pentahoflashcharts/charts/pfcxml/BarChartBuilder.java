package com.google.code.pentahoflashcharts.charts.pfcxml;

import java.text.SimpleDateFormat;
import java.util.List;

import ofc4j.model.Chart;
import ofc4j.model.elements.BarChart;
import ofc4j.model.elements.Element;
import ofc4j.model.elements.BarChart.Style;

import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;

public class BarChartBuilder  extends DefaultChartBuilder {
	protected static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
	
	
	
	protected void setupElements(Chart c, Node root, IPentahoResultSet data) {
		List bars = root.selectNodes("/chart/bars/bar");
		
		BarChart[] values = null;
		int rowCount = data.getRowCount();

		int barNum = bars.size();
		values = new BarChart[barNum];
		for (int i = 0; i < barNum; i++) {
			Node bar = (Node) bars.get(i);
			Node colorNode = bar.selectSingleNode("color");
			Node textNode = bar.selectSingleNode("text");
			BarChart e = new BarChart(getStyle());
			setBarchartData(data, rowCount, bar, colorNode, textNode, e);
			setOnClick(e,root,"/chart/bars/bar/on-click");
			setLink(e, root, "/chart/bars/bar/link");
			values[i] = e;
		}
		c.addElements(values);
	}

	
	protected void setupOthers(Chart c, Node root, IPentahoResultSet data) {
		
	}
	
	

	/**
	 * subclass should override this function.
	 * @return
	 */
	protected Style getStyle() {
		
		return Style.NORMAL;
	}

	protected static void setBarchartData(IPentahoResultSet data, int rowCount,
			Node bar, Node colorNode, Node textNode, BarChart e) {
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
	}
	
	

}
