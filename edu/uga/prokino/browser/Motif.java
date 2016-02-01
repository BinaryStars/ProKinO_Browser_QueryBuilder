package edu.uga.prokino.browser;



/**
 * @author Krys J. Kochut
 *
 * This class represents a motif.  It stores a ResourceNode representing the
 * motif and its printable name.  For a sequence motif, its starting position
 * is represented, as well.  For structural motif, the starting position is -1.
 */
public class Motif {

    private ResourceNode motif;
    private String       name;
    private int          startPosition;

    public Motif( ResourceNode mot, String nm, int startPos )
    {
	motif = mot;
	name = nm;
	startPosition = startPos;
    }

    public boolean isSequenceMotif()
    {
	return startPosition >= 0;
    }

    public ResourceNode getMotif()
    {
	return motif;
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
