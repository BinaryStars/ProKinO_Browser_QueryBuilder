package edu.uga.prokino.browser;



/**
 * @author Krys J. Kochut
 *
 * This class represents a subdomain.  It stores a ResourceNode representing the
 * subdomain, its printable name, and its starting position.
 */
public class SubDomain {

    private ResourceNode subDomain;
    private String       name;
    private int          startPosition;

    public SubDomain( ResourceNode subDom, String nm,int startPos )
    {
	subDomain = subDom;
	name = nm;
	startPosition = startPos;
    }

    public ResourceNode getSubDomain()
    {
	return subDomain;
    }

    public String getName()
    {
	return name;
    }

    public int getStartPosition()
    {
	return startPosition;
    }
}
