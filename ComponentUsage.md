# Prerequsities #

  * You need to know how to build .xaction sequences in design studio.
  * You need to know how to build "Get Data From" a source (OLAP, Javascript, or SQL) in an .xaction
  * You need to know how to add a "Custom" component definition to an .xaction using the Design Studio

# Pentaho Flash Chart #

## Component Class ##
`com.google.code.pentahoflashcharts.OpenFlashChartComponent`

## Inputs ##

  * chart\_dataset (_required_) - A Pentaho "result-set" passed from a previous "Get Data From" operation.  You must have configured a SQL/MDX/Javascript object BEFORE this action that retrieved your data
  * chart\_height (_optional_) - The height of the flash chart to render.  DEFAULT: 500
  * chart\_width (_optional_) - The width of the flash chart to render.  DEFAULT: 300
  * chart\_template\_string (_optional_) - The string chart template instead of using a "resource" to use.  See the section on [ChartTemplate](ChartTemplate.md) syntax.  Either chart\_template\_string or chart\_template must be defined.
  * use\_pentaho\_xml (_optional_) - A true/false string that tells the flash chart component to use the Pentaho XML format [XMLTemplates](XMLTemplates.md) or to use the [PentahoFlashChartXMLTemplate](PentahoFlashChartXMLTemplate.md).  DEFAULT: true
  * ofc\_lib\_name (_optional_) - The name of the Open Flash Chart .swf file to use.  Some charts may want to use a "patched" version of the SWF file.  DEFAULT: "open-flash-chart.swf" the standard OFC2 SWF file.
  * other\_html\_fragment (_optional_) - Any additional "HTML" to present with the flash chart.  Useful to create javascript functions for onclick events.  DEFAULT: empty.
  * **ANY OTHER VARIABLE** (_optional_) - You can pass as an input any custom variables (myCustomTitleForChart) that can be used in the [ChartTemplate](ChartTemplate.md)

## Resources ##

  * chart\_template (_optional_) - The "resource", usually a solution file, to use as the [ChartTemplate](ChartTemplate.md).  This is typically used so that you can create a file, "mychartdefinition.txt" that contains the [ChartTemplate](ChartTemplate.md) rather than using a string

## Outputs ##

  * html\_fragment - Contains the HTML fragment that will render the flash chart.  This includes a Flash Object fragment of size chart\_height x chart\_width (from inputs) and a callback location for the open flash chart data file in the content repository.

  * content\_url - Contains the URL of the generated open flash chart data file in the content repository.  This is the [ChartTemplate](ChartTemplate.md) mashed up with the data to make the real data file to display in open flash chart.