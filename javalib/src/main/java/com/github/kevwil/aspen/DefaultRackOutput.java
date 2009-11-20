package com.github.kevwil.aspen;

import org.jruby.*;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

import java.io.PrintStream;

/**
 * @author kevwil
 * @since Nov 20, 2009
 */
public class DefaultRackOutput implements RackOutput
{
    private Ruby _runtime;
    private PrintStream _errors;

    private Ruby getRuntime()
    {
        return _runtime;
    }

    public DefaultRackOutput( final RubyHash env )
    {
        _runtime = env.getRuntime();
        _errors = getRuntime().getErrorStream();
    }

    public IRubyObject puts( final ThreadContext context, final IRubyObject data )
    {
        if( data.respondsTo( "to_s" ) )
        {
            _errors.append( data.asString().asJavaString() );
        }
        else
        {
            _errors.append( "given data does not respond to 'to_s'" );
        }
        return getRuntime().getNil();
    }

    public IRubyObject write( final ThreadContext context, final RubyString data )
    {
        _errors.append( data.asJavaString() );
        return getRuntime().getNil();
    }

    public IRubyObject flush( final ThreadContext context )
    {
        _errors.flush();
        return getRuntime().getNil();
    }

    public void close()
    {
    }
}
