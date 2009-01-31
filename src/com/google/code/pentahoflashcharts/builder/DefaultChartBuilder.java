package com.google.code.pentahoflashcharts.builder;

import ofc4j.model.Chart;
import ofc4j.model.Text;
import ofc4j.model.axis.XAxis;
import ofc4j.model.axis.YAxis;

import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;

import com.google.code.pentahoflashcharts.PentahoOFC4JHelper;

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
		Node chartNode = root.selectSingleNode("/"+PentahoOFC4JHelper.CHART_NODE_LOC);
		Node title = chartNode.selectSingleNode(PentahoOFC4JHelper.TITLE_NODE_LOC);

		
		Node titleFont = chartNode.selectSingleNode(PentahoOFC4JHelper.TITLE_FONT_NODE_LOC);
		
		Text titleText = new Text();
		
		if ( getValue(title) != null ) {
			titleText.setText(getNodeValue(title));
		} else {
			// TODO Figure out a default
			titleText.setText("Title");
		}
		titleText.setStyle(PentahoOFC4JHelper.buildCSSStringFromNode(titleFont));
		c.setTitle(titleText);
	}

	protected void setupXAxis(Chart c,Node root, IPentahoResultSet data) {
		
		setXLegend(c,root);
		XAxis xaxis =new XAxis();
		c.setXAxis(xaxis);
		setupXAxisColor(c,root);

	}

	

	protected void setupXAxisColor(Chart c, Node root) {
		
		
	}

	protected void setupYAxis(Chart c,Node root, IPentahoResultSet data) {
		setYLegend(c,root);

		Node stepsNode = root.selectSingleNode("/chart/y-axis/y-steps");
		Node yMaxNode = root.selectSingleNode("/chart/y-axis/y-max");
		if(stepsNode!=null||yMaxNode!=null)
			setYAxisRange(c, stepsNode, yMaxNode);
	}


	
	protected static void setYLegend(Chart c,Node root)
	{

		
		Node chartNode = root.selectSingleNode("/"+PentahoOFC4JHelper.CHART_NODE_LOC);
		// in the Pentaho chart, range-title equals yLengend title
		Node rangeTitle = chartNode.selectSingleNode(PentahoOFC4JHelper.RANGE_TITLE_NODE_LOC);
		Node rangeTitleFont = chartNode.selectSingleNode(PentahoOFC4JHelper.RANGE_TITLE_FONT_NODE_LOC);
		Text rangeText = new Text();
		if ( getValue(rangeTitle) != null ) {
			rangeText.setText(getNodeValue(rangeTitle));
		} else {
			// TODO set it to ??
			rangeText.setText("Range Title");
		}
		rangeText.setStyle(PentahoOFC4JHelper.buildCSSStringFromNode(rangeTitleFont));
		c.setYLegend(rangeText);
	}
	
	protected static void setXLegend(Chart c,Node root)
	{

		Node chartNode = root.selectSingleNode("/"+PentahoOFC4JHelper.CHART_NODE_LOC);
		// in the Pentaho chart, domain-title equals xLengend title
		Node domainTitle = chartNode.selectSingleNode(PentahoOFC4JHelper.DOMAIN_TITLE_NODE_LOC);
		Node domainTitleFont = chartNode.selectSingleNode(PentahoOFC4JHelper.DOMAIN_TITLE_FONT_NODE_LOC);
		Text domainText = new Text();
		if ( getValue(domainTitle) != null ) {
			domainText.setText(getNodeValue(domainTitle));
		} else {
			// TODO figure out what to do if the header isn't CategoryDataset
//			domainText.setText(data.getMetaData().getColumnHeaders()[0][0].toString());
		}
		domainText.setStyle(PentahoOFC4JHelper.buildCSSStringFromNode(domainTitleFont));
		c.setXLegend(domainText);
		
	}
	
	

}
