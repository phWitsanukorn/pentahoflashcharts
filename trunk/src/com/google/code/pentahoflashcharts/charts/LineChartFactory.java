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
 * Copyright 2009 Pentaho Corporation.  All rights reserved.
 */
package com.google.code.pentahoflashcharts.charts;

import jofc2.model.elements.LineChart;

import org.dom4j.Node;

public class LineChartFactory extends AbstractChartFactory {

  // line related elements
  private static final String LINE_WIDTH_NODE_LOC = "line-width"; //$NON-NLS-1$
  private static final String DOTSTYLE_NODE_LOC = "dot-style"; //$NON-NLS-1$
  private static final String DOT_WIDTH_NODE_LOC = "dot-width"; //$NON-NLS-1$

  // defaults
  private static final LineChart.Style.Type LINECHART_STYLE_TYPE_DEFAULT = LineChart.Style.Type.DOT;
  
  // line related members
  protected LineChart.Style linechartstyle;
  protected Integer linechartwidth;
  protected Integer dotwidth;
  
  @Override
  protected void createElements() {
    if (CATEGORY_TYPE.equals(datasetType)) {
      int columnCount = getColumnCount();
      
      // Create a "series" or element for each column past the first
      for (int col = 0; col < columnCount; col++) {
        elements.add(getLineChartFromColumn(col));
      }
    } else {
      // TODO: support XY in the future
    }
  }
  
  public LineChart getLineChartFromColumn(int col) {
    LineChart lc = new LineChart(this.linechartstyle);
    for (int row = 0; row < getRowCount(); row++) {
      double d = ((Number) getValueAt(row, col)).doubleValue();
      LineChart.Dot dot = new LineChart.Dot(d);

      if (dotwidth != null) {
        dot.setDotSize(dotwidth);
      }
      dot.setOnClick(buildURLTemplate(getColumnHeader(col), getRowHeader(row)));
      lc.addDots(dot);
    }
    if (linechartwidth != null) {
      lc.setWidth(linechartwidth);
    }

    lc.setColour(getColor(col));
    
    if (tooltipText != null) {
      lc.setTooltip(tooltipText);
    }
    
    // set the title for this series
    lc.setText(getColumnHeader(col));

    
    if (alpha != null) {
      lc.setAlpha(alpha);
    }
    
    return lc;
  }

  @Override
  protected void setupStyles() {
    super.setupStyles();
    
    LineChart.Style.Type mytype = this.LINECHART_STYLE_TYPE_DEFAULT;
    linechartstyle = new LineChart.Style(mytype);
    
    Node temp = chartNode.selectSingleNode(DOTSTYLE_NODE_LOC);
    if (getValue(temp) != null) {
    	linechartstyle.setType(getValue(temp));
    }
    
    
    
    temp = chartNode.selectSingleNode(LINE_WIDTH_NODE_LOC);
    if (getValue(temp) != null) {
      // parse with double so 1.0 is parsable
      linechartwidth = (int)Double.parseDouble(getValue(temp));
    }
    
    temp = chartNode.selectSingleNode(DOT_WIDTH_NODE_LOC);
    if (getValue(temp) != null) {
      dotwidth = Integer.parseInt(getValue(temp));
    } 
  }
}
