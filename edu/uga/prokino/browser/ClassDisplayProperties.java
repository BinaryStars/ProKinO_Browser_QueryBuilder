package edu.uga.prokino.browser;

import java.util.ArrayList;
import java.util.List;


// this class represents data and object properties that
// should be displayed in the browser for a given ProKinO class
//
// instance variables and methods are self-explanatory
//
/**
 * @author Krys J. Kochut
 *
 * This class represents data and object properties that
 * should be displayed in the browser for a given ProKinO class.
 * 
 * Instance variables and methods are self-explanatory.
 */
public class ClassDisplayProperties
{
    private String         className        = null;
    private List<String> dataProperties   = null;
    private List<String> objectProperties = null;

    public ClassDisplayProperties(String cname)
    {
        className = cname;
        dataProperties = new ArrayList<String>();
        objectProperties = new ArrayList<String>();
    }

    public String getClassName()
    {
        return className;
    }

    List<String> getDataProperties()
    {
        return dataProperties;
    }

    public void addDataProperty(String prop)
    {
        dataProperties.add( prop );
    }

    List<String> getObjectProperties()
    {
        return objectProperties;
    }

    public void addObjectProperty(String prop)
    {
        objectProperties.add( prop );
    }

}
