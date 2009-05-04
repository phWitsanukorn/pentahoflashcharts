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

import java.util.List;

import jofc2.model.Chart;
import jofc2.model.elements.BarChart;
import jofc2.model.elements.LineChart;
import jofc2.model.elements.LineChart.Style;

import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;

public class BarLineChartBuilder  extends LineChartBuilder {

	
	
	
	protected void setupElements(Chart c, Node root, IPentahoResultSet data) {
		int rowCount = data.getRowCount();
		setupBarElements(c, root, data);
		
		setupLineElements(c, root, data, rowCount);
//		setupXAxisLabels(data, c, rowCount);
	}

	protected void setupLineElements(Chart c, Node root,
			IPentahoResultSet data, int rowCount) {
		Style lineStyle = setupLineStyle(root);
		LineChart e = new LineChart(lineStyle);

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
		Node is_3DNode = root.selectSingleNode("/chart/is-3D");
		Node is_glassNode = root.selectSingleNode("/chart/is-glass");
//		Node is_sketchNode = root.selectSingleNode("/chart/is-sketch");
		BarChart.Style  style = null;
		if (getValue(is_3DNode) != null ) {
			String str = is_3DNode.getText().trim();
			if (str.equalsIgnoreCase("true")) {
				style =BarChart.Style.THREED;
			}
		}
		if (getValue(is_glassNode) != null ) {
			String str = is_glassNode.getText().trim();
			if (str.equalsIgnoreCase("true")) {
				style =BarChart.Style.GLASS;
			}
		}
		if(style==null)
			style =BarChart.Style.NORMAL;
		BarChart[] values = null;
		int rowCount = data.getRowCount();

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
		c.addElements(values);
		
	}
	
}
