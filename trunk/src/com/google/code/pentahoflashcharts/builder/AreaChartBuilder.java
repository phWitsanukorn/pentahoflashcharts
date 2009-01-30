package com.google.code.pentahoflashcharts.builder;

import java.text.SimpleDateFormat;
import java.util.List;

import ofc4j.model.Chart;
import ofc4j.model.axis.XAxis;
import ofc4j.model.elements.AreaHollowChart;

import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;

public class AreaChartBuilder extends ChartBuilder {
	private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
	

	public Chart build(Node root, IPentahoResultSet data) {
		Chart c = new Chart();
//		AreaHollowChart e = new AreaHollowChart();
		int rowCount = data.getRowCount();
		int columnCount = data.getMetaData().getColumnCount();
		AreaHollowChart[] elements = null;
		if (columnCount > 1) {
			elements = new AreaHollowChart[rowCount];
			List colors = root.selectNodes("/chart/color-palette/color");
			for(int n = 0 ; n< rowCount; n ++ )
			{
				AreaHollowChart e = new AreaHollowChart();
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
				e.setWidth(1);
				
			}
			setupXAxis(data, c, columnCount);
		}
		

		c.addElements(elements);


		setupYAxis(root, c);
		return c;
	}
	
	protected void setupXAxis(IPentahoResultSet data, Chart c, int columnCount) {
		String[] labels = new String[columnCount];
		for (int j = 1; j <= columnCount - 1; j++) {
			Object obj = data.getMetaData().getColumnHeaders()[0][j];
			
			labels[j] = obj.toString();
			
		}
		c.setXAxis(new XAxis().addLabels(labels));
	}
	
	protected void setupYAxis(Node root, Chart c) {
		Node stepsNode = root.selectSingleNode("/chart/y-axis/y-steps");
		Node yMaxNode = root.selectSingleNode("/chart/y-axis/y-max");
		setYAxisRange(c, stepsNode, yMaxNode);
	}

	
	
	public static void main(String[] args) {
		AreaChartBuilder b = new AreaChartBuilder();
		
		System.out.println(b.build(null, null));
	}

}
