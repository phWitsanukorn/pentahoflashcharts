/*
 * This program is free software; you can redistribute it and/or modify it under the 
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software 
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this 
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html 
 * or from the Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright 2009 Pentaho Corporation.  All rights reserved.
 */
package com.google.code.pentahoflashcharts;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IParameterProvider;
import org.pentaho.platform.api.engine.IPluginResourceLoader;
import org.pentaho.platform.api.repository.IContentItem;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.services.solution.BaseContentGenerator;
import org.pentaho.platform.util.web.MimeHelper;

/**
 * This class should be removed post bi-platform 3.0.  The next release includes 
 * a generic way to load static resources from a plugin, using the plugin.xml file 
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class StaticContentGenerator extends BaseContentGenerator {

  private static final long serialVersionUID = 3058223057846686925L;
  private static final String RESOURCE_PATH = "/resources/"; //$NON-NLS-1$
  
  @Override
  public void createContent() throws Exception {
    IPluginResourceLoader resLoader = PentahoSystem.get(IPluginResourceLoader.class, null);
    
    IContentItem contentItem = outputHandler.getOutputContentItem( "response", "content", "", instanceId, "text/plain"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    
    if( contentItem == null ) {
      error( "StaticContentGenerator.ERROR_0001_NO_CONTENT_ITEM" ); //$NON-NLS-1$
      throw new InvalidParameterException( "StaticContentGenerator.ERROR_0001_NO_CONTENT_ITEM" );  //$NON-NLS-1$
    }
    
    OutputStream out = null;
    out = contentItem.getOutputStream( null );
    if( out == null ) {
      error( "StaticContentGenerator.ERROR_0002_NO_OUTPUT_STREAM" ); //$NON-NLS-1$
      throw new InvalidParameterException( "StaticContentGenerator.ERROR_0002_NO_OUTPUT_STREAM" );  //$NON-NLS-1$
    }
    IParameterProvider pathParams = parameterProviders.get( "path" ); //$NON-NLS-1$
    if( pathParams != null ) {
      String urlPath = pathParams.getStringParameter( "path", null); //$NON-NLS-1$
      if (urlPath.startsWith("/")) { //$NON-NLS-1$
        urlPath = urlPath.substring(1);
      }
      if( StringUtils.isNotEmpty( urlPath ) ) {
        allowBrowserCache( resLoader );
        contentItem.setMimeType(MimeHelper.getMimeTypeFromFileName(urlPath));
        InputStream is = resLoader.getResourceAsStream(StaticContentGenerator.class, RESOURCE_PATH + urlPath);
        IOUtils.copy(is, out);
        return;
      }
    }
  }

  protected void allowBrowserCache( IPluginResourceLoader resLoader ) {
    String maxAge = resLoader.getPluginSetting(StaticContentGenerator.class, "settings/max-age" ); //$NON-NLS-1$
    if( maxAge == null || "0".equals( maxAge )) { //$NON-NLS-1$
      return;
    }
    IParameterProvider pathParams = parameterProviders.get( "path" ); //$NON-NLS-1$
    if( maxAge != null ) {
      HttpServletResponse response = (HttpServletResponse) pathParams.getParameter("httpresponse"); //$NON-NLS-1$
      if( response != null ) {
        response.setHeader( "Cache-Control","max-age=" + maxAge); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
  }
  
  @Override
  public Log getLogger() {
    return LogFactory.getLog(StaticContentGenerator.class);
  }

}
