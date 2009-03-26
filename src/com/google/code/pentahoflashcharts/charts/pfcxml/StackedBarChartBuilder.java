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
import ofc4j.model.elements.StackedBarChart;
import ofc4j.model.elements.StackedBarChart.StackKey;
import ofc4j.model.elements.StackedBarChart.StackValue;

import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;

public class StackedBarChartBuilder extends BarChartBuilder {
	
	
	
	protected void setupElements(Chart c, Node root, IPentahoResultSet data) {

		StackedBarChart sbc = new StackedBarChart();
		setupKeys(c,root,data,sbc);
		setupStacks(c,root,data,sbc);
		c.addElements(sbc);
	}
	
	private void setupStacks(Chart c, Node root, IPentahoResultSet data,
			StackedBarChart sbc) {
		
		int rowCount = data.getRowCount();
		int columnCount = data.getColumnCount();
		List colors =root.selectNodes("/chart/color-palette/color");
		for (int i = 1; i < columnCount; i++) {
			StackValue[] datas = new StackValue[rowCount];
			for (int k = 0; k < rowCount; k++) {
				Node colorNode = (Node)colors.get(k);
				datas[k] = new StackValue((Number) data.getValueAt(k, i),getNodeValue(colorNode));
			}
			sbc.newStack().addStackValues(datas);
		}
	}

	private void setupKeys(Chart c, Node root, IPentahoResultSet data,StackedBarChart sbc) {
		int rowCount = data.getRowCount();
		List colors =root.selectNodes("/chart/color-palette/color");
		for (int j = 0; j < rowCount; j++) {
			Object obj = data.getValueAt(j, 0);
			StackKey key1 =new StackKey();
			key1.setText(""+obj);
			key1.setFontSize("14");
			Node colorNode = (Node)colors.get(j);
			key1.setColour(getNodeValue(colorNode));
			sbc.addKeys(key1);
		}
			
		
	}

}
