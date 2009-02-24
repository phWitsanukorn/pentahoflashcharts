package com.google.code.pentahoflashcharts.builder;

import java.util.List;

import ofc4j.model.Chart;
import ofc4j.model.elements.Element;
import ofc4j.model.elements.HorizontalBarChart;

import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;

public class HorizontalBarChartBuilder extends BarChartBuilder {
	
	protected void setupElements(Chart c, Node root, IPentahoResultSet data) {
		HorizontalBarChart hbc = new HorizontalBarChart();
		for (int i = 0; i < data.getRowCount(); i ++ ){
			double d = ((Number) data.getValueAt(i, 1)).doubleValue();
			hbc.addBars(new HorizontalBarChart.Bar(d));
		}
		if(getValue(root.selectSingleNode("/chart/color-palette"))!=null)
		{
			List colors = root.selectNodes("/chart/color-palette/color");
			String colour= getNodeValue((Node)colors.get(0));
			hbc.setColour(colour);
		}
		else
			hbc.setColour("#86BBEF");
		c.addElements(hbc);
		
	}
	
	protected void setupOthers(Chart c, Node root, IPentahoResultSet data) {
		c.getXAxis().setOffset(false);
		c.getYAxis().setOffset(true);
		Element e = (Element)c.getElements().iterator().next();
		setTooltip(root,e);
		
	}
	
	
	
	
	
//	public static IPentahoResultSet test_setupdata () {
//		IPentahoResultSet ips = null;
//		
//		ArrayList<String> colHeaders = new ArrayList<String>();
//		
//		colHeaders.add(0, "DEPARTMENT");
//		colHeaders.add(1, "ACTUAL");
//		colHeaders.add(2, "BUDGET");
//		
//		ArrayList<Object> r1 = new ArrayList<Object>(); 
//		r1.add("Sales"); 
//		r1.add(11); 
//		r1.add(12);
//		ArrayList<Object> r2 = new ArrayList<Object>(); 
//		r2.add("Finance"); 
//		r2.add(14); 
//		r2.add(-9);
//		ArrayList<Object> r3 = new ArrayList<Object>(); 
//		r3.add("Human Resource"); 
//		r3.add(7); 
//		r3.add(100);
//		
//		
//		ArrayList<Object> data = new ArrayList<Object>();  
//		data.add(r1); 
//		data.add(r2);
//		data.add(r3);
//		
//		ips = MemoryResultSet.createFromLists(colHeaders, data);
//		
//		System.out.println(ips.getRowCount());
//		
//		
//		
//		return ips;
//	}
}
