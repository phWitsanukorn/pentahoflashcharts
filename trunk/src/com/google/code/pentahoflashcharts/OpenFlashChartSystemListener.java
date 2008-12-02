package com.google.code.pentahoflashcharts;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.IPentahoSystemListener;
import org.pentaho.platform.api.repository.IContentLocation;
import org.pentaho.platform.api.repository.IContentRepository;
import org.pentaho.platform.repository.content.ContentItem;
import org.pentaho.platform.repository.content.ContentRepository;
import org.pentaho.platform.repository.hibernate.HibernateUtil;


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
//						HibernateUtil.makeTransient(object);
						c.makeTransient();
					}
					
				}
				
			}
		}
		HibernateUtil.flushSession();
		return true;
	}

}
