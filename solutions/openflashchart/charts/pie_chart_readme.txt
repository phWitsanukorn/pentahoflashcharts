 How to build Pie Chart

Since OFC2.0 use the json format, the Pie Chart need the following format to show the labels and values. 
For instance, 

{
"title":{"text":  "head1 vs head2" ,"style": "{font-size:18px; color: #d01f3c}"},
"elements":[
	{"type":      "pie",
	"colours":   ["#d01f3c","#356aa0","#C79810"],
  "alpha":     0.6,      
  "border":    2,      
  "animate":   0,      
  "start-angle": 60,      
  "tip":         "%23val%23",      
  "values" :   [
	  {"value":14,"label":"Central"},
	  {"value":27,"label":"Eastern"},
	  {"value":34,"label":"Southern"},
	  {"value":25,"label":"Western"}
	  ]    
  }  
  ]
}

Hence, to get the combination of labels and values, we have two ways.
1. Use the token "${pie_values}", our ofc component will automatically replace this token with the right values.
Note:to use this function, you should obey the rule: the first column in the data should be the value column,
	the second column should be the label column. For instance,
	 select sum(actual) as "Actuals", region  from quadrant_actuals group by region
You can see the ofc_piechart2.xaction for reference.

2. Write a javascript function to combine the labels and values. 
we can add a javascript after the SQL query in the xaction.
For instance,

var rowCount = chart_dataset.getRowCount();
var head3 ="";
for (j = 0; j < rowCount; j++) 
{
head3+="{";
var obj1 = chart_dataset.getValueAt(j,0);
var obj2 = chart_dataset.getValueAt(j,1);
head3+="\"value\":"+obj1;
head3+=",";
head3+="\"label\":"+"\""+obj2+"\"";
head3+="}";

if(j<rowCount-1)
{
	head3+=",";
}
}

In this way, the template can use directly viarable "{head3}" as the values.
You can see the ofc_piechart.xaction for reference.

