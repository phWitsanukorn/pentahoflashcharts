# Pre Reqs #
  * Pentaho BI Server 2.0x is needed for OFC 2.0.  Download and verify a working installation of Pentaho 2.0x.  The base directory (bi-server-?e) is now referred to as $PENTAHO\_HOME
  * Desire for nice looking charts

# Steps #

  1. Download and unzip the "pentahoflashchart-dist-VERSION.zip" somewhere.  We'll now refer to this as $OFC\_HOME
  1. Copy $OFC\_HOME/resource/open-flash-chart.swf to $PENTAHO\_HOME/tomcat/webapps/pentaho-styles/images/).
  1. Copy the directory "openflashchart" of $OFC\_HOME/samples/ to $PENTAHO\_HOME/pentaho-solutions/.  End result is that there should be a directory (with files at $PENTAHO\_HOME/pentaho-solutions/openflashchart
  1. Copy the file $OFC\_HOME/pentahoflashchart-VERSION.jar" to $PENTAHO\_HOME/tomcat/webapps/pentaho/WEB-INF/lib/)
  1. Copy the jars in $OFC\_HOME/lib to $PENTAHO\_HOME/tomcat/webapps/pentaho/WEB-INF/lib/
  1. Start (or Restart) your Pentaho server.  You may have to "Update the Solution Repository" from the Admin screen if the Solution doesn't show up.

# Optional Steps #
  1. Some features of (y\_axis right, BarLine, etc) use a patched version of OFC2 SWF file.  Copy the the additional .SWF files to $PENTAHO\_HOME/tomcat/webapps/pentaho-styles/images/ if you wish to use these features.  NOTES: Patch came from http://ofc.x10hosting.com/index.html.  For examples, see the Bar Line Chart sample.
  1. Configure the System Listener (stub). Open $PENTAHO\_HOME/pentaho-solutions/system/systemListeners.xml and find the 

&lt;system-listeners&gt;

. Then add the openflashchart listener like these: 

&lt;bean id="OpenFlashChartSystemListener" class="com.google.code.pentahoflashcharts.OpenFlashChartSystemListener"/&gt;


Save it and restart your pentaho server.

# Building Your Own Charts #

Review the document [ComponentUsage](ComponentUsage.md) and use one of the [two chart templates](ChartTemplate.md).  Definitely consider "copying" and pasting from the samples provided.