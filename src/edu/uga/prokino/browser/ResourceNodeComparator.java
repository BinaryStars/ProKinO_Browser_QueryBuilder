package edu.uga.prokino.browser;
import java.util.Comparator;


/**
 * @author Krys J. Kochut
 * 
 * This class is a Comparator of ResourceNodes.
 *
 */
public class ResourceNodeComparator implements Comparator<ResourceNode>
{
    public int compare(ResourceNode n1, ResourceNode n2)
    {
        if( n1.getDisplayString() == null )
            if( n2.getDisplayString() == null )
                return 0;
            else
                return -1;
        else if( n2.getDisplayString() == null )
            return 1;
        else
            return n1.getDisplayString().compareTo( n2.getDisplayString() );
    }
}
