package edu.uga.prokino.browser;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import freemarker.template.Template;

// Recognized URIs:
//
//  URI                 Method  Output  Description
//--------------------------------------------------------------------------------
//
//  /schema/class             GET     HTML    output information about all classes
//  /schema/property          GET     HTML    output information about all properties
//  /schema/class/{name}      GET     HTML    output information about the named class
//  /schema/property{name}    GET     HTML    output information about the named property
//

@Path("/")
public class SchemaService
{
    // ProKinO browser object; maintains point of focus (current resource)
    //
    private Browser          browser            = null;

    // data formatters
    //
    private UtilityFormatter        utilityFormatter   = null;

    // this server's URL
    //
    // private String serverURL = null;

    private final String            title              = "ProKinO: Protein Kinase Ontology Browser";
    
    private final String            pubMedURL          = "http://www.ncbi.nlm.nih.gov/pubmed/";


    // constructor
    //
    public SchemaService()
    {
        // create a new browser in verbose mode
        //
        browser = new Browser( true );

        utilityFormatter = new UtilityFormatter();

        System.out.println( "SchemaService created." );

    }
    
    // GET method to display the information about all classes (HTML)
    //
    @GET
    @Path("schema/class")
    @Produces("text/html")
    public void showSchemaClassesPage_HTML(@Context ServletContext servletContext,
                                           @Context HttpServletRequest req, 
                                           @Context HttpServletResponse res)
    {
        PrintWriter toClient = null;

        try {

            toClient = res.getWriter();

            String remoteAddr = req.getRemoteAddr();

            System.out.println( "SchemaService: [" +
                                remoteAddr +
                                "]: " +
                                "/schema/class" );

            outputSchemaClassesPage_html( toClient, servletContext );

            toClient.close();
        }
        catch( Exception e ) {
            e.printStackTrace( System.err );
            toClient.close();
        }
    }

    private void outputSchemaClassesPage_html(PrintWriter toClient,
            ServletContext servletContext)
    {
        // TODO Auto-generated method stub
        
    }

    // GET method to display the information about a particular class (HTML)
    //
    // classname -- the name of the class of interest
    //
    @GET
    @Path("schema/class/{classname}")
    @Produces("text/html")
    public void showSchemaClassPage_HTML(@PathParam("classname") String className,
                                         @Context ServletContext servletContext,
                                         @Context HttpServletRequest req, 
                                         @Context HttpServletResponse res)
    {
        PrintWriter toClient = null;

        try {

            toClient = res.getWriter();

            String remoteAddr = req.getRemoteAddr();

            System.out.println( "SchemaService: [" +
                                remoteAddr +
                                "]: " +
                                "/schema/class/" +
                                className );

            outputSchemaClassPage_html( toClient, servletContext, className );

            toClient.close();
        }
        catch( Exception e ) {
            e.printStackTrace( System.err );
            toClient.close();
        }
    }

    private void outputSchemaClassPage_html(PrintWriter toClient,
            ServletContext servletContext, String className)
    {
        // TODO Auto-generated method stub
        
    }

    // GET method to display the information about all properties (HTML)
    //
    @GET
    @Path("schema/property")
    @Produces("text/html")
    public void showSchemaPropertiesPage_HTML(@Context ServletContext servletContext,
                                              @Context HttpServletRequest req, 
                                              @Context HttpServletResponse res)
    {
        PrintWriter toClient = null;

        try {

            toClient = res.getWriter();

            String remoteAddr = req.getRemoteAddr();

            System.out.println( "SchemaService: [" +
                                remoteAddr +
                                "]: " +
                                "/schema/property" );

            outputSchemaPropertiesPage_html( toClient, servletContext );

            toClient.close();
        }
        catch( Exception e ) {
            e.printStackTrace( System.err );
            toClient.close();
        }
    }
    
    private void outputSchemaPropertiesPage_html(PrintWriter toClient,
            ServletContext servletContext)
    {
        // TODO Auto-generated method stub
        
    }

    // GET method to display the information about a particular property (HTML)
    //
    // propertyname -- the name of the topic of interest
    //
    @GET
    @Path("schema/property/{propertyname}")
    @Produces("text/html")
    public void showSchemaPropertyPage_HTML(@PathParam("propertyname") String propertyName,
                                            @Context ServletContext servletContext,
                                            @Context HttpServletRequest req, 
                                            @Context HttpServletResponse res)
    {
        PrintWriter toClient = null;

        try {

            toClient = res.getWriter();

            String remoteAddr = req.getRemoteAddr();

            System.out.println( "SchemaService: [" +
                                remoteAddr +
                                "]: " +
                                "/schema/property/" +
                                propertyName );

            outputSchemaPropertyPage_html( toClient, servletContext, propertyName );

            toClient.close();
        }
        catch( Exception e ) {
            e.printStackTrace( System.err );
            toClient.close();
        }
    }

    private void outputSchemaPropertyPage_html(PrintWriter toClient,
            ServletContext servletContext, String propertyName)
    {
        // TODO Auto-generated method stub
        
    }
 

}

