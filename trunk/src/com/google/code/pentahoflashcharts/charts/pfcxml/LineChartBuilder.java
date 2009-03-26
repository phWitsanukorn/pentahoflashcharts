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
import java.util.List;

import ofc4j.model.Chart;
import ofc4j.model.axis.XAxis;
import ofc4j.model.elements.LineChart;
import ofc4j.model.elements.LineChart.Style;

import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;

public class LineChartBuilder  extends DefaultChartBuilder {
	public static final String DOT_TYPE_NORMAL = "normal";
	public static final String DOT_TYPE_DOT = "dot";
	public static final String DOT_TYPE_HOLLOW = "hollow";
	private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
	

	
	protected void setupElements(Chart c, Node root, IPentahoResultSet data) {
		Style lineStyle = setupLineStyle(root);
		int columnCount = data.getMetaData().getColumnCount();
		LineChart[] elements = null;
		if (columnCount > 1) {
			elements = new LineChart[columnCount - 1];
			int rowCount = data.getRowCount();
			List colors = root.selectNodes("/chart/color-palette/color");
			for (int i = 1; i <= columnCount - 1; i++) {
				LineChart e = new LineChart(lineStyle);
				Number[] datas = new Number[rowCount];

				
					for (int j = 0; j < rowCount; j++) {
						datas[j] = (Number) data.getValueAt(j, i);
						e.addValues(datas[j].doubleValue());
					}
					String colour;
					if(colors!=null&&colors.size()>1)
					{
					colour= ((Node)colors.get(i-1)).getText().trim();
					e.setColour(colour);
					}
					e.setText((String)data.getMetaData().getColumnHeaders()[0][i]);
					setLink(e, root, "/chart/link");
					setOnClick(e,root,"/chart/on-click");
				elements[i - 1] = e;
			}
			setupXAxisLabels(data, c, rowCount);

		} else {
			elements = new LineChart[columnCount];
			LineChart e = new LineChart(setupLineStyle(root));

			Node chartBackGround=root.selectSingleNode("/chart/chart-background");
			if (chartBackGround != null) {

				e.setColour(chartBackGround.getText());
			}
			int rowCount = data.getRowCount();
			Number[] datas = new Number[rowCount];
			for (int i = 0; i < rowCount; i++) {
				datas[i] = (Number) data.getValueAt(i, 0);
			}
			e.addValues(datas);
			elements[0] = e;
			setLink(e, root, "/chart/link");
			setOnClick(e,root,"/chart/on-click");
		}
		c.addElements(elements);
	}

	protected Style setupLineStyle(Node root) {
		if(getValue(root.selectSingleNode("/chart/dot-style"))!=null)
		{
			String style = getNodeValue(root.selectSingleNode("/chart/dot-style"));
			if(DOT_TYPE_DOT.equalsIgnoreCase(style))
			{
				return LineChart.Style.DOT;
			}
			else if(DOT_TYPE_HOLLOW.equalsIgnoreCase(style))
			{
				return LineChart.Style.HOLLOW;
			}
		}
		return LineChart.Style.NORMAL;
	}
	


	protected void setupXAxisLabels(IPentahoResultSet data, Chart c, int rowCount) {
		String[] labels = new String[rowCount];
		for (int j = 0; j < rowCount; j++) {
			Object obj = data.getValueAt(j, 0);
			if (obj instanceof java.sql.Timestamp
					|| obj instanceof java.util.Date) {
				labels[j] = sf.format(obj);
			} else {
				labels[j] = obj.toString();
			}
		}
		if(c.getXAxis()==null)
			c.setXAxis(new XAxis().addLabels(labels));
		else
			c.getXAxis().addLabels(labels);
	}

}
