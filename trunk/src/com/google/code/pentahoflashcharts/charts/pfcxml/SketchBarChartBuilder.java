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
import ofc4j.model.elements.BarChart;
import ofc4j.model.elements.SketchBarChart;

import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;

public class SketchBarChartBuilder extends BarChartBuilder {
	
	
	
	protected void setupElements(Chart c, Node root, IPentahoResultSet data) {
		BarChart[] values = null;
		int rowCount = data.getRowCount();
		List bars = root.selectNodes("/chart/bars/bar");
		int barNum = bars.size();
		values = new BarChart[barNum];
		int functor = 5;
		for (int i = 0; i < barNum; i++) {
			Node bar = (Node) bars.get(i);
		
			Node textNode = bar.selectSingleNode("text");
			Node colorNode = bar.selectSingleNode("color");
			Node outlineColorNode = bar.selectSingleNode("outlineColor");
			String color="";
			String outlineColor="";
			if (colorNode != null && colorNode.getText().length() > 0) {
				color= colorNode.getText().trim();
			}
			if (outlineColorNode != null && outlineColorNode.getText().length() > 0) {
				outlineColor=outlineColorNode.getText().trim();
			}
			BarChart e = new SketchBarChart(color,outlineColor,functor);
			setBarchartData(data, rowCount, bar, colorNode, textNode, e);
			setOnClick(e,root,"/chart/bars/bar/on-click");
			setLink(e, root, "/chart/bars/bar/link");
			values[i] = e;
		}
		c.addElements(values);
	}


}
