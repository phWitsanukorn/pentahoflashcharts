package com.google.code.pentahoflashcharts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import ofc4j.model.Chart;
import ofc4j.model.Text;
import ofc4j.model.axis.XAxis;
import ofc4j.model.axis.YAxis;
import ofc4j.model.axis.Label.Rotation;
import ofc4j.model.elements.AreaHollowChart;
import ofc4j.model.elements.BarChart;
import ofc4j.model.elements.LineChart;
import ofc4j.model.elements.PieChart;
import ofc4j.model.elements.BarChart.Style;
import ofc4j.model.elements.PieChart.Slice;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;

public class OFC4JHelper {

	private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");

	public static void main(String[] args) {
		double t = 123456789.06;
		System.out.println(t);
		java.text.NumberFormat nf = java.text.NumberFormat.getNumberInstance();
		nf.setGroupingUsed(false);
		System.out.println(nf.format(t));

	}

	/**
	 * 
	 * @param doc
	 * @param data
	 * @return
	 */
	public static Chart convert(Document doc, IPentahoResultSet data) {
		Chart c = new Chart();
		Text t = new Text();
		Element root = doc.getRootElement();
		Node chartType = root.selectSingleNode("/chart/chart-type");
		String cType = chartType.getText().trim();
		Node titleNode = root.selectSingleNode("/chart/title");
		if (titleNode != null && titleNode.getText().length() > 0) {
			c.setTitle(t.setText(titleNode.getText()));
		}

		// in the Pentaho chart, range-title equals yLengend title
		Node yLengendNode = root.selectSingleNode("/chart/range-title");
		// in the Pentaho chart, domain-title equals xLengend title
		Node xLengendNode = root.selectSingleNode("/chart/domain-title");
		Node is_3DNode = root.selectSingleNode("/chart/is-3D");
		Node is_glassNode = root.selectSingleNode("/chart/is-glass");
		List bars = root.selectNodes("/chart/bars/bar");
		if (cType.equalsIgnoreCase("BarChart")) {
			boolean isDone = false;
			if (is_3DNode != null && is_3DNode.getText().length() > 0) {
				String str = is_3DNode.getText().trim();
				if (str.equalsIgnoreCase("true")) {
					create3DBarChart(data, c, root, bars);
					c.getXAxis().set3D(5);
					isDone = true;
				}
			}
			if (isDone != true && is_glassNode != null
					&& is_glassNode.getText().length() > 0) {
				String str = is_glassNode.getText().trim();
				if (str.equalsIgnoreCase("true")) {
					createGlassBarChart(data, c, root, bars);
					isDone = true;
				}
			}

			if (isDone != true)
				createBarChart(data, c, root, bars, BarChart.Style.NORMAL);
		} else if (cType.equalsIgnoreCase("AreaChart")) {
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

			c.setYAxis(yaxis);
			c.setXAxis(xaxis);
			c.addElements(e);
		} else if (cType.equalsIgnoreCase("LineChart")) {
			createLineChart(data, c, root);
		} else if (cType.equalsIgnoreCase("PieChart")) {
			createPieChart(data, c, root);
		} else if (cType.equalsIgnoreCase("BarLineChart")) {
			BarChart e = new BarChart();
			int rowCount = data.getRowCount();

			c.addElements(e);
		}

		if (yLengendNode != null && yLengendNode.getText().length() > 0) {
			Text text = new Text();
			text.setText(yLengendNode.getText().trim());
			text.setStyle("color: #736AFF; font-size: 14px;");
			c.setYLegend(text);
		}

		if (xLengendNode != null && xLengendNode.getText().length() > 0) {
			Text text = new Text();
			text.setText(xLengendNode.getText().trim());
			text.setStyle("color: #736AFF; font-size: 14px;");
			c.setXLegend(text);
		}
		return c;
	}

	private static void createGlassBarChart(IPentahoResultSet data, Chart c,
			Node root, List bars) {
		createBarChart(data, c, root, bars, BarChart.Style.GLASS);

	}

	private static void create3DBarChart(IPentahoResultSet data, Chart c,
			Node root, List bars) {

		createBarChart(data, c, root, bars, BarChart.Style.THREED);
	}

	private static void createPieChart(IPentahoResultSet data, Chart c,
			Element root) {
		PieChart e = new PieChart();
		int rowCount = data.getRowCount();

		Node startAngleNode = root.selectSingleNode("/chart/start-angle");
		if (startAngleNode != null && startAngleNode.getText().length() > 0) {
			e.setStartAngle(Integer.parseInt(startAngleNode.getText().trim()));
		} else
			e.setStartAngle(35);

		int columnCount = data.getMetaData().getColumnCount();

		for (int j = 0; j < rowCount; j++) {
			Object obj = data.getValueAt(j, 0);
			Number value = (Number) data.getValueAt(j, 1);
			if (obj instanceof java.sql.Timestamp
					|| obj instanceof java.util.Date) {
				e.addSlice(value.doubleValue(), sf.format(obj));
			} else {
				Slice s = new Slice(value.doubleValue(), obj.toString());
				e.addSlices(s);
			}
		}
		e.setAlpha(0.6f);
		e.setBorder(2);
		e.setAnimate(true);
		e.setColours("#d01f3c", "#356aa0", "#C79810", "#C79810");
		e.setTooltip("#val# of #total#<br>#percent# of 100%");
		c.addElements(e);
	}

	private static void createLineChart(IPentahoResultSet data, Chart c,
			Node root) {
		int columnCount = data.getMetaData().getColumnCount();
		LineChart[] elements = null;
		if (columnCount > 1) {
			elements = new LineChart[columnCount - 1];
			int rowCount = data.getRowCount();
			for (int i = 1; i <= columnCount - 1; i++) {
				LineChart e = new LineChart(LineChart.Style.DOT);
				Number[] datas = new Number[rowCount];

				
					for (int j = 0; j < rowCount; j++) {
						datas[j] = (Number) data.getValueAt(j, i);
						e.addValues(datas[j].doubleValue());
					}

				
				elements[i - 1] = e;
			}
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

		} else {
			elements = new LineChart[columnCount];
			LineChart e = new LineChart(LineChart.Style.DOT);

			Node chartBackGround=root.selectSingleNode("/chart/chart-background");
			if (chartBackGround != null) {

				e.setColour(chartBackGround.getText());
			}
			int rowCount = data.getRowCount();
			Number[] datas = new Number[rowCount];
			for (int i = 0; i < rowCount; i++) {
				datas[i] = (Number) data.getValueAt(i, 0);
			}
			e.addValues(datas);
			elements[0] = e;

		}
		c.addElements(elements);

		Node stepsNode = root.selectSingleNode("/chart/y-axis/y-steps");
		Node yMaxNode = root.selectSingleNode("/chart/y-axis/y-max");
		setYAxisRange(c, stepsNode, yMaxNode);
	}

	private static void createBarChart(IPentahoResultSet data, Chart c,
			Node root, List bars, Style style) {
		BarChart[] values = null;
		int rowCount = data.getRowCount();
		int columnCount = data.getMetaData().getColumnCount();
		int barNum = bars.size();
		values = new BarChart[barNum];
		for (int i = 0; i < barNum; i++) {
			Node bar = (Node) bars.get(i);
			Node colorNode = bar.selectSingleNode("color");
			Node textNode = bar.selectSingleNode("text");
			BarChart e = new BarChart(style);
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

		// set the x-axis
		setXAxis(c,root, data);
		//set the y-axis
		setYAxis(c, root);
		c.addElements(values);
	}

	private static void setYAxis(Chart c, Node root) {
		Node stepsNode = root.selectSingleNode("/chart/y-axis/y-steps");
		Node xMaxNode = root.selectSingleNode("/chart/y-axis/y-max");
		
		setYAxisRange(c, stepsNode, xMaxNode);
	}

	private static void setXAxis(Chart c,Node root, IPentahoResultSet data) {
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

	private static void setYAxisRange(Chart c, Node stepsNode, Node xMaxNode) {
		YAxis yaxis = new YAxis();
		int y_max = 10000;
		int y_step = 10;
		if (stepsNode != null && stepsNode.getText().length() > 0) {
			y_step = Integer.parseInt(stepsNode.getText().trim());
		}

		if (xMaxNode != null && xMaxNode.getText().length() > 0) {
			y_max = Integer.parseInt(xMaxNode.getText().trim());
		}
		yaxis.setRange(0, y_max, y_step);

		c.setYAxis(yaxis);
	}

}
