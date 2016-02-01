package edu.uga.prokino.browser;

import java.util.List;
import java.util.ListIterator;

public class BrowserUtility
{
    public static String formatFASTASequence(String sequence, int lineLength)
    {
        StringBuffer formattedSequence = new StringBuffer( sequence );
        int pos;
        int pos2;

        pos = lineLength;
        pos2 = lineLength;

        while( pos < sequence.length() ) {
            formattedSequence.insert( pos2, "<br>" );
            pos2 += lineLength + 4;
            pos += lineLength;
        }

        formattedSequence.insert( 0, "<tt>" );
        formattedSequence.append( "</tt>" );

        return formattedSequence.toString();
    }

    public static void cleanLiteralValues(List<String> literals)
    {
        String literal = null;

        for( ListIterator<String> litIter = literals.listIterator(); litIter.hasNext(); ) {
            literal = litIter.next();
            litIter.set( cleanLiteralValue( literal ) );
        }
    }

    public static String cleanLiteralValue(String literal)
    {
        int pos;

        pos = literal.indexOf( "^^http" );
        if( pos != -1 )
            literal = literal.substring( 0, pos );

        pos = literal.indexOf( "@en" );
        if( pos != -1 )
            literal = literal.substring( 0, pos );

        return literal;
    }

}
