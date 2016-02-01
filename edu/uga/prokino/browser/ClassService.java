package edu.uga.prokino.browser;

import java.io.PrintWriter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;




import freemarker.template.Template;

// Recognized URIs:
//
//  URI                 Method  Output  Description
//--------------------------------------------------------------------------------
//
//  /class/{name}               GET     HTML    output all instances for class {name}
//  /hierarchy/{name}           GET     HTML    output the entire hierarchy (ProteinKinaseDomain)
//

@Path("/")
public class ClassService
{

    // ProKinO browser object; maintains point of focus (current resource)
    //
    private Browser                 browser            = null;

    // Data formatters
    private UtilityFormatter        utilityFormatter   = null;

    // this server's URL
    //
    // private String serverURL = null;

    private final String            title              = "ProKinO: Protein Kinase Ontology Browser";

    private final int               geneLabelMaxLength = 45;

    // constructor
    //
    public ClassService()
    {
        // create a new browser in verbose mode
        //
        browser = new Browser( true );

        utilityFormatter = new UtilityFormatter();

        System.out.println( "ProKinOBrowserClassService (2) created." );
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
    @Path("class/{classname}")
    @Produces("text/html")
    public void showClassIndividuals_HTML(
                                          @PathParam("classname") String className,
                                          @Context ServletContext servletContext,
                                          @Context HttpServletRequest req, 
                                          @Context HttpServletResponse res
                                         )
    {
        List<ResourceNode> indivs = null;
        PrintWriter toClient = null;

        try {

            toClient = res.getWriter();

            String remoteAddr = req.getRemoteAddr();

            System.out.println( "ProKinOBrowserService: [" +
                                remoteAddr +
                                "]: " +
                                "class/" +
                                className );

            indivs = browser.getAllClassIndivs( className );

            if( indivs == null ) {
                utilityFormatter.outputError_html( toClient, servletContext, "Unknown class: " + className );
            }
            else {
                outputClassIndividuals_html( toClient, servletContext, className, indivs );
            }

            toClient.close();
        }
        catch( Exception e ) {
            e.printStackTrace( System.err );
            utilityFormatter.outputError_html( toClient, servletContext, "ProKinO Internal Error.  Our apologies." );
            toClient.close();
        }
    }

    // GET method to display the information about the Protein Kinase Hierarchy
    // (HTML)
    //
    // domainname -- the local name of the domain; currently, fixed as
    // ProteinKinaseDomain
    //
    @GET
    @Path("hierarchy/{domainname}")
    @Produces("text/html")
    public void showKinaseDomainHierarchy_HTML(
                                                @PathParam("domainname") String domainName,
                                                @Context ServletContext servletContext,
                                                @Context HttpServletRequest req, 
                                                @Context HttpServletResponse res
                                               )
    {
        KinaseDomainNode hierarchyRoot = null;
        PrintWriter toClient = null;

        try {

            toClient = res.getWriter();

            String remoteAddr = req.getRemoteAddr();

            System.out.println( "ProKinOBrowserService: [" +
                                remoteAddr +
                                "]: " +
                                "hierarchy/" +
                                domainName );

            hierarchyRoot = browser.getKinaseDomainHierarchy();
            
            System.out.println( "showKinaseDomainHierarchy_HTML: have hierarchyRoot" );
            System.out.flush();

            if( hierarchyRoot == null ) {
                utilityFormatter.outputError_html( toClient, servletContext, "hierarchy does not exist for: " + domainName );
            }
            else {
                System.out.println( "showKinaseDomainHierarchy_HTML: start " );
                outputKinaseDomainHierarchy_html( toClient, servletContext, domainName, hierarchyRoot );
                System.out.println( "showKinaseDomainHierarchy_HTML: stop " );
            }

            toClient.close();
        }
        catch( Exception e ) {
            e.printStackTrace( System.err );
            utilityFormatter.outputError_html( toClient, servletContext, "ProKinO Internal Error.  Our apologies." );
            toClient.close();
        }
    }

    // ///////////////////////////////////////////////////////////////////////////////////
    //
    // Output generation methods
    //
    private void outputClassIndividuals_html(PrintWriter writer,
                                             ServletContext servletContext, 
                                             String className,
                                             List<ResourceNode> indivs)
    {
        freemarker.template.Configuration cfg = null;
        Template template = null;
        Map<String, Object> dataModel = null;
        String templateDir = "WEB-INF/templates";
        String templateName = "class_indivs.html";

        try {

            // Initialize the FreeMarker configuration;
            // - Create a configuration instance
            cfg = new freemarker.template.Configuration();

            cfg.setServletContextForTemplateLoading( servletContext,
                    templateDir );

            // Load templates from the WEB-INF/templates directory of the Web
            // app.
            //
            template = cfg.getTemplate( templateName );

            // Build the data-model
            dataModel = createClassIndivsDataModel( className, indivs );

            // Process the template, using the values from the data-model
            // the instance of the template (with substituted values) will be
            // written to the parameter writer (servlet's output)
            template.process( dataModel, writer );

            writer.flush();

        }
        catch( Exception e ) {
            utilityFormatter.outputError_html( writer, servletContext, "ProKinO Internal Error.  Our apologies." );
            e.printStackTrace( System.err );
        }

    }

    private void outputKinaseDomainHierarchy_html( PrintWriter writer,
                                                   ServletContext servletContext, 
                                                   String domainName,
                                                   KinaseDomainNode hierarchyRoot )
    {
        freemarker.template.Configuration cfg = null;
        Template template = null;
        Map<String, Object> dataModel = null;
        String templateDir = "WEB-INF/templates";
        String templateName = "domain_hierarchy.html";

        try {

            // Initialize the FreeMarker configuration;
            // - Create a configuration instance
            //
            cfg = new freemarker.template.Configuration();

            cfg.setServletContextForTemplateLoading( servletContext, templateDir );

            // Load templates from the WEB-INF/templates directory of the Web app.
            //
            template = cfg.getTemplate( templateName );

            // Build the data-model
            //
            dataModel = createDomainHierarchyDataModel( domainName, hierarchyRoot );

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

    private Map<String, Object> createClassIndivsDataModel( String className, List<ResourceNode> indivs )
    {
        Map<String, Object> dataModel = null;
        ResourceNode indiv = null;

        ArrayList<String> variableVal = null;
        String objectValue = null;
        String fullName = null;

        int i;

        dataModel = new HashMap<String, Object>();

        dataModel.put( "title", title + ": " + className );
        dataModel.put( "version", browser.getVersionInfo() );
        dataModel.put( "date", browser.getDate() );
        dataModel.put( "classname", className );

        if( className.equals( "Gene" ) )
            dataModel.put( "classlabel", "Genes" );
        else if( className.equals( "Cancer" ) )
            dataModel.put( "classlabel", "Diseases" );
        else if( className.equals( "FunctionalDomain" ) )
            dataModel.put( "classlabel", "Functional Domains" );
        else
            dataModel.put( "classlabel", className + "s" );

        variableVal = new ArrayList<String>();
        dataModel.put( "individuals", variableVal );

        Collections.sort( indivs, new ResourceNodeComparator() );

        for( i = 0; i < indivs.size(); i++ ) {

            indiv = indivs.get( i );
            objectValue = "";

            // form the HTML href element
            //
            objectValue += "<a href=\"../resource/";
            objectValue += indiv.getResource().getLocalName();
            objectValue += "\" class=\"navlink\">";

            if( className.equals( "Gene" ) ) {
                fullName = indiv.getInfoString();
                if( fullName.length() > geneLabelMaxLength )
                    fullName = fullName.substring( 0, geneLabelMaxLength ) +
                               "...";
                objectValue += 
                    indiv.getDisplayString() +
                    "</a> &nbsp;&nbsp;&nbsp; " +
                    fullName;
            }
            else if( className.equals( "FunctionalDomain" ) ) {
                objectValue += 
                    indiv.getDisplayString() +
                    "</a> &nbsp;&nbsp;&nbsp; ";
                List<String> organisms = browser.getDomainOrganisms( indiv.getResource().getLocalName() );
                if( organisms != null && organisms.size() > 0 ) {
                    objectValue += " -";
                    for( String organism : organisms ) {
                        objectValue += " " + organism;  
                    }
                }
            }
            else
                objectValue += indiv.getDisplayString() + "</a>";

            variableVal.add( objectValue );

        }

        return dataModel;

    }

    private Map<String, Object> createDomainHierarchyDataModel( String domainName, KinaseDomainNode hierarchyRoot )
    {
        Map<String, Object> dataModel = null;
        ArrayList<String> variableVal = null;
        dataModel = new HashMap<String, Object>();

        dataModel.put( "title", title + ": " + domainName );
        dataModel.put( "version", browser.getVersionInfo() );
        dataModel.put( "date", browser.getDate() );
        dataModel.put( "domainname", domainName );

        dataModel.put( "domainlabel", "Human Kinome Tree" );

        variableVal = new ArrayList<String>();
        dataModel.put( "domains", variableVal );

        traverseDomainHierarchyDataModel( hierarchyRoot, variableVal );

        return dataModel;

    }

    private void traverseDomainHierarchyDataModel( KinaseDomainNode node, ArrayList<String> lines )
    {
        List<KinaseDomainNode> children = null;
        String domainName = null;
        String line = null;
        int i;

        if( node.isClass() ) {
            domainName = node.getResourceNode().getDisplayString();
            line = "<li>" + domainName;
            lines.add( line );
            if( domainName.equals( "ProteinKinaseDomain" ) )
                lines.add( "<ul rel=\"open\">" );
            else
                lines.add( "<ul>" );

            children = node.getChildren();
            Collections.sort( children, new KinaseDomainNodeComparator() );
            for( i = 0; i < children.size(); i++ )
                traverseDomainHierarchyDataModel( children.get( i ), lines );
            lines.add( "</ul>" );
            lines.add( "</li>" );
        }
        else {
            line = "<li><a href=\"../resource/" +
                   node.getResourceNode().getResource().getLocalName() +
                   "\" class=\"navlink\">";
            line += node.getResourceNode().getDisplayString() + "</a>";
            String organism = node.getOrganism();
            if( organism != null && organism.length() > 0 )
                line += " - " + organism;
            line += "</li>";
            lines.add( line );
        }
    }

}

