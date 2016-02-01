package edu.uga.prokino.browser;


import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author Krys J. Kochut
 *
 * This class represents a single Resource (Jena type) and its type (OWL class)
 * and a string to be used as a display information.
 */
public class ResourceNode
{
    private Resource resource      = null;
    private Resource resourceType  = null;
    private String   displayString = null;
    private String   infoString    = null;

    public ResourceNode(Resource res)
    {
        resource = res;
        displayString = null;
    }

    public ResourceNode(Resource res, String display)
    {
        resource = res;
        displayString = display;
    }

    public ResourceNode(Resource res, Resource typ, String display)
    {
        resource = res;
        resourceType = typ;
        displayString = display;
    }

    public Resource getResource()
    {
        return resource;
    }

    public Resource getResourceType()
    {
        return resourceType;
    }

    public String getDisplayString()
    {
        return displayString;
    }

    public void setResourceType(Resource typ)
    {
        resourceType = typ;
    }

    public void setDisplayString(String display)
    {
        displayString = display;
    }

    public String getInfoString()
    {
        return infoString;
    }

    public void setInfoString(String infoString)
    {
        this.infoString = infoString;
    }
}
