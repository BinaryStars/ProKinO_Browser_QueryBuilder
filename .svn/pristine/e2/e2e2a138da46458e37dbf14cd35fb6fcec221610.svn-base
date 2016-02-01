package edu.uga.prokino.browser;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import freemarker.template.Template;


public class GeneFormatter
{

    // ProKinO browser object; maintains point of focus (current resource)
    //
    private Browser          browser                   = null;

    // this server's URL
    //
    // private String serverURL = null;

    private final String            title              = "ProKinO: Protein Kinase Ontology Browser";

    private PageCache               resourceCache      = null;

    private StringWriter            stringWriter       = null;

    private final String            noDataMsg          = "no data";

    // constructor
    //
    public GeneFormatter()
    {
        // create a new browser in verbose mode
        //
        browser = new Browser( true );

        this.resourceCache = PageCache.getPageCache();
        stringWriter = new StringWriter( 50000 );
    }

    // ///////////////////////////////////////////////////////////////////////////////////
    //
    // Output generation methods
    //

    // output data about a given Gene ProKinO resource (HTML)
    //
    public void outputGeneResource_html(PrintWriter writer,
            ServletContext servletContext, ProKinOResource resource)
    {

        freemarker.template.Configuration cfg = null;
        Template template = null;
        Map<String, Object> dataModel = null;
        String templateDir = "WEB-INF/templates";
        String templateName = "gene_resource.html";
        String resourcePage = null;

        try {

            resourcePage = resourceCache.get( resource.getLocalName() );
            
            if( resourcePage == null ) {

                System.out.println( "ProKinOBrowserService.outputGeneResource_html: not in cache: " +
                                    resource.getLocalName() );
                
                // Initialize the FreeMarker configuration;
                // - Create a configuration instance
                cfg = new freemarker.template.Configuration();

                cfg.setServletContextForTemplateLoading( servletContext,
                        templateDir );

                // Load templates from the WEB-INF/templates directory of the
                // Web app.
                //
                template = cfg.getTemplate( templateName );

                // Build the data-model
                //
                dataModel = createGeneDataModel( resource );

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

                System.out.println( "ProKinOBrowserService.outputGeneResource_html: caching: " +
                                    resource.getLocalName() );
                resourceCache.put( resource.getLocalName(), resourcePage );

            }
            else
                System.out.println( "ProKinOBrowserService.outputGeneResource_html: from cache: " +
                                    resource.getLocalName() );

            writer.write( resourcePage );

            writer.flush();

        }
        catch( Exception e ) {
            e.printStackTrace( System.err );
        }

    }

    // SPLIT UP THIS METHOD INTO SMALLER ONES
    //
    // Create the representation of the properties and values for this Gene
    // The properties and their values as presented as key-value pairs.
    //
    // This is a long and complex method which prepares a wealth of information
    // about a kinase gene.  Data is assembled in phases.
    //
    private Map<String, Object> createGeneDataModel(ProKinOResource resource)
    {
        Map<String, Object> dataModel = null;
        List<String> literals = null;
        ClassDisplayProperties displayProperties = null;

        List<String> displayDataProps = null;

        ArrayList<ArrayList<String>> variableVals = null;
        ArrayList<String> variableVal = null;
        String literalValue = null;
        String objectValue = null;
        List<String> objectValues = null;

        String propName = null;

        int i;
        int j;

        displayProperties = BrowserConfig.getDisplayProperties( "Gene" );

        dataModel = new HashMap<String, Object>();

        dataModel.put( "title", title + ": " + resource.getLocalName() );
        dataModel.put( "version", browser.getVersionInfo() );
        dataModel.put( "date", browser.getDate() );

        dataModel.put( "typename", "Gene" );
        dataModel.put( "resname", resource.getLocalName() );

        // /////////////////////////////////////////////////////////////////////////
        //
        // Prepare the data model with the values for all tabs in the HTML Gene
        // display
        //

        // /////////////////////////////////////////////////////////////////////////
        //
        // General information tab
        //
        variableVals = new ArrayList<ArrayList<String>>();

        dataModel.put( "generalvals", variableVals );

        displayDataProps = displayProperties.getDataProperties();

        for( i = 0; i < displayDataProps.size(); i++ ) {

            propName = displayDataProps.get( i );

            literals = resource.getDataProperty( propName );
            if( literals == null ) {
                System.out.println( "createGeneDataModel: literals is null" );
                // continue;
            }

            if( literals != null ) {

                BrowserUtility.cleanLiteralValues( literals );
                Collections.sort( literals );

                literalValue = "";
                for( j = 0; j < literals.size(); j++ ) {
                    literalValue += literals.get( j );
                    if( j < literals.size() - 1 )
                        literalValue += ", ";
                }
            }
            else
                literalValue = "no data";

            variableVal = new ArrayList<String>();
            variableVal.add( BrowserConfig.getPropertyDisplayName( propName,
                    Browser.forward ) );
            variableVal.add( literalValue );
            variableVals.add( variableVal );

        }

        // get Organisms
        //
        objectValue = getDataModelPropertyValue( "presentIn", resource, 
                                                 Browser.forward );

        variableVal = new ArrayList<String>();
        variableVal.add( BrowserConfig.getPropertyDisplayName( "presentIn",
                Browser.forward ) );
        if( objectValue.length() > 0 )
            variableVal.add( objectValue );
        else
            variableVal.add( noDataMsg );
        variableVals.add( variableVal );

        // get this gene in other organisms
        List<String> otherGenes = browser.getGeneOrganisms( resource.getURI() );
        variableVal = new ArrayList<String>();
        objectValue = "";
        int gi = 0;
        int outCnt = 0;
        while( gi < otherGenes.size() ) {
            String geneName = otherGenes.get( gi++ );
            if( gi >= otherGenes.size() )
                break;
            // skip this gene?
            if( geneName.equals( resource.getLocalName() ) ) {
                gi++;
                continue;
            }
            String organismName = otherGenes.get( gi++ );
            if( outCnt > 0 )
                objectValue += ", ";
            objectValue += "<a href=\"";
            objectValue += geneName + "\" class=\"navlink\"> ";
            objectValue += organismName  + "</a>";
            outCnt++;
        }

        variableVal.add( "Also Present In:" );
        variableVal.add( objectValue );
        variableVals.add( variableVal );

        // get the classification of this gene
        //
        objectValues = browser.getObjectPropertyValue( resource.getLocalName(), "codedBy", Browser.backward );
        if( objectValues != null && objectValues.size() > 0 ) {
            if( browser.isKinaseDomain( objectValues.get(0) ) ) {
                System.out.println( "Have domain name for classification\n" );
                String domainName = objectValues.get(0);
                KinaseDomainNode dNode = browser.getKinaseDomainNode( domainName );

                objectValue = "";
                dNode = dNode.getParent();
                while( dNode != null ) {
                    if( !dNode.getResourceNode().getResource().getLocalName().equals( "ProteinKinaseDomain" ) ) {
                        if( objectValue.length() > 0 )
                            objectValue = objectValue + ", ";
                        objectValue = 
                            objectValue + 
                            "<a href=\"" +
                            dNode.getResourceNode().getResource().getLocalName() +
                            "\" class=\"navlink\"> " +
                            getClassificationName( dNode.getResourceNode().getResource().getLocalName() ) +
                            "</a>";
                    }
                    dNode = dNode.getParent();
                }

                variableVal = new ArrayList<String>();
                variableVal.add( "Classification:" );
                if( objectValue.length() > 0 )
                    variableVal.add( objectValue );
                else
                    variableVal.add( noDataMsg );
                variableVals.add( variableVal );
                
            }
        }

        // get Kinase Domains
        //
        objectValue = getDataModelPropertyValue( "codedBy", resource,
                                                 Browser.backward );

        variableVal = new ArrayList<String>();
        variableVal.add( BrowserConfig.getPropertyDisplayName( "codedBy",
                Browser.backward ) );
        if( objectValue.length() > 0 )
            variableVal.add( objectValue );
        else
            variableVal.add( noDataMsg );
        variableVals.add( variableVal );

        // get Functional Domains
        //
        objectValue = getDataModelPropertyValue( "hasFunctionalDomain",
                resource, Browser.forward );

        variableVal = new ArrayList<String>();
        variableVal.add( BrowserConfig.getPropertyDisplayName(
                "hasFunctionalDomain", Browser.forward ) );
        if( objectValue.length() > 0 )
            variableVal.add( objectValue );
        else
            variableVal.add( noDataMsg );
        variableVals.add( variableVal );

        // get Sequences
        //
        objectValue = getDataModelPropertyValue( "hasSequence", resource,
                Browser.forward );

        variableVal = new ArrayList<String>();
        variableVal.add( BrowserConfig.getPropertyDisplayName( "hasSequence",
                Browser.forward ) );
        if( objectValue.length() > 0 )
            variableVal.add( objectValue );
        else
            variableVal.add( noDataMsg );
        variableVals.add( variableVal );

        // get Diseases
        //
        //        objectValue = getDataModelPropertyValue( "associatedWith", resource,
        //                ProKinOBrowser.forward );
        objectValue = getDataModelPropertyValue( "associatedWith", resource,
                                                 Browser.forward );

        variableVal = new ArrayList<String>();
        variableVal.add( BrowserConfig.getPropertyDisplayName( "associatedWith",
                Browser.forward ) );
        if( objectValue.length() > 0 )
            variableVal.add( objectValue );
        else
            variableVal.add( noDataMsg );
        variableVals.add( variableVal );

        // /////////////////////////////////////////////////////////////////////////
        //
        // Functional Features tab
        //
        variableVals = new ArrayList<ArrayList<String>>();

        dataModel.put( "funcvals", variableVals );

        // get hasFunctionalFeature property values
        //
        objectValue = getDataModelPropertyValue( "hasFunctionalFeature",
                resource, Browser.forward );

        variableVal = new ArrayList<String>();
        variableVal.add( BrowserConfig.getPropertyDisplayName(
                "hasFunctionalFeature", Browser.forward ) );
        if( objectValue.length() > 0 )
            variableVal.add( objectValue );
        else
            variableVal.add( noDataMsg );
        variableVals.add( variableVal );

        // /////////////////////////////////////////////////////////////////////////
        //
        // Mutations tab (including several sub-tabs)
        //

        processMutations( dataModel, resource );

        // /////////////////////////////////////////////////////////////////////////
        //
        // Pathways tab
        //
        variableVals = new ArrayList<ArrayList<String>>();

        // set up all pathways for this gene
        dataModel.put( "pathvals", variableVals );

        // get this gene's pathways
        List<String> pathways = browser.getGenePathways( resource.getURI() );
        variableVal = new ArrayList<String>();
        objectValue = "";
        int pi = 0;
        outCnt = 0;
        while( pi < pathways.size() ) {
            String pathwayURI = pathways.get( pi++ );
            if( pi >= pathways.size() )
                break;
            String pathwayName = pathways.get( pi++ );
            objectValue += "<a href=\"";
            objectValue += pathwayURI + "\" class=\"navlink\"> ";
            objectValue += pathwayName  + "</a><br>";
            outCnt++;
        }

        variableVal.add( BrowserConfig.getPropertyDisplayName( "participatesIn",
                                                             Browser.forward ) );
        variableVal.add( objectValue );
        variableVals.add( variableVal );


        // get this gene's reactions
        List<String> reactions = browser.getGeneReactions( resource.getURI() );
        variableVal = new ArrayList<String>();
        objectValue = "";
        pi = 0;
        outCnt = 0;
        while( pi < reactions.size() ) {
            String reactionURI = reactions.get( pi++ );
            if( pi >= reactions.size() )
                break;
            String reactionName = reactions.get( pi++ );
            objectValue += "<a href=\"";
            objectValue += reactionURI + "\" class=\"navlink\"> ";
            objectValue += reactionName  + "</a><br>";
            outCnt++;
        }

        variableVal.add( "Participates in Reactions:" );
        variableVal.add( objectValue );
        variableVals.add( variableVal );

        // get this gene's complexes
        List<String> complexes = browser.getGeneComplexes( resource.getURI() );
        variableVal = new ArrayList<String>();
        objectValue = "";
        pi = 0;
        outCnt = 0;
        while( pi < complexes.size() ) {
            String complexURI = complexes.get( pi++ );
            if( pi >= complexes.size() )
                break;
            String complexName = complexes.get( pi++ );
            objectValue += "<a href=\"";
            objectValue += complexURI + "\" class=\"navlink\"> ";
            objectValue += complexName  + "</a><br>";
            outCnt++;
        }

        variableVal.add( BrowserConfig.getPropertyDisplayName( "includes",
                                                             Browser.backward ) );
        variableVal.add( objectValue );
        variableVals.add( variableVal );

        // /////////////////////////////////////////////////////////////////////////
        //
        // References tab
        //
        variableVals = new ArrayList<ArrayList<String>>();
        dataModel.put( "referencevals", variableVals );

        processDbXrefs( variableVals, resource );

        return dataModel;

    }

    // //////////////////////////////////////////////////////////
    //
    // Other auxiliary methods
    //

    // return a value formatted for HTML display
    private String getDataModelPropertyValue(String property,
                                             ProKinOResource resource, 
                                             boolean forward)
    {
        List<ResourceNode> objects = null;
        StringBuffer         objectValue = null;
        ArrayList<SubDomain> subdomains = null;
        ArrayList<SubDomain> motifs = null;
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

            for( ListIterator<ResourceNode> rnodeIter = objects.listIterator(); rnodeIter.hasNext(); ) {

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
        if( forward && property.equals( "hasSubDomain" ) ) {

            System.out.println( "Processing sequence's subdomains" );

            subdomains = new ArrayList<SubDomain>();
            motifs     = new ArrayList<SubDomain>();

            for( ResourceNode subDom : objects ) {

                int startPos = Integer.parseInt( browser.getPropertyValue( subDom.getResource(), "hasStartLocation" ) );
                String name = browser.getPropertyValue( subDom.getResource(), "hasPrimaryName" );

                if( name.startsWith( "subdomain" ) ) {
                    subdomains.add( new SubDomain( subDom, name, startPos ) );
                    System.out.println( "Subdomain: " + name );
                }
                else {
                    motifs.add( new SubDomain( subDom, name, startPos ) );
                    System.out.println( "Motif: " + name );
                }
            }

            Collections.sort( subdomains, new SubDomainComparator() );
            Collections.sort( motifs, new SubDomainComparator() );

        }
        else
            Collections.sort( objects, new ResourceNodeComparator() );

        // now, form the property value string
        //
        objectValue.setLength( 0 );

        // combine the values into a String
        //
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
        else if( forward && property.equals( "hasSubDomain" ) ) { // process sub-domains

            // format sub-domains
            i = 0;
            for( SubDomain sDom : subdomains ) {

                // form the HTML href element
                //
                objectValue.append( "<a href=\"" );
                objectValue.append( Ontology.getResourceNameFragment( sDom.getSubDomain().getResource() ) +
                                    "\" class=\"navlink\"> " );
                objectValue.append( sDom.getName() + "</a>" );

                if( i < subdomains.size() - 1 )
                    objectValue.append( ", " );

                i++;
            }

            // format motifs
            if( subdomains.size() > 0 && motifs.size() > 0 ) {
                objectValue.append( ",<br>" );
            }

            i = 0;
            for( SubDomain sDom : motifs ) {

                // form the HTML href element
                //
                objectValue.append( "<a href=\"" );
                objectValue.append( Ontology.getResourceNameFragment( sDom.getSubDomain().getResource() ) +
                                    "\" class=\"navlink\"> " );
                objectValue.append( sDom.getName() + "</a>" );

                if( i < motifs.size() - 1 )
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

    // SPLIT UP THIS METHOD
    //
    @SuppressWarnings("unchecked")
    private void processMutations(Map<String, Object> dataModel,
            ProKinOResource resource)
    {
        HashMap<String, List<ResourceNode>> mutationMap = null;
        List<ResourceNode> mutations = null;
        String mutationType = null;
        ArrayList<ArrayList<String>> variableVals = null;
        ArrayList<String> variableVal = null;
        List<ResourceNode> objects = null;
        Iterator<ResourceNode> objectIter = null;
        ResourceNode object = null;
        String objectValue = null;

        // Initialize the mutation subtypes tab variables
        //

        // Substitutions
        //
        variableVals = new ArrayList<ArrayList<String>>();
        dataModel.put( "substitutionvals", variableVals );

        // Insertions
        //
        variableVals = new ArrayList<ArrayList<String>>();
        dataModel.put( "insertionvals", variableVals );

        // Deletions
        //
        variableVals = new ArrayList<ArrayList<String>>();
        dataModel.put( "deletionvals", variableVals );

        // Complex
        //
        variableVals = new ArrayList<ArrayList<String>>();
        dataModel.put( "complexvals", variableVals );

        // Fusions
        //
        variableVals = new ArrayList<ArrayList<String>>();
        dataModel.put( "fusionvals", variableVals );

        // Other
        //
        variableVals = new ArrayList<ArrayList<String>>();
        dataModel.put( "othervals", variableVals );

        // create the map of mutation types
        //
        mutationMap = new HashMap<String, List<ResourceNode>>();

        // get all mutations for this Gene
        //
        objects = resource.getFwdProperty( "hasMutation" );
        if( objects == null ) {
            System.out.println( "processMutations: objects is null" );
            // return;
        }

        // collect all mutations and group them by mutation type
        if( objects != null ) {

            // group mutations into their mutation types
            //
            objectIter = objects.iterator();
            while( objectIter.hasNext() ) {

                object = objectIter.next();

                mutationType = browser.getResourceTypeName( object.getResource() );

                mutations = mutationMap.get( mutationType );
                if( mutations == null ) {
                    mutations = new ArrayList<ResourceNode>();
                    mutationMap.put( mutationType, mutations );
                }
                mutations.add( object );
            }

        }

        // prepare mutation sub-tabs
        //
        // ////////////////////////
        //
        // Substitutions sub-tab
        //
        variableVals = (ArrayList<ArrayList<String>>) dataModel.get( "substitutionvals" );

        // get Missense mutation values
        //
        objectValue = getDataModelMutationTypeValue( "Missense", mutationMap );
        variableVal = new ArrayList<String>();
        variableVal.add( BrowserConfig.getPropertyDisplayName( "Missense", Browser.forward ) );
        variableVal.add( objectValue );
        variableVals.add( variableVal );

        // get CodingSilent mutation values
        //
        objectValue = getDataModelMutationTypeValue( "CodingSilent", mutationMap );
        variableVal = new ArrayList<String>();
        variableVal.add( BrowserConfig.getPropertyDisplayName( "CodingSilent", Browser.forward ) );
        variableVal.add( objectValue );
        variableVals.add( variableVal );

        // get Nonsense mutation values
        //
        objectValue = getDataModelMutationTypeValue( "Nonsense", mutationMap );
        variableVal = new ArrayList<String>();
        variableVal.add( BrowserConfig.getPropertyDisplayName( "Nonsense", Browser.forward ) );
        variableVal.add( objectValue );
        variableVals.add( variableVal );

        // ////////////////////////
        //
        // Insertions sub-tab
        //
        variableVals = (ArrayList<ArrayList<String>>) dataModel.get( "insertionvals" );

        // get InsertionInframe mutation values
        //
        objectValue = getDataModelMutationTypeValue( "InsertionInframe", mutationMap );
        variableVal = new ArrayList<String>();
        variableVal.add( BrowserConfig.getPropertyDisplayName( "InsertionInframe", Browser.forward ) );
        variableVal.add( objectValue );
        variableVals.add( variableVal );

        // get InsertionFrameshift mutation values
        //
        objectValue = getDataModelMutationTypeValue( "InsertionFrameshift", mutationMap );
        variableVal = new ArrayList<String>();
        variableVal.add( BrowserConfig.getPropertyDisplayName( "InsertionFrameshift", Browser.forward ) );
        variableVal.add( objectValue );
        variableVals.add( variableVal );

        // ////////////////////////
        //
        // Deletions sub-tab
        //
        variableVals = (ArrayList<ArrayList<String>>) dataModel.get( "deletionvals" );

        // get DeletionInframe mutation values
        //
        objectValue = getDataModelMutationTypeValue( "DeletionInframe", mutationMap );
        variableVal = new ArrayList<String>();
        variableVal.add( BrowserConfig.getPropertyDisplayName( "DeletionInframe", Browser.forward ) );
        variableVal.add( objectValue );
        variableVals.add( variableVal );

        // get DeletionFrameshift mutation values
        //
        objectValue = getDataModelMutationTypeValue( "DeletionFrameshift", mutationMap );
        variableVal = new ArrayList<String>();
        variableVal.add( BrowserConfig.getPropertyDisplayName( "DeletionFrameshift", Browser.forward ) );
        variableVal.add( objectValue );
        variableVals.add( variableVal );

        // ////////////////////////
        //
        // Complex sub-tab
        //
        variableVals = (ArrayList<ArrayList<String>>) dataModel.get( "complexvals" );

        // get ComplexInsertionInframe mutation values
        //
        objectValue = getDataModelMutationTypeValue( "ComplexInsertionInframe", mutationMap );
        variableVal = new ArrayList<String>();
        variableVal.add( BrowserConfig.getPropertyDisplayName( "ComplexInsertionInframe", Browser.forward ) );
        variableVal.add( objectValue );
        variableVals.add( variableVal );

        // get ComplexDeletionInframe mutation values
        //
        objectValue = getDataModelMutationTypeValue( "ComplexDeletionInframe", mutationMap );
        variableVal = new ArrayList<String>();
        variableVal.add( BrowserConfig.getPropertyDisplayName( "ComplexDeletionInframe", Browser.forward ) );
        variableVal.add( objectValue );
        variableVals.add( variableVal );

        // get ComplexFrameshift mutation values
        //
        objectValue = getDataModelMutationTypeValue( "ComplexFrameshift", mutationMap );
        variableVal = new ArrayList<String>();
        variableVal.add( BrowserConfig.getPropertyDisplayName( "ComplexFrameshift", Browser.forward ) );
        variableVal.add( objectValue );
        variableVals.add( variableVal );

        // get CompoundSubstitution mutation values
        //
        objectValue = getDataModelMutationTypeValue( "CompoundSubstitution", mutationMap );
        variableVal = new ArrayList<String>();
        variableVal.add( BrowserConfig.getPropertyDisplayName( "CompoundSubstitution", Browser.forward ) );
        variableVal.add( objectValue );
        variableVals.add( variableVal );

        // ////////////////////////
        //
        // Fusion sub-tab
        //
        variableVals = (ArrayList<ArrayList<String>>) dataModel.get( "fusionvals" );

        // get Fusion mutation values
        //
        objectValue = getDataModelMutationTypeValue( "Fusion", mutationMap );
        variableVal = new ArrayList<String>();
        variableVal.add( BrowserConfig.getPropertyDisplayName( "Fusion", Browser.forward ) );
        variableVal.add( objectValue );
        variableVals.add( variableVal );

        // ////////////////////////
        //
        // Other sub-tab
        //
        variableVals = (ArrayList<ArrayList<String>>) dataModel.get( "othervals" );

        // get Other mutation values
        //
        objectValue = getDataModelMutationTypeValue( "OtherMutation", mutationMap );
        variableVal = new ArrayList<String>();
        variableVal.add( BrowserConfig.getPropertyDisplayName( "OtherMutation", Browser.forward ) );
        variableVal.add( objectValue );
        variableVals.add( variableVal );

    }

    private String getDataModelMutationTypeValue(String mutationType,
                                                 HashMap<String, 
                                                 List<ResourceNode>> mutationMap)
    {
        List<ResourceNode> objects = null;
        ArrayList<Mutation>  mutations = null;
        StringBuffer         objectValue = null;
        String               mutationName = null;
        int                  i;

        if( mutationType.equals( "Fusion" ) )
            System.out.println( "Fusion start" );
        
        objectValue = new StringBuffer( 10000 );

        objectValue.setLength( 0 );

        mutations = new ArrayList<Mutation>();

        // get the objects for the mutation type
        //
        objects = mutationMap.get( mutationType );

        if( objects == null ) {
            System.out.println( "getDataModelMutationTypeValue: mutation: " +
                                mutationType +
                                ": objects is null" );
            return "no data";
        }

        for( ResourceNode mutation : objects ) {
            int startPos;
            try {
                startPos = Integer.parseInt( browser.getPropertyValue( mutation.getResource(), "hasStartLocation" ) );
            }
            catch( NumberFormatException nfe ) {
                startPos = 999999999;
            }

            if( mutationType.equals( "Fusion" ) )
                mutationName = browser.getPropertyValue( mutation.getResource(), "hasFusionDescription" );
            else
                mutationName = browser.getPropertyValue( mutation.getResource(), "hasMutationAA" );

            mutations.add( new Mutation( mutation, mutationName, startPos ) );
        }

        // sort the values
        //
        Collections.sort( mutations, new MutationComparator() );

        // combine the values into a String
        i = 0;
        for( Mutation mutation : mutations ) {

            // form the HTML href element
            //
            objectValue.append( "<a href=\"" );
            objectValue.append( Ontology.getResourceNameFragment( mutation.getMutation().getResource() ) );
            objectValue.append( "\" class=\"navlink\"> " );
            objectValue.append( mutation.getName() + "</a>" );

            if( i < objects.size() - 1 )
                objectValue.append( ", " );

            i++;

        }

        if( mutationType.equals( "Fusion" ) )
            System.out.println( "Fusion stop" );
        
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

    private String getClassificationName( String name )
    {
        int pos;

        if( ( pos = name.indexOf( "subfamily" ) ) > 0 )
            return name.substring( 0, pos ) + " " + name.substring( pos );
        else if( ( pos = name.indexOf( "family" ) ) > 0 )
            return name.substring( 0, pos ) + " " + name.substring( pos );
        else if( ( pos = name.indexOf( "group" ) ) > 0 )
            return name.substring( 0, pos ) + " " + name.substring( pos );
        else
            return name;
    }

    private String getPDBStructures( ProKinOResource resource )
    {
        List<ResourceNode> objects = null;
        StringBuffer         objectValue = null;
        ArrayList<SubDomain> subdomains = null;
        ArrayList<SubDomain> motifs = null;
        ArrayList<String>    structuresWithPKDomain = null;
        ArrayList<String>    structuresWithNoPKDomain = null;
        ResourceNode         uniProtSeq = null;
        int                  i;

        objectValue = new StringBuffer( 10000 );
        // objectValue = "";

        // get sequences
        //
        objects = resource.getFwdProperty( "hasSequence" );

        if( objects == null ) {
            System.out.println( "getPDBStructures: objects is null; property: hasSequence" );
            // return "no data";
            return objectValue.toString();
        }

        // find the UniProt sequence
        //
        for( ResourceNode rn : objects )
            if( rn.getDisplayString().endsWith( "-UniProt" ) )
                uniProtSeq = rn;

        System.out.println( "getPDBStructures: found the UniProt sequence: " + uniProtSeq.getDisplayString() );

        List<String> structures = browser.getObjectPropertyValue( uniProtSeq.getDisplayString(), "hasProteinStructure", Browser.forward );

        if( structures != null ) {

            for( String structure : structures ) {

                String name = null;

		String hasPKDomain = null;

		name = browser.getPropertyValue( structure, "hasPrimaryName" );
		hasPKDomain = browser.getPropertyValue( structure, "hasPKDomain" );

		if( hasPKDomain != null && hasPKDomain.equals( "true" ) ) {
		    structuresWithPKDomain.add( structure );
		    System.out.println( "Structure with PK Domain: " + name );
		}
		else {
                    structuresWithNoPKDomain.add( structure );
                    System.out.println( "Structure with no PK Domain: " + name );
		}
            }
	    Collections.sort( structuresWithPKDomain );
	    Collections.sort( structuresWithNoPKDomain );

        }

        return objectValue.toString();
    }

}

