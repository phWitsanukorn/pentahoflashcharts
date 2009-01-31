package com.google.code.pentahoflashcharts.builder;

import java.text.SimpleDateFormat;

import ofc4j.model.Chart;
import ofc4j.model.elements.PieChart;
import ofc4j.model.elements.PieChart.Slice;

import org.dom4j.Node;
import org.pentaho.commons.connection.IPentahoResultSet;

public class PieChartBuilder  extends DefaultChartBuilder {
	private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
	
	public Chart build(Node root, IPentahoResultSet data) {
		Chart c = super.build(root, data);
		return c;
	}
	
	protected void setupElements(Chart c, Node root, IPentahoResultSet data) {
		PieChart e = new PieChart();
		int rowCount = data.getRowCount();
		Node colIndexNode = root.selectSingleNode("/chart/slice/datas/sql-column-index");
		Node labelIndexNode = root.selectSingleNode("/chart/slice/labels/sql-column-index");
		if (colIndexNode != null && colIndexNode.getText().length() > 0) {
			int index = Integer.parseInt(colIndexNode.getText().trim())  ;
			int labelindex = Integer.parseInt(labelIndexNode.getText().trim())  ;
			for (int j = 0; j < rowCount; j++) {
				Object obj = data.getValueAt(j, labelindex-1);
				Number value = (Number) data.getValueAt(j, index-1);
				if (obj instanceof java.sql.Timestamp
						|| obj instanceof java.util.Date) {
					e.addSlice(value.doubleValue(), sf.format(obj));
				} else {
					Slice s = new Slice(value.doubleValue(), obj.toString());
					e.addSlices(s);
				}
			}
		}
		c.addElements(e);
	}
	
	protected void setupOthers(Chart c, Node root, IPentahoResultSet data) {
		PieChart e = (PieChart)c.getElements().iterator().next();
		setStartAngle(root, e);
		setOnClick(e,root,"/chart/on-click");
		setLink(e, root, "/chart/link");
		e.setAlpha(0.3f);
		e.setBorder(2);
		setIsAnimated(root, e); 
		setColorPalette(root, e);
		setTooltip(root, e);
		
	}
	

	protected void setTooltip(Node root, PieChart e) {
		Node tooltipNode = root.selectSingleNode("/chart/tooltip");
		if (tooltipNode!=null&&tooltipNode.getText().length()>0) {
			String tooltip = tooltipNode.getText().trim();
			e.setTooltip(tooltip);
		} else {
			e.setTooltip("#val# of #total#<br>#percent# of 100%");
		}
	}


	protected void setColorPalette(Node root, PieChart e) {
		Node colorsNode = root.selectSingleNode("/chart/slice/color-palette");
		if(colorsNode!=null&&colorsNode.getText().length()>0)
		{
			String[] colors=null;

			colors=fillLabels(colorsNode);
			e.setColours(colors);

		}
	}


	protected void setIsAnimated(Node root, PieChart e) {
		Node isAnimateNode = root.selectSingleNode("/chart/isAnimate");
		if (isAnimateNode != null && isAnimateNode.getText().length() > 0) {
			String str = isAnimateNode.getText().trim();
			if("true".equalsIgnoreCase(str))
			{
				e.setAnimate(true);
			}
			else
				e.setAnimate(false);
		}
	}


	protected void setStartAngle(Node root, PieChart e) {
		Node startAngleNode = root.selectSingleNode("/chart/start-angle");
		if (startAngleNode != null && startAngleNode.getText().length() > 0) {
			e.setStartAngle(Integer.parseInt(startAngleNode.getText().trim()));
		} else
			e.setStartAngle(35);
	}

}
