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

import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;

import jofc2.model.Chart;
import jofc2.model.elements.BarChart.Style;

public class ThreeDBarChartBuilder extends BarChartBuilder {
	
	@Override
	protected Style getStyle() {
		
		return Style.THREED;
	}
	
	
	protected void setupOthers(Chart c, Node root, IPentahoResultSet data) {
		super.setupOthers(c, root, data);
		c.getXAxis().set3D(5);
	}
}
