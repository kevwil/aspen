package com.github.kevwil.aspen;

import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.runtime.builtin.IRubyObject;

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
    public static final String ASPEN = versionString();
    public static final RubyArray RACK = createVersionArray();

    private Version(){}

    private static RubyArray createVersionArray()
    {
        RubyArray array = RubyArray.newArray( Ruby.getGlobalRuntime() );
        array.add( 1 );
        array.add( 1 );
        return array;
    }

    private static String versionString()
    {
        return join( Arrays.asList( Integer.toString( MAJOR ),
                                    Integer.toString( MINOR ),
                                    Integer.toString( TINY ) ),
                     "." );
    }

    private static String join( Collection<String> s, String delimiter )
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
