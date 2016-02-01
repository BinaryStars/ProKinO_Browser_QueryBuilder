package edu.uga.prokino.browser;


import java.util.Comparator;


/**
 * @author Krys J. Kochut
 *
 * * This class is a comparator of Motifs.
 *
 * if the startPosition of the motifs is -1, these are StrcturalMotifs, which
 *   should be compared by name
 * otherwise, compare by startPosition
 */
public class MotifComparator 
    implements Comparator<Motif>
{
    public int compare(Motif m1, Motif m2)
    {
	if( m1.getStartPosition() == -1 && m2.getStartPosition() == -1 )
	    return m1.getName().compareTo( m2.getName() );
	else
	    return m1.getStartPosition() - m2.getStartPosition();
    }
}
