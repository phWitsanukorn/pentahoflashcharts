/**
 * Copyright [2008] [Bayon Technologies, Inc] 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */



package com.bayontechnologies.bi.pentaho.plugin.openflashchart;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.commons.connection.IPentahoResultSet;
import org.pentaho.core.repository.IContentItem;
import org.pentaho.core.repository.IContentLocation;
import org.pentaho.core.repository.IContentRepository;
import org.pentaho.core.session.IPentahoSession;
import org.pentaho.core.solution.IActionResource;
import org.pentaho.core.system.PentahoSystem;
import org.pentaho.messages.util.LocaleHelper;
import org.pentaho.plugin.jfreechart.ChartComponent;
import org.pentaho.repository.content.ContentRepository;
import org.pentaho.util.UUIDUtil;

/**
 * To integrate the open-flash-chart, we write this component.
 * This component has the following parameters:
 * ?chart_template-----optional,
 * ?chart_template_string-----optional,Contains the open flash chart template for this chart, along with the required tokens for replacing values in the chart with values from the dataset
 * ?chart_dataset (required),
 * ?chart_width,
 * ?chart_height,
 * ?ofc_url.It is the URL of the open flash chart .swf file.  Defaults to the pentaho base URL (style deployment) / images / open-flash-chart.swf.
 * For instance, http://localhost:8080/pentaho-style/images/open-flash-chart.swf.
 *
 * @author tom qin
 *
 */
public class OpenFlashChartComponent extends ChartComponent {
	
	public static final String LOCATION_DIR_PATH = "OpenFlashChart";
	private static final long serialVersionUID = 825147871232129168L;
	private static final Log log = LogFactory.getLog(OpenFlashChartComponent.class);
	private static final String CHART_DATA_PROP = "chart_dataset";
	private static final String CHART_WIDTH = "chart_width"; 
	private static final String CHART_HEIGHT = "chart_height"; 
	private static final String OFC_URL = "ofc_url"; 
	private static final String CHART_TEMPLATE_STRING = "chart_template_string"; 
	private static final String CHART_TEMPLATE = "chart_template"; 
	

	protected int width = 500;
	protected int height = 300;
	protected String template = null;
	protected IPentahoResultSet data =null;
	Map<String,ArrayList<String>> map =null;
	
	protected static String flashFragment;
	static
	{
		flashFragment ="<object classid='clsid:d27cdb6e-ae6d-11cf-96b8-444553540000'"+
		"codebase='http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=8,0,0,0'"+
		"width='{chart-width}' height='{chart-height}' id='graph-2' align='middle'>"+
		"<param name='allowScriptAccess' value='sameDomain' />"+
		"<param name='movie' value='{ofc-url}?width={chart-width}&height={chart-height}&data={data}' />"+
		"<param name='quality' value='high' /><param name='bgcolor' value='#FFFFFF' /> "+
		"<embed src='{ofc-url}?width={chart-width}&height={chart-height}&data={data}' quality='high' bgcolor='#FFFFFF'"+
		"width='{chart-width}' height='{chart-height}' name='open-flash-chart' align='middle' allowScriptAccess='sameDomain'"+
		"type='application/x-shockwave-flash' pluginspage='http://www.macromedia.com/go/getflashplayer' /> </object>";
	}
	
	
	public OpenFlashChartComponent() {
		
	}
	
	public OpenFlashChartComponent(String template,int width,int height,IPentahoResultSet data) 
	{
		this.template = template;
		this.width = width;
		this.height= height;
		this.data = data;
	}
	
	
	protected boolean validateAction() {
		 // See if we have the chart data
	    if (!isDefinedInput(CHART_DATA_PROP)) {
	      inputMissingError(CHART_DATA_PROP);
	      return false;
	    }
	    
	    //See if we have the chart definition
	    if(!isDefinedInput(CHART_TEMPLATE_STRING))
	    {
	    	if(!isDefinedResource(CHART_TEMPLATE))
	    	{
	    		inputMissingError(CHART_TEMPLATE);
	  	      	return false;
	    	}
	    }
	    
		
		return true;
	}
	
	protected boolean executeAction() {
		//get the input datas
		IPentahoResultSet data = (IPentahoResultSet) getInputValue(CHART_DATA_PROP);
		String chartTemplateString = getInputStringValue(CHART_TEMPLATE_STRING);
		Integer chartWidth =null;
		IPentahoSession userSession =this.getSession();
		String input1 = getInputStringValue(CHART_WIDTH);
		if(null==input1)
		{
			chartWidth = new Integer(this.width);
		}
		else
		{
			chartWidth = Integer.valueOf(input1);
		}
		
		
		Integer chartHeight = null;
		String input2 = getInputStringValue(CHART_HEIGHT);
		if(null==input2)
		{
			chartHeight= new Integer(this.height);
		}
		else
		{
			chartHeight= Integer.valueOf(input2);
		}
		
		IActionResource  fileResource=null;
		String ofcURL =  getInputStringValue(OFC_URL);
		if(ofcURL==null||"".equals(ofcURL) )
		{
			ofcURL= "/pentaho-style/images/open-flash-chart.swf";
		}
		
		if(chartTemplateString==null||chartTemplateString.equals(""))
		{
			fileResource = this.getResource(CHART_TEMPLATE);
			try {
				
				chartTemplateString =PentahoSystem.getSolutionRepository(userSession).getResourceAsString(fileResource);
				
				
			} catch (IOException e) {
				error(e.getLocalizedMessage());
				return false;
			}			
		}
		//parse the chart Template String and get the parsed tokens
		map = parseString(chartTemplateString);
		chartTemplateString = replaceChartDefParams(chartTemplateString,data);
		
		//replace the custom variables
		ArrayList<String> customs = map.get("customs");
		Set inputNames=this.getInputNames();
		for (Iterator iterator = inputNames.iterator(); iterator.hasNext();) 
		{
			String name = (String) iterator.next();
			if(customs.contains(name))
			{
				Object value = this.getInputValue(name);
				chartTemplateString=replaceLongStr(chartTemplateString, "{"+name+"}", ""+value);
			}
		}
		
		log.debug("chartTemplateString after replacing:"+chartTemplateString);
		
		
		
		String solutionName = this.getSolutionName();
		//uuid 
		String uuid = 	"openFlashChart-" + UUIDUtil.getUUIDAsString();
		
		
        IContentRepository repository = ContentRepository.getInstance(userSession);
        IContentLocation cl =null;
        cl =  repository.getContentLocationByPath(LOCATION_DIR_PATH);
        if(cl==null)
        	cl=	repository.newContentLocation(LOCATION_DIR_PATH, solutionName, solutionName, this.getId(), true);
        //Fix me, to create a new contentitem every time is not a good choice.
        //Maybe we can remove the older and create a new one. But I can't find the solution right now. :( 
        IContentItem citem=	cl.newContentItem(uuid,uuid, this.getActionTitle(), "txt", "text/plain", "", IContentItem.WRITEMODE_OVERWRITE);
            

		try {
			//get the  outputstream and write the content into the repository.
			OutputStream os = citem.getOutputStream(this.getActionName());
			os.write(chartTemplateString.getBytes(LocaleHelper.getSystemEncoding()));
			os.close();
		} catch (IOException e) {
			error(e.getLocalizedMessage());
			return false;
		}
        //replace the parameters in the flashFragment
		String flashContent = replace( flashFragment,citem.getId(),chartWidth,chartHeight,ofcURL);
        
		log.debug("html_fragment="+flashContent);
		Set outputNames = this.getOutputNames();
		if(outputNames.contains("html_fragment"))
		{
			this.setOutputValue("html_fragment", flashContent);
		}
		if(outputNames.contains("content_url"))
		{
			this.setOutputValue("content_url", this.getTmpContentURL(citem.getId()));
		}
		
		

		return true;
	}
	
	/**
	 * 1.? {colN} will be replaced with a comma separated list of values of the column in N in the dataset.
	 * For instance,x_values={col1}could be replaced with the values in the FIRST column of the data set
	 * x_values=2039,193,8930,3839,1023
 	 * ?2. {minN} will be replaced with the minimum value in the column N in the dataset.
 	 * 3. ?{maxN} will be replaced with the maximum value in the column N in the dataset.
 	 * 4. ?{headN} will be replaced with the ¡°header value¡± for the column N in the dataset.
	 * @param chartTemplateString 
	 * @return the clear chart definition string.
	 */
	public String replaceChartDefParams(String chartTemplateString,IPentahoResultSet data) {
		
		
		ArrayList<String> columns = map.get("colN");
		
		ArrayList<String> maxs = map.get("maxN");
		ArrayList<String> mins = map.get("minN");
		
		
		if(columns.size()==0)
		{
			return chartTemplateString;
		}
		

		String[] colValues = new String[columns.size()];
		for (int i = 0; i < colValues.length; i++) {
			colValues[i]="";
		}
		
		Object[] maxValues = new Object[maxs.size()];
		Object[] minValues = new Object[mins.size()];
		

		int rowCount = data.getRowCount();
		Object max =null;
		Object min = null;
		for (int j = 0; j < rowCount; j++) 
		{
			for (int i = 0; i < columns.size(); i++) 
			{
				String column =  columns.get(i);
				// k is from 1 to N
				int k = Integer.valueOf(column.substring(column.indexOf("col")+"col".length()));
				//get the value of colN
				Object obj = data.getValueAt(j, k-1);
				if(null==obj)
				{
					continue;
				}
				colValues[i]=colValues[i]+obj;
			
				if(j<rowCount-1)
				{
					colValues[i]=colValues[i]+",";
				}
			}
			
			
			//maxN can have more than one
			//minN can have more than one
			for (int i = 0; i < maxs.size(); i++) {
				String maxN = maxs.get(i);
				int k = Integer.valueOf(maxN.substring(maxN.indexOf("max")+"max".length()));
				Object obj = data.getValueAt(j, k-1);
				
				if(null==obj)
				{
					continue;
				}
				if(j==0)
				{
//					max = obj;
//					min = obj;
					if(maxValues.length>0)
					{
						maxValues[i]=obj;
					}
					if(minValues.length>0)
					{
						minValues[i]=obj;
					}
					
					continue;
				}
				if(obj instanceof Comparable)
				{
					
//					if(((Comparable)obj).compareTo(max)>0)
//					{
//						max = obj;
//					}
//					else if(((Comparable)obj).compareTo(min)<0)
//					{
//						min = obj;
//					}
					
					if(maxValues.length>0&&((Comparable)obj).compareTo(maxValues[i])>0)
					{
						maxValues[i] = obj;
					}
					else if(minValues.length>0&&((Comparable)obj).compareTo(minValues[i])<0)
					{
						minValues[i] = obj;
					}
				}
			}
			
			
			
		}
		
		for (int i = 0; i < colValues.length; i++) {
			String values = colValues[i];
			chartTemplateString=replaceLongStr(chartTemplateString, "{"+columns.get(i)+"}", values);
		}
		
		
		//replace the maxN and minN
		for (int i = 0; i < maxs.size(); i++) {
			chartTemplateString=replaceLongStr(chartTemplateString,"{"+maxs.get(i)+"}" , ""+maxValues[i]);
		}
		
		for (int i = 0; i < mins.size(); i++) {
			chartTemplateString=replaceLongStr(chartTemplateString,"{"+mins.get(i)+"}" , ""+minValues[i]);
		}
		
		//replace headers
		Object[][] objs = data.getMetaData().getColumnHeaders();
		if(null==objs)
		{
			return chartTemplateString;
		}	
		Object headers[] = objs[0];
		ArrayList<String> heads = map.get("headN");
		ArrayList<String> colHeaders = new ArrayList<String>(heads.size());
		
		for (int i = 0; i < heads.size(); i++) {
			String head =  heads.get(i);
			// k is from 1 to N
			int k = Integer.valueOf(head.substring(head.indexOf("head")+"head".length()));
			chartTemplateString=replaceLongStr(chartTemplateString,"{"+head+"}" , ""+headers[k-1]);
		}
					
		return chartTemplateString;
	}

	
	
	
	

	/**
	 * Flash Template String Replacement. It is the latest replacement.
	 * @param flashFragment2 the flash template
	 * @param id the id of the content item
	 * @param chartWidth
	 * @param chartHeight
	 * @param ofcURL
	 * @return the working flash codes 
	 */
	public String replace(String flashFragment2, String id,
			Integer chartWidth, Integer chartHeight, String ofcURL) {
		StringBuffer buff = new StringBuffer(flashFragment2);
		//replace the {chart-width}
		String token= "{chart-width}";
		int index;
		replaceByToken( buff,""+chartWidth,token);
		
		
		//replace the {chart-height}
		token = "{chart-height}";
		replaceByToken( buff,""+chartHeight,token);
		
		//replace the {ofc-url}
		token = "{ofc-url}";
		replaceByToken( buff,ofcURL,token);
		
		//replace the {data}
		token = "{data}";
		index = buff.lastIndexOf(token);
		buff.replace(index, index+token.length(), getTmpContentURL(id));
		//replace the {data} again
		index = buff.lastIndexOf(token);
		if(index!=-1)
		{
			buff.replace(index, index+token.length(), getTmpContentURL(id));

		}
		
		log.debug(" flash codes after replacing:"+buff.toString());
		return buff.toString();
	}

	private String getTmpContentURL(String id) {
		return PentahoSystem.getApplicationContext().getBaseUrl()+"GetContent?id="+id;
	}

	private StringBuffer replaceByToken(StringBuffer buff,String value, String token) {
		
		int index = buff.indexOf(token);
		buff.replace(index, index+token.length(), value);
		if(buff.indexOf(token)!=-1)
		{
			//loop replaceByToken again until there is no token at all.
			return replaceByToken(buff,value,token);
		}
		return buff;
	}

	public void done() 
	{
	  //clear something
		
	}
	
	
	public boolean init() {
	    // nothing to do here really
	    return true;
	 }
	
	
	public static Map<String,ArrayList<String>> parseString(final String template)
	{
		Map<String,ArrayList<String>> map = new HashMap<String,ArrayList<String>>();
		ArrayList<String> columns = new ArrayList<String>();
		ArrayList<String> headers = new ArrayList<String>();
		ArrayList<String> maxs = new ArrayList<String>();
		ArrayList<String> mins = new ArrayList<String>();
		ArrayList<String> customs = new ArrayList<String>();
		
		String token1 = "{";
		String token2 = "}";
		String tmpTemplate = template;

		int index = tmpTemplate.indexOf(token1);
		int index2 = tmpTemplate.indexOf(token2);
		while(index!=-1)
		{
			String tmpStr = tmpTemplate.substring(index+1,index2);
			//check the illegal token
			if(tmpStr.indexOf(":")!=-1||tmpStr.indexOf(";")!=-1)
			{
				tmpTemplate = tmpTemplate.substring(0,index-1>=0?index-1:0)+tmpTemplate.substring(index2+1);
				index = tmpTemplate.indexOf(token1);
				index2 = tmpTemplate.indexOf(token2);
				continue;
			}
			
			if(tmpStr.indexOf("col")!=-1)
			{
				if(!columns.contains(tmpStr))
					columns.add(tmpStr);
			}
			else if(tmpStr.indexOf("head")!=-1)
			{
				if(!headers.contains(tmpStr))
				headers.add(tmpStr);
			}
			else if(tmpStr.indexOf("max")!=-1)
			{
				if(!maxs.contains(tmpStr))
				maxs.add(tmpStr);
			}
			else if(tmpStr.indexOf("min")!=-1)
			{
				if(!mins.contains(tmpStr))
				mins.add(tmpStr);
			}
			else //any other my custom variable
			{
				if(!customs.contains(tmpStr))
				customs.add(tmpStr);
			}
			
			tmpTemplate = tmpTemplate.substring(0,index-1>=0?index-1:0)+tmpTemplate.substring(index2+1);
			index = tmpTemplate.indexOf(token1);
			index2 = tmpTemplate.indexOf(token2);
		}
		
		map.put("colN", columns);
		map.put("headN", headers);
		map.put("maxN", maxs);
		map.put("minN", mins);
		map.put("customs", customs);
		return map;
	}
	
	public static String replaceLongStr(String str, String fromStr, String toStr) {
	    StringBuffer result = new StringBuffer();
	    if (str != null && !str.equals("")) {
	      while (str.indexOf(fromStr)!=-1) {
	        result.append(str.substring(0, str.indexOf(fromStr)));
	        result.append(toStr);
	        str = str.substring(str.indexOf(fromStr)+fromStr.length());
	        System.out.println(str);
	      }
	      result.append(str);
	    }
	    return result.toString();
	 }

	
	
//	public static void main(String[] args) {
//		OpenFlashChartComponent test = new OpenFlashChartComponent();
//		String a = "<![CDATA[&is_thousand_separator_disabled=false&&title=Actuals By Region,{font-size:18px; color: #d01f3c}& &x_axis_steps=1& &y_ticks=5,10,5& &line=3,#87421F& &y_min=0& &y_max=20& &pie=60,#505050,{font-size: 12px; color: #404040;}& &values={col1}& &pie_labels={head1}{head1}& &colours=#d01f3c,#356aa0,#C79810& &links=& &tool_tip=%23val%23&]]></default-value>";
//		String token1 = "{";
//		String token2 = "}";
//		int index = a.indexOf(token1);
//		int index2 = a.indexOf(token2);
//		String tmpStr = a.substring(index+1,index2);
////		System.out.println(tmpStr);
////		System.out.println(a.substring(0,index-1>=0?index-1:0)+a.substring(index2+1));
//		System.out.println(parseString(a));
//		String t = "col1";
//		System.out.println(t.substring(t.indexOf("col")+"col".length()));
//		System.out.println(replaceLongStr(a,"{head1}","Header1"));
////		System.out.println(test.replaceByToken(new StringBuffer(a),"{head1}","Header1"));
//		
//		
//	}

}
