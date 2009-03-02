package com.google.code.pentahoflashcharts;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ofc4j.model.Chart;
import ofc4j.model.Text;
import ofc4j.model.axis.XAxis;
import ofc4j.model.axis.YAxis;
import ofc4j.model.elements.AreaHollowChart;
import ofc4j.model.elements.AreaLineChart;
import ofc4j.model.elements.BarChart;
import ofc4j.model.elements.Element;
import ofc4j.model.elements.HorizontalBarChart;
import ofc4j.model.elements.LineChart;
import ofc4j.model.elements.PieChart;
import ofc4j.model.elements.ScatterChart;
import ofc4j.model.elements.SketchBarChart;
import ofc4j.model.elements.StackedBarChart;
import ofc4j.model.elements.BarChart.Style;
import ofc4j.model.elements.StackedBarChart.Stack;
import ofc4j.model.elements.StackedBarChart.StackKey;
import ofc4j.model.elements.StackedBarChart.StackValue;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.pentaho.commons.connection.IPentahoResultSet;
import org.pentaho.commons.connection.memory.MemoryResultSet;

import com.google.code.pentahoflashcharts.builder.BarLineChartBuilder;

public class PentahoOFC4JHelper {

  // Static declarations
  public static String CHART_NODE_LOC = "chart";

  public static String ARG = "$ARG";

  public static String CSS_FONT_SIZE = " font-size: $ARGpx;";

  public static String CSS_FONT_FAMILY = " font-family: $ARG;";

  public static String CSS_FONT_WEIGHT = " font-weight: $ARG;";

  public static String CSS_FONT_STYLE = " font-style: $ARG";

  // assume starting at the "Chart" node
  // /chart/title
  // /chart/range-title
  public static String TITLE_NODE_LOC = "title";

  public static String TITLE_FONT_NODE_LOC = "title-font";

  public static String RANGE_TITLE_NODE_LOC = "range-title";

  public static String RANGE_TITLE_FONT_NODE_LOC = "range-title-font";

  public static String LINES_RANGE_TITLE_NODE_LOC = "lines-range-title";

  public static String LINES_RANGE_TITLE_FONT_NODE_LOC = "lines-range-title-font";

  public static String DOMAIN_TITLE_NODE_LOC = "domain-title";

  public static String DOMAIN_TITLE_FONT_NODE_LOC = "domain-title-font";

  public static String IS3D_NODE_LOC = "is-3D";

  public static String ISGLASS_NODE_LOC = "is-glass";

  public static String ISSKETCH_NODE_LOC = "is-sketch";
  
  public static String ISSTACKED_NODE_LOC = "is-stacked";

  public static String DATASET_TYPE_NODE_LOC = "dataset-type";

  public static String CHART_TYPE_NODE_LOC = "chart-type";

  public static String COLOR_PALETTE_NODE_LOC = "color-palette";
  public static String OUTLINE_COLOR_PALETTE_NODE_LOC = "outline-color-palette";

  public static String RANGE_MAXIMUM_NODE_LOC = "range-maximum";

  public static String RANGE_MINIMUM_NODE_LOC = "range-minimum";

  public static String DOMAIN_MAXIMUM_NODE_LOC = "domain-maximum";

  public static String DOMAIN_MINIMUM_NODE_LOC = "domain-minimum";
  
  public static String LINES_RANGE_MAXIMUM_NODE_LOC = "lines-range-maximum";

  public static String LINES_RANGE_MINIMUM_NODE_LOC = "lines-range-minimum";

  public static String ORIENTATION_NODE_LOC = "orientation";

  public static String PLOT_BACKGROUND_NODE_LOC = "plot-background";

  public static String PLOT_BACKGROUND_COLOR_XPATH = "@type"; //att of plot-background

  public static String CHART_BACKGROUND_NODE_LOC = "chart-background";

  public static String CHART_BACKGROUND_COLOR_XPATH = "@type"; //att of plot-background

  public static String URL_TEMPLATE_NODE_LOC = "url-template";

  public static String DOTSTYLE_NODE_LOC = "dot-style";
  public static String DOT_WIDTH_NODE_LOC = "dot-width";

  // assume starting at "color-palette" node
  // /color-palette/color
  public static String COLOR_NODE_LOC = "color";

  // assume starting at a "*-title-font" node
  public static String FONT_FAMILY_NODE_LOC = "font-family";

  public static String FONT_SIZE_NODE_LOC = "size";

  public static String FONT_BOLD_NODE_LOC = "is-bold";

  public static String FONT_ITALIC_NODE_LOC = "is-italic";
  
  public static String TOOLTIP_NODE_LOC = "tooltip";

  // Default values
  public static String CSS_FONT_FAMILY_DEFAULT = "Ariel";

  public static String CSS_FONT_SIZE_DEFAULT = "14";

  public static String DATASET_TYPE_DEFAULT = "CategoryDataset";

  public static String CSS_FONT_WEIGHT_DEFAULT = "normal";

  public static String CSS_FONT_STYLE_DEFAULT = "normal";

  public static String CHART_TYPE_DEFAULT = "BarChart";

  public static Style BARCHART_STYLE_DEFAULT = BarChart.Style.NORMAL;

  public static LineChart.Style LINECHART_STYLE_DEFAULT = LineChart.Style.NORMAL;

  public static String ORIENTATION_DEFAULT = "vertical";

  public static int SKETCH_FUNFACTOR_DEFAULT = 5;

  public static String[] COLORS_DEFAULT = { "#006666", "#0066CC", "#009999", "#336699", "#339966", "#3399FF",
      "#663366", "#666666", "#666699", "#669999", "#6699CC", "#66CCCC", "#993300", "#999933", "#999966", "#999999",
      "#9999CC", "#9999FF", "#99CC33", "#99CCCC", "#99CCFF", "#CC6600", "#CC9933", "#CCCC33", "#CCCC66", "#CCCC99",
      "#CCCCCC", "#FF9900", "#FFCC00", "#FFCC66" };

  // Chart Type Values (CHARTTUPE_NODE_LOC)
  public static String BARCHART_TYPE = "BarChart";

  public static String LINECHART_TYPE = "LineChart";

  public static String PIECHART_TYPE = "PieChart";

  public static String AREACHART_TYPE = "AreaChart";

  public static String BARLINECHART_TYPE = "BarLineChart";
  
  public static String BUBBLECHART_TYPE = "BubbleChart";
  
  public static String DOTCHART_TYPE = "DotChart";

  // Orientation Type Values (ORIENTATION_NODE_LOC)
  public static String HORIZONTAL_ORIENTATION = "horizontal";

  public static String VERTICAL_ORIENTATION = "vertical";

  // Dataset Type Values
  public static String CATEGORY_TYPE = "CategoryDataset";
  
  public static String XY_TYPE = "XYSeriesCollection";
  
  public static String XYZ_TYPE = "XYZSeriesCollection";

  // *-background Type Values
  public static String COLOR_TYPE = "color";

  // Private static members
  private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");

  // Private members 
  private IPentahoResultSet data;

  private boolean byRow;

  private Node chartNode;

  private ExtendedChart ec;

  private Chart c;

  private String datasetType;

  private String chartType;

  private String orientation;

  private ArrayList<Element> elements;

  private ArrayList<String> colors;
  private ArrayList<String> outlineColors;

  private BarChart.Style barchartstyle;

  private boolean animate;
  private Integer startAngle;
  
  private LineChart.Style linechartstyle;
  private Integer linechartwidth;
  private Integer dotwidth;

  private boolean issketch;
  
  private boolean isstacked = false;

  private String baseURLTemplate;
  
  private Integer sketchBarFunFactor;
  private Integer threedheight;
  
  private String tooltipText;

  public static IPentahoResultSet test_setupdata() {
    IPentahoResultSet ips = null;

    ArrayList<String> colHeaders = new ArrayList();

    colHeaders.add(0, "DEPARTMENT");
    colHeaders.add(1, "ACTUAL");
    colHeaders.add(2, "BUDGET");

    ArrayList r1 = new ArrayList();
    r1.add("Sales");
    r1.add(11);
    r1.add(12);
    ArrayList r2 = new ArrayList();
    r2.add("Finance");
    r2.add(14);
    r2.add(-9);
    ArrayList r3 = new ArrayList();
    r3.add("Human Resource");
    r3.add(7);
    r3.add(100);

    ArrayList data = new ArrayList();
    data.add(r1);
    data.add(r2);
    data.add(r3);

    ips = MemoryResultSet.createFromLists(colHeaders, data);

    System.out.println(ips.getRowCount());

    return ips;
  }

  public static void main(String[] args) {
    SAXReader xmlReader = new SAXReader();

    Document doc = null;
    try {
      doc = xmlReader
          .read("/Users/ngoodman/dev/workspace/pentahoflashcharts/solutions/openflashchart/charts/barchart.xml");
    } catch (DocumentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return;
    }

    System.out.println(doc.asXML());

    IPentahoResultSet testdata = test_setupdata();

    PentahoOFC4JHelper testing = new PentahoOFC4JHelper(doc, testdata, false);

    //java.lang.Thread.sleep(0);
    Chart testChart = testing.convert();
    try {
      // Create file 
      FileWriter fstream = new FileWriter(
          "/Users/ngoodman/pentaho/biserver-ce-2.0.0.stable/tomcat/webapps/ofc/testoutput1.json");
      BufferedWriter out = new BufferedWriter(fstream);
      out.write(testChart.toString());
      //Close the output stream
      out.close();
    } catch (Exception e) {//Catch exception if any
      System.err.println("Error: " + e.getMessage());
    }

    System.out.println(testChart.toString());

    // Line Chart

    try {
      doc = xmlReader
          .read("/Users/ngoodman/dev/workspace/pentahoflashcharts/solutions/openflashchart/charts/linechart.xml");
    } catch (DocumentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return;
    }
    testing = new PentahoOFC4JHelper(doc, testdata, false);

    testChart = testing.convert();
    try {
      // Create file 
      FileWriter fstream = new FileWriter(
          "/Users/ngoodman/pentaho/biserver-ce-2.0.0.stable/tomcat/webapps/ofc/testoutput2.json");
      BufferedWriter out = new BufferedWriter(fstream);
      out.write(testChart.toString());
      //Close the output stream
      out.close();
    } catch (Exception e) {//Catch exception if any
      System.err.println("Error: " + e.getMessage());
    }

    System.out.println(testChart.toString());

    // Pie Chart

    try {
      doc = xmlReader
          .read("/Users/ngoodman/dev/workspace/pentahoflashcharts/solutions/openflashchart/charts/piechart.xml");
    } catch (DocumentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return;
    }
    testing = new PentahoOFC4JHelper(doc, testdata, false);

    testChart = testing.convert();
    try {
      // Create file 
      FileWriter fstream = new FileWriter(
          "/Users/ngoodman/pentaho/biserver-ce-2.0.0.stable/tomcat/webapps/ofc/testoutput3.json");
      BufferedWriter out = new BufferedWriter(fstream);
      out.write(testChart.toString());
      //Close the output stream
      out.close();
    } catch (Exception e) {//Catch exception if any
      System.err.println("Error: " + e.getMessage());
    }

    System.out.println(testChart.toString());

    //			  Horizontal Bar Chart

    try {
      doc = xmlReader
          .read("/Users/ngoodman/dev/workspace/pentahoflashcharts/solutions/openflashchart/charts/barchart_horizontal.xml");
    } catch (DocumentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return;
    }
    testing = new PentahoOFC4JHelper(doc, testdata, false);

    testChart = testing.convert();
    try {
      // Create file 
      FileWriter fstream = new FileWriter(
          "/Users/ngoodman/pentaho/biserver-ce-2.0.0.stable/tomcat/webapps/ofc/testoutput4.json");
      BufferedWriter out = new BufferedWriter(fstream);
      out.write(testChart.toString());
      //Close the output stream
      out.close();
    } catch (Exception e) {//Catch exception if any
      System.err.println("Error: " + e.getMessage());
    }

    System.out.println(testChart.toString());

    //					  Area Bar Chart

    try {
      doc = xmlReader
          .read("/Users/ngoodman/dev/workspace/pentahoflashcharts/solutions/openflashchart/charts/areachart.xml");
    } catch (DocumentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return;
    }
    testing = new PentahoOFC4JHelper(doc, testdata, false);

    testChart = testing.convert();
    try {
      // Create file 
      FileWriter fstream = new FileWriter(
          "/Users/ngoodman/pentaho/biserver-ce-2.0.0.stable/tomcat/webapps/ofc/testoutput5.json");
      BufferedWriter out = new BufferedWriter(fstream);
      out.write(testChart.toString());
      //Close the output stream
      out.close();
    } catch (Exception e) {//Catch exception if any
      System.err.println("Error: " + e.getMessage());
    }

    System.out.println(testChart.toString());

  }

  public static class ExtendedChart extends Chart {
    private String innerBackground;

    private boolean legendVisible;

    private Chart c;

    public ExtendedChart(Chart c) {
      this.c = c;
    }

//    public void setInnerBackground(String innerBackground) {
//      this.innerBackground = innerBackground;
//    }

    public void setLegendVisible(boolean legendVisible) {
      this.legendVisible = legendVisible;
    }

    public String toString() {
      String str = c.toString();
      // str = str.replaceAll("grid_colour", "grid-colour");
      // inner background is not yet supported in the actual flash\
//      String append = "{";
//      if (innerBackground != null) {
//        append += "\"inner_bg_colour\":\"" + innerBackground + "\",";
//      }
//      if (legendVisible) {
//        append += "\"legend\":{\"position\":\"right\", \"visible\":true},";
//      }
//
//      str = append + str.substring(1);
      return str;
    }
  }

  public PentahoOFC4JHelper(Document doc, IPentahoResultSet data, boolean byRow) {
    this.byRow = byRow;
    this.data = data;
    this.c = new Chart();
    this.ec = new ExtendedChart(c);
    elements = new ArrayList<Element>();
    colors = new ArrayList<String>();
    outlineColors = new ArrayList<String>();

    this.chartNode = doc.selectSingleNode(CHART_NODE_LOC);

  }

  public Chart convert() {

    // These things apply to pretty much all charts
    setupDataAndType();
    setupColors();
    setupStyles();
    setupDomain();
    setupOnclick();

    // Build the elements (usually Chart Type specific)
    createElements();

    // Setup a few additional things before adding elements
    setupTitles();
    setupRange();

    c.addElements(elements);

    return ec;

  }

  public void setupOnclick() {

    Node urlTemplateNode = chartNode.selectSingleNode(URL_TEMPLATE_NODE_LOC);

    if (getValue(urlTemplateNode) != null) {
      baseURLTemplate = getValue(urlTemplateNode);
    }

  }

  private void setupTitles() {

    // in the Pentaho chart, range-title equals yLengend title
    Node rangeTitle = chartNode.selectSingleNode(RANGE_TITLE_NODE_LOC);
    Node rangeTitleFont = chartNode.selectSingleNode(RANGE_TITLE_FONT_NODE_LOC);
    Node title = chartNode.selectSingleNode(TITLE_NODE_LOC);

    // in the Pentaho chart, domain-title equals xLengend title
    Node domainTitle = chartNode.selectSingleNode(DOMAIN_TITLE_NODE_LOC);
    Node domainTitleFont = chartNode.selectSingleNode(DOMAIN_TITLE_FONT_NODE_LOC);
    Node titleFont = chartNode.selectSingleNode(TITLE_FONT_NODE_LOC);

    Text titleText = new Text();

    if (getValue(title) != null) {
      titleText.setText(getValue(title));
    } else {
      // TODO Figure out a default
      titleText.setText("Title");
    }
    titleText.setStyle(buildCSSStringFromNode(titleFont));

    Text domainText = new Text();
    if (getValue(domainTitle) != null) {
      domainText.setText(getValue(domainTitle));
    } else {
      // TODO figure out what to do if the header isn't CategoryDataset
      domainText.setText(data.getMetaData().getColumnHeaders()[0][0].toString());
    }
    domainText.setStyle(buildCSSStringFromNode(domainTitleFont));

    Text rangeText = new Text();
    if (getValue(rangeTitle) != null) {
      rangeText.setText(getValue(rangeTitle));
    } else {
      // TODO set it to ??
      rangeText.setText("Range Title");
    }
    rangeText.setStyle(buildCSSStringFromNode(rangeTitleFont));

    c.setYLegend(rangeText);
    // need to support YRightLegend, exposed as y2_legend in open flash charts
    
    c.setXLegend(domainText);
    c.setTitle(titleText);

  }

  public static String buildCSSStringFromNode(Node n) {

    String fontFamily = null;
    String fontSize = null;
    String fontWeight = null;
    String fontStyle = null;

    if (n != null) {
      Node fontFamilyNode = n.selectSingleNode(FONT_FAMILY_NODE_LOC);
      fontFamily = getValue(fontFamilyNode);
      Node fontSizeNode = n.selectSingleNode(FONT_SIZE_NODE_LOC);
      fontSize = getValue(fontSizeNode);
      Node fontBoldNode = n.selectSingleNode(FONT_BOLD_NODE_LOC);
      if (fontBoldNode != null && "true".equals(fontBoldNode.getText().trim())) {
        fontWeight = "bold";
      }
      Node fontItalicNode = n.selectSingleNode(FONT_ITALIC_NODE_LOC);
      if (fontItalicNode != null && "true".equals(fontItalicNode.getText().trim())) {
        fontStyle = "italic";
      }
    }
    return buildCSSString(fontFamily, fontSize, fontWeight, fontStyle);

  }

  public static String getValue(Node n) {

    if (n != null && n.getText() != null && n.getText().length() > 0) {
      return n.getText().trim();
    } else {
      return null;
    }

  }

  public static String buildCSSString(String fontname, String fontsize, String fontweight, String fontstyle) {
    StringBuffer sb = new StringBuffer();

    if (null != fontname)
      sb.append(CSS_FONT_FAMILY.replace(ARG, fontname));
    else
      sb.append(CSS_FONT_FAMILY.replace(ARG, CSS_FONT_FAMILY_DEFAULT));

    if (null != fontsize)
      sb.append(CSS_FONT_SIZE.replace(ARG, fontsize));
    else
      sb.append(CSS_FONT_SIZE.replace(ARG, CSS_FONT_SIZE_DEFAULT));

    if (null != fontweight)
      sb.append(CSS_FONT_WEIGHT.replace(ARG, fontweight));
    else
      sb.append(CSS_FONT_WEIGHT.replace(ARG, CSS_FONT_WEIGHT_DEFAULT));

    if (null != fontstyle)
      sb.append(CSS_FONT_STYLE.replace(ARG, fontstyle));
    else
      sb.append(CSS_FONT_STYLE.replace(ARG, CSS_FONT_STYLE_DEFAULT));

    return sb.toString();

  }

  public void setupDataAndType() {

    Node temp = chartNode.selectSingleNode(DATASET_TYPE_NODE_LOC);
    if (getValue(temp) != null) {
      datasetType = getValue(temp);
    } else {
      // Default is CategoricalDataset
      datasetType = DATASET_TYPE_DEFAULT;
    }

    temp = chartNode.selectSingleNode(CHART_TYPE_NODE_LOC);

    if (getValue(temp) != null) {
      chartType = getValue(temp);
    } else {
      // This should NEVER happen.
      chartType = CHART_TYPE_DEFAULT;
    }

  }

  /**
   * Setup colors for the series and also background
   *
   */
  public void setupColors() {

    Node temp = chartNode.selectSingleNode(COLOR_PALETTE_NODE_LOC);
    if (temp != null) {
      Object[] colorNodes = temp.selectNodes(COLOR_NODE_LOC).toArray();

      for (int j = 0; j < colorNodes.length; j++) {
        colors.add(getValue((Node) colorNodes[j]));
      }

    } else {
      for (int i = 0; i < COLORS_DEFAULT.length; i++) {
        colors.add(COLORS_DEFAULT[i]);
      }
    }
    
    temp = chartNode.selectSingleNode(OUTLINE_COLOR_PALETTE_NODE_LOC);
    if (temp != null) {
      Object[] colorNodes = temp.selectNodes(COLOR_NODE_LOC).toArray();

      for (int j = 0; j < colorNodes.length; j++) {
        outlineColors.add(getValue((Node) colorNodes[j]));
      }

    } else {
      for (int i = 0; i < COLORS_DEFAULT.length; i++) {
        outlineColors.add(COLORS_DEFAULT[i]);
      }
    }


    // Use either chart-background or plot-background (chart takes precendence)
    temp = chartNode.selectSingleNode(PLOT_BACKGROUND_NODE_LOC);
    if (getValue(temp) != null) {
      String type = temp.valueOf(PLOT_BACKGROUND_COLOR_XPATH);
      if (type != null && COLOR_TYPE.equals(type)) {
        c.setBackgroundColour(getValue(temp));
        c.setInnerBackgroundColour(getValue(temp));
      }
    }
    temp = chartNode.selectSingleNode(CHART_BACKGROUND_NODE_LOC);
    if (getValue(temp) != null) {
      String type = temp.valueOf(CHART_BACKGROUND_COLOR_XPATH);
      if (type != null && COLOR_TYPE.equals(type))
        c.setBackgroundColour(getValue(temp));
    }

  }

  public void setupStyles() {

    if (BARCHART_TYPE.equals(chartType))
      setupBarStyles();
    if (LINECHART_TYPE.equals(chartType))
      setupLineStyles();
    if (BARLINECHART_TYPE.equals(chartType)) {
      setupBarStyles();
      setupLineStyles();
    } else if (PIECHART_TYPE.equals(chartType)) {
      setupPieStyles();
    }
    
    Node temp = chartNode.selectSingleNode(TOOLTIP_NODE_LOC);
    if (getValue(temp) != null) {
      tooltipText = getValue(temp);
    }
  }

  public void setupLineRange() {
    int rangeMin = 0;
    int rangeMax = 100;
    int steps = 9;

    String rangeColor = "#000000";
    String rangeGridColor = "#aaaaaa";
    int rangeStroke = 1;

    if (CATEGORY_TYPE.equals(datasetType) || XYZ_TYPE.equals(datasetType)) {
      // Set to first number in our data set
      if (BARLINECHART_TYPE.equals(chartType)) {
        rangeMin = Integer.MAX_VALUE;
        rangeMax = Integer.MIN_VALUE;
        List nodes = chartNode.selectNodes("line-series/series");
        List<String> bars = new ArrayList<String>();
        for (Object node : nodes) {
          if (getValue((Node) node) != null) {
            bars.add(getValue((Node) node));
          }
        }

        for (int c = 1; c < getColumnCount(); c++) {
          String text = (String) getValueAt(0, c);
          if (bars.contains(text)) {
            for (int r = 1; r < getRowCount(); r++) {
              if (rangeMin > ((Number) getValueAt(r, c)).intValue())
                rangeMin = ((Number) getValueAt(r, c)).intValue();
              if (rangeMax < ((Number) getValueAt(r, c)).intValue())
                rangeMax = ((Number) getValueAt(r, c)).intValue();
            }
          }
        }
      } else {
        rangeMin = ((Number) getValueAt(1, 1)).intValue();
        rangeMax = rangeMin;
        // Iterate over columns 1+
        for (int c = 1; c < getColumnCount(); c++) {
          for (int r = 1; r < getRowCount(); r++) {
            if (rangeMin > ((Number) getValueAt(r, c)).intValue())
              rangeMin = ((Number) getValueAt(r, c)).intValue();
            if (rangeMax < ((Number) getValueAt(r, c)).intValue())
              rangeMax = ((Number) getValueAt(r, c)).intValue();
          }
        }
      }
    }

    boolean minDefined = false;
    boolean maxDefined = false;

    Node temp = chartNode.selectSingleNode(LINES_RANGE_MINIMUM_NODE_LOC);
    if (getValue(temp) != null) {
      rangeMin = new Integer(getValue(temp)).intValue();
      minDefined = true;
    }

    temp = chartNode.selectSingleNode(LINES_RANGE_MAXIMUM_NODE_LOC);
    if (getValue(temp) != null) {
      rangeMax = new Integer(getValue(temp)).intValue();
      maxDefined = true;
    }

    temp = chartNode.selectSingleNode("lines-range-color");
    if (getValue(temp) != null) {
      rangeColor = getValue(temp);
    }

    temp = chartNode.selectSingleNode("lines-range-grid-color");
    if (getValue(temp) != null) {
      rangeGridColor = getValue(temp);
    }

    temp = chartNode.selectSingleNode("lines-range-stroke");
    if (getValue(temp) != null) {
      rangeStroke = Integer.parseInt(getValue(temp));
    }

    temp = chartNode.selectSingleNode("range-steps");
    if (getValue(temp) != null) {
      steps = new Integer(getValue(temp)).intValue();
    }
    
    int diff = rangeMax - rangeMin;

    int chunksize = diff / steps;

    Integer stepforchart = null;
    if (chunksize > 0)
      stepforchart = new Integer(chunksize);

    // Readjust mins/maxs only if they weren't specified
    if (!minDefined) {
      // If actual min is positive, don't go below ZERO
      if (rangeMin > 0 && rangeMin - chunksize < 0)
        rangeMin = 0;
      else
        rangeMin = rangeMin - chunksize;
    }
    if (!maxDefined) {
      rangeMax = rangeMin + (chunksize * (steps + 2));
    }

    //    if (HORIZONTAL_ORIENTATION.equals(orientation)) {
    //      XAxis xaxis = new XAxis();
    //      xaxis.setRange(rangeMin, rangeMax, stepforchart);
    //      xaxis.setStroke(rangeStroke);
    //      xaxis.setColour(rangeColor);
    //      xaxis.setGridColour(rangeGridColor);
    //      c.setXAxis(xaxis);
    //
    //    } else {
    YAxis yaxis = new YAxis();

    yaxis.setRange(rangeMin, rangeMax, stepforchart);
    yaxis.setStroke(rangeStroke);
    yaxis.setColour(rangeColor);
    yaxis.setGridColour(rangeGridColor);
    c.setYAxisRight(yaxis);
    //    }
  }

  public int getStackedMaxRange() {
    int maxRange = 0;
    for (int i = 0; i < sbc.getStackCount(); i++) {
      int currRange = 0;
      List<Object> vals = (List<Object>)sbc.getValues().get(i);
      for (Object val : vals) {
        currRange += ((StackValue)val).getValue().intValue();
      }
      if (currRange > maxRange) {
        maxRange = currRange;
      }
    }
    
    return maxRange;
    
  }
  
  public void setupRange() {

    int rangeMin = 0;
    int rangeMax = 100;
    int steps = 9;

    String rangeColor = "#000000";
    String rangeGridColor = "#aaaaaa";
    int rangeStroke = 1;

    if (CATEGORY_TYPE.equals(datasetType) || XYZ_TYPE.equals(datasetType) || XY_TYPE.equals(datasetType)) {
      // Set to first number in our data set
      if (BARLINECHART_TYPE.equals(chartType)) {
        setupLineRange();
        if (isstacked) {
          rangeMin = 0;
          rangeMax = getStackedMaxRange();
        } else {
          rangeMin = Integer.MAX_VALUE;
          rangeMax = Integer.MIN_VALUE;
          List nodes = chartNode.selectNodes("bar-series/series");
          List<String> bars = new ArrayList<String>();
          for (Object node : nodes) {
            if (getValue((Node) node) != null) {
              bars.add(getValue((Node) node));
            }
          }
  
          for (int c = 1; c < getColumnCount(); c++) {
            String text = (String) getValueAt(0, c);
            if (bars.contains(text)) {
              for (int r = 1; r < getRowCount(); r++) {
                if (rangeMin > ((Number) getValueAt(r, c)).intValue())
                  rangeMin = ((Number) getValueAt(r, c)).intValue();
                if (rangeMax < ((Number) getValueAt(r, c)).intValue())
                  rangeMax = ((Number) getValueAt(r, c)).intValue();
              }
            }
          }
        }
      } else if (XYZ_TYPE.equals(datasetType) || XY_TYPE.equals(datasetType)) {
        rangeMin = ((Number) getValueAt(1, 2)).intValue();
        rangeMax = rangeMin;
        // Iterate over 2nd row
        for (int r = 1; r < getRowCount(); r++) {
            if (rangeMin > ((Number) getValueAt(r, 2)).intValue())
              rangeMin = ((Number) getValueAt(r, 2)).intValue();
            if (rangeMax < ((Number) getValueAt(r, 2)).intValue())
              rangeMax = ((Number) getValueAt(r, 2)).intValue();
        }
      } else {
        
        if (isstacked) {
          rangeMin = 0;
          rangeMax = getStackedMaxRange();
        } else {
          rangeMin = ((Number) getValueAt(1, 1)).intValue();
          rangeMax = rangeMin;
          // Iterate over columns 1+
          for (int c = 1; c < getColumnCount(); c++) {
            for (int r = 1; r < getRowCount(); r++) {
              if (rangeMin > ((Number) getValueAt(r, c)).intValue())
                rangeMin = ((Number) getValueAt(r, c)).intValue();
              if (rangeMax < ((Number) getValueAt(r, c)).intValue())
                rangeMax = ((Number) getValueAt(r, c)).intValue();
            }
          }
        }
      }

    }

    boolean minDefined = false;
    boolean maxDefined = false;

    Node temp = chartNode.selectSingleNode(RANGE_MINIMUM_NODE_LOC);
    if (getValue(temp) != null) {
      rangeMin = new Integer(getValue(temp)).intValue();
      minDefined = true;
    }

    temp = chartNode.selectSingleNode(RANGE_MAXIMUM_NODE_LOC);
    if (getValue(temp) != null) {
      rangeMax = new Integer(getValue(temp)).intValue();
      maxDefined = true;
    }
    
    temp = chartNode.selectSingleNode("range-steps");
    if (getValue(temp) != null) {
      steps = new Integer(getValue(temp)).intValue();
    }

    temp = chartNode.selectSingleNode("range-color");
    if (getValue(temp) != null) {
      rangeColor = getValue(temp);
    }

    temp = chartNode.selectSingleNode("range-grid-color");
    if (getValue(temp) != null) {
      rangeGridColor = getValue(temp);
    }

    temp = chartNode.selectSingleNode("range-stroke");
    if (getValue(temp) != null) {
      rangeStroke = Integer.parseInt(getValue(temp));
    }

    int diff = rangeMax - rangeMin;

    int chunksize = diff / steps;

    Integer stepforchart = null;
    if (chunksize > 0)
      stepforchart = new Integer(chunksize);

    // Readjust mins/maxs only if they weren't specified
    if (!minDefined) {
      // If actual min is positive, don't go below ZERO
      if (rangeMin > 0 && rangeMin - chunksize < 0)
        rangeMin = 0;
      else
        rangeMin = rangeMin - chunksize;
    }
    if (!maxDefined) {
      rangeMax = rangeMin + (chunksize * (steps + 2));
    }

    if (HORIZONTAL_ORIENTATION.equals(orientation)) {
      XAxis xaxis = new XAxis();
      xaxis.setRange(rangeMin, rangeMax, stepforchart);
      xaxis.setStroke(rangeStroke);
      xaxis.setColour(rangeColor);
      xaxis.setGridColour(rangeGridColor);
      c.setXAxis(xaxis);

    } else {
      YAxis yaxis = new YAxis();
      yaxis.setRange(rangeMin, rangeMax, stepforchart);
      yaxis.setStroke(rangeStroke);
      yaxis.setColour(rangeColor);
      yaxis.setGridColour(rangeGridColor);
      c.setYAxis(yaxis);
    }

  }

  public void setupPieStyles() {

    Node temp = chartNode.selectSingleNode("animate");
    if (getValue(temp) != null) {
      animate = "true".equals(getValue(temp));
    }
    
    temp = chartNode.selectSingleNode("start-angle");
    if (getValue(temp) != null) {
      startAngle = Integer.parseInt(getValue(temp));
    }
  }
  
  public void setupBarStyles() {

    barchartstyle = BARCHART_STYLE_DEFAULT;

    // 3d
    Node temp = chartNode.selectSingleNode(IS3D_NODE_LOC);
    if (getValue(temp) != null && "true".equals(getValue(temp))) {
      barchartstyle = BarChart.Style.THREED;
    }
    // Glass
    temp = chartNode.selectSingleNode(ISGLASS_NODE_LOC);
    if (getValue(temp) != null && "true".equals(getValue(temp))) {
      barchartstyle = BarChart.Style.GLASS;
    }
    // Sketch
    temp = chartNode.selectSingleNode(ISSKETCH_NODE_LOC);
    if (getValue(temp) != null && "true".equals(getValue(temp))) {
      issketch = true;
    } else {
      issketch = false;
    }

    // Sketch
    temp = chartNode.selectSingleNode(ISSTACKED_NODE_LOC);
    if (getValue(temp) != null) {
      isstacked = "true".equals(getValue(temp));
    }

    
    temp = chartNode.selectSingleNode(ORIENTATION_NODE_LOC);
    if (getValue(temp) != null)
      orientation = getValue(temp);
    else
      orientation = ORIENTATION_DEFAULT;
    
    temp = chartNode.selectSingleNode("fun-factor");
    if (getValue(temp) != null) {
      sketchBarFunFactor = Integer.parseInt(getValue(temp));
    } else {
      sketchBarFunFactor = SKETCH_FUNFACTOR_DEFAULT;
    }

    temp = chartNode.selectSingleNode("height-3d");
    if (getValue(temp) != null) {
      threedheight = Integer.parseInt(getValue(temp));
    }
  }

  public void setupLineStyles() {

    Node temp = chartNode.selectSingleNode(DOTSTYLE_NODE_LOC);

    if (getValue(temp) != null) {
      if ("dot".equals(getValue(temp)))
        linechartstyle = LineChart.Style.DOT;
      else if ("normal".equals(getValue(temp)))
        linechartstyle = LineChart.Style.NORMAL;
      else if ("hollow".equals(getValue(temp)))
        linechartstyle = LineChart.Style.HOLLOW;
      else
        linechartstyle = LINECHART_STYLE_DEFAULT;
    } else {
      linechartstyle = LINECHART_STYLE_DEFAULT;
    }
    
    temp = chartNode.selectSingleNode("line-width");
    
    if (getValue(temp) != null) {
      linechartwidth = Integer.parseInt(getValue(temp));
    }
    
    temp = chartNode.selectSingleNode(DOT_WIDTH_NODE_LOC);
    if (getValue(temp) != null) {
      dotwidth = Integer.parseInt(getValue(temp));
    }

  }

  public void createElements() {

    if (CATEGORY_TYPE.equals(datasetType)) {

      int columnCount;

      // Ignore additional columns for PieCharts
      if (PIECHART_TYPE.equals(chartType))
        columnCount = 2;
      else
        columnCount = getColumnCount();

      // Create a "series" or element for each column past the first
      for (int i = 1; i < columnCount; i++) {
        elements.add(getElementForColumn(i));
      }
    } else if (XYZ_TYPE.equals(datasetType) || XY_TYPE.equals(datasetType)) {
      
      int rowCount = getRowCount();
      for (int i = 1; i < rowCount; i++) {
        Element e = null;
        String text = (String) getValueAt(i, 0);        
        if (BUBBLECHART_TYPE.equals(chartType) || DOTCHART_TYPE.equals(chartType)) {
          ScatterChart sc = new ScatterChart("");
          sc.setColour(colors.get(i-1));
          Number x = (Number)getValueAt(i, 1);
          Number y = (Number)getValueAt(i, 2);
          Number z = null; 
          if (XYZ_TYPE.equals(datasetType)) {
            z = (Number)getValueAt(i, 3);
            setupDotSize(sc, z);
          } else {
            if (dotwidth != null) {
              sc.setDotSize(dotwidth);
            
            }
            
            Node temp = chartNode.selectSingleNode("dot-label-content");
            if (getValue(temp) != null) {
              sc.setTooltip(MessageFormat.format(getValue(temp), text, 
                  NumberFormat.getInstance().format(x), NumberFormat.getInstance().format(y)));
            } else {
              sc.setTooltip(MessageFormat.format("{0}: {1}, {2}", text, 
                  NumberFormat.getInstance().format(x), NumberFormat.getInstance().format(y)));
            }
          }
          sc.addPoint(x.doubleValue(), y.doubleValue());
          
          
          if (BUBBLECHART_TYPE.equals(datasetType)) {
            Node temp = chartNode.selectSingleNode("bubble-label-content");
            if (getValue(temp) != null) {
              Node temp2 = chartNode.selectSingleNode("bubble-label-z-format");
              String zstr = null;
              if (getValue(temp2) != null) {
                DecimalFormat df = new DecimalFormat(getValue(temp2));
                zstr = df.format(z); 
              } else {
                if (z != null) {
                  zstr = z.toString();
                }
              }
              sc.setTooltip(MessageFormat.format(getValue(temp), text, 
                  NumberFormat.getInstance().format(x), NumberFormat.getInstance().format(y), zstr));
            } 
          }
          e = sc;
        }

        e.setText(text);
        elements.add(e);
      }
    }
  }

  private int getColumnCount() {
    if (!byRow) {
      return data.getColumnCount();
    } else {
      return data.getRowCount() + 1;
    }
  }

  private int getRowCount() {
    if (byRow) {
      return data.getColumnCount();
    } else {
      return data.getRowCount() + 1;
    }
  }

  private Object getValueAt(int i, int n) {
    if (!byRow) {
      if (i == 0) {
        return data.getMetaData().getColumnHeaders()[0][n];
      } else {
        return data.getValueAt(i - 1, n);
      }
    } else {
      if (n == 0) {
        return data.getMetaData().getColumnHeaders()[0][i];
      } else {
        return data.getValueAt(n - 1, i);
      }
    }
  }

  public void setupDomain() {
    String[] labels = null;

    Number domainMin = null;
    Number domainMax = null;
    Integer stepforchart = null;
    
    if (CATEGORY_TYPE.equals(datasetType)) {
      int index = 0;
      int rowCount = getRowCount() - 1;
      labels = new String[rowCount];
      for (int j = 0; j < rowCount; j++) {
        Object obj = getValueAt(j + 1, index);
        if (obj instanceof java.sql.Timestamp || obj instanceof java.util.Date) {
          labels[j] = sf.format(obj);
        } else {
          labels[j] = obj.toString();
        }
      }
    } else if (XYZ_TYPE.equals(datasetType) || XY_TYPE.equals(datasetType)) {
      domainMin = ((Number) getValueAt(1, 1)).intValue();
      domainMax = domainMin;
      // Iterate over rows
      for (int r = 1; r < getRowCount(); r++) {
          if (domainMin.intValue() > ((Number) getValueAt(r, 1)).intValue()) {
            domainMin = ((Number) getValueAt(r, 1)).intValue();
          }
          if (domainMax.intValue() < ((Number) getValueAt(r, 1)).intValue()) {
            domainMax = ((Number) getValueAt(r, 1)).intValue();
          }
      }
      
      int steps = 9;
      int diff = domainMax.intValue() - domainMin.intValue();
      
      Node temp = chartNode.selectSingleNode("domain-steps");
      if (getValue(temp) != null) {
        steps = new Integer(getValue(temp)).intValue();
      }
      
      int chunksize = diff / steps;
      
      if (chunksize > 0) {
        stepforchart = new Integer(chunksize);
      }
      
      // If actual min is positive, don't go below ZERO
      if (domainMin.intValue() > 0 && domainMin.intValue() - chunksize < 0)
        domainMin = 0;
      else
        domainMin = domainMin.intValue() - chunksize;

      domainMax = domainMin.intValue() + (chunksize * (steps + 2));

      temp = chartNode.selectSingleNode(DOMAIN_MINIMUM_NODE_LOC);
      if (getValue(temp) != null) {
        domainMin = new Integer(getValue(temp)).intValue();
      }

      temp = chartNode.selectSingleNode(DOMAIN_MAXIMUM_NODE_LOC);
      if (getValue(temp) != null) {
        domainMax = new Integer(getValue(temp)).intValue();
      }
      
    }

    String domainColor = "#000000";
    String domainGridColor = "#aaaaaa";
    int domainStroke = 1;

    Node temp = chartNode.selectSingleNode("domain-color");
    if (getValue(temp) != null) {
      domainColor = getValue(temp);
    }

    temp = chartNode.selectSingleNode("domain-grid-color");
    if (getValue(temp) != null) {
      domainGridColor = getValue(temp);
    }

    temp = chartNode.selectSingleNode("domain-stroke");
    if (getValue(temp) != null) {
      domainStroke = Integer.parseInt(getValue(temp));
    }

    if (HORIZONTAL_ORIENTATION.equals(orientation)) {
      YAxis yaxis = new YAxis();
      if (labels != null) {
        yaxis.addLabels(labels);
      }
      yaxis.setStroke(domainStroke);
      yaxis.setColour(domainColor);
      yaxis.setGridColour(domainGridColor);
      
      if (domainMin != null && domainMax != null) {
        yaxis.setRange(domainMin.intValue(), domainMax.intValue(), stepforchart);  
      }
      
      c.setYAxis(yaxis);
    } else {
      XAxis xaxis = new XAxis();
      if (labels != null) {
        xaxis.addLabels(labels);
      }
      xaxis.setStroke(domainStroke);
      xaxis.setColour(domainColor);
      xaxis.setGridColour(domainGridColor);
      if (domainMin != null && domainMax != null) {
        xaxis.setRange(domainMin.intValue(), domainMax.intValue(), stepforchart);  
      }
      
      c.setXAxis(xaxis);
    }

  }
  
  public LineChart getLineChart(int n) {
    LineChart lc = new LineChart(this.linechartstyle);
    for (int i = 1; i < getRowCount(); i++) {
      double d = ((Number) getValueAt(i, n)).doubleValue();
      LineChart.Dot dot = new LineChart.Dot(d);

      if (dotwidth != null) {
        dot.setDotSize(dotwidth);
      }
      lc.addDots(dot);
      if (null != baseURLTemplate)
        lc.setOn_click(baseURLTemplate);
    }
    if (linechartwidth != null) {
      lc.setWidth(linechartwidth);
    }
    //       TODO wrap around the set of colors if bars.length > colors.length
    lc.setColour(colors.get(n - 1));
    
    if (tooltipText != null) {
      lc.setTooltip(tooltipText);
    }
    
    return lc;
  }
  
  StackedBarChart sbc;
  
  public Element getVerticalBarChart(int n) {
    if (isstacked) {
      if (sbc == null) {
        sbc = new StackedBarChart();
      }
      // keys ?

      StackKey key = new StackKey();
      String text = (String) getValueAt(0, n);
      key.setText(text);
      // key.setFontSize(???)
      key.setColour(colors.get(n - 1));
      sbc.addKeys(key);
      // values

      StackValue[] datas = new StackValue[getRowCount()];
      
      for (int i = 1; i < getRowCount(); i++) {
        Stack stack = null;
        if (sbc.getStackCount() > i - 1) {
          stack = sbc.stack(i - 1);
        } else {
          stack = sbc.newStack();
        }
        double d = ((Number) getValueAt(i, n)).doubleValue();
        stack.addStackValues(new StackValue(d, colors.get(n - 1)));
      }
      
      return sbc;
    } else {
      
      BarChart bc;
      // Is Sketch?
      if (issketch) {
        bc = new SketchBarChart();
        ((SketchBarChart) bc).setFunFactor(sketchBarFunFactor);
        ((SketchBarChart) bc).setOutlineColour(outlineColors.get(n - 1));
        
      } else {
        bc = new BarChart(this.barchartstyle);
        if (this.barchartstyle == Style.THREED && threedheight != null) {
          c.getXAxis().set3D(threedheight);
        }
      }

      for (int i = 1; i < getRowCount(); i++) {
        double d = ((Number) getValueAt(i, n)).doubleValue();
        bc.addBars(new BarChart.Bar(d));
        if (null != baseURLTemplate)
          bc.setOn_click(baseURLTemplate);
      }
      // TODO wrap around the set of colors if bars.length > colors.length
      bc.setColour(colors.get(n - 1));
      
      if (tooltipText != null) {
        bc.setTooltip(tooltipText);
      }

      return bc;
    }
  }

  public Element getElementForColumn(int n) {

    Element e = null;

    if (BARCHART_TYPE.equals(chartType) && VERTICAL_ORIENTATION.equals(orientation)) {
      e = getVerticalBarChart(n);
    } else if (BARCHART_TYPE.equals(chartType) && HORIZONTAL_ORIENTATION.equals(orientation)) {
      HorizontalBarChart hbc = new HorizontalBarChart();
      for (int i = 1; i < getRowCount(); i++) {
        double d = ((Number) getValueAt(i, n)).doubleValue();
        HorizontalBarChart.Bar hbcb = new HorizontalBarChart.Bar(d);
        hbc.addBars(new HorizontalBarChart.Bar(d));
        if (null != baseURLTemplate)
          hbc.setOn_click(baseURLTemplate);
      }
      hbc.setColour(colors.get(n - 1));
      if (tooltipText != null) {
        hbc.setTooltip(tooltipText);
      }
      e = hbc;

    } else if (LINECHART_TYPE.equals(chartType)) {
      e = getLineChart(n);
    } else if (PIECHART_TYPE.equals(chartType)) {
      PieChart pc = new PieChart();
      PieChart.Slice[] slices = new PieChart.Slice[getRowCount() - 1];
      for (int i = 1; i < getRowCount(); i++) {
        double d = ((Number) getValueAt(i, n)).doubleValue();
        // Labels are already set - use them
        String label = (String) c.getXAxis().getLabels().getLabels().get(i - 1);
        
        slices[i - 1] = new PieChart.Slice(d, label, label);
        if (tooltipText != null) {
          slices[i - 1].setTooltip(tooltipText);
        }
        
        if (null != baseURLTemplate) {
          pc.setOn_click(baseURLTemplate);
        }
      }

      pc.addSlices(slices);
      pc.setColours(this.colors);
      pc.setStartAngle(startAngle);
      pc.setAnimate(animate);
      
      e = pc;

    } else if (AREACHART_TYPE.equals(chartType)) {
      LineChart ac = null;
      if(linechartstyle != LineChart.Style.HOLLOW) {
        AreaLineChart ahc = new AreaLineChart();
        ahc.setFill(colors.get(n-1));
        ac = ahc;
      } else {
        AreaHollowChart ahc = new AreaHollowChart();
        ahc.setFill(colors.get(n-1));
        ac = ahc;
      }

      Number[] numbers = new Number[getRowCount() - 1];

      for (int i = 1; i < getRowCount(); i++) {
        //double d = ((Number) getValueAt(i, n)).doubleValue();
        //ahc.addDots(new LineChart.Dot(d));
        numbers[i - 1] = ((Number) getValueAt(i, n)).doubleValue();
        if (null != baseURLTemplate)
          ac.setOn_click(baseURLTemplate);
      }

      ac.addValues(numbers);
      ac.setColour(colors.get(n - 1));
      
      if (linechartwidth != null) {
        ac.setWidth(linechartwidth);
      }
      if (tooltipText != null) {
        ac.setTooltip(tooltipText);
      }
      e = ac;

    } else if (BARLINECHART_TYPE.equals(chartType)) {
      String text = (String) getValueAt(0, n);
      // determine if this is a line or a bar
      List nodes = chartNode.selectNodes("bar-series/series");
      List<String> bars = new ArrayList<String>();
      for (Object node : nodes) {
        if (getValue((Node) node) != null) {
          bars.add(getValue((Node) node));
        }
      }
      //      Chart blc = 
      if (!bars.contains(text)) {
        LineChart lc = getLineChart(n);
        lc.setRightYAxis();
        e = lc;
      } else {
        e = getVerticalBarChart(n);
      }
//    } else if (BUBBLECHART_TYPE.equals(chartType)) {
//      ScatterChart sc = new ScatterChart("");
//      sc.setColour(colors.get(n-1));
//      setupDotSize(sc, (Number)getValueAt(3, n));
//      Number x = (Number)getValueAt(1, n);
//      Number y = (Number)getValueAt(2, n);
//      sc.addPoint(x.doubleValue(), y.doubleValue());
//      e = sc;
//      // TODO: setTooltip(root,se);

    }

    String text = (String) getValueAt(0, n);

    e.setText(text);

    return e;
  }
  
  private Number bubbleMaxX = null;
  
  private void setupDotSize(ScatterChart se, Number x) {
    
    // cache the max x value once looked up
    if (bubbleMaxX == null) {
      Number maxX=0;
      int rowCount = getRowCount();
      for (int j = 1; j < rowCount; j++) {
        Number currx = (Number)getValueAt(j, 3);
        if(maxX.doubleValue()<currx.doubleValue())
        {
          maxX = currx;
        }
      }
      bubbleMaxX = maxX;
    }
    
    // Number x = (Number)getValueAt(i, 3);

    //<max-bubble-size>100</max-bubble-size>
    int maxBubbleSize = 100;
    Node bubbleSizeNode = chartNode.selectSingleNode("max-bubble-size");

    if (getValue(bubbleSizeNode) != null) {
      maxBubbleSize = Integer.parseInt(getValue(bubbleSizeNode));
    }

    se.setDotSize(Integer.valueOf(java.lang.Math.round(maxBubbleSize*(x.floatValue()/bubbleMaxX.floatValue())) ));
  }
}
