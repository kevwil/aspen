package com.github.kevwil.aspen.io;

import com.github.kevwil.aspen.RackErrors;
import org.jruby.*;
import org.jruby.anno.JRubyClass;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * @author kevwil
 * @since Feb 03, 2011
 */
@JRubyClass(name = "AspenRackErrors")
public class RubyIORackErrors
extends RubyObject
implements RackErrors
{
    public static RubyClass createRubyIORackErrors(Ruby runtime)
    {
        RubyClass myClass = runtime.defineClass("AspenRackErrors", runtime.getObject(), RubyIORackErrors::new);
        myClass.setReifiedClass(RubyIORackErrors.class);
        myClass.defineAnnotatedMethods(RubyIORackErrors.class);
        return myClass;
    }

    public RubyIORackErrors(Ruby runtime)
    {
        super(runtime, RubyIORackErrors.createRubyIORackErrors(runtime));
    }
    public RubyIORackErrors( Ruby runtime, RubyClass metaClass )
    {
        super( runtime, metaClass );
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
