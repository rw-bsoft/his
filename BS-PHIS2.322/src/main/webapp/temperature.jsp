<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="org.jfree.chart.title.TextTitle"%>
<%@page import="org.jfree.chart.StandardChartTheme"%>
<%@page import="java.awt.Font"%>
<%@page import="org.jfree.ui.FontDisplayField"%>
<%@page import="java.io.File"%>
<%@page import="phis.application.war.source.temperature.*"%>
<%@ page contentType="text/html;charset=GBK"%>

<%@ page
	import="org.jfree.chart.ChartFactory,               org.jfree.chart.JFreeChart,           org.jfree.chart.plot.PlotOrientation,          org.jfree.chart.servlet.ServletUtilities,      org.jfree.data.category.DefaultCategoryDataset"%>
<%
	ChartProcessor cp = new ChartProcessor(request,response);
TwdChartService ts = new TwdChartService();
Map<String, Object> map = new HashMap<String, Object>();
ts.initAllData(cp, map);
// A4ж╫уе
String filename = cp.createChart("", "", "", "", "", false, 840, 1188);
File file=new File(filename);
String graphURL="";
if(file.exists())return;
graphURL = request.getContextPath() + "/DisplayChart?filename=" + filename;
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>

	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  
  <body>
  	<div id="div_temperature">
    <img id ="img_temperature" src="<%= graphURL %>" >
    </div>
  </body>
</html>