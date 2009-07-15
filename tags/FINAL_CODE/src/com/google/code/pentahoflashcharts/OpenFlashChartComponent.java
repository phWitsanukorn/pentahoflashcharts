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
package com.google.code.pentahoflashcharts;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;
import org.pentaho.platform.api.engine.IActionSequenceResource;
import org.pentaho.platform.api.util.XmlParseException;
import org.pentaho.platform.engine.services.runtime.TemplateUtil;
import org.pentaho.platform.engine.services.solution.ComponentBase;
import org.pentaho.platform.engine.services.solution.PentahoEntityResolver;
import org.pentaho.platform.plugin.action.messages.Messages;
import com.google.code.pentahoflashcharts.charts.PentahoOFC4JChartHelper;
import org.pentaho.platform.util.UUIDUtil;
import org.pentaho.platform.util.xml.dom4j.XmlDom4JHelper;

/**
 * This component is a contribution from Nick Goodman and Tom Qin,
 * it conforms to the ChartComponent API.  Please see the wiki for details on this
 * component.
 * 
 * The component uses open-flash-chart-full-embedded-font.swf, found at
 * http://www.ofc2dz.com/, which is a patched version of Open Flash Chart 2, found at
 * http://teethgrinder.co.uk/open-flash-chart-2/
 * 
 * note: onclick events are only partially implemented, due to limitations of
 * OFC.  In future releases of OFC, this should be revisited.
 * 
 * Backlog:
 * - y2_legend - barline right axis title, would need to update OFC4J
 * - horizontal stacked bars
 * http://www.ofc2dz.com/OFC2/examples/HorizontalStackedBars.html
 * - styled stacked bars (would require impl in OFC)
 * - dial chart (would require impl in OFC)
 * - XY Line Chart
 * - XY Area Chart
 * - onclick / link support (would require enhancements to OFC) 
 * - allow override of dataFunction name
 * - review colors, move into config file for both jfree and ofc?
 * - area stacked
 * - bold font on y axis legend, requires change to OFC swf
 * - disable x axis grid lines, control thickness? both would require enhancements to OFC
 * - 
 *
 * @author Nick Goodman
 * @author Tom Qin
 * @author Will Gorman (wgorman@pentaho.com)
 */
public class OpenFlashChartComponent extends ComponentBase {

  private static final long serialVersionUID = 825147871232129168L;

  private static final Log log = LogFactory.getLog(OpenFlashChartComponent.class);

  private static final String DEFAULT_FLASH_LOC = "content/openflashchart"; //$NON-NLS-1$
  
  private static final String DEFAULT_FLASH_SWF = "open-flash-chart-full-embedded-fontDZ.swf"; //$NON-NLS-1$s
  
  private static final String CHART_DATA = "chart-data"; //$NON-NLS-1$

  private static final String CHART_WIDTH = "width"; //$NON-NLS-1$

  private static final String CHART_HEIGHT = "height"; //$NON-NLS-1$

  private static final String OFC_URL = "ofc_url"; //$NON-NLS-1$

  // Static declarations
  public static String CHART_NODE_LOC = "chart"; //$NON-NLS-1$
  
  private static final String CHART_ATTRIBUTES = "chart-attributes"; //$NON-NLS-1$

  private static final String OFC_LIB_NAME = "ofc_lib_name"; //$NON-NLS-1$
  
  private static final String USE_PENTAHO_XML = "use_pentaho_xml"; //$NON-NLS-1$
  
  private static final String DEFAULT_USE_PENTAHO_XML = "true";

  private static final String BY_ROW_PROP = "by-row"; //$NON-NLS-1$

  private static final String DEFAULT_WIDTH = "100%";

  private static final String DEFAULT_HEIGHT = "100%";
  
  

  protected String template = null;

  protected IPentahoResultSet data = null;

  protected static String flashFragment =
        "<script>function {dataFunction}() { return \"{chartJson}\";}</script>" //$NON-NLS-1$
        + "<object classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" " //$NON-NLS-1$
        + "codebase=\"http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=8,0,0,0\" " //$NON-NLS-1$
        + "width=\"{chart-width}\" height=\"{chart-height}\"  id=\"ofco{chartId}\" align=\"middle\"> " //$NON-NLS-1$
        + "<param name=\"allowScriptAccess\" value=\"sameDomain\" /> " //$NON-NLS-1$
        + "<param name=\"wmode\" value=\"opaque\">"  //$NON-NLS-1$
        + "<param name=\"movie\" value=\"{ofc-url}/{ofc-libname}?get-data={dataFunction}\" /> " //$NON-NLS-1$
        + "<param name=\"quality\" value=\"high\" /> " //$NON-NLS-1$
        + "<embed src=\"{ofc-url}/{ofc-libname}?get-data={dataFunction}\" wmode=\"opaque\" quality=\"high\" bgcolor=\"#FFFFFF\" " //$NON-NLS-1$
        + "width=\"{chart-width}\" height=\"{chart-height}\"  id=\"ofce{chartId}\" align=\"middle\" allowScriptAccess=\"sameDomain\" type=\"application/x-shockwave-flash\" " //$NON-NLS-1$
        + "pluginspage=\"http://www.macromedia.com/go/getflashplayer\" /></object>"; //$NON-NLS-1$

  public OpenFlashChartComponent() {
    // default constructor
  }

  protected boolean validateAction() {
    // See if we have the chart data
    if (!isDefinedInput(CHART_DATA)) {
      inputMissingError(CHART_DATA);
      return false;
    }

    // See if we have chart attributes
    if (!isDefinedInput(CHART_ATTRIBUTES)) {
      if (!isDefinedResource(CHART_ATTRIBUTES)) {
        inputMissingError(CHART_ATTRIBUTES);
        return false;
      }
    }

    return true;
  }

  protected boolean executeAction() {
    log.debug("start to run open flash chart component......"); //$NON-NLS-1$
    
    // input data
    
    IPentahoResultSet data = (IPentahoResultSet) getInputValue(CHART_DATA);

    // chart width
    
    String chartWidth = null;
    String inputWidth = getInputStringValue(CHART_WIDTH);

    if (inputWidth == null) {
      chartWidth = DEFAULT_WIDTH;
    } else {
      chartWidth = inputWidth;
    }

    // chart height
    
    String chartHeight = null;
    String inputHeight = getInputStringValue(CHART_HEIGHT);

    if (null == inputHeight) {
      chartHeight = DEFAULT_HEIGHT;
    } else {
      chartHeight = inputHeight;
    }

    // swf file location
    
    String ofcURL = getInputStringValue(OFC_URL);
    if (ofcURL == null || "".equals(ofcURL)) { //$NON-NLS-1$
      ofcURL = DEFAULT_FLASH_LOC;
    }

    // swf file name
    
    String ofclibname = getInputStringValue(OFC_LIB_NAME);
    if (ofclibname == null || "".equals(ofclibname)) { //$NON-NLS-1$
      ofclibname = DEFAULT_FLASH_SWF;
    }
    
    // use_pentaho_xml
    String use_pentaho_xml = getInputStringValue(USE_PENTAHO_XML);
    if (use_pentaho_xml == null || "".equals(use_pentaho_xml)) { //$NON-NLS-1$
      use_pentaho_xml = DEFAULT_USE_PENTAHO_XML;
    }
   
    
    // chart definition
    
    String chartAttributeString = null;
    if (getInputNames().contains(CHART_ATTRIBUTES)) {
      chartAttributeString = getInputStringValue(CHART_ATTRIBUTES);
    } else if (isDefinedResource(CHART_ATTRIBUTES)) {
      IActionSequenceResource resource = getResource(CHART_ATTRIBUTES);
      chartAttributeString = getResourceAsString(resource);
    }

    Node chartNode = null;
    
    if (chartAttributeString != null) {

      // apply any additional inputs to the chart definition
      
      chartAttributeString = applyInputsToFormat(chartAttributeString);
      
      // parse the xml
      
      try {
        Document chartDocument = XmlDom4JHelper.getDocFromString(chartAttributeString, new PentahoEntityResolver());
        
        chartNode = chartDocument.selectSingleNode(CHART_NODE_LOC);
        if (chartNode == null) {
          chartNode = chartDocument.selectSingleNode(CHART_ATTRIBUTES);
        }
  
      } catch (XmlParseException e) {
        getLogger().error(Messages.getErrorString("OpenFlashChartComponent.ERROR_0001_CANT_DOCUMENT_FROM_STRING"), e);//$NON-NLS-1$
        return false;
      }
    } else {

      // see if the chart-attributes node is available in the component definition
      
      chartNode = getComponentDefinition(true).selectSingleNode(CHART_ATTRIBUTES);
    }
    
    // if the chart def isn't available, exit
    
    if (chartNode == null) {
      getLogger().error(Messages.getErrorString("OpenFlashChartComponent.ERROR_0002_CHART_DEFINITION_NOT_FOUND"));//$NON-NLS-1$
      return false;
    }
    
    // Determine if we are going to read the chart data set by row or by column
    boolean byRow = false;
    if (getInputStringValue(BY_ROW_PROP) != null) {
      byRow = Boolean.valueOf(getInputStringValue(BY_ROW_PROP)).booleanValue();
    }

   
    
    // set Helper classname based on use_pentaho_xml
    String helperclassname;
    if ( "false".equals(use_pentaho_xml) )
    	helperclassname = "com.google.code.pentahoflashcharts.charts.pfcxml.OFC4JHelper";
    else if ( "true".equals(use_pentaho_xml)) 
    	helperclassname = "com.google.code.pentahoflashcharts.charts.PentahoOFC4JChartHelper";
    else {
		getLogger().error(Messages.getErrorString("OpenFlashChartComponent.ERROR_0003_CHARTHELPER_NOT_FOUND"));
		return false;
    }
    	

    // Use reflection to access static methods
    String chartJson = "";
	try {
		Class c = Class.forName(helperclassname);
	    
	    Method m = c.getMethod("generateChartJson", new Class[] {Node.class, IPentahoResultSet.class, boolean.class, Log.class});
		chartJson = (String) m.invoke(null, new Object[] {chartNode, data, byRow, log});
		
	} catch (Exception e) {
		getLogger().error(Messages.getErrorString("OpenFlashChartComponent.ERROR_0003_CHARTHELPER_NOT_FOUND"), e);
		return false;
	}
    

    // generate a unique name for the function
    
    String chartId = UUIDUtil.getUUIDAsString().replaceAll("[^\\w]",""); //$NON-NLS-1$ //$NON-NLS-2$
    
    // populate the flash html template
    
    Properties props = new Properties();
    props.setProperty("chartId", chartId); //$NON-NLS-1$
    props.setProperty("dataFunction", "getData" + chartId); //$NON-NLS-1$ //$NON-NLS-2$
    props.setProperty("chart-width", chartWidth); //$NON-NLS-1$
    props.setProperty("chart-height", chartHeight); //$NON-NLS-1$
    props.setProperty("ofc-url", ofcURL); //$NON-NLS-1$
    props.setProperty("ofc-libname", ofclibname); //$NON-NLS-1$
    props.setProperty("chartJson", chartJson.replaceAll("\"", "\\\\\"")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    
    String flashContent = TemplateUtil.applyTemplate(getFlashFragment(), props, null);
    
    // set output value
    
    log.debug("html_fragment=" + flashContent); //$NON-NLS-1$
    
    if (this.isDefinedOutput("html_fragment")) { //$NON-NLS-1$
      this.setOutputValue("html_fragment", flashContent); //$NON-NLS-1$
    }
    if (this.isDefinedOutput("image-tag")) { //$NON-NLS-1$
      this.setOutputValue("image-tag", flashContent); //$NON-NLS-1$
    }

    return true;
  }

  protected String getFlashFragment() {
    return flashFragment;
  }

  public void done() {
    // nothing to do here
  }

  public boolean init() {
    // nothing to do here
    return true;
  }

  public boolean validateSystemSettings() {
    return true;
  }

  public Log getLogger() {
    return log;
  }

}
