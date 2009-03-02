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
import org.pentaho.platform.plugin.action.jfreechart.ChartComponent;
import org.pentaho.platform.repository.content.ContentRepository;
import org.pentaho.platform.util.UUIDUtil;
import org.pentaho.platform.util.messages.LocaleHelper;
import org.pentaho.platform.util.xml.dom4j.XmlDom4JHelper;
import org.pentaho.platform.api.repository.ISolutionRepository;

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
 *
 * @author tom qin
 *
 */
public class OpenFlashChartComponent extends ComponentBase {

  public static final String LOCATION_DIR_PATH = "OpenFlashChart";

  private static final long serialVersionUID = 825147871232129168L;

  private static final Log log = LogFactory.getLog(OpenFlashChartComponent.class);

  private static final String CHART_DATA_PROP = "chart_dataset";

  private static final String CHART_WIDTH = "chart_width";

  private static final String CHART_HEIGHT = "chart_height";

  private static final String OFC_URL = "ofc_url";

  private static final String CHART_TEMPLATE_STRING = "chart_template_string";

  private static final String CHART_TEMPLATE = "chart_template";

  private static final String USE_PENTAHO_XML = "use_pentaho_xml";

  private static final String OTHER_HTML_TEMPLATE = "other_html_template";

  private static final String OFC_LIB_NAME = "ofc_lib_name";

  private static final String BY_ROW_PROP = "by-row"; //$NON-NLS-1$

  protected int width = 500;

  protected int height = 300;

  protected String template = null;

  protected IPentahoResultSet data = null;

  Map<String, ArrayList<String>> map = null;

  protected static String flashFragment;
  static {
    //    flashFragment =" <script type='text/javascript' src='{ofc-url}/js/swfobject.js'></script> <script type='text/javascript'>swfobject.embedSWF('{ofc-url}/open-flash-chart.swf', 'my_chart', '{chart-width}','{chart-height}','9.0.0', 'expressInstall.swf',{'data-file':'{data}'});</script><div id='my_chart'></div>";
    flashFragment = "<object classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" "
        + "codebase=\"http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=8,0,0,0\" "
        + "width=\"{chart-width}\" height=\"{chart-height}\"  id=\"ofcgraphx\" align=\"middle\"> "
        + "<param name=\"allowScriptAccess\" value=\"sameDomain\" /> "
        + "<param name=\"wmode\" value=\"opaque\">" 
        + "<param name=\"movie\" value=\"{ofc-url}/{ofc-libname}?get-data={data}\" /> "
        + "<param name=\"quality\" value=\"high\" /> "
        + "<embed src=\"{ofc-url}/{ofc-libname}?get-data={data}\" wmode=\"opaque\" quality=\"high\" bgcolor=\"#FFFFFF\" "
        + "width=\"{chart-width}\" height=\"{chart-height}\"  id=\"ofcgraph1\" align=\"middle\" allowScriptAccess=\"sameDomain\" type=\"application/x-shockwave-flash\" "
        + "pluginspage=\"http://www.macromedia.com/go/getflashplayer\" /></object>";
  }

  public OpenFlashChartComponent() {

  }

  public OpenFlashChartComponent(String template, int width, int height, IPentahoResultSet data) {
    this.template = template;
    this.width = width;
    this.height = height;
    this.data = data;
  }

  protected boolean validateAction() {
    // See if we have the chart data
    if (!isDefinedInput(CHART_DATA_PROP) && !isDefinedInput("chart-data")) {
      inputMissingError(CHART_DATA_PROP);
      return false;
    }

    //See if we have the chart definition
    if (!isDefinedInput(CHART_TEMPLATE_STRING)) {
      if (!isDefinedResource(CHART_TEMPLATE) && !isDefinedResource("chart-attributes")) {
        inputMissingError(CHART_TEMPLATE);
        return false;
      }
    }

    return true;
  }

  protected boolean executeAction() {
    log.debug("start to run open flash chart component......");
    //get the input datas
    IPentahoResultSet data = (IPentahoResultSet) getInputValue(CHART_DATA_PROP);
    if (data == null) {
      data = (IPentahoResultSet) getInputValue("chart-data");
    }
    String chartTemplateString = getInputStringValue(CHART_TEMPLATE_STRING);
    Integer chartWidth = null;
    IPentahoSession userSession = this.getSession();
    String inputWidth = getInputStringValue(CHART_WIDTH);
    if (inputWidth == null) {
      inputWidth = getInputStringValue("width");
    }

    if (inputWidth == null) {
      chartWidth = new Integer(this.width);
    } else {
      chartWidth = Integer.valueOf(inputWidth);
    }

    Integer chartHeight = null;
    String inputHeight = getInputStringValue(CHART_HEIGHT);
    if (inputHeight == null) {
      inputHeight = getInputStringValue("height");
    }

    if (null == inputHeight) {
      chartHeight = new Integer(this.height);
    } else {
      chartHeight = Integer.valueOf(inputHeight);
    }

    IActionSequenceResource fileResource = null;
    String ofcURL = getInputStringValue(OFC_URL);
    if (ofcURL == null || "".equals(ofcURL)) {
      ofcURL = "/pentaho-style/images";
    }

    String ofclibname = getInputStringValue(OFC_LIB_NAME);
    if (ofclibname == null || "".equals(ofclibname)) {
      ofclibname = "open-flash-chart-full-embedded-font.swf";
    }

    if (chartTemplateString == null || chartTemplateString.equals("")) {
      if (this.isDefinedResource(CHART_TEMPLATE)) {
        fileResource = this.getResource(CHART_TEMPLATE);
      }
      if (isDefinedResource("chart-attributes")) {
        fileResource = this.getResource("chart-attributes");
      }
      try {
        // TODO Sort out deprecated method
        chartTemplateString = PentahoSystem.get(ISolutionRepository.class, userSession).getResourceAsString(fileResource);

      } catch (IOException e) {
        error(e.getLocalizedMessage());
        return false;
      }
    }
    log.debug("chartTemplateString before replacing:" + chartTemplateString);

    //parse the chart Template String and get the parsed tokens
    map = parseString(chartTemplateString);
    chartTemplateString = replaceChartDefParams(chartTemplateString, data);

    //replace the custom variables
    ArrayList<String> customs = map.get("customs");
    log.debug("customs:" + customs);
    Set inputNames = this.getInputNames();
    for (Iterator iterator = inputNames.iterator(); iterator.hasNext();) {
      String name = (String) iterator.next();
      if (customs.contains(name)) {
        Object value = this.getInputValue(name);
        chartTemplateString = replaceLongStr(chartTemplateString, "${" + name + "}", "" + value);
        customs.remove(name);
      }

    }

    if (customs != null && customs.contains("pie_values")) {
      chartTemplateString = replacePieValues(chartTemplateString, data);

    }

    log.debug("chartTemplateString after replacing:" + chartTemplateString);

    // add inputs
    chartTemplateString = applyInputsToFormat(chartTemplateString);
    
    //		get the xml node
    Document chartDocument = null;
    try {
      chartDocument = XmlDom4JHelper.getDocFromString(chartTemplateString, new PentahoEntityResolver());
    } catch (XmlParseException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    // Add support for PentahoFlashChart XML AND Pentaho XML
    // Default is to use Pentaho XML format (PentahoOFC4JHelper)
    // If one wants to use PentahoFlashChart XML format set input variable
    // USE_PENTAHO_XML (use_pentaho_xml) to "false"
    String usePentahoXML;

    usePentahoXML = getInputStringValue(USE_PENTAHO_XML);

    Chart c;
    if ("false".equals(usePentahoXML))
      c = OFC4JHelper.convert(chartDocument, data);
    else {

      // Determine if we are going to read the chart data set by row or by column
      boolean byRow = false;
      if (getInputStringValue(BY_ROW_PROP) != null) {
        byRow = Boolean.valueOf(getInputStringValue(BY_ROW_PROP)).booleanValue();
      }

      PentahoOFC4JHelper helper = new PentahoOFC4JHelper(chartDocument, data, byRow);
      c = helper.convert();
    }

    String solutionName = this.getSolutionName();
    //uuid 
    // String uuid = "openFlashChart-" + UUIDUtil.getUUIDAsString();

    IContentRepository repository = ContentRepository.getInstance(userSession);
    IContentLocation cl = null;
    cl = repository.getContentLocationByPath(LOCATION_DIR_PATH);
    if (cl == null)
      cl = repository.newContentLocation(LOCATION_DIR_PATH, solutionName, solutionName, this.getId(), true);
    //Fix me, to create a new contentitem every time is not a good choice.
    //Maybe we can remove the older and create a new one. But I can't find the solution right now. :( 
//    IContentItem citem = cl.newContentItem(uuid, uuid, this.getActionTitle(), "txt", "text/plain", "",
//        IContentItem.WRITEMODE_OVERWRITE);

//    try {
//      //get the  outputstream and write the content into the repository.
//      OutputStream os = citem.getOutputStream(this.getActionName());
//      os.write(c.toString().getBytes(LocaleHelper.getSystemEncoding()));
//      citem.closeOutputStream();
//    } catch (IOException e) {
//      error(e.getLocalizedMessage());
//      return false;
//    }
    //replace the parameters in the flashFragment
    
    String getDataFunctionName = "getData" + UUIDUtil.getUUIDAsString().replaceAll("[^\\w]","");
    
    String flashContent = replace(getFlashFragment(), getDataFunctionName, chartWidth, chartHeight, ofcURL, ofclibname);
    
    flashContent = "<script>function " + getDataFunctionName + "() { return \"" + c.toString().replaceAll("\"", "\\\\\"") + "\";}</script>" + flashContent;
      
    
    log.debug("json string:" + c.toString());
    log.debug("html_fragment=" + flashContent);
    Set outputNames = this.getOutputNames();
    String html_fragment = null;
    if (outputNames.contains("html_fragment")) {
      html_fragment = getHtmlFragment(flashContent);
      //			this.setOutputValue("html_fragment", flashContent);
      this.setOutputValue("html_fragment", html_fragment);
    }
    if (outputNames.contains("image-tag")) {
      html_fragment = getHtmlFragment(flashContent);
      //      this.setOutputValue("html_fragment", flashContent);
      this.setOutputValue("image-tag", html_fragment);
    }

//    if (outputNames.contains("content_url")) {
//      this.setOutputValue("content_url", this.getTmpContentURL(citem.getId()));
//    }

    return true;
  }

  /**
   * to support custom complex chart
   * @param flashContent
   * @return
   */
  protected String getHtmlFragment(String flashContent) {
    return flashContent + getOtherHtmlFragment();
  }

  /**
   * to support the click event. We can add some html object as the input string in the xaction.
   * @return
   */
  protected String getOtherHtmlFragment() {
    String fragment = this.getInputStringValue(OTHER_HTML_TEMPLATE);
    if (fragment != null && fragment.length() > 0)
      return fragment.trim();
    return "";
  }

  protected String getFlashFragment() {
    return flashFragment;
  }

  /**
   * The data must obey the rule: the first column in the data2 should be the value column,
   * the second column should be the label column. For instance,
   *  select sum(actual) as "Actuals", region  from quadrant_actuals group by region
   * @param chartTemplateString
   * @param data2
   */
  private String replacePieValues(String chartTemplateString, IPentahoResultSet data2) {
    int rowCount = data2.getRowCount();
    StringBuffer sb = new StringBuffer();
    for (int j = 0; j < rowCount; j++) {
      sb.append("{");
      Object value = data2.getValueAt(j, 0);
      Object label = data2.getValueAt(j, 1);
      sb.append("\"value\":").append(value);
      sb.append(",");
      sb.append("\"label\":\"").append(label).append("\"");
      sb.append("}");

      if (j < rowCount - 1) {
        sb.append(",");
      }
    }
    log.debug("replace pie_values:" + sb.toString());
    log.debug("chartTemplateString=" + chartTemplateString);
    return replaceLongStr(chartTemplateString, "${pie_values}", sb.toString());

  }

  /**
   * 1. ${colN} will be replaced with a comma separated list of values of the column in N in the dataset.
   * For instance,x_values={col1}could be replaced with the values in the FIRST column of the data set
   * x_values=2039,193,8930,3839,1023
   * 2. ${minN} will be replaced with the minimum value in the column N in the dataset.
   * 3. ${maxN} will be replaced with the maximum value in the column N in the dataset.
   * 4. ${headN} will be replaced with the ��header value�� for the column N in the dataset.
   * @param chartTemplateString 
   * @return the clear chart definition string.
   */
  public String replaceChartDefParams(String chartTemplateString, IPentahoResultSet data) {

    ArrayList<String> columns = map.get("colN");

    ArrayList<String> maxs = map.get("maxN");
    ArrayList<String> mins = map.get("minN");

    //		if(columns.size()==0)
    //		{
    //			return chartTemplateString;
    //		}

    String[] colValues = new String[columns.size()];
    for (int i = 0; i < colValues.length; i++) {
      colValues[i] = "";
    }

    Object[] maxValues = new Object[maxs.size()];
    Object[] minValues = new Object[mins.size()];

    int rowCount = data.getRowCount();
    Object max = null;
    Object min = null;
    Object[][] objs = data.getMetaData().getColumnHeaders();
    for (int j = 0; j < rowCount; j++) {
      for (int i = 0; i < columns.size(); i++) {
        String column = columns.get(i);
        // k is from 1 to N
        int k = Integer.valueOf(column.substring(column.indexOf("col") + "col".length()));
        //get the value of colN
        Object obj = data.getValueAt(j, k - 1);
        if (null == obj) {
          continue;
        }
        if ((obj instanceof String)) // for the string value, we add "" for enclosing it
        {
          boolean isString = false;
          try {
            Double.valueOf((String) obj);
          } catch (NumberFormatException e) {
            colValues[i] = colValues[i] + "\"" + obj + "\"";
            isString = true;
          }

          if (!isString)
            colValues[i] = colValues[i] + obj;
        } else
          colValues[i] = colValues[i] + obj;

        if (j < rowCount - 1) {
          colValues[i] = colValues[i] + ",";
        }
      }

      //maxN can have more than one
      //minN can have more than one
      for (int i = 0; i < maxs.size(); i++) {
        String maxN = maxs.get(i);
        int k = Integer.valueOf(maxN.substring(maxN.indexOf("max") + "max".length()));
        Object obj = data.getValueAt(j, k - 1);

        if (null == obj) {
          continue;
        }
        if (j == 0) {

          if (maxValues.length > 0) {
            maxValues[i] = obj;
          }
          if (minValues.length > 0) {
            minValues[i] = obj;
          }

          continue;
        }
        if (obj instanceof Comparable) {
          if (maxValues.length > 0 && ((Comparable) obj).compareTo(maxValues[i]) > 0) {
            maxValues[i] = obj;
          } else if (minValues.length > 0 && ((Comparable) obj).compareTo(minValues[i]) < 0) {
            minValues[i] = obj;
          }
        }
      }

    }

    for (int i = 0; i < colValues.length; i++) {
      String values = colValues[i];
      chartTemplateString = replaceLongStr(chartTemplateString, "${" + columns.get(i) + "}", values);
    }

    //replace the maxN and minN
    for (int i = 0; i < maxs.size(); i++) {
      chartTemplateString = replaceLongStr(chartTemplateString, "${" + maxs.get(i) + "}", ""
          + ((Number) maxValues[i]).intValue());
    }

    for (int i = 0; i < mins.size(); i++) {
      chartTemplateString = replaceLongStr(chartTemplateString, "${" + mins.get(i) + "}", ""
          + ((Number) minValues[i]).intValue());
    }

    //replace headers

    if (null == objs) {
      log.debug("headers get null from dataset. we do not change the template.");
      return chartTemplateString;
    }
    Object headers[] = objs[0];
    ArrayList<String> heads = map.get("headN");
    ArrayList<String> colHeaders = new ArrayList<String>(heads.size());

    for (int i = 0; i < heads.size(); i++) {
      String head = heads.get(i);
      // k is from 1 to N
      int k = Integer.valueOf(head.substring(head.indexOf("head") + "head".length()));
      log.debug("header:" + head + " is replaced by " + headers[k - 1]);
      chartTemplateString = replaceLongStr(chartTemplateString, "${" + head + "}", "" + headers[k - 1]);
    }

    return chartTemplateString;
  }

  /**
   * Flash Template String Replacement. It is the latest replacement.
   * @param flashFragment2 the flash template
   * @param id the id of the content item
   * @param chartWidth
   * @param chartHeight
   * @param ofcURL
   * @return the working flash codes 
   */
  public String replace(String flashFragment2, String functionName, Integer chartWidth, Integer chartHeight, String ofcURL,
      String ofclibname) {
    StringBuffer buff = new StringBuffer(flashFragment2);
    //replace the {chart-width}
    String token = "{chart-width}";
    int index;
    replaceByToken(buff, "" + chartWidth, token);

    //replace the {chart-height}
    token = "{chart-height}";
    replaceByToken(buff, "" + chartHeight, token);

    //
    token = "{ofc-libname}";
    replaceByToken(buff, "" + ofclibname, token);

    //replace the {ofc-url}
    token = "{ofc-url}";
    replaceByToken(buff, ofcURL, token);

    //replace the {data}
    //String encodedDataLocation = URLEncoder.encode(getTmpContentURL(id));
    token = "{data}";
    index = buff.lastIndexOf(token);
    buff.replace(index, index + token.length(), functionName);
    //replace the {data} again
    index = buff.lastIndexOf(token);
    if (index != -1) {
      buff.replace(index, index + token.length(), functionName);

    }

    log.debug(" flash codes after replacing:" + buff.toString());
    return buff.toString();
  }

//  private String getTmpContentURL(String id) {
//    return PentahoSystem.getApplicationContext().getBaseUrl() + "GetContent?id=" + id;
//  }

  private StringBuffer replaceByToken(StringBuffer buff, String value, String token) {

    int index = buff.indexOf(token);
    buff.replace(index, index + token.length(), value);
    if (buff.indexOf(token) != -1) {
      //loop replaceByToken again until there is no token at all.
      return replaceByToken(buff, value, token);
    }
    return buff;
  }

  public void done() {
    //clear something

  }

  public boolean init() {
    // nothing to do here really
    return true;
  }

  /**
   * replace the tokens in the template.
   * The tokens should be enclosed with "${" and "}". For instance, ${col1},${head1}
   * @param template
   * @return
   */
  public static Map<String, ArrayList<String>> parseString(final String template) {
    Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
    ArrayList<String> columns = new ArrayList<String>();
    ArrayList<String> headers = new ArrayList<String>();
    ArrayList<String> maxs = new ArrayList<String>();
    ArrayList<String> mins = new ArrayList<String>();
    ArrayList<String> customs = new ArrayList<String>();

    String token1 = "${";
    String token2 = "}";
    String tmpTemplate = template;
    tmpTemplate = tmpTemplate.trim();

    int index = tmpTemplate.indexOf(token1);

    //		int index2 = tmpTemplate.indexOf(token2);
    while (index != -1) {
      tmpTemplate = tmpTemplate.substring(index);
      index = 1;
      int index2 = tmpTemplate.indexOf(token2);
      String tmpStr = tmpTemplate.substring(index + 1, index2);
      //check the illegal token
      if (tmpStr.indexOf(":") != -1 || tmpStr.indexOf(";") != -1) {
        tmpTemplate = tmpTemplate.substring(0, index - 1 >= 0 ? index - 1 : 0) + tmpTemplate.substring(index2 + 1);
        index = tmpTemplate.indexOf(token1);
        index2 = tmpTemplate.indexOf(token2);
        continue;
      }

      if (tmpStr.indexOf("col") != -1) {
        if (!columns.contains(tmpStr))
          columns.add(tmpStr);
      } else if (tmpStr.indexOf("head") != -1) {
        if (!headers.contains(tmpStr))
          headers.add(tmpStr);
      } else if (tmpStr.indexOf("max") != -1) {
        if (!maxs.contains(tmpStr))
          maxs.add(tmpStr);
      } else if (tmpStr.indexOf("min") != -1) {
        if (!mins.contains(tmpStr))
          mins.add(tmpStr);
      } else //any other my custom variable
      {
        if (!customs.contains(tmpStr))
          customs.add(tmpStr);
      }

      tmpTemplate = tmpTemplate.substring(0, index - 1 >= 0 ? index - 1 : 0) + tmpTemplate.substring(index2 + 1);
      index = tmpTemplate.indexOf(token1);
      index2 = tmpTemplate.indexOf(token2);
    }

    map.put("colN", columns);
    map.put("headN", headers);
    map.put("maxN", maxs);
    map.put("minN", mins);
    map.put("customs", customs);
    return map;
  }

  public static String replaceLongStr(String str, String fromStr, String toStr) {
    StringBuffer result = new StringBuffer();
    if (str != null && !str.equals("")) {
      while (str.indexOf(fromStr) != -1) {
        result.append(str.substring(0, str.indexOf(fromStr)));
        result.append(toStr);
        str = str.substring(str.indexOf(fromStr) + fromStr.length());
        //	        System.out.println(str);
      }
      result.append(str);
    }
    log.debug("fromStr:" + fromStr + ",toStr:" + toStr + "\n\r result:" + result.toString());
    System.out.println("fromStr:" + fromStr + ",toStr:" + toStr + "\n\r result:" + result.toString());
    return result.toString();
  }

  public static void main(String[] args) {
    OpenFlashChartComponent test = new OpenFlashChartComponent();
    String a = "<![CDATA[&is_thousand_separator_disabled=false&&title=Actuals By Region,{font-size:18px; color: #d01f3c}& &x_axis_steps=1& &y_ticks=5,10,5& &line=3,#87421F& &y_min=0& &y_max=20& &pie=60,#505050,{font-size: 12px; color: #404040;}& &values=${col1}& &pie_labels=${head1}${head1}& &colours=#d01f3c,#356aa0,#C79810& &links=& &tool_tip=%23val%23&${pie_values}]]></default-value>";
    //		String token1 = "${";
    //		String token2 = "}";
    ////		int index = a.indexOf(token1);

    //		int index2 = 0;
    //		if(index!=-1)
    //		{
    //			a=a.substring(index);
    //		}
    //		index2=a.indexOf(token2);
    //		String tmpStr = a.substring(index+1,index2);
    //		System.out.println(tmpStr);
    //		System.out.println(a.substring(0,index-1>=0?index-1:0)+a.substring(index2+1));
    //		System.out.println(parseString(a));
    String t = "col1";
    //		System.out.println(t.substring(t.indexOf("col")+"col".length()));
    System.out.println(replaceLongStr(a, "${pie_values}", "abc"));
    //		System.out.println(test.replaceByToken(new StringBuffer(a),"{head1}","Header1"));
    File f = new File("D:\\docs\\openflashchart\\data.json");

  }

  public boolean validateSystemSettings() {
    // TODO Auto-generated method stub
    return true;
  }

  public Log getLogger() {
    // TODO Auto-generated method stub
    return log;
  }

}
