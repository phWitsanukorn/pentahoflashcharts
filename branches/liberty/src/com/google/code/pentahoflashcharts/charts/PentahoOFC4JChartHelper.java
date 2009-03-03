package com.google.code.pentahoflashcharts.charts;

import java.util.HashMap;
import java.util.Map;

import ofc4j.model.Chart;

import org.apache.commons.logging.Log;
import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;
import org.pentaho.commons.connection.PentahoDataTransmuter;

public class PentahoOFC4JChartHelper {
  
  private static final String CHART_TYPE_NODE_LOC = "chart-type"; //$NON-NLS-1$
  private static final String CHART_TYPE_DEFAULT = "BarChart"; //$NON-NLS-1$
  
  static {
    registerChartFactory("BarChart", BarChartFactory.class); //$NON-NLS-1$
    registerChartFactory("LineChart", LineChartFactory.class); //$NON-NLS-1$
    registerChartFactory("AreaChart", AreaChartFactory.class); //$NON-NLS-1$
    registerChartFactory("BarLineChart", BarLineChartFactory.class); //$NON-NLS-1$
    registerChartFactory("PieChart", PieChartFactory.class); //$NON-NLS-1$
    registerChartFactory("DotChart", DotChartFactory.class); //$NON-NLS-1$
    registerChartFactory("BubbleChart", BubbleChartFactory.class); //$NON-NLS-1$
  }
  
  static Map<String, Class<? extends ChartFactory>> factories = new HashMap<String, Class<? extends ChartFactory>>(); 
  
  public static void registerChartFactory(String type, Class<? extends ChartFactory> factory) {
    factories.put(type, factory);
  }
  
  public static Chart generateChart(Node chartNode, IPentahoResultSet data, boolean byRow, Log log) {
    try {
      String chartType = null;
      Node temp = chartNode.selectSingleNode(CHART_TYPE_NODE_LOC);
      if (AbstractChartFactory.getValue(temp) != null) {
        chartType =AbstractChartFactory. getValue(temp);
      } else {
        // This should NEVER happen.
        chartType = CHART_TYPE_DEFAULT;
      }

      Class<? extends ChartFactory> factoryClass = factories.get(chartType);
      
      // throw exception if factoryClass not found
      
      ChartFactory factory = factoryClass.getConstructor(new Class[0]).newInstance(new Object[0]);      
      factory.setChartNode(chartNode);
      factory.setLog(log);
      if (byRow) {
        factory.setData(PentahoDataTransmuter.pivot(data));
      } else {
        factory.setData(data);
      }
      return factory.convert();
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
