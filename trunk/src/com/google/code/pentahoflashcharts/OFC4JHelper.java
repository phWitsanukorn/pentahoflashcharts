package com.google.code.pentahoflashcharts;

import ofc4j.model.Chart;
import ofc4j.model.Text;
import ofc4j.model.axis.YAxis;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;

import com.google.code.pentahoflashcharts.builder.AreaChartBuilder;
import com.google.code.pentahoflashcharts.builder.BarChartBuilder;
import com.google.code.pentahoflashcharts.builder.BarLineChartBuilder;
import com.google.code.pentahoflashcharts.builder.GlassBarChartBuilder;
import com.google.code.pentahoflashcharts.builder.LineChartBuilder;
import com.google.code.pentahoflashcharts.builder.PieChartBuilder;
import com.google.code.pentahoflashcharts.builder.SketchBarChartBuilder;
import com.google.code.pentahoflashcharts.builder.ThreeDBarChartBuilder;

public class OFC4JHelper {

	
	// public static void main(String[] args) {
	// double t = 123456789.06;
	// System.out.println(t);
	// java.text.NumberFormat nf = java.text.NumberFormat.getNumberInstance();
	// nf.setGroupingUsed(false);
	// System.out.println(nf.format(t));
	//
	// }

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
		Node is_sketchNode = root.selectSingleNode("/chart/is-sketch");
	
		if (cType.equalsIgnoreCase("BarChart")) {
			boolean isDone = false;
			if (is_3DNode != null && is_3DNode.getText().length() > 0) {
				String str = is_3DNode.getText().trim();
				if (str.equalsIgnoreCase("true")) {

					ThreeDBarChartBuilder builder = new ThreeDBarChartBuilder();
					c = builder.build(root, data);
					isDone = true;
				}
			}
			if (isDone != true && is_glassNode != null
					&& is_glassNode.getText().length() > 0) {
				String str = is_glassNode.getText().trim();
				if (str.equalsIgnoreCase("true")) {

					GlassBarChartBuilder builder = new GlassBarChartBuilder();
					c = builder.build(root, data);
					isDone = true;
				}
			}

			if (isDone != true && is_sketchNode != null
					&& is_sketchNode.getText().length() > 0) {
				String str = is_sketchNode.getText().trim();
				if (str.equalsIgnoreCase("true")) {
					
					SketchBarChartBuilder builder = new SketchBarChartBuilder();
					c = builder.build(root, data);
					isDone = true;
				}
			}

			if (isDone != true) {
				BarChartBuilder builder = new BarChartBuilder();
				c = builder.build(root, data);
			}
		} else if (cType.equalsIgnoreCase("AreaChart")) {
			AreaChartBuilder builder = new AreaChartBuilder();
			return builder.build(root, data);

		} else if (cType.equalsIgnoreCase("LineChart")) {

			LineChartBuilder builder = new LineChartBuilder();
			c = builder.build(root, data);
		} else if (cType.equalsIgnoreCase("PieChart")) {

			PieChartBuilder builder = new PieChartBuilder();
			c = builder.build(root, data);
		} else if (cType.equalsIgnoreCase("BarLineChart")) {
			BarLineChartBuilder builder = new BarLineChartBuilder();
			c = builder.build(root, data);
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

	public static void setOnClick(ofc4j.model.elements.Element e, Node root,
			String xpath) {
		if (getValue(root.selectSingleNode(xpath)) != null) {
			e.setOn_click(getValue(root.selectSingleNode(xpath)).trim());
		}

	}

	public static void setLink(ofc4j.model.elements.Element e, Node root,
			String xpath) {
		if (getValue(root.selectSingleNode(xpath)) != null) {
			e.setLink(getValue(root.selectSingleNode(xpath)).trim());
		}

	}

	public static void setYAxisRange(Chart c, Node stepsNode, Node xMaxNode) {
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

	public static String getValue(Node n) {

		if (n != null && n.getText() != null && n.getText().length() > 0) {
			return n.getText().trim();
		} else {
			return null;
		}

	}
	
	public static String getNodeValue(Node n) {
		return n.getText().trim();
	}


}
