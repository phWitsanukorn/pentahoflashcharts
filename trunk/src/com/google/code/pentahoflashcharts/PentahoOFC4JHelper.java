package com.google.code.pentahoflashcharts;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import ofc4j.model.Chart;
import ofc4j.model.Text;
import ofc4j.model.axis.Axis;
import ofc4j.model.axis.XAxis;
import ofc4j.model.axis.YAxis;
import ofc4j.model.elements.BarChart;
import ofc4j.model.elements.Element;
import ofc4j.model.elements.HorizontalBarChart;
import ofc4j.model.elements.LineChart;
import ofc4j.model.elements.PieChart;
import ofc4j.model.elements.BarChart.Style;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.pentaho.commons.connection.IPentahoResultSet;
import org.pentaho.commons.connection.memory.MemoryResultSet;

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
	public static String DOMAIN_TITLE_NODE_LOC = "domain-title";
	public static String DOMAIN_TITLE_FONT_NODE_LOC = "domain-title-font";
	public static String IS3D_NODE_LOC = "is-3D";
	public static String ISGLASS_NODE_LOC = "is-glass";
	public static String ISSKETCH_NODE_LOC = "is-sketch";
	public static String DATASET_TYPE_NODE_LOC = "dataset-type";
	public static String CHART_TYPE_NODE_LOC = "chart-type";
	public static String COLOR_PALETTE_NODE_LOC = "color-palette";
	public static String RANGE_MAXIMUM_NODE_LOC = "range-maximum";
	public static String RANGE_MINIMUM_NODE_LOC = "range-minimum";
	public static String ORIENTATION_NODE_LOC = "orientation";
	public static String PLOT_BACKGROUND_NODE_LOC = "plot-background";
	public static String PLOT_BACKGROUND_COLOR_XPATH = "@type"; //att of plot-background
	public static String CHART_BACKGROUND_NODE_LOC = "chart-background";
	public static String CHART_BACKGROUND_COLOR_XPATH = "@type"; //att of plot-background
	
	
	
	// assume starting at "color-palette" node
	// /color-palette/color
	public static String COLOR_NODE_LOC = "color";
	
	// assume starting at a "*-title-font" node
	public static String FONT_FAMILY_NODE_LOC = "font-family";
	public static String FONT_SIZE_NODE_LOC = "size";
	public static String FONT_BOLD_NODE_LOC = "is-bold";
	public static String FONT_ITALIC_NODE_LOC = "is-italic";
	
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
	
	
	public static String [] COLORS_DEFAULT = {
		"#006666",
		"#0066CC",
		"#009999",
		"#336699",
		"#339966",
		"#3399FF",
		"#663366",
		"#666666",
		"#666699",
		"#669999",
		"#6699CC",
		"#66CCCC",
		"#993300",
		"#999933",
		"#999966",
		"#999999",
		"#9999CC",
		"#9999FF",
		"#99CC33",
		"#99CCCC",
		"#99CCFF",
		"#CC6600",
		"#CC9933",
		"#CCCC33",
		"#CCCC66",
		"#CCCC99",
		"#CCCCCC",
		"#FF9900",
		"#FFCC00",
		"#FFCC66"
	};
	
	// Chart Type Values (CHARTTUPE_NODE_LOC)
	public static String BARCHART_TYPE = "BarChart";
	public static String LINECHART_TYPE = "LineChart";
	public static String PIECHART_TYPE = "PieChart";
	
	// Orientation Type Values (ORIENTATION_NODE_LOC)
	public static String HORIZONTAL_ORIENTATION = "horizontal";
	public static String VERTICAL_ORIENTATION = "vertical";
	
	// Dataset Type Values
	public static String CATEGORY_TYPE = "CategoryDataset";
	
	// *-background Type Values
	public static String COLOR_TYPE = "color";
	
	// Private static members
	private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
	
	// Private members 
	private IPentahoResultSet data;
	private Node chartNode;
	private Chart c;
	private String datasetType;
	private String chartType;
	private String orientation;
	private ArrayList <Element> elements;
	private ArrayList <String> colors;
	private BarChart.Style barchartstyle;
	private LineChart.Style linechartstyle;
	
	
	public static IPentahoResultSet test_setupdata () {
		IPentahoResultSet ips = null;
		
		ArrayList<String> colHeaders = new ArrayList();
		
		colHeaders.add(0, "DEPARTMENT");
		colHeaders.add(1, "ACTUAL");
		colHeaders.add(2, "BUDGET");
		
		ArrayList r1 = new ArrayList(); r1.add("Sales"); r1.add(11); r1.add(12);
		ArrayList r2 = new ArrayList(); r2.add("Finance"); r2.add(14); r2.add(-9);
		ArrayList r3 = new ArrayList(); r3.add("Human Resource"); r3.add(7); r3.add(100);
		
		
		ArrayList data = new ArrayList();  data.add(r1); data.add(r2); data.add(r3);
		
		ips = MemoryResultSet.createFromLists(colHeaders, data);
		
		System.out.println(ips.getRowCount());
		
		
		
		return ips;
	}
	 
	public static void main(String[] args) {
		SAXReader xmlReader = new SAXReader();
		
		Document doc = null;
		try {
			doc = xmlReader.read("/Users/ngoodman/dev/workspace/pentahoflashcharts/solutions/openflashchart/charts/barchart.xml");
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		System.out.println(doc.asXML());
		
		IPentahoResultSet testdata = test_setupdata();
		
		PentahoOFC4JHelper testing = new PentahoOFC4JHelper(doc, testdata);
		
		//java.lang.Thread.sleep(0);
		Chart testChart = testing.convert();
		try{
		    // Create file 
		    FileWriter fstream = new FileWriter("/Users/ngoodman/pentaho/biserver-ce-2.0.0.stable/tomcat/webapps/ofc/testoutput1.json");
		        BufferedWriter out = new BufferedWriter(fstream);
		    out.write(testChart.toString());
		    //Close the output stream
		    out.close();
		    }catch (Exception e){//Catch exception if any
		      System.err.println("Error: " + e.getMessage());
		    }
		
		System.out.println(testChart.toString());
		
		// Line Chart
		
		try {
			doc = xmlReader.read("/Users/ngoodman/dev/workspace/pentahoflashcharts/solutions/openflashchart/charts/linechart.xml");
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		testing = new PentahoOFC4JHelper(doc, testdata);
		
		testChart = testing.convert();
		try{
		    // Create file 
		    FileWriter fstream = new FileWriter("/Users/ngoodman/pentaho/biserver-ce-2.0.0.stable/tomcat/webapps/ofc/testoutput2.json");
		        BufferedWriter out = new BufferedWriter(fstream);
		    out.write(testChart.toString());
		    //Close the output stream
		    out.close();
		    }catch (Exception e){//Catch exception if any
		      System.err.println("Error: " + e.getMessage());
		    }
		
		    System.out.println(testChart.toString());
		    
			// Pie Chart
			
			try {
				doc = xmlReader.read("/Users/ngoodman/dev/workspace/pentahoflashcharts/solutions/openflashchart/charts/piechart.xml");
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			testing = new PentahoOFC4JHelper(doc, testdata);
			
			testChart = testing.convert();
			try{
			    // Create file 
			    FileWriter fstream = new FileWriter("/Users/ngoodman/pentaho/biserver-ce-2.0.0.stable/tomcat/webapps/ofc/testoutput3.json");
			        BufferedWriter out = new BufferedWriter(fstream);
			    out.write(testChart.toString());
			    //Close the output stream
			    out.close();
			    }catch (Exception e){//Catch exception if any
			      System.err.println("Error: " + e.getMessage());
			    }
			
			    System.out.println(testChart.toString());
			    
//			  Horizontal Bar Chart
				
				try {
					doc = xmlReader.read("/Users/ngoodman/dev/workspace/pentahoflashcharts/solutions/openflashchart/charts/barchart_horizontal.xml");
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
				testing = new PentahoOFC4JHelper(doc, testdata);
				
				testChart = testing.convert();
				try{
				    // Create file 
				    FileWriter fstream = new FileWriter("/Users/ngoodman/pentaho/biserver-ce-2.0.0.stable/tomcat/webapps/ofc/testoutput4.json");
				        BufferedWriter out = new BufferedWriter(fstream);
				    out.write(testChart.toString());
				    //Close the output stream
				    out.close();
				    }catch (Exception e){//Catch exception if any
				      System.err.println("Error: " + e.getMessage());
				    }
				
				    System.out.println(testChart.toString());
		

	}
	
	
	public PentahoOFC4JHelper (Document doc, IPentahoResultSet data) {
		
		this.data = data;
		this.c = new Chart();
		elements = new ArrayList<Element>();
		colors = new ArrayList<String>();
		
		this.chartNode = doc.selectSingleNode(this.CHART_NODE_LOC);
			
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
		
		if ( getValue(title) != null ) {
			titleText.setText(getValue(title));
		} else {
			// TODO Figure out a default
			titleText.setText("Title");
		}
		titleText.setStyle(buildCSSStringFromNode(titleFont));
		
		Text domainText = new Text();
		if ( getValue(domainTitle) != null ) {
			domainText.setText(getValue(domainTitle));
		} else {
			// TODO figure out what to do if the header isn't CategoryDataset
			domainText.setText(data.getMetaData().getColumnHeaders()[0][0].toString());
		}
		domainText.setStyle(buildCSSStringFromNode(domainTitleFont));
		
		Text rangeText = new Text();
		if ( getValue(rangeTitle) != null ) {
			rangeText.setText(getValue(rangeTitle));
		} else {
			// TODO set it to ??
			rangeText.setText("Range Title");
		}
		rangeText.setStyle(buildCSSStringFromNode(rangeTitleFont));
		
		
		c.setYLegend(rangeText);
		c.setXLegend(domainText);
		c.setTitle(titleText);
		
	}
	
	
	public static String buildCSSStringFromNode (Node n) {
		
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
			if (fontBoldNode != null
					&& "true".equals(fontBoldNode.getText().trim())) {
				fontWeight = "bold";
			}
			Node fontItalicNode = n.selectSingleNode(FONT_ITALIC_NODE_LOC);
			if (fontItalicNode != null
					&& "true".equals(fontItalicNode.getText().trim())) {
				fontStyle = "italic";
			}
		}		
		return buildCSSString(fontFamily, fontSize, fontWeight, fontStyle);
		
	
	}
	
	public static String getValue(Node n) {
		
		if (n != null && n.getText() != null && n.getText().length() > 0 ) {
			return n.getText().trim();
		} else {
			return null;
		}
		
	}
	public static String buildCSSString(String fontname, String fontsize, String fontweight, String fontstyle) {
		StringBuffer sb = new StringBuffer();
		
		if ( null != fontname ) 
			sb.append(CSS_FONT_FAMILY.replace(ARG, fontname));
		else
			sb.append(CSS_FONT_FAMILY.replace(ARG, CSS_FONT_FAMILY_DEFAULT));

		if ( null != fontsize ) 
			sb.append(CSS_FONT_SIZE.replace(ARG, fontsize));
		else
			sb.append(CSS_FONT_SIZE.replace(ARG, CSS_FONT_SIZE_DEFAULT));
		
		if ( null != fontweight ) 
			sb.append(CSS_FONT_WEIGHT.replace(ARG, fontweight));
		else
			sb.append(CSS_FONT_WEIGHT.replace(ARG, CSS_FONT_WEIGHT_DEFAULT));
		
		if ( null != fontstyle ) 
			sb.append(CSS_FONT_STYLE.replace(ARG, fontstyle));
		else
			sb.append(CSS_FONT_STYLE.replace(ARG, CSS_FONT_STYLE_DEFAULT));
			
		return sb.toString();
		
	}

	public void setupDataAndType() {
		
		Node temp = chartNode.selectSingleNode(DATASET_TYPE_NODE_LOC);
		if ( getValue(temp) != null ) {
			datasetType = getValue(temp);
		} else {
			// Default is CategoricalDataset
			datasetType = DATASET_TYPE_DEFAULT;
		}
		
		temp = chartNode.selectSingleNode(CHART_TYPE_NODE_LOC);
		
		if ( getValue(temp) != null ) {
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
		if ( temp != null ) {
			Object [] colorNodes =  temp.selectNodes(COLOR_NODE_LOC).toArray();

			for (int j = 0 ; j < colorNodes.length; j++ ) {
				colors.add(getValue((Node) colorNodes[j]));
			}
						
		} else {
			for (int i = 0 ; i < COLORS_DEFAULT.length; i ++) {
				colors.add(COLORS_DEFAULT[i]);
			}
		}
		
		// Use either chart-background or plot-background (chart takes precendence)
		temp = chartNode.selectSingleNode(PLOT_BACKGROUND_NODE_LOC);
		if ( getValue(temp) != null ){
			String type = temp.valueOf(PLOT_BACKGROUND_COLOR_XPATH);
			if ( type != null && COLOR_TYPE.equals(type) )
				c.setBackgroundColour(getValue(temp));
		}
		temp = chartNode.selectSingleNode(CHART_BACKGROUND_NODE_LOC);
		if ( getValue(temp) != null ){
			String type = temp.valueOf(CHART_BACKGROUND_COLOR_XPATH);
			if ( type != null && COLOR_TYPE.equals(type) )
				c.setBackgroundColour(getValue(temp));
		}
			
	}

	public Chart convert() {
		
		
		setupDataAndType();
		setupColors();
		setupStyles();
		setupLabels();
		createElements();
		
		setupTitles();
		setupRange();
	
		
		c.addElements(elements);
		
		return c;		

	}
	
	public void setupStyles() {
		
		if (BARCHART_TYPE.equals(chartType)) {
			setupBarStyles();
		}
		if (LINECHART_TYPE.equals(chartType))  setupLineStyles();
		//if (PIECHART_TYPE.equals(chartType)) setupPieStyles();
	}
	
	
	
	public void setupRange() {
		
		int rangeMin = 0;
		int rangeMax = 100;
		int steps = 9;
		
		
		if (CATEGORY_TYPE.equals(datasetType)){
			// Set to first number in our data set
			rangeMin = ((Number) data.getValueAt(0,1)).intValue();
			rangeMax = rangeMin;
			// Iterate over columns 1+
			for (int c = 1; c < data.getColumnCount(); c++ ){
				for (int r = 0; r < data.getRowCount(); r++ ){
					if ( rangeMin > ((Number)data.getValueAt(r,c)).intValue() )
						rangeMin = ((Number)data.getValueAt(r,c)).intValue();
					if ( rangeMax < ((Number)data.getValueAt(r,c)).intValue() )
						rangeMax = ((Number)data.getValueAt(r,c)).intValue();
				}
			}
			
		}
		
		boolean minDefined = false;
		boolean maxDefined = false;
		
		Node temp = chartNode.selectSingleNode(RANGE_MINIMUM_NODE_LOC);
		if ( getValue(temp) != null ){
			rangeMin = new Integer(getValue(temp)).intValue();
			minDefined = true;
		} 
		
		temp = chartNode.selectSingleNode(RANGE_MAXIMUM_NODE_LOC);
		if ( getValue(temp) != null ){
			rangeMax = new Integer(getValue(temp)).intValue();
			maxDefined = true;
		} 
		
		int diff = rangeMax - rangeMin;
		
		int chunksize = diff / steps;
		
		Integer stepforchart = null;
		if ( chunksize > 0 )
			stepforchart = new Integer(chunksize);
		
		// Readjust mins/maxs only if they weren't specified
		if ( !minDefined ) {
			// If actual min is positive, don't go below ZERO
			if ( rangeMin > 0 && rangeMin - chunksize < 0 )
				rangeMin = 0;
			else
				rangeMin = rangeMin - chunksize;
		}
		if ( !maxDefined ) {
			rangeMax = rangeMin + (chunksize * (steps+2));
		}
		
		if ( HORIZONTAL_ORIENTATION.equals(orientation) ){
			XAxis xaxis = new XAxis();
			xaxis.setRange(rangeMin, rangeMax, stepforchart);
			c.setXAxis(xaxis);
		} else {
			YAxis yaxis = new YAxis();
			yaxis.setRange(rangeMin, rangeMax, stepforchart);
			c.setYAxis(yaxis);
		}
		
	}
	
	
	
	public void setupBarStyles () {
		
		barchartstyle = BARCHART_STYLE_DEFAULT;
		
		Node temp = chartNode.selectSingleNode(IS3D_NODE_LOC);
		if ( getValue(temp) != null && "true".equals(getValue(temp)) ) {
			barchartstyle = BarChart.Style.THREED;
		} 
		temp = chartNode.selectSingleNode(ISGLASS_NODE_LOC);
		if ( getValue(temp ) != null && "true".equals(getValue(temp))) {
			barchartstyle = BarChart.Style.GLASS;
		}
		
		temp = chartNode.selectSingleNode(ORIENTATION_NODE_LOC);
		if ( getValue(temp) != null )
			orientation = getValue(temp);
		else
			orientation = ORIENTATION_DEFAULT;
		
	}
	
	public void setupLineStyles () {
		
		linechartstyle = LINECHART_STYLE_DEFAULT;
		// TODO: Swap on Styles
		
	}
	
	
	public void createElements() {	
		
		if ( CATEGORY_TYPE.equals(datasetType)){
			
			int columnCount;
			
			// Ignore additional columns for PieCharts
			if (PIECHART_TYPE.equals(chartType)) columnCount = 2;
			else columnCount = data.getColumnCount();
			
			// Create a "series" or element for each column past the first
			for (int i = 1; i < columnCount; i ++ ) {
				elements.add(getElementForColumn(i));
			}
		}
		
	}
	
    public void setupLabels() {

    	String[] labels = null;
    
    	if ( CATEGORY_TYPE.equals(datasetType) ) {
			int index = 0;
			int rowCount = data.getRowCount();
			labels = new String[rowCount];
			for (int j = 0; j < rowCount; j++) {
				Object obj = data.getValueAt(j, index);
				if (obj instanceof java.sql.Timestamp
						|| obj instanceof java.util.Date) {
					labels[j] = sf.format(obj);
				} else {
					labels[j] = obj.toString();
				}
			}
			
		}
    	
    	if ( HORIZONTAL_ORIENTATION.equals(orientation)) {
    		YAxis yaxis = new YAxis();
    		yaxis.addLabels(labels);
    		c.setYAxis(yaxis);
    	} else {
    		XAxis xaxis = new XAxis();
    		xaxis.addLabels(labels);
    		c.setXAxis(xaxis);
    	}
    		
		
	}
    
	
	public Element getElementForColumn(int n) {

		Element e = null;
		
		
		
		if ( BARCHART_TYPE.equals(chartType) && VERTICAL_ORIENTATION.equals(orientation) ) {
			
			BarChart bc = new BarChart(this.barchartstyle);
			
			for (int i = 0; i < data.getRowCount(); i ++ ){
				double d = ((Number) data.getValueAt(i, n)).doubleValue();
				bc.addBars(new BarChart.Bar(d));
			}
			// TODO wrap around the set of colors if bars.length > colors.length
			bc.setColour(colors.get(n));
	
			e = bc;

		} else if (BARCHART_TYPE.equals(chartType) && HORIZONTAL_ORIENTATION.equals(orientation)) {
			HorizontalBarChart hbc = new HorizontalBarChart();
			for (int i = 0; i < data.getRowCount(); i ++ ){
				double d = ((Number) data.getValueAt(i, n)).doubleValue();
				hbc.addBars(new HorizontalBarChart.Bar(d));
			}
			hbc.setColour(colors.get(n));
			
			e = hbc;
			
		} else if ( LINECHART_TYPE.equals(chartType)) { 
			LineChart lc = new LineChart(this.linechartstyle);
			
			for (int i = 0 ; i < data.getRowCount(); i ++ ){
				double d = ((Number) data.getValueAt(i, n)).doubleValue();
				lc.addDots(new LineChart.Dot(d));
			}
//			 TODO wrap around the set of colors if bars.length > colors.length
			lc.setColour(colors.get(n));
			
			e = lc;
		} else if (PIECHART_TYPE.equals(chartType)){
			PieChart pc = new PieChart();
			PieChart.Slice[] slices = new PieChart.Slice[data.getRowCount()];
			for (int i = 0 ; i < data.getRowCount(); i ++ ){
				double d = ((Number) data.getValueAt(i, n)).doubleValue();
				// Labels are already set - use them
				String label = (String) c.getXAxis().getLabels().getLabels().get(i);
				slices[i] = new PieChart.Slice(d, label);
			}
			
			pc.addSlices(slices);
			pc.setColours(this.colors);
			
			e = pc;
			
		}
		
		String text = (String) data.getMetaData().getColumnHeaders()[0][n];
		
		e.setText(text);
		
		return e;
		
	}
}
