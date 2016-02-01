package edu.uga.prokino.browser;

import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.IOException;
import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.Produces;





import freemarker.template.Template;

// Recognized URIs:
//
//  URI                 Method  Output  Description
//--------------------------------------------------------------------------------
//
//  /query/build                      GET     HTML    output the query builder page
//  /query/build/sparqlresult         POST    HTML    output the results of a constructed SPARQL query
//                                                query_text is the query to process
//

@Path("/")
public class QueryBuilderService
{
    // ProKinO browser object; maintains point of focus (current resource)
    //
    private Browser                 browser            = null;

    // data formatters
    private UtilityFormatter        utilityFormatter   = null;

    // this server's URL
    //
    // private String serverURL = null;

    private final String            title              = "ProKinO: Protein Kinase Ontology Browser";

    // constructor
    //
    public QueryBuilderService()
    {
        // create a new browser in verbose mode
        //
        browser = new Browser( true );

        // create data formatters
        utilityFormatter = new UtilityFormatter();

        System.out.println( "QueryBuilderService created." );

    }

    // TO BE CHANGED:done
    // GET method to initiate building of a query (HTML)
    //
    @GET
    @Path("query/build")
    @Produces("text/html")
    public void showQueryBuilder_HTML(
                                      @Context ServletContext servletContext,
                                      @Context HttpServletRequest req,
                                      @Context HttpServletResponse res
                                     )
    {
        PrintWriter toClient = null;
        
        try {

            toClient = res.getWriter();

            String remoteAddr = req.getRemoteAddr();

            System.out.println( "QueryBuilderService: [" +
                                remoteAddr +
                                "]: " +
                                "build" );

            try {

                String content = loadStaticHtmlFile( "sparqlbuilder.html",
                                                     servletContext );

                toClient.write( content );

            }
            catch( Exception ex ) {

                System.out.println( "QueryBuilderService: " + ex );

                utilityFormatter.outputError_html( toClient, servletContext, "ProKinO Internal Error.  Our apologies." );

            }

            toClient.close();
        }
        catch( Exception e ) {
            e.printStackTrace( System.err );
            toClient.close();
        }
    }
    
 // Post method to display built SPARQL query (HTML)
    //
    
    //
    @POST
    @Path("query/built_sparql")
    @Produces("text/html")
    public void showSparqlExampleQuery_HTML(@FormParam("query_panel") String query,
                                            @Context ServletContext servletContext,
                                            @Context HttpServletRequest req,
                                            @Context HttpServletResponse res)
    {
        PrintWriter toClient = null;

        try {

            toClient = res.getWriter();

            String remoteAddr = req.getRemoteAddr();

            System.out.println( "ProKinOBrowserService: [" +
                                remoteAddr +
                                "]: " +
                                "query/built" +
                                query );
            String prfx = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
            		"PREFIX    rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
            		"PREFIX prokino: <http://om.cs.uga.edu/prokino/2.0/#>\n"+
            		"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n\n";
            outputSparqlQueryExamplePage_html( toClient, servletContext,
                                               prfx+query );

            toClient.close();
        }
        catch( Exception e ) {
            e.printStackTrace( System.err );
            utilityFormatter.outputError_html( toClient, servletContext, "ProKinO Internal Error.  Our apologies." );
        }
    }
     
    

    // POST method to display the SPARQL query results (HTML)
    //
    // filename -- the name of the file to download
    //
    @POST
    @Path("query/sparqlresult")
    @Produces("text/html")
    public void showQuerySparqResult_HTML(
                                          @FormParam("query_text") String query,
                                          @FormParam("output_format") String format,
                                          @Context ServletContext servletContext,
                                          @Context HttpServletRequest req,
                                          @Context HttpServletResponse res
                                         )
    {
        PrintWriter toClient = null;

        try {


            String remoteAddr = req.getRemoteAddr();

            System.out.println( "ProKinOBrowserService: [" +
                                remoteAddr +
                                "]: " + "format: " + format + ": " +
                                "query/sparqlresult: " + query );

            try {

                String result = null;

                if( format == null ) {
                    toClient = res.getWriter();
                    result = browser.executeSPARQLQuery( query );
                    outputSparqlQueryResult_html( toClient, servletContext, result );
                }
                else if( format.equals( "html" ) ) {
                    toClient = res.getWriter();
                    result = browser.executeSPARQLQuery( query );
                    outputSparqlQueryResult_html( toClient, servletContext, result );
                }
                else if( format.equals( "csv" ) ) {
                    res.setHeader("Content-Disposition", "attachment; filename=\"QueryResult.csv\"");
                    res.setContentType("application/vnd.ms-excel");

                    OutputStream os = res.getOutputStream();

                    result = browser.executeSPARQLQuery( os, query );

                }


            }
            catch( Exception ex ) {
                ex.printStackTrace( System.err );
                utilityFormatter.outputError_html( toClient, servletContext, "ProKinO Internal Error.  Our apologies." );
            }

            if( format == null || format.equals( "html" ) )
                toClient.close();

        }
        catch( Exception e ) {
            e.printStackTrace( System.err );
            if( format == null || format.equals( "html" ) )
                toClient.close();
        }
    }

    private void outputSparqlQueryResult_html(
                                              PrintWriter writer,
                                              ServletContext servletContext,
                                              String result
                                             )
    {
        freemarker.template.Configuration cfg = null;
        Template template = null;
        Map<String, Object> dataModel = null;
        String templateDir = "WEB-INF/templates";
        String templateName = "sparql_result.html";

        try {

            // Initialize the FreeMarker configuration;
            // - Create a configuration instance
            //
            cfg = new freemarker.template.Configuration();

            cfg.setServletContextForTemplateLoading( servletContext,
                    templateDir );

            // Load templates from the WEB-INF/templates directory of the Web
            // app.
            //
            template = cfg.getTemplate( templateName );

            // Build the data-model
            //
            dataModel = createSparqlResultDataModel( result );

            // Process the template, using the values from the data-model
            // the instance of the template (with substituted values) will be
            // written to the parameter writer (servlet's output)
            //
            template.process( dataModel, writer );

            writer.flush();

        }
        catch( Exception e ) {
            e.printStackTrace( System.err );
        }
    }

    private Map<String, Object> createSparqlResultDataModel(String result)
    {
        Map<String, Object> dataModel = null;

        dataModel = new HashMap<String, Object>();

        dataModel.put( "title", title + ": SPARQL query result " );
        dataModel.put( "version", browser.getVersionInfo() );
        dataModel.put( "date", browser.getDate() );

        dataModel.put( "results", result );

        return dataModel;
    }

    
    private void outputSparqlQueryExamplePage_html(
            PrintWriter writer,
            ServletContext servletContext, 
            String query
           )
{
freemarker.template.Configuration cfg = null;
Template template = null;
Map<String, Object> dataModel = null;
String templateDir = "WEB-INF/templates";
String templateName = "sparql_built_query.html";

try {

// Initialize the FreeMarker configuration;
// - Create a configuration instance
//
cfg = new freemarker.template.Configuration();

cfg.setServletContextForTemplateLoading( servletContext,
templateDir );

// Load templates from the WEB-INF/templates directory of the Web
// app.
//
template = cfg.getTemplate( templateName );

/*String queryText = loadStaticHtmlFile( queryFileName + ".spql",
            servletContext );*/
String queryText = query;

/*String queryExplanation = loadStaticHtmlFile( queryFileName + "-explain.txt",
                   servletContext );*/

// Build the data-model
//
 dataModel = createSparqlBuiltDataModel( queryText );

// Process the template, using the values from the data-model
// the instance of the template (with substituted values) will be
// written to the parameter writer (servlet's output)
//
template.process( dataModel, writer );

writer.flush();

}
catch( Exception e ) {
e.printStackTrace( System.err );
utilityFormatter.outputError_html( writer, servletContext, "ProKinO Internal Error.  Our apologies." );
}
}

    private Map<String, Object> createSparqlBuiltDataModel(String queryText)
    {
        Map<String, Object> dataModel = null;

        dataModel = new HashMap<String, Object>();

        //dataModel.put( "title", title + ": SPARQL query result " );
        dataModel.put( "version", browser.getVersionInfo() );
        dataModel.put( "date", browser.getDate() );

        dataModel.put( "query_text", queryText );
        //dataModel.put( "query_explain", queryExplanation );

        return dataModel;
    }

   

    // //////////////////////////////////////////////////////////
    //
    // Other auxiliary methods
    //

    // Read a static HTML page file fileName from WEB-INF/html
    // and return its content as a String
    //
    private String loadStaticHtmlFile(
                                      String              fileName,
                                      ServletContext      servletContext
                                     )
        throws IOException
    {

        StringBuilder sb = null;
        String        path = null;
        File          file = null;
        Scanner       scanner = null;
        String        NL = null;

        sb = new StringBuilder();
        path = servletContext.getRealPath( "WEB-INF/html/" + fileName );
        file = new File( path );
        scanner = new Scanner(file);
        NL = System.getProperty("line.separator");

        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine() + NL);
        }

        return sb.toString();

    }

}

