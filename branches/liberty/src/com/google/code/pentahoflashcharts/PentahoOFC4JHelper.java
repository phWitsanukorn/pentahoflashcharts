package com.google.code.pentahoflashcharts;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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

import org.apache.commons.logging.Log;
import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoDataTypes;
import org.pentaho.commons.connection.IPentahoResultSet;
import org.pentaho.commons.connection.PentahoDataTransmuter;
import org.pentaho.platform.engine.services.runtime.TemplateUtil;
import org.pentaho.platform.plugin.action.messages.Messages;

/**
 * Contributed by Nick Goodman, this class creates an OFC4J Chart object for
 * rendering, using pentaho chart xml format.
 * 
 * See http://www.ofc2dz.com for details on the SWF we are using.
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
 */
public class PentahoOFC4JHelper {

  // general chart related elements
  
  private static final String TITLE_NODE_LOC = "title"; //$NON-NLS-1$

  private static final String TITLE_FONT_NODE_LOC = "title-font"; //$NON-NLS-1$

  private static final String DATASET_TYPE_NODE_LOC = "dataset-type"; //$NON-NLS-1$

  private static final String CHART_TYPE_NODE_LOC = "chart-type"; //$NON-NLS-1$

  private static final String COLOR_PALETTE_NODE_LOC = "color-palette"; //$NON-NLS-1$
  
  private static final String COLOR_NODE_LOC = "color"; //$NON-NLS-1$
  
  private static final String OUTLINE_COLOR_PALETTE_NODE_LOC = "outline-color-palette"; //$NON-NLS-1$
  
  private static final String PLOT_BACKGROUND_NODE_LOC = "plot-background"; //$NON-NLS-1$

  private static final String PLOT_BACKGROUND_COLOR_XPATH = "@type"; //attribute of plot-background  //$NON-NLS-1$

  private static final String CHART_BACKGROUND_NODE_LOC = "chart-background"; //$NON-NLS-1$

  private static final String CHART_BACKGROUND_COLOR_XPATH = "@type"; //attribute of chart-background  //$NON-NLS-1$

  private static final String URL_TEMPLATE_NODE_LOC = "url-template"; //$NON-NLS-1$

  private static final String TOOLTIP_NODE_LOC = "tooltip"; //$NON-NLS-1$
  
  // font related elements
  
  private static final String FONT_FAMILY_NODE_LOC = "font-family"; //$NON-NLS-1$

  private static final String FONT_SIZE_NODE_LOC = "size"; //$NON-NLS-1$

  private static final String FONT_BOLD_NODE_LOC = "is-bold"; //$NON-NLS-1$

  private static final String FONT_ITALIC_NODE_LOC = "is-italic"; //$NON-NLS-1$
  
  // domain axis related elements

  private static final String DOMAIN_STROKE_NODE_LOC = "domain-stroke"; //$NON-NLS-1$

  private static final String DOMAIN_GRID_COLOR_NODE_LOC = "domain-grid-color"; //$NON-NLS-1$

  private static final String DOMAIN_COLOR_NODE_LOC = "domain-color"; //$NON-NLS-1$

  private static final String DOMAIN_STEPS_NODE_LOC = "domain-steps"; //$NON-NLS-1$

  private static final String DOMAIN_TITLE_NODE_LOC = "domain-title"; //$NON-NLS-1$

  private static final String DOMAIN_TITLE_FONT_NODE_LOC = "domain-title-font"; //$NON-NLS-1$

  private static final String DOMAIN_MAXIMUM_NODE_LOC = "domain-maximum"; //$NON-NLS-1$

  private static final String DOMAIN_MINIMUM_NODE_LOC = "domain-minimum"; //$NON-NLS-1$
  
  // range axis related elements
  
  private static final String RANGE_STROKE_NODE_LOC = "range-stroke"; //$NON-NLS-1$

  private static final String RANGE_GRID_COLOR_NODE_LOC = "range-grid-color"; //$NON-NLS-1$

  private static final String RANGE_COLOR_NODE_LOC = "range-color"; //$NON-NLS-1$

  private static final String RANGE_STEPS_NODE_LOC = "range-steps"; //$NON-NLS-1$

  private static final String RANGE_TITLE_NODE_LOC = "range-title"; //$NON-NLS-1$

  private static final String RANGE_TITLE_FONT_NODE_LOC = "range-title-font"; //$NON-NLS-1$

  private static final String RANGE_MAXIMUM_NODE_LOC = "range-maximum"; //$NON-NLS-1$

  private static final String RANGE_MINIMUM_NODE_LOC = "range-minimum"; //$NON-NLS-1$

  // bubble / dot scatter related elements
  
  private static final String MAX_BUBBLE_SIZE_NODE_LOC = "max-bubble-size"; //$NON-NLS-1$

  private static final String BUBBLE_LABEL_Z_FORMAT_NODE_LOC = "bubble-label-z-format"; //$NON-NLS-1$

  private static final String BUBBLE_LABEL_CONTENT_NODE_LOC = "bubble-label-content"; //$NON-NLS-1$

  private static final String DOT_LABEL_CONTENT_NODE_LOC = "dot-label-content"; //$NON-NLS-1$

  // line related elements
  
  private static final String LINE_WIDTH_NODE_LOC = "line-width"; //$NON-NLS-1$

  private static final String DOTSTYLE_NODE_LOC = "dot-style"; //$NON-NLS-1$
  
  private static final String DOT_WIDTH_NODE_LOC = "dot-width"; //$NON-NLS-1$
  
  // bar related elements
  
  private static final String HEIGHT_3D_NODE_LOC = "height-3d"; //$NON-NLS-1$

  private static final String FUN_FACTOR_NODE_LOC = "fun-factor"; //$NON-NLS-1$

  private static final String IS3D_NODE_LOC = "is-3D"; //$NON-NLS-1$

  private static final String ISGLASS_NODE_LOC = "is-glass"; //$NON-NLS-1$

  private static final String ISSKETCH_NODE_LOC = "is-sketch"; //$NON-NLS-1$
  
  private static final String ISSTACKED_NODE_LOC = "is-stacked"; //$NON-NLS-1$

  private static final String ORIENTATION_NODE_LOC = "orientation"; //$NON-NLS-1$
  
  // bar line related elements
  
  private static final String BAR_SERIES_SERIES_NODE_LOC = "bar-series/series"; //$NON-NLS-1$

  private static final String LINES_RANGE_STROKE_NODE_LOC = "lines-range-stroke"; //$NON-NLS-1$

  private static final String LINES_RANGE_GRID_COLOR_NODE_LOC = "lines-range-grid-color"; //$NON-NLS-1$

  private static final String LINES_RANGE_COLOR_NODE_LOC = "lines-range-color"; //$NON-NLS-1$
  
  private static final String LINE_SERIES_SERIES_NODE_LOC = "line-series/series"; //$NON-NLS-1$

  private static final String LINES_RANGE_MAXIMUM_NODE_LOC = "lines-range-maximum"; //$NON-NLS-1$

  private static final String LINES_RANGE_MINIMUM_NODE_LOC = "lines-range-minimum"; //$NON-NLS-1$
  
  private static final String LINE_RANGE_STEPS_NODE_LOC = "line-range-steps"; //$NON-NLS-1$
  
  // pie related elements
  
  private static final String START_ANGLE_NODE_LOC = "start-angle"; //$NON-NLS-1$

  private static final String ANIMATE_NODE_LOC = "animate"; //$NON-NLS-1$

  // Default values

  private static final String DATASET_TYPE_DEFAULT = "CategoryDataset"; //$NON-NLS-1$
  
  private static final String CSS_FONT_FAMILY_DEFAULT = "Arial"; //$NON-NLS-1$

  private static final String CSS_FONT_SIZE_DEFAULT = "14"; //$NON-NLS-1$

  private static final String CSS_FONT_WEIGHT_DEFAULT = "normal"; //$NON-NLS-1$

  private static final String CSS_FONT_STYLE_DEFAULT = "normal"; //$NON-NLS-1$

  private static final String CHART_TYPE_DEFAULT = "BarChart"; //$NON-NLS-1$

  private static final String AXIS_GRID_COLOR_DEFAULT = "#aaaaaa"; //$NON-NLS-1$

  private static final String AXIS_COLOR_DEFAULT = "#000000"; //$NON-NLS-1$

  private static final LineChart.Style LINECHART_STYLE_DEFAULT = LineChart.Style.NORMAL;
  
  private static final String ORIENTATION_DEFAULT = "vertical"; //$NON-NLS-1$
  
  private static final Style BARCHART_STYLE_DEFAULT = BarChart.Style.NORMAL;

  private static final int SKETCH_FUNFACTOR_DEFAULT = 5;

  @SuppressWarnings("nls")
  private static final String[] COLORS_DEFAULT = { "#006666", "#0066CC", "#009999", "#336699", "#339966", "#3399FF",
      "#663366", "#666666", "#666699", "#669999", "#6699CC", "#66CCCC", "#993300", "#999933", "#999966", "#999999",
      "#9999CC", "#9999FF", "#99CC33", "#99CCCC", "#99CCFF", "#CC6600", "#CC9933", "#CCCC33", "#CCCC66", "#CCCC99",
      "#CCCCCC", "#FF9900", "#FFCC00", "#FFCC66" };

  // Chart Type Values (CHART_TYPE_NODE_LOC)
  private static final String BARCHART_TYPE = "BarChart"; //$NON-NLS-1$

  private static final String LINECHART_TYPE = "LineChart"; //$NON-NLS-1$

  private static final String PIECHART_TYPE = "PieChart"; //$NON-NLS-1$

  private static final String AREACHART_TYPE = "AreaChart"; //$NON-NLS-1$

  private static final String BARLINECHART_TYPE = "BarLineChart"; //$NON-NLS-1$
  
  private static final String BUBBLECHART_TYPE = "BubbleChart"; //$NON-NLS-1$
  
  private static final String DOTCHART_TYPE = "DotChart"; //$NON-NLS-1$

  // Orientation Type Values (ORIENTATION_NODE_LOC)
  private static final String HORIZONTAL_ORIENTATION = "horizontal"; //$NON-NLS-1$

  private static final String VERTICAL_ORIENTATION = "vertical"; //$NON-NLS-1$

  // Dataset Type Values
  private static final String CATEGORY_TYPE = "CategoryDataset"; //$NON-NLS-1$
  
  private static final String XY_TYPE = "XYSeriesCollection"; //$NON-NLS-1$
  
  private static final String XYZ_TYPE = "XYZSeriesCollection"; //$NON-NLS-1$

  // color types
  
  private static final String COLOR_TYPE = "color"; //$NON-NLS-1$

  private static final String CSS_FONT_STYLES = "font-family: {fontfamily}; font-size: {fontsize}px; " + //$NON-NLS-1$
                                                "font-weight: {fontweight}; font-style: {fontstyle};"; //$NON-NLS-1$
  
  // Private members 

  private Chart chart = new Chart();
  private ArrayList<Element> elements = new ArrayList<Element>();
  private ArrayList<String> colors = new ArrayList<String>();
  private ArrayList<String> outlineColors = new ArrayList<String>();

  private Node chartNode;  
  private String chartType;
  private Log log;
  
  // data related members
  private String[] rowHeaders;
  private String[] columnHeaders;
  private IPentahoResultSet data;
  private boolean hasRowHeaders = false;
  private boolean hasColumnHeaders = false;  
  private String datasetType;

  // general chart members 
  private String baseURLTemplate;
  private String tooltipText;
  
  // bar related members
  private String orientation;
  private BarChart.Style barchartstyle;
  private boolean issketch;
  private Integer sketchBarFunFactor;
  private boolean isstacked;
  private Integer threedheight;
  private StackedBarChart sbc;
  
  // pie related members
  private boolean animate;
  private Integer startAngle;

  // line related members
  private LineChart.Style linechartstyle;
  private Integer linechartwidth;
  private Integer dotwidth;
  
  // scatter / bubble members
  private Number bubbleMaxX;

  public PentahoOFC4JHelper(Node chartNode, IPentahoResultSet data, boolean byRow, Log log) {
    this.chartNode = chartNode;
    this.log = log;
    if (byRow) {
      setData(PentahoDataTransmuter.pivot(data));
    } else {
      setData(data);
    }
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

    // Setup a few additional things after creating elements
    setupTitles();
    setupRange();

    chart.addElements(elements);

    return chart;
  }
  
  //
  // Data Related Methods
  // 
  
  public void setData(IPentahoResultSet data) {
    hasRowHeaders = data.getMetaData().getRowHeaders() != null;
    hasColumnHeaders = data.getMetaData().getColumnHeaders() != null;
    if (!hasRowHeaders || !hasColumnHeaders) {
      // this populates the data's row header and col headers if not already populated
      data = PentahoDataTransmuter.transmute(data, false);
    }
    try {
      rowHeaders = PentahoDataTransmuter.getCollapsedHeaders(IPentahoDataTypes.AXIS_ROW, data, '|');
      columnHeaders = PentahoDataTransmuter.getCollapsedHeaders(IPentahoDataTypes.AXIS_COLUMN, data, '|');
    } catch (Exception e) {
      // should really NEVER get here
      if (log != null) {
        log.error(null, e);
      }
    }
    
    this.data = data;
  }
  
  private int getColumnCount() {
    if (!hasRowHeaders) {
      return data.getColumnCount() - 1;
    } else {
      return data.getColumnCount();
    }
  }

  private int getRowCount() {
    if (!hasColumnHeaders) {
      return data.getRowCount() - 1;
    } else {
      return data.getRowCount();
    }
  }

  private String getRowHeader(int r) {
    if (!hasColumnHeaders) {
      r = r + 1;
    }
    return rowHeaders[r];
  }

  private String getColumnHeader(int c) {
    if (!hasRowHeaders) {
      c = c + 1;
    }
    return columnHeaders[c];
  }
  
  private Object getValueAt(int r, int c) {
    if (!hasRowHeaders) {
      c = c + 1;
    }
    if (!hasColumnHeaders) {
      r = r + 1;
    }
    return data.getValueAt(r, c);
  }
  
  //
  // Setup Methods
  //
  
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

    if (getValue(title) != null) {
      Text titleText = new Text();
      titleText.setText(getValue(title));
      titleText.setStyle(buildCSSStringFromNode(titleFont));      
      chart.setTitle(titleText);
    }

    Text domainText = new Text();
    if (getValue(domainTitle) != null) {
      domainText.setText(getValue(domainTitle));
    } else {
      // TODO figure out what to do if the header isn't CategoryDataset
      domainText.setText(columnHeaders[0]);
    }
    domainText.setStyle(buildCSSStringFromNode(domainTitleFont));

    Text rangeText = new Text();
    if (getValue(rangeTitle) != null) {
      rangeText.setText(getValue(rangeTitle));
      rangeText.setStyle(buildCSSStringFromNode(rangeTitleFont));
      chart.setYLegend(rangeText);
    }
    
    // TODO: need to support YRightLegend, exposed as y2_legend in open flash charts
    
    chart.setXLegend(domainText);
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
        chart.setBackgroundColour(getValue(temp));
        chart.setInnerBackgroundColour(getValue(temp));
      }
    }
    temp = chartNode.selectSingleNode(CHART_BACKGROUND_NODE_LOC);
    if (getValue(temp) != null) {
      String type = temp.valueOf(CHART_BACKGROUND_COLOR_XPATH);
      if (type != null && COLOR_TYPE.equals(type))
        chart.setBackgroundColour(getValue(temp));
    }
  }

  public void setupStyles() {
    if (BARCHART_TYPE.equals(chartType)) {
      setupBarStyles();
    } else if (LINECHART_TYPE.equals(chartType)) {
      setupLineStyles();
    } else if (BARLINECHART_TYPE.equals(chartType)) {
      setupBarStyles();
      setupLineStyles();
    } else if (PIECHART_TYPE.equals(chartType)) {
      setupPieStyles();
    } else if (DOTCHART_TYPE.equals(chartType)) {
      setupDotStyles();
    }
    
    // TODO: setupBubbleStyles! missing dots
    
    Node temp = chartNode.selectSingleNode(TOOLTIP_NODE_LOC);
    if (getValue(temp) != null) {
      tooltipText = getValue(temp);
    }
  }

  @SuppressWarnings("unchecked")
  public void setupLineRange() {
    int rangeMin = 0;
    int rangeMax = 100;
    int steps = 9;

    String rangeColor = AXIS_COLOR_DEFAULT;
    String rangeGridColor = AXIS_GRID_COLOR_DEFAULT;
    int rangeStroke = 1;

    if (CATEGORY_TYPE.equals(datasetType) || XYZ_TYPE.equals(datasetType)) {
      // Set to first number in our data set
      if (BARLINECHART_TYPE.equals(chartType)) {
        rangeMin = Integer.MAX_VALUE;
        rangeMax = Integer.MIN_VALUE;
        List nodes = chartNode.selectNodes(LINE_SERIES_SERIES_NODE_LOC);
        List<String> bars = new ArrayList<String>();
        for (Object node : nodes) {
          if (getValue((Node) node) != null) {
            bars.add(getValue((Node) node));
          }
        }

        for (int c = 0; c < getColumnCount(); c++) {
          String text = getColumnHeader(c);
          if (bars.contains(text)) {
            for (int r = 0; r < getRowCount(); r++) {
              if (rangeMin > ((Number) getValueAt(r, c)).intValue())
                rangeMin = ((Number) getValueAt(r, c)).intValue();
              if (rangeMax < ((Number) getValueAt(r, c)).intValue())
                rangeMax = ((Number) getValueAt(r, c)).intValue();
            }
          }
        }
      } else {
        rangeMin = ((Number) getValueAt(0, 0)).intValue();
        rangeMax = rangeMin;
        // Iterate over columns 1+
        for (int c = 0; c < getColumnCount(); c++) {
          for (int r = 0; r < getRowCount(); r++) {
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

    temp = chartNode.selectSingleNode(LINES_RANGE_COLOR_NODE_LOC);
    if (getValue(temp) != null) {
      rangeColor = getValue(temp);
    }

    temp = chartNode.selectSingleNode(LINES_RANGE_GRID_COLOR_NODE_LOC);
    if (getValue(temp) != null) {
      rangeGridColor = getValue(temp);
    }

    temp = chartNode.selectSingleNode(LINES_RANGE_STROKE_NODE_LOC);
    if (getValue(temp) != null) {
      rangeStroke = Integer.parseInt(getValue(temp));
    }

    temp = chartNode.selectSingleNode(LINE_RANGE_STEPS_NODE_LOC);
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

    YAxis yaxis = new YAxis();
    yaxis.setRange(rangeMin, rangeMax, stepforchart);
    yaxis.setStroke(rangeStroke);
    yaxis.setColour(rangeColor);
    yaxis.setGridColour(rangeGridColor);
    chart.setYAxisRight(yaxis);
  }

  @SuppressWarnings("unchecked")
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
  
  @SuppressWarnings("unchecked")
  public void setupRange() {
    int rangeMin = 0;
    int rangeMax = 100;
    int steps = 9;

    String rangeColor = AXIS_COLOR_DEFAULT;
    String rangeGridColor = AXIS_GRID_COLOR_DEFAULT;
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
          List nodes = chartNode.selectNodes(BAR_SERIES_SERIES_NODE_LOC);
          List<String> bars = new ArrayList<String>();
          for (Object node : nodes) {
            if (getValue((Node) node) != null) {
              bars.add(getValue((Node) node));
            }
          }
  
          for (int c = 0; c < getColumnCount(); c++) {
            String text = getColumnHeader(c);
            if (bars.contains(text)) {
              for (int r = 0; r < getRowCount(); r++) {
                if (rangeMin > ((Number) getValueAt(r, c)).intValue())
                  rangeMin = ((Number) getValueAt(r, c)).intValue();
                if (rangeMax < ((Number) getValueAt(r, c)).intValue())
                  rangeMax = ((Number) getValueAt(r, c)).intValue();
              }
            }
          }
        }
      } else if (XYZ_TYPE.equals(datasetType) || XY_TYPE.equals(datasetType)) {
        rangeMin = ((Number) getValueAt(0, 1)).intValue();
        rangeMax = rangeMin;
        // Iterate over 2nd row
        for (int r = 0; r < getRowCount(); r++) {
            if (rangeMin > ((Number) getValueAt(r, 1)).intValue())
              rangeMin = ((Number) getValueAt(r, 1)).intValue();
            if (rangeMax < ((Number) getValueAt(r, 1)).intValue())
              rangeMax = ((Number) getValueAt(r, 1)).intValue();
        }
      } else {
        
        if (isstacked) {
          rangeMin = 0;
          rangeMax = getStackedMaxRange();
        } else {
          rangeMin = ((Number) getValueAt(0, 0)).intValue();
          rangeMax = rangeMin;
          // Iterate over columns 1+
          for (int c = 0; c < getColumnCount(); c++) {
            for (int r = 0; r < getRowCount(); r++) {
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
    
    temp = chartNode.selectSingleNode(RANGE_STEPS_NODE_LOC);
    if (getValue(temp) != null) {
      steps = new Integer(getValue(temp)).intValue();
    }

    temp = chartNode.selectSingleNode(RANGE_COLOR_NODE_LOC);
    if (getValue(temp) != null) {
      rangeColor = getValue(temp);
    }

    temp = chartNode.selectSingleNode(RANGE_GRID_COLOR_NODE_LOC);
    if (getValue(temp) != null) {
      rangeGridColor = getValue(temp);
    }

    temp = chartNode.selectSingleNode(RANGE_STROKE_NODE_LOC);
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
      chart.setXAxis(xaxis);
    } else {
      YAxis yaxis = new YAxis();
      yaxis.setRange(rangeMin, rangeMax, stepforchart);
      yaxis.setStroke(rangeStroke);
      yaxis.setColour(rangeColor);
      yaxis.setGridColour(rangeGridColor);
      chart.setYAxis(yaxis);
    }
  }

  public void setupPieStyles() {
    Node temp = chartNode.selectSingleNode(ANIMATE_NODE_LOC);
    if (getValue(temp) != null) {
      animate = "true".equals(getValue(temp)); //$NON-NLS-1$
    }
    
    temp = chartNode.selectSingleNode(START_ANGLE_NODE_LOC);
    if (getValue(temp) != null) {
      startAngle = Integer.parseInt(getValue(temp));
    }
  }
  
  public void setupBarStyles() {
    barchartstyle = BARCHART_STYLE_DEFAULT;

    // 3d
    Node temp = chartNode.selectSingleNode(IS3D_NODE_LOC);
    if (getValue(temp) != null && "true".equals(getValue(temp))) { //$NON-NLS-1$
      barchartstyle = BarChart.Style.THREED;
      
      // also load 3d height
      temp = chartNode.selectSingleNode(HEIGHT_3D_NODE_LOC);
      if (getValue(temp) != null) {
        threedheight = Integer.parseInt(getValue(temp));
      }
    }
    // Glass
    temp = chartNode.selectSingleNode(ISGLASS_NODE_LOC);
    if (getValue(temp) != null && "true".equals(getValue(temp))) { //$NON-NLS-1$
      barchartstyle = BarChart.Style.GLASS;
    }
    // Sketch
    temp = chartNode.selectSingleNode(ISSKETCH_NODE_LOC);
    if (getValue(temp) != null && "true".equals(getValue(temp))) { //$NON-NLS-1$
      issketch = true;
      // Also load fun factor
      temp = chartNode.selectSingleNode(FUN_FACTOR_NODE_LOC);
      if (getValue(temp) != null) {
        sketchBarFunFactor = Integer.parseInt(getValue(temp));
      } else {
        sketchBarFunFactor = SKETCH_FUNFACTOR_DEFAULT;
      }
    } else {
      issketch = false;
    }

    // Stacked
    temp = chartNode.selectSingleNode(ISSTACKED_NODE_LOC);
    if (getValue(temp) != null) {
      isstacked = "true".equals(getValue(temp)); //$NON-NLS-1$
    }
    
    // Orientation
    temp = chartNode.selectSingleNode(ORIENTATION_NODE_LOC);
    if (getValue(temp) != null) {
      orientation = getValue(temp);
    } else {
      orientation = ORIENTATION_DEFAULT;
    }
  }

  public void setupLineStyles() {
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
  
  void setupDotStyles() {
    Node temp = chartNode.selectSingleNode(DOT_WIDTH_NODE_LOC);
    if (getValue(temp) != null) {
      dotwidth = Integer.parseInt(getValue(temp));
    }
  }

  public void setupDomain() {
    String[] labels = null;
    Number domainMin = null;
    Number domainMax = null;
    Integer stepforchart = null;
    
    if (CATEGORY_TYPE.equals(datasetType)) {
      int rowCount = getRowCount();
      labels = new String[rowCount];
      for (int j = 0; j < rowCount; j++) {
        labels[j] = getRowHeader(j);
      }
    } else if (XYZ_TYPE.equals(datasetType) || XY_TYPE.equals(datasetType)) {
      domainMin = ((Number) getValueAt(0, 0)).intValue();
      domainMax = domainMin;
      // Iterate over rows
      for (int r = 1; r < getRowCount(); r++) {
          if (domainMin.intValue() > ((Number) getValueAt(r, 0)).intValue()) {
            domainMin = ((Number) getValueAt(r, 0)).intValue();
          }
          if (domainMax.intValue() < ((Number) getValueAt(r, 0)).intValue()) {
            domainMax = ((Number) getValueAt(r, 0)).intValue();
          }
      }
      
      int steps = 9;
      int diff = domainMax.intValue() - domainMin.intValue();
      
      Node temp = chartNode.selectSingleNode(DOMAIN_STEPS_NODE_LOC);
      if (getValue(temp) != null) {
        steps = new Integer(getValue(temp)).intValue();
      }
      
      int chunksize = diff / steps;
      
      if (chunksize > 0) {
        stepforchart = new Integer(chunksize);
      }
      
      // If actual min is positive, don't go below ZERO
      if (domainMin.intValue() > 0 && domainMin.intValue() - chunksize < 0) {
        domainMin = 0;
      } else {
        domainMin = domainMin.intValue() - chunksize;
      }
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

    String domainColor = AXIS_COLOR_DEFAULT;
    String domainGridColor = AXIS_GRID_COLOR_DEFAULT;
    int domainStroke = 1;

    Node temp = chartNode.selectSingleNode(DOMAIN_COLOR_NODE_LOC);
    if (getValue(temp) != null) {
      domainColor = getValue(temp);
    }

    temp = chartNode.selectSingleNode(DOMAIN_GRID_COLOR_NODE_LOC);
    if (getValue(temp) != null) {
      domainGridColor = getValue(temp);
    }

    temp = chartNode.selectSingleNode(DOMAIN_STROKE_NODE_LOC);
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
      
      chart.setYAxis(yaxis);
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
      
      chart.setXAxis(xaxis);
    }
  }
  
  //
  // Element Creation Methods
  //

  public void createElements() {
    if (CATEGORY_TYPE.equals(datasetType)) {
      int columnCount;

      // Ignore additional columns for PieCharts
      if (PIECHART_TYPE.equals(chartType)) {
        columnCount = 1;
      } else {
        columnCount = getColumnCount();
      }
      // Create a "series" or element for each column past the first
      for (int i = 0; i < columnCount; i++) {
        elements.add(getElementForColumn(i));
      }
    } else if (XYZ_TYPE.equals(datasetType) || XY_TYPE.equals(datasetType)) {
      
      for (int row = 0; row < getRowCount(); row++) {
        Element e = null;
        String text = getRowHeader(row);        
        if (BUBBLECHART_TYPE.equals(chartType) || DOTCHART_TYPE.equals(chartType)) {
          ScatterChart sc = new ScatterChart(""); //$NON-NLS-1$
          sc.setColour(getColor(row));
          Number x = (Number)getValueAt(row, 0);
          Number y = (Number)getValueAt(row, 1);
          Number z = null; 
          if (XYZ_TYPE.equals(datasetType)) {
            z = (Number)getValueAt(row, 2);
            setupDotSize(sc, z);
          } else {
            if (dotwidth != null) {
              sc.setDotSize(dotwidth);
            }
            
            Node temp = chartNode.selectSingleNode(DOT_LABEL_CONTENT_NODE_LOC);
            if (getValue(temp) != null) {
              sc.setTooltip(MessageFormat.format(getValue(temp), text, 
                  NumberFormat.getInstance().format(x), NumberFormat.getInstance().format(y)));
            } else {
              sc.setTooltip(MessageFormat.format("{0}: {1}, {2}", text,  //$NON-NLS-1$
                  NumberFormat.getInstance().format(x), NumberFormat.getInstance().format(y)));
            }
          }
          sc.addPoint(x.doubleValue(), y.doubleValue());
          
          if (BUBBLECHART_TYPE.equals(datasetType)) {
            Node temp = chartNode.selectSingleNode(BUBBLE_LABEL_CONTENT_NODE_LOC);
            if (getValue(temp) != null) {
              Node temp2 = chartNode.selectSingleNode(BUBBLE_LABEL_Z_FORMAT_NODE_LOC);
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
    
    return lc;
  }
  
  public Element getStackedBarChartFromColumn(int col) {
    if (sbc == null) {
      sbc = new StackedBarChart();
    }

    StackKey key = new StackKey();
    String text = getColumnHeader(col);
    key.setText(text);
    key.setColour(getColor(col));
    sbc.addKeys(key);
    
    for (int row = 0; row < getRowCount(); row++) {
      Stack stack = null;
      if (sbc.getStackCount() > row) {
        stack = sbc.stack(row);
      } else {
        stack = sbc.newStack();
      }
      double d = ((Number) getValueAt(row, col)).doubleValue();
      stack.addStackValues(new StackValue(d, getColor(col)));
    }
    
    return sbc;
  }
  
  public Element getVerticalBarChartFromColumn(int col) {
    if (isstacked) {
      return getStackedBarChartFromColumn(col);
    } else {
      BarChart bc;
      if (issketch) {
        bc = new SketchBarChart();
        ((SketchBarChart) bc).setFunFactor(sketchBarFunFactor);
        ((SketchBarChart) bc).setOutlineColour(getOutlineColor(col));
      } else {
        bc = new BarChart(this.barchartstyle);
        if (this.barchartstyle == Style.THREED && threedheight != null) {
          chart.getXAxis().set3D(threedheight);
        }
      }

      for (int row = 0; row < getRowCount(); row++) {
        double d = ((Number) getValueAt(row, col)).doubleValue();
        bc.addBars(new BarChart.Bar(d));
      }

      bc.setColour(getColor(col));
      
      if (tooltipText != null) {
        bc.setTooltip(tooltipText);
      }

      return bc;
    }
  }
  
  public Element getHorizontalBarChartFromColumn(int col) {
    HorizontalBarChart hbc = new HorizontalBarChart();
    for (int row = 0; row < getRowCount(); row++) {
      double d = ((Number) getValueAt(row, col)).doubleValue();
      hbc.addBars(new HorizontalBarChart.Bar(d));
    }
    hbc.setColour(getColor(col));
    if (tooltipText != null) {
      hbc.setTooltip(tooltipText);
    }
    return hbc;
  }
  
  public Element getPieChartFromColumn(int col) {
    PieChart pc = new PieChart();
    PieChart.Slice[] slices = new PieChart.Slice[getRowCount()];
    for (int row = 0; row < getRowCount(); row++) {
      double d = ((Number) getValueAt(row, col)).doubleValue();
      // Labels are already set - use them
      String label = (String) chart.getXAxis().getLabels().getLabels().get(row);
      
      slices[row] = new PieChart.Slice(d, label, label);
      if (tooltipText != null) {
        slices[row].setTooltip(tooltipText);
      }
    }

    pc.addSlices(slices);
    pc.setColours(this.colors);
    pc.setStartAngle(startAngle);
    pc.setAnimate(animate);

    return pc;
  }
  
  public Element getAreaChartFromColumn(int col) {
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
  
  @SuppressWarnings("unchecked")
  public Element getBarLineChartFromColumn(int col) {
    String text = getColumnHeader(col); 
    // determine if this is a line or a bar
    List nodes = chartNode.selectNodes(BAR_SERIES_SERIES_NODE_LOC);
    List<String> bars = new ArrayList<String>();
    for (Object node : nodes) {
      if (getValue((Node) node) != null) {
        bars.add(getValue((Node) node));
      }
    }
    if (!bars.contains(text)) {
      LineChart lc = getLineChartFromColumn(col);
      lc.setRightYAxis();
      return lc;
    } else {
      return getVerticalBarChartFromColumn(col);
    }
  }
  
  public Element getElementForColumn(int col) {
    Element e = null;
    if (BARCHART_TYPE.equals(chartType) && VERTICAL_ORIENTATION.equals(orientation)) {
      e = getVerticalBarChartFromColumn(col);
    } else if (BARCHART_TYPE.equals(chartType) && HORIZONTAL_ORIENTATION.equals(orientation)) {
      e = getHorizontalBarChartFromColumn(col);
    } else if (LINECHART_TYPE.equals(chartType)) {
      e = getLineChartFromColumn(col);
    } else if (PIECHART_TYPE.equals(chartType)) {
      e = getPieChartFromColumn(col);
    } else if (AREACHART_TYPE.equals(chartType)) {
      e = getAreaChartFromColumn(col);
    } else if (BARLINECHART_TYPE.equals(chartType)) {
      e = getBarLineChartFromColumn(col);
    } else {
      // Log exception, chart not recognized
      log.error(Messages.getString("PentahoOFC4JHelper.ERROR_0001_UNSUPPORTED_CHART_TYPE", chartType)); //$NON-NLS-1$
    }

    // set the title for this series
    e.setText(getColumnHeader(col));

    // set the onclick event to the base url template
    if (null != baseURLTemplate) {
      e.setOn_click(baseURLTemplate);
    }
    
    return e;
  }
  
  //
  // Utility Methods
  //
  
  public String buildCSSStringFromNode(Node n) {
    String fontFamily = getNodeValue(n, FONT_FAMILY_NODE_LOC);
    String fontSize = getNodeValue(n, FONT_SIZE_NODE_LOC);
    String fontWeight = null;
    if ("true".equals(getNodeValue(n, FONT_BOLD_NODE_LOC))) { //$NON-NLS-1$
      fontWeight = "bold"; //$NON-NLS-1$
    }
    String fontStyle = null;
    if ("true".equals(getNodeValue(n, FONT_ITALIC_NODE_LOC))) { //$NON-NLS-1$
      fontStyle = "italic"; //$NON-NLS-1$
    }
    return buildCSSString(fontFamily, fontSize, fontWeight, fontStyle);
  }
  
  public String getNodeValue(Node parent, String node) {
    if (parent == null) {
      return null;
    }
    Node textNode = parent.selectSingleNode(node);
    return getValue(textNode);
  }
  
  public String getValue(Node n) {
    if (n != null && n.getText() != null && n.getText().length() > 0) {
      return n.getText().trim();
    } else {
      return null;
    }
  }

  public String buildCSSString(String fontfamily, String fontsize, String fontweight, String fontstyle) {
    Properties props = new Properties();
    props.put("fontfamily", fontfamily != null ? fontfamily : CSS_FONT_FAMILY_DEFAULT); //$NON-NLS-1$
    props.put("fontsize", fontsize != null ? fontsize : CSS_FONT_SIZE_DEFAULT); //$NON-NLS-1$
    props.put("fontweight", fontweight != null ? fontweight : CSS_FONT_WEIGHT_DEFAULT); //$NON-NLS-1$
    props.put("fontstyle", fontstyle != null ? fontstyle : CSS_FONT_STYLE_DEFAULT); //$NON-NLS-1$
    
    return TemplateUtil.applyTemplate(CSS_FONT_STYLES, props, null);
  }
  
  public String getColor(int i) {
    return colors.get(i%colors.size());
  }
  
  public String getOutlineColor(int i) {
    return outlineColors.get(i % outlineColors.size());
  }
  
  public void setupDotSize(ScatterChart se, Number x) {
    // cache the max x value once looked up
    if (bubbleMaxX == null) {
      Number maxX = 0;
      int rowCount = getRowCount();
      for (int row = 0; row < rowCount; row++) {
        Number currx = (Number)getValueAt(row, 2);
        if(maxX.doubleValue() < currx.doubleValue()) {
          maxX = currx;
        }
      }
      bubbleMaxX = maxX;
    }

    int maxBubbleSize = 100;
    Node bubbleSizeNode = chartNode.selectSingleNode(MAX_BUBBLE_SIZE_NODE_LOC);

    if (getValue(bubbleSizeNode) != null) {
      maxBubbleSize = Integer.parseInt(getValue(bubbleSizeNode));
    }

    se.setDotSize(Integer.valueOf(java.lang.Math.round(maxBubbleSize*(x.floatValue()/bubbleMaxX.floatValue())) ));
  }
}
