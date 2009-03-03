package com.google.code.pentahoflashcharts.charts;

import ofc4j.model.elements.LineChart;

import org.dom4j.Node;

public class LineChartFactory extends AbstractChartFactory {

  // line related elements
  private static final String LINE_WIDTH_NODE_LOC = "line-width"; //$NON-NLS-1$
  private static final String DOTSTYLE_NODE_LOC = "dot-style"; //$NON-NLS-1$
  private static final String DOT_WIDTH_NODE_LOC = "dot-width"; //$NON-NLS-1$

  // defaults
  private static final LineChart.Style LINECHART_STYLE_DEFAULT = LineChart.Style.NORMAL;
  
  // line related members
  protected LineChart.Style linechartstyle;
  protected Integer linechartwidth;
  protected Integer dotwidth;
  
  @Override
  void createElements() {
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

    // set the onclick event to the base url template
    if (null != baseURLTemplate) {
      lc.setOn_click(baseURLTemplate);
    }
    
    return lc;
  }

  @Override
  void setupStyles() {
    Node temp = chartNode.selectSingleNode(DOTSTYLE_NODE_LOC);

    if (getValue(temp) != null) {
      if ("dot".equals(getValue(temp))) //$NON-NLS-1$
        linechartstyle = LineChart.Style.DOT;
      else if ("normal".equals(getValue(temp))) //$NON-NLS-1$
        linechartstyle = LineChart.Style.NORMAL;
      else if ("hollow".equals(getValue(temp))) //$NON-NLS-1$
        linechartstyle = LineChart.Style.HOLLOW;
      else
        linechartstyle = LINECHART_STYLE_DEFAULT;
    } else {
      linechartstyle = LINECHART_STYLE_DEFAULT;
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
