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
import jofc2.model.elements.Element;
import jofc2.model.elements.PieChart;
import jofc2.model.elements.PieChart.Slice;

import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;

public class PieChartBuilder  extends DefaultChartBuilder {
	private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
	
	
	
	protected void setupElements(Chart c, Node root, IPentahoResultSet data) {
		PieChart e = new PieChart();
		int rowCount = data.getRowCount();
		Node colIndexNode = root.selectSingleNode("/chart/slice/datas/sql-column-index");
		Node labelIndexNode = root.selectSingleNode("/chart/slice/labels/sql-column-index");
		if (colIndexNode != null && colIndexNode.getText().length() > 0) {
			int index = Integer.parseInt(colIndexNode.getText().trim())  ;
			int labelindex = Integer.parseInt(labelIndexNode.getText().trim())  ;
			for (int j = 0; j < rowCount; j++) {
				Object obj = data.getValueAt(j, labelindex-1);
				Number value = (Number) data.getValueAt(j, index-1);
				if (obj instanceof java.sql.Timestamp
						|| obj instanceof java.util.Date) {
					e.addSlice(value.doubleValue(), sf.format(obj));
				} else {
					Slice s = new Slice(value.doubleValue(), obj.toString());
					e.addSlices(s);
				}
			}
		}
		c.addElements(e);
	}
	
	protected void setupOthers(Chart c, Node root, IPentahoResultSet data) {
		PieChart e = (PieChart)c.getElements().iterator().next();
		setStartAngle(root, e);
		setOnClick(e,root,"/chart/on-click");
		setLink(e, root, "/chart/link");
		e.setAlpha(0.3f);
		e.setBorder(2);
		setIsAnimated(root, e); 
		setColorPalette(root, e);
		setTooltip(root, e);
		
	}
	

	protected void setTooltip(Node root, Element e) {
		Node tooltipNode = root.selectSingleNode("/chart/tooltip");
		if (tooltipNode!=null&&tooltipNode.getText().length()>0) {
			String tooltip = tooltipNode.getText().trim();
			e.setTooltip(tooltip);
		} else {
			e.setTooltip("#val# of #total#<br>#percent# of 100%");
		}
	}


	protected void setColorPalette(Node root, PieChart e) {
		Node colorsNode = root.selectSingleNode("/chart/slice/color-palette");
		if(colorsNode!=null&&colorsNode.getText().length()>0)
		{
			String[] colors=null;

			colors=fillLabels(colorsNode);
			e.setColours(colors);

		}
	}


	protected void setIsAnimated(Node root, PieChart e) {
		Node isAnimateNode = root.selectSingleNode("/chart/isAnimate");
		if (isAnimateNode != null && isAnimateNode.getText().length() > 0) {
			String str = isAnimateNode.getText().trim();
			if("true".equalsIgnoreCase(str))
			{
				e.setAnimate(true);
			}
			else
				e.setAnimate(false);
		}
	}


	protected void setStartAngle(Node root, PieChart e) {
		Node startAngleNode = root.selectSingleNode("/chart/start-angle");
		if (startAngleNode != null && startAngleNode.getText().length() > 0) {
			e.setStartAngle(Integer.parseInt(startAngleNode.getText().trim()));
		} else
			e.setStartAngle(35);
	}

}
