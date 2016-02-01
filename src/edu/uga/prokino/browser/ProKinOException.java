package edu.uga.prokino.browser;


/**
 * @author Krys J. Kochut
 *
 * A "generic" ProKinOExcpetion class.
 */
public class ProKinOException extends Exception
{
    private static final long serialVersionUID = 1L;

    public ProKinOException(String msg)
    {
        super( msg );
    }

    public ProKinOException(Throwable cause)
    {
        super( cause );
    }

}
