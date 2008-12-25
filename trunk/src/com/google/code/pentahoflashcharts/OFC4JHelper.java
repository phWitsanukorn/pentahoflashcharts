package com.google.code.pentahoflashcharts;

import java.util.Date;

import ofc4j.model.Chart;
import ofc4j.model.axis.XAxis;
import ofc4j.model.axis.YAxis;
import ofc4j.model.elements.AreaHollowChart;
import ofc4j.model.elements.BarChart;
import ofc4j.model.elements.LineChart;
import ofc4j.model.elements.PieChart;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;




public class OFC4JHelper {
	public static final String DEFAULT_WIDTH="500";
	public static final String DEFAULT_HEIGHT="300";
	
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
		Chart c = new Chart(new Date().toString());
		Element root =doc.getRootElement();
		Node chartType=root.selectSingleNode("/chart/chart-type");
		String cType = chartType.getText().trim();
		Node titleNode = root.selectSingleNode("/chart/title");
		Node heightNode = root.selectSingleNode("/chart/height");
		Node widthNode = root.selectSingleNode("/chart/width");
		if(cType.equalsIgnoreCase("BarChart"))
		{
			BarChart e = new BarChart();
			int rowCount = data.getRowCount();
			
			c.addElements(e);
		}
		else if(cType.equalsIgnoreCase("AreaChart"))
		{
			AreaHollowChart e = new AreaHollowChart();
			int rowCount = data.getRowCount();
			
			c.addElements(e);
		}
		else if(cType.equalsIgnoreCase("LineChart"))
		{
			LineChart e = new LineChart();
			int rowCount = data.getRowCount();
			if(widthNode!=null)
			{
				String width = widthNode.getText();
				if(width==null||width.length()==0)
				{
					width=DEFAULT_WIDTH;
				}
				else
					width = width.trim();
				e.setWidth(Integer.valueOf(width));
			}
			e.addValues(9,8,7,6,5,4,3,2,1);
			c.addElements(e);
		}
		else if(cType.equalsIgnoreCase("PieChart"))
		{
			PieChart e = new PieChart();
			int rowCount = data.getRowCount();
			
			c.addElements(e);
		}
		else if(cType.equalsIgnoreCase("BarLineChart"))
		{
			BarChart e = new BarChart();
			int rowCount = data.getRowCount();
			
			c.addElements(e);
		}
		
		return c;
	}

}
