package edu.uga.prokino.browser;

import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;


//Recognized URIs:
//
//URI                 Method  Output  Description
//--------------------------------------------------------------------------------
//
///admin/{function}           GET     HTML    get the result of an administrative function
//

@Path("/")
public class AdminService
{
    // ProKinO browser object; maintains point of focus (current resource)
    //
    private Browser          browser            = null;

    // Data formatters
    private UtilityFormatter        utilityFormatter   = null;

    // this server's URL
    //
    // private String serverURL = null;

    private final String            title              = "ProKinO: Protein Kinase Ontology Browser";

    private final int               geneLabelMaxLength = 45;
    

    // constructor
    //
    public AdminService()
    {
        // create a new browser in verbose mode
        //
        browser = new Browser( true );

        utilityFormatter = new UtilityFormatter();

        System.out.println( "ProKinOBrowserAdminService created." );
    }

    // /////////////////////////////////////////////////////////////////////////////////////////
    //
    // Service methods
    //

    // GET method to display the information about a given class (HTML)
    //
    // -- classname -- local name of the class
    //
    @GET
    @Path("admin/{function}")
    @Produces("text/html")
    public void showClassIndividuals_HTML(
            @PathParam("function") String functionName,
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
                    "admin/" +
                    functionName );

            if( functionName != null && functionName.equalsIgnoreCase( "connect" ) ) {
                // connect or re-connect to the ontology service
                String result = browser.reconnect();
                utilityFormatter.outputStringResult_html( toClient, servletContext, result );
            }
            else {
                utilityFormatter.outputError_html( toClient, servletContext, "Unknown function: " + functionName );
            }

            toClient.close();
        }
        catch( Exception e ) {
            e.printStackTrace( System.err );
            toClient.close();
        }
    }

}
