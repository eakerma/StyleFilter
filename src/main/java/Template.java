package edu.uark.util.servletfilters;

import java.io.IOException;
import javax.servlet.FilterConfig;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.StringUtils;

import edu.uark.util.ServletContextUtils;

//@Immutable
class Template
{
	private final String header;
	private final String footer;
	
	protected Template ( final FilterConfig fc ) throws IOException
	{
		final String delimiter = "<!--content-->";

		final String template = getTemplateAsString( fc );
		Validate.notEmpty( template, "Template must not be empty." );
		Validate.isTrue( template.contains( delimiter ), "Template must contain " + delimiter );

		this.header = template.split( delimiter )[0];
		Validate.notNull( header );

		this.footer = template.split( delimiter )[1];
		Validate.notNull( footer );
	}

	protected static String getTemplateAsString( final FilterConfig fc ) throws IOException
	{
		Validate.notNull( fc );
	
		final String resource = StringUtils.defaultIfEmpty( fc.getInitParameter( "resource" ), "style.html" );

		final String template = ServletContextUtils.getResourceAsString( fc.getServletContext(), resource );
		Validate.notNull( template, resource + " not found." );

		final String title = StringUtils.defaultString( fc.getInitParameter( "title" ), "" ); //this feature mostly exists so the style.html can match across projects.
		
		return template.replace( "<!--title-->", title );
	}

	protected String getHeader()
	{
		return header;
	}
	
	protected String getFooter()
	{
		return footer;
	}
}