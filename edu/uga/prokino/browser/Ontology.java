package edu.uga.prokino.browser;


import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import org.apache.jena.atlas.iterator.Iter;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtModel;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import com.hp.hpl.jena.shared.PrefixMapping;

import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryException;
import com.hp.hpl.jena.query.QuerySolution;




/**
 * @author Krys J. Kochut
 * 
 *         This class serves as a representation of the ProKinO ontology. It
 *         provides a variety of methods to query the ontology resources,
 *         properties, and related information
 */
public class Ontology
{
    private static final String prokinoGraphURI     = "http://prokino.uga.edu";
    private static final int    WORDSINITCAPACITY   = 100;
    
    private VirtGraph           graph               = null;
    private VirtModel           vModel              = null;
    private OntModel            model               = null;
    
    private Boolean             verbose             = false;
    private Set<String>         propExclusions      = null;
    
    private String              prokinoVersion      = null;
    private String              prokinoDate         = null;

    private String              rdfNameSpace        = null;
    private String              rdfsNameSpace       = null;

    private String              nameSpace           = null;
    private String              prokinoNameSpace    = null;
    private PrefixMapping       prefixMapping       = null;

    public static final Boolean forward             = true;
    public static final Boolean backward            = false;
    
    private Property            subClassOfPro       = null;


    private Property            hasMutationAAPro    = null;
    private Property            hasFusionDescriptionPro = null;
    private Property            hasPrimaryNamePro   = null;
    private Property            hasIdentifierPro    = null;
    private Property            isPrimaryUniprotIdPro = null;
    private Property            hasURIPro           = null;
    private Property            labelPro            = null;
    private Property            commentPro          = null;
    private Property            typePro             = null;

    private Property            hasStartLocationPro = null;
    private Property            hasEndLocationPro   = null;
    private Property            hasPositionPro      = null;

    private KinaseDomainNode    hierarchyRoot       = null;
    private int                 hierarchyIndivNo    = 0;
    private HashMap<String, KinaseDomainNode> domainMap = null;

    private Resource            geneRes             = null;

    private Set<String>         searchResEntities   = null;

    private static final String ParseExceptionClass = "com.hp.hpl.jena.query.QueryParseException";
    private static final String resName = null;
    
    private static Ontology     ontology            = null;
    
    private PageCache           resourceCache      = null;

    

    /**
     * Create a ProKinO object wrapping the ProKinO ontology from an external
     * file
     * 
     * @param ontologyFileName
     *            - the ProKinO OWL ontology file name
     * @param v
     *            - keep in verbose mode
     */
    private Ontology(Boolean v)
    {
        init(v);
    }

    /**
     * Connect to the ProKinO ontology server.
     * @param graphName the name of the ProKinO graph
     * @param ontologyServerURL ProKinO ontology server URL
     * @param userName the user name to use in connecting
     * @param password the password to use in connecting
     */
    public void connectToOntology( String graphName, String ontologyServerURL, String userName, String password )
    {    
        //VirtModel vModel = VirtModel.openDatabaseModel( "http://prokino.uga.edu", "jdbc:virtuoso://vulcan.cs.uga.edu:8088", "dba", "dba" );

        vModel = VirtModel.openDatabaseModel( graphName, ontologyServerURL, userName, password );
        model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, vModel );
        graph = new VirtGraph( graphName, ontologyServerURL, userName, password );
        
        System.out.println( "ProKinO.loadOntology: Connected to ProKinO at " + ontologyServerURL );
    }
    
    private void init(boolean v)
    {
        propExclusions = new HashSet<String>();

        connectToOntology( BrowserConfig.ontologyServerGraph, BrowserConfig.ontologyServerURL, 
                           BrowserConfig.ontologyServerUser, BrowserConfig.ontologyServerPassword );
        
        resourceCache = PageCache.getPageCache();
        
        Map<String, String> prefixMap = model.getNsPrefixMap();
        
        rdfNameSpace = prefixMap.get( "rdf" );
        System.out.println( "rdf: namespace: " + rdfNameSpace );

        rdfsNameSpace = prefixMap.get( "rdfs" );
        System.out.println( "rdfs: namespace: " + rdfsNameSpace );

        nameSpace = prefixMap.get( "prokino" );
        prokinoNameSpace = nameSpace;
        System.out.println( "ProKinO: namespace: " + prokinoNameSpace );
        
        prefixMapping = PrefixMapping.Factory.create();
        if( prefixMapping != null ) {
            prefixMapping.setNsPrefix( "prokino", prokinoNameSpace );
            prefixMapping.setNsPrefix( "rdf", rdfNameSpace );
            prefixMapping.setNsPrefix( "rdfs", rdfsNameSpace );
            System.out.println( "ProKinO: namespace: " + prokinoNameSpace );
        }
        else
            System.out.println( "Ontology.init: failed to obtain prefixMapping" );

        Set<String> keys = prefixMap.keySet();
        Iterator<String> keyIter = keys.iterator();

        System.out.println( "PrefixMap Keys: " );
        while( keyIter.hasNext() )
            System.out.println( "\t" + keyIter.next() );

        System.out.println( "getNsURIPrefix of hasPrimaryName: " +
                            model.getNsURIPrefix( "hasPrimaryName" ) );
        System.out.println( "getNsPrefixURI of hasPrimaryName: " +
                            model.getNsPrefixURI( "hasPrimaryName" ) );
        System.out.println( "expandPrefix   of hasPrimaryName: " +
                            model.expandPrefix( "hasPrimaryName" ) );
        System.out.println( "qnameFor       of hasPrimaryName: " +
                            model.qnameFor( "hasPrimaryName" ) );
        System.out.println( "shortForm      of hasPrimaryName: " +
                            model.shortForm( "hasPrimaryName" ) );
        
        subClassOfPro = model.getProperty( rdfsNameSpace, "subClassOf" );

        hasMutationAAPro = model.getProperty( nameSpace + "hasMutationAA" );
        hasFusionDescriptionPro = model.getProperty( nameSpace + "hasFusionDescription" );
        hasPrimaryNamePro = model.getProperty( nameSpace + "hasPrimaryName" );
        hasIdentifierPro = model.getProperty( nameSpace + "hasIdentifier" );
        hasURIPro = model.getProperty( nameSpace + "hasURI" );
        isPrimaryUniprotIdPro = model.getProperty( nameSpace + "isPrimaryUniprotId" );
        
        System.out.println( "hasMutationAAPro: " + hasMutationAAPro );
        System.out.println( "hasPrimaryNamePro: " + hasPrimaryNamePro );
        System.out.println( "isPrimaryUniprotIdPro: " + isPrimaryUniprotIdPro );

        hasStartLocationPro = model.getProperty( nameSpace + "hasStartLocation" );
        hasEndLocationPro = model.getProperty( nameSpace + "hasEndLocation" );
        hasPositionPro = model.getProperty( nameSpace + "hasPosition" );

        labelPro = model.getProperty( "http://www.w3.org/2000/01/rdf-schema#label" );
        commentPro = model.getProperty( "http://www.w3.org/2000/01/rdf-schema#comment" );
        typePro = model.getProperty( rdfNameSpace + "type" );

        geneRes = model.getResource( nameSpace + "Gene" );
        
        System.out.println( "Have OntResource for Gene: " + model.getOntResource( nameSpace + "Gene" ) );

        // allocate the map for kinase domains
        domainMap = new HashMap<String, KinaseDomainNode>();
        hierarchyRoot = getKinaseDomainHierarchy();

        if( searchResEntities == null )
            searchResEntities = new HashSet<String>();

        verbose = v;

        if( verbose ) {
            System.out.println( "ProKinO: ProKinO object created." );
            System.out.println( "ProKinO: server: " + BrowserConfig.ontologyServerURL );
            System.out.println( "ProKinO: graph: " + BrowserConfig.ontologyServerGraph );
            System.out.println( "ProKinO: version: " + getVersionInfo() );
            System.out.println( "ProKinO: date: " + getDate() );
        }
    }
    
    public static synchronized Ontology getOntology()
    {
        if( ontology == null )
            ontology = new Ontology( true );
        return ontology;
    }

    /**
     * Add the property to the set of properties to be excluded from
     * ProKinOResource neighborhood
     * 
     * @param property
     *            - name of the property to be excluded
     */
    public void addPropExclusion(String property)
    {
        propExclusions.add( property );
    }

    /**
     * Remove the property from the set of properties to be excluded from
     * ProKinOResource neighborhood
     * 
     * @param property
     *            - name of the excluded property
     */
    public void delPropExclusion(String property)
    {
        propExclusions.remove( property );
    }
    
    /**
     * Output type of the resources returned in a query result from executeSPARQL.
     * RDFNODE requests that the resulting resources be represented as RDFNodes,
     * while STRING indicates String representation.
     */
    public enum OutputType {
        RDFNODE,
        STRING
    }

    /**
     * Output type of the resources returned in a query result from executeSPARQL.
     * FULLURI requests that the resulting resources be represented as full URIs,
     * while LOCALNAME indicates that only local names of resource URIs be returned.
     */
    public enum URIType {
        FULLURI,
        LOCALNAME
    }
    
    /**
     * A generic method to execute a SPARQL query against the ProKinO ontology.
     * It returns a List containing the resulting rows (as Lists).  The values in each row are Objects, 
     * which may be either Strings or RDFNodes, depending on the requested output.
     * @param queryString the string representing the query
     * @param oType type of the output (RDFNODE or STRING)
     * @param uriType type of the URI in returned resources (FULLURI or LOCALNAME)
     * @return the list of rows representing the result
     */
    public List<List<Object>> executeSPARQL( String queryString, OutputType oType, URIType uriType )
    {
        System.out.println( "executeSPARQL query: " + queryString );
        Query query = QueryFactory.create( queryString );
        VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create( query, graph );

        ResultSet results = vqe.execSelect();

        List<List<Object>> resultList = new ArrayList<List<Object>>();
        QuerySolution result = null;

        List<String> varList = results.getResultVars();
        List<String> vars = new ArrayList<String>();
        
        int i = 0;
        for( String var : varList ) {
            if( !var.equals( "graph" ) ) {
                vars.add( var );
            }
        }

        while( results.hasNext() ) {
            result = results.nextSolution();
            List<Object> nextRow = new ArrayList<Object>();

            for( i = 0; i < vars.size(); i++ ) {
                RDFNode varValue = result.get( vars.get(i) );
                if( oType == OutputType.RDFNODE )
                    nextRow.add( varValue );
                else if( oType == OutputType.STRING ) {
                    if( uriType == URIType.FULLURI )
                        nextRow.add( varValue.toString() );
                    else {
                        if( varValue.isResource() )
                            nextRow.add( varValue.asNode().getLocalName() );
                        else if( varValue.isAnon() )
                            nextRow.add( varValue.asNode().getLocalName() );
                        else if( varValue.isURIResource() )
                            nextRow.add( varValue.asNode().getLocalName() );
                        else
                            nextRow.add( varValue.toString() );
                    }
                }
            }

            resultList.add( nextRow );

        }

        vqe.close();

        return resultList;
    }
    
    /**
     * Get the versionInfo property value of this ontology
     * @return - String representing the version of this ontology
     */
    public String getVersionInfo()
    {
        String query = "select ?version from <http://prokino.uga.edu> where {  ?s <http://www.w3.org/2002/07/owl#versionInfo> ?version }";
        List<List<Object>> result = null;
        
        if( prokinoVersion != null )
            return prokinoVersion;
        
        result = executeSPARQL( query, OutputType.STRING, URIType.LOCALNAME );
        if( result == null || result.size() == 0 )
            return "";
        
        prokinoVersion = (String) result.get( 0 ).get( 0 );
        
        return prokinoVersion;
    }

    /**
     * Get the versionInfo property value of this ontology
     * @return - String representing the date of this ontology version
     */
    public String getDate()
    {
        String query = "select ?date from <http://prokino.uga.edu> where {  ?s <http://purl.org/dc/elements/1.1/date> ?date }";
        List<List<Object>> result;
        
        if( prokinoDate != null )
            return prokinoDate;
        
        result = executeSPARQL( query, OutputType.STRING, URIType.LOCALNAME );
        if( result == null || result.size() == 0 )
            return null;
        
        prokinoDate = (String) result.get( 0 ).get( 0 );
                
        return prokinoDate;
    }

    /**
     * Return the Property object (Jena) representing the given property
     * 
     * @param propertyName - the name of the property
     * @return - Property object for the requested property
     */
    public Property getPropertyByName(String propertyName)
    {
        if( propertyName.equals( "hasMutationAA" ) )
            return hasMutationAAPro;
        else if( propertyName.equals( "hasPrimaryName" ) )
            return hasPrimaryNamePro;
        else if( propertyName.equals( "hasIdentifier" ) )
            return hasIdentifierPro;
        else if( propertyName.equals( "hasURI" ) )
            return hasURIPro;
        else if( propertyName.equals( "label" ) )
            return labelPro;
        else if( propertyName.equals( "comment" ) )
            return commentPro;
        else
            return null;
    }
    
    public Resource getRDFType( Resource res )
    {
        String queryString = "select ?type where { <" + res.toString() + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?type }";
        
        List<List<Object>> types = executeSPARQL( queryString, OutputType.RDFNODE, URIType.FULLURI );
        if( types.size() == 0 )
            return null;
        
        for( List<Object> row : types )
            for( Object type : row ) {
                RDFNode typeNode = (RDFNode) type;
                if( typeNode.isResource() )
                    return (Resource) typeNode;
            }
                
        return null;
    }
    
    public List<Resource> getRDFTypes( Resource res )
    {
        String queryString = "select ?type where { <" + res.toString() + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?type }";
        List<Resource> rdfTypes = new ArrayList<Resource>();
        
        System.out.println( "getRDFTypes query: " + queryString );
        System.out.flush();
        
        List<List<Object>> types = executeSPARQL( queryString, OutputType.RDFNODE, URIType.FULLURI );
        if( types.size() == 0 )
            return rdfTypes;
        
        for( List<Object> row : types )
            for( Object type : row ) {
                RDFNode typeNode = (RDFNode) type;
                if( typeNode.isResource() )
                    rdfTypes.add( (Resource) typeNode );
            }
                
        return rdfTypes;
    }
    
    public String getDisplayString( Resource res )
    {
        Resource type = null;
        String typeName = null;
        String displayName = null;
        int pos;

        if( res == null ) {
            System.out.println( "getDisplayString: null res!" );
            return "";
        }
            
        type = getRDFType( res );
        if( type == null ) {
            String fragment = getResourceNameFragment( res );
            return fragment;
        }

        typeName = getResourceNameFragment( type );

        if( typeName.equals( "Mutation" ) ||
            typeName.equals( "ComplexMutation" ) ||
            typeName.equals( "ComplexDeletionInframe" ) ||
            typeName.equals( "ComplexFrameshift" ) ||
            typeName.equals( "ComplexInsertionInframe" ) ||
            typeName.equals( "CompoundSubstitution" ) ||
            typeName.equals( "DeletionMutation" ) ||
            typeName.equals( "DeletionFrameshift" ) ||
            typeName.equals( "DeletionInframe" ) ||
            typeName.equals( "InsertionMutation" ) ||
            typeName.equals( "InsertionFrameshift" ) ||
            typeName.equals( "InsertionInframe" ) ||
            typeName.equals( "SubstitutionMutation" ) ||
            typeName.equals( "CodingSilent" ) ||
            typeName.equals( "Missense" ) ||
            typeName.equals( "Nonsense" ) ||
            typeName.equals( "OtherMutation" ) )
            displayName = getPropertyValue( res, hasMutationAAPro );
        else if( typeName.equals( "Fusion" ) ) {
            displayName = getPropertyValue( res, hasFusionDescriptionPro );
        }
        else if( typeName.equals( "TopologicalDomain" ) ||
                 typeName.equals( "SignalPeptide" ) ) {

            try {
                int startPos = Integer.parseInt( getPropertyValue( res, hasStartLocationPro ) );
                int endPos = Integer.parseInt( getPropertyValue( res, hasEndLocationPro ) );

                displayName = typeName + "(" + startPos + ".." + endPos + ")";
            }
            catch( NumberFormatException nfe ) {
                displayName = getPropertyValue( res, labelPro );
            }

        }
        else if( typeName.equals( "StructuralMotif" ) ) {
            displayName = getPropertyValue( res, labelPro );
        }
        else if( typeName.equals( "ModifiedResidue" ) ) {

            try {
                int position = Integer.parseInt( getPropertyValue( res, hasPositionPro ) );

                displayName = typeName + "(" + position + ")";
            }
            catch( NumberFormatException nfe ) {
                displayName = getPropertyValue( res, labelPro );
            }

        }
        else if( typeName.equals( "Reaction" ) ||
                 typeName.equals( "Cancer" ) ||
                 typeName.equals( "Pathway" ) ||
                 typeName.equals( "Complex" ) ||
                 typeName.equals( "SmallMoleculeEntity" ) ||
                 typeName.equals( "GenomeEncodedEntity" ) ||
                 typeName.equals( "EntitySet" ) ||
                 typeName.equals( "CatalystActivity" ) ||
                 typeName.equals( "FunctionalDomain" ) ) {
            displayName = getPropertyValue( res, hasPrimaryNamePro );
            // if no hasPrimaryName defined, return the local name
            if( displayName == null || displayName.length() == 0 )
                displayName = getResourceNameFragment( res );
        }
        else if( typeName.equals( "Structure" ) ) {
            displayName = getPropertyValue( res, hasPrimaryNamePro );
        }
        else if( typeName.equals( "Sequence" ) ) {
            displayName = res.getLocalName();
            if( displayName.startsWith( "Seq-" ) )
                return displayName.substring( 4 );
            else
                return displayName;
        }
        else {
            String fragment = getResourceNameFragment( res );
            return fragment;
        }

        pos = displayName.indexOf( "^^http://www.w3.org/2001/XMLSchema#string" );
        if( pos != -1 )
            displayName = displayName.substring( 0, pos );

        pos = displayName.indexOf( "@en" );
        if( pos != -1 )
            displayName = displayName.substring( 0, pos );

        return BrowserUtility.cleanLiteralValue( displayName );
    }
    
    public String getDisplayString( Resource res, Resource type )
    {
        String typeName = null;
        String displayName = null;
        int pos;


        if( res == null ) {
            System.out.println( "getDisplayString2: null res!" );
            return "";
        }
        

        if( type == null ) {
            System.out.println( "getDisplayString2: null type!" );
            return "";
        }
        
        typeName = getResourceNameFragment( type );

        if( typeName.equals( "Mutation" ) ||
            typeName.equals( "ComplexMutation" ) ||
            typeName.equals( "ComplexDeletionInframe" ) ||
            typeName.equals( "ComplexFrameshift" ) ||
            typeName.equals( "ComplexInsertionInframe" ) ||
            typeName.equals( "CompoundSubstitution" ) ||
            typeName.equals( "DeletionMutation" ) ||
            typeName.equals( "DeletionFrameshift" ) ||
            typeName.equals( "DeletionInframe" ) ||
            typeName.equals( "InsertionMutation" ) ||
            typeName.equals( "InsertionFrameshift" ) ||
            typeName.equals( "InsertionInframe" ) ||
            typeName.equals( "SubstitutionMutation" ) ||
            typeName.equals( "CodingSilent" ) ||
            typeName.equals( "Missense" ) ||
            typeName.equals( "Nonsense" ) ||
            typeName.equals( "OtherMutation" ) )
            displayName = getPropertyValue( res, hasMutationAAPro );
        else if( typeName.equals( "Fusion" ) ) {
            displayName = getPropertyValue( res, hasFusionDescriptionPro );
        }
        else if( typeName.equals( "TopologicalDomain" ) ||
                 typeName.equals( "SignalPeptide" ) ) {

            try {
                int startPos = Integer.parseInt( getPropertyValue( res,
                        hasStartLocationPro ) );
                int endPos = Integer.parseInt( getPropertyValue( res,
                        hasEndLocationPro ) );

                displayName = typeName + "(" + startPos + ".." + endPos + ")";
            }
            catch( NumberFormatException nfe ) {
                displayName = getPropertyValue( res, labelPro );
            }

        }
        else if( typeName.equals( "StructuralMotif" ) ) {
            displayName = getPropertyValue( res, labelPro );
        }
        else if( typeName.equals( "ModifiedResidue" ) ) {

            try {
                int position = Integer.parseInt( getPropertyValue( res,
                        hasPositionPro ) );

                displayName = typeName + "(" + position + ")";
            }
            catch( NumberFormatException nfe ) {
                displayName = getPropertyValue( res, labelPro );
            }

        }
        else if( typeName.equals( "Reaction" ) ||
                 typeName.equals( "Cancer" ) ||
                 typeName.equals( "Pathway" ) ||
                 typeName.equals( "Complex" ) ||
                 typeName.equals( "SmallMoleculeEntity" ) ||
                 typeName.equals( "GenomeEncodedEntity" ) ||
                 typeName.equals( "EntitySet" ) ||
                 typeName.equals( "CatalystActivity" ) ||
                 typeName.equals( "FunctionalDomain" ) ) {
            displayName = getPropertyValue( res, hasPrimaryNamePro );
            // if no hasPrimaryName defined, return the local name
            if( displayName == null || displayName.length() == 0 )
                displayName = getResourceNameFragment( res );
        }
        else if( typeName.equals( "Structure" ) ) {
            displayName = getPropertyValue( res, hasPrimaryNamePro );
        }
        else if( typeName.equals( "Sequence" ) ) {
            displayName = res.getLocalName();
            if( displayName.startsWith( "Seq-" ) )
                return displayName.substring( 4 );
            else
                return displayName;
        }
        else {
            String fragment = getResourceNameFragment( res );
            return fragment;
        }

        pos = displayName.indexOf( "^^http://www.w3.org/2001/XMLSchema#string" );
        if( pos != -1 )
            displayName = displayName.substring( 0, pos );

        pos = displayName.indexOf( "@en" );
        if( pos != -1 )
            displayName = displayName.substring( 0, pos );

        return BrowserUtility.cleanLiteralValue( displayName );
    }
    
    public static String getResourceNameFragment(Resource res)
    {
        String uri = res.getURI();
        int pos;

        if( uri != null ) {
            pos = uri.lastIndexOf( '#' );
            if( pos >= 0 )
                return uri.substring( pos + 1 );
            else
                return res.toString();
        }
        else {
            System.err.println( "ProKinO.getResourceNameFragment: uri: " + uri );
            System.err.println( "ProKinO.getResourceNameFragment: res: " + res.toString() );
            return "**unknown**";
        }
    }

    public static String getResourceNameFragment(String uri)
    {
        int pos;

        if( uri != null ) {
            pos = uri.lastIndexOf( '#' );
            if( pos >= 0 )
                return uri.substring( pos + 1 );
            else
                return uri;
        }
        else {
            System.err.println( "ProKinO.getResourceNameFragment: uri: " + uri );
            return "**unknown**";
        }
    }

    public String getResourceTypeName(Resource res)
    {
        OntResource ontres = model.getOntResource( res );
        return getResourceTypeName( ontres );
    }

    public String getResourceTypeName(OntResource res)
    {
        Resource type = null;

        type = res.getRDFType( true );
        return getResourceNameFragment( type );
    }

    public String getResourceTypeNames(Resource res)
    {
        OntResource ontres = model.getOntResource( res );
        return getResourceTypeNames( ontres );
    }

    public String getResourceTypeNames(OntResource res)
    {
        ExtendedIterator typeIter = null;
        List<Resource> types = null;
        Resource type = null;
        String displayName = null;
        Boolean isAGene = false;
        int genePos;
        int noTypes;
        int i;

        typeIter = res.listRDFTypes( true );
        types = new ArrayList<Resource>();

        genePos = -1;
        noTypes = 0;
        while( typeIter.hasNext() ) {
            type = (Resource) typeIter.next();
            if( type.equals( geneRes ) ) {
                isAGene = true;
                genePos = noTypes;
            }
            types.add( type );
            noTypes++;
        }
        typeIter.close();
        
        displayName = "";
        if( types.size() > 1 && isAGene ) {
            displayName += getResourceNameFragment( types.get( genePos ) ) +
                           ", ";
            for( i = 0; i < types.size(); i++ ) {
                if( i != genePos ) {
                    displayName += getResourceNameFragment( types.get( i ) );
                    if( i < types.size() - 2 )
                        displayName += ", ";
                }
            }
        }
        else {
            for( i = 0; i < types.size(); i++ ) {
                if( i != genePos ) {
                    displayName += getResourceNameFragment( types.get( i ) );
                    if( i < types.size() - 1 )
                        displayName += ", ";
                }
            }
        }

        if( types.size() <= 0 )
            return "**unkown**";

        return displayName;
    }

    public List<String> getResourceTypes(Resource res)
    {
        OntResource ontres = model.getOntResource( res );
        return getResourceTypes( ontres );
    }

    public List<String> getResourceTypes(OntResource res)
    {
        ExtendedIterator typeIter = null;
        List<Resource> types = null;
        Resource type = null;
        List<String> displayName = null;
        Boolean isAGene = false;
        int genePos;
        int noTypes;
        int i;

        typeIter = res.listRDFTypes( true );
        types = new ArrayList<Resource>();

        genePos = -1;
        noTypes = 0;
        while( typeIter.hasNext() ) {
            type = (Resource) typeIter.next();
            if( type.equals( geneRes ) ) {
                isAGene = true;
                genePos = noTypes;
            }
            types.add( type );
            noTypes++;
        }
        typeIter.close();
        
        displayName = new ArrayList<String>();
        if( types.size() > 1 && isAGene ) {
            displayName.add( getResourceNameFragment( types.get( genePos ) ) );
            for( i = 0; i < types.size(); i++ ) {
                if( i != genePos ) {
                    displayName.add( getResourceNameFragment( types.get( i ) ) );
                }
            }
        }
        else {
            for( i = 0; i < types.size(); i++ ) {
                if( i != genePos ) {
                    displayName.add( getResourceNameFragment( types.get( i ) ) );
                }
            }
        }

        return displayName;
    }

    public String getPropertyValue(String resName, String propertyName)
    {
        Query          query = null;
        QueryExecution qexec = null;
        ResultSet      results = null;
        String         output = "";
        String         queryString = null;
        String         resURI = null;
        String         propURI = null;
        
        //System.out.println( "getPropertyValue: res: " + resName + "  prop: " + propertyName );
        
        if( resName == null ) {
            System.out.println( "getPropertyValue: null resName" );
            return "";
        }
        if( propertyName == null ) {
            System.out.println( "getPropertyValue: null propertyName" );
            return ""; 
        }
        
        if( resName.startsWith( "http" ) )
            resURI = resName;
        else
            resURI = prokinoNameSpace + resName;
        
        if( propertyName.startsWith( "http" ) )
            propURI = propertyName;
        else
            propURI = prokinoNameSpace + propertyName;
        //System.out.println( "getPropertyValue: resURI: " + resURI + "  propURI: " + propURI );
       
        queryString = "select (str(?lit) as ?object) from <" + BrowserConfig.ontologyServerGraph + "> where { <" + resURI + "> <" + propURI + "> ?lit filter isLiteral(?lit) } limit 1";
        //System.out.println( "getPropertyValue: Query>>" + queryString + "<<" );
        //System.out.flush();
        try {
            query = QueryFactory.create( queryString );
            qexec = VirtuosoQueryExecutionFactory.sparqlService( BrowserConfig.sparqlServiceURL, query);
            results = qexec.execSelect();
            
            if( results.hasNext() ) {

                QuerySolution solutionRow = results.nextSolution();

                Literal objectNode  = solutionRow.getLiteral( "?object" );
                if( objectNode != null ) {
                    output = objectNode.toString();
                    //System.out.println( "getPropertyValue: resURI: " + resURI + "  propURI: " + propURI + " object is >>" + output + "<<");
                }
            }
            else
                System.out.println( "getPropertyValue: resURI: " + resURI + "  propURI: " + propURI + " no results!" );
        }
        catch( QueryException qe ) {
            qe.printStackTrace( System.err );
        }
        finally {
            if( qexec != null )
                qexec.close();
        }
        return output;
    }
    
    // propertyName must be a data property!
    //
    public String getPropertyValue( Resource res, String propertyName )
    {
        if( res == null ) {
            System.out.println( "getPropertyValue2: null res" );
            return "";
        }
        return getPropertyValue( res.toString(), propertyName );
    }
    
    // propertyName must be a data property!
    //
    public String getPropertyValue( Resource res, Property property )
    {
        if( res == null ) {
            System.out.println( "getPropertyValue3: null res" );
            return "";
        }
        if( property == null ) {
            System.out.println( "getPropertyValue3: null property" );
            return "";
        }
        return getPropertyValue( res.toString(), property.toString() );
    }
    
    /*
    // propertyName must be a data property!
    //
    public String getPropertyValue(Resource res, String propertyName)
    {
        Property property = model.getProperty( nameSpace + propertyName );

        return getPropertyValue( res, property );
    }

    // propertyName must be a data property!
    //
    public String getPropertyValue(Resource res, Property property)
    {
        //System.out.println( "getPropertyValue1: res: " + res + " prop: " + property );
        OntResource ontres = model.getOntResource( res );
        return getPropertyValue( ontres, property );
    }

    // propertyName must be a data property!
    //
    public String getPropertyValue(OntResource res, Property property)
    {
        StmtIterator sIter = null;
        Statement stmt = null;
        RDFNode object = null;
        String displayName = null;
        int pos;

        if( res.toString().indexOf( ' ' ) >= 0 ) {
            System.out.println( "getPropertyValue2: res with a space: " + res + " prop: " + property );
            return "";
        }
        sIter = model.listStatements( new SimpleSelector( res, property, (RDFNode) null ) );

        // return the first object value
        if( sIter.hasNext() ) {
            stmt = sIter.nextStatement();
            object = stmt.getObject();
            displayName = object.toString();
        }
        else
            displayName = "";

        pos = displayName.indexOf( "^^http" );
        if( pos != -1 )
            displayName = displayName.substring( 0, pos );

        pos = displayName.indexOf( "@en" );
        if( pos != -1 )
            displayName = displayName.substring( 0, pos );

        sIter.close();
        
        return displayName;

    }
    */

    /*
    public List<OntResource> getObjectPropertyValue(OntResource res, Property property, boolean forward )
    {
        StmtIterator sIter = null;
        Statement stmt = null;
        RDFNode object = null;
        Resource subject = null;
        List<OntResource> result = new ArrayList<OntResource>();

        if( forward ) {
            sIter = model.listStatements( new SimpleSelector( res, property, (RDFNode) null ) );
            while( sIter.hasNext() ) {
                stmt = sIter.nextStatement();
                object = stmt.getObject();
                if( object.isResource() )
                    result.add( model.getOntResource( (Resource) object.asNode() ) );
            }
            sIter.close();
        }
        else {
            sIter = model.listStatements( new SimpleSelector( null, property, res ) );
            while( sIter.hasNext() ) {
                stmt = sIter.nextStatement();
                subject = stmt.getSubject();
                result.add( model.getOntResource( model.getOntResource( subject ) ) );
            }
            sIter.close();
        }

        return result;

    }
    */
    
    // return a list of local names
    public List<String> getObjectPropertyValue( String res, String property, boolean forward )
    {
        Query              query = null;
        QueryExecution     qexec = null;
        ResultSet          results = null;
        String             fwdQueryString = "select ?o where { <" + prokinoNameSpace + res + "> <" + prokinoNameSpace + property + "> ?o }";
        String             backQueryString = "select ?s where { ?s <" + prokinoNameSpace + property + "> <" + prokinoNameSpace + res + "> }";
        String             queryString;
        List<String>       result = new ArrayList<String>();
        List<List<Object>> resources = null;
        String             resource;
        Resource           resNode = null;

        if( res == null ) {
            System.out.println( "Ontology.getObjectPropertyValue: null res" );
            return result;
        }

        if( property == null ) {
            System.out.println( "Ontology.getObjectPropertyValue: null property" );
            return result;
        }
        
        if( forward )
            queryString = fwdQueryString;
        else
            queryString = backQueryString;
        
        //System.out.println( "Ontology.getObjectPropertyValue: query: " + queryString );
        try {
            query = QueryFactory.create( queryString );
            qexec = VirtuosoQueryExecutionFactory.sparqlService( BrowserConfig.sparqlServiceURL, query);
            results = qexec.execSelect();
            
            while( results.hasNext() ) {

                QuerySolution solutionRow = results.nextSolution();

                if( forward )
                    resNode  = solutionRow.getResource( "?o" );
                else
                    resNode  = solutionRow.getResource( "?s" );

                if( resNode != null ) {
                    result.add( resNode.getLocalName() );
                }
            }
        }
        catch( QueryException qe ) {
            qe.printStackTrace( System.err );
        }
        finally {
            if( qexec != null )
                qexec.close();
        }

        return result;
    }
    
    // returns all Genes
    //
    public List<String> getAllGenes()
    {
        List<String> genes = new ArrayList<String>();
        ExtendedIterator geneIndivs = model.listIndividuals( geneRes );

        while( geneIndivs.hasNext() ) {
            genes.add( geneIndivs.next().toString() );
        }
        geneIndivs.close();
        return genes;
    }
    
    // returns a List with all Class individuals
    // returns null if className is not a known class
    //
    public List<ResourceNode> getAllClassIndivs( String className )
            throws ProKinOException
    {
        String queryString = "select ?res where { ?res <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <" + prokinoNameSpace + className + "> }";
        List<ResourceNode> indivs = null;
        Resource indiv = null;
        ResourceNode resNode = null;
        Resource type = null;

        if( className.equals( "Gene" ) )
            queryString = "select ?res (str(?name) as ?genename) where { ?res <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <" + prokinoNameSpace + className + "> . optional { ?res <" + prokinoNameSpace + "hasFullName> ?name } }";

        System.out.println( "getAllClassIndivs query: " + queryString );
        List<List<Object>> classIndivs = executeSPARQL( queryString, OutputType.RDFNODE, URIType.FULLURI );

        type = model.getResource( prokinoNameSpace + className );
        System.out.println( "getAllClassIndivs type: " + type );

        indivs = new ArrayList<ResourceNode>();

        for( List<Object> row : classIndivs ) {
            RDFNode node = (RDFNode) row.get( 0 );
            if( node.isResource() ) {
                indiv = (Resource) node;
                resNode = new ResourceNode( indiv );
                resNode.setDisplayString( getDisplayString( indiv, type ) );
                indivs.add( resNode );
            }
            if( className.equals( "Gene" ) )
                if( row.size() > 1 ) {
                    node = (RDFNode) row.get( 1 );
                    if( node != null && node.isLiteral() )
                        resNode.setInfoString( node.toString() );
                    else
                        resNode.setInfoString( "" );                }
                else
                    resNode.setInfoString( "" );
        }
        
        return indivs;
    }
    
    // returns all Class subclasses
    //
    public List<String> getAllClassSubClasses(String className)
            throws ProKinOException
    {
        List<String> indivs = new ArrayList<String>();
        OntClass classRes = null;

        classRes = model.getOntClass( nameSpace + className );

        if( classRes == null )
            throw new ProKinOException( "Unknown ProKinO class: " + className );

        ExtendedIterator classSubClasses = classRes.listSubClasses();

        while( classSubClasses.hasNext() ) {
            indivs.add( classSubClasses.next().toString() );
        }
        classSubClasses.close();
        return indivs;
    }
        
    // returns the entity with the given name
    //
    // name -- a local name, or a complete URI
    //
    public ProKinOResource getProKinOResource( String localName_or_URI, ProKinOResource currentResource )
    {
        Resource centerRes = null;
        String outDataPropsQuery = null;
        String outObjectPropsQuery = null;
        String intoPropsQuery = null;
        String literal;
        Resource property = null;
        Resource resource = null;
        OntResource resourceType = null;
        ResourceNode resNode = null;
        String uri = null;
        boolean trace = false;
        int i;

        if( currentResource == null ) {
            System.out.println( "ProKinO.getProKinOResource: null prokRes" );
            currentResource = new ProKinOResource();
        }

        if( localName_or_URI.indexOf( "http" ) != -1 && localName_or_URI.indexOf( "/" ) != -1 )
            uri = localName_or_URI;
        else
            uri = prokinoNameSpace + localName_or_URI;

        System.out.println( "ProKinO.getProKinOResource: getting center resource for: " + uri );
        centerRes = model.getResource( uri );
        if( centerRes == null ) {
            System.out.println( "ProKinO.getProKinOResource: stop (centerRes is null)" );
            return null;
        }
        
        outDataPropsQuery = "select ?p ?o from <" + prokinoGraphURI + "> where { <"  + uri + "> ?p ?o filter isLiteral(?o) }";
        outObjectPropsQuery = "select ?p ?o ?t from <" + prokinoGraphURI + "> where { <" + uri + "> ?p ?o filter (!isLiteral(?o)) . ?o <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?t }";
        intoPropsQuery = "select ?p ?s ?t from <" + prokinoGraphURI + "> where { ?s ?p <" + uri + "> . ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?t }";
        
        currentResource.init( uri, centerRes.getLocalName() );
        
        // collect all direct types of this entity
        //
        List<Resource> types = getRDFTypes( centerRes );
        if( trace )
            System.out.println( "getProKinOResource: types:" );
        for( Resource type : types ) {
            currentResource.addType( type );
            if( trace )
                System.out.println( "\t" + type.toString() );
        }

        // collect all forward data properties of this resource
        //
        if( trace )
            System.out.println( "getProKinOResource: forward properties: " );
        
        List<List<Object>> dataProps = executeSPARQL( outDataPropsQuery, OutputType.RDFNODE, URIType.FULLURI );

        i = 0;
        for( List<Object> row : dataProps ) {
            i++;
            RDFNode propNode = (RDFNode) row.get( 0 );
            RDFNode litNode = (RDFNode) row.get( 1 );
            property = (Resource) propNode;
            if( litNode.isLiteral() )
                literal = litNode.toString();
            else
                continue;
            
            currentResource.addDataProperty( property.getLocalName(), literal );

            if( trace )
                System.out.println( "\t" + property.getLocalName() + ": " + literal );
            
        }
        System.out.println( "Data props count: " + i );

        // collect all forward object properties of this resource
        //
        if( trace )
            System.out.println( "getProKinOResource: forward properties: " );

        List<List<Object>> objectProps = executeSPARQL( outObjectPropsQuery, OutputType.RDFNODE, URIType.FULLURI );

        i = 0;
        for( List<Object> row : objectProps ) {
            i++;
            RDFNode propNode = (RDFNode) row.get( 0 );
            RDFNode objectNode = (RDFNode) row.get( 1 );
            RDFNode typeNode = (RDFNode) row.get( 2 );
            property = (Resource) propNode;
            if( propExclusions.contains( property.toString() ) )
                continue;
            if( !objectNode.isResource() ) 
                continue;
            resource = (Resource) objectNode;
            resNode = new ResourceNode( resource );
            resourceType = model.getOntResource( resource );
            resNode.setResourceType( resourceType.getRDFType( true ) );
            resNode.setDisplayString( getDisplayString( resource, (Resource) typeNode ) );
            
            currentResource.addFwdProperty( property.getLocalName(), resNode );

            if( trace )
                System.out.println( "\t" + property.getLocalName() + ": " + resource.toString() );   
        }  
        System.out.println( "Object out props count: " + i );

        // collect all backward (incoming) properties of this resource
        //
        if( trace )
            System.out.println( "getProKinOResource: backward properties:" );

        List<List<Object>> backObjectProps = executeSPARQL( intoPropsQuery, OutputType.RDFNODE, URIType.FULLURI );

        i = 0;
        for( List<Object> row : backObjectProps ) {
            i++;
            RDFNode propNode = (RDFNode) row.get( 0 );
            RDFNode subjectNode = (RDFNode) row.get( 1 );
            RDFNode typeNode = (RDFNode) row.get( 2 );
            property = (Resource) propNode;
            if( propExclusions.contains( property.toString() ) )
                continue;
            if( !subjectNode.isResource() ) 
                continue;
            resource = (Resource) subjectNode;
            resNode = new ResourceNode( resource );
            resourceType = model.getOntResource( resource );          
            resNode.setResourceType( resourceType.getRDFType( true ) );
            resNode.setDisplayString( getDisplayString( resource, (Resource) typeNode ) );
            currentResource.addBckProperty( property.getLocalName(), resNode );

            if( trace )
                System.out.println( "\t" + property.getLocalName() + ": " + resource.toString() );  
        } 
        System.out.println( "Object in props count: " + i );

        return currentResource;
    }

    
    // returns the entire KinaseDomain hierarchy, including the individuals
    //
    public KinaseDomainNode getKinaseDomainHierarchy()
    {
        Resource rootResource;
        ResourceNode rootResourceNode;

        System.out.println( "ProKinO.getKinaseDomainHierarchy: 1" );
        System.out.flush();

        if( hierarchyRoot != null ) {
            System.out.println( "ProKinO.getKinaseDomainHierarchy: stop (reusing existing)" );
            return hierarchyRoot;
        }

        System.out.println( "ProKinO.getKinaseDomainHierarchy: creating the hierarchy" );

        rootResource = model.getOntResource( nameSpace + "ProteinKinaseDomain" );
        rootResource = model.getResource( nameSpace + "ProteinKinaseDomain" );
        System.out.println( "ProKinO.getKinaseDomainHierarchy: 1-2: " + rootResource );
        System.out.flush();
        rootResourceNode = new ResourceNode( rootResource );
        System.out.println( "ProKinO.getKinaseDomainHierarchy: 1-3" );
        System.out.flush();
        rootResourceNode.setDisplayString( rootResource.getLocalName() );

        System.out.println( "ProKinO.getKinaseDomainHierarchy: 2" );
        System.out.flush();

        // set the root node;  parent is null
        hierarchyRoot = new KinaseDomainNode( null, rootResourceNode, true ); // yes, it is a class node

        // install the hierarchy root in the domain map
        System.out.println( ">>> domainMap.put: " + rootResource.getLocalName() );
        domainMap.put( rootResource.getLocalName(), hierarchyRoot );

        System.out.println( "ProKinO.getKinaseDomainHierarchy: 3" );
        System.out.flush();

        expandKinaseDomainNode( hierarchyRoot );

        System.out.println( "ProKinO.getKinaseDomainHierarchy: 4" );
        System.out.flush();

        System.out.println( "ProKinO.getKinaseDomainHierarchy: indivNo: " +
                            hierarchyIndivNo );

        return hierarchyRoot;
    }

    public boolean isKinaseDomain( String name )
    {
        System.out.println( "Ontology.isKinaseDomain: " + name );
        return domainMap.containsKey( name );
    }

    public KinaseDomainNode getKinaseDomainNode( String name )
    {
        return domainMap.get( name );
    }

    public List<String> getGeneClassification( String geneURI )
    {
        return null;
    }
    
    public List<String> getDomainOrganisms( String domainName )
    {
        String             queryString = "select distinct ?organism where { ?gene <" + prokinoNameSpace + "hasFunctionalDomain> <" 
                                         + prokinoNameSpace + domainName + "> . ?gene <" + prokinoNameSpace + "presentIn> ?organism }";
        List<String>       result = new ArrayList<String>();
        List<List<Object>> resources = null;
        String             organism;

        if( domainName == null ) {
            System.out.println( "ProKinO.getDomainOrganisms: null domainName" );
            return result;
        }
        
        resources = executeSPARQL( queryString, OutputType.STRING, URIType.LOCALNAME );

        for( List<Object> row : resources )
            for( Object o : row ) {
                organism = (String) o;
                result.add( organism );
            }
        
        return result; 
    }

    // returns an even length list of pairs:  geneURI organismURI
    public List<String> getGeneOrganisms( String geneURI )
    {
        Query          query = null;
        QueryExecution qexec = null;
        ResultSet      results = null;
        List<String>   output = null;
        String         queryString = 
            "PREFIX     rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX prokino: <http://om.cs.uga.edu/prokino/2.0/#>\n" +
            "SELECT ?gene ?organism WHERE\n" +
            "{\n" +
            "<" + geneURI + "> prokino:hasPrimaryName  ?name .\n" +
            "?gene    rdf:type                prokino:Gene .\n" +
            "?gene    prokino:hasPrimaryName  ?name .\n" +
            "?gene    prokino:presentIn       ?organism .\n" +
            "}";

        System.out.println( "Query>>" + queryString + "<<\n" );

        output = new ArrayList<String>();

        try {
            query = QueryFactory.create( queryString );
            System.out.println( "Query>>" + query + "<<" );
            System.out.flush();

            qexec = VirtuosoQueryExecutionFactory.create( query, graph );

            results = qexec.execSelect();
            while( results.hasNext() ) {

                QuerySolution solutionRow = results.nextSolution();

                String geneName     = solutionRow.getResource( "?gene" ).getLocalName();
                String organismName = solutionRow.getResource( "?organism" ).getLocalName();

                output.add( geneName );
                output.add( organismName );
                System.out.println( "output.add: " + geneName + " in " + organismName );

            }
        }
        catch( QueryException qe ) {
            qe.printStackTrace( System.err );
        }
        finally {
            if( qexec != null )
                qexec.close();
        }

        return output;
    }

    // returns a list of pairs:  pathwayURI pathwayName
    public List<String> getGenePathways( String geneURI )
    {
        Query          query = null;
        QueryExecution qexec = null;
        ResultSet      results = null;
        List<String>   output = null;
        String queryString = 
            "PREFIX     rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX prokino: <http://om.cs.uga.edu/prokino/2.0/#>\n" +
            "SELECT  distinct ?parentPathway ?name WHERE\n" +
            "{\n" +
            "  ?container  prokino:includes+" + " <" + geneURI + "> .\n" +
            "  { { ?reaction  prokino:consumes          ?container . }\n" +
            "     UNION\n" +
            "    { ?reaction  prokino:produces          ?container . }\n" +
            "     UNION\n" +
            "    { ?reaction  prokino:hasCatalyst       ?container . } }\n" +
            "  ?reaction      rdf:type                  prokino:Reaction . \n" +
            "  ?pathway       prokino:hasReaction       ?reaction . \n" +
            "  ?pathway       prokino:hasParentPathway* ?parentPathway . \n" +
            "  ?parentPathway prokino:hasPrimaryName    ?name . \n" +
            "}\n" +
            "ORDER BY ?name";

        System.out.println( "Query>>" + queryString + "<<\n" );

        output = new ArrayList<String>();

        try {
            query = QueryFactory.create( queryString );
            System.out.println( "Query>>" + query + "<<" );
            System.out.flush();

            qexec = VirtuosoQueryExecutionFactory.create( query, graph );

            results = qexec.execSelect();
            searchResEntities.clear();
            while( results.hasNext() ) {

                QuerySolution solutionRow = results.nextSolution();

                String pathwayURI   = solutionRow.getResource( "?parentPathway" ).getLocalName();
                String pathwayName  = solutionRow.getLiteral( "?name" ).getString();

                output.add( pathwayURI );
                output.add( pathwayName );
                System.out.println( "output.add: " + pathwayURI + " in " + pathwayName );

            }
        }
        catch( QueryException qe ) {
            qe.printStackTrace( System.err );
        }
        finally {
            if( qexec != null )
                qexec.close();
        }

        return output;
    }

    // returns a list of pairs:  reactionURI reactionName
    public List<String> getGeneReactions( String geneURI )
    {
        Query          query = null;
        QueryExecution qexec = null;
        ResultSet      results = null;
        List<String>   output = null;
        String queryString = 
            "PREFIX     rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX prokino: <http://om.cs.uga.edu/prokino/2.0/#>\n" +
            "SELECT  distinct ?reaction ?name WHERE\n" +
            "{\n" +
            "  ?container  prokino:includes+" + " <" + geneURI + "> .\n" +
            "  { { ?reaction  prokino:consumes          ?container . }\n" +
            "     UNION\n" +
            "    { ?reaction  prokino:produces          ?container . }\n" +
            "     UNION\n" +
            "    { ?reaction  prokino:hasCatalyst       ?container . } }\n" +
            "  ?reaction      rdf:type                  prokino:Reaction . \n" +
            "  ?reaction   prokino:hasPrimaryName       ?name . \n" +
            "}\n" +
            "ORDER BY ?name";

        System.out.println( "Query>>" + queryString + "<<\n" );

        output = new ArrayList<String>();

        try {
            query = QueryFactory.create( queryString );
            System.out.println( "Query>>" + query + "<<" );
            System.out.flush();

            qexec = VirtuosoQueryExecutionFactory.create( query, graph );

            results = qexec.execSelect();
            searchResEntities.clear();
            while( results.hasNext() ) {

                QuerySolution solutionRow = results.nextSolution();

                String reactionURI  = solutionRow.getResource( "?reaction" ).getLocalName();
                String reactionName = solutionRow.getLiteral( "?name" ).getString();

                output.add( reactionURI );
                output.add( reactionName );
                System.out.println( "output.add: " + reactionURI + " in " + reactionName );

            }
        }
        catch( QueryException qe ) {
            qe.printStackTrace( System.err );
        }
        finally {
            if( qexec != null )
                qexec.close();
        }

        return output;
    }

    // returns a list of pairs:  complexURI complexName
    public List<String> getGeneComplexes( String geneURI )
    {
        Query          query = null;
        QueryExecution qexec = null;
        ResultSet      results = null;
        List<String>   output = null;
        String queryString = 
            "PREFIX     rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX prokino: <http://om.cs.uga.edu/prokino/2.0/#>\n" +
            "SELECT distinct ?container ?name WHERE\n" +
            "{\n" +
            "  ?container  prokino:includes+" + " <" + geneURI + "> .\n" +
            "  ?container  prokino:hasPrimaryName       ?name . \n" +
            "}\n" +
            "ORDER BY ?name";

        System.out.println( "Query>>" + queryString + "<<\n" );

        output = new ArrayList<String>();

        try {
            query = QueryFactory.create( queryString );
            System.out.println( "Query>>" + query + "<<" );
            System.out.flush();

            qexec = VirtuosoQueryExecutionFactory.create( query, graph );

            results = qexec.execSelect();
            searchResEntities.clear();
            while( results.hasNext() ) {

                QuerySolution solutionRow = results.nextSolution();

                String containerURI  = solutionRow.getResource( "?container" ).getLocalName();
                String containerName = solutionRow.getLiteral( "?name" ).getString();

                output.add( containerURI );
                output.add( containerName );
                System.out.println( "output.add: " + containerURI + " in " + containerName );

            }
        }
        catch( QueryException qe ) {
            qe.printStackTrace( System.err );
        }
        finally {
            if( qexec != null )
                qexec.close();
        }

        return output;
    }

    public List<SearchResult> executeSearch( String className, String queryTerms )
    {
        ResultSet          results = null;
        SearchResult       searchResult = null;
        List<SearchResult> output = null;
        List<SearchResult> humanOutput = null;
        List<SearchResult> mouseOutput = null;
        List<SearchResult> fruitFlyOutput = null;
        String             queryString = null;
        String             exerptWords = null;

        output = new ArrayList<SearchResult>();
        humanOutput = new ArrayList<SearchResult>();
        mouseOutput = new ArrayList<SearchResult>();
        fruitFlyOutput = new ArrayList<SearchResult>();
        
        exerptWords = getExerptWords( queryTerms );
  
        if( className.equalsIgnoreCase( "Gene" ) )
            queryString = "PREFIX prokino: <http://om.cs.uga.edu/prokino/2.0/#> " +
                          "PREFIX     rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                          "select distinct ?sc ?entity (bif:search_excerpt(bif:vector (" + exerptWords + "), ?lit)) as ?value " +
                          "from <http://prokino.uga.edu> " +
                          "where {" +
                          "    ?entity rdf:type     prokino:Gene ." +
                          "    ?entity ?prop        ?lit ." +
                          "    ?lit    bif:contains '" + queryTerms + "' option (score ?sc) " +
                          "} " +
                          "order by desc(?sc)";             
        else if( className.equalsIgnoreCase( "Cancer" ) )
            queryString = "PREFIX prokino: <http://om.cs.uga.edu/prokino/2.0/#> " +
                    "PREFIX     rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                    "select distinct ?sc ?entity (bif:search_excerpt(bif:vector (" + exerptWords + "), ?lit)) as ?value " +
                    "from <http://prokino.uga.edu> " +
                    "where {" +
                    "    ?entity rdf:type     prokino:Cancer ." +
                    "    ?entity ?prop        ?lit ." +
                    "    ?lit    bif:contains '" + queryTerms + "' option (score ?sc) " +
                    "} " +
                    "order by desc(?sc)";
        else if( className.equalsIgnoreCase( "Pathway" ) )
            queryString = "PREFIX prokino: <http://om.cs.uga.edu/prokino/2.0/#> " +
                    "PREFIX     rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                    "select distinct ?sc ?entity (bif:search_excerpt(bif:vector (" + exerptWords + "), ?lit)) as ?value " +
                    "from <http://prokino.uga.edu> " +
                    "where {" +
                    "    ?entity rdf:type     prokino:Pathway ." +
                    "    ?entity ?prop        ?lit ." +
                    "    ?lit    bif:contains '" + queryTerms + "' option (score ?sc) " +
                    "} " +
                    "order by desc(?sc)"; 
        else
            queryString = "PREFIX prokino: <http://om.cs.uga.edu/prokino/2.0/#> " +
                          "PREFIX     rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                          "select distinct ?sc ?entity (bif:search_excerpt(bif:vector (" + exerptWords + "), ?lit)) as ?value " +
                          "from <http://prokino.uga.edu> " +
                          "where {" +
                          "    ?entity ?prop        ?lit ." +
                          "    ?lit    bif:contains '" + queryTerms + "' option (score ?sc) " +
                          "} " +
                          "order by desc(?sc)"; 
        
        System.out.println( "executeSearch: Query>>" + queryString + "<<" );
        System.out.flush();

        QueryEngineHTTP qeHTTP = new QueryEngineHTTP( BrowserConfig.sparqlServiceURL, queryString );

        long lastClock;
        lastClock = System.currentTimeMillis();
        
        results = qeHTTP.execSelect();
        
        System.out.println( "executeSearch: Search query time: " + (System.currentTimeMillis()-lastClock) );
        
        searchResEntities.clear();
        
        lastClock = System.currentTimeMillis();

        int count = 0;
        int totCount = 0;
        while( results.hasNext() ) {
            totCount++;
            QuerySolution solutionRow = results.nextSolution();

            String entityType = getResourceTypeName( solutionRow.getResource( "?entity" ) );
            String entityName = solutionRow.getResource( "?entity" ).getLocalName();
            String literal    = solutionRow.getLiteral( "?value" ).getString();
            String score      = solutionRow.getLiteral( "?sc" ).getString();

            if( className.equals( "All" ) || className.equals( entityType ) ) {

                if( !searchResEntities.contains( entityName ) ) {

                    count++;

                    if( count > 1500 )
                        break;

                    searchResult = new SearchResult( entityName, literal, score, entityType );
                    searchResEntities.add( entityName );
                    
                    // now, collect results by Organism, but only if searching for genes
                    if( className.equals( "Gene" ) &&
                        searchResult.getOrganism() == SearchResult.HUMAN )
                        humanOutput.add( searchResult );
                    else if( className.equals( "Gene" ) &&
                             searchResult.getOrganism() == SearchResult.MOUSE )
                        mouseOutput.add( searchResult );
                    else if( className.equals( "Gene" ) &&
                             searchResult.getOrganism() == SearchResult.FRUITFLY )
                        fruitFlyOutput.add( searchResult );
                    else
                        output.add( searchResult );

                }
            }
        }
        
        System.out.println( "executeSearch: proc " + count + "(of " + totCount + ") results time: " + (System.currentTimeMillis()-lastClock) );

        if( className.equals( "Gene" ) ) {
            
            lastClock = System.currentTimeMillis();

            output.addAll( 0, fruitFlyOutput );
            output.addAll( 0, mouseOutput );
            output.addAll( 0, humanOutput );
            Collections.sort( output, new SearchResultComparator() );

            System.out.println( "executeSearch: proc Gene results time: " + (System.currentTimeMillis()-lastClock) );

        }

        if( qeHTTP != null )
            qeHTTP.close();

        return output;
    }
    
    private String getExerptWords(String queryTerms)
    {
        String[] tokens = queryTerms.split("\\s+");
        StringBuffer words = new StringBuffer( WORDSINITCAPACITY );
        
        words.setLength( 0 );
        
        for( String tok : tokens ) 
            if( !tok.equalsIgnoreCase( "and" ) &&
                !tok.equalsIgnoreCase( "or" ) &&
                !tok.equalsIgnoreCase( "not" ) ) {
                if( tok.endsWith( "*" ) )
                    tok = tok.substring( 0, tok.length() - 1 );
                if( tok.startsWith( "*" ) )
                    tok = tok.substring( 1 );
                if( tok.length() > 0 ) {
                    if( words.length() > 0 )
                        words.append( ", " );
                    words.append( "'" + tok + "'" );
                }
            }
        
        return words.toString();
    }

    public String executeSPARQLQuery( String queryString )
    {
        QueryEngineHTTP        qeHTTP = null;
        ResultSet              results = null;
        String                 output = null;
        ByteArrayOutputStream  baos = new ByteArrayOutputStream();

        try {

            System.out.println( "Creating Query: >>" + queryString + "<<" );
            System.out.flush();

            String queryStringLC = queryString.toLowerCase();
            
            qeHTTP = new QueryEngineHTTP( BrowserConfig.sparqlServiceURL, queryString );

            if( !(queryStringLC.contains( "update" ) 
                    || queryStringLC.contains( "delete" )
                    || queryStringLC.contains( "construct" )
                    || queryStringLC.contains( "describe" )
                    || queryStringLC.contains( "ask" )) ) {
                
                results = qeHTTP.execSelect();

                ResultSetFormatter.out( baos, results, prefixMapping );
                output = baos.toString();
            }
            else
                output = "Quries involving update, delete, ask, and describe are not allowed";

        }
        catch( QueryException qe ) {
            if( qe.toString().startsWith( ParseExceptionClass ) ) {
                output = "Query error" + qe.toString().substring( ParseExceptionClass.length() );
                qe.printStackTrace();
            }
            else {
                output = qe.toString();
            }
        }
        finally {
            if( qeHTTP != null )
                qeHTTP.close();
        }
        return output;
    }
    
    public String executeSPARQLQuery( OutputStream outStream, String queryString )
    {
        QueryEngineHTTP        qeHTTP = null;
        ResultSet              results = null;
        String                 output = "OK";

        try {

            System.out.println( "Creating Query to stream: >>" + queryString + "<<" );
            System.out.flush();
            
            qeHTTP = new QueryEngineHTTP( BrowserConfig.sparqlServiceURL, queryString );
            String queryStringLC = queryString.toLowerCase();

            if( !(queryStringLC.contains( "update" ) 
                    || queryStringLC.contains( "delete" )
                    || queryStringLC.contains( "construct" )
                    || queryStringLC.contains( "describe" )
                    || queryStringLC.contains( "ask" )) ) {
                results = qeHTTP.execSelect();
                ResultSetFormatter.outputAsCSV( outStream, results );
            }
            else
                output = "Quries involving update, delete, ask, and describe are not allowed";

        }
        catch( QueryException qe ) {
            if( qe.toString().startsWith( ParseExceptionClass ) ) {
                output = "Query error" + qe.toString().substring( ParseExceptionClass.length() );
                qe.printStackTrace();
            }
            else {
                output = qe.toString();
            }
        }
        finally {
            if( qeHTTP != null )
                qeHTTP.close();
        }
        return output;
    }
    
    public String executeQuery( String queryString )
    {
        Query                  query = null;
        QueryExecution         qexec = null;
        ResultSet              results = null;
        String                 output = null;
        PrefixMapping          prefixMap = null;
        ByteArrayOutputStream  baos = new ByteArrayOutputStream();

        try {

            System.out.println( "Creating Query: >>" + queryString + "<<" );
            System.out.flush();
            
            String queryStringLC = queryString.toLowerCase();
            qexec = VirtuosoQueryExecutionFactory.sparqlService( BrowserConfig.sparqlServiceURL, query);

            if( !(queryStringLC.contains( "update" ) 
                    || queryStringLC.contains( "delete" )
                    || queryStringLC.contains( "construct" )
                    || queryStringLC.contains( "describe" )
                    || queryStringLC.contains( "ask" )) ) {
                
                results = qexec.execSelect();
                
                ResultSetFormatter.out( baos, results, prefixMap );
                output = baos.toString();
            }
            else
                output = "Quries involving update, delete, ask, and describe are not allowed";

        }
        catch( QueryException qe ) {
            if( qe.toString().startsWith( ParseExceptionClass ) ) {
                output = "Query error" + qe.toString().substring( ParseExceptionClass.length() );
                qe.printStackTrace();
            }
            else {
                output = qe.toString();
            }
        }
        finally {
            if( qexec != null )
                qexec.close();
        }
        return output;
    }

    private void expandKinaseDomainNode(KinaseDomainNode node)
    {
        KinaseDomainNode child = null;
        ResourceNode rNode = null;
        Resource resource = null;
        //ExtendedIterator classSubClasses = null;
        //ExtendedIterator classInstances = null;

        if( node.isClass() ) {

            resource = node.getResourceNode().getResource();
            
            ResIterator iter = model.listSubjectsWithProperty( subClassOfPro, resource );
            int subClassNo = 0;
            while( iter.hasNext() ) {
                
                subClassNo++;
                Resource childClass = iter.nextResource();
                System.out.println( "subclass: " + childClass );

                rNode = new ResourceNode( childClass );
                rNode.setDisplayString( childClass.getLocalName() );

                // create a new KinaseDomainNode
                child = new KinaseDomainNode( node, rNode, true ); // yes, a class

                // install the child node in the domain map
                System.out.println( ">>> domainMap.put: " + childClass.getLocalName() );
                domainMap.put( childClass.getLocalName(), child );

                // add this child to the current KinaseDomainNode node
                node.addChild( child );

                // and expand this child further
                expandKinaseDomainNode( child );
            }
            iter.close();

            // check if any subclasses were obtained
            if( subClassNo == 0 ) {

                // if not, it means we need to get all individuals of a leaf class in the kinase hierarchy
                iter = model.listSubjectsWithProperty( typePro, resource );

                while( iter.hasNext() ) {

                    String primaryName = null;

                    hierarchyIndivNo++;

                    // get the next subclass
                    Resource childInstance = iter.nextResource();

                    rNode = new ResourceNode( childInstance );
                    primaryName = getPropertyValue( childInstance, "hasPrimaryName" );
                    rNode.setDisplayString( primaryName );
                    
                    // create a new KinaseDomainNode
                    child = new KinaseDomainNode( node, rNode, false ); // not a class
                    List<String> organisms = getObjectPropertyValue( rNode.getResource().getLocalName(), 
                                                                     "presentIn", 
                                                                     Browser.forward );
                    if( organisms.size() > 0 )
                        child.setOrganism( organisms.get( 0 ) ); // there should be only one organism per kinase domain

                    // install the child node in the domain map
                    System.out.println( ">>> domainMap.put (indiv): " + childInstance.getLocalName() );
                    domainMap.put( childInstance.getLocalName(), child );

                    // add this child to the current KinaseDomainNode node
                    node.addChild( child );

                }
                iter.close();

            }
        }
    }
    
    // reconnect to the ProKinO server and reinitialize the browser
    public String reconnect()
    {
        if( vModel != null ) {
            vModel.close();
        }

        // invalidate version and date
        prokinoVersion = null;
        prokinoDate = null;
        
        // invalidate the page cache
        resourceCache.invalidate();
        
        init(true);
        
        if( vModel != null )
            return "Connected to " + BrowserConfig.ontologyServerGraph + " at " + BrowserConfig.ontologyServerURL;
        else
            return "Connection failed";
    }
    
}
