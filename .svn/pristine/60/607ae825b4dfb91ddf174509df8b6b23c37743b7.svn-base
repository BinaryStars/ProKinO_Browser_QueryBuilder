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
//  /about/{name}               GET     HTML    output the information for topic {name}
//  /download/{name}            GET     HTML    output the download information for file {name}
//  /pubmed/{pmid}              GET     HTML    output the summary of a given PubMed {pmid} document
//

@Path("/")
public class InfoService
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
    public InfoService()
    {
        // create a new browser in verbose mode
        //
        browser = new Browser( true );

        utilityFormatter = new UtilityFormatter();

        System.out.println( "InfoService created." );

    }
    
    // GET method to display the information about a particular topic (HTML)
    //
    // topicname -- the name of the topic of interest
    //
    @GET
    @Path("about/{topicname}")
    @Produces("text/html")
    public void showAboutPage_HTML(@PathParam("topicname") String topicName,
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
                                "about/" +
                                topicName );

            if( topicName.equals( "prokino" ) ||
                topicName.equals( "schema" ) ||
                topicName.equals( "datasources" ) ||
                topicName.equals( "browser" ) ) {

                outputAboutPage_html( toClient, servletContext, topicName );

            }
            else {
                utilityFormatter.outputError_html( toClient, servletContext, "No information on topic: " + topicName );
            }

            toClient.close();
        }
        catch( Exception e ) {
            e.printStackTrace( System.err );
            toClient.close();
        }
    }

    // GET method to display the informatio about the download of a given file
    // (HTML)
    //
    // filename -- the name of the file to download
    //
    @GET
    @Path("download/{filename}")
    @Produces("text/html")
    public void showDownloadFile_HTML(@PathParam("filename") String fileName,
            @Context ServletContext servletContext,
            @Context HttpServletRequest req, @Context HttpServletResponse res)
    {
        PrintWriter toClient = null;

        try {

            toClient = res.getWriter();

            String remoteAddr = req.getRemoteAddr();

            System.out.println( "ProKinOBrowserService: [" +
                                remoteAddr +
                                "]: " +
                                "download/" +
                                fileName );

            if( fileName.equals( "prokino" ) ) {

                outputDownloadStartPage_html( toClient, servletContext,
                        fileName );

            }
            else {
                toClient.println( "<html>" );
                toClient.println( "<body>" );
                toClient.println( "Error: no download information for: " +
                                  fileName );
                toClient.println( "</body>" );
                toClient.println( "</html>" );
            }

            toClient.close();
        }
        catch( Exception e ) {
            e.printStackTrace( System.err );
            toClient.close();
        }
    }

    // GET method to display the summary of a given PubMed document (HTML)
    //
    // pmid -- the pubmed identifier of the paper summary to display
    //
    @GET
    @Path("pubmed/{pmid}")
    @Produces("text/html")
    public void showPubMedSummary_HTML(@PathParam("pmid") String pmid,
            @Context ServletContext servletContext,
            @Context HttpServletRequest req, @Context HttpServletResponse res)
    {
        PrintWriter toClient = null;

        try {

            res.setContentType( "text/html" );

            toClient = res.getWriter();

            String remoteAddr = req.getRemoteAddr();

            System.out.println( "ProKinOBrowserService: [" +
                                remoteAddr +
                                "]: " +
                                "pubmed/" +
                                pmid );

            String summary = getPubMedSummary( pmid );

            int prePos = summary.indexOf( "1:" );
            int endPrePos = summary.indexOf( "</pre>" );

            summary = summary.substring( prePos + 2, endPrePos );

            summary = "<html><body><a href=\"" +
                      pubMedURL +
                      pmid +
                      "\" target=\"_blank\" class=\"navlink\">Click to go to PubMed for the full record</a> (opens new window/tab)<p>" +
                      summary +
                      "</body></html>";

            if( summary.length() > 0 ) {

                toClient.print( summary );
            }
            else {
                toClient.println( "<html>" );
                toClient.println( "<body>" );
                toClient.println( "Error: no summary for PubMed id: " + pmid );
                toClient.println( "</body>" );
                toClient.println( "</html>" );
            }

            toClient.close();
        }
        catch( Exception e ) {
            e.printStackTrace( System.err );
            toClient.close();
        }
    }

    private void outputDownloadStartPage_html(PrintWriter writer,
            ServletContext servletContext, String fileName)
    {
        freemarker.template.Configuration cfg = null;
        Template template = null;
        Map<String, Object> dataModel = null;
        String templateDir = "WEB-INF/templates";
        String templateName = "download_start.html";

        try {

            // Initialize the FreeMarker configuration;
            // - Create a configuration instance
            //
            cfg = new freemarker.template.Configuration();

            cfg.setServletContextForTemplateLoading( servletContext,
                    templateDir );

            // Load templates from the WEB-INF/templates directory of the Web app.
            //
            template = cfg.getTemplate( templateName );

            // Build the data-model
            //
            dataModel = createDownloadStartDataModel( fileName );

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

    private Map<String, Object> createDownloadStartDataModel(String fileName)
    {
        Map<String, Object> dataModel = null;

        dataModel = new HashMap<String, Object>();

        dataModel.put( "title", title + ": download " + fileName );
        dataModel.put( "version", browser.getVersionInfo() );
        dataModel.put( "date", browser.getDate() );

        dataModel.put( "filename", fileName );

        return dataModel;

    }

    private String getPubMedSummary(String pmid)
    {
        StringBuffer summary = new StringBuffer( 10000 );

        try {
            URL url = new URL( "http://www.ncbi.nlm.nih.gov/sites/entrez?cmd=retrieve&report=DocSum&format=text&uid=" +
                               pmid );

            URLConnection connection = url.openConnection();

            connection.setDoInput( true );

            InputStream inStream = connection.getInputStream();

            BufferedReader input = new BufferedReader( new InputStreamReader(
                    inStream ) );

            String line = "";
            summary.setLength( 0 );

            while( ( line = input.readLine() ) != null ) {

                summary.append( line );

            }

        }
        catch( Exception e ) {
            System.out.println( e.toString() );
        }

        return summary.toString();

    }

    private void outputAboutPage_html(PrintWriter writer,
            ServletContext servletContext, String topicName)
    {
        freemarker.template.Configuration cfg = null;
        Template template = null;
        Map<String, Object> dataModel = null;
        String templateDir = "WEB-INF/templates";
        String templateName = "about_topic.html";

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
            dataModel = createAboutTopicDataModel( topicName );

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
    
    private Map<String, Object> createAboutTopicDataModel(String topicName)
    {
        Map<String, Object> dataModel = null;

        dataModel = new HashMap<String, Object>();

        dataModel.put( "title", title + ": about " + topicName );
        dataModel.put( "version", browser.getVersionInfo() );
        dataModel.put( "date", browser.getDate() );

        dataModel.put( "topicname", topicName );

        return dataModel;

    }

}
