package edu.uga.prokino.browser;


public class SearchResult
{
    public final static int HUMAN    = 1;
    public final static int MOUSE    = 2;
    public final static int FRUITFLY = 3;
    public final static int OTHER    = 4;

    private String entityName;
    private String literal;
    private float  score;
    private String entityType;
    private int    organism;

    public SearchResult( String entityName, String literal, String score, String entityType )
    {
	this.entityName = entityName;
	this.literal    = literal;
	this.score      = Float.parseFloat( score );
	this.entityType = entityType;
	
	if( entityName.startsWith( "Human" ) )
	    organism = HUMAN;
	else if( entityName.startsWith( "Mouse" ) )
	    organism = MOUSE;
	else if( entityName.startsWith( "FruitFly" ) )
	    organism = FRUITFLY;
	else
	    organism = OTHER;
    }

    public String getEntityName()
    {
	return entityName;
    }

    public String getLiteral()
    {
	return literal;
    }

    public float getScore()
    {
	return score;
    }

    public String getEntityType()
    {
	return entityType;
    }

    public int getOrganism()
    {
	return organism;
    }

    public void setOrganism(int organism)
    {
        this.organism = organism;
    }
}
