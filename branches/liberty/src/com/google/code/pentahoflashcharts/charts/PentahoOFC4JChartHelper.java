package com.google.code.pentahoflashcharts.charts;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;
import org.pentaho.commons.connection.PentahoDataTransmuter;
import org.pentaho.platform.api.repository.ISolutionRepository;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.core.system.StandaloneSession;
import org.pentaho.platform.engine.services.messages.Messages;

public class PentahoOFC4JChartHelper {

  private static final Log logger = LogFactory.getLog(PentahoOFC4JChartHelper.class);
  
  private static final String CHART_TYPE_NODE_LOC = "chart-type"; //$NON-NLS-1$
  private static final String CHART_TYPE_DEFAULT = "BarChart"; //$NON-NLS-1$
  
  private static final String PLUGIN_BUNDLE_NAME = "com.google.code.pentahoflashcharts.charts.chartfactories";//$NON-NLS-1$
  
  private static final String SOLUTION_PROPS = "system/openflashchart.properties"; //$NON-NLS-1$
  
  @SuppressWarnings("unchecked")
  static Map factories = null; 
  
  @SuppressWarnings("unchecked")
  public static String generateChartJson(Node chartNode, IPentahoResultSet data, boolean byRow, Log log) {
    String chartType = null;
    String factoryClassString = null;
    try {
      Node temp = chartNode.selectSingleNode(CHART_TYPE_NODE_LOC);
      if (AbstractChartFactory.getValue(temp) != null) {
        chartType =AbstractChartFactory. getValue(temp);
      } else {
        // This should NEVER happen.
        chartType = CHART_TYPE_DEFAULT;
      }

      factoryClassString = (String)getChartFactories().get(chartType);
      Class factoryClass = Class.forName(factoryClassString);
      
      // throw exception if factoryClass not found
      
      IChartFactory factory = (IChartFactory)factoryClass.getConstructor(new Class[0]).newInstance(new Object[0]);      
      factory.setChartNode(chartNode);
      factory.setLog(log);
      if (byRow) {
        factory.setData(PentahoDataTransmuter.pivot(data));
      } else {
        factory.setData(data);
      }
      return factory.convertToJson();
      
    } catch (Exception e) {
      logger.error(Messages.getString("PentahoOFC4JChartHelper.ERROR_0001_FACTORY_INIT", chartType, factoryClassString), e); //$NON-NLS-1$
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings({"unchecked"})
  protected static Map getChartFactories() {
    if (factories == null) {
      factories = Collections.synchronizedMap(createChartFactoryMap());
    }
    return factories;
  }
  
  @SuppressWarnings("unchecked")
  private static Map createChartFactoryMap() {
    Properties chartFactories = new Properties();
    // First, get known chart factories...
    try {
      ResourceBundle pluginBundle = ResourceBundle.getBundle(PLUGIN_BUNDLE_NAME);
      if (pluginBundle != null) { // Copy the bundle here...
        Enumeration keyEnum = pluginBundle.getKeys();
        String bundleKey = null;
        while (keyEnum.hasMoreElements()) {
          bundleKey = (String) keyEnum.nextElement();
          chartFactories.put(bundleKey, pluginBundle.getString(bundleKey));
        }
      }
    } catch (Exception ex) {
      logger.warn(Messages.getString("PentahoOFC4JChartHelper.WARN_NO_CHART_FACTORY_PROPERTIES_BUNDLE")); //$NON-NLS-1$
    }
    // Get overrides...
    //
    // Note - If the override wants to remove an existing "known" plugin, 
    // simply adding an empty value will cause the "known" plugin to be removed.
    //
    if( PentahoSystem.getObjectFactory() == null || 
        !PentahoSystem.getObjectFactory().objectDefined( ISolutionRepository.class.getSimpleName() ) ) {
      // this is ok
      return chartFactories;
    }
    ISolutionRepository solutionRepository = PentahoSystem.get(ISolutionRepository.class, new StandaloneSession("system")); //$NON-NLS-1$
    try {
      if( solutionRepository.resourceExists( SOLUTION_PROPS ) ) {
        InputStream is = solutionRepository.getResourceInputStream(SOLUTION_PROPS, false);
        Properties overrideChartFactories = new Properties();
        overrideChartFactories.load(is);
        chartFactories.putAll(overrideChartFactories); // load over the top of the known properties
      }
    } catch (FileNotFoundException ignored) {
      logger.warn(Messages.getString("PentahoOFC4JChartHelper.WARN_NO_CHART_FACTORY_PROPERTIES")); //$NON-NLS-1$
    } catch (IOException ignored) {
      logger.warn(Messages.getString("PentahoOFC4JChartHelper.WARN_BAD_CHART_FACTORY_PROPERTIES"), ignored); //$NON-NLS-1$
    }

    return chartFactories;
  }
}
