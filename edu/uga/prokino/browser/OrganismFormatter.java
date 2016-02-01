package edu.uga.prokino.browser;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.ArrayList;

import javax.servlet.ServletContext;

import com.hp.hpl.jena.rdf.model.Resource;

import freemarker.template.Template;


public class OrganismFormatter
{

    // ProKinO browser object; maintains point of focus (current resource)
    //
    private Browser          browser            = null;

    // this server's URL
    //
    // private String serverURL = null;

    private final String            title              = "ProKinO: Protein Kinase Ontology Browser";

    private final int               fastaLineLength    = 70;

    private HashMap<String, String> resourceCache      = null;

    private StringWriter            stringWriter       = null;

    private final String            noDataMsg          = "no data";

    // constructor
    //
    public OrganismFormatter()
    {
        // create a new browser in verbose mode
        //
        browser = new Browser( true );

        resourceCache = new HashMap<String, String>();
        stringWriter = new StringWriter( 50000 );
    }

    // /////////////////////////////////////////////////////////////////////////////////////////
    //
    // Service methods
    //

    // output data about a given Organism ProKinO resource (HTML)
    //
    public void outputOrganismResource_html(PrintWriter writer,
            ServletContext servletContext, ProKinOResource resource)
    {

        freemarker.template.Configuration cfg = null;
        Template template = null;
        Map<String, Object> dataModel = null;
        String templateDir = "WEB-INF/templates";
        String templateName = "organism_resource.html";
        String resourcePage = null;

        try {

            resourcePage = resourceCache.get( resource.getLocalName() );
            if( resourcePage == null ) {

                // Initialize the FreeMarker configuration;
                // - Create a configuration instance
                cfg = new freemarker.template.Configuration();

                cfg.setServletContextForTemplateLoading( servletContext,
                        templateDir );

                // Load templates from the WEB-INF/templates directory of the Web app.
                //
                template = cfg.getTemplate( templateName );

                // Build the data-model
                //
                dataModel = createOrganismDataModel_html( resource );

                // Process the template, using the values from the data-model
                // the instance of the template (with substituted values) will
                // be
                // written to the parameter writer (servlet's output)
                //
                // template.process( dataModel, writer );

                // empty the stringWriter's buffer
                //
                stringWriter.getBuffer().setLength( 0 );

                template.process( dataModel, stringWriter );

                resourcePage = stringWriter.toString();

                System.out.println( "ProKinOBrowserService.outputOrganismResource_html: caching: " +
                                    resource.getLocalName() );
                resourceCache.put( resource.getLocalName(), resourcePage );

            }
            else
                System.out.println( "ProKinOBrowserService.outputOrganismResource_html: from cache: " +
                                    resource.getLocalName() );

            writer.write( resourcePage );

            writer.flush();

        }
        catch( Exception e ) {
            e.printStackTrace( System.err );
        }

    }

    private Map<String, Object> createOrganismDataModel_html(ProKinOResource resource)
    {
        Map<String, Object> dataModel = null;
        ClassDisplayProperties displayProperties = null;

        List<String> literals = null;
        ArrayList<ArrayList<String>> variableVals = null;
        ArrayList<String> variableVal = null;
        String literalValue = null;
        String objectValue = null;

        List<String> displayDataProps = null;
        List<String> displayObjectProps = null;

        String resourceTypeName = null;
        Resource resourceType = null;
        String propName = null;
        int i;
        int j;

        resourceType = resource.getType();
        if( resourceType != null )
            resourceTypeName = resourceType.getLocalName();

        dataModel = new HashMap<String, Object>();

        dataModel.put( "title", title + ": " + resource.getLocalName() );
        dataModel.put( "version", browser.getVersionInfo() );
        dataModel.put( "date", browser.getDate() );
        dataModel.put( "typename", resourceTypeName );
        dataModel.put( "resname", resource.getLocalName() );

        displayProperties = BrowserConfig.getDisplayProperties( resourceTypeName );

        variableVals = new ArrayList<ArrayList<String>>();

        dataModel.put( "propvalues", variableVals );

        if( displayProperties == null ) {

            System.out.println( "createOrganismDataModel: displayProperties is null for: " +
                                resourceTypeName );

            variableVal = new ArrayList<String>();
            variableVal.add( "" );
            variableVal.add( "" );
            variableVals.add( variableVal );

            return dataModel;
        }

        // prepare the data properties
        //
        displayDataProps = displayProperties.getDataProperties();
        if( displayDataProps == null )
            System.out.println( "createOrganismDataModel: null displayDataProps for: " +
                                  resourceTypeName );

        for( i = 0; i < displayDataProps.size(); i++ ) {

            propName = displayDataProps.get( i );

            literals = resource.getDataProperty( propName );
            if( literals == null ) {
                System.out.println( "createOrganismDataModel: literals is null" );
                continue;
            }

            BrowserUtility.cleanLiteralValues( literals );
            Collections.sort( literals );

            if( propName.equals( "hasFASTAFormat" ) )
                literalValue = BrowserUtility.formatFASTASequence(
                        literals.get( 0 ), fastaLineLength );
            else {
                literalValue = "";
                for( j = 0; j < literals.size(); j++ ) {
                    literalValue += literals.get( j );
                    if( j < literals.size() - 1 )
                        literalValue += ", ";
                }
            }

            variableVal = new ArrayList<String>();
            variableVal.add( BrowserConfig.getPropertyDisplayName( propName,
                    Browser.forward ) );
            variableVal.add( literalValue );
            variableVals.add( variableVal );

        }

        // prepare the object properties
        //
        displayObjectProps = displayProperties.getObjectProperties();

        for( i = 0; i < displayObjectProps.size(); i++ ) {

            propName = displayObjectProps.get( i );

            if( propName.equals( "hasDbXref" ) ) {

                processDbXrefs( variableVals, resource );
                continue;

            }

            // get forward property values (outgoing)
            //
            objectValue = getDataModelPropertyValue_html( propName, resource, Browser.forward );

            if( objectValue.length() > 0 ) {
                variableVal = new ArrayList<String>();
                variableVal.add( BrowserConfig.getPropertyDisplayName( propName,
                        Browser.forward ) );
                variableVal.add( objectValue );
                variableVals.add( variableVal );
            }

            // get backward property values (incoming)
            //
            objectValue = getDataModelPropertyValue_html( propName, resource,
                    Browser.backward );

            if( objectValue.length() > 0 ) {
                if( propName.equals( "presentIn" ) ) {
                    dataModel.put( "genes", objectValue );
                }
                else {
                    variableVal = new ArrayList<String>();
                    variableVal.add( BrowserConfig.getPropertyDisplayName( propName, Browser.backward ) );
                    variableVal.add( objectValue );
                    variableVals.add( variableVal );
                }
            }

        }

        return dataModel;

    }

    // //////////////////////////////////////////////////////////
    //
    // Other auxiliary methods
    //

    // return a property value formatted for HTML display
    private String getDataModelPropertyValue_html(
                                                  String property,
                                                  ProKinOResource resource, 
                                                  boolean forward
                                                 )
    {
        List<ResourceNode> objects = null;
        StringBuffer         objectValue = null;
        int                  i;

        objectValue = new StringBuffer( 10000 );

        // get the objects (values) for the property
        //
        if( forward )
            objects = resource.getFwdProperty( property );
        else
            objects = resource.getBckProperty( property );

        if( objects == null ) {
            System.out.println( "getDataModelPropertyValue: objects is null; property: " + property );
            return objectValue.toString();
        }

        // process the objects which are the value of the property
        //

        // Remove any KinaseDomains from objects -- they would be listed as
        // Genes
        //
        // This is a fix for dual relationships:
        // Gene -- presentIn --> Organism
        // KinaseDomain -- presentIn --> Organism
        // which would make the Reactions to be listed aas sub-pathways
        //
        if( !forward && property.equals( "presentIn" ) && resource.getType().getLocalName().equals( "Organism" ) ) {

            ResourceNode rNode = null;

            for( ListIterator<ResourceNode> rnodeIter = objects.listIterator(); rnodeIter.hasNext(); ) {

                rNode = rnodeIter.next();

                // should be:                if( rNode.getResource().getLocalName().indexOf( "-Domain" ) != -1 )
                if( rNode.getResource().getLocalName().endsWith( "-Domain" ) ||
                    rNode.getResource().getLocalName().endsWith( "-Domain1" ) ||
                    rNode.getResource().getLocalName().endsWith( "-Domain2" ) )
                    rnodeIter.remove();

            }

        }

        // sort and organize the objects
        
        Collections.sort( objects, new ResourceNodeComparator() );

        // now, form the property value string
        //
        objectValue.setLength( 0 );

        // combine the values into a String

        if( property.equals( "hasDbXref" ) ) { // process external refernces

            System.out.println( "!!!!!! SHOULDN'T HAPPEN !!!!!!!!" );
            i = 0;
            for( ResourceNode rNode : objects ) {

                // form the HTML href element
                //
                objectValue.append( "<a href=\"" );
                objectValue.append( browser.getPropertyValue(
                        rNode.getResource(), "hasURI" ) +
                                    "\" target=\"_blank\" class=\"navlink\"> " );
                objectValue.append( rNode.getDisplayString() + "</a>" );

                if( i < objects.size() - 1 )
                    objectValue.append( ", " );

                i++;
            }
        }
        else { // other properties

            i = 0;
            for( ResourceNode rNode : objects ) {

                // add space, but only if the Genes are shown for the Organism
                if( rNode.getResourceType().getLocalName().equals( "Gene" ) &&
                    resource.getType().getLocalName().equals( "Organism" ) ) {
                    objectValue.append( "&nbsp;" );
                }
                objectValue.append( "<a href=\"" );
                objectValue.append( Ontology.getResourceNameFragment( rNode.getResource() ) + 
                                    "\" class=\"navlink\"> " );
                if( rNode.getResourceType().getLocalName().equals( "Gene" ) ||
                    rNode.getResourceType().getLocalName().equals( "SubDomain" ) )
                    objectValue.append( browser.getPropertyValue( rNode.getResource(), "hasPrimaryName" ) + "</a>" );
                else
                    objectValue.append( rNode.getDisplayString() + "</a>" );

                if( i < objects.size() - 1 ) {
                    if( property.equals( "hasParentPathway" ) ||
                        property.equals( "includes" ) ||
                        forward && property.equals( "hasReaction" ) ||
                        forward && property.equals( "participatesIn" ) ||
                        !forward && property.equals( "consumes" ) ||
                        !forward && property.equals( "produces" ) )
                        objectValue.append( ",<br /> " );
                    else if( !forward &&
                             property.equals( "presentIn" ) )
                        objectValue.append( "<br /> " );
                    else
                        objectValue.append( ", " );
                    i++;
                }
            }
        }

        return objectValue.toString();
    }

    private void processDbXrefs(ArrayList<ArrayList<String>> variableVals,
            ProKinOResource resource)
    {
        ArrayList<String> variableVal = null;
        List<ResourceNode> objects = null;
        StringBuffer objectValue = null;
        boolean pubmed = false;
        int pubmedNo = 0;
        boolean uniprot = false;
        int i;

        if( variableVals == null )
            System.out.println( "ProKinOBrowserService.processDbXrefs: null variableVals" );
        if( resource == null )
            System.out.println( "ProKinOBrowserService.processDbXrefs: null resource" );

        objectValue = new StringBuffer( 10000 );
        objectValue.setLength( 0 );

        // get "hasDbXref" property values
        //
        objects = resource.getFwdProperty( "hasDbXref" );

        if( objects == null ) {

            System.out.println( "getDataModelPropertyValue: objects is null" );

            variableVal = new ArrayList<String>();
            variableVal.add( BrowserConfig.getPropertyDisplayName( "hasDbXref",
                    Browser.forward ) );
            variableVal.add( noDataMsg );
            variableVals.add( variableVal );

            return;
        }

        // sort the values
        //
        Collections.sort( objects, new ResourceNodeComparator() );

        String lastIdPrefix = "";
        String currIdPrefix = "";
        String currId = "";
        String displayString = null;
        int dashPos;
        int commaPos;

        // collect all the references
        //
        i = 0;
        for( ResourceNode rNode : objects ) {

            displayString = rNode.getDisplayString();
            dashPos = displayString.indexOf( "-" );
            currIdPrefix = displayString.substring( 0, dashPos );
            currId = displayString.substring( dashPos + 1 );

            if( !currIdPrefix.equals( lastIdPrefix ) ) {

                if( variableVal != null ) {
                    commaPos = objectValue.lastIndexOf( "," );
                    objectValue.deleteCharAt( commaPos );
                    variableVal.add( objectValue.toString() );
                    variableVals.add( variableVal );
                }

                pubmed = false;
                uniprot = false;

                lastIdPrefix = currIdPrefix;
                if( lastIdPrefix.equals( "PubMed" ) ||
                    lastIdPrefix.equals( "PubMedPMID" ) ) {
                    pubmed = true;
                }
                else if( lastIdPrefix.equals( "UniProt" ) ) {
                    uniprot = true;
                }

                variableVal = new ArrayList<String>();
                if( lastIdPrefix.equals( "Wiki" ) )
                    variableVal.add( "Wikipedia Refs:" );
                else if( lastIdPrefix.equals( "Isoform" ) ||
                         lastIdPrefix.equals( "UniProtTaxonomy" ) )
                    variableVal.add( "UniProt Refs:" );
                else if( lastIdPrefix.equals( "PubMedPMID" ) )
                    variableVal.add( "PubMed Refs:" );
                else if( lastIdPrefix.equals( "COSMIC_MUTATION" ) )
                    variableVal.add( "COSMIC Refs:" );
                else if( lastIdPrefix.equals( "NCBITaxonomy" ) )
                    variableVal.add( "NCBI Refs:" );
                else
                    variableVal.add( lastIdPrefix + " Refs:" );

                objectValue.setLength( 0 );

            }

            objectValue.append( "<nobr><a href=\"" );
            objectValue.append( browser.getPropertyValue( rNode.getResource(),
                    "hasURI" ) + "\" target=\"_blank\" class=\"navlink\"> " );
            if( uniprot ) {
                String isPrimary = browser.getPropertyValue( rNode.getResource(), "isPrimaryUniprotId" );
                if( isPrimary != null && isPrimary.equals( "true" ) ) {
                    objectValue.append( "<b>" + currId + "</b></a>" );
                }
                else {
                    objectValue.append( currId + "</a>" );
                }
            }
            else 
                objectValue.append( currId + "</a>" );
            
            // special handling of PubMed references
            if( pubmed ) {

                pubmedNo++;

                objectValue.append( " <a href=\"javascript:ajaxpage('" );
                objectValue.append( "../pubmed/" + currId + "'" );
                objectValue.append( ",'reference');\">" );
                objectValue.append( "<img src=\"../ui/preview.gif\" border=\"0\" /></a>," );

            }

            if( !pubmed && ( i < objects.size() - 1 ) )
                objectValue.append( "," );

            i++;
            objectValue.append( "</nobr> " );
        }

        // finish the last reference
        //
        if( pubmed ) { // if the last group was pubmed references, remove the
                       // last comma
            commaPos = objectValue.lastIndexOf( "," );
            objectValue.deleteCharAt( commaPos );
        }

        variableVal.add( objectValue.toString() );
        variableVals.add( variableVal );

        if( pubmedNo > 0 ) {

            // output the PubMed preview label and div
            //
            // empty row first...
            //
            variableVal = new ArrayList<String>();
            variableVal.add( "&nbsp;" );
            variableVal.add( "&nbsp;" );
            variableVals.add( variableVal );

            variableVal = new ArrayList<String>();
            variableVal.add( "PubMed preview:" );
            variableVal.add( "<div id=\"reference\" style=\"width:620px; height:150px; border:1px solid black;overflow-y:auto;\"><p>Click on one of the <img src=\"../ui/preview.gif\" border=\"0\" /> icons within the <strong>PubMed Refs</strong> above to see the preview.</div>" );
            variableVals.add( variableVal );

        }

    }

}