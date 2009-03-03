package com.google.code.pentahoflashcharts.charts;

import ofc4j.model.elements.AreaHollowChart;
import ofc4j.model.elements.AreaLineChart;
import ofc4j.model.elements.LineChart;

public class AreaChartFactory extends LineChartFactory {

  @Override
  public LineChart getLineChartFromColumn(int col) {
    LineChart ac = null;
    if(linechartstyle != LineChart.Style.HOLLOW) {
      AreaLineChart ahc = new AreaLineChart();
      ahc.setFill(getColor(col));
      ac = ahc;
    } else {
      AreaHollowChart ahc = new AreaHollowChart();
      ahc.setFill(getColor(col));
      ac = ahc;
    }

    Number[] numbers = new Number[getRowCount()];
    for (int row = 0; row < getRowCount(); row++) {
      numbers[row] = ((Number) getValueAt(row, col)).doubleValue();
    }

    ac.addValues(numbers);
    ac.setColour(getColor(col));
    
    if (linechartwidth != null) {
      ac.setWidth(linechartwidth);
    }
    if (tooltipText != null) {
      ac.setTooltip(tooltipText);
    }
    return ac;
  }
}
