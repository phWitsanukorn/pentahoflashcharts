package com.google.code.pentahoflashcharts.builder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ofc4j.model.Chart;
import ofc4j.model.axis.XAxis;
import ofc4j.model.axis.YAxis;
import ofc4j.model.axis.Label.Rotation;
import ofc4j.model.elements.AreaHollowChart;
import ofc4j.model.elements.LineChart;

import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;

import com.google.code.pentahoflashcharts.OFC4JHelper;

public class LineChartBuilder  extends ChartBuilder {
	private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	public Chart build(Node root, IPentahoResultSet data) {
		 Chart c = new Chart();
		int columnCount = data.getMetaData().getColumnCount();
		LineChart[] elements = null;
		if (columnCount > 1) {
			elements = new LineChart[columnCount - 1];
			int rowCount = data.getRowCount();
			List colors = root.selectNodes("/chart/color-palette/color");
			for (int i = 1; i <= columnCount - 1; i++) {
				LineChart e = new LineChart(LineChart.Style.DOT);
				Number[] datas = new Number[rowCount];

				
					for (int j = 0; j < rowCount; j++) {
						datas[j] = (Number) data.getValueAt(j, i);
						e.addValues(datas[j].doubleValue());
					}
					String colour;
					if(colors!=null&&colors.size()>1)
					{
					colour= ((Node)colors.get(i-1)).getText().trim();
					e.setColour(colour);
					}
					e.setText((String)data.getMetaData().getColumnHeaders()[0][i]);
					setLink(e, root, "/chart/link");
					setOnClick(e,root,"/chart/on-click");
				elements[i - 1] = e;
			}
			String[] labels = new String[rowCount];
			for (int j = 0; j < rowCount; j++) {
				Object obj = data.getValueAt(j, 0);
				if (obj instanceof java.sql.Timestamp
						|| obj instanceof java.util.Date) {
					labels[j] = sf.format(obj);
				} else {
					labels[j] = obj.toString();
				}
			}
			c.setXAxis(new XAxis().addLabels(labels));

		} else {
			elements = new LineChart[columnCount];
			LineChart e = new LineChart(LineChart.Style.DOT);

			Node chartBackGround=root.selectSingleNode("/chart/chart-background");
			if (chartBackGround != null) {

				e.setColour(chartBackGround.getText());
			}
			int rowCount = data.getRowCount();
			Number[] datas = new Number[rowCount];
			for (int i = 0; i < rowCount; i++) {
				datas[i] = (Number) data.getValueAt(i, 0);
			}
			e.addValues(datas);
			elements[0] = e;
			setLink(e, root, "/chart/link");
			setOnClick(e,root,"/chart/on-click");
		}
		c.addElements(elements);

		Node stepsNode = root.selectSingleNode("/chart/y-axis/y-steps");
		Node yMaxNode = root.selectSingleNode("/chart/y-axis/y-max");
		setYAxisRange(c, stepsNode, yMaxNode);
		return c;
	}

}
