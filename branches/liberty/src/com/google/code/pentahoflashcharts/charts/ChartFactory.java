package com.google.code.pentahoflashcharts.charts;

import ofc4j.model.Chart;

import org.apache.commons.logging.Log;
import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;

public interface ChartFactory {
  void setData(IPentahoResultSet data);
  void setChartNode(Node chartNode);
  void setLog(Log log);
  Chart convert();
}
