package com.google.code.pentahoflashcharts.builder;

import java.util.List;

import ofc4j.model.Chart;
import ofc4j.model.axis.XAxis;
import ofc4j.model.elements.AreaHollowChart;
import ofc4j.model.elements.AreaLineChart;
import ofc4j.model.elements.LineChart;

import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;

public class AreaChartBuilder extends LineChartBuilder {
	
	
	
	public Chart build(Node root, IPentahoResultSet data) {
		Chart c = super.build(root, data);
		return c;
	}
	
	protected void setupElements(Chart c, Node root, IPentahoResultSet data) {
		String dotType = setupDotType(root);
		int rowCount = data.getRowCount();
		int columnCount = data.getMetaData().getColumnCount();
		LineChart[] elements = null;
		if (columnCount > 1) {
			if(DOT_TYPE_HOLLOW.equalsIgnoreCase(dotType))
				elements = new AreaHollowChart[rowCount];
			else
				elements = new AreaLineChart[rowCount];
			List colors = root.selectNodes("/chart/color-palette/color");
			for(int n = 0 ; n< rowCount; n ++ )
			{
				LineChart e = null;
				if(DOT_TYPE_HOLLOW.equalsIgnoreCase(dotType))
				{
					e =new AreaHollowChart();
				}
				else
				{
					e =new AreaLineChart();
				}
					
				Number[] datas = new Number[columnCount - 1];
				for (int i = 1; i <= columnCount - 1; i++) {
					datas[i-1] = (Number) data.getValueAt(n, i);
					e.addValues(datas[i-1].doubleValue());
				}	
				String colour;
				if(colors!=null&&colors.size()>1)
				{
				colour= ((Node)colors.get(n)).getText().trim();
				e.setColour(colour);
				}
				e.setText(""+data.getValueAt(n,0));
				setLink(e, root, "/chart/link");
				setOnClick(e,root,"/chart/on-click");
				elements[n] = e;
				//TODO
				setWidth(e,null);
				
			}
			setupXAxisLabels(data, c, columnCount);
		}
		

		c.addElements(elements);
	}

	private String setupDotType(Node root) {
		if(getValue(root.selectSingleNode("/chart/dot-style"))!=null)
		{
			return getNodeValue(root.selectSingleNode("/chart/dot-style"));
		}
		return "normal";
	}

	protected void setWidth(LineChart e,Node node) {
		e.setWidth(1);
	}

	
	protected void setupXAxisLabels(IPentahoResultSet data, Chart c, int columnCount) {
		String[] labels = new String[columnCount-1];
		for (int j = 1; j <= columnCount - 1; j++) {
			Object obj = data.getMetaData().getColumnHeaders()[0][j];
			
			labels[j-1] = obj.toString();
			
		}
		c.setXAxis(new XAxis().addLabels(labels));
	}
	
}
