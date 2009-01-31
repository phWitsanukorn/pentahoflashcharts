package com.google.code.pentahoflashcharts.builder;

import ofc4j.model.Chart;
import ofc4j.model.Text;
import ofc4j.model.axis.XAxis;
import ofc4j.model.axis.YAxis;

import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;

/**
 * It is the common base class of chart builder.
 * @author TOMQIN
 *
 */
public class DefaultChartBuilder extends ChartBuilder {

	@Override
	public Chart build(Node root, IPentahoResultSet data) {
		Chart c = new Chart();
		setupBackground(c,root);
		setupChartText(c,root);
		setupYAxis(c,root,data);
		setupYRightAxis(c,root,data);
		setupXAxis(c,root,data);
		setupElements(c,root,data);
		setupOthers(c,root,data);
		return c;
	}
	
	protected void setupOthers(Chart c, Node root, IPentahoResultSet data) {
		
		
	}

	/**
	 * should be overrided by the subclass
	 * @param c
	 * @param root
	 * @param data
	 */
	protected void setupElements(Chart c, Node root, IPentahoResultSet data) {
		
		
	}

	protected void setupYRightAxis(Chart c, Node root, IPentahoResultSet data) {
		if(root.selectSingleNode("/chart/y-axis-right")!=null)
		{
			YAxis y_axis_right= new YAxis();
			c.setYAxisRight(y_axis_right);
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
			
		}
		
	}

	protected void setupBackground(Chart c, Node root) {
		Node bgNode = root.selectSingleNode("/chart/chart-background");
		if(getValue(bgNode)!=null)
		{
			c.setBackgroundColour(getNodeValue(bgNode));
		}
		
	}

	protected void setupChartText(Chart c, Node root) {
		Node titleNode = root.selectSingleNode("/chart/title");
		if(getValue(titleNode)!=null)
		{
			c.setTitle(new Text(getNodeValue(titleNode)));
		}
		
	}

	protected void setupXAxis(Chart c,Node root, IPentahoResultSet data) {
		
		setXLegend(c,root);
		XAxis xaxis =new XAxis();
		c.setXAxis(xaxis);
		setupXAxisColor(c,root);
		setupXLegendFont(c,root);
	}

	protected void setupXLegendFont(Chart c, Node root) {
		
		
	}

	protected void setupXAxisColor(Chart c, Node root) {
		
		
	}

	protected void setupYAxis(Chart c,Node root, IPentahoResultSet data) {
		setYLegend(c,root);
		setupYLegendFont(c,root);
		Node stepsNode = root.selectSingleNode("/chart/y-axis/y-steps");
		Node yMaxNode = root.selectSingleNode("/chart/y-axis/y-max");
		if(stepsNode!=null||yMaxNode!=null)
			setYAxisRange(c, stepsNode, yMaxNode);
	}

	protected void setupYLegendFont(Chart c, Node root) {
		// TODO Auto-generated method stub
		
	}

}
