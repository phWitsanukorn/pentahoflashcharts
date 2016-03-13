**WARNING**: This document is incomplete right now.

# Introduction #

These instructions are valid for the following versions of Pentaho BI Server
  * biserver-ce-CITRUS-M3

# Steps #

  1. cd $PENTAHO\_HOME/tomcat/webapps/pentaho/WEB-INF/lib
  1. rm xstream-1.3.1.jar ofc4j-1.0-alpha5-pentaho.jar
  1. rm -rf $PENTAHO\_HOME/tomcat/webapps/pentaho/openflashchart
  1. cd $PENTAHO\_HOME/pentaho-solutions/system
  1. unzip pentahoflashchart-plugin-0.1-[r218](https://code.google.com/p/pentahoflashcharts/source/detail?r=218).zip (this should create a directory in pentaho-solutions/system named openflashchart)
  1. As an administrator, Use Tools -> Refresh -> Plugins