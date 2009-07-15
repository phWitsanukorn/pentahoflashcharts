/*
 * This program is free software; you can redistribute it and/or modify it under the 
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software 
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this 
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html 
 * or from the Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright 2009 Pentaho Corporation.  
 * Copyright 2009 Bayon Technologies.
 * All rights reserved.
 */
package com.google.code.pentahoflashcharts.charts.pfcxml;

import java.text.SimpleDateFormat;

import jofc2.model.Chart;
import jofc2.model.Text;
import jofc2.model.axis.XAxis;
import jofc2.model.axis.YAxis;
import jofc2.model.elements.Element;

import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;

import com.google.code.pentahoflashcharts.OpenFlashChartComponent;
import com.google.code.pentahoflashcharts.charts.AbstractChartFactory;
import com.google.code.pentahoflashcharts.charts.BarChartFactory;

import com.google.code.pentahoflashcharts.charts.PentahoOFC4JChartHelper;

/**
 * It is the common base class of chart builder.
 * @author TOMQIN
 *
 */
public class DefaultChartBuilder extends ChartBuilder {
	protected static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
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
	
	protected void setTooltip(Node root, Element e) {
		Node tooltipNode = root.selectSingleNode("/chart/tooltip");
		if (tooltipNode!=null&&tooltipNode.getText().length()>0) {
			String tooltip = tooltipNode.getText().trim();
			e.setTooltip(tooltip);
		} else {
			e.setTooltip("#val#");
		}
	}

	protected void setupChartText(Chart c, Node root) {
		Node chartNode = root.selectSingleNode("/"+OpenFlashChartComponent.CHART_NODE_LOC);
		Node title = chartNode.selectSingleNode(AbstractChartFactory.TITLE_NODE_LOC);

		
		Node titleFont = chartNode.selectSingleNode(AbstractChartFactory.TITLE_FONT_NODE_LOC);
		
		Text titleText = new Text();
		
		if ( getValue(title) != null ) {
			titleText.setText(getNodeValue(title));
		} else {
			// TODO Figure out a default
			titleText.setText("Title");
		}
		titleText.setStyle(AbstractChartFactory.buildCSSStringFromNode(titleFont));
		c.setTitle(titleText);
	}

	protected void setupXAxis(Chart c,Node root, IPentahoResultSet data) {
		
		setXLegend(c,root);
		XAxis xaxis =new XAxis();
		c.setXAxis(xaxis);
		setXAxisLabels(c,root,data);
		Node stepsNode = root.selectSingleNode("/chart/x-axis/x-steps");
		Node maxNode = root.selectSingleNode("/chart/x-axis/x-max");
		if(stepsNode!=null||maxNode!=null)
			setXAxisRange(c, stepsNode, maxNode);
		setupXAxisColor(c,root);

	}

	

	protected void setXAxisRange(Chart c, Node stepsNode, Node maxNode) {
		XAxis axis = c.getXAxis();
		if(axis==null)
		{
			axis = new XAxis();
			
			c.setXAxis(axis);
		}
		
		int max = 10000;
		int step = 10;
		if (stepsNode != null && stepsNode.getText().length() > 0) {
			step = Integer.parseInt(stepsNode.getText().trim());
		}

		if (maxNode != null && maxNode.getText().length() > 0) {
			max = Integer.parseInt(maxNode.getText().trim());
		}
		axis.setRange(0, max, step);
		
	}

	protected void setupXAxisColor(Chart c, Node root) {
		
		
	}

	protected void setupYAxis(Chart c,Node root, IPentahoResultSet data) {
		setYLegend(c,root);

		Node stepsNode = root.selectSingleNode("/chart/y-axis/y-steps");
		Node yMaxNode = root.selectSingleNode("/chart/y-axis/y-max");
		if(stepsNode!=null||yMaxNode!=null)
			setYAxisRange(c, stepsNode, yMaxNode);
		setupYAxisLabels(c, root, data);
	}


	
	protected static void setYLegend(Chart c,Node root)
	{

		
		Node chartNode = root.selectSingleNode("/"+OpenFlashChartComponent.CHART_NODE_LOC);
		// in the Pentaho chart, range-title equals yLengend title
		Node rangeTitle = chartNode.selectSingleNode(AbstractChartFactory.RANGE_TITLE_NODE_LOC);
		Node rangeTitleFont = chartNode.selectSingleNode(AbstractChartFactory.RANGE_TITLE_FONT_NODE_LOC);
		Text rangeText = new Text();
		if ( getValue(rangeTitle) != null ) {
			rangeText.setText(getNodeValue(rangeTitle));
		} else {
			// TODO set it to ??
			rangeText.setText("Range Title");
		}
		rangeText.setStyle(BarChartFactory.buildCSSStringFromNode(rangeTitleFont));

		
		c.setYLegend(rangeText);
	}
	
	protected static void setXLegend(Chart c,Node root)
	{

		Node chartNode = root.selectSingleNode("/"+OpenFlashChartComponent.CHART_NODE_LOC);
		// in the Pentaho chart, domain-title equals xLengend title
		Node domainTitle = chartNode.selectSingleNode(AbstractChartFactory.DOMAIN_TITLE_NODE_LOC);
		Node domainTitleFont = chartNode.selectSingleNode(AbstractChartFactory.DOMAIN_TITLE_FONT_NODE_LOC);
		Text domainText = new Text();
		if ( getValue(domainTitle) != null ) {
			domainText.setText(getNodeValue(domainTitle));
		} else {
			// TODO figure out what to do if the header isn't CategoryDataset
//			domainText.setText(data.getMetaData().getColumnHeaders()[0][0].toString());
		}
		domainText.setStyle(AbstractChartFactory.buildCSSStringFromNode(domainTitleFont));
		c.setXLegend(domainText);
		
	}
	
	protected void setupYAxisLabels(Chart c, Node root, IPentahoResultSet data) {
		if (root.selectSingleNode("/chart/y-axis") != null) {

			YAxis axis = c.getYAxis();
			if(axis==null)
			{
				axis = new YAxis();
				c.setYAxis(axis);
			}
			Node colIndexNode = root
					.selectSingleNode("/chart/y-axis/labels/sql-column-index");
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
			else if(getValue(root.selectSingleNode("/chart/y-axis/labels/values"))!=null)
			{
				axis.setLabels(fillLabels(root.selectSingleNode("/chart/y-axis/labels/values")));
			}
			
			
			Node colorNode = root.selectSingleNode("/chart/y-axis/color");
			if(colorNode!=null&&colorNode.getText().length()>2)
			{
				axis.setColour(colorNode.getText().trim());
			}

		}
	}
	
	protected static void setXAxisLabels(Chart c,Node root, IPentahoResultSet data) {
		if (root.selectSingleNode("/chart/x-axis") != null) {

			XAxis axis = c.getXAxis();
			if(axis==null)
			{
				axis = new XAxis();
				
				c.setXAxis(axis);
			}
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
			else if(getValue(root.selectSingleNode("/chart/x-axis/labels/values"))!=null)
			{
				axis.setLabels(fillLabels(root.selectSingleNode("/chart/x-axis/labels/values")));
			}
			
			
			Node colorNode = root.selectSingleNode("/chart/x-axis/color");
			if(colorNode!=null&&colorNode.getText().length()>2)
			{
				axis.setColour(colorNode.getText().trim());
			}

		}
	}

}
