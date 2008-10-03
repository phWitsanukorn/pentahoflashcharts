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

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.core.repository.IContentLocation;
import org.pentaho.core.repository.IContentRepository;
import org.pentaho.core.session.IPentahoSession;
import org.pentaho.core.system.IPentahoSystemListener;
import org.pentaho.repository.HibernateUtil;
import org.pentaho.repository.content.ContentItem;
import org.pentaho.repository.content.ContentRepository;

public class OpenFlashChartSystemListener implements IPentahoSystemListener 
{
	private static final Log log = LogFactory.getLog(OpenFlashChartSystemListener.class);
	public void shutdown() {
		
		
	}

	public boolean startup(IPentahoSession session) {
		log.debug("OpenFlashChartSystemListener startup.......");
		IContentRepository repository = ContentRepository.getInstance(session);
		IContentLocation lc =repository.getContentLocationByPath(OpenFlashChartComponent.LOCATION_DIR_PATH);
		if(lc==null)
		{
			//do nothing
			return true;
		}
		Iterator iterator = lc.getContentItemIterator();
		if(iterator!=null)
		{
			for (; iterator.hasNext();) {
				Object object = (Object) iterator.next();
				if(object instanceof ContentItem)
				{
					ContentItem c = (ContentItem)object;
					String name = c.getName();
					if(name.indexOf("openFlashChart")!=-1)
					{
						log.debug("removed item="+object);
						HibernateUtil.makeTransient(object);
					}
					
				}
				
			}
		}
		HibernateUtil.flushSession();
		return true;
	}

}
