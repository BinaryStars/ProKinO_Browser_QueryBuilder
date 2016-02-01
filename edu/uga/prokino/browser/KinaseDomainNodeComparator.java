package edu.uga.prokino.browser;
import java.util.Comparator;


/**
 * @author Krys J. Kochut
 *
 * * This class is a comparator of KinaseDomainNodes
 */
public class KinaseDomainNodeComparator implements Comparator<KinaseDomainNode>
{
    public int compare(KinaseDomainNode n1, KinaseDomainNode n2)
    {
        return n1.getResourceNode().getDisplayString().compareTo( n2.getResourceNode().getDisplayString() );
    }
}
