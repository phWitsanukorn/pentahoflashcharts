Open Flash Chart Component Supported XML Attributes, values

GENERAL
-------

<plot-background type="color">HTML COLOR (ie #FF0000)
<chart-background type="color">HTML COLOR (ie #FF0000)
-- only color is supported for attribute type at the moment

<title> - Title Text
<title-font>
	<font-family> ie Arial
	<size> ie 14
	<is-bold> ie true
	<is-italic> ie true
 - Note that title font attributes are translated to CSS, so any flash CSS supported font is available.


RANGE
-----

<range-minimum> <- int values only
<range-maximum> <- int values only
<range-color>
<range-grid-color>
<range-stroke> font size?
<range-steps> <- if defined, determines how many ticks in the range

DOMAIN
------

<domain-color>
<domain-grid-color>
<domain-stroke> font size?

- domain labels are pulled from the first column (or row if pivoted), for CategoryDataset (the default <dataset-type>)

- For BubbleChart and DotChart (XYSeriesCollection and XYZSeriesCollection), the following apply:
<domain-minimum>
<domain-maximum>
<domain-steps>

LINE RANGE 
----------

for bar lines, identical to range but with "line-" in front, appears on the right of the chart

COLORS
------
Used in bar/line/bubble/ etc for color selection:
<color-palette>
	<color>#FF0000
	<color>#00FF00
	...


LineChart
---------
<chart-type>LineChart
<dot-style>dot OR normal OR hollow</dot-style>
<line-width>Integer value
<dot-width>Integer value
- only CategoryDataset is supported at this time (is this a big deal?)

BarChart
--------
<chart-type>BarChart

<orientation>horizontal OR vertical

The following bar chart styles are mutually exclusive:
<is-3D>true OR false
 - additional 3d values:
	<height-3d>Integer value, optional
<is-glass>true OR false
<is-sketch>true OR false
 - additional sketch properties:
	<fun-factor> 0,1 and 2 are pretty boring. 4 to 6 is a bit fun, 7 and above is lots of fun. 
  - Used in sketch bar for outlines of bars:
	<outline-color-palette>
		<color>#FF0000
		<color>#00FF00
		...


<is-stacked>true OR false

<tooltip>
#top#
#bottom#
#val#

- only CategoryDataset is supported at this time

BarLineChart
------------
<bar-series>
	<series>Series Name 1
	<series>Series Name 2
	...
<line-series>
	<series>Series Name 3
	...
 - all unnamed series are added to the line chart
 - all properties except orientation may apply to both line and bars

AreaChart
---------
 - shares the same properties as LineChart

 - the fill of the area chart is set to the line color, but has an alpha value, so is semi-transparent
 - different from the jfreechart component, does not stack the areas
 - only CategoryDataset is supported at this time (is this a big deal?)

PieChart
--------
<animate>true OR false
<start-angle>Integer in Degrees
<tooltip>
#label#
#key#
#val#
#radius#

DotChart
--------
<dataset-type>XYSeriesCollection</dataset-type>
<dot-width>Integer value
<dot-label-content> <- formatted label, using {0} - series name, {1} - x , {2} - y

BubbleChart
-----------
<dataset-type>XYZSeriesCollection</dataset-type> 
<bubble-label-content> <- formatted label, using {0} - series name, {1} - x , {2} - y {3} - z
<bubble-label-z-format> <- formatted using java decimal format, ie #,###


OpenFlashChartComponent standard inputs and outputs
---------------------------------------------------
inputs:
	chart-data - required - PentahoResultSet
	chart-attributes - required - resource or input defining chart xml
	width - optional - sets the width of the chart, defaults to 300
	height - optional - sets the height of the chart, defaults to 300
	by-row - optional - if true, pivots the data
outputs:
	image-tag

OpenFlashChartComponent additional inputs and outputs
-----------------------------------------------------
inputs:
	ofc_lib_name - optional - allows overriding of the swf file
	ofc_url - optional - allows overriding of the default location of the swf file
outputs:
	html_fragment

note that you may also add {INPUT PARAM} values in the chart-attributes resource or input to parameterize the chart xml.  Note that component-definitions do not get resolved, only input values.

