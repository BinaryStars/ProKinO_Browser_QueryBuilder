package edu.uga.prokino.browser;

import java.util.ArrayList;
import java.util.List;



/**
 * @author kochut
 *
 * This class represents a single node in the kinome tree.
 * The parent node and children are represented.
 */
public class KinaseDomainNode
{
    private ResourceNode     resourceNode    = null;
    private boolean          resourceIsClass = false;
    KinaseDomainNode         parentNode      = null;
    List<KinaseDomainNode>   children        = null;
    private String           organism        = null;

    /**
     * Create a KinaseDomainNode object instance.
     * @param parent parent node;  null if this is the root node
     * @param rNode a resource (Jena) representing the domain node 
     * @param cls true iff this node has children
     */
    public KinaseDomainNode(KinaseDomainNode parent, ResourceNode rNode, boolean cls)
    {
	parentNode   = parent;
        resourceNode = rNode;
        resourceIsClass = cls;
        if( cls )
            children = new ArrayList<KinaseDomainNode>();
        organism = null;
    }

    public KinaseDomainNode getParent()
    {
	return parentNode;
    }

    public ResourceNode getResourceNode()
    {
        return resourceNode;
    }

    public void setResourceNode(ResourceNode rNode)
    {
        resourceNode = rNode;
    }

    public boolean isClass()
    {
        return resourceIsClass;
    }

    public void addChild(KinaseDomainNode kNode)
    {
        children.add( kNode );
    }

    public List<KinaseDomainNode> getChildren()
    {
        return children;
    }

    public String getOrganism()
    {
        return organism;
    }

    public void setOrganism(String organism)
    {
        this.organism = organism;
    }
}
