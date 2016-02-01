package edu.uga.prokino.browser;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Resource;




/**
 * @author Krys J. Kochut
 *
 * This class represents a single ProKinO resource
 * It represents the local name, uri, and
 *     all data properties (vectors of literals),
 *     all direct types of the resource (vector of type Resources),
 *     all forward properties (vectors of ResourceNodes, i.e. resource/display pairs), and
 *     all backward properties (vectors of ResourceNodes).
 *
 */
public class ProKinOResource
{

    private String                          uri       = null;
    private String                          localName = null;

    // a vector of direct types (classes) of this resource
    //
    private List<Resource>                types     = null;

    // map of data properties
    //
    // maps a property name (String) to a vector of literals (Strings),
    // i.e. all literal values of the data properties, possibly just one
    //
    private HashMap<String, List<String>> dataProps = null;

    // map of object properties (forward neighbors)
    //
    // maps a property name (String) to a vector of ResourceNodes
    //
    HashMap<String, List<ResourceNode>>   fwdProps  = null;

    // map of object properties (backward neighbors)
    //
    // maps a property name (String) to a vector of ResourceNodes
    //
    HashMap<String, List<ResourceNode>>   bckProps  = null;

    public ProKinOResource()
    {
        uri = "Unknown URI";
        localName = "Unknown local name";
        types = new ArrayList<Resource>();
        dataProps = new HashMap<String, List<String>>();
        fwdProps = new HashMap<String, List<ResourceNode>>();
        bckProps = new HashMap<String, List<ResourceNode>>();
    }

    public ProKinOResource(String resURI, String lname)
    {
        uri = resURI;
        localName = lname;
        types = new ArrayList<Resource>();
        dataProps = new HashMap<String, List<String>>();
        fwdProps = new HashMap<String, List<ResourceNode>>();
        bckProps = new HashMap<String, List<ResourceNode>>();
    }

    public void init(String resURI, String lname)
    {
        uri = resURI;
        localName = lname;
        types.clear();
        dataProps.clear();
        fwdProps.clear();
        bckProps.clear();
    }

    String getLocalName()
    {
        return localName;
    }

    void setLocalName(String lname)
    {
        localName = lname;
    }

    String getURI()
    {
        return uri;
    }

    void setURI(String uri)
    {
        this.uri = uri;
    }

    List<Resource> getTypes()
    {
        return types;
    }

    // The first type is returned
    //
    Resource getType()
    {
        return types.get( 0 );
    }

    boolean isOfType(String typeName)
    {
        for( Resource thisType : types ) {

            // check for a local name, or possibly a complete URI
            //
            if( typeName.equals( thisType.getLocalName() ) ||
                typeName.equals( thisType.toString() ) )
                return true;

        }

        return false;
    }

    boolean isOfType(Resource typeResource)
    {
        for( Resource thisType : types )
            if( typeResource == thisType )
                return true;

        return false;
    }

    boolean isOfType(ResourceNode typeResource)
    {
        for( Resource thisType : types )
            if( typeResource.getResource() == thisType )
                return true;

        return false;
    }

    HashMap<String, List<String>> getDataProperties()
    {
        return dataProps;
    }

    List<String> getDataProperty(String property)
    {
        return dataProps.get( property );
    }

    List<ResourceNode> getFwdProperty(String property)
    {
        return fwdProps.get( property );
    }

    HashMap<String, List<ResourceNode>> getFwdProperties()
    {
        return fwdProps;
    }

    List<ResourceNode> getBckProperty(String property)
    {
        return bckProps.get( property );
    }

    HashMap<String, List<ResourceNode>> getBckProperties()
    {
        return bckProps;
    }

    void addType(Resource typeResource)
    {
        types.add( typeResource );
    }

    void addDataProperty(String property, String literal)
    {
        List<String> literals = null;

        literals = dataProps.get( property );
        if( literals == null )
            literals = new ArrayList<String>();
        literals.add( literal );
        dataProps.put( property, literals );
    }

    void addFwdProperty(String property, ResourceNode resource)
    {
        List<ResourceNode> objects = null;

        objects = fwdProps.get( property );
        if( objects == null ) {
            objects = new ArrayList<ResourceNode>();
            fwdProps.put( property, objects );
        }
        objects.add( resource );
    }

    void addBckProperty(String property, ResourceNode resource)
    {
        List<ResourceNode> subjects = null;

        subjects = bckProps.get( property );
        if( subjects == null ) {
            subjects = new ArrayList<ResourceNode>();
            bckProps.put( property, subjects );
        }
        subjects.add( resource );
    }

}
