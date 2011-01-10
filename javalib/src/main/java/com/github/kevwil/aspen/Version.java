package com.github.kevwil.aspen;

import java.util.*;

/**
 * @author kevwil
 * @since Jan 10, 2011
 */
public class Version
{
    public static final int MAJOR = 1;
    public static final int MINOR = 0;
    public static final int TINY = 0;
    public static final int[] RACK = {1,1};

    @Override
    public String toString()
    {
        return join( Arrays.asList( Integer.toString( MAJOR ),
                                    Integer.toString( MINOR ),
                                    Integer.toString( TINY ) ),
                     "." );
    }

    private String join( Collection<String> s, String delimiter )
    {
        Iterator<String> iterator = s.iterator();
        StringBuffer buffer = new StringBuffer( iterator.next() );
        while( iterator.hasNext() )
        {
            buffer.append( delimiter ).append( iterator.next() );
        }
        return buffer.toString();
    }
}
