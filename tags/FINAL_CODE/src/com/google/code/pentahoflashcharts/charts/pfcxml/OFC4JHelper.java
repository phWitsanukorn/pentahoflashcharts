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

import jofc2.model.Chart;

import org.apache.commons.logging.Log;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;
import org.pentaho.commons.connection.PentahoDataTransmuter;

import com.google.code.pentahoflashcharts.charts.pfcxml.AreaChartBuilder;
import com.google.code.pentahoflashcharts.charts.pfcxml.BarChartBuilder;
import com.google.code.pentahoflashcharts.charts.pfcxml.BarLineChartBuilder;
import com.google.code.pentahoflashcharts.charts.pfcxml.GlassBarChartBuilder;
import com.google.code.pentahoflashcharts.charts.pfcxml.HorizontalBarChartBuilder;
import com.google.code.pentahoflashcharts.charts.pfcxml.LineChartBuilder;
import com.google.code.pentahoflashcharts.charts.pfcxml.PieChartBuilder;
import com.google.code.pentahoflashcharts.charts.pfcxml.ScatterChartBuilder;
import com.google.code.pentahoflashcharts.charts.pfcxml.SketchBarChartBuilder;
import com.google.code.pentahoflashcharts.charts.pfcxml.StackedBarChartBuilder;
import com.google.code.pentahoflashcharts.charts.pfcxml.ThreeDBarChartBuilder;

public class OFC4JHelper {

	
	// public static void main(String[] args) {
	// double t = 123456789.06;
	// System.out.println(t);
	// java.text.NumberFormat nf = java.text.NumberFormat.getNumberInstance();
	// nf.setGroupingUsed(false);
	// System.out.println(nf.format(t));
	//
	// }
	public static String generateChartJson(Node chartNode, IPentahoResultSet data, boolean byRow, Log log) {
		
		Chart c = new Chart();
		Node root = chartNode;
		
		IPentahoResultSet chartdata;
		
		if (byRow) {
	          chartdata = PentahoDataTransmuter.pivot(data);
	        } else {
	        	chartdata = data;
	        }
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
					c = builder.build(root, chartdata);
					isDone = true;
				}
			}
			if (isDone != true && is_glassNode != null
					&& is_glassNode.getText().length() > 0) {
				String str = is_glassNode.getText().trim();
				if (str.equalsIgnoreCase("true")) {

					GlassBarChartBuilder builder = new GlassBarChartBuilder();
					c = builder.build(root, chartdata);
					isDone = true;
				}
			}

			if (isDone != true && is_sketchNode != null
					&& is_sketchNode.getText().length() > 0) {
				String str = is_sketchNode.getText().trim();
				if (str.equalsIgnoreCase("true")) {
					
					SketchBarChartBuilder builder = new SketchBarChartBuilder();
					c = builder.build(root, chartdata);
					isDone = true;
				}
			}
			Node isStackedNode = root.selectSingleNode("/chart/is-stacked");
			if (isDone != true && isStackedNode != null
					&& isStackedNode.getText().length() > 0) {
				String str = isStackedNode.getText().trim();
				if (str.equalsIgnoreCase("true")) {
					
					StackedBarChartBuilder builder = new StackedBarChartBuilder();
					c = builder.build(root, chartdata);
					isDone = true;
				}
			}
			
			Node orientationNode = root.selectSingleNode("/chart/orientation");
			if (isDone != true && orientationNode != null
					&& orientationNode.getText().length() > 0) {
				String str = orientationNode.getText().trim();
				if (str.equalsIgnoreCase("horizontal")) {
					
					HorizontalBarChartBuilder builder = new HorizontalBarChartBuilder();
					c = builder.build(root, chartdata);
					isDone = true;
				}
			}
			if (isDone != true) {
				BarChartBuilder builder = new BarChartBuilder();
				c = builder.build(root, chartdata);
			}
		} else if (cType.equalsIgnoreCase("AreaChart")) {
			AreaChartBuilder builder = new AreaChartBuilder();
			c= builder.build(root, chartdata);

		} else if (cType.equalsIgnoreCase("LineChart")) {

			LineChartBuilder builder = new LineChartBuilder();
			c = builder.build(root, chartdata);
		} else if (cType.equalsIgnoreCase("PieChart")) {

			PieChartBuilder builder = new PieChartBuilder();
			c = builder.build(root, chartdata);
		} else if (cType.equalsIgnoreCase("BarLineChart")) {
			BarLineChartBuilder builder = new BarLineChartBuilder();
			c = builder.build(root, chartdata);
		} else if (cType.equalsIgnoreCase("ScatterChart")) {
			ScatterChartBuilder builder = new ScatterChartBuilder();
			c = builder.build(root, chartdata);
		}

		
		return c.toString();
		
		
	}


	

}
