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

import ofc4j.model.Chart;
import ofc4j.model.elements.ScatterChart;

import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;

public class ScatterChartBuilder extends DefaultChartBuilder {
	
	protected void setupElements(Chart c, Node root, IPentahoResultSet data) {
		int rowCount = data.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			ScatterChart se = new ScatterChart("");
			String text = (String) data.getValueAt(i, 0);
			se.setText(text);
			setupColors(root, se,i);
			setupDotSize(root, se,data,i);
			Number x = (Number)data.getValueAt(i, 1);
			Number y = (Number)data.getValueAt(i, 2);
			se.addPoint(x.doubleValue(), y);
			
			setTooltip(root,se);
//			se.setTooltip(""+text+":"+"("+data.getValueAt(i, 3)+","+y+","+x+")");
			c.addElements(se);
		}
		
		
		
	}

	private void setupColors(Node root, ScatterChart se,int i) {
		if(getValue(root.selectSingleNode("/chart/color-palette"))!=null)
		{
			List colors = root.selectNodes("/chart/color-palette/color");
			String colour= getNodeValue((Node)colors.get(i));
			se.setColour(colour);
		}
		else
			se.setColour("#86BBEF");
	}

	private void setupDotSize(Node root, ScatterChart se, IPentahoResultSet data,int i) {
		Number maxX=0;
		
		int rowCount = data.getRowCount();
		for (int j = 0; j < rowCount; j++) {
			Number x = (Number)data.getValueAt(j, 1);
			if(maxX.doubleValue()<x.doubleValue())
			{
				maxX = x;
			}
		}
		if(getValue(root.selectSingleNode("/chart/dot-size"))!=null)
		{
			
			String dotSize= getNodeValue(root.selectSingleNode("/chart/dot-size"));
			se.setDotSize(Integer.parseInt(dotSize));
		}
		else
		{
			Number x = (Number)data.getValueAt(i, 1);
			//<max-bubble-size>100</max-bubble-size>
			int maxBubbleSize = 100;
			if(getValue(root.selectSingleNode("/chart/max-bubble-size"))!=null)
			{
				maxBubbleSize = Integer.parseInt(getNodeValue(root.selectSingleNode("/chart/max-bubble-size")));
			}
			
				
			
			se.setDotSize(Integer.valueOf(java.lang.Math.round(maxBubbleSize*(x.floatValue()/maxX.floatValue())) ));
		}
			
	}

}
