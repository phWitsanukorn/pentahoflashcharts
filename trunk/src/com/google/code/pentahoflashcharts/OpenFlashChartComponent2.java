package com.google.code.pentahoflashcharts;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import ofc4j.model.Chart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.pentaho.commons.connection.IPentahoResultSet;
import org.pentaho.platform.engine.services.solution.ComponentBase;
import org.pentaho.platform.api.engine.IActionSequenceResource;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.repository.IContentItem;
import org.pentaho.platform.api.repository.IContentLocation;
import org.pentaho.platform.api.repository.IContentRepository;
import org.pentaho.platform.api.util.XmlParseException;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.services.solution.PentahoEntityResolver;
import org.pentaho.platform.repository.content.ContentRepository;
import org.pentaho.platform.util.UUIDUtil;
import org.pentaho.platform.util.messages.LocaleHelper;
import org.pentaho.platform.util.xml.dom4j.XmlDom4JHelper;

/**
 * To integrate the open-flash-chart, we write this component.
 * This component has the following parameters:
 * chart_template-----optional,
 * chart_template_string-----optional,Contains the open flash chart template for this chart, along with the required tokens for replacing values in the chart with values from the dataset
 * chart_dataset (required),
 * chart_width,
 * chart_height,
 * ofc_url. It is the directory URL of the open flash chart .swf file.  Defaults to the pentaho base URL (style deployment)/images.
 * For instance, /pentaho-style/images. 
 * This component will use the Open Flash Chart 2 patch flash.(came from http://ofc.x10hosting.com/index.html). It can support the dual y_axis, then we can use Bar Line chart. 
 *
 * @author tom qin
 *
 */
public class OpenFlashChartComponent2 extends OpenFlashChartComponent {
	
	private static final long serialVersionUID = 825147871232139168L;
	
	protected static String flashFragment;
	static
	{
		flashFragment ="<object classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" " + 
		"codebase=\"http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=8,0,0,0\" " +
		"width=\"{chart-width}\" height=\"{chart-height}\" align=\"middle\"> " +
		"<param name=\"allowScriptAccess\" value=\"sameDomain\" /> " +
		"<param name=\"movie\" value=\"{ofc-url}/open-flash-chart-full-embedded-font.swf?data-file={data}\" /> " +
		"<param name=\"quality\" value=\"high\" /> " +
		"<embed src=\"{ofc-url}/open-flash-chart-full-embedded-font.swf?data-file={data}\" quality=\"high\" bgcolor=\"#FFFFFF\" " +
        "width=\"{chart-width}\" height=\"{chart-height}\" align=\"middle\" allowScriptAccess=\"sameDomain\" type=\"application/x-shockwave-flash\" " +
        "pluginspage=\"http://www.macromedia.com/go/getflashplayer\" /></object>";
	}
	
	protected boolean executeAction() {

		return super.executeAction();
	}
	
	/**
	 * override the super codes
	 */
	protected String getFlashFragment() {
		return flashFragment;
	}
	
	
}
