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
import ofc4j.model.elements.BarChart.Style;

import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;

import com.google.code.pentahoflashcharts.OFC4JHelper;

public class BarChartBuilder  extends ChartBuilder {
	private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
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
			BarChart e = new BarChart(getStyle());
			setBarchartData(data, rowCount, bar, colorNode, textNode, e);
			setOnClick(e,root,"/chart/bars/bar/on-click");
			setLink(e, root, "/chart/bars/bar/link");
			values[i] = e;
		}

		// set the x-axis
		setXAxis(c,root, data);
		//set the y-axis
		setYAxis(c, root);
		
		c.addElements(values);
		
		setupOthers(c,root);
		return c;
	}
	
	protected void setupOthers(Chart c, Node root) {
		
		
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
	
	public static void setXAxis(Chart c,Node root, IPentahoResultSet data) {
		if (root.selectSingleNode("/chart/x-axis") != null) {

			XAxis axis = new XAxis();
			Node colIndexNode = root
					.selectSingleNode("/chart/x-axis/labels/sql-column-index");
			if (colIndexNode != null && colIndexNode.getText().length() > 0) {
				int index = Integer.parseInt(colIndexNode.getText().trim());
				int rowCount = data.getRowCount();
				String[] labels = new String[rowCount];
				for (int j = 0; j < rowCount; j++) {
					Object obj = data.getValueAt(j, index - 1);
					if (obj instanceof java.sql.Timestamp
							|| obj instanceof java.util.Date) {
						labels[j] = sf.format(obj);
					} else {
						labels[j] = obj.toString();
					}
				}
				axis.setLabels(labels);
			}
			
			
			Node colorNode = root.selectSingleNode("/chart/x-axis/color");
			if(colorNode!=null&&colorNode.getText().length()>2)
			{
				axis.setColour(colorNode.getText().trim());
			}
			c.setXAxis(axis);
		}
	}

	public static void setYAxis(Chart c, Node root) {
		Node stepsNode = root.selectSingleNode("/chart/y-axis/y-steps");
		Node xMaxNode = root.selectSingleNode("/chart/y-axis/y-max");
		
		setYAxisRange(c, stepsNode, xMaxNode);
	}

}
