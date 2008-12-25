package com.google.code.pentahoflashcharts;

import java.util.Date;

import ofc4j.model.Chart;
import ofc4j.model.axis.XAxis;
import ofc4j.model.axis.YAxis;
import ofc4j.model.elements.BarChart;
import ofc4j.model.elements.LineChart;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;




public class OFC4JHelper {
	
	public Chart generateChart()
	{
		Chart c = new Chart("test");
		XAxis x_axis = new XAxis();
		x_axis.setLabels(new String[]{"2003","2004"});
		x_axis.setSteps(Integer.valueOf(1));
		c.setXAxis(x_axis);
		
		YAxis y_axis=new YAxis ();
		y_axis.addLabels(new String[]{"2003","3000"});
		c.setYAxis(y_axis);
		return c;
	}
	
	
	
	//
	public static void main(String[] args) {
		//render the chart
		Chart c=new Chart(new Date().toString())
		  .addElements(new LineChart()
		    .addValues(9,8,7,6,5,4,3,2,1));
		System.out.println(c);
	}

	public static Chart convert(Document doc,IPentahoResultSet data) {
		Chart c = new Chart();
		Element root =doc.getRootElement();
		Node chartType=root.selectSingleNode("/chart/chart-type");
		String cType = chartType.getText().trim();
		if(cType.equalsIgnoreCase("BarChart"))
		{
			ofc4j.model.elements.Element e = new BarChart();
			int rowCount = data.getRowCount();
			
			c.addElements(e);
		}
		else if(cType.equalsIgnoreCase("AreaChart"))
		{
			ofc4j.model.elements.Element e = new BarChart();
			int rowCount = data.getRowCount();
			
			c.addElements(e);
		}
		else if(cType.equalsIgnoreCase("LineChart"))
		{
			ofc4j.model.elements.Element e = new BarChart();
			int rowCount = data.getRowCount();
			
			c.addElements(e);
		}
		else if(cType.equalsIgnoreCase("PieChart"))
		{
			ofc4j.model.elements.Element e = new BarChart();
			int rowCount = data.getRowCount();
			
			c.addElements(e);
		}
		else if(cType.equalsIgnoreCase("BarLineChart"))
		{
			ofc4j.model.elements.Element e = new BarChart();
			int rowCount = data.getRowCount();
			
			c.addElements(e);
		}
		
		return c;
	}

}
