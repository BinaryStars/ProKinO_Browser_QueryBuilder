package edu.uga.prokino.browser;

import java.util.HashMap;


// singleton class for storing generated browser pages for reuse
//
public class PageCache
{
    private HashMap<String, String> resourceCache  = null;
    private static PageCache instance = null;
    
    private PageCache()
    {
        resourceCache = new HashMap<String, String>();
    }
    
    public static PageCache getPageCache()
    {
        if( instance == null ) {
            instance = new PageCache();
            System.out.println( "PageCache.getPageCache: page cache created" );
        }
        return instance;
    }

    public HashMap<String, String> getResourceCache()
    {
        return resourceCache;
    }

    public void setResourceCache(HashMap<String, String> resourceCache)
    {
        this.resourceCache = resourceCache;
    }
    
    public void put( String key, String value )
    {
        resourceCache.put( key, value );
    }
    
    public String get( String key )
    {
        return resourceCache.get( key );
    }
    
    public boolean contains( String key )
    {
        return resourceCache.containsKey( key );
    }
    
    public int size()
    {
        return resourceCache.size();
    }
    
    public void invalidate()
    {
        resourceCache.clear();
    }
}
