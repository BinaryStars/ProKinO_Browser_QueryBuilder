package edu.uga.prokino.browser;

import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;




// Recognized URIs:
//
//  URI			Method  Output	Description
//--------------------------------------------------------------------------------
//
//  /resource/{name}		GET	HTML    output resource page for a resource {name}
//       
//  name -- should be a local name of a gene, organism, or any other known individual
//

@Path("/")
public class ResourceService
{

    // ProKinO browser object; maintains the point of focus (current resource)
    private Browser             browser            = null;
    
    // Data formatters
    private GeneFormatter       geneFormatter      = null;
    private OrganismFormatter   organismFormatter  = null;
    private GenericFormatter    genericFormatter   = null;
    private UtilityFormatter    utilityFormatter   = null;
    
    private PageCache           resourceCache      = null;


    // constructor
    //
    public ResourceService()
    {
        // create a new browser in verbose mode
        //
        browser = new Browser( true );
        
        resourceCache = PageCache.getPageCache();

        // create formatters
        geneFormatter = new GeneFormatter();
        organismFormatter = new OrganismFormatter();
        genericFormatter = new GenericFormatter();
        utilityFormatter = new UtilityFormatter();
                
        System.out.println( "ProKinOBrowserResourceService created." );
    }

    // /////////////////////////////////////////////////////////////////////////////////////////
    //
    // Service methods
    //
    // GET method to display the information about a given resource (HTML)
    //
    // -- localname -- local name of the resource
    //
    @GET
    @Path("resource/{localname}")
    @Produces("text/html")
    public void showResource_HTML(
                                  @PathParam("localname") String localName,
                                  @Context ServletContext servletContext,
                                  @Context HttpServletRequest req, 
                                  @Context HttpServletResponse res
                                 )
    {
        ProKinOResource resource = null;
        String          resourcePage = null;

        try {
            PrintWriter toClient = res.getWriter();

            String remoteAddr = req.getRemoteAddr();
            
            // log this request
            System.out.println( "ProKinOBrowserService: [" +
                                remoteAddr +
                                "]: " +
                                "resource/" +
                                localName );
            
            resourcePage = resourceCache.get( localName );

            if( resourcePage != null ) {
                System.out.println( "ProKinOBrowserService.showResource_HTML: from cache: " + localName );
                toClient.write( resourcePage );
                toClient.flush();
            }
            else {

                resource = browser.getProKinOResource( localName );

                if( resource == null ) {
                    utilityFormatter.outputError_html( toClient, servletContext, "Unknown resource: " + localName );
                }
                else {
                    if( resource.isOfType( "Gene" ) ) {
                        geneFormatter.outputGeneResource_html( toClient, servletContext, resource );
                    }
                    else if( resource.isOfType( "Organism" ) ) {
                        organismFormatter.outputOrganismResource_html( toClient, servletContext, resource );
                    }
                    /* UNUSED CODE
                    else if( browser.isKinaseDomain( localName ) ) {
		        System.out.println( "ProKinOBrowserService.showResource_HTML: a kinase domain" );
                        outputKinaseDomainResource_html( toClient, servletContext, resource );
                    }
                    */
                    else {
                        genericFormatter.outputGenericResource_html( toClient, servletContext, resource );
                    }
                }
            }

            toClient.close();
        }
        catch( Exception e ) {
            e.printStackTrace( System.err );
        }
    }
    
}
