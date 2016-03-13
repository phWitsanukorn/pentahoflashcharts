# How to use the on-click event #

I write some examples for showing the usage of on-click event. You can check the ofc\_linechart\_onclick.xaction,ofc\_piechart\_ofc4j\_onclick.xaction and ofc\_bar\_olap.xaction


# Steps #


  * Copy the open-flash-chart-full-embedded-font.swf and open-flash-chartDZ.swf into \pentaho-style\images;
  * In the xaction, add one input string : ofc\_lib\_name. The value of ofc\_lib\_name will tell the OFC use which SWF lib file. In this way, we can use many other powerful patch of OFC flash.
  * In the xaction, add one input string : other\_html\_template. The value of other\_html\_template can be any html fragment. In normal, we will put the javascript function in here.
  * In the chart\_template definition,we add the element: on-click. For instance, 

&lt;on-click&gt;

clickme

&lt;/on-click&gt;


> "clickme" is the javascript function name which defined in  other\_html\_template.