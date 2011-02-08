package com.github.kevwil.aspen.io;

import com.github.kevwil.aspen.RackErrors;
import org.jruby.*;
import org.jruby.anno.JRubyMethod;
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
        RubyModule aspenMod = runtime.getOrCreateModule( "Aspen" );
        RubyClass klass = aspenMod.getClass( name );
        if( klass == null )
        {
            klass = aspenMod.defineClassUnder( name, parent, allocator );
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
    }

    public RubyIORackErrors( Ruby runtime )
    {
        super( runtime, getRubyIORackErrorsClass( runtime ) );
    }

    @JRubyMethod( required = 1 )
    public IRubyObject puts( final ThreadContext context, final IRubyObject args )
    {
        System.err.println( args.toString() );
        return getRuntime().getNil();
    }

    @JRubyMethod()
    public IRubyObject write( final ThreadContext context, final IRubyObject string )
    {
        System.err.println( string.toString() );
        return getRuntime().getNil();
    }

    @JRubyMethod()
    public IRubyObject flush()
    {
        System.err.flush();
        return getRuntime().getNil();
    }

    @JRubyMethod()
    public IRubyObject close()
    {
        throw getRuntime().newIOError( "Rack spec prohibits calling close() on rack.errors stream." );
    }
}
