package com.google.code.pentahoflashcharts;

import ofc4j.model.Chart;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;

import com.google.code.pentahoflashcharts.builder.AreaChartBuilder;
import com.google.code.pentahoflashcharts.builder.BarChartBuilder;
import com.google.code.pentahoflashcharts.builder.BarLineChartBuilder;
import com.google.code.pentahoflashcharts.builder.GlassBarChartBuilder;
import com.google.code.pentahoflashcharts.builder.HorizontalBarChartBuilder;
import com.google.code.pentahoflashcharts.builder.LineChartBuilder;
import com.google.code.pentahoflashcharts.builder.PieChartBuilder;
import com.google.code.pentahoflashcharts.builder.ScatterChartBuilder;
import com.google.code.pentahoflashcharts.builder.SketchBarChartBuilder;
import com.google.code.pentahoflashcharts.builder.StackedBarChartBuilder;
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

		Element root = doc.getRootElement();
		Node chartType = root.selectSingleNode("/chart/chart-type");
		String cType = chartType.getText().trim();

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
			Node isStackedNode = root.selectSingleNode("/chart/is-stacked");
			if (isDone != true && isStackedNode != null
					&& isStackedNode.getText().length() > 0) {
				String str = isStackedNode.getText().trim();
				if (str.equalsIgnoreCase("true")) {
					
					StackedBarChartBuilder builder = new StackedBarChartBuilder();
					c = builder.build(root, data);
					isDone = true;
				}
			}
			
			Node orientationNode = root.selectSingleNode("/chart/orientation");
			if (isDone != true && orientationNode != null
					&& orientationNode.getText().length() > 0) {
				String str = orientationNode.getText().trim();
				if (str.equalsIgnoreCase("horizontal")) {
					
					HorizontalBarChartBuilder builder = new HorizontalBarChartBuilder();
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
			c= builder.build(root, data);

		} else if (cType.equalsIgnoreCase("LineChart")) {

			LineChartBuilder builder = new LineChartBuilder();
			c = builder.build(root, data);
		} else if (cType.equalsIgnoreCase("PieChart")) {

			PieChartBuilder builder = new PieChartBuilder();
			c = builder.build(root, data);
		} else if (cType.equalsIgnoreCase("BarLineChart")) {
			BarLineChartBuilder builder = new BarLineChartBuilder();
			c = builder.build(root, data);
		} else if (cType.equalsIgnoreCase("ScatterChart")) {
			ScatterChartBuilder builder = new ScatterChartBuilder();
			c = builder.build(root, data);
		}

		return c;
	}

	

}
