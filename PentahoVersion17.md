# Old Pre Reqs #
  * Pentaho Demo 1.7.GA is needed for OFC 1.0.  Download and verify a working installation of Pentaho 1.7.GA.  The base directory (pentaho-demo) is now referred to as $PENTAHO\_HOME

# Old Steps #
  * Download and unzip the "pentahoflashchartcomponent-VERSION.zip" somewhere.  We'll now refer to this as $OFC\_HOME
    * Copy $OFC\_HOME/open-flash-chart.swf to $PENTAHO\_HOME/jboss/server/default/deploy/pentaho-styles.war/images/

  * Copy the directory "openflashchart" of $OFC\_HOME/solutions/ to $PENTAHO\_HOME/pentaho-solutions/.  End result is that there should be a directory (with files at $PENTAHO\_HOME/pentaho-solutions/openflashchart
  * Copy the file $OFC\_HOME/pentahoflashchartcomponent-VERSION.jar" to $PENTAHO\_HOME/jboss/server/default/deploy/pentaho.war/WEB-INF/lib

  * Copy the file $OFC\_HOME/xstream-1.3.jar and ofc4j105.jar" to $PENTAHO\_HOME/jboss/server/default/deploy/pentaho.war/WEB-INF/lib (Pentaho 2.x: $PENTAHO\_HOME/tomcat/webapps/pentaho/WEB-INF/lib/)
  * Start (or Restart) your Pentaho server.  You may have to "Update the Solution Repository" from the Admin screen if the Solution doesn't show up.

# Old Optional Steps #
Configure the System Listener (stub). Open $PENTAHO\_HOME/pentaho-solutions/system/pentaho.xml and find the 

&lt;system-listeners&gt;

. Then add the openflashchart listener like these: 

&lt;openflashchart&gt;

com.bayontechnologies.bi.pentaho.plugin.openflashchart.OpenFlashChartSystemListener

&lt;/openflashchart&gt;


Save it and restart your pentaho server.

# Old Templates #

First off - 95% of the chart template examples and magic is provided by Open Flash Chart.  You should visit their web site for examples on Open Flash Chart datafiles to see how to build Open Flash Charts.

This component is only a means to use a template and combine it with the data to come up with the actual chart datafile.


# Replacements #

  * The component reads the template, and then replaces all occurences of the below tokens with the data from the "chart\_dataset" that was an input to the component.  See [TemplateReplacements](TemplateReplacements.md) for more information


## Example ##

## Template ##
```
&y_min={min1}& 
&y_max={max1} & 
&y_steps=4&
&title=Actual vs Budget by Region,{font-size:20px; color: #bcd6ff; margin:10px; background-color: #5E83BF; padding: 5px 15px 5px 15px;}& 
&y_legend=USD,12,#736AFF&  
&x_labels={col2}& 
&x_axis_colour=#909090& 
&x_grid_colour=#D2D2FB& 
&y_axis_colour=#909090& 
&y_grid_colour=#D2D2FB& 
&bar_glass=55,#D54C78,#C31812,{head3},12& 
&values={col3}& 
&bar_glass_2=55,#5E83BF,#424581,{head4},12& 
&values_2={col4}&
```

## Results ##
```
&y_min=0& 
&y_max=2000000 & 
&y_steps=4&
&title=Actual vs Budget by Region,{font-size:20px; color: #bcd6ff; margin:10px; background-color: #5E83BF; padding: 5px 15px 5px 15px;}& 
&y_legend=USD,12,#736AFF&  
&x_labels=Central, Western, Eastern, Southern& 
&x_axis_colour=#909090& 
&x_grid_colour=#D2D2FB& 
&y_axis_colour=#909090& 
&y_grid_colour=#D2D2FB& 
&bar_glass=55,#D54C78,#C31812,Actuals,12& 
&values=200,400,600,800& 
&bar_glass_2=55,#5E83BF,#424581,Budget,12& 
&values_2=200,400,600,800&
```