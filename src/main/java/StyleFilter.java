package edu.uark.util.servletfilters;

import java.io.PrintWriter;
import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.lang.Validate;

public class StyleFilter implements Filter
{
	//would have preferred these final but "init(fc)" isn't a constructor
	protected volatile FilterConfig fc = null;
	protected volatile ServletContext sc = null;
	protected volatile String header = "";
	protected volatile String footer = "";

	/**
	 * Set the "header" and "footer" Strings
	 * Override this method to change the initialization technique
	 */
	@Override
	public void init ( final FilterConfig fc ) throws ServletException
	{
		try
		{
			this.fc = fc;
			this.sc = fc.getServletContext();
			
			final Template t = new Template( fc );

			this.header = t.getHeader();
			Validate.notNull( header );
			
			this.footer = t.getFooter();
			Validate.notNull( footer );
		}
		catch ( final Exception e )
		{
			throw new ServletException( e );  //ServletException causes the filter to fail to init, which will force the webserver to reattempt the init next time
		}
	}

	@Override
	public void doFilter ( final ServletRequest request, final ServletResponse response, final FilterChain chain ) throws ServletException
	{
		doFilter( (HttpServletRequest) request, (HttpServletResponse) response, chain );
	}

	protected void doFilter ( final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain ) throws ServletException
	{
		try
		{
			response.setContentType( "text/html" );
			response.setCharacterEncoding( "UTF-8" ); //EhCache SimplePageCachingFilter isn't compatible with the default ISO-8859-1 encoding
			response.setBufferSize( header.length() + 1024 ); //big buffer so we can sendRedirect even after the header is printed.

			final PrintWriter out = response.getWriter();

			out.println( getHeader( request ) );
			chain.doFilter( request, response );
			out.println( getFooter( request ) ); //Shouldn't print this if sendRedirect happened. Seems to not be a problem though.
		}
		catch ( final Exception e )
		{
			throw new ServletException( e );
		}
	}
	
	//allows subclasses to override the behavior and have a header that's not always the same
	protected String getHeader( final HttpServletRequest request ) throws IOException
	{
		return header;
	}

	//allows subclasses to override the behavior and have a footer that's not always the same
	protected String getFooter( final HttpServletRequest request ) throws IOException
	{
		return footer;
	}

	@Override
	public void destroy ()
	{
		//placeholder needed for "implements Filter"
	}
}