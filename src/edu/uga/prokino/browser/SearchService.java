package edu.uga.prokino.browser;

import java.io.PrintWriter;
import java.io.IOException;
import java.io.File;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import freemarker.template.Template;

// Recognized URIs:
//
//  URI                 Method  Output  Description
//--------------------------------------------------------------------------------
//
//  /search/{name}              GET     HTML    output the search input page in class {name}
//  /search/result              POST    HTML    output the search result page
//                                                search_text is the search input
//  /explore/{topic}            GET     HTML    output the query page from the Gene viewpoint
//

@Path("/")
public class SearchService
{

    // ProKinO browser object; maintains point of focus (current resource)
    //
    private Browser          browser            = null;

    // data formatters
    private UtilityFormatter        utilityFormatter   = null;

    // this server's URL
    //
    // private String serverURL = null;

    private final String            title              = "ProKinO: Protein Kinase Ontology Browser";

    // constructor
    //
    public SearchService()
    {
        // create a new browser in verbose mode
        //
        browser = new Browser( true );

        // create data formatters
        utilityFormatter = new UtilityFormatter();

        System.out.println( "ProKinOBrowserSearchService created." );
    }

    // /////////////////////////////////////////////////////////////////////////////////////////
    //
    // Service methods
    //

    // GET method to display the informatio about the search in the given class
    // (HTML)
    //
    // classname -- the name of the class to search for individuals
    //
    @GET
    @Path("search/{classname}")
    @Produces("text/html")
    public void showSearchInClass_HTML(
                                       @PathParam("classname") String className,
                                       @Context ServletContext servletContext,
                                       @Context HttpServletRequest req, 
                                       @Context HttpServletResponse res
                                      )
    {
        PrintWriter toClient = null;

        try {

            toClient = res.getWriter();

            String remoteAddr = req.getRemoteAddr();

            System.out.println( "ProKinOBrowserService: [" +
                                remoteAddr +
                                "]: " +
                                "search/" +
                                className );

            if( className.equals( "Gene" ) ||
                className.equals( "Cancer" ) ||
                className.equals( "Pathway" ) ||
                className.equals( "All" ) ) {

                outputSearchStartPage_html( toClient, servletContext, className );

            }
            else {
                utilityFormatter.outputError_html( toClient, servletContext, "Cannot search in class: " + className );
            }

            toClient.close();
        }
        catch( Exception e ) {
            e.printStackTrace( System.err );
            toClient.close();
        }
    }
    
    // POST method to display the search results (HTML)
    //
    // search_text -- the search query
    //
    @POST
    @Path("search/result")
    @Produces("text/html")
    public void showSearchResult_HTML(
                                      @FormParam("search_text") String query,
                                      @FormParam("class_name") String className,
                                      @Context ServletContext servletContext,
                                      @Context HttpServletRequest req, 
                                      @Context HttpServletResponse res
                                     )
    {
        PrintWriter toClient = null;

        try {

            toClient = res.getWriter();

            String remoteAddr = req.getRemoteAddr();

            System.out.println( "ProKinOBrowserService: [" +
                                remoteAddr +
                                "]: " +
                                "search/result in: " + className + ": " + query );

            try {

                List<SearchResult> result = browser.executeSearch( className, query );

                outputSearchResult_html( toClient, servletContext,
                                         className, query, result );

            }
            catch( Exception ex ) {

                System.err.println( ex );
                ex.printStackTrace( System.err );

                utilityFormatter.outputError_html( toClient, servletContext, "ProKinO Internal Error.  Our apologies." );

            }

            toClient.close();
        }
        catch( Exception e ) {
            e.printStackTrace( System.err );
            toClient.close();
        }
    }

    // GET method to display the information about topic exploration (HTML)
    //
    // topicname -- the topic (class) for the exploration view
    //
    @GET
    @Path("explore/{topicname}")
    @Produces("text/html")
    public void showExploreTopic_HTML(@PathParam("topicname") String topicName,
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
                                "explore/" +
                                topicName );

            outputExploreTopicPage_html( toClient, servletContext,
                                         topicName );

            toClient.close();
        }
        catch( Exception e ) {
            e.printStackTrace( System.err );
            toClient.close();
        }
    }

    private void outputSearchStartPage_html(
                                            PrintWriter writer,
                                            ServletContext servletContext, 
                                            String className
                                           )
    {
        freemarker.template.Configuration cfg = null;
        Template template = null;
        Map<String, Object> dataModel = null;
        String templateDir = "WEB-INF/templates";
        String templateName = "search_start.html";

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
            dataModel = createSearchStartDataModel( className );

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

    private void outputSearchResult_html( PrintWriter writer,
                                          ServletContext servletContext,
                                          String className,
                                          String queryText,
                                          List<SearchResult> result )
    {
        freemarker.template.Configuration cfg = null;
        Template template = null;
        Map<String, Object> dataModel = null;
        String templateDir = "WEB-INF/templates";
        String templateName = "search_result.html";

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
            dataModel = createSearchResultDataModel( className, queryText, result );

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

    private void outputExploreTopicPage_html( PrintWriter writer,
                                              ServletContext servletContext,
                                              String topicName )
    {
        String templateName = null;

        if( topicName.equals( "gene" ) )
            templateName = "explore_genes_input.html";
        else if( topicName.equals( "disease" ) )
            templateName = "explore_diseases_input.html";
        else if( topicName.equals( "pathway" ) )
            templateName = "explore_pathways_input.html";
        else {
            utilityFormatter.outputError_html( writer, servletContext, "ProKinO Internal Error.  Our apologies." );
            return;
        }

        try {

            String content = loadStaticHtmlFile( templateName, servletContext );

            writer.write( content );

        }
        catch( Exception e ) {
            e.printStackTrace( System.err );
        }
    }    

    private Map<String, Object> createSearchStartDataModel(String className)
    {
        Map<String, Object> dataModel = null;
        dataModel = new HashMap<String, Object>();

        dataModel.put( "title", title + ": search in " + className );
        dataModel.put( "version", browser.getVersionInfo() );
        dataModel.put( "date", browser.getDate() );

        dataModel.put( "classname", className );

        return dataModel;

    }

    private Map<String, Object> createSearchResultDataModel( 
                                                            String className, 
                                                            String queryText,
                                                            List<SearchResult> result
                                                           )
    {
        Map<String, Object> dataModel = null;
        StringBuffer           output = new StringBuffer();
        String        highlightedText = null;

        dataModel = new HashMap<String, Object>();

        dataModel.put( "title", title + ": search in " + className );
        dataModel.put( "version", browser.getVersionInfo() );
        dataModel.put( "date", browser.getDate() );

        dataModel.put( "classname", className );
        dataModel.put( "no_hits", " " + result.size() );

        if( result.size() > 0 ) {

            output.append( "<div style=\"width: 750px;\">\r\n" );

            int count = 0;
            for( SearchResult row : result ) {

                if( ( count % 10 ) == 0 && count > 0 )
                    output.append( "</div>\r\n" );

                if( ( count % 10 ) == 0 )
                    output.append( "<div class=\"virtualpage hidepiece\">\r\n" );


                //highlightedText = highlightQueryTerms( queryText, row.getLiteral() );
                //highlightedText = row.get( 1 );
                highlightedText = row.getLiteral();

                output.append( "<br>" + row.getEntityType() + ": <a href=\"../resource/" + row.getEntityName() + "\">" + 
                        row.getEntityName() + "</a><br>&nbsp;&nbsp;&nbsp;&nbsp;<small>" + highlightedText + "</small>\r\n" );

                count++;

            }

            // one extra close div may be needed the the number of hits is not
            // a multiple of 10.
            //
            if( ( count % 10 ) < 10 )
                output.append( "</div>\r\n" );

            output.append( "</div>\r\n" );
            output.append( "\r\n" );

            output.append( "<div id=\"paginatediv\" class=\"paginationstyle\" style=\"width: 600px;\">\r\n" +
                           "<a href=\"#\" rel=\"first\">||<<</a> &nbsp;&nbsp;&nbsp;\r\n" +
                           "<a href=\"#\" rel=\"previous\"><<</a> &nbsp;&nbsp;&nbsp;\r\n" +
                           "<span class=\"paginateinfo\"></span>&nbsp;&nbsp;&nbsp;\r\n" +
                           "<a href=\"#\" rel=\"next\">>></a> &nbsp;&nbsp;&nbsp;\r\n" +
                           "<a href=\"#\" rel=\"last\">>>||</a>\r\n" +
                           "</div>" );

            dataModel.put( "results", output.toString() );
        }
        else
            dataModel.put( "results", "No search results" );

        return dataModel;

    }

    /*
     *    // not used for now
    private String highlightQueryTerms( String queryText, String literalText )
    {
        //      SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span class=\"highlight\">","</span>"); 
        //     <span style="background-color: #FFFF00">

        // need explicit package-qualified names to avoid conflicts with ARQ's types
        org.apache.lucene.analysis.SimpleAnalyzer         simpleAnalyzer = null;
        org.apache.lucene.queryParser.QueryParser                 parser = null;
        org.apache.lucene.search.Query                             query = null;
        org.apache.lucene.analysis.TokenStream               tokenStream = null;
        org.apache.lucene.search.highlight.QueryScorer            scorer = null;
        org.apache.lucene.search.highlight.Fragmenter         fragmenter = null;
        org.apache.lucene.search.highlight.SimpleHTMLFormatter formatter = null;
        org.apache.lucene.search.highlight.Highlighter       highlighter = null;

        String                                          formattedLiteral = null;

        formattedLiteral = literalText;

        //      System.out.println("=== Highlighter input: " + literalText ); 

        try {

            simpleAnalyzer = new org.apache.lucene.analysis.SimpleAnalyzer();
            parser = new org.apache.lucene.queryParser.QueryParser( "contents", simpleAnalyzer );

            query = parser.parse( queryText );


            tokenStream = simpleAnalyzer.tokenStream( "contents", 
                                                      new StringReader( literalText ) );
            scorer = new org.apache.lucene.search.highlight.QueryScorer( query, "contents" );
            //      scorer.setExpandMultiTermQuery(true); 

            fragmenter = new org.apache.lucene.search.highlight.SimpleFragmenter(); 

            formatter = 
                //new org.apache.lucene.search.highlight.SimpleHTMLFormatter( "<span style=\"background-color: #FFFF00\">", 
                //new org.apache.lucene.search.highlight.SimpleHTMLFormatter( "<span style=\"background-color: #EEE\">", 
                new org.apache.lucene.search.highlight.SimpleHTMLFormatter( "<span style=\"font-weight:bold;\">",
                                                                            "</span>" );

            highlighter = new org.apache.lucene.search.highlight.Highlighter( formatter, scorer ); 
            highlighter.setTextFragmenter( fragmenter ); 

            //      highlighter.setMaxDocBytesToAnalyze(10000);

            //      String resultString = 
            //          highlighter.getBestFragments(tokenStream,sourceText,1000, "..."); 

            formattedLiteral = highlighter.getBestFragment( tokenStream, literalText );
            if( formattedLiteral.length() < literalText.length() )
                formattedLiteral = "... " + formattedLiteral + " ...";

            //String resultString[] = highlighter.getBestFragments(tokenStream,sourceText,1000 );
            //                                                  "******************\n"); 

        }
        catch( Exception ex ) {
            System.out.println( "ProKinOBrowserService.highlightQueryTerms: " + ex );
            ex.printStackTrace( System.out );
        }

        //      System.out.println("=== Formatted literal = " + formattedLiteral );

        return formattedLiteral;

    }
    */
    
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

