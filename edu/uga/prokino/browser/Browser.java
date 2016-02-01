package edu.uga.prokino.browser;

import java.io.OutputStream;
import java.util.*;

import com.hp.hpl.jena.rdf.model.*;


/**
 * @author Krys J. Kochut
 *
 * This class represents the ProKinO browser and its state, that is
 * the current resource which is the point of focus.
 * A variety of methods are available for manipulating the ontology
 * being browsed.
 * 
 * This class interacts with the ProKinO class, which represents the
 * data in the ontology and provides a variety of methods to access
 * the data.
 */
public class Browser
{

    private Ontology            prokino             = null;
    private Boolean             verbose             = false;
    private Set<String>         propExclusions      = null;

    // the current resource; point of focus
    //
    private ProKinOResource     currentResource     = null;

    public static final Boolean forward             = true;
    public static final Boolean backward            = false;

    public Browser(boolean v)
    {
        setVerbose( v );
        prokino = Ontology.getOntology();
        currentResource = new ProKinOResource();
        propExclusions = new HashSet<String>();

        addPropExclusion( "http://www.w3.org/1999/02/22-rdf-syntax-ns#type" );
        addPropExclusion( "http://om.cs.uga.edu/~gosal/Prokino.owl#foundIn" );

        addPropExclusion( "http://om.cs.uga.edu/~gosal/Prokino.owl#correlatesTo" );
        addPropExclusion( "http://om.cs.uga.edu/~gosal/Prokino.owl#hasFunctionalRelationship" );
        addPropExclusion( "http://om.cs.uga.edu/~gosal/Prokino.owl#occursIn" );
    }

    public void addPropExclusion(String property)
    {
        propExclusions.add( property );
    }

    public void delPropExclusion(String property)
    {
        propExclusions.remove( property );
    }

    public String getVersionInfo()
    {
        return prokino.getVersionInfo();
    }

    public String getDate()
    {
        return prokino.getDate();
    }

    public ProKinOResource getCurrentResource()
    {
        return currentResource;
    }

    public void setCurrentResource(String localName_or_URI)
    {
        currentResource = prokino.getProKinOResource( localName_or_URI,
                currentResource );
    }

    public ProKinOResource getProKinOResource(String localName_or_URI)
    {
        currentResource = prokino.getProKinOResource( localName_or_URI, currentResource );
        return currentResource;
    }

    public List<SearchResult> executeSearch( String className, String queryString )
    {
	return prokino.executeSearch( className, queryString );
    }

    public String executeSPARQLQuery( String queryString )
    {
	return prokino.executeSPARQLQuery( queryString );
    }

    public String executeSPARQLQuery( OutputStream outStream, String queryString )
    {
	return prokino.executeSPARQLQuery(  outStream, queryString );
    }

    public String getResourceTypeName(Resource resource)
    {
        return prokino.getResourceTypeName( resource );
    }
    
    public String getPropertyValue(String resourceName, String propertyName)
    {
        return prokino.getPropertyValue( resourceName, propertyName );
    }

    public String getPropertyValue(Resource resource, String propertyName)
    {
        return prokino.getPropertyValue( resource, propertyName );
    }

    public List<String> getObjectPropertyValue(String res, String property, boolean forward )
    {
	return prokino.getObjectPropertyValue( res, property, forward );
    }

    public List<ResourceNode> getAllClassIndivs(String className)
            throws ProKinOException
    {
        return prokino.getAllClassIndivs( className );
    }

    public KinaseDomainNode getKinaseDomainHierarchy()
    {
        return prokino.getKinaseDomainHierarchy();
    }

    public boolean isKinaseDomain( String name )
    {
        return prokino.isKinaseDomain( name );
    }

    public KinaseDomainNode getKinaseDomainNode( String name )
    {
        return prokino.getKinaseDomainNode( name );
    }
    
    public List<String> getDomainOrganisms( String domainName )
    {
        return prokino.getDomainOrganisms( domainName );
    }

    public List<String> getGeneOrganisms( String geneURI )
    {
	return prokino.getGeneOrganisms( geneURI );
    }

    public List<String> getGenePathways( String geneURI )
    {
	return prokino.getGenePathways( geneURI );
    }

    public List<String> getGeneReactions( String geneURI )
    {
	return prokino.getGeneReactions( geneURI );
    }

    public List<String> getGeneComplexes( String geneURI )
    {
	return prokino.getGeneComplexes( geneURI );
    }

    public void setVerbose(Boolean verbose)
    {
        this.verbose = verbose;
    }

    public Boolean getVerbose()
    {
        return verbose;
    }
    
    public String reconnect()
    {
        return prokino.reconnect();
    }

}
