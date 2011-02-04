package com.github.kevwil.aspen.io;

import com.github.kevwil.aspen.RackErrors;
import org.jruby.*;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * @author kevwil
 * @since Feb 03, 2011
 */
public class RubyIORackErrors
extends RubyObject
implements RackErrors
{
    private static final ObjectAllocator ALLOCATOR = new ObjectAllocator()
    {
        public IRubyObject allocate( Ruby runtime, RubyClass klass )
        {
            return new RubyIORackErrors( runtime, klass );
        }
    };

    public static RubyClass getClass(
            Ruby runtime,
            String name,
            RubyClass parent,
            ObjectAllocator allocator,
            Class annoClass)
    {
        RubyModule jrubyMod = runtime.getOrCreateModule( "Aspen" );
        RubyClass klass = jrubyMod.getClass( name );
        if( klass == null )
        {
            klass = jrubyMod.defineClassUnder( name, parent, allocator );
            klass.defineAnnotatedMethods( annoClass );
        }
        return klass;
    }

    public static RubyClass getRubyIORackErrorsClass( Ruby runtime )
    {
        return getClass( runtime, "RubyIORackErrors", runtime.getObject(),
                ALLOCATOR, RubyIORackErrors.class );
    }

    public RubyIORackErrors( Ruby runtime, RubyClass metaClass )
    {
        super( runtime, metaClass );
        _stderr = (RubyIO) runtime.getGlobalVariables().get( "$stderr" );
    }

    public RubyIORackErrors( Ruby runtime )
    {
        super( runtime, getRubyIORackErrorsClass( runtime ) );
    }

    private RubyIO _stderr;

    @Override
    public IRubyObject puts( final ThreadContext context, final IRubyObject arg )
    {
        return _stderr.puts( context, new IRubyObject[]{ arg } );
    }

    @Override
    public IRubyObject write( final ThreadContext context, final IRubyObject string )
    {
        return _stderr.write( context, string );
    }

    @Override
    public IRubyObject flush()
    {
        return _stderr.flush();
    }

    @Override
    public IRubyObject close()
    {
        throw _stderr.getRuntime().newIOError( "Rack spec prohibits calling close() on rack.errors stream." );
    }
}
