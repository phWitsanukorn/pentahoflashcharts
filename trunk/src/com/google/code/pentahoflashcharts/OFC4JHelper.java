package com.google.code.pentahoflashcharts;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import ofc4j.model.Chart;
import ofc4j.model.Text;
import ofc4j.model.axis.XAxis;
import ofc4j.model.axis.YAxis;
import ofc4j.model.axis.Label.Rotation;
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
	
	/**
	 * 
	 * @param doc
	 * @param data
	 * @return
	 */
	public static Chart convert(Document doc,IPentahoResultSet data) {
		Chart c = new Chart();
		Text t = new Text();
		Element root =doc.getRootElement();
		Node chartType=root.selectSingleNode("/chart/chart-type");
		String cType = chartType.getText().trim();
		Node titleNode = root.selectSingleNode("/chart/title");
		if(titleNode!=null&&titleNode.getText().length()>0)
		{
			c.setTitle(t.setText( titleNode.getText()));
		}
		Node heightNode = root.selectSingleNode("/chart/height");
		Node widthNode = root.selectSingleNode("/chart/width");
		Node chartBackGround = root.selectSingleNode("/chart/chart-background");
		Node stepsNode=root.selectSingleNode("/chart/x-steps");
		Node xMaxNode=root.selectSingleNode("/chart/x-max");
		Node valuesNode = root.selectSingleNode("/chart/values");
		//in the Pentaho chart, range-title equals yLengend title
		Node yLengendNode = root.selectSingleNode("/chart/range-title");
		Node xLengendNode = root.selectSingleNode("/chart/domain-title");
		if(cType.equalsIgnoreCase("BarChart"))
		{
			BarChart e = new BarChart();
			int rowCount = data.getRowCount();
			int columnCount=data.getMetaData().getColumnCount();
			e.addValues(9,8,7,6,5,4,3,2,1);
			c.addElements(e);
		}
		else if(cType.equalsIgnoreCase("AreaChart"))
		{
			AreaHollowChart e = new AreaHollowChart();
			int rowCount = data.getRowCount();
			List<Number> datas = new ArrayList<Number>(30);
			for (float i = 0; i < 6.2; i += 0.2) {
			    datas.add(new Float(Math.sin(i) * 1.9));
			}
			e.addValues(datas);
			e.setWidth(1);
			XAxis xaxis = new XAxis();
			xaxis.setSteps(2);
			xaxis.getLabels().setSteps(4);
			xaxis.getLabels().setRotation(Rotation.VERTICAL);

			YAxis yaxis = new YAxis();
			yaxis.setRange(-2, 2, 2);
			yaxis.setOffset(false);

			c.setYAxis(yaxis);
			c.setXAxis(xaxis);
			c.addElements(e);
		}
		else if(cType.equalsIgnoreCase("LineChart"))
		{
			int columnCount=data.getMetaData().getColumnCount();
			LineChart[] elements = null;
			if(columnCount>1)
			{
				elements=new LineChart[columnCount-1];
				int rowCount = data.getRowCount();
				for (int i = 1; i <=columnCount-1; i++) 
				{
					LineChart e = new LineChart(LineChart.Style.DOT);
					Number[] datas = new Number[rowCount];
					
					if(valuesNode!=null&&valuesNode.getText().length()>0)
					{
						String valueStr = valuesNode.getText().trim();
						StringTokenizer st = new StringTokenizer(valueStr,",");
						while(st.hasMoreTokens())
						{
							String value = st.nextToken();
							e.addValues(Double.parseDouble(value));
						}
						
					}
					else
					{
						for (int j= 0; j < rowCount; j++) 
						{
							datas[j]= (Number)data.getValueAt(j, i);
							e.addValues(datas[j].doubleValue());
						}
						
					}
					elements[i-1]=e;
				}
				String[] labels = new String[rowCount];
				for (int j= 0; j < rowCount; j++) 
				{
					labels[j]= (data.getValueAt(j, 0)).toString();
				}
				c.setXAxis(new XAxis().addLabels(labels));
				
			}
			else
			{
				elements=new LineChart[columnCount];
				LineChart e = new LineChart(LineChart.Style.DOT);
				
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
				if(chartBackGround!=null)
				{
					
					e.setColour(chartBackGround.getText());
				}
				int rowCount = data.getRowCount();
				Number[] datas = new Number[rowCount];
				for (int i = 0; i < rowCount; i++) {
					datas[i]= (Number)data.getValueAt(i, 0);
				}
				e.addValues(datas);
				elements[0]=e;
				
			}
			

			
			
			c.addElements(elements);
		}
		else if(cType.equalsIgnoreCase("PieChart"))
		{
			PieChart e = new PieChart();
			int rowCount = data.getRowCount();
			e.setAnimate(true);
			e.setStartAngle(35);
			e.setBorder(2);
			e.setAlpha(0.6f);
			e.addValues(2, 3);
			e.addSlice(6.5f, "hello (6.5)");
			e.setColours("#d01f3c", "#356aa0", "#C79810");
			e.setTooltip("#val# of #total#<br>#percent# of 100%");
			c.addElements(e);
		}
		else if(cType.equalsIgnoreCase("BarLineChart"))
		{
			BarChart e = new BarChart();
			int rowCount = data.getRowCount();
			
			c.addElements(e);
		}
		
		YAxis yaxis = new YAxis();
		int x_max = 10000;
		int step=10;
		if(stepsNode!=null&&stepsNode.getText().length()>0)
		{
			step = Integer.parseInt(stepsNode.getText().trim());
		}
		
		if(xMaxNode!=null&&xMaxNode.getText().length()>0)
		{
			x_max =Integer.parseInt(xMaxNode.getText().trim());
		}
		yaxis.setRange(0, x_max, step);

		c.setYAxis(yaxis);
		
		if(yLengendNode!=null&&yLengendNode.getText().length()>0)
		{
			Text text = new Text();
			text.setText(yLengendNode.getText().trim());
			text.setStyle("color: #736AFF; font-size: 14px;");
			c.setYLegend(text);
		}
		
		if(xLengendNode!=null&&xLengendNode.getText().length()>0)
		{
			Text text = new Text();
			text.setText(xLengendNode.getText().trim());
			text.setStyle("color: #736AFF; font-size: 14px;");
			c.setXLegend(text);
		}
		return c;
	}

}
