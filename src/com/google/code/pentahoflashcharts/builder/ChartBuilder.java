package com.google.code.pentahoflashcharts.builder;

import java.util.StringTokenizer;

import ofc4j.model.Chart;
import ofc4j.model.axis.YAxis;

import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;

public abstract class ChartBuilder {
	
	
	public abstract Chart build(Node root, IPentahoResultSet data) ;
	
	public static void setOnClick(ofc4j.model.elements.Element e,Node root ,String xpath) {
		if(getValue(root.selectSingleNode(xpath))!=null)
		{
			e.setOn_click(getNodeValue(root.selectSingleNode(xpath)));
		}
		
	}
	
	public static void setLink(ofc4j.model.elements.Element e,Node root ,String xpath) {
		if(getValue(root.selectSingleNode(xpath))!=null)
		{
			e.setLink(getNodeValue(root.selectSingleNode(xpath)));
		}
		
	}
	
	public static String getValue(Node n) {
		
		if (n != null && n.getText() != null && n.getText().length() > 0 ) {
			return n.getText().trim();
		} else {
			return null;
		}
		
	}
	
	public static String getNodeValue(Node n) {
			return n.getText().trim();
	}
	
	
	public static String[] fillLabels(Node rightstepsNode) {
		String[] labels;
		String labelStr = getNodeValue(rightstepsNode);
		StringTokenizer st = new StringTokenizer(labelStr,",");
		labels = new String[st.countTokens()];
		int i =0;
		while(st.hasMoreTokens())
		{
			String label = st.nextToken();
			labels[i]=label.trim();
			i++;

		}
		return labels;
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
	
	public static void addLabels(YAxis y_axis_right, Node rightstepsNode) {
		String[] labels =null;
		labels = fillLabels(rightstepsNode);
		y_axis_right.addLabels(labels);
	}
	

}
