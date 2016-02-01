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
public class SearchResultComparator 
    implements Comparator<SearchResult>
{
    public int compare(SearchResult r1, SearchResult r2)
    {
	if( r1.getScore() < r2.getScore() )
	    return 1;
	else if( r1.getScore() > r2.getScore() )
	    return -1;
	else {
	    if( r1.getOrganism() == SearchResult.HUMAN ) {
		if( r2.getOrganism() == SearchResult.HUMAN )
		    return 0;
		else
		    return -1;
	    }
	    else if( r2.getOrganism() == SearchResult.HUMAN )
                return 1;
	    else if( r1.getOrganism() == SearchResult.MOUSE ) {
		if( r2.getOrganism() == SearchResult.MOUSE )
		    return 0;
		else
		    return -1;
	    }
	    else if( r2.getOrganism() == SearchResult.MOUSE )
                return 1;
	    else if( r1.getOrganism() == SearchResult.FRUITFLY ) {
		if( r2.getOrganism() == SearchResult.FRUITFLY )
		    return 0;
		else
		    return -1;
	    }
	    else if( r2.getOrganism() == SearchResult.FRUITFLY )
                return 1;
	    else
		return 0;
	}
    }
}
