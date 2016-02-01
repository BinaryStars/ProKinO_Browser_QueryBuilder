package edu.uga.prokino.browser;

import java.io.PrintWriter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.ArrayList;

import javax.servlet.ServletContext;

import com.hp.hpl.jena.rdf.model.Resource;

import freemarker.template.Template;



public class GenericFormatter
{

    // ProKinO browser object; maintains point of focus (current resource)
    //
    private Browser          browser            = null;

    // this server's URL
    //
    // private String serverURL = null;

    private final String            title              = "ProKinO: Protein Kinase Ontology Browser";

    private final int               fastaLineLength    = 70;

    private final String            noDataMsg          = "no data";

    // constructor
    //
    public GenericFormatter()
    {
        // create a new browser in verbose mode
        //
        browser = new Browser( true );
    }

    // output data about a given Gene ProKinO resource (HTML)
    //
    public void outputGenericResource_html(PrintWriter writer,
            ServletContext servletContext, ProKinOResource resource)
    {
        freemarker.template.Configuration cfg = null;
        Template template = null;
        Map<String, Object> dataModel = null;
        String templateDir = "WEB-INF/templates";
        String templateName = "generic_resource.html";

        try {

            // Initialize the FreeMarker configuration;
            // - Create a configuration instance
            cfg = new freemarker.template.Configuration();

            cfg.setServletContextForTemplateLoading( servletContext,
                    templateDir );

            // Load templates from the WEB-INF/templates directory of the Web app.
            //
            template = cfg.getTemplate( templateName );

            // Build the data-model
            dataModel = createGenericDataModel( resource );

            // Process the template, using the values from the data-model
            // the instance of the template (with substituted values) will be
            // written to the parameter writer (servlet's output)
            template.process( dataModel, writer );

            writer.flush();

        }
        catch( Exception e ) {
            e.printStackTrace( System.err );
        }

    }

    // !!! SPLIT UP THIS METHOD
    private Map<String, Object> createGenericDataModel(ProKinOResource resource)
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

        // check if it is a resource in the Kinase Domain Hierarchy
        if( ! browser.isKinaseDomain( resource.getLocalName() ) ) {

            displayProperties = BrowserConfig.getDisplayProperties( resourceTypeName );

	    // DEBUG
	    if( displayProperties != null )
		System.out.println( "GenericFormatter.createGenericDataModel: have display properties for: " + resourceTypeName );
	    else
		System.out.println( "GenericFormatter.createGenericDataModel: no display properties for: " + resourceTypeName );

            if( displayProperties == null ) {
                boolean isMutation = false;
                for( i = 0; i < BrowserConfig.mutationTypes.length; i++ ) {
                    if( resourceTypeName.equals( BrowserConfig.mutationTypes[i] ) )
                        isMutation = true;
                }
                if( isMutation ) {
                    displayProperties = BrowserConfig.getDisplayProperties( "Mutation" );
		    System.out.println( "GenericFormatter.createGenericDataModel: display properties set for Mutation" );
		}
            }

            if( displayProperties == null ) {
                if( resourceTypeName.endsWith( "subfamily" ) ) {
                    displayProperties = BrowserConfig.getDisplayProperties( "Subfamily" );
		    System.out.println( "GenericFormatter.createGenericDataModel: display properties set for Subfamily" );
                }
                else if( resourceTypeName.endsWith( "family" ) ) {
                    displayProperties = BrowserConfig.getDisplayProperties( "Family" );
		    System.out.println( "GenericFormatter.createGenericDataModel: display properties set for Family" );
                }
                else if( resourceTypeName.endsWith( "group" ) ) {
                    displayProperties = BrowserConfig.getDisplayProperties( "Group" );
		    System.out.println( "GenericFormatter.createGenericDataModel: display properties set for Group" );
                }
            }

            variableVals = new ArrayList<ArrayList<String>>();

            dataModel.put( "propvalues", variableVals );
            
            if( displayProperties == null ) {

                System.out.println( "GenericFormatter.createGenericDataModel: null displayProperties for: " +
				    resourceTypeName );

                variableVal = new ArrayList<String>();
                variableVal.add( "" );
                variableVal.add( "" );
                variableVals.add( variableVal );

                System.out.println( "GenericFormatter.createGenericDataModel: exiting" );

                return dataModel;
            }

            // prepare the data properties
            //
            displayDataProps = displayProperties.getDataProperties();
            if( displayDataProps == null )
                System.out.println( "createGenericDataModel: null displayDataProps for: " + resourceTypeName );

            for( i = 0; i < displayDataProps.size(); i++ ) {

                propName = displayDataProps.get( i );

                literals = resource.getDataProperty( propName );
                if( literals == null ) {
                    System.out.println( "createGenericDataModel: literals is null" );
                    continue;
                }

                BrowserUtility.cleanLiteralValues( literals );
                Collections.sort( literals );

                if( propName.equals( "hasFASTAFormat" ) ||
                    propName.equals( "hasSubDomainSequence" ) )
                    literalValue = 
                        BrowserUtility.formatFASTASequence( literals.get( 0 ), fastaLineLength );
                else {
                    literalValue = "";
                    for( j = 0; j < literals.size(); j++ ) {
                        literalValue += literals.get( j );
                        if( j < literals.size() - 1 )
                            literalValue += ", ";
                    }
                }

                variableVal = new ArrayList<String>();
                variableVal.add( BrowserConfig.getPropertyDisplayName( propName, Browser.forward ) );
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
                objectValue = getDataModelPropertyValue( propName, resource, Browser.forward );

                if( objectValue.length() > 0 ) {
                    variableVal = new ArrayList<String>();
                    variableVal.add( BrowserConfig.getPropertyDisplayName( propName, Browser.forward ) );
                    variableVal.add( objectValue );
                    variableVals.add( variableVal );
                }

                // get backward property values (incoming)
                //
                objectValue = getDataModelPropertyValue( propName, resource, Browser.backward );

                if( objectValue.length() > 0 ) {
                    variableVal = new ArrayList<String>();
                    variableVal.add( BrowserConfig.getPropertyDisplayName( propName, Browser.backward ) );
                    variableVal.add( objectValue );
                    variableVals.add( variableVal );
                }

            }
        }
        else {

            System.out.println( "ProKinOBrowserService.createGenericDataModel: a kinase domain" );

            // it is a domain hierarchy element

            KinaseDomainNode         domainNode = null;
            KinaseDomainNode         parentNode = null;
            List<KinaseDomainNode>   childrenNodes = null;

            String resourceName = resource.getLocalName();

            System.out.println( "ProKinOBrowserService.createGenericDataModel: resourceName: " + resourceName );

            if( displayProperties == null ) {
                if( resourceName.endsWith( "subfamily" ) ) {
                    displayProperties = BrowserConfig.getDisplayProperties( "Subfamily" );
                    System.out.println( "displayProperties for subfamily" );
                }
                else if( resourceName.endsWith( "family" ) ||
                         resourceName.endsWith( "Domain" ) ) {
                    displayProperties = BrowserConfig.getDisplayProperties( "Family" );
                    System.out.println( "displayProperties for Family" );
                }
                else if( resourceName.endsWith( "group" ) ) {
                    displayProperties = BrowserConfig.getDisplayProperties( "Group" );
                    System.out.println( "displayProperties for Group" );
                }
            }

            variableVals = new ArrayList<ArrayList<String>>();

            dataModel.put( "propvalues", variableVals );

            // prepare the data properties
            //
            displayDataProps = displayProperties.getDataProperties();
            if( displayDataProps == null )
                System.out.println( "createGenericDataModel: null displayDataProps for: " +
                                    resourceTypeName );

            System.out.println( "displayDataProps.size() = " + displayDataProps.size() );

            for( i = 0; i < displayDataProps.size(); i++ ) {

                propName = displayDataProps.get( i );

                System.out.println( "displayDataProp: " + propName );

                literals = resource.getDataProperty( propName );
                if( literals == null ) {
                    System.out.println( "createGenericDataModel: literals is null" );
                    continue;
                }

                BrowserUtility.cleanLiteralValues( literals );
                Collections.sort( literals );

                if( propName.equals( "hasFASTAFormat" ) )
                    literalValue = BrowserUtility.formatFASTASequence( literals.get( 0 ), fastaLineLength );
                else {
                    literalValue = "";
                    for( j = 0; j < literals.size(); j++ ) {
                        literalValue += literals.get( j );
                        if( j < literals.size() - 1 )
                            literalValue += ", ";
                    }
                }

                variableVal = new ArrayList<String>();
                variableVal.add( BrowserConfig.getPropertyDisplayName( propName, Browser.forward ) );
                variableVal.add( literalValue );
                variableVals.add( variableVal );

            }

            // prepare the object properties
            //
            displayObjectProps = displayProperties.getObjectProperties();

            System.out.println( "displayObjectProps.size() = " + displayObjectProps.size() );

            for( i = 0; i < displayObjectProps.size(); i++ ) {

                propName = displayObjectProps.get( i );

                System.out.println( "displayObjectProp: " + propName );

                if( propName.equals( "hasDbXref" ) ) {

                    processDbXrefs( variableVals, resource );
                    continue;

                }

                // get forward property values (outgoing)
                //
                objectValue = getDataModelPropertyValue( propName, resource, Browser.forward );

                if( objectValue != null && objectValue.length() > 0 ) {
                    variableVal = new ArrayList<String>();
                    variableVal.add( BrowserConfig.getPropertyDisplayName( propName, Browser.forward ) );
                    variableVal.add( objectValue );
                    variableVals.add( variableVal );
                }

                // get backward property values (incoming)
                //
                objectValue = getDataModelPropertyValue( propName, resource, Browser.backward );

                if( objectValue != null && objectValue.length() > 0 ) {
                    variableVal = new ArrayList<String>();
                    variableVal.add( BrowserConfig.getPropertyDisplayName( propName, Browser.backward ) );
                    variableVal.add( objectValue );
                    variableVals.add( variableVal );
                }

            }

            domainNode = browser.getKinaseDomainNode( resource.getLocalName() );
            if( domainNode == null )
                System.out.println( "ProKinOBrowserService.createGenericDataModel: domainNode is null" );

            if( domainNode != null ) {

                StringBuffer objectString;

                System.out.println( "ProKinOBrowserService.createGenericDataModel: have a KinaseDomainNode!" );

                objectString = new StringBuffer( 10000 );
                objectString.setLength( 0 );

                parentNode = domainNode.getParent();
                childrenNodes = domainNode.getChildren();

                if( parentNode == null )
                    System.out.println( "ProKinOBrowserService.createGenericDataModel: parent is null" );
                else
                    System.out.println( "ProKinOBrowserService.createGenericDataModel: have parent" );

                if( childrenNodes == null )
                    System.out.println( "ProKinOBrowserService.createGenericDataModel: childrenNodes is null" );
                else
                    System.out.println( "ProKinOBrowserService.createGenericDataModel: have children: " + 
                                        childrenNodes.size() );

                // first, get the parent
                if( parentNode != null ) {

                    System.out.println( "ProKinOBrowserService.createGenericDataModel: starting parent" );

                    variableVal = new ArrayList<String>();
                    variableVal.add( BrowserConfig.getPropertyDisplayName( "parent", Browser.forward ) );

                    objectString.append( "<a href=\"" );
                    objectString.append( Ontology.getResourceNameFragment( parentNode.getResourceNode().getResource() ) +
                                        "\" class=\"navlink\"> " );
                    objectString.append( parentNode.getResourceNode().getDisplayString() + "</a>" );
                    variableVal.add( objectString.toString() );

                    variableVals.add( variableVal );

                    System.out.println( "ProKinOBrowserService.createGenericDataModel: finished parent" );

                }

                if( childrenNodes != null ) {

                    System.out.println( "ProKinOBrowserService.createGenericDataModel: starting children" );

                    variableVal = new ArrayList<String>();
                    variableVal.add( BrowserConfig.getPropertyDisplayName( "parent", Browser.backward ) );
                    objectString.setLength( 0 );

                    Collections.sort( childrenNodes, new KinaseDomainNodeComparator() );

                    for( i = 0; i < childrenNodes.size(); i++ ) {
                        
                        System.out.println( "ProKinOBrowserService.createGenericDataModel: start child: " + i );

                        objectString.append( "<a href=\"" );
                        objectString.append( Ontology.getResourceNameFragment( childrenNodes.get( i ).getResourceNode().getResource() ) +
                                            "\" class=\"navlink\"> " );
                        objectString.append( childrenNodes.get( i ).getResourceNode().getDisplayString() + "</a>" );
                        if( i < childrenNodes.size() - 1 ) 
                            objectString.append( ",<br /> " );

                        System.out.println( "ProKinOBrowserService.createGenericDataModel: end child: " + i );

                    }

                    variableVal.add( objectString.toString() );
                    variableVals.add( variableVal );

                    System.out.println( "ProKinOBrowserService.createGenericDataModel: finished children" );

                }
            }

        }

        return dataModel;

    }

    // //////////////////////////////////////////////////////////
    //
    // Other auxiliary methods
    //

    // return a value formatted for HTML display
    private String getDataModelPropertyValue(String property, ProKinOResource resource, boolean forward)
    {
        List<ResourceNode>        objects = null;
        StringBuffer              objectValue = null;
        ArrayList<Motif>          subdomains = null;
        ArrayList<Motif>          seqMotifs = null;
        ArrayList<Motif>          strMotifs = null;
        ArrayList<ResourceNode>   structuresWithPKDomain = null;
        ArrayList<ResourceNode>   structuresWithNoPKDomain = null;
        int                       i;

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

        // Remove any Reactions from objects -- they would be listed as
        // Sub-Pathways
        //
        // This is a fix for dual relationships:
        // Pathway -- hasReaction --> Reaction
        // Reaction -- hasParentPathway --> Pathway
        // which would make the Reactions to be listed aas sub-pathways
        //
        if( !forward && property.equals( "hasParentPathway" ) ) {

            ResourceNode rNode = null;

            for( ListIterator<ResourceNode> rnodeIter = objects.listIterator(); rnodeIter
                    .hasNext(); ) {

                rNode = rnodeIter.next();
                if( browser.getResourceTypeName( rNode.getResource() ).equals(
                        "Reaction" ) )
                    rnodeIter.remove();

            }

        }

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
        //
        if( forward && property.equals( "hasMotif" ) ||
	    forward && resource.getType().getLocalName().equals( "StructuralMotif" ) && property.equals( "contains" ) ) {

            System.out.println( "GenericFormatter.getDataModelPropertyValue: Processing motifs" );
	    if( forward && resource.getType().getLocalName().equals( "StructuralMotif" ) && property.equals( "contains" ) )
		System.out.println( "GenericFormatter.getDataModelPropertyValue: Processing structural motifs & contains" );
	    else
		System.out.println( "GenericFormatter.getDataModelPropertyValue: Processing other motifs" );

            subdomains = new ArrayList<Motif>();
            seqMotifs  = new ArrayList<Motif>();
            strMotifs  = new ArrayList<Motif>();

            for( ResourceNode motif : objects ) {

		int startPos = -1; // default value for StructuralMotif
                String name = null;

		if( motif.getResourceType().getLocalName().equals( "SequenceMotif" ) ) {
		    name = browser.getPropertyValue( motif.getResource(), "hasPrimaryName" );
		    startPos = Integer.parseInt( browser.getPropertyValue( motif.getResource(), "hasStartLocation" ) );
		    if( name.startsWith( "subdomain" ) ) {
			subdomains.add( new Motif( motif, name, startPos ) );
			System.out.println( "Subdomain: " + name + "   pos: " + startPos );
		    }
		    else {
			seqMotifs.add( new Motif( motif, name, startPos ) );
			System.out.println( "SequenceMotif: " + name + "   pos: " + startPos );
		    }
		}
		else {
		    //name = motif.getDisplayString();
		    name = browser.getPropertyValue( motif.getResource(), "hasPrimaryName" );
		    strMotifs.add( new Motif( motif, name, startPos ) );
                    System.out.println( "StructuralMotif: " + name );
		}
            }

            Collections.sort( subdomains, new MotifComparator() );
            Collections.sort( strMotifs, new MotifComparator() );
            Collections.sort( seqMotifs, new MotifComparator() );

        }
        else if( forward && property.equals( "hasProteinStructure" ) ) {
	    structuresWithPKDomain = new ArrayList<ResourceNode>();
	    structuresWithNoPKDomain = new ArrayList<ResourceNode>();
            for( ResourceNode structure : objects ) {

                String name = null;

		String hasPKDomain = null;

		name = browser.getPropertyValue( structure.getResource(), "hasPrimaryName" );
		hasPKDomain = browser.getPropertyValue( structure.getResource(), "hasPKDomain" );

		if( hasPKDomain != null && hasPKDomain.equals( "true" ) ) {
		    structuresWithPKDomain.add( structure );
		    System.out.println( "Structure with PK Domain: " + name );
		}
		else {
                    structuresWithNoPKDomain.add( structure );
                    System.out.println( "Structure with no PK Domain: " + name );
		}
            }
	    Collections.sort( structuresWithPKDomain, new ResourceNodeComparator() );
	    Collections.sort( structuresWithNoPKDomain, new ResourceNodeComparator() );
	}
        else
            Collections.sort( objects, new ResourceNodeComparator() );

        // now, form the property value string
        //
        objectValue.setLength( 0 );

        // combine the values into a String

        if( property.equals( "hasDbXref" ) ) { // process external refernces

            System.out.println( "!!!!!! SHOULDN'T HAPPEN !!!!!!!!" );
            i = 0;
            for( ResourceNode rNode : objects ) {

                // clear the StringBbuffer
                //
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
        else if( forward && property.equals( "hasMotif" ) ||
		 forward && resource.getType().getLocalName().equals( "StructuralMotif" ) && property.equals( "contains" ) ) {

            // format subdomains
            i = 0;
	    if( subdomains.size() > 0 )
		objectValue.append( "Subdomains: " );
            for( Motif motif : subdomains ) {

                // form the HTML href element
                //
                objectValue.append( "<a href=\"" );
                objectValue.append( Ontology.getResourceNameFragment( motif.getMotif().getResource() ) +
                                    "\" class=\"navlink\"> " );
		// remove the leading "subdomain " substring to get just the subdomain number
                objectValue.append( motif.getName().substring( 10 ) + "</a>" );
                //objectValue.append( motif.getName() + "</a>" );

                if( i < subdomains.size() - 1 )
                    objectValue.append( ", " );

                i++;
            }

            // format structural motifs
            if( subdomains.size() > 0 && strMotifs.size() > 0 ) {
                objectValue.append( "<br><br>" );
		objectValue.append( "Structural motifs: " );
            }
            i = 0;
            for( Motif motif : strMotifs ) {

                // form the HTML href element
                //
                objectValue.append( "<a href=\"" );
                objectValue.append( Ontology.getResourceNameFragment( motif.getMotif().getResource() ) +
                                    "\" class=\"navlink\"> " );
                objectValue.append( motif.getName() + "</a>" );

                if( i < strMotifs.size() - 1 )
                    objectValue.append( ", " );

                i++;
            }

            // format sequence motifs
            if( (subdomains.size() > 0 || strMotifs.size() > 0 ) && seqMotifs.size() > 0 ) {
                objectValue.append( "<br><br>" );
		objectValue.append( "Sequence motifs: " );
            }

            i = 0;
            for( Motif motif : seqMotifs ) {

                // form the HTML href element
                //
                objectValue.append( "<a href=\"" );
                objectValue.append( Ontology.getResourceNameFragment( motif.getMotif().getResource() ) +
                                    "\" class=\"navlink\"> " );
                objectValue.append( motif.getName() + "</a>" );

                if( i < seqMotifs.size() - 1 )
                    objectValue.append( ", " );

                i++;
            }

        }
        else if( forward && property.equals( "hasProteinStructure" ) ) {

            // format structures
            i = 0;
	    if( structuresWithPKDomain.size() > 0 )
		objectValue.append( "With PK Domain: " );
            for( ResourceNode structure : structuresWithPKDomain ) {

                // form the HTML href element
                //
                objectValue.append( "<a href=\"" );
                objectValue.append( Ontology.getResourceNameFragment( structure.getResource() ) +
                                    "\" class=\"navlink\"> " );
                objectValue.append( structure.getDisplayString() + "</a>" );

                if( i < structuresWithNoPKDomain.size() - 1 )
                    objectValue.append( ", " );

                i++;
            }

            // format structural motifs
            if( structuresWithNoPKDomain.size() > 0 && structuresWithNoPKDomain.size() > 0 ) {
                objectValue.append( "<br><br>" );
		objectValue.append( "With no PK Domain: " );
            }
            i = 0;
            for( ResourceNode structure : structuresWithNoPKDomain ) {

                // form the HTML href element
                //
                objectValue.append( "<a href=\"" );
                objectValue.append( Ontology.getResourceNameFragment( structure.getResource() ) +
                                    "\" class=\"navlink\"> " );
                objectValue.append( structure.getDisplayString() + "</a>" );

                if( i < structuresWithNoPKDomain.size() - 1 )
                    objectValue.append( ", " );

                i++;
            }

	}
        else { // other properties

            i = 0;
            for( ResourceNode rNode : objects ) {

                // clear the StringBbuffer
                //
                // form the HTML href element
                //

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
            System.out
                    .println( "ProKinOBrowserService.processDbXrefs: null variableVals" );
        if( resource == null )
            System.out
                    .println( "ProKinOBrowserService.processDbXrefs: null resource" );

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
                objectValue
                        .append( "<img src=\"../ui/preview.gif\" border=\"0\" /></a>," );

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
            variableVal
                    .add( "<div id=\"reference\" style=\"width:620px; height:150px; border:1px solid black;overflow-y:auto;\"><p>Click on one of the <img src=\"../ui/preview.gif\" border=\"0\" /> icons within the <strong>PubMed Refs</strong> above to see the preview.</div>" );
            variableVals.add( variableVal );

        }

    }

}

